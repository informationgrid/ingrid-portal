/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.PlugDescription;
import de.ingrid.portal.forms.SimpleSearchForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.ibus.IBUSInterface;
import de.ingrid.portal.interfaces.ibus.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.SimilarTreeNode;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.portal.search.mockup.SimilarNodeFactoryMockup;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SearchResultPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(SearchResultPortlet.class);

    /** Keys of parameters in session/request */
    private final static String KEY_START_HIT_RANKED = "rstart";

    private final static String KEY_START_HIT_UNRANKED = "nrstart";

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

        // TODO remove page state in future, when separate portlets
        // use messages and render parameters instead !!!
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        // BEGIN: "simple_search" Portlet
        // TODO: MERGE THIS WITH THE SimpleSearch"Teaser" portlet !!!!

        // NOTICE:
        // may be called directly from Start page, then this method has to be an action method !
        // When called from startPage, the action request parameter is "doSearch" !!! 

        // get ActionForm, we use get method without instantiation, so we can do
        // special initialisation
        SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
        if (af == null) {
            af = (SimpleSearchForm) Utils.addActionForm(request, SimpleSearchForm.SESSION_KEY, SimpleSearchForm.class,
                    PortletSession.APPLICATION_SCOPE);
            // TODO when separate portlet, then read this from lokale !!!! we don't do this here because we
            // don't wanna add it to resource bundle of RESULT PORTLET !
            //            af.setINITIAL_QUERY(messages.getString("simpleSearch.query.initial"));
            af.setINITIAL_QUERY("Geben Sie hier Ihre Suchanfrage ein");
            af.init();
        }
        // put ActionForm to context. use variable name "actionForm" so velocity macros work !
        context.put("actionForm", af);

        // if action is "doSearch" page WAS CALLED FROM STARTPAGE and no action was triggered ! 
        // (then we are on the result psml page !!!)
        String action = request.getParameter("action");
        if (action != null && action.equalsIgnoreCase("doSearch")) {
            // all ActionStuff encapsulated in separate method !
            doSimpleSearchPortletActionStuff(request, af);
        }

        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.INITIAL_DATASOURCE;
            publishRenderMessage(request, Settings.MSG_DATASOURCE, selectedDS);
        }
        context.put("ds", selectedDS);
        // END: "simple_search" Portlet

        // BEGIN: search_result Portlet
        // if no query display "nothing"
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
        String queryString = (String) receiveRenderMessage(request, Settings.MSG_QUERY_STRING);
        if (query == null) {
            //            setDefaultViewPage(TEMPLATE_NO_QUERY);
            //            super.doView(request, response);
            //            return;
        }

        // if query assume we have results
        //        setDefaultViewPage(TEMPLATE_RESULT);

        // no "if" clause necessary when separate portlet, then we return above !
        if (query != null) {

            // ----------------------------------
            // fetch data
            // ----------------------------------
            int rankedStartHit = 0;
            int unrankedStartHit = 0;

            // indicates whether a new query was performed !
            Object newQuery = receiveRenderMessage(request, Settings.MSG_NEW_QUERY);
            try {
                // if no new query was performed, read render parameters from former action request
                if (newQuery == null) {
                    String reqParam = request.getParameter(KEY_START_HIT_RANKED);
                    if (reqParam != null) {
                        rankedStartHit = (new Integer(reqParam)).intValue();                        
                    }
                    reqParam = request.getParameter(KEY_START_HIT_UNRANKED);
                    if (reqParam != null) {
                        unrankedStartHit = (new Integer(reqParam)).intValue();
                    }
                }
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Problems parsing starthit (ranked or unranked) from render request, set starthit to 0");
                }
            }

            /*String*/selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
            if (selectedDS == null) {
                selectedDS = Settings.INITIAL_DATASOURCE;
            }

            // ----------------------------------
            // business logic
            // ----------------------------------

            // ranked results	
            int RANKED_HITS_PER_PAGE = Settings.RANKED_HITS_PER_PAGE;
            IngridHits rankedHits = null;
            int numberOfHits = 0;
            try {
                rankedHits = doRankedSearch(query, selectedDS, rankedStartHit, RANKED_HITS_PER_PAGE);
                numberOfHits = (int) rankedHits.length();                
            } catch (Exception ex) {
                if (log.isInfoEnabled()) {
                    log.info("Problems fetching ranked hits ! hits = "+rankedHits+", numHits = "+numberOfHits, ex);
                }                
            }

            // adapt settings of ranked page navigation
            HashMap rankedPageNavigation = Utils.getPageNavigation(rankedStartHit, RANKED_HITS_PER_PAGE, numberOfHits,
                    Settings.RANKED_NUM_PAGES_TO_SELECT);

            // unranked results	
            int UNRANKED_HITS_PER_PAGE = Settings.UNRANKED_HITS_PER_PAGE;
            SearchResultList unrankedSRL = doUnrankedSearch(query, selectedDS, unrankedStartHit, UNRANKED_HITS_PER_PAGE);
            numberOfHits = unrankedSRL.getNumberOfHits();

            // adapt settings of unranked page navigation
            HashMap unrankedPageNavigation = Utils.getPageNavigation(unrankedStartHit, UNRANKED_HITS_PER_PAGE,
                    numberOfHits, Settings.UNRANKED_NUM_PAGES_TO_SELECT);

            // ----------------------------------
            // prepare view
            // ----------------------------------

            context.put("rankedPageSelector", rankedPageNavigation);
            context.put("unrankedPageSelector", unrankedPageNavigation);
            context.put("rankedResultList", rankedHits);
            context.put("unrankedResultList", unrankedSRL);
            // query string will be displayed when no results !
            context.put("queryString", queryString);
        }
        // END: search_result Portlet

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

        // BEGIN: "simple_search" Portlet
        // TODO: MERGE THIS WITH THE SimpleSearch"Teaser" portlet !!!!
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        if (action.equalsIgnoreCase("doSearch")) {

            // check form input
            SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                    SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
            // encapsulate all ActionStuff in separate method, has to be called in view method too (when called
            // from start page !)
            doSimpleSearchPortletActionStuff(request, af);

            // when separate portlets: just send a message that a new query was executed and do this in the
            // search_result_similar Portlet
            ps.setBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);

        } else if (action.equalsIgnoreCase("doChangeDS")) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, request.getParameter("ds"));
        }
        // END: "simple_search" Portlet

        // BEGIN: search_result_similar Portlet
        if (action.equalsIgnoreCase("doOpenSimilar")) {
            ps.setBoolean("isSimilarOpen", true);
            ps.put("similarRoot", SimilarNodeFactoryMockup.getSimilarNodes());
        } else if (action.equalsIgnoreCase("doCloseSimilar")) {
            ps.setBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
        } else if (action.equalsIgnoreCase("doOpenNode")) {
            SimilarNodeFactoryMockup.setOpen((SimilarTreeNode) ps.get("similarRoot"), request.getParameter("nodeId"),
                    true);
        } else if (action.equalsIgnoreCase("doCloseNode")) {
            SimilarNodeFactoryMockup.setOpen((SimilarTreeNode) ps.get("similarRoot"), request.getParameter("nodeId"),
                    false);
        } else if (action.equalsIgnoreCase("doAddSimilar")) {
            ArrayList nodes = getNodeChildrenArray((SimilarTreeNode) ps.get("similarRoot"));
            Iterator it = nodes.iterator();
            StringBuffer sbQuery = new StringBuffer(ps.getString("query"));
            while (it.hasNext()) {
                SimilarTreeNode node = (SimilarTreeNode) it.next();
                if (request.getParameter("chk_" + node.getId()) != null && sbQuery.indexOf(node.getName()) == -1) {
                    sbQuery.append(" AND ").append(node.getName());
                } else if (node.getDepth() == 2) {
                    node.setOpen(false);
                }
            }
            ps.put("query", sbQuery.toString());
        }
        // END: search_result_similar Portlet

        // BEGIN: search_result Portlet
        // ----------------------------------
        // fetch parameters
        // ----------------------------------
        String rankedStarthit = request.getParameter(KEY_START_HIT_RANKED);
        String unrankedStarthit = request.getParameter(KEY_START_HIT_UNRANKED);

        // ----------------------------------
        // business logic
        // ----------------------------------
        // no new query anymore, we remove messages, so portlets read our render parameters (set below)
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY);
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY_FOR_SIMILAR);

        // ----------------------------------
        // set render parameters
        // ----------------------------------
        if (rankedStarthit != null) {
            actionResponse.setRenderParameter(KEY_START_HIT_RANKED, rankedStarthit);            
        }
        if (unrankedStarthit != null) {
            actionResponse.setRenderParameter(KEY_START_HIT_UNRANKED, unrankedStarthit);
        }
        // END: search_result Portlet
    }

    private IngridHits doRankedSearch(IngridQuery query, String ds, int startHit, int hitsPerPage) {

        int SEARCH_TIMEOUT = 2000;
        int SEARCH_REQUESTED_NUM_HITS = 10;
        int currentPage = (int) (startHit / hitsPerPage) + 1;

        String RESULT_KEY_TITLE = "title";
        String RESULT_KEY_ABSTRACT = "abstract";
        String RESULT_KEY_URL = "url";
        String RESULT_KEY_PROVIDER = "provider";
        String RESULT_KEY_SOURCE = "source";
        String RESULT_KEY_TYPE = "type";
        String RESULT_KEY_PLUG_ID = "plugid";
        String RESULT_KEY_DOC_ID = "docid";
        String RESULT_KEY_WMS_URL = "wms_url";
        String RESULT_KEY_UDK_CLASS = "udk_class";

        IngridHits hits = null;

        try {
            
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance(); 
            hits = ibus.search(query, hitsPerPage, currentPage, SEARCH_REQUESTED_NUM_HITS, SEARCH_TIMEOUT);
            IngridHit[] results = hits.getHits();

            IngridHit result = null;
            IngridHitDetail detail = null;
            String plugId = null;
            PlugDescription plug = null;
            for (int i = 0; i < results.length; i++) {
                detail = null;
                plug = null;
                try {
                    result = results[i];
                    detail = ibus.getDetails(result, query);
                    plugId = result.getPlugId();
                    plug = ibus.getIPlug(plugId);
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems fetching result details or iPlug", t);
                    }
                }
                if (result == null) {
                    continue;
                }
                result.put(RESULT_KEY_DOC_ID, new Integer(result.getDocumentId()));
                if (detail != null) {
                    result.put(RESULT_KEY_TITLE, detail.getTitle());
                    result.put(RESULT_KEY_ABSTRACT, detail.getSummary());
                    result.put(RESULT_KEY_URL, detail.get("url"));
                }
                if (plug != null) {
                    result.put(RESULT_KEY_PROVIDER, plug.getOrganisation());
                    result.put(RESULT_KEY_SOURCE, plug.getDataSourceName());
                    result.put(RESULT_KEY_PLUG_ID, plug.getPlugId());
                    if (plug.getIPlugClass().equals("de.ingrid.iplug.dsc.index.DSCSearcher")) {
                        result.put(RESULT_KEY_TYPE, "dsc");
                        
                        Record record = ibus.getRecord(result);
                        Column c = getUDKColumn(record, "T011_obj_serv_op_connpoint.connect_point");
                        if (c != null) {
                            result.put(RESULT_KEY_WMS_URL, URLEncoder.encode(record.getValueAsString(c), "UTF-8"));
                        }
                        c = getUDKColumn(record, "T01_object.obj_class");
                        if (c != null) {
                            result.put(RESULT_KEY_UDK_CLASS, record.getValueAsString(c));
                        }

                    } else if (plug.getIPlugClass().equals("de.ingrid.iplug.se.NutchSearcher")) {
                        result.put(RESULT_KEY_TYPE, "nutch");
                    } else {
                        result.put(RESULT_KEY_TYPE, "unknown");
                    }
                }
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing Search !", t);
            }
        }

        return hits;
    }

    private SearchResultList doUnrankedSearch(IngridQuery query, String ds, int startHit, int hitsPerPage) {

        SearchResultList result = new SearchResultList();

        SearchResultList l = SearchResultListMockup.getUnrankedSearchResultList();
        for (int i = 0; i < hitsPerPage; i++) {
            if (i >= l.size())
                break;
            result.add(l.get(i));
        }
        result.setNumberOfHits(l.getNumberOfHits());
        return result;
    }

    private void doSimpleSearchPortletActionStuff(PortletRequest request, SimpleSearchForm af) {
        // remove old query message
        cancelRenderMessage(request, Settings.MSG_QUERY);
        cancelRenderMessage(request, Settings.MSG_QUERY_STRING);
        // set messages that a new query was performed ! set separate messages for every portlet, because every
        // portlet removes it's message (we don't know processing order) !
        publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);
        publishRenderMessage(request, Settings.MSG_NEW_QUERY_FOR_SIMILAR, Settings.MSG_VALUE_TRUE);

        af.populate(request);
        if (!af.validate()) {
            return;
        }

        // check query input
        String queryString = af.getInput(af.FIELD_QUERY);
        if (queryString.equals(af.getINITIAL_QUERY()) || queryString.length() == 0) {
            return;
        }

        // Create IngridQuery from form input !
        IngridQuery query = null;
        queryString = queryString.toLowerCase();
        try {
            query = QueryStringParser.parse(queryString);
        } catch (ParseException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Problems creating IngridQuery, parsed query string: " + queryString, ex);
            }
            // TODO create ERROR message when no IngridQuery because of parse error and return (OR even do that in form ???) 
        }
        // set query in message for result portlet
        if (query != null) {
            publishRenderMessage(request, Settings.MSG_QUERY, query);
            publishRenderMessage(request, Settings.MSG_QUERY_STRING, queryString);
        }
    }

    private PageState initPageState(PageState ps) {
        ps.put("query", null);
        ps.setBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }

    private ArrayList getNodeChildrenArray(SimilarTreeNode n) {

        ArrayList ret = new ArrayList();
        Iterator it = n.getChildren().iterator();
        while (it != null && it.hasNext()) {
            ret.addAll(getNodeChildrenArray((SimilarTreeNode) it.next()));
        }
        ret.add(n);

        return ret;
    }
    
    private Column getUDKColumn(Record record, String udkColumnName) {
        // serach for column
        Column[] columns = record.getColumns();
        for (int i=0;i<columns.length;i++) {
            if (columns[i].getTargetName().equalsIgnoreCase(udkColumnName)) {
                return columns[i];
            }
        }
        // search sub records
        Record[] subRecords = record.getSubRecords();
        Column c = null;
        for (int i=0;i<subRecords.length;i++) {
            c = getUDKColumn(subRecords[i], udkColumnName);
            if (c != null) {
                return c;
            }
        }
        return null;
    }
    

}