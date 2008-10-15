package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtilsSecurity;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.AddressWorkflowResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectWorkflowResultBean;
import de.ingrid.mdek.beans.query.SearchResultBean;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekObjectUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class QueryRequestHandlerImpl implements QueryRequestHandler {

	private final static Logger log = Logger.getLogger(QueryRequestHandlerImpl.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekCallerQuery mdekCallerQuery;
	private IMdekCallerAddress mdekCallerAddress;

	public void init() {
		mdekCallerQuery = connectionFacade.getMdekCallerQuery();
		mdekCallerAddress = connectionFacade.getMdekCallerAddress();
	}

	
	public AddressSearchResultBean queryAddressesThesaurusTerm(String topicId, int startHit, int numHits) {
		log.debug("Searching for addresses with topicId: "+topicId);

		IngridDocument response = mdekCallerQuery.queryAddressesThesaurusTerm(connectionFacade.getCurrentPlugId(), topicId, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean queryAddressesExtended(AddressExtSearchParamsBean query, int startHit, int numHits) {
		IngridDocument queryParams = MdekUtils.convertAddressExtSearchParamsToIngridDoc(query);

		IngridDocument response = mdekCallerQuery.queryAddressesExtended(connectionFacade.getCurrentPlugId(), queryParams, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsExtended(ObjectExtSearchParamsBean query, int startHit, int numHits) {
		IngridDocument queryParams = MdekUtils.convertObjectExtSearchParamsToIngridDoc(query);

		IngridDocument response = mdekCallerQuery.queryObjectsExtended(connectionFacade.getCurrentPlugId(), queryParams, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean queryAddressesFullText(String searchTerm, int startHit, int numHits) {
		IngridDocument response = mdekCallerQuery.queryAddressesFullText(connectionFacade.getCurrentPlugId(), searchTerm, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsFullText(String searchTerm, int startHit, int numHits) {
		IngridDocument response = mdekCallerQuery.queryObjectsFullText(connectionFacade.getCurrentPlugId(), searchTerm, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits) {
		log.debug("Searching via HQL query: "+hqlQuery);

		IngridDocument response = mdekCallerQuery.queryHQL(connectionFacade.getCurrentPlugId(), hqlQuery, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());

		return MdekUtils.extractSearchResultsFromResponse(response);
	}

	public SearchResultBean queryHQLToCSV(String hqlQuery) {
		log.debug("Searching via HQL to csv query: "+hqlQuery);

		IngridDocument response = mdekCallerQuery.queryHQLToCsv(connectionFacade.getCurrentPlugId(), hqlQuery, HTTPSessionHelper.getCurrentSessionId());

		return MdekUtils.extractSearchResultsFromResponse(response);
	}

	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits) {
		log.debug("Searching for objects with topicId: "+topicId);

		IngridDocument response = mdekCallerQuery.queryObjectsThesaurusTerm(connectionFacade.getCurrentPlugId(), topicId, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		
		return MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits) {
		IngridDocument adrDoc = (IngridDocument) MdekAddressUtils.convertFromAddressRepresentation(adr);

		log.debug("Sending the following address search:");
		log.debug(adrDoc);

		IngridDocument response = mdekCallerAddress.searchAddresses(connectionFacade.getCurrentPlugId(), adrDoc, startHit, numHits, HTTPSessionHelper.getCurrentSessionId());
		
		// TODO Convert the response
		return MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
	}

	public ArrayList<AddressWorkflowResultBean> getExpiredAddresses(int numHits) {
		ArrayList<AddressWorkflowResultBean> adrResults = new ArrayList<AddressWorkflowResultBean>();
		Integer maxNumResults = numHits < 0 ? null : numHits;
		
		Integer expiryDuration = getExpiryDuration();
		if (expiryDuration == null || expiryDuration <= 0) {
			return adrResults;
		}

		Calendar expireCal = Calendar.getInstance();
		expireCal.add(Calendar.DAY_OF_MONTH, -(expiryDuration));

		String qString = "select adr.adrUuid, adr.institution, adr.firstname, adr.lastname, adr.adrType, adr.modTime, " +
				"addr.adrUuid, addr.institution, addr.firstname, addr.lastname, addr.adrType " +
		"from AddressNode addrNode " +
			"inner join addrNode.t02AddressPublished adr " +
			"inner join adr.addressMetadata aMeta, " +
			"AddressNode as aNode " +
			"inner join aNode.t02AddressPublished addr " +
			"inner join addr.t021Communications comm " +
		"where " +
			"adr.responsibleUuid = aNode.addrUuid " +
			" and adr.modTime <= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(expireCal.getTime()) +
			" and adr.responsibleUuid = '"+HTTPSessionHelper.getCurrentSessionId()+"'";
		qString += " order by adr.adrType, adr.institution, adr.lastname, adr.firstname";
		
		IngridDocument response = mdekCallerQuery.queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, maxNumResults, HTTPSessionHelper.getCurrentSessionId());
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					AddressWorkflowResultBean adrWf = new AddressWorkflowResultBean();

					Calendar cal = Calendar.getInstance();
					cal.setTime(MdekUtils.convertTimestampToDate(adrEntity.getString("adr.modTime")));
					cal.add(Calendar.DAY_OF_MONTH, expiryDuration);
					adrWf.setDate(cal.getTime());

					adrWf.setAddress(createAddressFromHQLMap(adrEntity, "adr.adrUuid", "adr.adrType", "adr.institution", "adr.firstname", "adr.lastname"));
					adrWf.setAssignedUser(createAddressFromHQLMap(adrEntity, "addr.adrUuid", "addr.adrType", "addr.institution", "addr.firstname", "addr.lastname"));

					adrResults.add(adrWf);
				}
			}
		}

		return adrResults;
	}

	private Integer getExpiryDuration() {
		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();

		IngridDocument catDoc = mdekCallerCatalog.fetchCatalog(connectionFacade.getCurrentPlugId(), "");
		CatalogBean cat = MdekCatalogUtils.extractCatalogFromResponse(catDoc);
		return cat.getExpiryDuration();		
	}

	public ArrayList<ObjectWorkflowResultBean> getExpiredObjects(int numHits) {
		ArrayList<ObjectWorkflowResultBean> objResults = new ArrayList<ObjectWorkflowResultBean>();
		Integer maxNumResults = numHits < 0 ? null : numHits;

		Integer expiryDuration = getExpiryDuration();
		if (expiryDuration == null || expiryDuration <= 0) {
			return objResults;
		}

		Calendar expireCal = Calendar.getInstance();
		expireCal.add(Calendar.DAY_OF_MONTH, -(expiryDuration));

		String qString = "select obj.objUuid, obj.objClass, obj.objName, obj.modTime, addr.institution, addr.firstname, addr.lastname, addr.adrUuid, addr.adrType " +
		"from ObjectNode oNode " +
			"inner join oNode.t01ObjectPublished obj, " +
			"AddressNode as aNode " +
			"inner join aNode.t02AddressPublished addr " +
			"inner join addr.t021Communications comm " +
		"where " +
			"obj.responsibleUuid = aNode.addrUuid " +
			" and obj.modTime <= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(expireCal.getTime()) +
			" and obj.responsibleUuid = '"+HTTPSessionHelper.getCurrentSessionId()+"'";
		qString += " order by obj.objClass, obj.objName";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, maxNumResults, HTTPSessionHelper.getCurrentSessionId());
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					ObjectWorkflowResultBean objWf = new ObjectWorkflowResultBean();

					Calendar cal = Calendar.getInstance();
					cal.setTime(MdekUtils.convertTimestampToDate(objEntity.getString("obj.modTime")));
					cal.add(Calendar.DAY_OF_MONTH, expiryDuration);
					objWf.setDate(cal.getTime());

					objWf.setObject(createObjectFromHQLMap(objEntity, "obj.objUuid", "obj.objName", "obj.objClass"));
					objWf.setAssignedUser(createAddressFromHQLMap(objEntity, "addr.adrUuid", "addr.adrType", "addr.institution", "addr.firstname", "addr.lastname"));

					objResults.add(objWf);
				}
			}
		}

		return objResults;
	}

	public ArrayList<ObjectWorkflowResultBean> getModifiedObjects(int numHits) {
		ArrayList<ObjectWorkflowResultBean> objResults = new ArrayList<ObjectWorkflowResultBean>();
		Integer maxNumResults = numHits < 0 ? null : numHits;

		String qString = "select obj.objUuid, obj.objClass, obj.objName, oNode.objIdPublished, " +
				" addr.institution, addr.firstname, addr.lastname, addr.adrUuid, addr.adrType " +
		"from ObjectNode oNode " +
			"inner join oNode.t01ObjectWork obj, " +
			"AddressNode as aNode " +
			"inner join aNode.t02AddressPublished addr " +
		"where " +
			"((obj.modUuid is not null and obj.modUuid = aNode.addrUuid) " +
				"or (obj.modUuid is null and obj.responsibleUuid = aNode.addrUuid)) " +
			" and (obj.modUuid = '"+HTTPSessionHelper.getCurrentSessionId()+"'" +
					"or obj.responsibleUuid = '"+HTTPSessionHelper.getCurrentSessionId()+"')" +
			" and obj.workState = '"+de.ingrid.mdek.MdekUtils.WorkState.IN_BEARBEITUNG.getDbValue()+"'";
		qString += " order by obj.objClass, obj.objName";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, maxNumResults, HTTPSessionHelper.getCurrentSessionId());
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					ObjectWorkflowResultBean objWf = new ObjectWorkflowResultBean();
					objWf.setType(objEntity.get("oNode.objIdPublished") == null ? "A" : "B");
					objWf.setObject(createObjectFromHQLMap(objEntity, "obj.objUuid", "obj.objName", "obj.objClass"));
					objWf.setAssignedUser(createAddressFromHQLMap(objEntity, "addr.adrUuid", "addr.adrType", "addr.institution", "addr.firstname", "addr.lastname"));

					objResults.add(objWf);
				}
			}
		}

		return objResults;
	}
	
	public ArrayList<AddressWorkflowResultBean> getModifiedAddresses(int numHits) {
		ArrayList<AddressWorkflowResultBean> adrResults = new ArrayList<AddressWorkflowResultBean>();
		Integer maxNumResults = numHits < 0 ? null : numHits;

		String qString = "select adr.institution, adr.firstname, adr.lastname, adr.adrUuid, adr.adrType, aNode.addrIdPublished, " +
				" addr.institution, addr.firstname, addr.lastname, addr.adrUuid, addr.adrType " +
		"from AddressNode aNode " +
			"inner join aNode.t02AddressWork adr, " +
			"AddressNode as adrNode " +
			"inner join adrNode.t02AddressPublished addr " +
		"where " +
			"((adr.modUuid is not null and adr.modUuid = adrNode.addrUuid) " +
				"or (adr.modUuid is null and adr.responsibleUuid = adrNode.addrUuid)) " +
			" and (adr.modUuid = '"+HTTPSessionHelper.getCurrentSessionId()+"'" +
					"or adr.responsibleUuid = '"+HTTPSessionHelper.getCurrentSessionId()+"')" +
			" and adr.workState = '"+de.ingrid.mdek.MdekUtils.WorkState.IN_BEARBEITUNG.getDbValue()+"'";
		qString += " order by adr.adrType desc, adr.institution, adr.lastname, adr.firstname";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, maxNumResults, HTTPSessionHelper.getCurrentSessionId());
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					AddressWorkflowResultBean adrWf = new AddressWorkflowResultBean();
					adrWf.setType(adrEntity.get("aNode.addrIdPublished") == null ? "A" : "B");
					adrWf.setAddress(createAddressFromHQLMap(adrEntity, "adr.adrUuid", "adr.adrType", "adr.institution", "adr.firstname", "adr.lastname"));
					adrWf.setAssignedUser(createAddressFromHQLMap(adrEntity, "addr.adrUuid", "addr.adrType", "addr.institution", "addr.firstname", "addr.lastname"));

					adrResults.add(adrWf);
				}
			}
		}

		return adrResults;
	}

	public ArrayList<ObjectWorkflowResultBean> getQAObjects(int numHits) {
		ArrayList<ObjectWorkflowResultBean> objResults = new ArrayList<ObjectWorkflowResultBean>();
		Integer maxNumResults = numHits < 0 ? null : numHits;

		String assignedToQA = de.ingrid.mdek.MdekUtils.WorkState.QS_UEBERWIESEN.getDbValue();
		String reassignedFromQA = de.ingrid.mdek.MdekUtils.WorkState.QS_RUECKUEBERWIESEN.getDbValue();
		String currentUserUuid = HTTPSessionHelper.getCurrentSessionId();

		String qString = "select obj.objUuid, obj.objClass, obj.objName, obj.workState, oNode.objIdPublished, " +
				"addr.institution, addr.firstname, addr.lastname, addr.adrUuid, addr.adrType, " +
				"oMeta.assignTime, oMeta.reassignTime, oMeta.markDeleted " +
		"from ObjectNode oNode " +
			"inner join oNode.t01ObjectWork obj " +
			"inner join obj.objectMetadata oMeta, " +
			"AddressNode as aNode " +
			"inner join aNode.t02AddressPublished addr " +
		"where " +
			"oMeta.assignerUuid = aNode.addrUuid" +
			" and (obj.modUuid = '"+currentUserUuid+"'" +
					" or obj.responsibleUuid = '"+currentUserUuid+"'" +
					" or oMeta.assignerUuid = '"+currentUserUuid+"')" +
			" and (obj.workState = '"+assignedToQA+"'" +
					"or obj.workState = '"+reassignedFromQA+"')";
		qString += " order by obj.objClass, obj.objName";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, maxNumResults, HTTPSessionHelper.getCurrentSessionId());
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					ObjectWorkflowResultBean objWf = new ObjectWorkflowResultBean();
					String workState = (String) objEntity.get("obj.workState");
					boolean markedDeleted = objEntity.getString("oMeta.markDeleted").equals("Y");

					if (markedDeleted) {
						objWf.setType("L");						
					} else {
						objWf.setType(objEntity.get("oNode.objIdPublished") == null ? "A" : "B");						
					}

					if (workState.equals(assignedToQA)) {
						objWf.setDate(MdekUtils.convertTimestampToDate((String) objEntity.get("oMeta.assignTime")));
					} else {
						objWf.setDate(MdekUtils.convertTimestampToDate((String) objEntity.get("oMeta.reassignTime")));
					}

					objWf.setState(workState);
					objWf.setObject(createObjectFromHQLMap(objEntity, "obj.objUuid", "obj.objName", "obj.objClass"));
					objWf.setAssignedUser(createAddressFromHQLMap(objEntity, "addr.adrUuid", "addr.adrType", "addr.institution", "addr.firstname", "addr.lastname"));

					objResults.add(objWf);
				}
			}
		}

		return objResults;
	}

	public ArrayList<AddressWorkflowResultBean> getQAAddresses(int numHits) {
		ArrayList<AddressWorkflowResultBean> adrResults = new ArrayList<AddressWorkflowResultBean>();
		Integer maxNumResults = numHits < 0 ? null : numHits;

		String assignedToQA = de.ingrid.mdek.MdekUtils.WorkState.QS_UEBERWIESEN.getDbValue();
		String reassignedFromQA = de.ingrid.mdek.MdekUtils.WorkState.QS_RUECKUEBERWIESEN.getDbValue();
		String currentUserUuid = HTTPSessionHelper.getCurrentSessionId();

		String qString = "select adr.institution, adr.firstname, adr.lastname, adr.adrUuid, adr.adrType, adr.workState, aNode.addrIdPublished, " +
				"addr.institution, addr.firstname, addr.lastname, addr.adrUuid, addr.adrType, " +
				"aMeta.assignTime, aMeta.reassignTime, aMeta.markDeleted " +
		"from AddressNode aNode " +
			"inner join aNode.t02AddressWork adr " +
			"inner join adr.addressMetadata aMeta, " +
			"AddressNode as adrNode " +
			"inner join adrNode.t02AddressPublished addr " +
		"where " +
			"aMeta.assignerUuid = adrNode.addrUuid" +
			" and (adr.modUuid = '"+currentUserUuid+"'" +
					" or adr.responsibleUuid = '"+currentUserUuid+"'" +
					" or aMeta.assignerUuid = '"+currentUserUuid+"')" +
			" and (adr.workState = '"+assignedToQA+"'" +
					"or adr.workState = '"+reassignedFromQA+"')";
		qString += " order by adr.adrType desc, adr.institution, adr.lastname, adr.firstname";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, maxNumResults, HTTPSessionHelper.getCurrentSessionId());
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					AddressWorkflowResultBean adrWf = new AddressWorkflowResultBean();
					String workState = (String) adrEntity.get("adr.workState");

					boolean markedDeleted = adrEntity.getString("aMeta.markDeleted").equals("Y");

					if (markedDeleted) {
						adrWf.setType("L");
					} else {
						adrWf.setType(adrEntity.get("aNode.addrIdPublished") == null ? "A" : "B");
					}

					if (workState.equals(assignedToQA)) {
						adrWf.setDate(MdekUtils.convertTimestampToDate((String) adrEntity.get("aMeta.assignTime")));
					} else {
						adrWf.setDate(MdekUtils.convertTimestampToDate((String) adrEntity.get("aMeta.reassignTime")));
					}

					adrWf.setState(workState);
					adrWf.setAddress(createAddressFromHQLMap(adrEntity, "adr.adrUuid", "adr.adrType", "adr.institution", "adr.firstname", "adr.lastname"));
					adrWf.setAssignedUser(createAddressFromHQLMap(adrEntity, "addr.adrUuid", "addr.adrType", "addr.institution", "addr.firstname", "addr.lastname"));

					adrResults.add(adrWf);
				}
			}
		}

		return adrResults;
	}

	private MdekDataBean createObjectFromHQLMap(IngridDocument objEntity, String uuidKey,
			String nameKey, String classKey) {
		MdekDataBean result = new MdekDataBean();
		result.setUuid(objEntity.getString(uuidKey));
		result.setObjectName(objEntity.getString(nameKey));
		result.setObjectClass((Integer) objEntity.get(classKey));
		return result;
	}

	private MdekAddressBean createAddressFromHQLMap(IngridDocument adrEntity, String uuidKey,
			String typeKey, String institutionKey, String firstnameKey, String lastnameKey) {
		MdekAddressBean result = new MdekAddressBean();
		result.setUuid(adrEntity.getString(uuidKey));
		result.setAddressClass((Integer) adrEntity.get(typeKey));
		result.setOrganisation(adrEntity.getString(institutionKey));
		result.setGivenName(adrEntity.getString(firstnameKey));
		result.setName(adrEntity.getString(lastnameKey));
		return result;
	}

	public IMdekCallerQuery getMdekCallerQuery() {
		return mdekCallerQuery;
	}

	public void setMdekCallerQuery(IMdekCallerQuery mdekCallerQuery) {
		this.mdekCallerQuery = mdekCallerQuery;
	}


	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

}
