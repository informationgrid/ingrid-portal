/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtEnvTopicThesaurusForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsQueryString;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.IngridQuery;
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
    
    private final static String ASSOCIATED_TOPICS = "associated_topics";
    
    private final static String CURRENT_TOPIC = "current_topic";
    
    
    

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
            context.put(CURRENT_TOPIC, request.getPortletSession().getAttribute(CURRENT_TOPIC));
            
            ArrayList narrowerTermAssoc = new ArrayList();
            ArrayList widerTermAssoc = new ArrayList();
            ArrayList synonymAssoc = new ArrayList();
            ArrayList relatedTermsAssoc = new ArrayList();
            
            Object obj = request.getPortletSession().getAttribute(ASSOCIATED_TOPICS);
            if (obj != null) {
                List hits = (List)obj;
                for (int i=0; i<hits.size(); i++) {
                    Topic t = (Topic)hits.get(i);
                    String assoc = t.getTopicAssoc();
                    if (assoc.indexOf("narrowerTermMember") != -1) {
                        narrowerTermAssoc.add(t);
                    } else if (assoc.indexOf("widerTermMember") != -1) {
                        widerTermAssoc.add(t);
                    } else if (assoc.indexOf("synonymMember") != -1) {
                        synonymAssoc.add(t);
                    } else if (assoc.indexOf("descriptorMember") != -1) {
                        relatedTermsAssoc.add(t);
                    }
                }
                context.put("list_size", new Integer(hits.size() + 1));
            }
            context.put("narrowerTermAssoc", narrowerTermAssoc);
            context.put("widerTermAssoc", widerTermAssoc);
            context.put("synonymAssoc", synonymAssoc);
            context.put("relatedTermsAssoc", relatedTermsAssoc);
            
            setDefaultViewPage(TEMPLATE_BROWSE);
        } else if (action.equals(PARAMV_VIEW_SYNONYM)) {
            context.put(CURRENT_TOPIC, request.getPortletSession().getAttribute(CURRENT_TOPIC));
            Object obj = request.getPortletSession().getAttribute(ASSOCIATED_TOPICS);
            if (obj != null) {
                context.put(ASSOCIATED_TOPICS, obj);
                context.put("list_size", new Integer(((List)obj).size() + 1));
            }
            setDefaultViewPage(TEMPLATE_SYNONYM);
        }

        context.put("enableSnsLogo", PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_ENABLE_SNS_LOGO, Boolean.TRUE));

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
                                openNode(similarRoot, ((DisplayTreeNode) similarRoot.getChildren().get(0)).getId(),
                                		request.getLocale(), false);
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
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS + urlViewParam));

        } else if (action.equalsIgnoreCase("doOpenNode")) {
            similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot != null) {
                openNode(similarRoot, request.getParameter("nodeId"), request.getLocale(), false);
                ps.put("similarRoot", similarRoot);
            }
            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS + urlViewParam));
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
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS + urlViewParam));
        } else if (submittedAddToQuery != null) {

            // Zur Suchanfrage hinzufuegen

            String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);

            if (request.getParameter("addFromDisplayTree") != null) {
                String subQueryStr = "";
                similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
                if (similarRoot != null) {
                    ArrayList queryTerms = similarRoot.getAllChildren();
                    Iterator it = queryTerms.iterator();
                    while (it.hasNext()) {
                        DisplayTreeNode node = (DisplayTreeNode) it.next();
                        if (request.getParameter("chk_" + node.getId()) != null) {
                            if (subQueryStr.length() > 0) {
                                subQueryStr = subQueryStr.concat(" OR ");
                            }
                            // check for phases, quote phrases
                            if (node.getName().indexOf(" ") > -1) {
                                subQueryStr = subQueryStr.concat("\"").concat(node.getName()).concat("\"");
                            } else {
                                subQueryStr = subQueryStr.concat(node.getName());
                            }
                        }
                    }
                    if (subQueryStr != null) {
                        subQueryStr = "(".concat(subQueryStr).concat(")");
                        PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, subQueryStr, UtilsQueryString.OP_AND));
                    }
                }            
            } else if (request.getParameter("addFromAssociatedTopics") != null) {
                String subQueryStr = "";
                int listSize = Integer.parseInt(request.getParameter("list_size"));
                for (int i=0; i<listSize; i++) {
                    String val = request.getParameter("chk_" + i);
                    if (val != null) {
                        if (subQueryStr.length() > 0) {
                            subQueryStr = subQueryStr.concat(" OR ");
                        }
                        // check for phases, quote phrases
                        if (val.indexOf(" ") > -1) {
                            subQueryStr = subQueryStr.concat("\"").concat(val).concat("\"");
                        } else {
                            subQueryStr = subQueryStr.concat(val);
                        }
                    }
                }
                if (subQueryStr != null) {
                    subQueryStr = "(".concat(subQueryStr).concat(")");
                    PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, subQueryStr, UtilsQueryString.OP_AND));
                }
            }
            
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
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS + urlViewParam));

        } else if (action.equalsIgnoreCase("doBrowseFromTree")) {

            // SNS Deskriptor browsen
            similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            String topicID = request.getParameter("topicID");
            Topic currentTopic = getTopicFromTree(similarRoot, topicID);
            request.getPortletSession().setAttribute(CURRENT_TOPIC, currentTopic, PortletSession.PORTLET_SCOPE);
            IngridHit[] assocTopics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(topicID, request.getLocale());
            request.getPortletSession().setAttribute(ASSOCIATED_TOPICS, Arrays.asList(assocTopics), PortletSession.PORTLET_SCOPE);

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_BROWSE);
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS + urlViewParam));

        } else if (action.equalsIgnoreCase("doBrowse")) {

            // SNS Deskriptor browsen
            String topicID = request.getParameter("topicID");
            String plugID = request.getParameter("plugID");
            String docID = request.getParameter("docID");
            
            Topic hit = new Topic();
            hit.setDocumentId(Integer.parseInt(docID));
            hit.setPlugId(plugID);
            hit.setTopicID(topicID);
            
            Topic currentTopic = (Topic)SNSSimilarTermsInterfaceImpl.getInstance().getDetailsTopic(hit, "/thesa", request.getLocale());
            request.getPortletSession().setAttribute(CURRENT_TOPIC, currentTopic, PortletSession.PORTLET_SCOPE);
            IngridHit[] assocTopics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(topicID, request.getLocale());
            request.getPortletSession().setAttribute(ASSOCIATED_TOPICS, Arrays.asList(assocTopics), PortletSession.PORTLET_SCOPE);

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_BROWSE);
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS + urlViewParam));
            
        } else if (action.equalsIgnoreCase("doSynonym")) {

            // SNS Synonym browsen
            String topicID = request.getParameter("topicID");
            String plugID = request.getParameter("plugID");
            String docID = request.getParameter("docID");
            
            Topic hit = new Topic();
            hit.setDocumentId(Integer.parseInt(docID));
            hit.setPlugId(plugID);
            hit.setTopicID(topicID);
            
            Topic currentTopic = (Topic)SNSSimilarTermsInterfaceImpl.getInstance().getDetailsTopic(hit, "/thesa", request.getLocale());
            request.getPortletSession().setAttribute(CURRENT_TOPIC, currentTopic, PortletSession.PORTLET_SCOPE);
            IngridHit[] assocTopics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromTopic(topicID, request.getLocale());
            ArrayList descriptors = new ArrayList();
            for (int i=0; i<assocTopics.length; i++) {
                Topic t = (Topic)assocTopics[i];
                String abstr = (String)t.get("abstract");
                if (abstr.indexOf("descriptorType") != -1) {
                    descriptors.add(t);
                }
            }
            request.getPortletSession().setAttribute(ASSOCIATED_TOPICS, descriptors, PortletSession.PORTLET_SCOPE);

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_SYNONYM);
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS + urlViewParam));

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            // changed main or sub tab
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }

    private Topic getTopicFromTree(DisplayTreeNode similarRoot, String topicId) {
        ArrayList queryTerms = similarRoot.getAllChildren();
        Iterator it = queryTerms.iterator();
        while (it.hasNext()) {
            DisplayTreeNode node = (DisplayTreeNode) it.next();
            if (topicId.equals((String)node.get("topicID"))) {
                return (Topic)node.get("topic");
            }
        }
        return null;
    }

    private PageState initPageState(PageState ps) {
        ps.putBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }
    
    private void openNode(DisplayTreeNode rootNode, String nodeId, Locale language, boolean filterEqualSubNode) {
        DisplayTreeNode node = rootNode.getChild(nodeId);
        node.setOpen(true);
        if (node != null && node.getType() == DisplayTreeNode.SEARCH_TERM && node.getChildren().size() == 0
                && !node.isLoading()) {
            node.setLoading(true);
            IngridHit[] hits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(node.getName(), "/thesa", language);
            if (hits != null && hits.length > 0) {
                for (int i=0; i<hits.length; i++) {
                    Topic hit = (Topic) hits[i];
                    if (hit.getTopicName().equalsIgnoreCase(node.getName())) {
                    	// node name (e.g. initial query term) occurs as sub node (sns topic with equal name)
                    	// include as sub node or not
                    	if (filterEqualSubNode) {
                    		continue;
                    	}
                    }

                    DisplayTreeNode snsNode = new DisplayTreeNode(node.getId() + i, hit.getTopicName(), false);
                    snsNode.setType(DisplayTreeNode.SNS_TERM);
                    snsNode.setParent(node);
                    snsNode.put("topicID", hit.getTopicID());
                    snsNode.put("topic", hit);
                    node.addChild(snsNode);
                }
            } else {
                // TODO remove node from display tree
            }
            node.setLoading(false);
        }
    }
    
}