/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchcatalog;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchState;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * This portlet handles the fragment of the hirarchy browser in the search/catalog section.
 *
 * @author martin@wemove.com, michael.benz@wemove.com
 */
public class SearchCatalogThesaurusPortlet extends SearchCatalog {

    // VIEW TEMPLATES
    private final static String TEMPLATE_START = "/WEB-INF/templates/search_catalog/search_cat_thesaurus.vm";

    // TEMPORARY Thesaurus settings. Currently we have can't receive the top terms from the SNS
    // TODO Remove this and use the getHierarchy() function when it's available
    private final static String THESAURUS_ASSOCIATION = "narrowerTermMember";
    private final static String THESAURUS_START_TOPIC = "(Hydrosphäre - Wasser und Gewässer)";
    private final static String THESAURUS_START_TOPIC_ID = "uba_thes_49252";
//    private final static String THESAURUS_ASSOCIATION = "widerTermMember";
//    private final static String THESAURUS_START_TOPIC = "Horizontalfilterbrunnen";
//    private final static String THESAURUS_START_TOPIC_ID = "uba_thes_12891";

    /* Defines which search state to use */
    private final static String SEARCH_STATE_TOPIC = Settings.MSG_TOPIC_SEARCH;
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // add velocity utils class
        context.put("tool", new UtilsVelocity());

        // set positions in main tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_THESAURUS);

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

