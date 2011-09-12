/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtEnvTimeChronicleForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.udk.UtilsDate;

/**
 * This portlet handles the fragment of the time references (events in chronicle) in the extended search
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvTimeChroniclePortlet extends AbstractVelocityMessagingPortlet {

    private final static Logger log = LoggerFactory.getLogger(SearchExtEnvTimeChroniclePortlet.class);

    // PAGES
    /** our current page, for redirecting with URL params */
    private final static String PAGE_CURRENT = "/portal/search-extended/search-ext-env-time-constraint.psml";

    // URL PARAMETER VALUES
    private final static String PARAMV_VIEW_RESULTS = "results";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        SearchExtEnvTimeChronicleForm f = (SearchExtEnvTimeChronicleForm) Utils.getActionForm(request,
                SearchExtEnvTimeChronicleForm.SESSION_KEY, SearchExtEnvTimeChronicleForm.class);
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
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            return;
        }
        PortletSession session = request.getPortletSession();

        // SEARCH FOR EVENTS
        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_SEARCH)) {

            // check form input
            SearchExtEnvTimeChronicleForm f = (SearchExtEnvTimeChronicleForm) Utils.getActionForm(request,
                    SearchExtEnvTimeChronicleForm.SESSION_KEY, SearchExtEnvTimeChronicleForm.class);
            f.clearErrors();
            f.populate(request);
            if (f.validate()) {
                Locale locale = request.getLocale();
                IngridResourceBundle resources = new IngridResourceBundle(getPortletConfig().getResourceBundle(locale));

                String searchTerm = f.getInput(SearchExtEnvTimeChronicleForm.FIELD_SEARCH_TERM);
                IngridQuery query = null;
                try {
                    // build SNS query
                    query = QueryStringParser.parse(searchTerm);
                    query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
                    query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);
                    query.put(Settings.QFIELD_DATE_TO, "3000-01-01");
                    // add language to query
                    UtilsSearch.processLanguage(query, request.getLocale());
                    // query.addField(new FieldQuery(true, false, Settings.QFIELD_LANG, Settings.QVALUE_LANG_DE));
                    // search in SNS
                    IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
					IngridHits hits = ibus.searchAndDetail(query, 50, 1, 0, PortalConfig.getInstance().getInt(
                    		PortalConfig.SNS_TIMEOUT_DEFAULT, 30000), null);
                    IngridHit[] results = hits.getHits();
                    if (hits == null) {
                        if (log.isErrorEnabled()) {
                            log.error("Problems fetching details of events !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        }
                    }
                    // copy detail data into hits
                    Topic topic = null;
                    DetailedTopic detail = null;
                    for (int i = 0; i < results.length; i++) {
                        try {
                            topic = (Topic) results[i];
                            detail = null;
                            if (results[i].getHitDetail() != null) {
                                detail = (DetailedTopic) results[i].getHitDetail();
                            }

                            if (topic == null) {
                                continue;
                            }
                            topic.put("title", topic.getTopicName());

                            if (detail != null) {
                                String searchData = (String) detail.get(DetailedTopic.ASSOCIATED_OCC);
                                if (searchData != null && searchData.length() > 0) {
                                    searchData = UtilsString.htmlescape(searchData.replaceAll("\\,", " OR "));
                                } else {
                                    searchData = UtilsString.htmlescape(topic.getTopicName());
                                }
                                topic.put("date", UtilsDate.getOutputString(detail.getFrom(), detail.getTo(), locale));
                                topic.put("description", detail.get(DetailedTopic.DESCRIPTION_OCC));
                            }
                        } catch (Throwable t) {
                            if (log.isErrorEnabled()) {
                                log.error("Problems processing Event, hit=" + topic + ", detail=" + detail, t);
                            }
                        }
                    }
                    // init page state
                    PageState ps = (PageState) session.getAttribute("portlet_state");
                    if (ps == null) {
                        ps = new PageState(this.getClass().getName());
                        ps = initPageState(ps);
                        session.setAttribute("portlet_state", ps);
                    }
                    // build display tree
                    DisplayTreeNode similarRoot = null;
                    similarRoot = DisplayTreeFactory.getTreeFromEvents(results);
                    if (similarRoot.getChildren().size() > 0) {
                        session.setAttribute("similarRoot", similarRoot);
                    } else {
                        f.setError("", "searchExtEnvTimeChronicle.error.no_term_found");
                    }
                    ps.put("similarRoot", similarRoot);

                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems performing SNS search", t);
                    }
                    f.setError("", "searchExtEnvTimeChronicle.error.no_term_found");
                }
            }

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_CURRENT + urlViewParam));

        } else if (action.equalsIgnoreCase("doOpenEvent")) {

            DisplayTreeNode similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(true);
                }
            }

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_CURRENT + urlViewParam));

        } else if (action.equalsIgnoreCase("doCloseEvent")) {

            DisplayTreeNode similarRoot = (DisplayTreeNode) session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(false);
                }
            }

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_VIEW_RESULTS);
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_CURRENT + urlViewParam));
        }

    }

    private PageState initPageState(PageState ps) {
        ps.putBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }

}
