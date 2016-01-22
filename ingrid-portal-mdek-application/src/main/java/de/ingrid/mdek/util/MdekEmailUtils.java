/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.mdek.util;

import java.io.FileReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtilsSecurity.IdcPermission;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.CommentBean;
import de.ingrid.mdek.beans.SNSLocationUpdateResult;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.security.User;
import de.ingrid.mdek.caller.IMdekCaller.FetchQuantity;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.jobs.util.ExpiredDataset;
import de.ingrid.utils.IngridDocument;

@Service
public class MdekEmailUtils {

	private final static Logger log = Logger.getLogger(MdekEmailUtils.class);

	// Set in the init method
	private static String MAIL_SENDER;
	private static String MAIL_RECEIVER;
	private static String MAIL_SMTP_HOST;
	private static String MAIL_SMTP_USER;
	private static String MAIL_SMTP_PASSWORD;
	private static String MAIL_SMTP_PORT;
	private static boolean MAIL_SMTP_SSL;
	private static String MAIL_SMTP_PROTOCOL;
	
	private static String MDEK_DIRECT_LINK;

	private final static String MAIL_SUBJECT = "[MDEK] Information";

	private static ConnectionFacade connectionFacade;
	private static IMdekCallerCatalog mdekCallerCatalog;
	private static IMdekCallerSecurity mdekCallerSecurity;
	private static IMdekCallerQuery mdekCallerQuery;
	private static IMdekCallerObject mdekCallerObject;
	private static IMdekCallerAddress mdekCallerAddress;
	
	@Autowired
	public MdekEmailUtils(ConnectionFacade connFacade) {
	    MdekEmailUtils.connectionFacade = connFacade;
	}

	public void init() {
		mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		mdekCallerSecurity = connectionFacade.getMdekCallerSecurity();
		mdekCallerQuery = connectionFacade.getMdekCallerQuery();
		mdekCallerObject = connectionFacade.getMdekCallerObject();
		mdekCallerAddress = connectionFacade.getMdekCallerAddress();

		ResourceBundle resourceBundle = ResourceBundle.getBundle("mdek");
		MAIL_SENDER = resourceBundle.getString("workflow.mail.sender");
		MAIL_SMTP_HOST = resourceBundle.getString("workflow.mail.smtp");
		MAIL_SMTP_USER = resourceBundle.getString("workflow.mail.smtp.user");
		MAIL_SMTP_PASSWORD = resourceBundle.getString("workflow.mail.smtp.password");
		MAIL_SMTP_PORT = resourceBundle.getString("workflow.mail.smtp.port");
		MAIL_SMTP_SSL = Boolean.parseBoolean(resourceBundle.getString("workflow.mail.smtp.ssl"));
		MAIL_SMTP_PROTOCOL = resourceBundle.getString("workflow.mail.smtp.protocol");
		MDEK_DIRECT_LINK = resourceBundle.getString("mdek.directLink");

		// Check if a receiver email was specified.
		try {
			MAIL_RECEIVER = resourceBundle.getString("workflow.mail.receiver").trim();
			if (MAIL_RECEIVER.length() == 0) {
				MAIL_RECEIVER = null;
			}

		} catch (Exception e) {
			// No receiver specified. Initialize MAIL_RECEIVER with null
			MAIL_RECEIVER = null;
		}
	}

	public static void sendObjectAssignedToQAMail(MdekDataBean data) {
		List<User> qaUserList = getQAUsersForObject(data);
		List<String> emailList = getEmailAddressesForUsers(qaUserList);
		Map<String, String> assignedDatasetMap = createDatasetFromObject(data);

		String currentUserTitle = getAddressTitle(MdekSecurityUtils.getCurrentUserUuid());

		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/dataset_assigned_to_qa_email.vm");
		String templatePath = url.getPath();
		Map<String, Object> mailData = new HashMap<String, Object>();
		mailData.put("assignedDataset", assignedDatasetMap);
		mailData.put("currentUser", currentUserTitle);
		String text = mergeTemplate(templatePath, mailData, "map");
		sendEmail(text, MAIL_SENDER, emailList.toArray(new String[]{}) );
	}

