/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
package de.ingrid.portal.portlets.searchcatalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.velocity.context.Context;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import de.ingrid.codelists.CodeListService;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.CodeListServiceFactory;
import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.utils.PlugDescription;

/**
 * This portlet handles the fragment of the hierarchy browser in the
 * search/catalog section.
 * 
 * @author martin@wemove.com
 */
public class SearchCatalogHierarchyPortlet extends SearchCatalog {

    // VIEW TEMPLATES
    private static final String TEMPLATE_START = "/WEB-INF/templates/search_catalog/search_cat_hierarchy.vm";
    
    private static final String UPDATE_LEAF = "updateLeaf";
    
    //scroll top value
    private String scrollTop = "0";

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        if(resourceID.equals(UPDATE_LEAF)) {
            String nodeId = request.getParameter("id");
            if(nodeId != null) {
                if(nodeId.indexOf('-') > -1) {
                    String[] nodeIdSplit = nodeId.split("-");
                    nodeId = nodeIdSplit[nodeIdSplit.length-1];
                } 
                PortletSession session = request.getPortletSession();
                PageState ps = (PageState) session.getAttribute("portlet_state");
                DisplayTreeNode root = (DisplayTreeNode) ps.get("plugsRoot");
                if (root != null) {
                    IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                        request.getLocale()), request.getLocale());
    
                    String value = openNodeString(root, nodeId, request.getLocale().getLanguage(), messages, CodeListServiceFactory.instance());
                    if(value != null) {
                        response.setContentType( "application/json" );
                        response.getWriter().write(value);
                    }
                }
            }
        }
    }

    @Override
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());

        PortletPreferences prefs = request.getPreferences();
        String helpKey = prefs.getValue( "helpKey", "");
        context.put( "helpKey", helpKey );

        // add velocity utils class
        context.put("tool", new UtilsVelocity());
        //add scolling info
        context.put("scrollTop", scrollTop);
        // set positions in main tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_HIERARCHY);
        
        context.put("Codelists", CodeListServiceFactory.instance());
        // add request language, used to localize the map client
        context.put("languageCode", request.getLocale().getLanguage());

        setDefaultViewPage(TEMPLATE_START);

        // TODO remove page state in future, when separate portlets
        // use messages and render parameters instead !!!
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = initPageState(new PageState(this.getClass().getName()));
            session.setAttribute("portlet_state", ps);
        }
        context.put("ps", ps);

        // define an REST URL to get the map data dynamically
        ResourceURL restUrl = response.createResourceURL();
        restUrl.setResourceID( UPDATE_LEAF );
        request.setAttribute( UPDATE_LEAF, restUrl.toString() );

        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        String paramOpenNodes = request.getParameter("openNodes");
        if (ps.get("plugsRoot") == null) {

            // set up ECS plug list for view

            // all iplugs
            PlugDescription[] allPlugs = IBUSInterfaceImpl.getInstance().getAllActiveIPlugs();

            // filter types
            String[] plugTypes = new String[] { Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS,
                    Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS };
            PlugDescription[] plugs = IPlugHelper.filterIPlugsByType(allPlugs, plugTypes);

            // filter corrupt ones
            plugs = IPlugHelper.filterCorruptECSIPlugs(plugs);

            // filter partners
            String partnerRestriction = PortalConfig.getInstance().getString(
                    PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
            if (partnerRestriction != null && partnerRestriction.length() > 0) {
                ArrayList filter = new ArrayList();
                filter.add(partnerRestriction);
                plugs = IPlugHelper.filterIPlugsByPartner(plugs, filter);
            }

            // sort
            plugs = IPlugHelper.sortPlugs(plugs, new IPlugHelperDscEcs.PlugComparatorECS());

            DisplayTreeNode plugsRoot = DisplayTreeFactory.getTreeFromECSIPlugs(plugs);

            int openNodesInitial = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER_LEVEL, 3);
            if (openNodesInitial > 0) {
                ArrayList<String> paramDefaultOpenNodes = new ArrayList<>();
                openNodesUntilHierarchyLevel(plugsRoot, plugsRoot, paramDefaultOpenNodes);
                if(paramOpenNodes == null) {
                    if(!paramDefaultOpenNodes.isEmpty()) {
                        context.put("openNodes", paramDefaultOpenNodes.toString());
                    }
                } else {
                    ArrayList<String> tmpOpenNodes = new ArrayList<String>(Arrays.asList(paramOpenNodes.split(",")));
                    plugsRoot = openNodesByParameter(plugsRoot, tmpOpenNodes);
                }
            } else {
                if(paramOpenNodes != null) {
                    ArrayList<String> tmpOpenNodes = new ArrayList<String>(Arrays.asList(paramOpenNodes.split(",")));
                    plugsRoot = openNodesByParameter(plugsRoot, tmpOpenNodes);
                }
            }
            ps.put("plugsRoot", plugsRoot);
        } else {
            if (paramOpenNodes != null) {
                ArrayList<String> tmpOpenNodes = new ArrayList<String>(Arrays.asList(paramOpenNodes.split(",")));
                DisplayTreeNode plugsRoot = (DisplayTreeNode) ps.get("plugsRoot");
                plugsRoot = openNodesByParameter(plugsRoot, tmpOpenNodes);
                ps.put("plugsRoot", plugsRoot);
            } else {
                ArrayList<String> paramDefaultOpenNodes = new ArrayList<>();
                DisplayTreeNode plugsRoot = (DisplayTreeNode) ps.get("plugsRoot");
                getOpenNodes(plugsRoot, paramDefaultOpenNodes);
                if(!paramDefaultOpenNodes.isEmpty()) {
                    context.put("openNodes", paramDefaultOpenNodes.toString());
                }
            }
        }
        response.setTitle(messages.getString("searchCatHierarchy.portlet.title"));
        
        super.doView(request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        scrollTop = request.getParameter("scrollTop");
        
        if(scrollTop == null){
            scrollTop = "0";
        }
        
        if (action == null) {
            action = "";
        }

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = initPageState(new PageState(this.getClass().getName()));
            session.setAttribute("portlet_state", ps);
        }

        String submittedReload = request.getParameter("submitReload");

        if (submittedReload != null) {
            initPageState(ps);

        } else if (action.equalsIgnoreCase("doOpenNode")) {
            DisplayTreeNode root = (DisplayTreeNode) ps.get("plugsRoot");
            if (root != null) {
                openNode(root, request.getParameter("nodeId"));
            }

        } else if (action.equalsIgnoreCase("doCloseNode")) {
            DisplayTreeNode root = (DisplayTreeNode) ps.get("plugsRoot");
            if (root != null) {
                DisplayTreeNode node = root.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(false);
                }
            }

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

            // only load if not loaded yet !
            if (!node.isLoading() && node.getChildren().isEmpty()) {
                node.setLoading(true);

                // handles all stuff
                DisplayTreeFactory.openECSNode(rootNode, node);

                node.setLoading(false);
            }
        }
    }

    private String openNodeString(DisplayTreeNode rootNode, String nodeId, String lang, IngridResourceBundle messages, CodeListService codeListService) {
        String value = "[]";
        DisplayTreeNode node = rootNode.getChild(nodeId);
        if (node != null) {
            node.setOpen(true);

            // only load if not loaded yet !
            if (!node.isLoading() && node.getChildren().isEmpty()) {
                node.setLoading(true);

                // handles all stuff
                value = DisplayTreeFactory.openECSNodeString(rootNode, node, lang, messages, codeListService);

                node.setLoading(false);
            } else {
                JSONArray values = new JSONArray();
                Iterator it = node.getChildren().iterator();
                while (it.hasNext()) {
                    DisplayTreeNode childNode = (DisplayTreeNode) it.next();
                    childNode.remove("parent");
                    childNode.remove("children");
                    values.put(new JSONObject(childNode));
                }
                return values.toString();
            }
            
        }
        return value;
    }
    /**
     * Open nodes if restrict partner and restrict partner level is set.
     * 
     * @param node
     *            to check for sub nodes
     * @param rootNode
     *            need for open sub nodes by node ID (must always be root node)
     * @param paramDefaultOpenNodes 
     */
    private void openNodesUntilHierarchyLevel(DisplayTreeNode node, DisplayTreeNode rootNode, ArrayList<String> paramDefaultOpenNodes) {
        ArrayList list = (ArrayList) node.getChildren();
        String rootNodeLevel = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER_LEVEL);

        if (rootNodeLevel != null)
            for (int i = 0; i < list.size(); i++) {
                if (node.get("level").toString().equals(rootNodeLevel)) {
                    break;
                }
                DisplayTreeNode subNode = (DisplayTreeNode) list.get(i);
                boolean closeAddress = PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_HIERARCHY_CATALOG_ADDRESS_CLOSE, false);
                if ((Boolean) subNode.get("expandable")) {
                    String plugType = (String) subNode.get("plugType");
                    if(plugType == null || !(closeAddress && plugType != null && plugType.equals( "dsc_ecs_address" ))){
                        openNode(rootNode, subNode.getId());
                        paramDefaultOpenNodes.add("'" + subNode.get("level") + "-" + subNode.getId() + "'");
                        openNodesUntilHierarchyLevel(subNode, rootNode, paramDefaultOpenNodes);
                    }
                }
            }
    }
    
    private DisplayTreeNode openNodesByParameter(DisplayTreeNode node, ArrayList<String> openNodes) {
        ArrayList list = (ArrayList) node.getChildren();
        for (int i = 0; i < list.size(); i++) {
            DisplayTreeNode subNode = (DisplayTreeNode) list.get(i);
            if(subNode.getChildren().size() > 0) {
                boolean isOpen = false;
                for (String openNode : openNodes) {
                    String[] nodeIdSplit = openNode.split("-");
                    if(subNode.getId().equals(nodeIdSplit[nodeIdSplit.length-1])) {
                        isOpen = true;
                        openNodes.remove(openNode);
                        break;
                    }
                }
                subNode.setOpen(isOpen);
                subNode = openNodesByParameter(subNode, openNodes);
            }
        }
        if(!openNodes.isEmpty()) {
            for (String openNode : openNodes) {
                String[] nodeIdSplit = openNode.split("-");
                openNode(node, nodeIdSplit[nodeIdSplit.length-1]);
            }
        }
        return node;
    }

    private void getOpenNodes(DisplayTreeNode node, ArrayList<String> openNodes) {
        ArrayList list = (ArrayList) node.getChildren();
        for (int i = 0; i < list.size(); i++) {
            DisplayTreeNode subNode = (DisplayTreeNode) list.get(i);
            if(subNode.isOpen()) {
                openNodes.add("'" + subNode.get("level") + "-" + subNode.getId() + "'");
                getOpenNodes(subNode, openNodes);
            }
        }
    }
}
