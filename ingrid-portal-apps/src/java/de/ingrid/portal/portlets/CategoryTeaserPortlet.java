/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.FacetsConfig;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.CodeListServiceFactory;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsFacete;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridFacet;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

public class CategoryTeaserPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger( CategoryTeaserPortlet.class );

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init( config );
    }

    @Override
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException {
        Context context = getContext( request );

        IngridResourceBundle messages = new IngridResourceBundle( getPortletConfig().getResourceBundle( request.getLocale() ), request.getLocale() );
        context.put( "MESSAGES", messages );

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue( "titleKey", "" );
        response.setTitle(messages.getString(titleKey));
        
        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) FacetsConfig.getFacets();
        IngridQuery query;
        IngridHits hits = null;
        String categoryQuery = null;
        try {
            categoryQuery = PortalConfig.getInstance().getString( PortalConfig.CATEGORY_TEASER_SEARCH_QUERY, "" ).trim();
            query = QueryStringParser.parse( categoryQuery );
            UtilsSearch.processRestrictingPartners(query);
            UtilsFacete.addDefaultIngridFacets( request, config );
            if (query.get( "FACETS" ) == null) {
                ArrayList<IngridDocument> facetQueries = new ArrayList<>();
                UtilsFacete.getConfigFacetQuery( config, facetQueries, true, null, true );
                query.put( "FACETS", facetQueries );
            }
            UtilsFacete.setFacetQuery( null, config, query );

            hits = doSearch( query, 0, 0, Settings.SEARCH_RANKED_HITS_PER_PAGE );
        } catch (ParseException e) {
            log.error("ParseExection Error.", e);
        }
        context.put( "facetsTyp", PortalConfig.getInstance().getList( PortalConfig.CATEGORY_TEASER_SEARCH_FACETS_TYP ) );
        if(categoryQuery != null){
            context.put( "categoryQuery", UtilsVelocity.urlencode(categoryQuery) );
        }
        context.put( "hits", hits );
        context.put( "config", config );
        context.put( "Codelists", CodeListServiceFactory.instance() );
        context.put( "enableFacete", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_ENABLE_SEARCH_FACETE, false ) );
        context.put( "languageCode", request.getLocale().getLanguage() );
        context.put( "enableRss", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_ENABLE_RSS, false ) );
        context.put( "maxColumn", PortalConfig.getInstance().getInt( PortalConfig.CATEGORY_TEASER_SEARCH_COLUMN_MAX, 6 ) );
        super.doView( request, response );
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {}

    private IngridHits doSearch(IngridQuery query, int startHit, int groupedStartHit, int hitsPerPage) {

        int currentPage = (startHit / hitsPerPage) + 1;

        if (groupedStartHit > 0) {
            startHit = groupedStartHit;
        }

        IngridHits hits = null;
        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            hits = ibus.search( query, hitsPerPage, currentPage, startHit, PortalConfig.getInstance().getInt( PortalConfig.QUERY_TIMEOUT_RANKED, 5000 ) );

            if (hits == null && log.isErrorEnabled()) {
                log.error( "Problems fetching details to hit list !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
            }
        } catch (Exception t) {
            if (log.isErrorEnabled()) {
                log.error( "Problems performing Search !", t );
            }
        }

        return hits;
    }
}