	public static void sendAddressAssignedToQAMail(MdekAddressBean adr) {
		List<User> qaUserList = getQAUsersForAddress(adr);
		List<String> emailList = getEmailAddressesForUsers(qaUserList);
		Map<String, String> assignedDatasetMap = createDatasetFromAddress(adr);

		String currentUserTitle = getAddressTitle(MdekSecurityUtils.getCurrentUserUuid());

		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/dataset_assigned_to_qa_email.vm");
		String templatePath = url.getPath();
		Map<String, Object> mailData = new HashMap<String, Object>();
		mailData.put("assignedDataset", assignedDatasetMap);
		mailData.put("currentUser", currentUserTitle);
		String text = mergeTemplate(templatePath, mailData, "map");
		sendEmail(text, MAIL_SENDER, emailList.toArray(new String[]{}) );
	}

	public static void sendObjectReassignedMail(MdekDataBean data) {
		List<String> uuidList = new ArrayList<String>();
		uuidList.add(data.getObjectOwner());
		uuidList.add(getAssignUserUuid(data));

		List<String> emailList = getEmailAddressesForUsers(uuidList.toArray(new String[]{}));
		Map<String, String> reassignedDatasetMap = createDatasetFromObject(data);

		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/dataset_reassigned_from_qa_email.vm");
		String templatePath = url.getPath();
		Map<String, Object> mailData = new HashMap<String, Object>();
		List<String> commentList = extractNewCommentsFromObject(data);
		mailData.put("commentList", commentList);
		mailData.put("reassignedDataset", reassignedDatasetMap);
		String text = mergeTemplate(templatePath, mailData, "map");
		sendEmail(text, MAIL_SENDER, emailList.toArray(new String[]{}) );
	}

	public static void sendAddressReassignedMail(MdekAddressBean adr) {
		List<String> uuidList = new ArrayList<String>();
		uuidList.add(adr.getAddressOwner());
		uuidList.add(getAssignUserUuid(adr));

		List<String> emailList = getEmailAddressesForUsers(uuidList.toArray(new String[]{}));
		Map<String, String> reassignedDatasetMap = createDatasetFromAddress(adr);

		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/dataset_reassigned_from_qa_email.vm");
		String templatePath = url.getPath();
		Map<String, Object> mailData = new HashMap<String, Object>();
		List<String> commentList = extractNewCommentsFromAddress(adr);
		mailData.put("reassignedDataset", reassignedDatasetMap);
		mailData.put("commentList", commentList);
		String text = mergeTemplate(templatePath, mailData, "map");
		sendEmail(text, MAIL_SENDER, emailList.toArray(new String[]{}) );
	}

	private static void sendObjectMovedMail(MdekDataBean data, String fromUuid, String toUuid) {
		List<User> qaUserList = getQAUsersForObject(data);
		List<String> emailList = getEmailAddressesForUsers(qaUserList);
		List<String> responsibleUserEmail = getEmailAddressesForUsers(new String[]{ data.getObjectOwner() });
		emailList.addAll(responsibleUserEmail);

		String srcTitle = (fromUuid == null? "" : getObjectTitle(fromUuid));
		String dstTitle = (toUuid == null? "" : getObjectTitle(toUuid));

		String currentUserTitle = getAddressTitle(MdekSecurityUtils.getCurrentUserUuid());

		Map<String, String> movedDatasetMap = createDatasetFromObject(data);
		movedDatasetMap.put("oldParent", srcTitle);
		movedDatasetMap.put("newParent", dstTitle);

		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/dataset_moved_email.vm");
		String templatePath = url.getPath();
		Map<String, Object> mailData = new HashMap<String, Object>();
		mailData.put("movedDataset", movedDatasetMap);
		mailData.put("currentUser", currentUserTitle);
		String text = mergeTemplate(templatePath, mailData, "map");
		sendEmail(text, MAIL_SENDER, emailList.toArray(new String[]{}) );		
	}

	public static void sendObjectMovedMail(String objUuid, String oldParentUuid, String newParentUuid) {
		if (!isWorkflowControlEnabled() || isCurrentUserQAForObject(objUuid)) {
			return;
		}
		IngridDocument response = mdekCallerObject.fetchObject(connectionFacade.getCurrentPlugId(), objUuid, FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, MdekSecurityUtils.getCurrentUserUuid());
		sendObjectMovedMail(MdekObjectUtils.extractSingleObjectFromResponse(response), oldParentUuid, newParentUuid);
	}
	
