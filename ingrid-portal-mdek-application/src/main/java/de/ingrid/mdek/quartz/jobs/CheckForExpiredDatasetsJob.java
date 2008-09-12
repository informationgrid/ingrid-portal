package de.ingrid.mdek.quartz.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.MdekUtils.IdcEntityVersion;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.jobs.util.ExpiredDataset;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekEmailUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class CheckForExpiredDatasetsJob extends QuartzJobBean {

	private final static Logger log = Logger.getLogger(CheckForExpiredDatasetsJob.class);
	private final static Integer MAX_NUM_EXPIRED_OBJECTS = 100;

	private ConnectionFacade connectionFacade;
	private Integer notifyDaysBeforeExpiry;
	
	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		// 1. Get all objects that: will expire soon & first notification has not been sent
		// 2. Get all objects that: are expired & final notification has not been sent
		// 3.1. Get emails for the objects
		// 3.2. sort the objects by the target email
		// 3.3. send the mails
		// 4. update the expiry state of all objects in the db

		IMdekCaller caller = connectionFacade.getMdekCaller();
		List<String> iplugList = caller.getRegisteredIPlugs();
		for (String plugId : iplugList) {
			Integer expiryDuration = getExpiryDuration(plugId);

			if (expiryDuration == null || expiryDuration <= 0) {
				continue;
			}

			Calendar expireCal = Calendar.getInstance();
			Calendar notifyCal = Calendar.getInstance();
			expireCal.add(Calendar.DAY_OF_MONTH, -(expiryDuration));
			notifyCal.add(Calendar.DAY_OF_MONTH, -(expiryDuration-this.notifyDaysBeforeExpiry));

			ArrayList<ExpiredDataset> datasetsWillExpireList = getExpiredDatasets(expireCal.getTime(), notifyCal.getTime(), de.ingrid.mdek.MdekUtils.ExpiryState.INITIAL, plugId);
			ArrayList<ExpiredDataset> datasetsExpiredList = getExpiredDatasets(null, expireCal.getTime(), de.ingrid.mdek.MdekUtils.ExpiryState.TO_BE_EXPIRED, plugId);

			MdekEmailUtils.sendExpiryNotificationMails(datasetsWillExpireList);
			MdekEmailUtils.sendExpiryMails(datasetsExpiredList);

			updateExpiryState(datasetsWillExpireList, de.ingrid.mdek.MdekUtils.ExpiryState.TO_BE_EXPIRED, plugId);
			updateExpiryState(datasetsExpiredList, de.ingrid.mdek.MdekUtils.ExpiryState.EXPIRED, plugId);
		}
	}

	private void updateExpiryState(ArrayList<ExpiredDataset> expiredDatasetList, de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		IMdekCallerObject mdekCallerObject = connectionFacade.getMdekCallerObject();
		IMdekCallerAddress mdekCallerAddress = connectionFacade.getMdekCallerAddress();
		String catAdminUuid = getCatAdminUuid(plugId);

		for (ExpiredDataset expiredDataset : expiredDatasetList) {
			IngridDocument doc = new IngridDocument();
			doc.put(MdekKeys.UUID, expiredDataset.getUuid());
			doc.put(MdekKeys.EXPIRY_STATE, state.getDbValue());

			if (expiredDataset.getType() == ExpiredDataset.Type.ADDRESS) {
				mdekCallerAddress.updateAddressPart(plugId, doc, IdcEntityVersion.PUBLISHED_VERSION, catAdminUuid);
			} else {
				mdekCallerObject.updateObjectPart(plugId, doc, IdcEntityVersion.PUBLISHED_VERSION, catAdminUuid);
			}
		}
	}


	private String getCatAdminUuid(String plugId) {
		try {
			IMdekCallerSecurity mdekCallerSecurity = connectionFacade.getMdekCallerSecurity();
			IngridDocument response = mdekCallerSecurity.getCatalogAdmin(plugId, "");
			IngridDocument result = MdekUtils.getResultFromResponse(response);
			return (String) result.get(MdekKeysSecurity.IDC_USER_ADDR_UUID);
		} catch (Exception e) {
			return "";
		}
	}

	private Integer getExpiryDuration(String plugId) {
		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();

		IngridDocument catDoc = mdekCallerCatalog.fetchCatalog(plugId, "");
		CatalogBean cat = MdekCatalogUtils.extractCatalogFromResponse(catDoc);
		return cat.getExpiryDuration();		
	}

	private ArrayList<ExpiredDataset> getExpiredDatasets(Date begin, Date end,
			de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {

		ArrayList<ExpiredDataset> expiredObjs = getExpiredObjects(begin, end, state, plugId);
		ArrayList<ExpiredDataset> expiredAdrs = getExpiredAddresses(begin, end, state, plugId);

		expiredObjs.addAll(expiredAdrs);
		return expiredObjs;
	}

	private ArrayList<ExpiredDataset> getExpiredObjects(Date begin, Date end,
			de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		ArrayList<ExpiredDataset> resultList = new ArrayList<ExpiredDataset>();
		IMdekCallerQuery mdekCallerQuery = connectionFacade.getMdekCallerQuery();

		if (mdekCallerQuery == null) {
			return resultList;
		}

		String qString = "select obj.objUuid, obj.objName, obj.modTime, comm.commValue " +
		"from ObjectNode oNode " +
			"inner join oNode.t01ObjectPublished obj " +
			"inner join obj.objectMetadata oMeta, " +
			"AddressNode as aNode " +
			"inner join aNode.t02AddressPublished addr " +
			"inner join addr.t021Communications comm " +
		"where " +
			"oMeta.expiryState <= " + state.getDbValue() +
			" and obj.responsibleUuid = aNode.addrUuid " +
			" and comm.commtypeKey = " + de.ingrid.mdek.MdekUtils.COMM_TYPE_EMAIL +
			" and obj.modTime <= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(end);
		if (begin != null) {
			qString += " and obj.modTime >= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(begin);
		}
		qString += " order by obj.objClass, obj.objName";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, MAX_NUM_EXPIRED_OBJECTS, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					ExpiredDataset dataset = new ExpiredDataset();
					dataset.setUuid(objEntity.getString("obj.objUuid"));
					dataset.setTitle(objEntity.getString("obj.objName"));
					dataset.setType(ExpiredDataset.Type.OBJECT);
					dataset.setLastModified(MdekUtils.convertTimestampToDate(objEntity.getString("obj.modTime")));
//					dataset.setLastModifiedBy(lastModifiedBy);
					dataset.setResponsibleUserEmail(objEntity.getString("comm.commValue"));
					resultList.add(dataset);
				}
			}
		}

		return resultList;
	}

	
	private ArrayList<ExpiredDataset> getExpiredAddresses(Date begin, Date end,
			de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		ArrayList<ExpiredDataset> resultList = new ArrayList<ExpiredDataset>();
		IMdekCallerQuery mdekCallerQuery = connectionFacade.getMdekCallerQuery();

		if (mdekCallerQuery == null) {
			return resultList;
		}

		String qString = "select adr.adrUuid, adr.institution, adr.firstname, adr.lastname, adr.modTime, comm.commValue " +
		"from AddressNode addrNode " +
			"inner join addrNode.t02AddressPublished adr " +
			"inner join adr.addressMetadata aMeta, " +
			"AddressNode as aNode " +
			"inner join aNode.t02AddressPublished addr " +
			"inner join addr.t021Communications comm " +
		"where " +
			"aMeta.expiryState <= " + state.getDbValue() +
			" and adr.responsibleUuid = aNode.addrUuid " +
			" and comm.commtypeKey = " + de.ingrid.mdek.MdekUtils.COMM_TYPE_EMAIL +
			" and adr.modTime <= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(end);
		if (begin != null) {
			qString += " and adr.modTime >= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(begin);
		}

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, MAX_NUM_EXPIRED_OBJECTS, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					ExpiredDataset dataset = new ExpiredDataset();
					dataset.setUuid(adrEntity.getString("adr.adrUuid"));
					String institution = adrEntity.getString("adr.institution");
					String lastName = adrEntity.getString("adr.lastname");
					String firstName = adrEntity.getString("adr.firstname");
					dataset.setTitle(MdekAddressUtils.createAddressTitle(institution, lastName, firstName));
					dataset.setType(ExpiredDataset.Type.ADDRESS);
					dataset.setLastModified(MdekUtils.convertTimestampToDate(adrEntity.getString("adr.modTime")));
//					dataset.setLastModifiedBy(lastModifiedBy);
					dataset.setResponsibleUserEmail(adrEntity.getString("comm.commValue"));
					resultList.add(dataset);
				}
			}
		}

		return resultList;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	public void setNotifyDaysBeforeExpiry(Integer notifyDaysBeforeExpiry) {
		this.notifyDaysBeforeExpiry = notifyDaysBeforeExpiry < 1 ? 14 : notifyDaysBeforeExpiry;
	}
}
