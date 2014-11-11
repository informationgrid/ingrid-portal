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
package de.ingrid.portal.portlets.searchcatalog;

import java.io.IOException;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.utils.IngridHit;

/**
 * This portlet handles the fragment of the hirarchy browser in the search/catalog section.
 *
 * @author martin@wemove.com, michael.benz@wemove.com
 */
public class SearchCatalogThesaurusPortlet extends SearchCatalog {

    private final static Logger log = LoggerFactory.getLogger(SearchCatalogThesaurusPortlet.class);

    // VIEW TEMPLATES
    private final static String TEMPLATE_START = "/WEB-INF/templates/search_catalog/search_cat_thesaurus.vm";
    
//    private final static String THESAURUS_ASSOCIATION = "narrowerTermMember";
//    private final static String THESAURUS_START_TOPIC_ID = "uba_thes_40282";
	private final static String THESAURUS_START_TOPIC_ID = "toplevel";
	// Only show 'german' topics
	private final static String SNS_TOPTERM = "topTermType";
    private final static String SNS_NODELABEL = "nodeLabelType";
    private final static String SNS_DESCRIPTOR = "descriptorType";
    private final static int SNS_TOPTERM_TYPE = 0;
    private final static int SNS_NODELABEL_TYPE = 1;
    private final static int SNS_DESCRIPTOR_TYPE = 2;
    private final static int NODE_ERROR_TYPE = -1;
    
