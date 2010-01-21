/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.pluto.core.impl.PortletSessionImpl;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.forms.SearchExtEnvPlaceGeothesaurusForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsQueryString;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.tool.SNSUtil;

/**
 * This portlet handles the fragment of the geothesaurus input in the extended search.
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvPlaceGeothesaurusPortlet extends SearchExtEnvPlace {

    // VIEW TEMPLATES

    private static final String CURRENT_TOPIC = "current_topic";

    private static final String TOPICS = "topics";

    private static final String SIMILAR_TOPICS = "similar_topics";

    private final static String TEMPLATE_START = "/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus.vm";

    private final static String TEMPLATE_RESULTS = "/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus_results.vm";

    private final static String TEMPLATE_BROWSE = "/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus_browse.vm";

    // PARAMETER VALUES

    private final static String PARAMV_VIEW_RESULTS = "1";

    private final static String PARAMV_VIEW_BROWSE = "2";
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_PLACE);
        context.put(VAR_SUB_TAB, PARAMV_TAB_GEOTHESAURUS);

        setDefaultViewPage(TEMPLATE_START);

        SearchExtEnvPlaceGeothesaurusForm f = (SearchExtEnvPlaceGeothesaurusForm) Utils.getActionForm(request, SearchExtEnvPlaceGeothesaurusForm.SESSION_KEY, SearchExtEnvPlaceGeothesaurusForm.class);        
        context.put("actionForm", f);
        
        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
            f.init();
        }
        if (action.equals(PARAMV_VIEW_RESULTS)) {
            if (!f.hasErrors()) {
                setDefaultViewPage(TEMPLATE_RESULTS);
                context.put("topics", (IngridHit[])request.getPortletSession().getAttribute(TOPICS));
                context.put("list_size", new Integer(((IngridHit[])request.getPortletSession().getAttribute(TOPICS)).length));
            }
        } else if (action.equals(PARAMV_VIEW_BROWSE)) {
            setDefaultViewPage(TEMPLATE_BROWSE);
            context.put("current_topic", request.getPortletSession().getAttribute(CURRENT_TOPIC));
            context.put("similar_topics", request.getPortletSession().getAttribute(SIMILAR_TOPICS));
            context.put("list_size", new Integer(((IngridHit[])request.getPortletSession().getAttribute(SIMILAR_TOPICS)).length));
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

        if (submittedSearch != null) {

            // In Thesaurus suchen
            SearchExtEnvPlaceGeothesaurusForm f = (SearchExtEnvPlaceGeothesaurusForm) Utils.getActionForm(request, SearchExtEnvPlaceGeothesaurusForm.SESSION_KEY, SearchExtEnvPlaceGeothesaurusForm.class);        
            f.clearErrors();
            f.populate(request);
            if (f.validate()) {
                IngridHit[] hits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(f.getInput(SearchExtEnvPlaceGeothesaurusForm.FIELD_SEARCH_TERM), "/location",
                		request.getLocale());
                if (hits != null && hits.length > 0) {
                    for (int i=0; i<hits.length; i++) {
                        String href = UtilsSearch.getDetailValue(hits[i], "href");
                        if (href != null && href.lastIndexOf("#") != -1) {
                            hits[i].put("topic_ref", href.substring(href.lastIndexOf("#")+1));
                        }
                    }
                    request.getPortletSession().setAttribute(TOPICS, hits, PortletSessionImpl.PORTLET_SCOPE);
                } else {
                    f.setError("", "searchExtEnvPlaceGeothesaurus.error.no_term_found");
                    request.getPortletSession().removeAttribute(TOPICS);
                }
            } else {
                request.getPortletSession().removeAttribute(TOPICS);
            }
            
            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            actionResponse.sendRedirect(PAGE_GEOTHESAURUS + urlViewParam);

        } else if (submittedAddToQuery != null) {

            // Zur Suchanfrage hinzufuegen
            String subTerm = "";
            String listSize = request.getParameter("list_size");
            for (int i=0; i< Integer.parseInt(listSize); i++) {
                String chkVal = request.getParameter("chk"+(i+1));
                if (chkVal != null) {
                    if (subTerm.length() > 0) {
                        subTerm = subTerm.concat(" OR ");
                    }
                    subTerm = subTerm.concat("areaid:").concat(SNSUtil.transformSpacialReference(null, chkVal));
                }
            }
            if (subTerm.length() > 0) {
                subTerm = "(".concat(subTerm).concat(")");
                String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);
                PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, subTerm, UtilsQueryString.OP_AND));
            }
            

            // redirect to same page with view param where we currently are (so we keep view !)
            String currView = getDefaultViewPage();
            String urlViewParam = "";
            if (currView.equals(TEMPLATE_RESULTS)) {
                urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            } else if (currView.equals(TEMPLATE_BROWSE)) {
                urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_BROWSE);
            }
            actionResponse.sendRedirect(PAGE_GEOTHESAURUS + urlViewParam);

        } else if (action.equalsIgnoreCase("doBrowse")) {

            // SNS Deskriptor browsen
            IngridHit[] hits = null;
            String topicId = request.getParameter("topic_id");
            if (topicId != null) {
                hits = (IngridHit[])request.getPortletSession().getAttribute(TOPICS);
            } else {
                topicId = request.getParameter("similar_topic_id");
                if (topicId != null) {
                    hits = (IngridHit[])request.getPortletSession().getAttribute(SIMILAR_TOPICS);
                }
            }

            if (hits != null && hits.length > 0) {
                for (int i=0; i<hits.length; i++) {
                    String tid = UtilsSearch.getDetailValue(hits[i], "topicID");
                    if (tid != null && tid.equals(topicId)) {
                        request.getPortletSession().setAttribute(CURRENT_TOPIC, hits[i], PortletSessionImpl.PORTLET_SCOPE);
                        IngridHit[] similarHits = SNSSimilarTermsInterfaceImpl.getInstance().getTopicSimilarLocationsFromTopic(topicId);
                        if (similarHits == null) {
                            SearchExtEnvPlaceGeothesaurusForm f = (SearchExtEnvPlaceGeothesaurusForm) Utils.getActionForm(request, SearchExtEnvPlaceGeothesaurusForm.SESSION_KEY, SearchExtEnvPlaceGeothesaurusForm.class);
                            f.setError("", "searchExtEnvPlaceGeothesaurus.error.no_term_found");
                            break;
                        }
                        for (int j=0; j<similarHits.length; j++) {
                        	String href = UtilsSearch.getDetailValue(similarHits[j], "abstract");
                            if (href != null && href.lastIndexOf("#") != -1) {
                                similarHits[j].put("topic_ref", href.substring(href.lastIndexOf("#")+1));
                            }
                        }
                        request.getPortletSession().setAttribute(SIMILAR_TOPICS, similarHits, PortletSessionImpl.PORTLET_SCOPE);            
                        break;
                    }
                }
            }
            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_BROWSE);
            actionResponse.sendRedirect(PAGE_GEOTHESAURUS + urlViewParam);

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}