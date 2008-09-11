package de.ingrid.mdek.util;

import java.io.FileReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.ingrid.mdek.quartz.jobs.util.ExpiredDataset;


public class MdekEmailUtils {

	private final static Logger log = Logger.getLogger(MdekEmailUtils.class);

	private final static String MAIL_SENDER = "michael.benz@wemove.com";
	private final static String TEMP_MAIL_RECEIVER = "michael.benz@wemove.com";
	private final static String MAIL_SMTP_HOST = "gotthard.wemove.lan";

	public void init() {
		// TODO read properties (smtp host, sender email, ...) from a property file
	}

	public void sendObjectAssignedToQAMail() {
		log.debug("sendObjectAssignedToQAMail() not implemented yet.");		
	}
	public void sendAddressAssignedToQAMail() {
		log.debug("sendAddressAssignedToQAMail() not implemented yet.");		
	}
	public void sendObjectReassignedMail() {
		log.debug("sendObjectReassignedMail() not implemented yet.");		
	}
	public void sendAddressReassignedMail() {
		log.debug("sendAddressReassignedMail() not implemented yet.");		
	}
	public void sendObjectMovedMail() {
		log.debug("sendObjectMovedMail() not implemented yet.");		
	}
	public void sendAddressMovedMail() {
		log.debug("sendAddressMovedMail() not implemented yet.");		
	}


	public void sendExpiryNotificationMails(ArrayList<ExpiredDataset> expiredDatasetList) {
		Map<String, ArrayList<ExpiredDataset>> emailDatasetMap = createMailDatasetMap(expiredDatasetList);

		Iterator<Map.Entry<String, ArrayList<ExpiredDataset>>> it = emailDatasetMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<ExpiredDataset>> mapEntry = it.next();
			String recipient = mapEntry.getKey();
			ArrayList<ExpiredDataset> expDatasets = mapEntry.getValue();

			URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/datasets_will_expire_email.vm");
			String templatePath = url.getPath();
			HashMap<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("expiredDatasetList", expDatasets);
			String text = mergeTemplate(templatePath, mailData, "map");
			sendEmail(text, MAIL_SENDER, new String[] { recipient } );
		}
	}

	public void sendExpiryMails(ArrayList<ExpiredDataset> expiredDatasetList) {
		Map<String, ArrayList<ExpiredDataset>> emailDatasetMap = createMailDatasetMap(expiredDatasetList);

		Iterator<Map.Entry<String, ArrayList<ExpiredDataset>>> it = emailDatasetMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<ExpiredDataset>> mapEntry = it.next();
			String recipient = mapEntry.getKey();
			ArrayList<ExpiredDataset> expDatasets = mapEntry.getValue();

			URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/datasets_expired_email.vm");
			String templatePath = url.getPath();
			HashMap<String, Object> mailData = new HashMap<String, Object>();
			mailData.put("expiredDatasetList", expDatasets);
			String text = mergeTemplate(templatePath, mailData, "map");
			sendEmail(text, MAIL_SENDER, new String[] { recipient } );
		}
	}

	public void sendEmail(String content, String from, String[] to) {
		Properties props = (Properties)System.getProperties().clone();

	    // Setup mail server
	    props.put("mail.smtp.host", MAIL_SMTP_HOST);

		Session session = Session.getDefaultInstance(props, null);
		if (log.isDebugEnabled()) {
			session.setDebug(true);
		}

		Message msg = new MimeMessage(session);

		try {
			InternetAddress[] receivers = new InternetAddress[to.length];
			for (int i = 0; i < to.length; ++i) {
				receivers[i] = new InternetAddress(TEMP_MAIL_RECEIVER); 
//				receivers[i] = new InternetAddress(to[i]);
			}

			msg.setFrom( new InternetAddress(from) );
			msg.setRecipients(Message.RecipientType.TO, receivers);
			msg.setSubject("[MDEK] Test");
			msg.setContent(content, "text/plain; charset=UTF-8");
			Transport.send(msg, msg.getAllRecipients());

		} catch (AddressException e) {
			log.error("invalid email address format", e);

		} catch (MessagingException e) {
			log.error("error sending email.", e);
		}		
	}

	private Map<String, ArrayList<ExpiredDataset>> createMailDatasetMap(ArrayList<ExpiredDataset> expiredDatasetList) {
		Map<String, ArrayList<ExpiredDataset>> mailDatasetMap = new HashMap<String, ArrayList<ExpiredDataset>>();

		for (ExpiredDataset expDataset : expiredDatasetList) {
			String email = expDataset.getResponsibleUserEmail();
			if (email == null) { continue; }

			ArrayList<ExpiredDataset> datasetList = mailDatasetMap.get(email);
			if (datasetList == null) {
				datasetList = new ArrayList<ExpiredDataset>();
				mailDatasetMap.put(email, datasetList);
			}
			datasetList.add(expDataset);
		}

		return mailDatasetMap;
	}

	private static String mergeTemplate(String realTemplatePath, Map<String, Object> attributes, String attributesName) {
		VelocityContext context = new VelocityContext();
		context.put(attributesName, attributes);
		StringWriter sw = new StringWriter();

		try {
			FileReader templateReader = new FileReader(realTemplatePath);

			sw = new StringWriter();
			Velocity.init();
			Velocity.evaluate(context, sw, "UserEmailProcessor", templateReader);

		} catch (Exception e) {
			log.error("failed to merge velocity template: " + realTemplatePath, e);
		}
		String buffer = sw.getBuffer().toString();
		return buffer;
	}
}
