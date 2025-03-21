/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ContactZammadForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridCMS;
import de.ingrid.portal.om.IngridCMSItem;

public class ContactZammadPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger(ContactZammadPortlet.class);
    
    private static final String TEMPLATE_FORM_INPUT = "/WEB-INF/templates/contact.vm";

    private static final String TEMPLATE_SUCCESS = "/WEB-INF/templates/contact_success.vm";

    private static final String EMAIL_TEMPLATE = "/WEB-INF/templates/contact_email.vm";

    public static final String PARAMV_ACTION_SUCCESS = "doSuccess";

    private static final String PARAMV_ACTION_DB_DO_REFRESH = "doRefresh";

    private UserManager userManager;

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        setDefaultViewPage(TEMPLATE_FORM_INPUT);

        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        ContactZammadForm cf = (ContactZammadForm) Utils.getActionForm(request, ContactZammadForm.SESSION_KEY, ContactZammadForm.class);
        context.put("actionForm", cf);

        // contact email address
        context.put("portalEmail", UtilsString.htmlescapeAll(PortalConfig.getInstance().getString(PortalConfig.EMAIL_CONTACT_FORM_RECEIVER,
                "info@informationgrid.eu")));

        // enable upload 
        context.put("uploadEnable", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_CONTACT_UPLOAD_ENABLE, Boolean.FALSE));
        context.put("uploadSize", PortalConfig.getInstance().getInt(PortalConfig.PORTAL_CONTACT_UPLOAD_SIZE, 10));

        // insert timestamp token into session
        request.getPortletSession().setAttribute("timestampToken", System.currentTimeMillis());

        // address after email
        Session session = HibernateUtil.currentSession();
        List entities = UtilsDB.getValuesFromDB(session.createCriteria(IngridCMS.class).add(
                Restrictions.eq("itemKey", "ingrid.contact.intro.postEmail")), session, null, true);
        if (!entities.isEmpty()) {
            IngridCMS entry = (IngridCMS) entities.get(0);
            String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
            IngridCMSItem localizedItem = entry.getLocalizedEntry(lang);
            
            if(localizedItem == null){
                localizedItem = entry.getLocalizedEntry(Locale.getDefault().getLanguage());
            }
            context.put("contactIntroPostEmail", localizedItem.getItemValue());
        }
        
        context.put("partners", UtilsDB.getPartnersExclude(PortalConfig.getInstance().getList(PortalConfig.PORTAL_CONTACT_REMOVE_PARTNERS), "ident"));

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
    private void populateWithLoggedInUser(ContactZammadForm cf, Principal userPrincipal) {
        User user = null;
        if (userPrincipal != null) {
            try {
                user = userManager.getUser(userPrincipal.getName());
            } catch (SecurityException e) {
                log.error("Error on populateWithLoggedInUser.", e);
            }
        }
        if (user != null) {
            Map<String, String> userInfo = user.getInfoMap();
            if (!cf.hasInput( ContactZammadForm.FIELD_NAME ))
                cf.setInput( ContactZammadForm.FIELD_NAME, userInfo.get( "user.name.given" ) + userInfo.get( "user.name.family" ));
            if (!cf.hasInput( ContactZammadForm.FIELD_EMAIL ))
                cf.setInput( ContactZammadForm.FIELD_EMAIL, userInfo.get( "user.business-info.online.email" ));
        }        
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        List<FileItem> items = null;
        File uploadFile = null;
        boolean uploadEnable = PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_CONTACT_UPLOAD_ENABLE, Boolean.FALSE);
        int uploadSize = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_CONTACT_UPLOAD_SIZE, 10);
        int minFilloutDuration = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_FORM_VALID_FILLOUT_DURATION_MIN, 3);
        Object timestampToken = request.getPortletSession().getAttribute("timestampToken");


        RequestContext requestContext = (RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV);
        HttpServletRequest servletRequest = requestContext.getRequest();
        if(ServletFileUpload.isMultipartContent(servletRequest)){
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constraints
            factory.setSizeThreshold(uploadSize * 1000 * 1000);
            File file = new File(".");
            factory.setRepository(file);
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            // Set overall request size constraint
            upload.setSizeMax(uploadSize * 1000L * 1000L);

            // Parse the request
            try {
                items = upload.parseRequest(servletRequest);
            } catch (FileUploadException e) {
                log.error("Error on upload file.", e);
            }
        }
        if(items != null){
             // check form input
            for(FileItem item : items) {
                if (item.isFormField() && item.getFieldName().equals(PARAMV_ACTION_DB_DO_REFRESH)) {
                    ContactZammadForm cf = (ContactZammadForm) Utils.getActionForm(request, ContactZammadForm.SESSION_KEY, ContactZammadForm.class);
                    cf.populate(items);
                    cf.clearErrors();
                    
                    String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
                    actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));
                    return;
                }
            }
            
            ContactZammadForm cf = (ContactZammadForm) Utils.getActionForm(request, ContactZammadForm.SESSION_KEY, ContactZammadForm.class);
            cf.populate(items);
            if (!cf.validate() || timestampToken == null || System.currentTimeMillis() - (Long) timestampToken <= minFilloutDuration * 1000) {
                // check if token exists and user spends more than xx mins to fill out the form
                // add URL parameter indicating that portlet action was called
                // before render request

                String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
                actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));

                return;
            }

            // remember that we need an id to validate!
            String sessionId = request.getPortletSession().getId();

            try {
                IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                        request.getLocale()), request.getLocale());

                HashMap mailData = new HashMap();
                String technical = cf.getInput(ContactZammadForm.FIELD_CHK_TECHNICAL);
                String contactPortal = PortalConfig.getInstance().getString(PortalConfig.PORTAL_CONTACT_PORTAL, "");
                
                String filter = "";
                if(!contactPortal.isEmpty()) {
                    filter += contactPortal;
                }
                if(technical.isEmpty()) {
                    String partner = cf.getInput(ContactZammadForm.FIELD_COMPANY);
                    if(!partner.isEmpty()) {
                        if(!filter.isEmpty()) {
                            filter += " ";
                        }
                        filter += partner.toUpperCase();
                    }
                } else {
                    if(!filter.isEmpty()) {
                        filter += " ";
                    }
                    filter += messages.getString("contact.report.email.technical");
                }
                if(!contactPortal.isEmpty()) {
                    mailData.put("message.filter", "[" + filter + "]");
                }
                mailData.put("message.body", cf.getInput(ContactZammadForm.FIELD_MESSAGE));

                Locale locale = request.getLocale();

                String language = locale.getLanguage();
                String localizedTemplatePath = EMAIL_TEMPLATE;
                int period = localizedTemplatePath.lastIndexOf('.');
                if (period > 0) {
                    String fixedTempl = localizedTemplatePath.substring(0, period) + "_" + language + "."
                            + localizedTemplatePath.substring(period + 1);
                    if (new File(getPortletContext().getRealPath(fixedTempl)).exists()) {
                        localizedTemplatePath = fixedTempl;
                    }
                }

                String emailSubject = cf.getInput(ContactZammadForm.FIELD_SUBJECT);

                String from = PortalConfig.getInstance().getString(PortalConfig.EMAIL_CONTACT_FORM_SENDER, "");
                String[] replyTo = null;
                if(from.isEmpty()) {
                    from = cf.getInput(ContactZammadForm.FIELD_EMAIL);
                } else {
                    replyTo = new String[]{ cf.getInput(ContactZammadForm.FIELD_EMAIL) };
                }
                String to = PortalConfig.getInstance().getString(PortalConfig.EMAIL_CONTACT_FORM_RECEIVER, "foo@bar.com");

                String text = Utils.mergeTemplate(getPortletConfig(), mailData, "map", localizedTemplatePath);
                if(uploadEnable){
                    for(FileItem item : items){
                        if(item.getName() != null && !item.getName().isEmpty() && item.getFieldName() != null && item.getFieldName().equals("upload")) {
                            uploadFile = new File(sessionId + "_" + item.getName());
                            // read this file into InputStream
                            try(
                                    InputStream inputStream = item.getInputStream();
                                    // write the inputStream to a FileOutputStream
                                    OutputStream out = new FileOutputStream(uploadFile);
                            ){
                                int read = 0;
                                byte[] bytes = new byte[1024];

                                while ((read = inputStream.read(bytes)) != -1) {
                                    out.write(bytes, 0, read);
                                }
                            } catch (Exception e) {
                                log.error("Error loading stream.", e);
                            }
                            break;
                        }
                    }
                    Utils.sendEmail(from, emailSubject, new String[] { to }, text, null, uploadFile, replyTo);
                }else{
                    Utils.sendEmail(from, emailSubject, new String[] { to }, text, null, null, replyTo);
                }
            } catch (Exception e) {
                cf.setError("", "Error sending mail from contact form.");
                log.error("Error sending mail from contact form.", e);
            }

            // temporarily show same page with content
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_SUCCESS);
            actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));
        }else{
            ContactZammadForm cf = (ContactZammadForm) Utils.getActionForm(request, ContactZammadForm.SESSION_KEY, ContactZammadForm.class);
            cf.setErrorUpload();
               String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
            actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CONTACT + urlViewParam));
        }
    }
}
