package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.MeasuresSearchForm;
import de.ingrid.portal.global.IngridHitWrapper;
import de.ingrid.portal.global.IngridHitsWrapper;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

public class MeasuresResultPortlet extends AbstractVelocityMessagingPortlet {

    private final static Logger log = LoggerFactory.getLogger(MeasuresResultPortlet.class);

    /** view templates */
    private final static String TEMPLATE_NO_QUERY = "/WEB-INF/templates/empty.vm";

    private final static String TEMPLATE_RESULT = "/WEB-INF/templates/measures_result.vm";

    private final static String TEMPLATE_NO_RESULT = "/WEB-INF/templates/measures_no_result.vm";

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_MEASURES);

        super.init(config);
    }

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
                log.debug("No starthit of MEASURES page from render request, set starthit to 0");
            }
        }

        // handle grouped page navigation
        String grouping = (String) query.get(Settings.QFIELD_GROUPED);
        int nextStartHit = 0;
        ArrayList groupedStartHits = null;
        int currentSelectorPage = 1;
        if (grouping != null && !grouping.equals(IngridQuery.GROUPED_OFF)) {
            // get the current page number, default to 1
            try {
                currentSelectorPage = (new Integer(request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE)))
                        .intValue();
            } catch (Exception ex) {
                currentSelectorPage = 1;
            }
            // get the grouping starthits history from session
            // create and initialize if not exists
            groupedStartHits = (ArrayList) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS);
            if (groupedStartHits == null || currentSelectorPage == 1) {
                groupedStartHits = new ArrayList();
                groupedStartHits.add(new Integer(0));
                SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits);
            }
            // set next starthit for grouped search
            nextStartHit = ((Integer) groupedStartHits.get(currentSelectorPage - 1)).intValue();
        }

        // ----------------------------------
        // business logic
        // ----------------------------------

        int HITS_PER_PAGE = Settings.SEARCH_RANKED_HITS_PER_PAGE;

        // do search
        IngridHitsWrapper hits = null;
        int numberOfHits = 0;
        try {
            hits = doSearch(query, startHit, nextStartHit, HITS_PER_PAGE, messages, request.getLocale());
            if (hits != null) {
                numberOfHits = (int) hits.length();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing measures catalogue search !", ex);
            }
        }

        if (numberOfHits == 0) {
            // TODO Katalog keine Einträge, WAS ANZEIGEN ??? -> Layouten
            setDefaultViewPage(TEMPLATE_NO_RESULT);
            super.doView(request, response);
            return;
        }

        // store start hit for next page (grouping)
        if (grouping != null && !grouping.equals(IngridQuery.GROUPED_OFF)) {
            groupedStartHits.add(currentSelectorPage, new Integer(hits.getGoupedHitsLength()));
        }

        // adapt settings of page nagihation
        HashMap pageNavigation = UtilsSearch.getPageNavigation(startHit, HITS_PER_PAGE, numberOfHits,
                Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        // ----------------------------------
        // prepare view
        // ----------------------------------

        // set context properties for grouping
        if (grouping != null) {
            if (grouping.equals(IngridQuery.GROUPED_BY_PARTNER)) {
                context.put("grouping", "partner");
            } else if (grouping.equals(IngridQuery.GROUPED_BY_ORGANISATION)) {
                context.put("grouping", "provider");
            }
            // adapt page navigation for grouping 
            if (!grouping.equals(IngridQuery.GROUPED_OFF)) {
                pageNavigation.put("currentSelectorPage", new Integer(currentSelectorPage));
                // check if we have more results to come
                if (numberOfHits <= hits.getGoupedHitsLength()) {
                    pageNavigation.put("selectorHasNextPage", new Boolean(false));
                }
            }
        } else {
            // check for "Zeige alle" for a provider !
        	ArrayList<FieldQuery> providers = (ArrayList<FieldQuery>)query.get("provider");
            ArrayList<String> resultsForProviderList = new ArrayList<String>();
        	if (providers != null) {
        		for (FieldQuery fq : providers) {
        			String providerName = UtilsDB.getProviderFromKey(fq.getFieldValue());
        			if (!resultsForProviderList.contains(providerName)) {
        				resultsForProviderList.add(providerName);
        			}
        		}
            	context.put("resultsForProviderList", resultsForProviderList);
                context.put("providerOnly", "1");
            }
        }
        context.put("rankedPageSelector", pageNavigation);
        context.put("rankedResultList", hits);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // get our ActionForm for generating URL params from current form input
        MeasuresSearchForm af = (MeasuresSearchForm) Utils.getActionForm(request, MeasuresSearchForm.SESSION_KEY,
                MeasuresSearchForm.class, PortletSession.APPLICATION_SCOPE);

        // redirect to our page wih URL parameters for bookmarking
        actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_MEASURES + SearchState.getURLParamsCatalogueSearch(request, af)));
    }

    private IngridHitsWrapper doSearch(IngridQuery query, int startHit, int groupedStartHit, int hitsPerPage,
            IngridResourceBundle resources, Locale locale) {
        if (log.isDebugEnabled()) {
            log.debug("Messwerte IngridQuery = " + UtilsSearch.queryToString(query));
        }

        int currentPage = (int) (startHit / hitsPerPage) + 1;

        if (groupedStartHit > 0) {
        	startHit = groupedStartHit;
        }

        IngridHits hits = null;
        IngridHitsWrapper hitsWrapper = null;
        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            String[] requestedFields = { Settings.RESULT_KEY_RUBRIC, Settings.RESULT_KEY_PARTNER,
                    Settings.RESULT_KEY_PROVIDER };
            
            hits = ibus.searchAndDetail(query, hitsPerPage, currentPage, startHit, PortalConfig.getInstance().getInt(
                    PortalConfig.QUERY_TIMEOUT_RANKED, 5000), requestedFields);
            
            if (hits == null) {
                if (log.isErrorEnabled()) {
                    log.error("Problems fetching details to hit list !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
            }

            // convert
            hitsWrapper = new IngridHitsWrapper(hits);
            IngridHitWrapper[] hitArray = hitsWrapper.getWrapperHits();
            
            IngridHit[] subHitArray = null;
            for (int i = 0; i < hitArray.length; i++) {
                try {
                    if (hitArray[i] == null) {
                        continue;
                    }
                    if (hitArray[i].getHitDetail() != null) {
                        transferDetailData(hitArray[i], hitArray[i].getHitDetail(), resources);
                    }
                    // check for grouping and get details of "sub hits"
                    // NO, WE ONLY SHOW ONE HIT !
                    subHitArray = hitArray[i].getHit().getGroupHits();
                    if (subHitArray.length > 0) {
                    	hitArray[i].putBoolean("moreHits", true);
                    }
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems processing Hit, hit = " + hitArray[i] +
                        		", detail = " + hitArray[i].getHitDetail(), t);
                    }
                }
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing Search !", t);
            }
        }

        return hitsWrapper;
    }

    private void transferDetailData(IngridHitWrapper hit, IngridHitDetail detail, IngridResourceBundle resources) {
        UtilsSearch.transferHitDetails(hit, detail);
        hit.put("topic", UtilsSearch.getDetailValue(detail, Settings.RESULT_KEY_RUBRIC, resources));
    }

}
