/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.IngridQuery;

/**
 * This portlet handles the "Similar Terms" fragment of the result page
 *
 * @author martin@wemove.com
 */
public class SearchSimilarPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(SearchSimilarPortlet.class);

    /** view templates */
    private final static String TEMPLATE_NO_QUERY = "/WEB-INF/templates/empty.vm";

    private final static String TEMPLATE_RESULT = "/WEB-INF/templates/search_similar.vm";

    //    private final static String TEMPLATE_NO_RESULT = "/WEB-INF/templates/empty.vm";

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SEARCH);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // ----------------------------------
        // set initial view template
        // ----------------------------------

        // if no query display "nothing"
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
        if (query == null) {
            setDefaultViewPage(TEMPLATE_NO_QUERY);
            super.doView(request, response);
            return;
        }

        // if query assume we have results
        setDefaultViewPage(TEMPLATE_RESULT);

        // TODO remove page state in future, when separate portlets
        // use messages and render parameters instead !!!
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        // indicates whether a new query was performed !
        Object newQuery = receiveRenderMessage(request, Settings.MSG_NEW_QUERY);
        if (newQuery != null) {
            ps.putBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
            session.removeAttribute("similarRoot");
        }

        context.put("ps", ps);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        DisplayTreeNode similarRoot = null;
        if (action.equalsIgnoreCase("doOpenSimilar")) {
            ps.putBoolean("isSimilarOpen", true);
            similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot == null) {
                IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
                similarRoot = DisplayTreeFactory.getTree(query);
                session.setAttribute("similarRoot", similarRoot);
            }
            ps.put("similarRoot", similarRoot);
        } else if (action.equalsIgnoreCase("doCloseSimilar")) {
            ps.putBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
        } else if (action.equalsIgnoreCase("doOpenNode")) {
            similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                node.setOpen(true);
                if (node != null && node.getType() == DisplayTreeNode.SEARCH_TERM && node.getChildren().size() == 0) {
                    IngridHit[] hits = SNSSimilarTermsInterfaceImpl.getInstance().getSimilarTerms(node.getName());
                    for (int i = 0; i < hits.length; i++) {
                        Topic hit = (Topic) hits[i];
                        if (!hit.getTopicName().equalsIgnoreCase(node.getName())) {
                            DisplayTreeNode snsNode = new DisplayTreeNode(node.getId() + i, hit.getTopicName(), false);
                            snsNode.setType(DisplayTreeNode.SNS_TERM);
                            snsNode.setParent(node);
                            node.addChild(snsNode);
                        }
                    }
                }
                ps.put("similarRoot", similarRoot);
            }
        } else if (action.equalsIgnoreCase("doCloseNode")) {
            similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(false);
                }
            }
        } else if (action.equalsIgnoreCase("doAddSimilar")) {

            String queryStr = (String) receiveRenderMessage(request, Settings.MSG_QUERY_STRING);

            String newQueryStr = queryStr.toLowerCase();
            similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot != null) {
                ArrayList queryTerms = similarRoot.getChildren();
                Iterator it = queryTerms.iterator();
                while (it.hasNext()) {
                    DisplayTreeNode queryTerm = (DisplayTreeNode) it.next();
                    Iterator it2 = queryTerm.getChildren().iterator();
                    StringBuffer subQueryStr = null;
                    boolean hasSubTerms = false;
                    while (it2.hasNext()) {
                        DisplayTreeNode node = (DisplayTreeNode) it2.next();
                        if (request.getParameter("chk_" + node.getId()) != null) {
                            if (!hasSubTerms) {
                                subQueryStr = new StringBuffer("(" + queryTerm.getName());
                                hasSubTerms = true;
                            }
                            subQueryStr.append(" OR \"").append(node.getName()).append("\"");
                        }
                        if (!it2.hasNext() && hasSubTerms) {
                            subQueryStr.append(")");
                        }
                    }
                    if (subQueryStr != null) {
                        newQueryStr = newQueryStr.replaceAll(queryTerm.getName(), subQueryStr.toString());
                    }
                }
                // republish the query
                if (!queryStr.toLowerCase().equals(newQueryStr)) {
                    publishRenderMessage(request, Settings.MSG_QUERY_STRING, newQueryStr);
                }
            }
        }

        // indicate, that no query is necessary, we just have to handle similar terms
        publishRenderMessage(request, Settings.MSG_NO_QUERY, Settings.MSG_VALUE_TRUE);

        // redirect to our page wih parameters for bookmarking
        actionResponse.sendRedirect(UtilsSearch.PAGE_SEARCH_RESULT + UtilsSearch.getURLParams(request));
    }

    private PageState initPageState(PageState ps) {
        ps.putBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }
}