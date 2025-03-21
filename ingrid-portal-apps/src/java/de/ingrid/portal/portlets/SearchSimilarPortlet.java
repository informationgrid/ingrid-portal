/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsQueryString;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchState;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.IngridQuery;

/**
 * This portlet handles the "Similar Terms" fragment of the result page
 *
 * @author martin@wemove.com
 */
public class SearchSimilarPortlet extends AbstractVelocityMessagingPortlet {

    private static final Logger log = LoggerFactory.getLogger(SearchSimilarPortlet.class);

    private static final String TEMPLATE_RESULT = "/WEB-INF/templates/search_similar.vm";

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SEARCH);

        super.init(config);
    }

    @Override
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        // ----------------------------------
        // set initial view template
        // ----------------------------------

        // if no query display "nothing"
        IngridQuery query = (IngridQuery) SearchState.getSearchStateObject(request, Settings.MSG_QUERY);
        if (query == null || query.getTerms().length == 0) {
            setDefaultViewPage(TEMPLATE_RESULT);
            context.put("closeSimilarSection", true);
            super.doView(request, response);
            return;
        }

        // if query assume we have results
        setDefaultViewPage(TEMPLATE_RESULT);

        // use messages and render parameters instead !!!
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = initPageState(new PageState(this.getClass().getName()));
            session.setAttribute("portlet_state", ps);
        }

        // indicates whether a new query was performed !
        String queryState = (String) SearchState.getSearchStateObject(request, Settings.MSG_QUERY_EXECUTION_TYPE);
        if (queryState != null && queryState.equals(Settings.MSGV_NEW_QUERY)) {
            if((boolean)ps.get("isSimilarOpen")) {
                DisplayTreeNode similarRoot = null;
                ps.putBoolean("isSimilarOpen", true);
                IngridQuery queryRoot = (IngridQuery) SearchState.getSearchStateObject(request, Settings.MSG_QUERY);
                similarRoot = DisplayTreeFactory.getTreeFromQueryTerms(queryRoot);
                session.setAttribute("similarRoot", similarRoot);
                for (int i = 0; i < similarRoot.getChildren().size(); i++) {
                    DisplayTreeNode node = (DisplayTreeNode) similarRoot.getChildren().get(i);
                    openNode(similarRoot, node.getId(), request
                            .getLocale(), true);
                    if(i > 0){
                        node.setOpen(false);
                    }
                }
                ps.put("similarRoot", similarRoot);
                SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
            } else {
                ps.putBoolean("isSimilarOpen", false);
                ps.put("similarRoot", null);
                session.removeAttribute("similarRoot");   
            }
        }
        context.put("ps", ps);

        context.put("enableSnsLogo", PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_ENABLE_SNS_LOGO, Boolean.TRUE));

        super.doView(request, response);
    }

    @Override
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

        try {
            DisplayTreeNode similarRoot = null;
            if (action.equalsIgnoreCase("doOpenSimilar")) {
                ps.putBoolean("isSimilarOpen", true);
                similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
                if (similarRoot == null) {
                    IngridQuery query = (IngridQuery) SearchState.getSearchStateObject(request, Settings.MSG_QUERY);
                    similarRoot = DisplayTreeFactory.getTreeFromQueryTerms(query);
                    session.setAttribute("similarRoot", similarRoot);
                    for (int i = 0; i < similarRoot.getChildren().size(); i++) {
                        DisplayTreeNode node = (DisplayTreeNode) similarRoot.getChildren().get(i);
                        openNode(similarRoot, node.getId(), request
                                .getLocale(), true);
                        if(i > 0){
                            node.setOpen(false);
                        }
                    }
                }
                ps.put("similarRoot", similarRoot);
            } else if (action.equalsIgnoreCase("doCloseSimilar")) {
                ps.putBoolean("isSimilarOpen", false);
                ps.put("similarRoot", null);
            } else if (action.equalsIgnoreCase("doOpenNode")) {
                similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
                if (similarRoot != null) {
                    openNode(similarRoot, request.getParameter("nodeId"), request.getLocale(), false);
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

                String queryStr = (String) receiveRenderMessage(request, Settings.PARAM_QUERY_STRING);

                String newQueryStr = queryStr;
                similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
                if (similarRoot != null) {
                    ArrayList queryTerms = (ArrayList) similarRoot.getChildren();
                    Iterator it = queryTerms.iterator();
                    while (it.hasNext()) {
                        DisplayTreeNode queryTerm = (DisplayTreeNode) it.next();
                        Iterator it2 = queryTerm.getChildren().iterator();
                        StringBuilder subQueryStr = null;
                        boolean hasSubTerms = false;
                        while (it2.hasNext()) {
                            DisplayTreeNode node = (DisplayTreeNode) it2.next();
                            if (request.getParameter("chk_" + node.getId()) != null) {
                                if (!hasSubTerms) {
                                    subQueryStr = new StringBuilder("(" + queryTerm.getName());
                                    hasSubTerms = true;
                                }
                                // check for phases, quote phrases
                                if (node.getName().indexOf(' ') > -1) {
                                    subQueryStr.append(" OR \"").append(node.getName()).append("\"");
                                } else {
                                    subQueryStr.append(" OR ").append(node.getName());
                                }
                            }
                            if (!it2.hasNext() && hasSubTerms) {
                                subQueryStr.append(")");
                            }
                        }
                        if (subQueryStr != null) {
                            newQueryStr = UtilsQueryString.replaceTerm(newQueryStr, queryTerm.getName(), subQueryStr
                                    .toString());
                        }
                    }
                    // republish the query
                    if (!queryStr.equalsIgnoreCase(newQueryStr)) {
                        publishRenderMessage(request, Settings.PARAM_QUERY_STRING, newQueryStr);
                    }
                }
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing SimilarTerms ACTION", ex);
            }
        }

        // indicate, that no query is necessary, we just have to handle similar terms
        SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_NO_QUERY);

        // redirect to our page wih parameters for bookmarking
        actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request)));
    }

    private PageState initPageState(PageState ps) {
        ps.putBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }

    private void openNode(DisplayTreeNode rootNode, String nodeId, Locale language, boolean loadData) {
        DisplayTreeNode node = rootNode.getChild(nodeId);
        node.setOpen(true);
        if (loadData && node.getType() == DisplayTreeNode.SEARCH_TERM && node.getChildren().isEmpty()
                && !node.isLoading()) {
            node.setLoading(true);
            IngridHit[] hits = SNSSimilarTermsInterfaceImpl.getInstance().getSimilarTerms(node.getName(), language);
            if (hits == null || hits.length == 0) {
                DisplayTreeNode snsNode = new DisplayTreeNode(node.getId() + 0, "similar.terms.not.available", false);
                snsNode.setType(DisplayTreeNode.MESSAGE_NODE);
                snsNode.setParent(node);
                node.addChild(snsNode);
            } else {
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
            node.setLoading(false);
        }
    }

}
