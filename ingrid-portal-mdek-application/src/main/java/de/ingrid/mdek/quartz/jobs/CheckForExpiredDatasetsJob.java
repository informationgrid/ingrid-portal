/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

	private static final Logger log = Logger.getLogger(CheckForExpiredDatasetsJob.class);

	private ConnectionFacade connectionFacade;
	private Integer notifyDaysBeforeExpiry;
    private Boolean repeatExpiryCheck;
	private Integer numAddressesMax;
	private Integer numObjectsMax;

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		log.info("Executing CheckForExpiredDatasetsJob...");
		// 1. Get all objects/addresses that: will expire soon & first notification has not been sent
		// 2. Get all objects/addresses that: are expired & final notification has not been sent
        // 3. Get all objects/addresses that: are expired & final notification has already been sent
		//    but has to be sent again, see https://dev.informationgrid.eu/redmine/issues/107
		// 4.1. Get emails for the objects/addresses
		// 4.2. sort the objects/addresses by the target email
		// 4.3. send the mails
		// 5. update the expiry state and time (email sent) of all objects/addresses notified

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
			List<ExpiredDataset> datasetsAgainExpiredList = new ArrayList<>(0);
			if (repeatExpiryCheck) {
	            // fetch entities already expired but where mail has to be sent again, see https://dev.informationgrid.eu/redmine/issues/107
	            datasetsAgainExpiredList = getExpiredDatasets(null, expireCal.getTime(), de.ingrid.mdek.MdekUtils.ExpiryState.EXPIRED, plugId);			    
			}

            if (!datasetsWillExpireList.isEmpty() || !datasetsExpiredList.isEmpty() || !datasetsAgainExpiredList.isEmpty()) {
                log.info("" + plugId + ":");
                log.info("  Number of entities to notify found: "+datasetsWillExpireList.size());
                log.info("  Number of entities expired found: "+datasetsExpiredList.size());
                log.info("  Number of entities again expired found: "+datasetsAgainExpiredList.size());
            } else {
                log.debug( "Nothing has expired." );
            }
            
			MdekEmailUtils.sendExpiryNotificationMails(datasetsWillExpireList);
			// NOTICE: Entities expired for the first time and entities again expired are handled
			// the same way (same email and update of expiry states/time) so we merge lists !
            datasetsExpiredList.addAll(datasetsAgainExpiredList);
			MdekEmailUtils.sendExpiryMails(datasetsExpiredList);

			log.debug("Updating expiry states and time.");
			updateExpiryState(datasetsWillExpireList, de.ingrid.mdek.MdekUtils.ExpiryState.TO_BE_EXPIRED, plugId);
			updateExpiryState(datasetsExpiredList, de.ingrid.mdek.MdekUtils.ExpiryState.EXPIRED, plugId);
			log.debug("CheckForExpiredDatasetsJob done.");
		}
	}

	private void updateExpiryState(List<ExpiredDataset> expiredDatasetList, de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		IMdekCallerObject mdekCallerObject = connectionFacade.getMdekCallerObject();
		IMdekCallerAddress mdekCallerAddress = connectionFacade.getMdekCallerAddress();
		String catAdminUuid = getCatAdminUuid(plugId);

		// set the date we sent our email !
		String expiryTime = de.ingrid.mdek.MdekUtils.dateToTimestamp(Calendar.getInstance().getTime());

		for (ExpiredDataset expiredDataset : expiredDatasetList) {
			IngridDocument doc = new IngridDocument();
			doc.put(MdekKeys.UUID, expiredDataset.getUuid());
			doc.put(MdekKeys.EXPIRY_STATE, state.getDbValue());
            doc.put(MdekKeys.LASTEXPIRY_TIME, expiryTime);

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
		List<ExpiredDataset> resultList = new ArrayList<>();
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
            "obj.responsibleUuid = responsibleUserNode.addrUuid " +
			" and comm.commtypeKey = " + de.ingrid.mdek.MdekUtils.COMM_TYPE_EMAIL +
			" and modUserNode.addrUuid = obj.modUuid";
		if (begin != null) {
			qString += " and obj.modTime >= '" + de.ingrid.mdek.MdekUtils.dateToTimestamp(begin) +"'";
		}
		// differ between querying for EXPIRED (to send another expiry email) or for first expiry email !
        if (de.ingrid.mdek.MdekUtils.ExpiryState.EXPIRED.equals( state )) {
            // if query for EXPIRED we compare with "=" not "<=" we only want entities already expired !
            qString += " and oMeta.expiryState = " + state.getDbValue(); 
            if (end != null) {
                // if expiry mail already sent, we use date when email was sent to determine whether again expired !
                // Also check if date not set, then send email (state after date was introduced)
                qString += " and (oMeta.lastexpiryTime is null OR oMeta.lastexpiryTime <= '" + de.ingrid.mdek.MdekUtils.dateToTimestamp(end) + "')";
            }
        } else {
            // if not query for EXPIRE we compare with "<=" so notification and expire mails can be sent.                
            qString += " and oMeta.expiryState <= " + state.getDbValue(); 
            if (end != null) {
                qString += " and obj.modTime <= '" + de.ingrid.mdek.MdekUtils.dateToTimestamp(end) + "'";
            }
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
		List<ExpiredDataset> resultList = new ArrayList<>();
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
			AddressType.getHQLExcludeIGEUsersViaNode("addrNode", "adr") +
			" and adr.responsibleUuid = responsibleUserNode.addrUuid " +
			" and comm.commtypeKey = " + de.ingrid.mdek.MdekUtils.COMM_TYPE_EMAIL +
			" and modUserNode.addrUuid = adr.modUuid";
		if (begin != null) {
			qString += " and adr.modTime >= '" + de.ingrid.mdek.MdekUtils.dateToTimestamp(begin) + "'";
		}
        // differ between querying for EXPIRED (to send another expiry email) or for first expiry email !
        if (de.ingrid.mdek.MdekUtils.ExpiryState.EXPIRED.equals( state )) {
            // if query for EXPIRED we compare with "=" not "<=" we only want entities already expired !
            qString += " and aMeta.expiryState = " + state.getDbValue(); 
            if (end != null) {
                // if expiry mail already sent, we use date when email was sent to determine whether again expired !
                // Also check if date not set, then send email (state after date was introduced)
                qString += " and (aMeta.lastexpiryTime is null OR aMeta.lastexpiryTime <= '" + de.ingrid.mdek.MdekUtils.dateToTimestamp(end) + "')";
            }
        } else {
            // if not query for EXPIRE we compare with "<=" so notification and expire mails can be sent.                
            qString += " and aMeta.expiryState <= " + state.getDbValue(); 
            if (end != null) {
                qString += " and adr.modTime <= '" + de.ingrid.mdek.MdekUtils.dateToTimestamp(end) + "'";
            }
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
	
    public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	public void setNotifyDaysBeforeExpiry(Integer notifyDaysBeforeExpiry) {
		this.notifyDaysBeforeExpiry = notifyDaysBeforeExpiry < 1 ? 14 : notifyDaysBeforeExpiry;
	}

    public void setRepeatExpiryCheck(Boolean repeatExpiryCheck) {
        this.repeatExpiryCheck = repeatExpiryCheck;
    }

	public void setNumAddressesMax(Integer numAddressesMax) {
		this.numAddressesMax = numAddressesMax;
	}

	public void setNumObjectsMax(Integer numObjectsMax) {
		this.numObjectsMax = numObjectsMax;
	}
}
