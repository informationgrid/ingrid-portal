package de.ingrid.mdek.quartz.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.IdcEntityVersion;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class CheckForExpiredDatasetsJob extends QuartzJobBean {

	private final static Logger log = Logger .getLogger(CheckForExpiredDatasetsJob.class);
	private final static Integer MAX_NUM_EXPIRED_OBJECTS = 100;
	private final static Integer NOTIFY_DAYS_BEFORE_EXPIRY = 14;

	private ConnectionFacade connectionFacade;


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
			notifyCal.add(Calendar.DAY_OF_MONTH, -(expiryDuration+NOTIFY_DAYS_BEFORE_EXPIRY));

			ArrayList<MdekDataBean> objsWillExpireList = getExpiredObjects(notifyCal.getTime(), expireCal.getTime(), de.ingrid.mdek.MdekUtils.ExpiryState.INITIAL, plugId);
			ArrayList<MdekDataBean> objsExpiredList = getExpiredObjects(null, expireCal.getTime(), de.ingrid.mdek.MdekUtils.ExpiryState.TO_BE_EXPIRED, plugId);

			HashMap<String, String> uuidEmailMap = new HashMap<String, String>();
			HashMap<String, ArrayList<MdekDataBean>> emailWillExpireMap = createEmailExpireMap(objsWillExpireList, uuidEmailMap, plugId);
			HashMap<String, ArrayList<MdekDataBean>> emailExpiredMap = createEmailExpireMap(objsExpiredList, uuidEmailMap, plugId);

			sendMails(emailWillExpireMap);
			sendMails(emailExpiredMap);

//			updateExpiryState(objsWillExpireList, de.ingrid.mdek.MdekUtils.ExpiryState.TO_BE_EXPIRED, plugId);
//			updateExpiryState(objsExpiredList, de.ingrid.mdek.MdekUtils.ExpiryState.EXPIRED, plugId);
		}
	}


	private void updateExpiryState(ArrayList<MdekDataBean> objs, de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		IMdekCallerObject mdekCallerObject = connectionFacade.getMdekCallerObject();

		for (MdekDataBean obj : objs) {
			IngridDocument doc = new IngridDocument();
			doc.put(MdekKeys.UUID, obj.getUuid());
			doc.put(MdekKeys.EXPIRY_STATE, state.getDbValue());
			mdekCallerObject.updateObjectPart(plugId, doc, IdcEntityVersion.PUBLISHED_VERSION, null);
		}
	}


	private void sendMails(HashMap<String, ArrayList<MdekDataBean>> emailObjMap) {
		Set<Map.Entry<String, ArrayList<MdekDataBean>>> entrySet = emailObjMap.entrySet();
		Iterator<Map.Entry<String, ArrayList<MdekDataBean>>> it = entrySet.iterator();

		while (it.hasNext()) {
			Map.Entry<String, ArrayList<MdekDataBean>> entry = it.next();
			String sendToEmail = entry.getKey();
			ArrayList<MdekDataBean> objs = entry.getValue();

			log.debug("Send email to: "+sendToEmail);
			for (MdekDataBean obj : objs) {
				log.debug(" obj uuid: "+obj.getUuid());
				log.debug(" obj title: "+obj.getObjectName());
			}
		}

/*		
		Properties props = (Properties)System.getProperties().clone();

	    // Setup mail server
	    props.put("mail.smtp.host", "gotthard.wemove.lan");

		Session session = Session.getDefaultInstance(props, null);
		if (log.isDebugEnabled()) {
			session.setDebug(true);
		}

		Message msg = new MimeMessage(session);

		try {
			msg.setFrom( new InternetAddress( "michael.benz@wemove.com") );
			msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress( "michael.benz@wemove.com") });
			msg.setSubject("[MDEK] Test");
			msg.setContent(buildMessageContent(), "text/plain; charset=UTF-8");
			Transport.send(msg, msg.getAllRecipients());

		} catch (AddressException e) {
			log.error("invalid email address format", e);

		} catch (MessagingException e) {
			log.error("error sending email.", e);
		}		
*/
	}


	private HashMap<String, ArrayList<MdekDataBean>> createEmailExpireMap(ArrayList<MdekDataBean> objsExpireList, HashMap<String, String> uuidEmailMap, String plugId) {
		HashMap<String, ArrayList<MdekDataBean>> emailObjMap = new HashMap<String, ArrayList<MdekDataBean>>();
		for (MdekDataBean obj : objsExpireList) {
			String email = uuidEmailMap.get(obj.getObjectOwner());

			if (email == null) {
				email = getResponsibleUserEmail(plugId, obj);
				uuidEmailMap.put(obj.getObjectOwner(), email);
			}
			ArrayList<MdekDataBean> willExpireList = emailObjMap.get(email);
			if (willExpireList == null) {
				willExpireList = new ArrayList<MdekDataBean>();
				emailObjMap.put(email, willExpireList);
			}
			willExpireList.add(obj);
		}
		return emailObjMap;
	}


	private String buildMessageContent() {
		String message = "Mdek Test Message created at "+new Date()+"\n\n";

		IMdekCaller caller = connectionFacade.getMdekCaller();

		List<String> iplugList = caller.getRegisteredIPlugs();
		message += "Current registered iPlugs ("+iplugList.size()+"):\n";
		for (String iplug : iplugList) {
			message += " "+iplug+ "\n";

			ArrayList<MdekDataBean> expObjList = getExpiredObjects(null, null, null, iplug);

			HashMap<String, String> responsibleUserMap = new HashMap<String, String>();

			message += "  expired objects:\n";			
			for (MdekDataBean obj : expObjList) {
				String email = responsibleUserMap.get(obj.getObjectOwner());

				if (email == null) {
					email = getResponsibleUserEmail(iplug, obj);
					responsibleUserMap.put(obj.getObjectOwner(), email);
				}

				message += "\nhttp://localhost:8080/ingrid-portal/portal/service-myportal.psml?r=http://localhost:8080/ingrid-portal/portal/mdek%3FnodeType=O%26nodeId="+obj.getUuid()+"\n";
				message += "   obj name: "+obj.getObjectName()+"\n";
				message += "   obj id: "+obj.getUuid()+"\n";
				message += "   expired since: "+obj.getTimeRefDate1()+"\n";
				message += "   send email to: "+email+"\n";
			}
		}

		message += "\n--------\nThe End!\n";

		return message;
	}


	private String getResponsibleUserEmail(String plugId, MdekDataBean obj) {
		IMdekCallerQuery mdekCallerQuery = connectionFacade.getMdekCallerQuery();

		String qString = "select distinct adrComm.commValue " +
		"from AddressNode aNode " +
		"inner join aNode.t02AddressPublished adr " +
		"inner join adr.t021Communications adrComm " +
		"where " +
		"adrComm.commtypeValue = 'Email' " +
		"and aNode.addrUuid = '"+obj.getObjectOwner()+"'";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, MAX_NUM_EXPIRED_OBJECTS, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					return adrEntity.getString("adrComm.commValue");
				}
			}
		}
		
		return "";
	}

	private Integer getExpiryDuration(String plugId) {
		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();

		IngridDocument catDoc = mdekCallerCatalog.fetchCatalog(plugId, "");
		CatalogBean cat = MdekCatalogUtils.extractCatalogFromResponse(catDoc);
		return cat.getExpiryDuration();		
	}

	private ArrayList<MdekDataBean> getExpiredObjects(Date begin, Date end, de.ingrid.mdek.MdekUtils.ExpiryState state, String plugId) {
		ArrayList<MdekDataBean> resultList = new ArrayList<MdekDataBean>();
		IMdekCallerQuery mdekCallerQuery = connectionFacade.getMdekCallerQuery();

		if (mdekCallerQuery == null) {
			return resultList;
		}

		String qString = "select distinct oNode.objUuid, obj.responsibleUuid, obj.objName " +
				"from ObjectNode oNode " +
				"inner join oNode.t01ObjectPublished obj " +
				"inner join obj.objectMetadata objMetadata " +
				"where " +
				"objMetadata.expiryState <= " + state.getDbValue() +
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
					MdekDataBean data = new MdekDataBean();
					data.setUuid(objEntity.getString("oNode.objUuid"));
					data.setObjectName(objEntity.getString("obj.objName"));
					data.setObjectOwner(objEntity.getString("obj.responsibleUuid"));
					resultList.add(data);
				}
			}
		}

		return resultList;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
