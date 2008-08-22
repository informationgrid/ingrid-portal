package de.ingrid.mdek.quartz.jobs;

import java.util.Date;
import java.util.List;
import java.util.Properties;

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
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.handler.ConnectionFacade;

public class CheckForExpiredDatasetsJob extends QuartzJobBean {

	private final static Logger log = Logger .getLogger(CheckForExpiredDatasetsJob.class);
	private ConnectionFacade connectionFacade;

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
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
	}

	private String buildMessageContent() {
		String message = "Mdek Test Message created at "+new Date()+"\n\n";

		IMdekCaller caller = connectionFacade.getMdekCaller();

		List<String> iplugList = caller.getRegisteredIPlugs();
		message += "Current registered iPlugs ("+iplugList.size()+"):\n";
		for (String iplug : iplugList) {
			message += iplug+ "\n";
		}

		message += "\n--------\nThe End!\n";

		return message;
	}
}