        if (ps.get("thesRoot") == null) {
            // 1. Get initial data from the SNS IPlug (Top Terms)
        	// 2. sort Terms (needed?)
            // 3. Create Tree from the initial Terms and put them into the Page State 
        	
        	// Create a test term tree from a search query 
        	IngridQuery query = null;
            try {
                query = QueryStringParser.parse("Trinkwasser");
            } catch (ParseException e) {}

//			DisplayTreeNode thesRoot = DisplayTreeFactory.getTreeFromQueryTerms(query);
            DisplayTreeNode thesRoot = new DisplayTreeNode("root", "root", true);
            thesRoot.setType(DisplayTreeNode.ROOT);

            session.setAttribute("thesRoot", thesRoot);
			initTree(thesRoot);

        	// ------ END TEST CODE --------
        	
            ps.put("thesRoot", thesRoot);
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
    	
        // Build the URL to the current psml
        RequestContext requestContext = (RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV);                	
    	PortalURL portalURL = requestContext.getPortalURL(); 
    	String redirectPage = portalURL.getBasePath() + portalURL.getPath(); 

    	String submittedReload = request.getParameter("submitReload");

        if (submittedReload != null) {
            initPageState(ps);
        	actionResponse.sendRedirect(redirectPage + "?action=reload");

        } else if (action.equalsIgnoreCase("doOpenNode")) {
        	DisplayTreeNode root = (DisplayTreeNode) session.getAttribute("thesRoot");
            if (root != null) {
                DisplayTreeNode node = root.getChild(request.getParameter("nodeId"));
            	openNode(node);

                // NOTICE: also Action is encoded as Param + further stuff (so bookmarking or back button works)
                String urlParams = SearchState.getURLParamsMainSearch(request, SEARCH_STATE_TOPIC);
            	actionResponse.sendRedirect(redirectPage + urlParams);
            }

        } else if (action.equalsIgnoreCase("doCloseNode")) {
        	DisplayTreeNode root = (DisplayTreeNode) ps.get("thesRoot");
            if (root != null) {
                DisplayTreeNode node = root.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(false);

                    // NOTICE: also Action is encoded as Param + further stuff (so bookmarking or back button works)
                    String urlParams = SearchState.getURLParamsMainSearch(request, SEARCH_STATE_TOPIC);
                	actionResponse.sendRedirect(redirectPage + urlParams);
                }
            }

        } else if (action.equalsIgnoreCase("doSearch")) {
        	DisplayTreeNode root = (DisplayTreeNode) ps.get("thesRoot");
            if (root != null) {
                DisplayTreeNode node = root.getChild(request.getParameter("nodeId"));
                if (node != null) {
                	// TODO: 1. Gather the search parameters
                	// TODO: 1.1 Modify the URL and redirect to self
                	// ----- 2-4 are executed in the Result Portlet (SearchCatalogThesaurusResultPortlet) -----
                	// TODO: 2. Execute the search
                	// TODO: 3. Post-process search result
                	// TODO: 4. Display -> velocity template

                    // adapt SearchState (base for generating URL params for render request FOR BOOKMARKING AND BACK BUTTON)
                    // query string
                    SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, node.getName(), SEARCH_STATE_TOPIC);
                    // ALSO GROUPING, so we guarantee this is part of Request (has highest priority in Query Preprocessor)
                    SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, Settings.PARAMV_GROUPING_PLUG_ID, SEARCH_STATE_TOPIC);

                    // NOTICE: also Action is encoded as Param ! serves as indicator for new search in result portlet
                    String urlParams = SearchState.getURLParamsMainSearch(request, SEARCH_STATE_TOPIC);

                	actionResponse.sendRedirect(redirectPage + urlParams);
                }
            }

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            // changed main or sub tab
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }

    private PageState initPageState(PageState ps) {
    	ps.put("thesRoot", null);
        return ps;
    }


    private void initTree(DisplayTreeNode rootNode) {
    	// TODO: Check if rootNode != null, etc.
    	DisplayTreeNode node = rootNode;
        rootNode.put("level", new Integer(0));

    	// TODO: Remove Test code if we get the full hierarchy from the backend (SNS) 
    	// ---------- BEGIN Test Code ------------------
    	node.put("topicID", THESAURUS_START_TOPIC_ID);
        node.put("level", new Integer(1));
        node.put("expandable", new Boolean(true));
        node.setName(THESAURUS_START_TOPIC);
        // ---------- END Test Code --------------------

        if (node.getChildren().size() == 0) {
            IngridHit[] hits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(THESAURUS_START_TOPIC_ID);
            if (hits != null && hits.length > 0) {
                for (int i=0; i<hits.length; i++) {
                    Topic hit = (Topic) hits[i];
                    String assoc = hit.getTopicAssoc();
                    if (assoc == null)
                    	assoc = THESAURUS_ASSOCIATION;
                    
                    if (assoc.indexOf(THESAURUS_ASSOCIATION) != -1 && !hit.getTopicName().equalsIgnoreCase(node.getName())) {
                        DisplayTreeNode snsNode = new DisplayTreeNode(node.getId() + i, hit.getTopicName(), false);

                        // Check if the snsNode has displayable children and can be expanded
                        IngridHit[] nodeHits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(hit.getTopicID());
                        boolean expandable = false;
                        for (int j = 0; j < nodeHits.length; j++) {
                            Topic subHit = (Topic) nodeHits[j];
                        	String subAssoc = subHit.getTopicAssoc();
                            if (subAssoc == null)
                            	subAssoc = THESAURUS_ASSOCIATION;

                            if (subAssoc.indexOf(THESAURUS_ASSOCIATION) != -1)
                        		expandable = true;
                        }
                        snsNode.put("expandable", new Boolean(expandable));    

                        snsNode.setType(DisplayTreeNode.SNS_TERM);
                        snsNode.setParent(node);
                        snsNode.put("topicID", hit.getTopicID());
                        snsNode.put("topic", hit);
                        snsNode.put("level", new Integer(1));
                        node.addChild(snsNode);
                    }
                }
            } else {
                // TODO remove node from display tree
            }
        }
    }

    
    private void openNode(DisplayTreeNode node) {
        node.setOpen(true);
        String topicId = (String) node.get("topicID");

        if (node != null && node.getChildren().size() == 0 && !node.isLoading()) {
            node.setLoading(true);

            int nodeLevel = ((Integer) node.get("level")).intValue();
            IngridHit[] hits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(topicId);
            if (hits != null && hits.length > 0) {
                for (int i=0; i<hits.length; i++) {
                    Topic hit = (Topic) hits[i];
                    String assoc = hit.getTopicAssoc();
                    if (assoc == null)
                    	assoc = THESAURUS_ASSOCIATION;

                    if (assoc.indexOf(THESAURUS_ASSOCIATION) != -1 && !hit.getTopicName().equalsIgnoreCase(node.getName())) {
                    	DisplayTreeNode snsNode = new DisplayTreeNode(node.getId() + i, hit.getTopicName(), false);

                        // Check if the snsNode has displayable children and can be expanded
                        IngridHit[] nodeHits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(hit.getTopicID());
                        boolean expandable = false;
                        for (int j = 0; j < nodeHits.length; j++) {
                            Topic subHit = (Topic) nodeHits[j];
                        	String subAssoc = subHit.getTopicAssoc();
                            if (subAssoc == null)
                            	subAssoc = THESAURUS_ASSOCIATION;

                            if (subAssoc.indexOf(THESAURUS_ASSOCIATION) != -1)
                        		expandable = true;
                        }
                        snsNode.put("expandable", new Boolean(expandable));    
                    	
                    	snsNode.setType(DisplayTreeNode.SNS_TERM);
                        snsNode.setParent(node);
                        snsNode.put("topicID", hit.getTopicID());
                        snsNode.put("topic", hit);
                        snsNode.put("level", new Integer(nodeLevel+1));
                        node.addChild(snsNode);
                    }
                }
            } else {
                // TODO remove node from display tree
            }
            node.setLoading(false);
        }
    }
}