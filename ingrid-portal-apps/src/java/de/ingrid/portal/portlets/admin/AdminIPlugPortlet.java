/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.RoleManager;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.utils.PlugDescription;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminIPlugPortlet extends GenericVelocityPortlet {

    private RoleManager roleManager;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager) {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }

    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        
        // set localized title for this page
        response.setTitle(messages.getString("admin.iplug.title"));

        PlugDescription[] plugs = IBUSInterfaceImpl.getInstance().getAllIPlugsWithoutTimeLimitation();
        
        PortletSession session = request.getPortletSession();
        DisplayTreeNode treeRoot = (DisplayTreeNode) session.getAttribute("treeRoot");
        if (treeRoot == null) {
            treeRoot = getPartnerProviderIPlugs(messages, plugs);
            session.setAttribute("treeRoot", treeRoot);
        }

        context.put("tree", treeRoot);

        // get iplug-se iplugs
        context.put("SEIplugs", getSEIPlugs(plugs));
        
        // get iplug-se indexer iplugs the user has permissions for
        context.put("SEIndexIplugs", getSEIndexIPlugs(plugs));

        // check, get for ibus
        context.put("ibusURL", PortalConfig.getInstance().getString("ibus.admin.url", ""));

        super.doView(request, response);
    }

    private ArrayList getSEIndexIPlugs(PlugDescription[] plugs) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < plugs.length; i++) {
            PlugDescription plug = plugs[i];
            // only include activated index engine iplugs
            if (plug.isActivate() && plug.getIPlugClass() != null &&
            		(plug.getIPlugClass().equals("de.ingrid.iplug.se.IndexIPlug") || plug.getIPlugClass().equals("de.ingrid.admin.object.IndexSePlug"))) {
                result.add(plug);
            }
        }
        return result;
    }

    
    private ArrayList getSEIPlugs(PlugDescription[] plugs) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < plugs.length; i++) {
            PlugDescription plug = plugs[i];
            // do not include search engine iplugs
            if (plug.isActivate() && plug.getIPlugClass() != null &&
            		plug.getIPlugClass().equals("de.ingrid.iplug.se.NutchSearcher")) {
                result.add(plug);
            }
        }
        return result;
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        PortletSession session = request.getPortletSession();

        if (action.equalsIgnoreCase("doOpenNode")) {
            DisplayTreeNode treeRoot = (DisplayTreeNode) session.getAttribute("treeRoot");
            if (treeRoot != null) {
                DisplayTreeNode node = treeRoot.getChild(request.getParameter("id"));
                if (node != null) {
                    node.setOpen(true);
                }
            }
        } else if (action.equalsIgnoreCase("doCloseNode")) {
            DisplayTreeNode treeRoot = (DisplayTreeNode) session.getAttribute("treeRoot");
            if (treeRoot != null) {
                DisplayTreeNode node = treeRoot.getChild(request.getParameter("id"));
                if (node != null) {
                    node.setOpen(false);
                }
            }
        }
    }

    /**
     * Builds a tree for the display of partner, provider, iPlugs
     * 
     * @param principal
     * @return
     */
    private DisplayTreeNode getPartnerProviderIPlugs(IngridResourceBundle messages, PlugDescription[] plugs) {
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);
        List<IngridPartner> partners = UtilsDB.getPartners();
        List<IngridProvider> providers = UtilsDB.getProviders();
        for (int i = 0; i < partners.size(); i++) {
            String partnerId = partners.get(i).getIdent();
            DisplayTreeNode partnerNode = new DisplayTreeNode(partnerId, UtilsDB.getPartnerFromKey(partnerId), false);
            for (int j = 0; j < providers.size(); j++) {
                String providerId = providers.get(j).getIdent();
                // check if the provider fits to the partner
                if (providerId.startsWith(Utils.normalizePartnerKey(partnerId, true).concat("_"))) {
                    DisplayTreeNode providerNode = new DisplayTreeNode(providerId, UtilsDB
                            .getProviderFromKey(providerId), false);
                    for (int k = 0; k < plugs.length; k++) {
                        PlugDescription plug = plugs[k];
                        // do not include search engine iplugs
                        if (plug != null && plug.getIPlugClass() != null && plug.isActivate() && !plug.getIPlugClass().equals("de.ingrid.iplug.se.NutchSearcher") && !plug.getIPlugClass().equals("de.ingrid.iplug.se.IndexIPlug") && !plug.getIPlugClass().equals("de.ingrid.admin.object.IndexSePlug")) {
                            String[] plugProviders = plug.getProviders();
                            DisplayTreeNode plugNode = null;
                            for (int l = 0; l < plugProviders.length; l++) {
                                // check for matching provider AND if the iplug
                                // is not already added
                                if (plugProviders[l].equalsIgnoreCase(providerId)
                                        && providerNode.getChild(plug.getPlugId()) == null) {
                                    plugNode = new DisplayTreeNode(plug.getPlugId(), plug.getDataSourceName(), false);
                                    if (plugNode.getId().endsWith("_addr")) {
                                        plugNode.setName(plugNode.getName().concat(" ").concat(messages.getString("admin.iplug.name.postfix.udk.address")));
                                    } else if (plugNode.getId().indexOf("udk-db") > -1 && !plugNode.getId().endsWith("_addr")) {
                                        plugNode.setName(plugNode.getName().concat(" ").concat(messages.getString("admin.iplug.name.postfix.udk.object")));
                                    }
                                    plugNode.put("iplug", plug);
                                    plugNode.setType(DisplayTreeNode.GENERIC);
                                    plugNode.setParent(providerNode);
                                    providerNode.addChild(plugNode);
                                    break;
                                }
                            }
                        }
                    }
                    if (providerNode.getChildren().size() != 0) {
                        providerNode.setType(DisplayTreeNode.GENERIC);
                        providerNode.setParent(partnerNode);
                        partnerNode.addChild(providerNode);
                    }
                }
            }
            if (partnerNode.getChildren().size() != 0) {
                partnerNode.setType(DisplayTreeNode.GENERIC);
                partnerNode.setParent(root);
                root.addChild(partnerNode);
            }
        }

        return root;
    }

}
