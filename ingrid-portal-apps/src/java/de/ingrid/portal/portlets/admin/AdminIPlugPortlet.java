/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.security.Permissions;
import java.security.Principal;
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsSecurity;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.utils.PlugDescription;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminIPlugPortlet extends GenericVelocityPortlet {

    private PermissionManager permissionManager;

    private RoleManager roleManager;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        permissionManager = (PermissionManager) getPortletContext().getAttribute(
                CommonPortletServices.CPS_PERMISSION_MANAGER);
        if (permissionManager == null) {
            throw new PortletException("Could not get instance of portal permission manager component");
        }

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
                request.getLocale()));
        context.put("MESSAGES", messages);

        PortletSession session = request.getPortletSession();
        DisplayTreeNode treeRoot = (DisplayTreeNode) session.getAttribute("treeRoot");
        if (treeRoot == null) {
            Principal authUserPrincipal = request.getUserPrincipal();
            treeRoot = getPartnerProviderIPlugs(authUserPrincipal);
            session.setAttribute("treeRoot", treeRoot);
        }
        context.put("tree", treeRoot);

        super.doView(request, response);
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
    private DisplayTreeNode getPartnerProviderIPlugs(Principal principal) {
        PlugDescription[] plugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);
        Permissions permissions = UtilsSecurity.getMergedPermissions(principal, permissionManager, roleManager);
        ArrayList partners = UtilsSecurity.getPartnersFromPermissions(permissions, false);
        ArrayList providers = UtilsSecurity.getProvidersFromPermissions(permissions, false);
        for (int i = 0; i < partners.size(); i++) {
            String partnerId = (String) partners.get(i);
            DisplayTreeNode partnerNode = new DisplayTreeNode(partnerId, UtilsDB.getPartnerFromKey(partnerId), false);
            for (int j = 0; j < providers.size(); j++) {
                String providerId = (String) providers.get(j);
                // check if the provider fits to the partner
                if (providerId.startsWith(Utils.normalizePartnerKey(partnerId, true).concat("_"))) {
                    DisplayTreeNode providerNode = new DisplayTreeNode(providerId, UtilsDB
                            .getProviderFromKey(providerId), false);
                    for (int k = 0; k < plugs.length; k++) {
                        PlugDescription plug = plugs[k];
                        String[] plugProviders = plug.getProviders();
                        DisplayTreeNode plugNode = null;
                        for (int l = 0; l < plugProviders.length; l++) {
                            if (plugProviders[l].equalsIgnoreCase(providerId)) {
                                plugNode = new DisplayTreeNode(plug.getPlugId(), plug.getDataSourceName(), false);
                                plugNode.put("iplug", plug);
                                plugNode.setType(DisplayTreeNode.GENERIC);
                                plugNode.setParent(providerNode);
                                providerNode.addChild(plugNode);
                                break;
                            }
                        }
                    }
                    if (providerNode.getChildren().size() == 0) {
                        DisplayTreeNode messageNode = new DisplayTreeNode("message", "Keine iPlugs gefunden", false);
                        messageNode.setType(DisplayTreeNode.MESSAGE_NODE);
                        providerNode.addChild(messageNode);
                    }
                    providerNode.setType(DisplayTreeNode.GENERIC);
                    providerNode.setParent(partnerNode);
                    partnerNode.addChild(providerNode);
                }
            }
            if (partnerNode.getChildren().size() == 0) {
                DisplayTreeNode messageNode = new DisplayTreeNode("message", "Keine Provider gefunden.", false);
                messageNode.setType(DisplayTreeNode.MESSAGE_NODE);
                partnerNode.addChild(messageNode);
            }
            partnerNode.setType(DisplayTreeNode.GENERIC);
            partnerNode.setParent(root);
            root.addChild(partnerNode);
        }

        return root;
    }

}
