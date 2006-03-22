/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.forms.ContactNewsletterSubscribeForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridNewsletterData;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.utils.PlugDescription;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class ShowPartnerPortlet extends GenericVelocityPortlet {

    private HibernateManager fHibernateManager = null;
    
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        
        Context context = getContext(request);

        fHibernateManager  = HibernateManager.getInstance();
        Session session = this.fHibernateManager.getSession();

        List partnerList = session.createCriteria(IngridPartner.class).list();

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
        
        PlugDescription[] plugs = IBUSInterfaceImpl.getInstance().getIBus().getIPlugRegistry().getAllIPlugs();
        
        HashMap partnerMap = new HashMap();
        
        Iterator it = partnerList.iterator();
        while (it.hasNext()) {
            IngridPartner partner = (IngridPartner)it.next();
            HashMap partnerHash = new HashMap();
            partnerHash.put("partner", partner);
            partnerMap.put(partner.getIdent(), partnerHash);
            for (int i=0; i<plugs.length; i++) {
                PlugDescription plug = plugs[i]; 
                // check for ident AND name
                // TODO: all plug descriptions should user the partner abbr.
                if (plug.getPartner().equalsIgnoreCase(partner.getIdent()) || plug.getPartner().equalsIgnoreCase(partner.getName())) {
                    // check for providers
                    if (!partnerHash.containsKey("providers")) {
                        partnerHash.put("providers", new HashMap());
                    }
                    // get providers of the partner
                    HashMap providersHash = (HashMap)partnerMap.get("providers");
                    // check for prvider with an organisation abbr.
                    if (!providersHash.containsKey(plug.getOrganisationAbbr())) {
                        providersHash.put(plug.getOrganisationAbbr(), new HashMap());
                    }
                    // get provider hash map
                    HashMap providerHash = (HashMap)partnerMap.get(plug.getOrganisationAbbr());
                    // check for provider entry, create if not exists
                    // initialise with iplug, which contains all information
                    if (!providerHash.containsKey("provider")) {
                        providerHash.put("provider", plug);
                    }
                    // check for iplugs
                    if (!partnerHash.containsKey("iplugs")) {
                        providerHash.put("iplugs", new ArrayList());
                    }
                    // add current iplug
                    ((ArrayList)providerHash.get("iplugs")).add(plug);
                }
            }
        }
        
        context.put("partners", partnerMap);
        
        super.doView(request, response);
    }

}
