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
public class ShowDataSourcePortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(ShowDataSourcePortlet.class);

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        String[] hideIPlugIdList = PortalConfig.getInstance().getStringArray(PortalConfig.HIDE_IPLUG_ID_LIST);
        if(hideIPlugIdList != null){
        	context.put("hideIPlugIdList", hideIPlugIdList);
        }
               
        try {
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
