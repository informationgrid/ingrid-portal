/*
 * Copyright (c) 20067 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchcatalog;

import java.io.IOException;
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.utils.PlugDescription;

/**
 * This portlet handles the fragment of the hirarchy browser in the search/catalog section.
 *
 * @author martin@wemove.com
 */
public class SearchCatalogHierarchyPortlet extends SearchCatalog {

    // VIEW TEMPLATES

    private final static String TEMPLATE_START = "/WEB-INF/templates/search_catalog/search_cat_hierarchy.vm";

    // PARAMETER VALUES

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // add velocity utils class
        context.put("tool", new UtilsVelocity());

        // set positions in main tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_HIERARCHY);

        setDefaultViewPage(TEMPLATE_START);

        // TODO remove page state in future, when separate portlets
        // use messages and render parameters instead !!!
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }
        context.put("ps", ps);

        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        if (ps.get("plugsRoot") == null) {

        	// set up ECS plug list for view
        	
        	// all iplugs
            PlugDescription[] allPlugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();
            
            // filter types
            String[] plugTypes = new String[]{Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS};
            PlugDescription[] plugs = IPlugHelper.filterIPlugsByType(allPlugs, plugTypes);

        	// filter partners
            String partnerRestriction = PortalConfig.getInstance().getString(
                    PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
            if (partnerRestriction != null && partnerRestriction.length() > 0) {
                ArrayList filter = new ArrayList();
                filter.add(partnerRestriction);
                plugs = IPlugHelper.filterIPlugsByPartner(plugs, filter);
            }

            // sort
            plugs = IPlugHelper.sortPlugs(plugs, new IPlugHelper.PlugComparatorECS());

            DisplayTreeNode plugsRoot = DisplayTreeFactory.getTreeFromECSIPlugs(plugs);
            ps.put("plugsRoot", plugsRoot);
        }
        
        super.doView(request, response);
    }

    /**
     * NOTICE: on actions in same page we redirect to ourself with url param determining the view
     * template. If no view template is passed per URL param, the start template is rendered !
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        String submittedReload = request.getParameter("submitReload");

        if (submittedReload != null) {
            initPageState(ps);

        } else if (action.equalsIgnoreCase("doOpenNode")) {
        	DisplayTreeNode root = (DisplayTreeNode) ps.get("plugsRoot");
            if (root != null) {
                openNode(root, request.getParameter("nodeId"));
                ps.put("plugsRoot", root);
            }

        } else if (action.equalsIgnoreCase("doCloseNode")) {
        	DisplayTreeNode root = (DisplayTreeNode) ps.get("plugsRoot");
            if (root != null) {
                DisplayTreeNode node = root.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(false);
                }
            }

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            // changed main or sub tab
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }

    private PageState initPageState(PageState ps) {
        ps.put("plugsRoot", null);
        return ps;
    }
    
    private void openNode(DisplayTreeNode rootNode, String nodeId) {
        DisplayTreeNode node = rootNode.getChild(nodeId);
        if (node != null) {
            node.setOpen(true);
/*
            if (node.getType() == DisplayTreeNode.SEARCH_TERM && node.getChildren().size() == 0
                    && !node.isLoading()) {
                node.setLoading(true);
                IngridHit[] hits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(node.getName(), "/thesa");
                if (hits != null && hits.length > 0) {
                    for (int i=0; i<hits.length; i++) {
                        Topic hit = (Topic) hits[i];
                        if (!hit.getTopicName().equalsIgnoreCase(node.getName())) {
                            DisplayTreeNode snsNode = new DisplayTreeNode(node.getId() + i, hit.getTopicName(), false);
                            snsNode.setType(DisplayTreeNode.SNS_TERM);
                            snsNode.setParent(node);
                            snsNode.put("topicID", hit.getTopicID());
                            snsNode.put("topic", hit);
                            node.addChild(snsNode);
                        }
                    }
                } else {
                    // TODO remove node from display tree
                }
                node.setLoading(false);
            }
*/
        }
    }
}