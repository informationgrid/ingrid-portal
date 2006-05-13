/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.pluto.core.impl.PortletSessionImpl;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.forms.SearchExtEnvPlaceGeothesaurusForm;
import de.ingrid.portal.forms.SearchExtEnvTopicThesaurusForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * This portlet handles the fragment of the thesaurus input in the extended search.
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvTopicThesaurusPortlet extends SearchExtEnvTopic {

    // VIEW TEMPLATES

    private final static String TEMPLATE_START = "/WEB-INF/templates/search_extended/search_ext_env_topic_thesaurus.vm";

    private final static String TEMPLATE_RESULTS = "/WEB-INF/templates/search_extended/search_ext_env_topic_thesaurus_results.vm";

    private final static String TEMPLATE_BROWSE = "/WEB-INF/templates/search_extended/search_ext_env_topic_thesaurus_browse.vm";

    private final static String TEMPLATE_SYNONYM = "/WEB-INF/templates/search_extended/search_ext_env_topic_thesaurus_synonym.vm";

    // PARAMETER VALUES

    private final static String PARAMV_VIEW_RESULTS = "1";

    private final static String PARAMV_VIEW_BROWSE = "2";

    private final static String PARAMV_VIEW_SYNONYM = "3";
    
    private final static String TOPICS = "topics";
    

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_TOPIC);
        context.put(VAR_SUB_TAB, PARAMV_TAB_THESAURUS);

        setDefaultViewPage(TEMPLATE_START);

        SearchExtEnvTopicThesaurusForm f = (SearchExtEnvTopicThesaurusForm) Utils.getActionForm(request, SearchExtEnvTopicThesaurusForm.SESSION_KEY, SearchExtEnvTopicThesaurusForm.class);        
        context.put("actionForm", f);
        
        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        if (action.equals(PARAMV_VIEW_RESULTS)) {
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
            
            setDefaultViewPage(TEMPLATE_RESULTS);
        } else if (action.equals(PARAMV_VIEW_BROWSE)) {
            setDefaultViewPage(TEMPLATE_BROWSE);
        } else if (action.equals(PARAMV_VIEW_SYNONYM)) {
            setDefaultViewPage(TEMPLATE_SYNONYM);
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
        String submittedSearch = request.getParameter("submitSearch");
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }
        
        // TODO: implement functionality
        DisplayTreeNode similarRoot = null;
        if (submittedSearch != null) {
            // In Thesaurus suchen
            SearchExtEnvTopicThesaurusForm f = (SearchExtEnvTopicThesaurusForm) Utils.getActionForm(request, SearchExtEnvTopicThesaurusForm.SESSION_KEY, SearchExtEnvTopicThesaurusForm.class);        
            f.clearErrors();
            f.populate(request);
            if (f.validate()) {
                // get terms from querystring
                IngridQuery query = null;
                try {
                    query = QueryStringParser.parse(f.getInput(SearchExtEnvTopicThesaurusForm.FIELD_SEARCH_TERM));
                } catch (ParseException e) {
                }
                if (query == null) {
                    f.setError("", "searchExtEnvTopicThesaurus.error.no_term_found");
                } else {
                    ps.putBoolean("isSimilarOpen", true);
                    if (similarRoot == null) {
                        similarRoot = DisplayTreeFactory.getTreeFromQueryTerms(query);
                        if (similarRoot.getChildren().size() > 0) {
                            session.setAttribute("similarRoot", similarRoot);
                            if (similarRoot.getChildren().size() == 1) {
                                openNode(similarRoot, ((DisplayTreeNode) similarRoot.getChildren().get(0)).getId());
                            }
                        } else {
                            f.setError("", "searchExtEnvTopicThesaurus.error.no_term_found");
                        }
                    }
                }
                ps.put("similarRoot", similarRoot);
            }
            
            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            actionResponse.sendRedirect(PAGE_THESAURUS + urlViewParam);

        } else if (action.equalsIgnoreCase("doOpenNode")) {
            similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot != null) {
                openNode(similarRoot, request.getParameter("nodeId"));
                ps.put("similarRoot", similarRoot);
            }
            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            actionResponse.sendRedirect(PAGE_THESAURUS + urlViewParam);
        } else if (action.equalsIgnoreCase("doCloseNode")) {
            similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(false);
                }
            }
            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            actionResponse.sendRedirect(PAGE_THESAURUS + urlViewParam);
        } else if (submittedAddToQuery != null) {

            // Zur Suchanfrage hinzufuegen

            // redirect to same page with view param where we currently are (so we keep view !)
            String currView = getDefaultViewPage();
            String urlViewParam = "";
            if (currView.equals(TEMPLATE_RESULTS)) {
                urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            } else if (currView.equals(TEMPLATE_BROWSE)) {
                urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_BROWSE);
            } else if (currView.equals(TEMPLATE_SYNONYM)) {
                urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_SYNONYM);
            }
            actionResponse.sendRedirect(PAGE_THESAURUS + urlViewParam);

        } else if (action.equalsIgnoreCase("doBrowse")) {

            // SNS Deskriptor browsen
            String topicID = request.getParameter("topicID");
//            IngridHit[] similarHits = SNSSimilarTermsInterfaceImpl.getInstance().ge.getTopicSimilarLocationsFromTopic(topicId);
            

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_BROWSE);
            actionResponse.sendRedirect(PAGE_THESAURUS + urlViewParam);

        } else if (action.equalsIgnoreCase("doSynonym")) {

            // SNS Synonym browsen

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_SYNONYM);
            actionResponse.sendRedirect(PAGE_THESAURUS + urlViewParam);

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            // changed main or sub tab
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }

    private PageState initPageState(PageState ps) {
        ps.putBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }
    
    private void openNode(DisplayTreeNode rootNode, String nodeId) {
        DisplayTreeNode node = rootNode.getChild(nodeId);
        node.setOpen(true);
        if (node != null && node.getType() == DisplayTreeNode.SEARCH_TERM && node.getChildren().size() == 0
                && !node.isLoading()) {
            node.setLoading(true);
            IngridHit[] hits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(node.getName(), "/thesa");
            if (hits != null && hits.length > 1) {
                for (int i=0; i<hits.length; i++) {
                    Topic hit = (Topic) hits[i];
                    if (!hit.getTopicName().equalsIgnoreCase(node.getName())) {
                        DisplayTreeNode snsNode = new DisplayTreeNode(node.getId() + i, hit.getTopicName(), false);
                        snsNode.setType(DisplayTreeNode.SNS_TERM);
                        snsNode.setParent(node);
                        snsNode.put("topicID", hit.getTopicID());
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