	/* Defines which search state to use */
    private final static String SEARCH_STATE_TOPIC = Settings.MSG_TOPIC_SEARCH;
    //store the scrollposition of the browser
    private String scrollTop = "0";
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {

    	IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                 request.getLocale()));
    	
		Context context = getContext(request);
		if(request.getParameter("scrollTop") != null)
			scrollTop = request.getParameter("scrollTop");
        // add velocity utils class
        context.put("tool", new UtilsVelocity());
        context.put("scrollTop", scrollTop);
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

            DisplayTreeNode thesRoot = new DisplayTreeNode("root", "root", true);
            thesRoot.setType(DisplayTreeNode.ROOT);

            session.setAttribute("thesRoot", thesRoot);
			initTree(thesRoot, request.getLocale());

            ps.put("thesRoot", thesRoot);
        }
        
        // open tree up to specified node and search for results
        if (request.getParameter("nodeId") != null) {
            DisplayTreeNode root = (DisplayTreeNode) session.getAttribute("thesRoot");
            if (root != null) {
                // close first hierarchy to make sure only one branch is open
            	// this is a fix to prevent too large pages as observed in Portal HEAP
            	for (Object child : root.getChildren()) {
                	((DisplayTreeNode)child).setOpen(false);
                }
                // open up all parent nodes
                String[] parentNodes = ((String)request.getParameter("parentNodes")).split(",");
                for (String nodeId : parentNodes) {
                    openNode(root, root.getChildByField(nodeId, "topicID"), request.getLocale());
                }
                
                // do open node here since command has to be delivered as a normal link, otherwise
                // search engines won't be able to open the node during a later crawl and so
                // they wouldn't be able to get the underlying links
                DisplayTreeNode node = root.getChildByField(request.getParameter("nodeId"), "topicID");
                if (request.getParameter("action").equals("doOpenNode")) {
                    try {
                        openNode(root, node, request.getLocale());
                    } catch (Exception e) {
                        node.setLoading(false);
                        if (log.isWarnEnabled())
                            log.warn(e.toString());
                    }
                } else if (action.equalsIgnoreCase("doCloseNode")) {
                    if (node != null) {
                        node.setOpen(false);
                    }
                }
            }
        }

        // Put the current query term in the search state so we can highlight it in the tree
        String queryThesaurusTerm = request.getParameter(Settings.PARAM_QUERY_STRING);
        ps.put("currentQueryTerm", queryThesaurusTerm);
        response.setTitle(messages.getString("searchCatThesaurus.portlet.title"));
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
        	actionResponse.sendRedirect(actionResponse.encodeURL(redirectPage + "?action=reload"));
        /*} else if (action.equalsIgnoreCase("doSearch")) {
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
        */
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


    private void initTree(DisplayTreeNode rootNode, Locale language) {
        rootNode.put("level", new Integer(0));

        // Get the initial list of TopTerm Topics
		IngridHit[] results = SNSSimilarTermsInterfaceImpl.getInstance().
			getHierarchy(THESAURUS_START_TOPIC_ID, "false", "narrowerTermAssoc", "1", "down", language);

		// If we don't get any results (or multiple results) there's a problem with the SNS service
		if (results == null || results.length != 1) {
			return;
		}

		// Here we extract the TopTerm Topics from the results. The rootHit represents the 'toplevel' node 
		Topic rootHit = (Topic) results[0];
		// getSuccessors() returns the nodes at level 1 (TopTerms in this case)
		Set<Topic> hits = rootHit.getSuccessors();

		// Sort the List alphabetically. We put it in a set to filter duplicates
		TreeSet hitSet = new TreeSet(new SNSTopicComparator());
		hitSet.addAll(hits);

        // Analyze each topic and add it to the rootNode if it's a TopTerm
        for (Iterator it = hitSet.iterator(); it.hasNext();) {
            Topic hit = (Topic) it.next();

            // The hitSummary is a String with the nodeType at the end
            String nodeType = hit.getSummary();
            String nodeLanguage = hit.getLanguage();

            if (nodeLanguage == null) {
            	nodeLanguage = language.getLanguage();
            }
            
            // Only add TopTerms as entry nodes (initial nodes at level 1) if topterm && matches queried language !
            if (nodeType.indexOf(SNS_TOPTERM) != -1 && nodeLanguage.equalsIgnoreCase(language.getLanguage()))
            {
            	// Create new TreeNode
            	// TODO Do we set the id right? Are multiple nodes with the same id allowed?
            	DisplayTreeNode snsNode = new DisplayTreeNode(new Integer(rootNode.getNextId()).toString(), hit.getTopicName(), false);

            	// -- Check if the Topic has subTopics (is expandable) --
        		// We need a search with depth = 2 for this
            	Set<Topic> succ = hit.getSuccessors();
        		if (succ.size() == 0)
        			snsNode.put("expandable", new Boolean(false));
        		else
        			snsNode.put("expandable", new Boolean(true));
        		// --

        		// We checked this earlier. Set the node type to make them distinguishable
        		snsNode.setType(SNS_TOPTERM_TYPE);

        		// Add general information
                snsNode.setParent(rootNode);
                snsNode.put("topicID", hit.getTopicID());
                snsNode.put("topic", hit);
                snsNode.put("level", new Integer(1));

                // Attach the current snsNode to the rootNode 
                rootNode.addChild(snsNode);
            }
        }
    }

    
    private void openNode(DisplayTreeNode rootNode, DisplayTreeNode nodeToOpen, Locale language) {
		if (nodeToOpen == null)
			return;

		nodeToOpen.setOpen(true);

		// Entry checks
		if (nodeToOpen.getChildren().size() != 0 || nodeToOpen.isLoading()) {
    		return;
    	}

		nodeToOpen.setLoading(true);

        // Get the hierarchy of subNodes starting from node
        IngridHit[] results = SNSSimilarTermsInterfaceImpl.getInstance().
        	getHierarchy((String) nodeToOpen.get("topicID"), "false", "narrowerTermAssoc", "2", "down", language);

		// If we don't get any results (or multiple results) there's a problem with the SNS service
        if (results.length != 1) {

        	nodeToOpen.setLoading(false);
        	nodeToOpen.addChild(createErrorNode(nodeToOpen));
    		return;
        }

        Topic rootHit = (Topic) results[0];
		if (rootHit == null)
		{
			nodeToOpen.setLoading(false);
			nodeToOpen.addChild(createErrorNode(nodeToOpen));
    		return;			
		}

		Set<Topic> hits = rootHit.getSuccessors();

        if (hits == null || hits.size() == 0) {
        	nodeToOpen.setLoading(false);
        	nodeToOpen.addChild(createErrorNode(nodeToOpen));
            return;
        }

		// Sort the List alphabetically. We put it in a set to filter duplicates
		TreeSet hitSet = new TreeSet(new SNSTopicComparator());
		hitSet.addAll(hits);

        // Analyze each topic and add it to the rootNode if it's a TopTerm
        for (Iterator it = hitSet.iterator(); it.hasNext();) {
        	Topic hit = (Topic) it.next();

    		// filter entries with duplicate topic IDs in path to break circular dependencies
        	boolean isDuplicate = false;
			if (hit.getTopicID().equals(nodeToOpen.get("topicID"))) {
				log.warn("Duplicat SNS node detected: " + hit.getTopicName() + " (TOPICID: +" + hit.getTopicID() + "). This node will be ignored.");
				isDuplicate = true;
			} else {
	        	for (String child : nodeToOpen.getAllParents()) {
	    			if (hit.getTopicID().equals(child)) {
	    				log.warn("Duplicat SNS node detected: " + hit.getTopicName() + " (TOPICID: +" + hit.getTopicID() + "). This node will be ignored.");
	    				isDuplicate = true;
	    				break;
	    			}
	    		}
			}
        	if (isDuplicate) {
        		continue;
        	}
        	
        	
        	String nodeLanguage = hit.getLanguage();
            if (nodeLanguage == null) {
            	nodeLanguage = language.getLanguage();
            }

            // Only handle queried language topics
        	if (!nodeLanguage.equalsIgnoreCase(language.getLanguage())) {
        		continue;
        	}

            // The hitSummary is a String with the nodeType at the end        	
        	String nodeType = hit.getSummary();
        	if (nodeType == null) {
        		nodeType = "";
        	}

        	DisplayTreeNode snsNode = new DisplayTreeNode(new Integer(rootNode.getNextId()).toString(), hit.getTopicName(), false);
        	
        	// -- Check if the Topic has subTopics (is expandable) --
    		Set<Topic> succ = hit.getSuccessors();
        	if (nodeType == null) {
        		succ = new HashSet<Topic>();
        	}
    		
    		// Mark the node as 'not expandable' at first
    		snsNode.put("expandable", new Boolean(false));
   			// Check if the successor list contains a german topic
	        for (Iterator listIt = succ.iterator(); listIt.hasNext();) {
	        	Topic topic = (Topic) listIt.next();
	        	String topicLanguage = topic.getLanguage();
	            if (topicLanguage == null) {
	            	topicLanguage = language.getLanguage();
	            }

	        	if (topicLanguage.equalsIgnoreCase(language.getLanguage())) {
	        		snsNode.put("expandable", new Boolean(true));
	        	}
    		}
    		// --

    		// -- Check the nodeType and set the corresponding Type of the SNSNode -- 
    		if (nodeType.indexOf(SNS_TOPTERM) != -1) 
    			snsNode.setType(SNS_TOPTERM_TYPE);
    		else if (nodeType.indexOf(SNS_NODELABEL) != -1) 
    			snsNode.setType(SNS_NODELABEL_TYPE);
    		else if (nodeType.indexOf(SNS_DESCRIPTOR) != -1) 
    			snsNode.setType(SNS_DESCRIPTOR_TYPE);
    		// --

    		// -- Set general Node Information --
    		snsNode.setParent(nodeToOpen);
    		snsNode.put("topicID", hit.getTopicID());
    		snsNode.put("topic", hit);
            int nodeLevel = ((Integer) nodeToOpen.get("level")).intValue();
    		snsNode.put("level", new Integer(nodeLevel+1));
    		// --
    		

    		nodeToOpen.addChild(snsNode);
        }
        nodeToOpen.setLoading(false);
    }

    private static DisplayTreeNode createErrorNode(DisplayTreeNode node) {
    	DisplayTreeNode errorNode = new DisplayTreeNode("errorNode", "Error", false);
        int nodeLevel = ((Integer) node.get("level")).intValue();

        errorNode.put("expandable", new Boolean(false));
		errorNode.setType(NODE_ERROR_TYPE);
		errorNode.setParent(node);
		errorNode.put("topicID", "Error");
		errorNode.put("topic", "Error");
		errorNode.put("level", new Integer(nodeLevel + 1));
		
		return errorNode;
    }


    static public class SNSTopicComparator implements Comparator {
    	public final int compare(Object a, Object b) {
            try {
            	Topic topicA = (Topic) a;
            	Topic topicB = (Topic) b;

            	// Get the collator for the German Locale 
            	Collator gerCollator = Collator.getInstance(Locale.GERMAN);
            	return gerCollator.compare(topicA.getTopicName(), topicB.getTopicName());
            } catch (Exception e) {
                return 0;
            }
        }
    }

}
