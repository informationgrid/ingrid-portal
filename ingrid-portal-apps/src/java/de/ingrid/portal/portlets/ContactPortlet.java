/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.portlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
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

public class ContactPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(ContactPortlet.class);
    
    private final static String TEMPLATE_FORM_INPUT = "/WEB-INF/templates/contact.vm";

    private final static String TEMPLATE_SUCCESS = "/WEB-INF/templates/contact_success.vm";

    private static final String EMAIL_TEMPLATE = "/WEB-INF/templates/contact_email.vm";

    public final static String PARAMV_ACTION_SUCCESS = "doSuccess";

    private final static String PARAMV_ACTION_DB_DO_REFRESH = "doRefresh";

    private UserManager userManager;

    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
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
                "info@informationgrid.eu")));

        // enable captcha 
        context.put("enableCaptcha", PortalConfig.getInstance().getBoolean("portal.contact.enable.captcha", Boolean.TRUE));

        // enable upload 
        context.put("uploadEnable", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_CONTACT_UPLOAD_ENABLE, Boolean.FALSE));
        context.put("uploadSize", PortalConfig.getInstance().getInt(PortalConfig.PORTAL_CONTACT_UPLOAD_SIZE, 10));

        // address after email
        Session session = HibernateUtil.currentSession();
        List entities = UtilsDB.getValuesFromDB(session.createCriteria(IngridCMS.class).add(
                Restrictions.eq("itemKey", "ingrid.contact.intro.postEmail")), session, null, true);
        if (entities.size() > 0) {
            IngridCMS entry = (IngridCMS) entities.get(0);
            String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
            IngridCMSItem localizedItem = entry.getLocalizedEntry(lang);
            
            if(localizedItem == null){
            	localizedItem = entry.getLocalizedEntry(Locale.getDefault().getLanguage());
            }
        	context.put("contactIntroPostEmail", localizedItem.getItemValue());
        }
        

        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        // clear form if render request not after action request (e.g. page
        // entered from other page)
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            cf.clear();
            // but add user data from logged in user, see REDMINE-302
            populateWithLoggedInUser(cf, request.getUserPrincipal());
        } else if (action.equals(PARAMV_ACTION_SUCCESS)) {
            response.setTitle(messages.getString("contact.success.title"));
            setDefaultViewPage(TEMPLATE_SUCCESS);
        }

        super.doView(request, response);
    }
    
    /** Populate contact form with data from principal if principal not null */
    private void populateWithLoggedInUser(ContactForm cf, Principal userPrincipal) {
        User user = null;
        if (userPrincipal != null) {
            try {
                user = userManager.getUser(userPrincipal.getName());
            } catch (SecurityException e) {
                e.printStackTrace();
            }            
        }
        if (user != null) {
            Map<String, String> userInfo = user.getInfoMap();
            if (!cf.hasInput( cf.FIELD_FIRSTNAME ))
                cf.setInput( cf.FIELD_FIRSTNAME, userInfo.get( "user.name.given" ));
            if (!cf.hasInput( cf.FIELD_LASTNAME ))
                cf.setInput( cf.FIELD_LASTNAME, userInfo.get( "user.name.family" ));
            if (!cf.hasInput( cf.FIELD_EMAIL ))
                cf.setInput( cf.FIELD_EMAIL, userInfo.get( "user.business-info.online.email" ));
        }        
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
                        			if(item.getName() != null && !item.getName().isEmpty()) {
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
