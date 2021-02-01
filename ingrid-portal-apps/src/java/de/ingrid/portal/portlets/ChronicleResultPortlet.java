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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ChronicleSearchForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.udk.UtilsDate;

public class ChronicleResultPortlet extends AbstractVelocityMessagingPortlet {

    private static final Logger log = LoggerFactory.getLogger(ChronicleResultPortlet.class);

    /** view templates */
    private static final String TEMPLATE_NO_QUERY = "/WEB-INF/templates/empty.vm";

    private static final String TEMPLATE_RESULT = "/WEB-INF/templates/chronicle_result.vm";

    private static final String TEMPLATE_NO_RESULT = "/WEB-INF/templates/chronicle_no_result.vm";

    /** search page for "recherche" */
    private static final String PAGE_SEARCH = "/portal/main-search.psml";

    @Override
    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_CHRONICLE);

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
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
        if (query == null) {
            setDefaultViewPage(TEMPLATE_NO_QUERY);
            super.doView(request, response);
            return;
        }

        // if query assume we have results
        setDefaultViewPage(TEMPLATE_RESULT);

        // ----------------------------------
        // fetch data
        // ----------------------------------

        // default: start at the beginning with the first hit (display first result page)
        int startHit = 0;
        try {
            startHit = (new Integer(request.getParameter(Settings.PARAM_STARTHIT_RANKED))).intValue();
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("No starthit of CHRONICLE page from render request, set starthit to 0");
            }
        }

        // ----------------------------------
        // business logic
        // ----------------------------------

        int hitsPerPage = Settings.SEARCH_RANKED_HITS_PER_PAGE;

        // do search
        IngridHits hits = null;
        int numberOfHits = 0;
        try {
            hits = doSearch(query, startHit, hitsPerPage, request, response);
            if (hits != null) {
                numberOfHits = (int) hits.length();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing CHRONICLE search !", ex);
            }
        }

        if (numberOfHits == 0) {
            // TODO Chronik keine Einträge, WAS ANZEIGEN ??? -> Layouten
            setDefaultViewPage(TEMPLATE_NO_RESULT);
            super.doView(request, response);
            return;
        }

        // adapt settings of page navigation
        Map<String, Object> pageNavigation = UtilsSearch.getPageNavigation(startHit, hitsPerPage, numberOfHits,
                Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        // ----------------------------------
        // prepare view
        // ----------------------------------

        context.put("rankedPageSelector", pageNavigation);
        context.put("rankedResultList", hits);

        super.doView(request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // get our ActionForm for generating URL params from current form input
        ChronicleSearchForm af = (ChronicleSearchForm) Utils.getActionForm(request, ChronicleSearchForm.SESSION_KEY,
                ChronicleSearchForm.class, PortletSession.APPLICATION_SCOPE);

        // redirect to our page wih URL parameters for bookmarking
        actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CHRONICLE + SearchState.getURLParamsCatalogueSearch(request, af)));
    }

    private IngridHits doSearch(IngridQuery query, int startHit, int hitsPerPage, PortletRequest request,
            PortletResponse response) {
        if (log.isDebugEnabled()) {
            log.debug("Umweltchronik IngridQuery = " + UtilsSearch.queryToString(query));
        }

        Locale locale = request.getLocale();
        IngridResourceBundle resources = new IngridResourceBundle(getPortletConfig().getResourceBundle(locale), locale);

        String searchURLBase = ((RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest()
                .getContextPath()
                + PAGE_SEARCH + "?action=doSearch&ds=1&q=";

        // check whether we're called from teaser on start page and should display ONLY THE TEASER event !
        String teaserTopicId = (String) query.remove("TEASER_TOPIC_ID");

        IngridHits hits = null;
        IngridHit[] results = null;
        IngridHitDetail[] details = null;
        try {
            int currentPage = (startHit / hitsPerPage) + 1;
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();

            // due to a HACK, it is not possible to use the searchAndDetail-Method
            // the details are requested with a modified resultSet from a former bus-search
            if (teaserTopicId == null) {
                hits = ibus.search(query, hitsPerPage, currentPage, startHit, PortalConfig.getInstance().getInt(
                        PortalConfig.SNS_TIMEOUT_DEFAULT, 30000));
            } else {
                // HACK: we request ONE Hit to have the correct plug ID !
                // Then we manipulate the Hit and the IngridHits to get the correct Event and Number Of Events !
                hits = ibus.search(query, 1, 1, 0, PortalConfig.getInstance().getInt(PortalConfig.SNS_TIMEOUT_DEFAULT,
                        30000));
                results = hits.getHits();
                if (results != null && results.length > 0) {
                    Topic topic = (Topic) results[0];
                    // !!! IMPORTANT !!! iBus compares documentId of Topic and DetailedTopic and has to be the same !!!
                    topic.setDocumentId(teaserTopicId.hashCode());
                    topic.setTopicID(teaserTopicId);
                    topic.remove("title");
                }
                if (hits.length() > 1) {
                    hits.putLong("length", 1);
                }
            }
            results = hits.getHits();

            // for details also add filter of topic type for SNS
            query.put("filter", "/event");
            details = ibus.getDetails(results, query, null);

            if (details == null) {
                if (log.isErrorEnabled()) {
                    log.error("Problems getting details of hits ! hits = " + results + ", details = " + details);
                }
            } else {
                Topic topic = null;
                DetailedTopic detail = null;
                for (int i = 0; i < results.length; i++) {
                    try {
                        topic = (Topic) results[i];
                        boolean isDetailedTopic = false;
                        if (details[i] != null) {
                        	isDetailedTopic = DetailedTopic.class.isAssignableFrom(details[i].getClass());
                        }
                        
                        // skip NULL entries
                        // and also plain IngridHitDetail (returned if topics details cannot be found) !
                        if (!isDetailedTopic || topic == null) {
                        	continue;
                        }

                        detail = (DetailedTopic) details[i];
                        topic.put("title", detail.getTopicName());

                        String searchData = (String) detail.get(DetailedTopic.ASSOCIATED_OCC);
                        if (searchData != null) {
                            // remove double quoted stuff '\"'
                            searchData = searchData.replaceAll("\\\\\"", "");
                            if (searchData.length() > 0) {
                                searchData = UtilsString.htmlescape(searchData.replaceAll("\\,", " OR "));
                            }
                        } else {
                            searchData = UtilsString.htmlescape(detail.getTopicName());
                        }
                        String searchURL = response.encodeURL(searchURLBase.concat(searchData));
                        topic.put("searchURL", searchURL);
                        topic.put("date", UtilsDate.getOutputString(detail.getFrom(), detail.getTo(), locale));

                        topic.put("description", detail.get(DetailedTopic.DESCRIPTION_OCC));
                        /*
                         String[] url = (String[]) detail.get(DetailedTopic.SAMPLE_OCC);
                         if (url != null && url.length > 0 && url[0].length() > 0) {
                         topic.put("url", url[0]);
                         topic.put("url_str", Utils.getShortURLStr(url[0], 80));
                         }
                         */
                        // type
                        String urlWithType = (String) detail.getArrayList(DetailedTopic.INSTANCE_OF).get(0);
                        String type = urlWithType.substring(urlWithType.lastIndexOf('/') + 1);
                        topic.put("type", resources.getString(type));

                        // Definitions: URL's and titles
                        String[] array = detail.getDefinitions();
                        if (array != null && array.length > 0) {
                            topic.put("definitions", Arrays.asList(array));
                        }
                        array = detail.getDefinitionTitles();
                        if (array != null && array.length > 0) {
                            array = Utils.getShortStrings(array, 80);
                            topic.put("defTitles", Arrays.asList(array));
                        }

                        // Verwandte Infos: URLs and titles
                        array = detail.getSamples();
                        if (array != null && array.length > 0) {
                            topic.put("samples", Arrays.asList(array));
                        }
                        array = detail.getSampleTitles();
                        if (array != null && array.length > 0) {
                            array = Utils.getShortStrings(array, 80);
                            topic.put("sampleTitles", Arrays.asList(array));
                        }

                        /*
                         // for testing template
                         String[] tmpArray = { "http://www.wemove.com/", "http://www.media-style.com/" };
                         topic.put("definitions", Arrays.asList(tmpArray));
                         String[] tmpArray2 = { "test title wemove", "test title media style" };
                         topic.put("defTitles", Arrays.asList(tmpArray2));
                         topic.put("samples", Arrays.asList(tmpArray));
                         topic.put("sampleTitles", Arrays.asList(tmpArray2));
                         */
                    } catch (Exception t) {
                        if (log.isErrorEnabled()) {
                            log.error("Problems processing " + i + ". Event, hit = " + topic + ", detail = " + detail, t);
                        }
                    }
                }                
            }
        } catch (Exception t) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing SNS Search ! results = " + results + ", details = " + details, t);
            }
        }

        return hits;
    }
}
