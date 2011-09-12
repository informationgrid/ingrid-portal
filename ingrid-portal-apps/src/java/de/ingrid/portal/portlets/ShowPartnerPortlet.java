/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.PlugDescription;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class ShowPartnerPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(ShowPartnerPortlet.class);

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        String[] hideIPlugIdList = PortalConfig.getInstance().getStringArray(PortalConfig.HIDE_IPLUG_ID_LIST);
        if(hideIPlugIdList != null){
        	context.put("hideIPlugIdList", hideIPlugIdList);
        }
               
        try {
            String partnerRestriction = PortalConfig.getInstance().getString(
                    PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
            if (partnerRestriction == null || partnerRestriction.length() == 0) {
                context.put("partners", UtilsDB.getPartnerProviderMap(null));
            } else {
                ArrayList filter = new ArrayList();
                filter.add(partnerRestriction);
                context.put("partners", UtilsDB.getPartnerProviderMap(filter));
            }

            // set up plug list for view, remove plugs with same name !
            PlugDescription[] plugs = IBUSInterfaceImpl.getInstance().getAllActiveIPlugs();
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