	private static void sendAddressMovedMail(MdekAddressBean adr, String fromUuid, String toUuid) {
		List<User> qaUserList = getQAUsersForAddress(adr);
		List<String> emailList = getEmailAddressesForUsers(qaUserList);
		List<String> responsibleUserEmail = getEmailAddressesForUsers(new String[]{ adr.getAddressOwner() });
		emailList.addAll(responsibleUserEmail);

		String srcTitle = (fromUuid == null? "" : getAddressTitle(fromUuid));
		String dstTitle = (toUuid == null? "" : getAddressTitle(toUuid));

		String currentUserTitle = getAddressTitle(MdekSecurityUtils.getCurrentUserUuid());

		Map<String, String> movedDatasetMap = createDatasetFromAddress(adr);
		movedDatasetMap.put("oldParent", srcTitle);
		movedDatasetMap.put("newParent", dstTitle);

		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/dataset_moved_email.vm");
		String templatePath = url.getPath();
		Map<String, Object> mailData = new HashMap<String, Object>();
		mailData.put("movedDataset", movedDatasetMap);
		mailData.put("currentUser", currentUserTitle);
		String text = mergeTemplate(templatePath, mailData, "map");
		sendEmail(text, MAIL_SENDER, emailList.toArray(new String[]{}) );
	}

	public static void sendAddressMovedMail(String adrUuid, String oldParentUuid, String newParentUuid) {
		if (!isWorkflowControlEnabled() || isCurrentUserQAForAddress(adrUuid)) {
			return;
		}
		IngridDocument response = mdekCallerAddress.fetchAddress(connectionFacade.getCurrentPlugId(), adrUuid, FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, 0, 1, MdekSecurityUtils.getCurrentUserUuid());
		sendAddressMovedMail(MdekAddressUtils.extractSingleAddressFromResponse(response), oldParentUuid, newParentUuid);	
	}

	public static void sendObjectMarkedDeletedMail(String objUuid) {
		IngridDocument response = mdekCallerObject.fetchObject(connectionFacade.getCurrentPlugId(), objUuid, FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, MdekSecurityUtils.getCurrentUserUuid());
		sendObjectMarkedDeletedMail(MdekObjectUtils.extractSingleObjectFromResponse(response));
	}

	public static void sendObjectMarkedDeletedMail(MdekDataBean data) {
		List<User> qaUserList = getQAUsersForObject(data);
		List<String> emailList = getEmailAddressesForUsers(qaUserList);
		Map<String, String> assignedDatasetMap = createDatasetFromObject(data);

		String currentUserTitle = getAddressTitle(MdekSecurityUtils.getCurrentUserUuid());

		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/dataset_marked_deleted_email.vm");
		String templatePath = url.getPath();
		Map<String, Object> mailData = new HashMap<String, Object>();
		mailData.put("assignedDataset", assignedDatasetMap);
		mailData.put("currentUser", currentUserTitle);
		String text = mergeTemplate(templatePath, mailData, "map");
		sendEmail(text, MAIL_SENDER, emailList.toArray(new String[]{}) );
	}

	public static void sendAddressMarkedDeletedMail(String adrUuid) {
		IngridDocument response = mdekCallerAddress.fetchAddress(connectionFacade.getCurrentPlugId(), adrUuid, FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, 0, 1, MdekSecurityUtils.getCurrentUserUuid());
		sendAddressMarkedDeletedMail(MdekAddressUtils.extractSingleAddressFromResponse(response));	
	}

	public static void sendAddressMarkedDeletedMail(MdekAddressBean adr) {
		List<User> qaUserList = getQAUsersForAddress(adr);
		List<String> emailList = getEmailAddressesForUsers(qaUserList);
		Map<String, String> assignedDatasetMap = createDatasetFromAddress(adr);

		String currentUserTitle = getAddressTitle(MdekSecurityUtils.getCurrentUserUuid());

		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/dataset_marked_deleted_email.vm");
		String templatePath = url.getPath();
		Map<String, Object> mailData = new HashMap<String, Object>();
		mailData.put("assignedDataset", assignedDatasetMap);
		mailData.put("currentUser", currentUserTitle);
		String text = mergeTemplate(templatePath, mailData, "map");
		sendEmail(text, MAIL_SENDER, emailList.toArray(new String[]{}) );
	}

