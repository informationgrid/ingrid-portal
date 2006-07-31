/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.utils.PlugDescription;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class ShowPartnerPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(ShowPartnerPortlet.class);

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        Context context = getContext(request);

        List partnerList = UtilsDB.getPartners();
        List providerList = UtilsDB.getProviders();

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        try {
            PlugDescription[] plugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();

            LinkedHashMap partnerMap = new LinkedHashMap();

            /*
             * This code builds a structure as follows:
             * 
             * partnerMap (partner Ident => partnerHash)
             * 
             * partnerHash ("partner" => IngridPartner) ("providers" =>
             * providersHash)
             * 
             * providersHash (provider ident of the first iPlug found for this
             * partner => providerHash)
             * 
             * providerHash ("provider" => IngridProvider)
             * 
             */

            Iterator it = partnerList.iterator();
            while (it.hasNext()) {
                IngridPartner partner = (IngridPartner) it.next();
                LinkedHashMap partnerHash = new LinkedHashMap();
                partnerHash.put("partner", partner);
                partnerMap.put(partner.getIdent(), partnerHash);
                Iterator providerIterator = providerList.iterator();
                while (providerIterator.hasNext()) {
                    IngridProvider provider = (IngridProvider) providerIterator.next();
                    String providerIdent = provider.getIdent();
                    String partnerIdent = "";
                    if (providerIdent != null && providerIdent.length() > 0) {
                        partnerIdent = providerIdent.substring(0, providerIdent.indexOf("_"));
                        // hack: "bund" is coded as "bu" in provider idents
                        if (partnerIdent.equals("bu")) {
                            partnerIdent = "bund";
                        }
                    }
                    if (partnerIdent.equals(partner.getIdent())) {
                        // check for providers
                        if (!partnerHash.containsKey("providers")) {
                            partnerHash.put("providers", new LinkedHashMap());
                        }
                        // get providers of the partner
                        HashMap providersHash = (LinkedHashMap) partnerHash.get("providers");
                        // LinkedHashMap for prvider with a provider id.
                        if (!providersHash.containsKey(providerIdent)) {
                            providersHash.put(providerIdent, new LinkedHashMap());
                        }
                        // get provider hash map
                        LinkedHashMap providerHash = (LinkedHashMap) providersHash.get(providerIdent);
                        // check for provider entry, create if not exists
                        // initialise with iplug, which contains all information
                        if (!providerHash.containsKey("provider")) {
                            providerHash.put("provider", provider);
                        }
                    }
                }
            }

            context.put("partners", partnerMap);

            // set up plug list for view, remove plugs with same name !
            ArrayList plugDescriptions = new ArrayList();
            String newName = null;
            String oldName = null;
            for (int i = 0; i < plugs.length; i++) {
                newName = plugs[i].getDataSourceName();
                boolean found = false;
                for (int j = 0; j < plugDescriptions.size(); j++) {
                    oldName = ((PlugDescription) plugDescriptions.get(j)).getDataSourceName();
                    if (newName != null && oldName != null && newName.equals(oldName)) {
                        found = true;
                    }
                }
                if (!found) {
                    plugDescriptions.add(plugs[i]);
                }
            }

            // sort alphabetical
            if (plugDescriptions != null) {
                Collections.sort(plugDescriptions, new PlugNameComparator());
            }

            context.put("plugs", plugDescriptions);
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing partner/provider and iPlugs.", t);
            }
        }

        super.doView(request, response);
    }

    /**
     * Inner class: PlugNameComparator for plugs sorting;
     *
     * @author Martin Maidhof
     */
    private class PlugNameComparator implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public final int compare(Object a, Object b) {
            try {
                String aName = ((PlugDescription) a).getDataSourceName().toLowerCase();
                String bName = ((PlugDescription) b).getDataSourceName().toLowerCase();

                return aName.compareTo(bName);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
