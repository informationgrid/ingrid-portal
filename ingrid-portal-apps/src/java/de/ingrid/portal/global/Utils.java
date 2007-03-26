/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.io.FileReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.hibernate.cfg.Environment;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ActionForm;

/**
 * Global STATIC data and utility methods.
 * 
 * @author Martin Maidhof
 */
public class Utils {

	private final static Log log = LogFactory.getLog(Utils.class);

	/**
	 * Get ActionForm from Session in given scope. Add new one of given Class if
	 * not there yet !
	 * 
	 * @param request
	 * @param afKey
	 * @param afClass
	 * @param sessionScope
	 * @return
	 */
	public static ActionForm getActionForm(PortletRequest request, String afKey, Class afClass, int sessionScope) {
		ActionForm af = getActionForm(request, afKey, sessionScope);
		if (af == null) {
			af = addActionForm(request, afKey, afClass, sessionScope);
		}

		return af;
	}

	/**
	 * Convenient method to get the session preferences object.
	 * 
	 * @param request
	 * @param aKey
	 * @param aClass
	 * @return
	 */
	public static IngridSessionPreferences getSessionPreferences(PortletRequest request, String aKey, Class aClass) {
		IngridSessionPreferences obj = (IngridSessionPreferences) request.getPortletSession().getAttribute(aKey,
				PortletSession.APPLICATION_SCOPE);
		if (obj == null) {
			obj = new IngridSessionPreferences();
			// initialize the session preference with persistent data from
			// personalization
			Principal principal = request.getUserPrincipal();
			if (principal != null) {
				HashMap searchSettings = (HashMap) IngridPersistencePrefs.getPref(principal.getName(),
						IngridPersistencePrefs.SEARCH_SETTINGS);
				if (searchSettings != null) {
					obj.putAll(searchSettings);
				}
			}
			request.getPortletSession().setAttribute(aKey, obj, PortletSession.APPLICATION_SCOPE);
		}
		return obj;
	}

	/**
	 * Convenient method for getting (incl. adding) action forms from
	 * PortletSession.PORTLET_SCOPE
	 * 
	 * @param request
	 * @param afKey
	 * @param afClass
	 * @return
	 */
	public static ActionForm getActionForm(PortletRequest request, String afKey, Class afClass) {
		return getActionForm(request, afKey, afClass, PortletSession.PORTLET_SCOPE);
	}

	/**
	 * Get ActionForm from Session in given scope, returns null if not there yet !
	 * 
	 * @param request
	 * @param afKey
	 * @param scope
	 * @return
	 */
	public static ActionForm getActionForm(PortletRequest request, String afKey, int scope) {
		return (ActionForm) request.getPortletSession().getAttribute(afKey, scope);
	}

	/**
	 * Add new ActionForm of given Class to Session. Also calls init() on
	 * ActionForm !
	 * 
	 * @param request
	 * @param afKey
	 * @param afClass
	 * @return
	 */
	public static ActionForm addActionForm(PortletRequest request, String afKey, Class afClass, int scope) {
		ActionForm af = null;
		try {
			af = (ActionForm) afClass.newInstance();
			af.init();
			request.getPortletSession().setAttribute(afKey, af, scope);
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Problems initial setup ActionForm of type " + afClass, ex);
			}
		}

