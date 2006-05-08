/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.forms.ContactNewsletterSubscribeForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDB;
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

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        
        Context context = getContext(request);

        List partnerList = UtilsDB.getPartners();

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
        
        PlugDescription[] plugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();
        
        LinkedHashMap partnerMap = new LinkedHashMap();
        
        /*
         * This code builds a structure as follows:
         * 
         * partnerMap
         *  (partner Ident => partnerHash)
         *  
         * partnerHash
         *  ("partner" => IdentPartner)
         *  ("providers" => providersHash)
         * 
         * providersHash
         *  (provider ident of the first iPlug found for this partner => providerHash)
         *  
         * providerHash
         *  ("provider" => first iPlug that hast been found for this provider) 
         *  ("iplugs" => ArrayList of all iPlugs that have been found for this provider)
         * 
         */
        
        Iterator it = partnerList.iterator();
        while (it.hasNext()) {
            IngridPartner partner = (IngridPartner)it.next();
            LinkedHashMap partnerHash = new LinkedHashMap();
            partnerHash.put("partner", partner);
            partnerMap.put(partner.getIdent(), partnerHash);
            for (int i=0; i<plugs.length; i++) {
                PlugDescription plug = plugs[i];
                String[] providers = plug.getProviders();
                for (int j=0;j<providers.length;j++) {
                    String partnerIdent = providers[j].substring(0, providers[j].indexOf("_"));
                    // hack: "bund" is coded as "bu" in provider idents
                    if (partnerIdent.equals("bu")) {
                        partnerIdent = "bund";
                    }
                        
                    if (partnerIdent.equalsIgnoreCase(partner.getIdent()) || partnerIdent.equalsIgnoreCase(partner.getName())) {
                        // check for providers
                        if (!partnerHash.containsKey("providers")) {
                            partnerHash.put("providers", new LinkedHashMap());
                        }
                        // get providers of the partner
                        HashMap providersHash = (LinkedHashMap)partnerHash.get("providers");
                        // LinkedHashMap for prvider with a provider id.
                        if (!providersHash.containsKey(providers[j])) {
                            providersHash.put(providers[j], new LinkedHashMap());
                        }
                        // get provider hash map
                        LinkedHashMap providerHash = (LinkedHashMap)providersHash.get(providers[j]);
                        // check for provider entry, create if not exists
                        // initialise with iplug, which contains all information
                        if (!providerHash.containsKey("provider")) {
                            providerHash.put("provider", UtilsDB.getIngridProviderFromKey(providers[j]));
                        }
                        // check for iplugs
                        if (!providerHash.containsKey("iplugs")) {
                            providerHash.put("iplugs", new ArrayList());
                        }
                        // add current iplug
                        ((ArrayList)providerHash.get("iplugs")).add(plug);
                    }
                }
            }
        }
        
        context.put("partners", partnerMap);
        
        super.doView(request, response);
    }

}
