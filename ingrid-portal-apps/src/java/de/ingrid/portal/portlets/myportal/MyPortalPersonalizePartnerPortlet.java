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
package de.ingrid.portal.portlets.myportal;

import java.io.IOException;
import java.security.Principal;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.UtilsSearch;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class MyPortalPersonalizePartnerPortlet extends GenericVelocityPortlet {

    protected final static String PAGE_PERSONALIZE_PARTNER = "/portal/search-extended/search-ext-env-area-partner.psml";

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "searchSettings.title.rankingAndGrouping");
        response.setTitle(messages.getString(titleKey));

        UtilsSearch.doViewForPartnerPortlet(request, context);

        Principal principal = request.getUserPrincipal();
        String partnerStr = (String) IngridPersistencePrefs.getPref(principal.getName(),
                IngridPersistencePrefs.SEARCH_PARTNER);

        if (partnerStr != null) {

            PortletSession session = request.getPortletSession();
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            Iterator it = partnerRoot.getChildren().iterator();
            while (it.hasNext()) {
                DisplayTreeNode partnerNode = (DisplayTreeNode) it.next();
                if (partnerStr.indexOf(Settings.QFIELD_PARTNER.concat(":").concat(partnerNode.getId())) != -1
                        || partnerNode.get("checked") != null) {
                    partnerNode.put("checked", "true");
                } else {
                    partnerNode.remove("checked");
                }
                Iterator it2 = partnerNode.getChildren().iterator();
                while (it2.hasNext()) {
                    DisplayTreeNode providerNode = (DisplayTreeNode) it2.next();
                    if (partnerStr.indexOf(Settings.QFIELD_PROVIDER.concat(":").concat(providerNode.getId())) != -1) {
                        providerNode.put("checked", "true");
                        partnerNode.setOpen(true);
                    } else {
                        providerNode.remove("checked");
                    }
                }
            }
        }
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        PortletSession session = request.getPortletSession();

        if (action.equalsIgnoreCase("doOpenPartner")) {
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            if (partnerRoot != null) {
                DisplayTreeNode node = partnerRoot.getChild(request.getParameter("id"));
                node.setOpen(true);
                if (node.get("checked") != null) {
                    Iterator it = node.getChildren().iterator();
                    while (it.hasNext()) {
                        DisplayTreeNode child = (DisplayTreeNode) it.next();
                        child.put("checked", "true");
                    }
                }
            }
        } else if (action.equalsIgnoreCase("doClosePartner")) {
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            if (partnerRoot != null) {
                DisplayTreeNode node = partnerRoot.getChild(request.getParameter("id"));
                node.setOpen(false);
            }
        } else if (action.equalsIgnoreCase("doOriginalSettings")) {
            Principal principal = request.getUserPrincipal();
            IngridPersistencePrefs.setPref(principal.getName(), IngridPersistencePrefs.SEARCH_PARTNER, "");
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            Iterator it = partnerRoot.getChildren().iterator();
            while (it.hasNext()) {
                DisplayTreeNode partnerNode = (DisplayTreeNode) it.next();
                partnerNode.setOpen(false);
                Iterator it2 = partnerNode.getChildren().iterator();
                while (it2.hasNext()) {
                    DisplayTreeNode providerNode = (DisplayTreeNode) it2.next();
                    providerNode.remove("checked");
                }
            }
        } else {
            // Zur Suchanfrage hinzufuegen
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            String resultQuery = UtilsSearch.processSearchPartner("", partnerRoot, request);
            Principal principal = request.getUserPrincipal();
            IngridPersistencePrefs.setPref(principal.getName(), IngridPersistencePrefs.SEARCH_PARTNER, resultQuery);
        }
    }
}