	public static void sendExpiryNotificationMails(List<ExpiredDataset> expiredDatasetList) {
		Map<String, List<ExpiredDataset>> emailDatasetMap = createMailDatasetMap(expiredDatasetList);

		Iterator<Map.Entry<String, List<ExpiredDataset>>> it = emailDatasetMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<ExpiredDataset>> mapEntry = it.next();
			String recipient = mapEntry.getKey();
			List<ExpiredDataset> expDatasets = mapEntry.getValue();

			URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/datasets_will_expire_email.vm");
			String templatePath = url.getPath();
			Map<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("expiredDatasetList", expDatasets);
			String text = mergeTemplate(templatePath, mailData, "map");
			sendEmail(text, MAIL_SENDER, new String[] { recipient } );
		}
	}

	public static void sendExpiryMails(List<ExpiredDataset> expiredDatasetList) {
		Map<String, List<ExpiredDataset>> emailDatasetMap = createMailDatasetMap(expiredDatasetList);

		Iterator<Map.Entry<String, List<ExpiredDataset>>> it = emailDatasetMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<ExpiredDataset>> mapEntry = it.next();
			String recipient = mapEntry.getKey();
			List<ExpiredDataset> expDatasets = mapEntry.getValue();

			URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/datasets_expired_email.vm");
			String templatePath = url.getPath();
			Map<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("expiredDatasetList", expDatasets);
			String text = mergeTemplate(templatePath, mailData, "map");
			sendEmail(text, MAIL_SENDER, new String[] { recipient } );
		}
	}

	public static void sendSpatialReferencesExpiredMails(List<SNSLocationUpdateResult> updateResults, String plugId, String userId) {
		Map<String, List<MdekDataBean>> emailDatasetMap = createMailDatasetMap(updateResults, plugId, userId);

		// TODO Add the spatial references?
		Iterator<Map.Entry<String, List<MdekDataBean>>> it = emailDatasetMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<MdekDataBean>> mapEntry = it.next();
			String recipient = mapEntry.getKey();
			List<MdekDataBean> expDatasets = mapEntry.getValue();
			URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/spatial_references_expired_email.vm");
			String templatePath = url.getPath();
			Map<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("expiredDatasets", expDatasets);
			String text = mergeTemplate(templatePath, mailData, "map");
			sendEmail(text, MAIL_SENDER, new String[] { recipient } );
		}
	}


	public static void sendEmail(String content, String from, String[] to) {
		Properties props = (Properties)System.getProperties().clone();
		Session session;
		
	    // Setup mail server
	    props.put("mail.smtp.host", MAIL_SMTP_HOST);
	    if(MAIL_SMTP_PORT != null && !MAIL_SMTP_PORT.equals("")){
			props.put("mail.smtp.port", MAIL_SMTP_PORT);
		}
	    
	    if(MAIL_SMTP_PROTOCOL != null && !MAIL_SMTP_PROTOCOL.equals("")){
	    	props.put("mail.transport.protocol", MAIL_SMTP_PROTOCOL);
		}
	    
		if(MAIL_SMTP_SSL){
			props.put("mail.smtp.starttls.enable","true");
		    props.put("mail.smtp.socketFactory.port", MAIL_SMTP_PORT);
		    props.put("mail.smtp.ssl.enable", true);
		    props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		    props.put("mail.smtp.socketFactory.fallback", "false"); 
		}

		if(MAIL_SMTP_USER != null && !MAIL_SMTP_USER.equals("") && MAIL_SMTP_PASSWORD != null && !MAIL_SMTP_PASSWORD.equals("")){
			props.put("mail.smtp.auth", "true");
			Authenticator auth = new MailAuthenticator(MAIL_SMTP_USER, MAIL_SMTP_PASSWORD);
			// create some properties and get the default Session
			session = Session.getDefaultInstance(props, auth);
		}else{
			// create some properties and get the default Session
			session = Session.getDefaultInstance(props, null);
		}
		
		if (log.isDebugEnabled()) {
			session.setDebug(true);
		}

		Message msg = new MimeMessage(session);

		try {
			InternetAddress[] receivers = null;
			if (MAIL_RECEIVER != null) {
				receivers = new InternetAddress[] { new InternetAddress(MAIL_RECEIVER) };

			} else {
				receivers = new InternetAddress[to.length];
				for (int i = 0; i < to.length; ++i) {
					receivers[i] = new InternetAddress(to[i]);
				}
			}

			msg.setFrom( new InternetAddress(from) );
			msg.setRecipients(Message.RecipientType.TO, receivers);
			msg.setSubject(MAIL_SUBJECT);
			msg.setContent(content, "text/plain; charset=UTF-8");
			Transport.send(msg, msg.getAllRecipients());

		} catch (AddressException e) {
			log.error("invalid email address format", e);

		} catch (MessagingException e) {
			log.error("error sending email.", e);
		}		
	}

	private static Map<String, List<ExpiredDataset>> createMailDatasetMap(List<ExpiredDataset> expiredDatasetList) {
		Map<String, List<ExpiredDataset>> mailDatasetMap = new HashMap<String, List<ExpiredDataset>>();

		for (ExpiredDataset expDataset : expiredDatasetList) {
			String email = expDataset.getResponsibleUserEmail();
			if (email == null) { continue; }

			List<ExpiredDataset> datasetList = mailDatasetMap.get(email);
			if (datasetList == null) {
				datasetList = new ArrayList<ExpiredDataset>();
				mailDatasetMap.put(email, datasetList);
			}
			datasetList.add(expDataset);
		}

		return mailDatasetMap;
	}


	private static Map<String, List<MdekDataBean>> createMailDatasetMap(List<SNSLocationUpdateResult> updateResults, String plugId, String userId) {
		// Map with the relation 'userUuid -> userEmail'
		Map<String, String> userEmailMap = new HashMap<String, String>();
		// Map with the relation 'email -> obj1, obj2, ...'
		Map<String, List<MdekDataBean>> emailObjectMap = new HashMap<String, List<MdekDataBean>>();

		for (SNSLocationUpdateResult updateResult : updateResults) {
			List<MdekDataBean> objEntities = updateResult.getObjEntities();
			if (objEntities != null) {
				for (MdekDataBean mdekDataBean : objEntities) {
					String responsibleUserUuid = mdekDataBean.getObjectOwner();

					if (responsibleUserUuid != null) {
						String userEmail = userEmailMap.get(responsibleUserUuid);
						if  (userEmail == null) {
							List<String> responsibleUserAddress = getEmailAddressesForUsers(new String[] { responsibleUserUuid }, plugId, userId);
							if (responsibleUserAddress != null && responsibleUserAddress.size() > 0) {
								userEmail = responsibleUserAddress.get(0);
								userEmailMap.put(responsibleUserUuid, userEmail);

							} else {
								continue;
							}
						}

						List<MdekDataBean> data = emailObjectMap.get(userEmail);
						if (data == null) {
							data = new ArrayList<MdekDataBean>();
							emailObjectMap.put(userEmail, data);
						}
						if (!contains(data, mdekDataBean)) {
							data.add(mdekDataBean);
						}
					}
				}
			}
		}

		return emailObjectMap;
	}

	private static boolean contains(List<MdekDataBean> list, MdekDataBean dataBean) {
		if (list != null && dataBean != null) {
			for (MdekDataBean listItem : list) {
				if (listItem != null && listItem.getUuid() != null && listItem.getUuid().equals(dataBean.getUuid())) {
					return true;
				}
			}
		}

		return false;
	}

	private static String mergeTemplate(String realTemplatePath, Map<String, Object> attributes, String attributesName) {
		attributes.put("directLink", MDEK_DIRECT_LINK);

		VelocityContext context = new VelocityContext();
		context.put(attributesName, attributes);
		StringWriter sw = new StringWriter();

		try {
			FileReader templateReader = new FileReader(realTemplatePath);

			sw = new StringWriter();

		    Properties p = new Properties();
		    p.setProperty("input.encoding", "UTF-8");
		    Velocity.init(p);
			Velocity.evaluate(context, sw, "UserEmailProcessor", templateReader);

		} catch (Exception e) {
			log.error("failed to merge velocity template: " + realTemplatePath, e);
		}
		String buffer = sw.getBuffer().toString();
		return buffer;
	}

	private static List<User> getQAUsersForObject(MdekDataBean data) {
		return getQAUsersForObject(data.getUuid());
	}

	private static List<User> getQAUsersForObject(String objUuid) {
		List<User> qaUserList = new ArrayList<User>();

		IngridDocument doc = mdekCallerSecurity.getUsersWithWritePermissionForObject(connectionFacade.getCurrentPlugId(), objUuid, MdekSecurityUtils.getCurrentUserUuid(), true, true);
		List<User> userList = MdekUtils.extractSecurityUsersFromResponse(doc);

		// Get all users that have qa right on the obj
		for (User u : userList) {
			List<IdcPermission> permissionList = u.getPermissions();
			if (permissionList.contains(IdcPermission.QUALITY_ASSURANCE)) {
				qaUserList.add(u);
			}
		}

		return qaUserList;
	}
	
	private static List<User> getQAUsersForAddress(MdekAddressBean data) {
		return getQAUsersForAddress(data.getUuid());
	}

	private static List<User> getQAUsersForAddress(String adrUuid) {
		List<User> qaUserList = new ArrayList<User>();

		IngridDocument doc = mdekCallerSecurity.getUsersWithWritePermissionForAddress(connectionFacade.getCurrentPlugId(), adrUuid, MdekSecurityUtils.getCurrentUserUuid(), true, true);
		List<User> userList = MdekUtils.extractSecurityUsersFromResponse(doc);

		// Get all users that have qa right on the obj
		for (User u : userList) {
			List<IdcPermission> permissionList = u.getPermissions();
			if (permissionList.contains(IdcPermission.QUALITY_ASSURANCE)) {
				qaUserList.add(u);
			}
		}

		return qaUserList;
	}

	private static String getAssignUserUuid(MdekDataBean data) {
		String qString = "select distinct oMeta.assignerUuid " +
		"from ObjectNode oNode, " +
			" T01Object obj, " +
			" ObjectMetadata oMeta " +
		"where " +
			" oNode.objUuid = '"+data.getUuid()+"'" +
			" and oNode.objId = obj.id " +
			" and obj.objMetadataId = oMeta.id";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, null, MdekSecurityUtils.getCurrentUserUuid());
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs!= null) {
				for (IngridDocument objEntity : objs) {
					String uuid = objEntity.getString("oMeta.assignerUuid");
					if (uuid != null) {
						return uuid;
					}
				}
			}
		}
		return null;
	}

	private static String getAssignUserUuid(MdekAddressBean adr) {
		String qString = "select distinct aMeta.assignerUuid " +
		"from AddressNode aNode, " +
			" T02Address adr, " +
			" AddressMetadata aMeta " +
		"where " +
			" aNode.addrUuid = '"+adr.getUuid()+"'" +
			" and aNode.addrId = adr.id " +
			" and adr.addrMetadataId = aMeta.id";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(connectionFacade.getCurrentPlugId(), qString, null, MdekSecurityUtils.getCurrentUserUuid());
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			if (adrs!= null) {
				for (IngridDocument objEntity : adrs) {
					String uuid = objEntity.getString("aMeta.assignerUuid");
					if (uuid != null) {
						return uuid;
					}
				}
			}
		}
		return null;
	}


	private static List<String> getEmailAddressesForUsers(String[] uuidList) {
		return getEmailAddressesForUsers(uuidList, connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
	}


	private static List<String> getEmailAddressesForUsers(String[] uuidList, String plugId, String userId) {
		List<String> emailAddressList = new ArrayList<String>();

		if (uuidList == null || uuidList.length <= 0) {
			return emailAddressList;
		}

		// Prepare hql query
		String qString = "select distinct comm.commValue " +
		"from AddressNode aNode, " +
			" T021Communication comm " +
		"where " +
			" aNode.addrId = comm.adrId " +
			" and comm.commtypeKey = " + de.ingrid.mdek.MdekUtils.COMM_TYPE_EMAIL;

		qString += " and (";		
		for (String uuid : uuidList) {
			qString += " aNode.addrUuid = '"+uuid+"' or ";
		}
		qString = qString.substring(0, qString.length() - 4) + ")";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, null, userId);
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					emailAddressList.add(adrEntity.getString("comm.commValue"));
				}
			}
		}

		return emailAddressList;
	}

	private static List<String> getEmailAddressesForUsers(List<User> userList) {
		List<String> uuidList = new ArrayList<String>();
		if (userList != null) {
			for (User u : userList) {
				uuidList.add(u.getAddressUuid());
			}
		}
		return getEmailAddressesForUsers(uuidList.toArray(new String[]{}));
	}

	private static Map<String, String> createDatasetFromObject(MdekDataBean data) {
		Map<String, String> assignedDatasetMap = new HashMap<String, String>();

		assignedDatasetMap.put("title", data.getObjectName());
		assignedDatasetMap.put("uuid", data.getUuid());
		assignedDatasetMap.put("type", "O");
		assignedDatasetMap.put("operation", data.getIsPublished() ? "B" : "A");

		return assignedDatasetMap;
	}

	private static Map<String, String> createDatasetFromAddress(MdekAddressBean data) {
		Map<String, String> assignedDatasetMap = new HashMap<String, String>();

		assignedDatasetMap.put("title", MdekAddressUtils.createAddressTitle(data.getOrganisation(), data.getName(), data.getGivenName()));
		assignedDatasetMap.put("uuid", data.getUuid());
		assignedDatasetMap.put("type", "A");
		assignedDatasetMap.put("operation", data.getIsPublished() ? "B" : "A");

		return assignedDatasetMap;
	}

	private static boolean isWorkflowControlEnabled() {
		IngridDocument response = mdekCallerCatalog.fetchCatalog(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
		CatalogBean cat = MdekCatalogUtils.extractCatalogFromResponse(response);
		return (cat.getWorkflowControl() != null && cat.getWorkflowControl().equals(de.ingrid.mdek.MdekUtils.YES));
	}

	private static boolean isCurrentUserQAForObject(String objUuid) {
		String userId = MdekSecurityUtils.getCurrentUserUuid();
		List<User> qaUsers = getQAUsersForObject(objUuid);
		for (User u : qaUsers) {
			if (u.getAddressUuid().equals(userId)) {
				return true;
			}
		}

		return false;
	}
	
	private static boolean isCurrentUserQAForAddress(String adrUuid) {
		String userId = MdekSecurityUtils.getCurrentUserUuid();
		List<User> qaUsers = getQAUsersForAddress(adrUuid);
		for (User u : qaUsers) {
			if (u.getAddressUuid().equals(userId)) {
				return true;
			}
		}

		return false;
	}

	private static List<String> extractNewCommentsFromObject(MdekDataBean data) {
		return extractNewComments(data.getCommentTable(), data.getAssignTime());		
	}

	private static List<String> extractNewCommentsFromAddress(MdekAddressBean adr) {
		return extractNewComments(adr.getCommentTable(), adr.getAssignTime());
	}

	private static List<String> extractNewComments(List<CommentBean> commentList, Date assignTime) {
		List<String> resultList = new ArrayList<String>();

		for (CommentBean c : commentList) {
			if (c.getDate().after(assignTime)) {
				resultList.add(c.getComment());
			}
		}

		return resultList;
	}

	private static String getObjectTitle(String objUuid) {
		IngridDocument response = mdekCallerObject.fetchObject(connectionFacade.getCurrentPlugId(), objUuid, FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, MdekSecurityUtils.getCurrentUserUuid());
		MdekDataBean data = MdekObjectUtils.extractSingleObjectFromResponse(response);
		return data.getTitle();
	}

	private static String getAddressTitle(String adrUuid) {
		IngridDocument response = mdekCallerAddress.fetchAddress(connectionFacade.getCurrentPlugId(), adrUuid, FetchQuantity.EDITOR_ENTITY, de.ingrid.mdek.MdekUtils.IdcEntityVersion.WORKING_VERSION, 0, 0, MdekSecurityUtils.getCurrentUserUuid());
		MdekAddressBean data = MdekAddressUtils.extractSingleAddressFromResponse(response);
		return MdekAddressUtils.createAddressTitle(data.getOrganisation(), data.getName(), data.getGivenName());
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}

class MailAuthenticator extends Authenticator {
	private String user; 
	private String password;
	
	public MailAuthenticator(String user, String password) {
		this.user = user;
		this.password = password;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.user, this.password);
	}
}
