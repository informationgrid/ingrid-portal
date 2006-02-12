/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

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
import de.ingrid.portal.interfaces.impl.SNSInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
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
        Object newQuery = receiveRenderMessage(request, Settings.MSG_NEW_QUERY_FOR_SIMILAR);
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

        // no new query anymore, we remove messages, so Similar fragment is displayed as clicked
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY_FOR_SIMILAR);

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
            similarRoot = (DisplayTreeNode)session.getAttribute("similarRoot");
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
            similarRoot = (DisplayTreeNode)session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                node.setOpen(true);
                if (node != null && node.getType() == DisplayTreeNode.SEARCH_TERM) {
                    IngridHit[] hits = SNSInterfaceImpl.getInstance().getSimilarTerms(node.getName());
                    for (int i=0; i<hits.length; i++) {
                        Topic hit = (Topic) hits[i];
                        if (!hit.getTopicName().equalsIgnoreCase(node.getName())) {
                            DisplayTreeNode snsNode = new DisplayTreeNode(node.getId()+i, hit.getTopicName(), false);
                            snsNode.setType(DisplayTreeNode.SNS_TERM);
                            snsNode.setParent(node);
                            node.addChild(snsNode);
                        }
                    }
                }
                ps.put("similarRoot", similarRoot);
            }
        } else if (action.equalsIgnoreCase("doCloseNode")) {
            similarRoot = (DisplayTreeNode)session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(false);
                }
            }
        } else if (action.equalsIgnoreCase("doAddSimilar")) {
            // TODO add snsTerms to the query term with operator OR
            // the query object is still a mystery
            
/*            similarRoot = (DisplayTreeNode)session.getAttribute("similarRoot");
            if (similarRoot != null) {
                IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
                ArrayList nodes = similarRoot.getAllChildren();
                Iterator it = nodes.iterator();
                while (it.hasNext()) {
                    DisplayTreeNode node = (DisplayTreeNode) it.next();
                    if (request.getParameter("chk_" + node.getId()) != null) {
                        boolean isInQuery = false;
                        TermQuery[] terms = query.getTerms();
                        for (int i=0; i<terms.length; i++) {
                            if (terms[i].getTerm().equalsIgnoreCase(node.getName()) && terms[i].getType() == TermQuery.TERM) {
                                isInQuery = true;
                                break;
                            }
                        }
                        if (!isInQuery) {
                            DisplayTreeNode parent = node.getParent();
                            for (int i=0; i<terms.length; i++) {
                                if (terms[i].getTerm().equalsIgnoreCase(parent.getName()) && terms[i].getType() == TermQuery.TERM) {
                                    terms[i].addClause(new ClauseQuery(true, false));
                                    terms[i].addTerm(new TermQuery(true, false, node.getName()));
                                    break;
                                }
                            }
                        }
                        System.out.println(query.toString());
                    } else if (node.getDepth() == 2) {
                        node.setOpen(false);
                    }
                }
            }
            */
        }
    }

   private PageState initPageState(PageState ps) {
        ps.putBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }
}