package de.ingrid.mdek.dwr.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.CsvRequestType;
import de.ingrid.mdek.beans.AnalyzeJobInfoBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.URLJobInfoBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.quartz.MdekJobHandler;
import de.ingrid.mdek.quartz.jobs.util.URLState;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class CatalogManagementServiceImpl {

	private final static Logger log = Logger
			.getLogger(CatalogManagementServiceImpl.class);

	private MdekJobHandler mdekJobHandler;
	private CatalogRequestHandler catalogRequestHandler;

	// TODO Move ConnectionFacade (or better: the specific MdekCallers) to a request handler!
	private ConnectionFacade connectionFacade;

	public void startUrlValidatorJob() {
		mdekJobHandler.startUrlValidatorJob();
	}

	public void stopUrlValidatorJob() {
		mdekJobHandler.stopUrlValidatorJob();
	}

	public URLJobInfoBean getUrlValidatorJobInfo() {
		return mdekJobHandler.getUrlValidatorJobInfo();
	}

	public void updateDBUrlJobInfo(List<Map<String, String>> sourceUrls, String targetUrl) {
		List<IngridDocument> urlList = new ArrayList<IngridDocument>();

		for (Map<String, String> map : sourceUrls) {
			IngridDocument urlDoc = new IngridDocument();
			urlDoc.put(MdekKeys.URL_RESULT_OBJECT_UUID, map.get("objectUuid"));
			urlDoc.put(MdekKeys.URL_RESULT_URL, map.get("url"));
			urlDoc.put(MdekKeys.URL_RESULT_STATE, URLState.State.NOT_CHECKED.toString());
			urlList.add(urlDoc);
		}

		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		IngridDocument response = mdekCallerCatalog.updateURLInfo(
				connectionFacade.getCurrentPlugId(),
				urlList,
				targetUrl,
				MdekSecurityUtils.getCurrentUserUuid());

		IMdekClientCaller mdekClientCaller = connectionFacade.getMdekClientCaller();
		if (mdekClientCaller.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
		}
	}

	public void replaceUrls(List<Map<String, String>> sourceUrls, String targetUrl) {
		List<IngridDocument> urlList = new ArrayList<IngridDocument>();

		for (Map<String, String> map : sourceUrls) {
			IngridDocument urlDoc = new IngridDocument();
			urlDoc.put(MdekKeys.URL_RESULT_OBJECT_UUID, map.get("objectUuid"));
			urlDoc.put(MdekKeys.URL_RESULT_URL, map.get("url"));
			urlList.add(urlDoc);
		}

		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		IngridDocument response = mdekCallerCatalog.replaceURLs(
				connectionFacade.getCurrentPlugId(),
				urlList,
				targetUrl,
				MdekSecurityUtils.getCurrentUserUuid());

		IMdekClientCaller mdekClientCaller = connectionFacade.getMdekClientCaller();
		if (mdekClientCaller.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
		}
	}

	public List<MdekDataBean> getDuplicateObjects() {
		// TODO Use function from backend when it's done
		IMdekCallerQuery mdekCallerQuery = connectionFacade
				.getMdekCallerQuery();

		String qString = "select obj.objUuid, obj.objClass, obj.objName, obj.objDescr "
			+ "from ObjectNode oNode "
				+ "inner join oNode.t01ObjectPublished obj "
			+ "where oNode.objIdPublished = oNode.objId "
			+ "order by obj.objName";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(
				connectionFacade.getCurrentPlugId(), qString, null, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		List<MdekDataBean> resultList = new ArrayList<MdekDataBean>();
		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					if (isDuplicate(objEntity, objs)) {
						MdekDataBean obj = new MdekDataBean();
						obj.setUuid(objEntity.getString("obj.objUuid"));
						obj.setObjectClass(objEntity.getInt("obj.objClass"));
						obj.setTitle(objEntity.getString("obj.objName"));
						obj.setGeneralDescription(objEntity.getString("obj.objDescr"));
						resultList.add(obj);
					}
				}
			}
		}
		return resultList;
	}

	private static boolean isDuplicate(IngridDocument objEntity, List<IngridDocument> list) {
		String objName = objEntity.getString("obj.objName");
		int count = 0;
		for (IngridDocument item : list) {
			if (item.getString("obj.objName").equals(objName))
				count++;
			if (count > 1) {
				return true;
			}
		}
		return false;
	}

	public void startSNSUpdateJob(FileTransfer fileTransfer) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse(fileTransfer.getInputStream());

		NodeList changedList = (NodeList) xpath.evaluate("//changed/@topicID", doc, XPathConstants.NODESET);
		String[] changedTopics = new String[changedList.getLength()];
		for (int index = 0; index < changedList.getLength(); ++index) {
			changedTopics[index] = changedList.item(index).getTextContent();
		}

		NodeList newList = (NodeList) xpath.evaluate("//new/@topicID", doc, XPathConstants.NODESET);
		String[] newTopics = new String[newList.getLength()];
		for (int index = 0; index < newList.getLength(); ++index) {
			newTopics[index] = newList.item(index).getTextContent();
		}

		NodeList expiredList = (NodeList) xpath.evaluate("//expired/@topicID", doc, XPathConstants.NODESET);
		String[] expiredTopics = new String[expiredList.getLength()];
		for (int index = 0; index < expiredList.getLength(); ++index) {
			expiredTopics[index] = expiredList.item(index).getTextContent();
		}

		mdekJobHandler.startSNSUpdateJob(changedTopics, newTopics, expiredTopics);
	}

	public AnalyzeJobInfoBean analyze() {
		try {
			return catalogRequestHandler.analyze();

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting analysis job.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}
	

	public void setMdekJobHandler(MdekJobHandler mdekJobHandler) {
		this.mdekJobHandler = mdekJobHandler;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
		this.catalogRequestHandler = catalogRequestHandler;
	}
	
	public List<MdekDataBean> getObjectsOfAuskunftAddress(String auskunftAddressUuid, int maxNumHits) {
		return catalogRequestHandler.getObjectsOfAuskunftAddress(auskunftAddressUuid, maxNumHits);
	}
	
	public List<MdekDataBean> getObjectsOfResponsibleUser(String responsibleUserUuid, int maxNumHits) {
		return catalogRequestHandler.getObjectsOfResponsibleUser(responsibleUserUuid, maxNumHits);
	}
	
	public List<MdekAddressBean> getAddressesOfResponsibleUser(String responsibleUserUuid, int maxNumHits) {
		return catalogRequestHandler.getAddressesOfResponsibleUser(responsibleUserUuid, maxNumHits);
	}
	
	public FileTransfer getCsvData( String uuid, CsvRequestType type ) {
		return new FileTransfer("export.csv.gz", "x-gzip", catalogRequestHandler.getCsvData(uuid, type));
	}
	
	public void replaceAddress(String oldUuid, String newUuid) {
		try {
			IngridDocument response = catalogRequestHandler.replaceAddress(oldUuid, newUuid);
			
			IMdekClientCaller mdekClientCaller = connectionFacade.getMdekClientCaller();
			if (mdekClientCaller.getResultFromResponse(response) == null) {
				MdekErrorUtils.handleError(response);
			}

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while replacing address.", e);
			throw MdekErrorUtils.convertToRuntimeException(e);
		}
	}

	public void rebuildSysListData() {
		try {
			catalogRequestHandler.rebuildSysListData();

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while starting 'rebuild sysList' job.", e);
			throw MdekErrorUtils.convertToRuntimeException(e);
		}
	}

	public JobInfoBean getRebuildSysListDataJobInfo() {
		try {
			return catalogRequestHandler.getRebuildJobInfo();

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching 'rebuild sysList' job info.", e);
			throw MdekErrorUtils.convertToRuntimeException(e);
		}
	}
}