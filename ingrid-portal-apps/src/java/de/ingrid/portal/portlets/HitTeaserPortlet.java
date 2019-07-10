/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.CodeListServiceFactory;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

public class HitTeaserPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger( HitTeaserPortlet.class );

    @Override
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException {
        Context context = getContext( request );

        IngridResourceBundle messages = new IngridResourceBundle( getPortletConfig().getResourceBundle( request.getLocale() ), request.getLocale() );
        context.put( "MESSAGES", messages );

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue( "titleKey", "" );
        response.setTitle(messages.getString(titleKey));
        
        IngridQuery query;
        IngridHits hits = null;
        String hitQuery = null;
        try {
            hitQuery = PortalConfig.getInstance().getString( PortalConfig.HIT_TEASER_SEARCH_QUERY, "" ).trim();
            query = QueryStringParser.parse( hitQuery );
            hits = doSearch( query, 0, 0, PortalConfig.getInstance().getInt( PortalConfig.HIT_TEASER_SEARCH_COUNT, 2 ) );
        } catch (ParseException e) {
            log.error("ParseExection Error.", e);
        }
        if(hits != null) {
            context.put( "hits", hits.getHits() );
        }
        context.put( "Codelists", CodeListServiceFactory.instance() );
        context.put( "languageCode", request.getLocale().getLanguage() );
        context.put( "UtilsSearch", new UtilsSearch() );
        
        context.put( "leafletBgLayerWMTS", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMTS));
        context.put( "leafletBgLayerAttribution", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_ATTRIBUTION));
        
        String [] leafletBgLayerWMS = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMS);
        String leafletBgLayerWMSURL = leafletBgLayerWMS[0];
        if(leafletBgLayerWMSURL.length() > 0 && leafletBgLayerWMS.length > 1){
            context.put( "leafletBgLayerWMSUrl", leafletBgLayerWMSURL);
            StringBuilder leafletBgLayerWMSName = new StringBuilder("");
            for (int i = 1; i < leafletBgLayerWMS.length; i++) {
                leafletBgLayerWMSName.append(leafletBgLayerWMS[i]);
                if(i < (leafletBgLayerWMS.length - 1)) {
                    leafletBgLayerWMSName.append(",");
                }
            }
            context.put( "leafletBgLayerWMSName", leafletBgLayerWMSName.toString());
        }
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
            hits = ibus.searchAndDetail( query, hitsPerPage, currentPage, startHit, 
                    PortalConfig.getInstance().getInt( PortalConfig.QUERY_TIMEOUT_RANKED, 5000 ),
                    PortalConfig.getInstance().getStringArray( PortalConfig.HIT_TEASER_SEARCH_REQUESTEDFIELDS ) );

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
