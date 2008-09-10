package de.ingrid.mdek.util;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import de.ingrid.mdek.quartz.jobs.util.ExpiredDataset;


public class MdekEmailUtils {

	private final static Logger log = Logger.getLogger(MdekEmailUtils.class);

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
		log.debug("not implemented yet.");

		for (ExpiredDataset expiredDataset : expiredDatasetList) {
			log.debug("Send email to: "+expiredDataset.getResponsibleUserEmail());
			log.debug(" title: "+expiredDataset.getTitle());
			log.debug(" uuid: "+expiredDataset.getUuid());
			log.debug(" type: "+expiredDataset.getType());
			log.debug(" last modified: "+expiredDataset.getLastModified());
		}
	}

	public void sendExpiryMails(ArrayList<ExpiredDataset> expiredDatasetList) {
		log.debug("not implemented yet.");

		for (ExpiredDataset expiredDataset : expiredDatasetList) {
			log.debug("Send email to: "+expiredDataset.getResponsibleUserEmail());
			log.debug(" title: "+expiredDataset.getTitle());
			log.debug(" uuid: "+expiredDataset.getUuid());
			log.debug(" type: "+expiredDataset.getType());
			log.debug(" last modified: "+expiredDataset.getLastModified());
		}
	}

	public void sendEmail(String content, String from, String to) {
		Properties props = (Properties)System.getProperties().clone();

	    // Setup mail server
	    props.put("mail.smtp.host", "gotthard.wemove.lan");

		Session session = Session.getDefaultInstance(props, null);
		if (log.isDebugEnabled()) {
			session.setDebug(true);
		}

		Message msg = new MimeMessage(session);

		try {
			msg.setFrom( new InternetAddress(from) );
			msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress(to) });
			msg.setSubject("[MDEK] Test");
			msg.setContent(content, "text/plain; charset=UTF-8");
			Transport.send(msg, msg.getAllRecipients());

		} catch (AddressException e) {
			log.error("invalid email address format", e);

		} catch (MessagingException e) {
			log.error("error sending email.", e);
		}		
	}
}