		return af;
	}

	/**
	 * Validate the form of an email address. see
	 * http://www.javapractices.com/Topic180.cjp
	 * 
	 * <P>
	 * Return <code>true</code> only if
	 * <ul>
	 * <li> <code>aEmailAddress</code> can successfully construct an
	 * {@link javax.mail.internet.InternetAddress}
	 * <li> when parsed with a "@" delimiter, <code>aEmailAddress</code>
	 * contains two tokens which satisfy
	 * {@link hirondelle.web4j.util.Util#textHasContent}.
	 * </ul>
	 * 
	 * <P>
	 * The second condition arises since local email addresses, simply of the
	 * form "albert", for example, are valid but almost always undesired.
	 */
	public static boolean isValidEmailAddress(String aEmailAddress) {
		// org.apache.portals.gems.util.ValidationHelper doesn't work !
		// NOTICE: needs
		// MAVEN_REPO/org.apache.portals.jetspeed-2/jars/portals-gems-2.0.jar
		// if (!ValidationHelper.isEmailAddress(myEmail, true)) {
		// return false;
		// } else {
		// return true;
		// }

		if (aEmailAddress == null)
			return false;

		boolean result = true;
		try {
			// check for format syntax of RFC822, throws Exception if not valid
			// !
			new InternetAddress(aEmailAddress);
			// check for name and email
			String[] tokens = aEmailAddress.split("@");
			boolean hasNameAndDomain = (tokens.length == 2) && (tokens[0].trim().length() > 0)
					&& tokens[1].trim().length() > 0;

			if (!hasNameAndDomain) {
				result = false;
			}
		} catch (AddressException ex) {
			if (log.isDebugEnabled()) {
				log.debug("checking email '" + aEmailAddress + "' caused AddressException", ex);
			}
			result = false;
		}
		return result;
	}

	public static String[] getShortStrings(String[] myStrings, int maxLength) {
		if (myStrings == null) {
			return null;
		}

		for (int i = 0; i < myStrings.length; i++) {
			myStrings[i] = getShortStr(myStrings[i], maxLength);
		}

		return myStrings;
	}

	public static String getShortStr(String myString, int maxLength) {
		if (myString.length() > maxLength) {
			return myString.substring(0, maxLength - 3).concat("...");
		}

		return myString;
	}

	/**
	 * Returns position of given value in given array.
	 * 
	 * @param array
	 * @param value
	 *            value to search for, if null is passed -1 is returned
	 * @return position of value in array or -1
	 */
	public static int getPosInArray(String[] array, String value) {
		if (array == null || value == null) {
			return -1;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null && array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the "GET" Parameter representation for the URL (WITHOUT leading
	 * "?" or "&") for all parameters in request !
	 * 
	 * @param request
	 * @return
	 */
	public static String getURLParams(PortletRequest request) {
		StringBuffer urlParams = new StringBuffer("");

		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			String newParam = toURLParam(paramValues, paramName);
			if (urlParams.length() != 0 && newParam.length() > 0) {
				urlParams.append("&");
			}
			urlParams.append(newParam);
		}

		return urlParams.toString();
	}

	/**
	 * Returns the "GET" Parameter representation for the URL (WITHOUT leading
	 * "?" or "&")
	 * 
	 * @param values
	 * @param paramName
	 * @return
	 */
	public static String toURLParam(String[] values, String paramName) {
		StringBuffer urlParam = new StringBuffer("");
		int i = 0;
		for (i = 0; i < values.length; i++) {
			if (urlParam.length() != 0) {
				urlParam.append("&");
			}
			urlParam.append(toURLParam(paramName, values[i]));
		}

		return urlParam.toString();
	}

	/**
	 * Returns the "GET" Parameter representation for the URL (WITHOUT leading
	 * "?" or "&") NOTICE: if string is null or empty, return value is ""
	 * 
	 * @param value
	 * @param paramName
	 * @return
	 */
	public static String toURLParam(String paramName, String value) {
		if (value == null || value.length() == 0) {
			return "";
		}

		String urlParam = null;
		try {
			urlParam = paramName + "=" + URLEncoder.encode(value, "UTF-8");
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Problems generating URL representation of parameter: " + paramName + "=" + value
						+ "We generate NO Parameter !", ex);
			}
			urlParam = "";
		}

		return urlParam;
	}

	/**
	 * Append a new urlParameter to the given url Parameters.
	 * 
	 * @param currentURLParams
	 * @param newURLParam
	 */
	public static void appendURLParameter(StringBuffer currentURLParams, String newURLParam) {
		if (newURLParam != null && newURLParam.length() > 0) {
			if (!currentURLParams.toString().equals("?")) {
				currentURLParams.append("&");
			}
			currentURLParams.append(newURLParam);
		}
	}

	/**
	 * Returns, if the session has been marked as JavaScript- enabled.
	 * 
	 * @param request
	 *            The PortletRequest to check.
	 * @return True for JavaScript enabled, false for not.
	 */
	public static boolean isJavaScriptEnabled(PortletRequest request) {
		String hasJavaScriptStr = (String) request.getPortletSession().getAttribute(Settings.MSG_HAS_JAVASCRIPT);
		if (hasJavaScriptStr != null && hasJavaScriptStr.equals(Settings.MSGV_TRUE)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getMD5Hash(String val) {
		MessageDigest mdAlgorithm = null;
		try {
			mdAlgorithm = MessageDigest.getInstance("MD5");
			mdAlgorithm.update(val.getBytes());

			String plainText = "";

			byte[] digest = mdAlgorithm.digest();
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < digest.length; i++) {
				plainText = Integer.toHexString(0xFF & digest[i]);

				if (plainText.length() < 2) {
					plainText = "0" + plainText;
				}

				hexString.append(plainText);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("unable to create MD5 hash from String '" + val + "'", e);
			return null;
		}

	}

	public static String mergeTemplate(PortletConfig portletConfig, Map attributes, String attributesName,
			String template) {

		String realTemplatePath = portletConfig.getPortletContext().getRealPath(template);
		return mergeTemplate(realTemplatePath, attributes, attributesName);
	}

	public static String mergeTemplate(String realTemplatePath, Map attributes, String attributesName) {

		VelocityContext context = new VelocityContext();
		context.put(attributesName, attributes);
		StringWriter sw = new StringWriter();

		try {
			FileReader templateReader = new FileReader(realTemplatePath);

			sw = new StringWriter();
			Velocity.evaluate(context, sw, "UserEmailProcessor", templateReader);

		} catch (Exception e) {
			log.error("failed to merge velocity template: " + realTemplatePath, e);
		}
		String buffer = sw.getBuffer().toString();
		return buffer;
	}

	/**
	 * Send email
	 * 
	 * @param from
	 * @param subject
	 * @param to
	 * @param text
	 * @param headers
	 * @return true if email was sent, else false
	 */
	public static boolean sendEmail(String from, String subject, String[] to, String text, HashMap headers) {

		boolean debug = log.isDebugEnabled();
		boolean emailSent = false;

		Properties props = new Properties();
		props.put("mail.smtp.host", PortalConfig.getInstance().getString(PortalConfig.EMAIL_SMTP_SERVER, "localhost"));

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		try {
			// set the from and to address
			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(addressFrom);

			InternetAddress[] addressTo = new InternetAddress[to.length];
			for (int i = 0; i < to.length; i++) {
				addressTo[i] = new InternetAddress(to[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// set custom headers
			if (headers != null) {
				Set keySet = headers.keySet();
				Iterator it = keySet.iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					msg.addHeader(key, (String) headers.get(key));
				}
			}

			// Setting the Subject and Content Type
			msg.setSubject(subject);
			msg.setContent(text, "text/plain; charset=ISO-8859-1");
			Transport.send(msg);
			emailSent = true;
		} catch (AddressException e) {
			log.error("invalid email address format", e);
		} catch (MessagingException e) {
			log.error("error sending email.", e);
		}

		return emailSent;
	}

	public static String getResourceAsStream(String resource) throws Exception {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

		String stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader != null) {
			URL url = classLoader.getResource(stripped);
			if (url != null) {
				stream = url.toString();
			}
		}
		if (stream == null) {
			Environment.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			URL url = Environment.class.getClassLoader().getResource(stripped);
			if (url != null) {
				stream = url.toString();
			}
		}
		if (stream == null) {
			throw new Exception(resource + " not found");
		}
		return stream;
	}

	/**
	 * Extend an Arraylist with null values until the capacity size is reached.
	 * 
	 * @param list
	 * @param size
	 */
	public static void ensureArraySize(ArrayList list, int size) {
		if (list.size() < size) {
			Object[] objArray = new Object[size - list.size()];
			list.ensureCapacity(size);
			list.addAll(Arrays.asList(objArray));
		}
	}

	/**
	 * Returns true if a user is logged on, false, if not.
	 * 
	 * @param request
	 *            The PortletRequest.
	 * @return True if a user is logged on, false, if not.
	 */
	public static boolean getLoggedOn(PortletRequest request) {
		Principal principal = request.getUserPrincipal();
		return (principal != null);
	}

	/**
	 * Transforms different syntax for partner key to and from partner <->
	 * provider.
	 * 
	 * provider keys are defined as <partner_key>_<provider_key>. The
	 * <partner_key> is treated different in for providers and partners.
	 * 
	 * Sample: The partner 'bund' has the provider_key 'bu'.
	 * 
	 * @param key
	 *            The partner or provider has the same key
	 * @param fromPartnerToProvider
	 * @return The partner key.
	 */
	public static String normalizePartnerKey(String key, boolean fromPartnerToProvider) {
		if (fromPartnerToProvider) {
			if (key.equalsIgnoreCase("bund")) {
				return "bu";
			}
		} else {
			if (key.equalsIgnoreCase("bu")) {
				return "bund";
			}
		}
		return key.toLowerCase();
	}

}
