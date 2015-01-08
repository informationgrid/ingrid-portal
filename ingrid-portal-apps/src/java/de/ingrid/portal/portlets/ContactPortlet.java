package de.ingrid.portal.portlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.octo.captcha.service.CaptchaServiceException;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ContactForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.jcaptcha.CaptchaServiceSingleton;
import de.ingrid.portal.om.IngridCMS;
import de.ingrid.portal.om.IngridCMSItem;
import de.ingrid.portal.om.IngridNewsletterData;

public class ContactPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(ContactPortlet.class);
    
    private final static String TEMPLATE_FORM_INPUT = "/WEB-INF/templates/contact.vm";

    private final static String TEMPLATE_SUCCESS = "/WEB-INF/templates/contact_success.vm";

    private static final String EMAIL_TEMPLATE = "/WEB-INF/templates/contact_email.vm";

    public final static String PARAMV_ACTION_SUCCESS = "doSuccess";

    private final static String PARAMV_ACTION_DB_DO_REFRESH = "doRefresh";

    
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        setDefaultViewPage(TEMPLATE_FORM_INPUT);

        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        ContactForm cf = (ContactForm) Utils.getActionForm(request, ContactForm.SESSION_KEY, ContactForm.class);
        context.put("actionForm", cf);

        // contact email address
        context.put("portalEmail", UtilsString.htmlescapeAll(PortalConfig.getInstance().getString(PortalConfig.EMAIL_CONTACT_FORM_RECEIVER,
                "portalu@portalu.de")));

        // enable captcha 
        context.put("enableCaptcha", PortalConfig.getInstance().getBoolean("portal.contact.enable.captcha", Boolean.TRUE));

        // enable upload 
        context.put("uploadEnable", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_CONTACT_UPLOAD_ENABLE, Boolean.FALSE));
        context.put("uploadSize", PortalConfig.getInstance().getInt(PortalConfig.PORTAL_CONTACT_UPLOAD_SIZE, 10));

        // address after email
        Session session = HibernateUtil.currentSession();
        List entities = UtilsDB.getValuesFromDB(session.createCriteria(IngridCMS.class).add(
                Restrictions.eq("itemKey", "portalu.contact.intro.postEmail")), session, null, true);
        if (entities.size() > 0) {
            IngridCMS entry = (IngridCMS) entities.get(0);
            String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
            IngridCMSItem localizedItem = entry.getLocalizedEntry(lang);
            
            if(localizedItem == null){
            	localizedItem = entry.getLocalizedEntry(Locale.getDefault().getLanguage());
            }
        	context.put("contactIntroPostEmail", localizedItem.getItemValue());
        }
        
        // show newsletter option if configured that way
        context.put("enableNewsletter", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_NEWSLETTER, Boolean.TRUE));

        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        // clear form if render request not after action request (e.g. page
        // entered from other page)
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            cf.clear();
        } else if (action.equals(PARAMV_ACTION_SUCCESS)) {
            response.setTitle(messages.getString("contact.success.title"));
            setDefaultViewPage(TEMPLATE_SUCCESS);
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    	List<FileItem> items = null;
    	File uploadFile = null;
    	boolean uploadEnable = PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_CONTACT_UPLOAD_ENABLE, Boolean.FALSE);
        int uploadSize = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_CONTACT_UPLOAD_SIZE, 10); 
        
        RequestContext requestContext = (RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV);
        HttpServletRequest servletRequest = requestContext.getRequest();
        if(ServletFileUpload.isMultipartContent(servletRequest)){
    		DiskFileItemFactory factory = new DiskFileItemFactory();

    		// Set factory constraints
    		factory.setSizeThreshold(uploadSize * 1000 * 1000);
    		File file = new File(".");
    		factory.setRepository(file);
    		ServletFileUpload.isMultipartContent(servletRequest);
    		// Create a new file upload handler
    		ServletFileUpload upload = new ServletFileUpload(factory);

    		// Set overall request size constraint
    		upload.setSizeMax(uploadSize * 1000 * 1000);

    		// Parse the request
    		try {
				items = upload.parseRequest(servletRequest);
			} catch (FileUploadException e) {
				// TODO Auto-generated catch block
			}
        }
    	if(items != null){
    		 // check form input
        	if (request.getParameter(PARAMV_ACTION_DB_DO_REFRESH) != null) {
        		 
        		ContactForm cf = (ContactForm) Utils.getActionForm(request, ContactForm.SESSION_KEY, ContactForm.class);
                cf.populate(items);
                
        		String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
                actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));
                return;
        	}else{
        		Boolean isResponseCorrect = false;
                
            	ContactForm cf = (ContactForm) Utils.getActionForm(request, ContactForm.SESSION_KEY, ContactForm.class);
                cf.populate(items);
                if (!cf.validate()) {
                    // add URL parameter indicating that portlet action was called
                    // before render request
                	 	
                    String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
                    actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));
                    
                    return;
                }

                //remenber that we need an id to validate!
                String sessionId = request.getPortletSession().getId();
                //retrieve the response
                boolean enableCaptcha = PortalConfig.getInstance().getBoolean("portal.contact.enable.captcha", Boolean.TRUE);
                
                if(enableCaptcha){
                	String response = request.getParameter("jcaptcha");
                	for(FileItem item : items){
	           			if(item.getFieldName() != null){
	           				if(item.getFieldName().equals("jcaptcha")){
	           					response = item.getString();
	           					break;
	           				}
	           			}
                	}
                	// Call the Service method
    				try {
    				    isResponseCorrect = CaptchaServiceSingleton.getInstance().validateResponseForID(sessionId,
    				            response);
    				} catch (CaptchaServiceException e) {
    				     //should not happen, may be thrown if the id is not valid
    				}
                }
                 
                if (isResponseCorrect || !enableCaptcha){
                	 try {
                         IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                                 request.getLocale()), request.getLocale());

                         HashMap mailData = new HashMap();
                         mailData.put("user.name.given", cf.getInput(ContactForm.FIELD_FIRSTNAME));
                         mailData.put("user.name.family", cf.getInput(ContactForm.FIELD_LASTNAME));
                         mailData.put("user.employer", cf.getInput(ContactForm.FIELD_COMPANY));
                         mailData.put("user.business-info.telecom.telephone", cf.getInput(ContactForm.FIELD_PHONE));
                         mailData.put("user.business-info.online.email", cf.getInput(ContactForm.FIELD_EMAIL));
                         mailData.put("user.area.of.profession", messages.getString("contact.report.email.area.of.profession."
                                 + cf.getInput(ContactForm.FIELD_ACTIVITY)));
                         mailData.put("user.interest.in.enviroment.info", messages
                                 .getString("contact.report.email.interest.in.enviroment.info."
                                         + cf.getInput(ContactForm.FIELD_INTEREST)));
                         if (cf.hasInput(ContactForm.FIELD_NEWSLETTER)) {
                             Session session = HibernateUtil.currentSession();
                             Transaction tx = null;

                             try {

                                 mailData.put("user.subscribed.to.newsletter", "yes");
                                 // check for email address in newsletter list
                                 tx = session.beginTransaction();
                                 List newsletterDataList = session.createCriteria(IngridNewsletterData.class).add(
                                         Restrictions.eq("emailAddress", cf.getInput(ContactForm.FIELD_EMAIL))).list();
                                 tx.commit();
                                 // register for newsletter if not already registered
                                 if (newsletterDataList.isEmpty()) {
                                     IngridNewsletterData data = new IngridNewsletterData();
                                     data.setFirstName(cf.getInput(ContactForm.FIELD_FIRSTNAME));
                                     data.setLastName(cf.getInput(ContactForm.FIELD_LASTNAME));
                                     data.setEmailAddress(cf.getInput(ContactForm.FIELD_EMAIL));
                                     data.setDateCreated(new Date());

                                     tx = session.beginTransaction();
                                     session.save(data);
                                     tx.commit();
                                 }
                             } catch (Throwable t) {
                                 if (tx != null) {
                                     tx.rollback();
                                 }
                                 throw new PortletException(t.getMessage());
                             } finally {
                                 HibernateUtil.closeSession();
                             }
                         } else {
                             mailData.put("user.subscribed.to.newsletter", "no");
                         }
                         mailData.put("message.body", cf.getInput(ContactForm.FIELD_MESSAGE));

                         Locale locale = request.getLocale();

                         String language = locale.getLanguage();
                         String localizedTemplatePath = EMAIL_TEMPLATE;
                         int period = localizedTemplatePath.lastIndexOf(".");
                         if (period > 0) {
                             String fixedTempl = localizedTemplatePath.substring(0, period) + "_" + language + "."
                                     + localizedTemplatePath.substring(period + 1);
                             if (new File(getPortletContext().getRealPath(fixedTempl)).exists()) {
                                 localizedTemplatePath = fixedTempl;
                             }
                         }

                         String emailSubject = messages.getString("contact.report.email.subject");

                         if(PortalConfig.getInstance().getBoolean("email.contact.subject.add.topic", Boolean.FALSE)){
                        	 if(cf.getInput(ContactForm.FIELD_ACTIVITY) != null && !cf.getInput(ContactForm.FIELD_ACTIVITY).equals("none")){
                        		 emailSubject = emailSubject + " - " + messages.getString("contact.report.email.area.of.profession." + cf.getInput(ContactForm.FIELD_ACTIVITY));
                        	 }
                         }
                         
                         String from = cf.getInput(ContactForm.FIELD_EMAIL);
                         String to = PortalConfig.getInstance().getString(PortalConfig.EMAIL_CONTACT_FORM_RECEIVER, "foo@bar.com");

                         String text = Utils.mergeTemplate(getPortletConfig(), mailData, "map", localizedTemplatePath);
                         if(uploadEnable){
                        	 if(uploadEnable){
                        		 for(FileItem item : items){
                        			 if(item.getFieldName() != null){
                        				 if(item.getFieldName().equals("upload")){
                        					 uploadFile = new File(sessionId + "_" + item.getName());
                        					 // read this file into InputStream
                        					 InputStream inputStream = item.getInputStream();

                        					 // write the inputStream to a FileOutputStream
                        					 OutputStream out = new FileOutputStream(uploadFile);
                        					 int read = 0;
                        					 byte[] bytes = new byte[1024];

                        					 while ((read = inputStream.read(bytes)) != -1) {
                        						 out.write(bytes, 0, read);
                        					 }

                        					 inputStream.close();
                        					 out.flush();
                        					 out.close();
                        					 break;
                        				 }
                        			 }
                        		 }
                        	 }
                        	 Utils.sendEmail(from, emailSubject, new String[] { to }, text, null, uploadFile);
                        }else{
                        	 Utils.sendEmail(from, emailSubject, new String[] { to }, text, null);
                        }
                     } catch (Throwable t) {
                         cf.setError("", "Error sending mail from contact form.");
                         log.error("Error sending mail from contact form.", t);
                     }

                     // temporarily show same page with content
                     String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_SUCCESS);
                     actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));
                }else{
               		cf.setErrorCaptcha();
               		String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
                    actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));
                    return;
                }
        	}
    	}else{
    		ContactForm cf = (ContactForm) Utils.getActionForm(request, ContactForm.SESSION_KEY, ContactForm.class);
            cf.setErrorUpload();
       		String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
            actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));
            return;
    	}
    }
}
