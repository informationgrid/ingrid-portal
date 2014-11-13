/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.quartz.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.MdekUtils.AddressType;
import de.ingrid.mdek.MdekUtils.IdcEntityVersion;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.jobs.util.ExpiredDataset;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekEmailUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class CheckForExpiredDatasetsJob extends QuartzJobBean {

	private final static Logger log = Logger.getLogger(CheckForExpiredDatasetsJob.class);

	private ConnectionFacade connectionFacade;
	private Integer notifyDaysBeforeExpiry;
	private Integer numAddressesMax;
	private Integer numObjectsMax;

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		log.debug("Executing CheckForExpiredDatasetsJob...");
		// 1. Get all objects that: will expire soon & first notification has not been sent
		// 2. Get all objects that: are expired & final notification has not been sent
		// 3.1. Get emails for the objects
		// 3.2. sort the objects by the target email
		// 3.3. send the mails
		// 4. update the expiry state of all objects in the db

		IMdekClientCaller caller = connectionFacade.getMdekClientCaller();
		List<String> iplugList = caller.getRegisteredIPlugs();
		log.debug("Number of iplugs found: "+iplugList.size());
		for (String plugId : iplugList) {
			log.debug("Checking expired datasets for: "+plugId);
			Integer expiryDuration = getExpiryDuration(plugId);

			if (expiryDuration == null || expiryDuration <= 0) {
				log.debug("Expiry duration deactivated -> done.");
				continue;
			}

			Calendar expireCal = Calendar.getInstance();
			Calendar notifyCal = Calendar.getInstance();
			expireCal.add(Calendar.DAY_OF_MONTH, -(expiryDuration));
			notifyCal.add(Calendar.DAY_OF_MONTH, -(expiryDuration-this.notifyDaysBeforeExpiry));

			List<ExpiredDataset> datasetsWillExpireList = getExpiredDatasets(expireCal.getTime(), notifyCal.getTime(), de.ingrid.mdek.MdekUtils.ExpiryState.INITIAL, plugId);
			List<ExpiredDataset> datasetsExpiredList = getExpiredDatasets(null, expireCal.getTime(), de.ingrid.mdek.MdekUtils.ExpiryState.TO_BE_EXPIRED, plugId);

			log.debug("Number of datasets to notify found: "+datasetsWillExpireList.size());
			log.debug("Number of datasets to expire found: "+datasetsExpiredList.size());
			MdekEmailUtils.sendExpiryNotificationMails(datasetsWillExpireList);
			MdekEmailUtils.sendExpiryMails(datasetsExpiredList);

			log.debug("Updating expiry states.");
			updateExpiryState(datasetsWillExpireList, de.ingrid.mdek.MdekUtils.ExpiryState.TO_BE_EXPIRED, plugId);
			updateExpiryState(datasetsExpiredList, de.ingrid.mdek.MdekUtils.ExpiryState.EXPIRED, plugId);
			log.debug("CheckForExpiredDatasetsJob done.");
		}
	}

	private void updateExpiryState(List<ExpiredDataset> expiredDatasetList, de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		IMdekCallerObject mdekCallerObject = connectionFacade.getMdekCallerObject();
		IMdekCallerAddress mdekCallerAddress = connectionFacade.getMdekCallerAddress();
		String catAdminUuid = getCatAdminUuid(plugId);

		for (ExpiredDataset expiredDataset : expiredDatasetList) {
			IngridDocument doc = new IngridDocument();
			doc.put(MdekKeys.UUID, expiredDataset.getUuid());
			doc.put(MdekKeys.EXPIRY_STATE, state.getDbValue());

			if (expiredDataset.getType() == ExpiredDataset.Type.ADDRESS) {
				mdekCallerAddress.updateAddressPart(plugId, doc, IdcEntityVersion.ALL_VERSIONS, catAdminUuid);

			} else {
				mdekCallerObject.updateObjectPart(plugId, doc, IdcEntityVersion.ALL_VERSIONS, catAdminUuid);
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

	private List<ExpiredDataset> getExpiredDatasets(Date begin, Date end,
			de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {

		List<ExpiredDataset> expiredObjs = getExpiredObjects(begin, end, state, plugId);
		List<ExpiredDataset> expiredAdrs = getExpiredAddresses(begin, end, state, plugId);

		expiredObjs.addAll(expiredAdrs);
		return expiredObjs;
	}

	private List<ExpiredDataset> getExpiredObjects(Date begin, Date end,
			de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		List<ExpiredDataset> resultList = new ArrayList<ExpiredDataset>();
		IMdekCallerQuery mdekCallerQuery = connectionFacade.getMdekCallerQuery();

		if (mdekCallerQuery == null) {
			return resultList;
		}

		String qString = "select obj.objUuid, obj.objName, obj.modTime, comm.commValue," +
				"modUserAddr.institution, modUserAddr.firstname, modUserAddr.lastname " +
		"from ObjectNode oNode " +
				"inner join oNode.t01ObjectPublished obj " +
				"inner join obj.objectMetadata oMeta, " +
			"AddressNode as responsibleUserNode " +
				"inner join responsibleUserNode.t02AddressWork responsibleUserAddr " +
				"inner join responsibleUserAddr.t021Communications comm, " +
			"AddressNode as modUserNode " +
				"inner join modUserNode.t02AddressWork modUserAddr " +
		"where " +
			"oMeta.expiryState <= " + state.getDbValue() +
			" and obj.responsibleUuid = responsibleUserNode.addrUuid " +
			" and comm.commtypeKey = " + de.ingrid.mdek.MdekUtils.COMM_TYPE_EMAIL +
			" and obj.modTime <= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(end) +
			" and modUserNode.addrUuid = obj.modUuid";
		if (begin != null) {
			qString += " and obj.modTime >= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(begin);
		}
		qString += " order by obj.objClass, obj.objName";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, numObjectsMax, "");
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
					String institution = objEntity.getString("modUserAddr.institution");
					String lastName = objEntity.getString("modUserAddr.lastname");
					String firstName = objEntity.getString("modUserAddr.firstname");
					dataset.setLastModifiedBy(MdekAddressUtils.createAddressTitle(institution, lastName, firstName));
					dataset.setResponsibleUserEmail(objEntity.getString("comm.commValue"));
					resultList.add(dataset);
				}
			}
		}

		return resultList;
	}

	
	private List<ExpiredDataset> getExpiredAddresses(Date begin, Date end,
			de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		List<ExpiredDataset> resultList = new ArrayList<ExpiredDataset>();
		IMdekCallerQuery mdekCallerQuery = connectionFacade.getMdekCallerQuery();

		if (mdekCallerQuery == null) {
			return resultList;
		}

		String qString = "select adr.adrUuid, adr.institution, adr.firstname, adr.lastname, adr.modTime, comm.commValue," +
				"modUserAddr.institution, modUserAddr.firstname, modUserAddr.lastname " +
		"from AddressNode addrNode " +
				"inner join addrNode.t02AddressPublished adr " +
				"inner join adr.addressMetadata aMeta, " +
			"AddressNode as responsibleUserNode " +
				"inner join responsibleUserNode.t02AddressWork responsibleUserAddr " +
				"inner join responsibleUserAddr.t021Communications comm, " +
			"AddressNode as modUserNode " +
				"inner join modUserNode.t02AddressWork modUserAddr " +
		"where " +
			// exclude hidden user addresses !
			AddressType.getHQLExcludeIGEUsersViaNode("addrNode") +
			" and aMeta.expiryState <= " + state.getDbValue() +
			" and adr.responsibleUuid = responsibleUserNode.addrUuid " +
			" and comm.commtypeKey = " + de.ingrid.mdek.MdekUtils.COMM_TYPE_EMAIL +
			" and adr.modTime <= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(end) +
			" and modUserNode.addrUuid = adr.modUuid";
		if (begin != null) {
			qString += " and adr.modTime >= " + de.ingrid.mdek.MdekUtils.dateToTimestamp(begin);
		}

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, numAddressesMax, "");
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
					
					/*
					// add parent information to title
					IngridDocument responseParent = connectionFacade.getMdekCallerAddress().fetchAddress(plugId, dataset.getUuid(), FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, 0, 20, "");
			        IngridDocument resultAddress = MdekUtils.getResultFromResponse(responseParent);
			        MdekAddressBean parentInstitutions = getDetailedParentInfo(resultAddress);
			        String parentInfo = null;
			        if (parentInstitutions.getParentInstitutions().size() > 0)
			            parentInfo = MdekAddressUtils.extractInstitutions(parentInstitutions);
					*/
			        //if (parentInfo != null)
					//    dataset.setTitle(MdekAddressUtils.createAddressTitle(institution, lastName, firstName)+"\n\t"+parentInfo);
					//else
					dataset.setTitle(MdekAddressUtils.createAddressTitle(institution, lastName, firstName));
					dataset.setType(ExpiredDataset.Type.ADDRESS);
					dataset.setLastModified(MdekUtils.convertTimestampToDate(adrEntity.getString("adr.modTime")));
					String modInstitution = adrEntity.getString("modUserAddr.institution");
					String modLastName = adrEntity.getString("modUserAddr.lastname");
					String modFirstName = adrEntity.getString("modUserAddr.firstname");
					dataset.setLastModifiedBy(MdekAddressUtils.createAddressTitle(modInstitution, modLastName, modFirstName));
					dataset.setResponsibleUserEmail(adrEntity.getString("comm.commValue"));
					resultList.add(dataset);
				}
			}
		}

		return resultList;
	}
	
	/*
	private MdekAddressBean getDetailedParentInfo(IngridDocument result) {
        MdekAddressBean addressBean = new MdekAddressBean(); //mdekDataMapper.getDetailedAddressRepresentation(result);// new MdekAddressBean();
        addressBean.setParentInstitutions(getParentInstitutions((List<IngridDocument>) result.get(MdekKeys.PATH_ORGANISATIONS)));
        addressBean.setOrganisation((String) result.get(MdekKeys.ORGANISATION));
        addressBean.setAddressClass((Integer) result.get(MdekKeys.CLASS));
        return addressBean;
        
	}

	private List<MdekAddressBean> getParentInstitutions(List<IngridDocument> parentOrganisations) {
	    List<MdekAddressBean> resultList = new ArrayList<MdekAddressBean>(); 
        if (parentOrganisations == null)
            return resultList;

        for (IngridDocument adr : parentOrganisations) {
            resultList.add(getDetailedParentInfo(adr));
        }
        return resultList;
    }*/

    public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	public void setNotifyDaysBeforeExpiry(Integer notifyDaysBeforeExpiry) {
		this.notifyDaysBeforeExpiry = notifyDaysBeforeExpiry < 1 ? 14 : notifyDaysBeforeExpiry;
	}

	public void setNumAddressesMax(Integer numAddressesMax) {
		this.numAddressesMax = numAddressesMax;
	}

	public void setNumObjectsMax(Integer numObjectsMax) {
		this.numObjectsMax = numObjectsMax;
	}
}
