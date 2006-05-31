package de.ingrid.portal.portlets;

import java.io.File;
import java.io.IOException;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ContactForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.om.IngridNewsletterData;

public class ContactPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(ContactPortlet.class);
    
    private final static String TEMPLATE_FORM_INPUT = "/WEB-INF/templates/contact.vm";

    private final static String TEMPLATE_SUCCESS = "/WEB-INF/templates/contact_success.vm";

    private static final String EMAIL_TEMPLATE = "/WEB-INF/templates/contact_email.vm";
    
    public final static String PARAMV_ACTION_SUCCESS = "doSuccess";

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        setDefaultViewPage(TEMPLATE_FORM_INPUT);

        // put ActionForm to context. use variable name "actionForm" so velocity macros work !
        ContactForm cf = (ContactForm) Utils.getActionForm(request, ContactForm.SESSION_KEY, ContactForm.class);
        context.put("actionForm", cf);

        // contact email address
        context.put("portalEmail", PortalConfig.getInstance().getString(PortalConfig.EMAIL_CONTACT_FORM_RECEIVER, "portalu@portalu.de"));
        
        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        // clear form if render request not after action request (e.g. page entered from other page)
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            cf.clear();
        } else if (action.equals(PARAMV_ACTION_SUCCESS)) {
            setDefaultViewPage(TEMPLATE_SUCCESS);
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // check form input
        ContactForm cf = (ContactForm) Utils.getActionForm(request, ContactForm.SESSION_KEY, ContactForm.class);
        cf.populate(request);
        if (!cf.validate()) {
            // add URL parameter indicating that portlet action was called before render request
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
            actionResponse.sendRedirect(Settings.PAGE_CONTACT + urlViewParam);
            return;
        }

        try {
            IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                    request.getLocale()));

            HashMap mailData = new HashMap();
            mailData.put("user.name.given", cf.getInput(ContactForm.FIELD_FIRSTNAME));
            mailData.put("user.name.family", cf.getInput(ContactForm.FIELD_LASTNAME));
            mailData.put("user.employer", cf.getInput(ContactForm.FIELD_COMPANY));
            mailData.put("user.business-info.telecom.telephone", cf.getInput(ContactForm.FIELD_PHONE));
            mailData.put("user.business-info.online.email", cf.getInput(ContactForm.FIELD_EMAIL));
            mailData.put("user.area.of.profession", messages.getString("contact.report.email.area.of.profession." + cf.getInput(ContactForm.FIELD_ACTIVITY)));
            mailData.put("user.interest.in.enviroment.info", messages.getString("contact.report.email.interest.in.enviroment.info." + cf.getInput(ContactForm.FIELD_INTEREST)));
            if (cf.hasInput(ContactForm.FIELD_NEWSLETTER)) {
                Session session = HibernateManager.getInstance().getSession();

                mailData.put("user.subscribed.to.newsletter", "yes");
                // check for email address in newsletter list
                List newsletterDataList = session.createCriteria(IngridNewsletterData.class)
                .add(Restrictions.eq("emailAddress", cf.getInput(ContactForm.FIELD_EMAIL)))
                .list();
                // register for newsletter if not already registered
                if (newsletterDataList.isEmpty()) {
                    IngridNewsletterData data = new IngridNewsletterData(); 
                    data.setFirstName(cf.getInput(ContactForm.FIELD_FIRSTNAME));
                    data.setLastName(cf.getInput(ContactForm.FIELD_LASTNAME));
                    data.setEmailAddress(cf.getInput(ContactForm.FIELD_EMAIL));
                    data.setDateCreated(new Date());
                    
                    session.beginTransaction();
                    session.save(data);
                    session.getTransaction().commit();
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
                String fixedTempl = localizedTemplatePath.substring(0, period) + "_"
                        + language + "." + localizedTemplatePath.substring(period + 1);
                if (new File(getPortletContext().getRealPath(fixedTempl)).exists()) {
                    localizedTemplatePath = fixedTempl;
                }
            }

            String emailSubject = messages.getString("contact.report.email.subject");
            
            String from = cf.getInput(ContactForm.FIELD_EMAIL);
            String to = PortalConfig.getInstance().getString(PortalConfig.EMAIL_CONTACT_FORM_RECEIVER, "foo@bar.com");
            
            String text = Utils.mergeTemplate(getPortletConfig(), mailData, "map", localizedTemplatePath);
            Utils.sendEmail(from, emailSubject, new String[] {to}, text, null);
        } catch (Throwable t) {
            cf.setError("", "Error sending mail from contact form.");
            log.error("Error sending mail from contact form.", t);
        }
        
        // temporarily show same page with content
        String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_SUCCESS);
        actionResponse.sendRedirect(Settings.PAGE_CONTACT + urlViewParam);
    }
}