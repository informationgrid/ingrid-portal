/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.forms.ContactNewsletterSubscribeForm;
import de.ingrid.portal.forms.ContactNewsletterUnsubscribeForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridNewsletterData;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class ContactNewsletterPortlet extends GenericVelocityPortlet {

    private static final String STATE_UNSUBSCRIBED = "unsubscribed"; 
    private static final String STATE_SUBSCRIBED = "subscribed"; 

    private final static String TEMPLATE_UNSUBSCRIBED = "/WEB-INF/templates/newsletter_unsubscribed.vm";
    private final static String TEMPLATE_SUBSCRIBED = "/WEB-INF/templates/newsletter_subscribed.vm";
    
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        String cmd = request.getParameter("cmd");
        
        if (null != cmd && cmd.equals(STATE_UNSUBSCRIBED)) {
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_UNSUBSCRIBED);
        } else if (null != cmd && cmd.equals(STATE_SUBSCRIBED)) {
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_SUBSCRIBED);
        }
        
        ContactNewsletterSubscribeForm cf1 = (ContactNewsletterSubscribeForm) Utils.getActionForm(request, ContactNewsletterSubscribeForm.SESSION_KEY, ContactNewsletterSubscribeForm.class);
        ContactNewsletterUnsubscribeForm cf2 = (ContactNewsletterUnsubscribeForm) Utils.getActionForm(request, ContactNewsletterUnsubscribeForm.SESSION_KEY, ContactNewsletterUnsubscribeForm.class);
        
        if (cmd == null) {
            cf1.clear();
            cf2.clear();
        }
        
        context.put("actionForm1", cf1);
        context.put("actionForm2", cf2);
        
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {
        String action = request.getParameter("cmd");
        actionResponse.setRenderParameter("cmd", action);

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;

        try {
        
            ContactNewsletterSubscribeForm cf1 = (ContactNewsletterSubscribeForm) Utils.getActionForm(request, ContactNewsletterSubscribeForm.SESSION_KEY, ContactNewsletterSubscribeForm.class);
            ContactNewsletterUnsubscribeForm cf2 = (ContactNewsletterUnsubscribeForm) Utils.getActionForm(request, ContactNewsletterUnsubscribeForm.SESSION_KEY, ContactNewsletterUnsubscribeForm.class);
            
            if (action.equalsIgnoreCase("subscribe")) {
                cf2.clearErrors();
                // check form input
                cf1.populate(request);
                if (!cf1.validate()) {
                    return;
                }
    
                tx = session.beginTransaction();
                List newsletterDataList = session.createCriteria(IngridNewsletterData.class)
                .add(Restrictions.eq("emailAddress", cf1.getInput(ContactNewsletterSubscribeForm.FIELD_EMAIL)))
                .list();
                tx.commit();
                
                if (newsletterDataList.isEmpty()) {
                
                    IngridNewsletterData data = new IngridNewsletterData(); 
                    data.setFirstName(cf1.getInput(ContactNewsletterSubscribeForm.FIELD_FIRSTNAME));
                    data.setLastName(cf1.getInput(ContactNewsletterSubscribeForm.FIELD_LASTNAME));
                    data.setEmailAddress(cf1.getInput(ContactNewsletterSubscribeForm.FIELD_EMAIL));
                    data.setDateCreated(new Date());
                    
                    tx = session.beginTransaction();
                    session.save(data);
                    tx.commit();
                    
                    actionResponse.setRenderParameter("cmd", STATE_SUBSCRIBED);
                } else {
                    cf1.setError(ContactNewsletterSubscribeForm.FIELD_EMAIL, "contact.newsletter.duplicate_email");
                }
            } else if (action.equalsIgnoreCase("unsubscribe")) {
                cf1.clearErrors();
                // check form input
                cf2.populate(request);
                if (!cf2.validate()) {
                    return;
                }
                
                // remove entry if exists
                List newsletterDataList = session.createCriteria(IngridNewsletterData.class)
                .add(Restrictions.eq("emailAddress", cf2.getInput(ContactNewsletterUnsubscribeForm.FIELD_EMAIL)))
                .list();
                
                Iterator it = newsletterDataList.iterator();
                if (it.hasNext()) {
                    session.beginTransaction();
                    while (it.hasNext()) {
                        session.delete((IngridNewsletterData) it.next());
                    }
                    session.getTransaction().commit();
                    actionResponse.setRenderParameter("cmd", STATE_UNSUBSCRIBED);
                } else  {
                    cf2.setError(ContactNewsletterUnsubscribeForm.FIELD_EMAIL, "contact.newsletter.email_not_found");
                }
            }
        } catch (Throwable t) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PortletException( t.getMessage() );
        } finally {
            HibernateUtil.closeSession();
        }
    }
}
