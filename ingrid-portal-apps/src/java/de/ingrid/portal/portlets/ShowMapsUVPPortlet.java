/*
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.net.IBusQueryResultIterator;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.queryparser.QueryStringParser;

public class ShowMapsUVPPortlet extends ShowMapsPortlet {

    private final static Logger log = LoggerFactory.getLogger(ShowMapsUVPPortlet.class);

    private static final String[] REQUESTED_FIELDS_MARKER = new String[] { "lon_center", "lat_center", "t01_object.obj_id", "uvp_category", "uvp_number", "t01_object.obj_class", "uvp_steps"};
    private static final String[] REQUESTED_FIELDS_BBOX = new String[] { "x1", "x2", "y1", "y2", "t01_object.obj_id" };

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        
        IngridSysCodeList sysCodeList = new IngridSysCodeList(request.getLocale());
        try {
            if (resourceID.equals( "marker" )) {
                response.setContentType( "application/javascript" );
                response.getWriter().write( "var markers = [" );
                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "datatype:metadata ranking:score") ), REQUESTED_FIELDS_MARKER, IBUSInterfaceImpl.getInstance()
                        .getIBus() );
                while (it.hasNext()) {
                    StringBuilder s = new StringBuilder();
                    IngridHit hit = it.next();
                    IngridHitDetail detail = hit.getHitDetail();
                    Object[] lat_center = (Object[]) detail.get( "lat_center" );
                    Object[] lon_center = (Object[]) detail.get( "lon_center" );
                    if (lat_center != null && lon_center != null) {
                        s.append( "[" )
                            .append( lat_center[0].toString() ).append( "," )
                            .append( lon_center[0].toString() ).append( ",'" )
                            .append( detail.get( "title" ).toString() ).append( "','" )
                            .append( UtilsSearch.getDetailValue( detail, "t01_object.obj_id" ) ).append( "','" )
                            .append( UtilsSearch.getDetailValue( detail, "t01_object.obj_class" ) ).append( "','" )
                            .append( sysCodeList.getName( "8001", UtilsSearch.getDetailValue( detail, "t01_object.obj_class" )) ).append( "'");
                        
                        if(detail.get( "uvp_category" ) != null){
                            ArrayList<String> categories = getIndexValue(detail.get( "uvp_category" ));
                            s.append( "," ).append( "[" );
                            if(categories != null && categories.size() > 0){
                                int index = 0;
                                s.append( "'" );
                                for (String category : categories) {
                                    s.append( messages.getString( "searchResult.categories.uvp." + category.trim() ) );
                                    if(index < categories.size() - 1){
                                        s.append( "','" );
                                    }else{
                                        s.append( "'" );
                                    }
                                    index ++;
                                }
                            }
                            s.append( "]" );
                        }else{
                            s.append( "," ).append( "[" );
                            s.append( "]" );
                        }
                        
                        if(detail.get( "uvp_steps" ) != null){
                            ArrayList<String> steps = getIndexValue(detail.get( "uvp_steps" ));
                            s.append( "," ).append( "[" );
                            if(steps != null && steps.size() > 0){
                                int index = 0;
                                s.append( "'" );
                                for (String step : steps) {
                                    s.append( messages.getString( "common.steps.uvp." + step.trim() ) );
                                    if(index < steps.size() - 1){
                                        s.append( "','" );
                                    }else{
                                        s.append( "'" );
                                    }
                                    index ++;
                                }
                            }
                            s.append( "]" );
                        }else{
                            s.append( "," ).append( "[" );
                            s.append( "]" );
                        }
                        
                        s.append( "]" );
                        if (it.hasNext()) {
                            s.append( "," );
                        }
                        response.getWriter().write( s.toString() );
                    }
                }
                response.getWriter().write( "];" );
            }
            if (resourceID.equals( "bbox" )) {
                String uuid = request.getParameter( "uuid" );
                response.setContentType( "application/javascript" );
                response.getWriter().write( "[" );
                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( "t01_object.obj_id:" + uuid + " ranking:score" ), REQUESTED_FIELDS_BBOX,
                        IBUSInterfaceImpl.getInstance().getIBus() );
                if (it.hasNext()) {
                    StringBuilder s = new StringBuilder();
                    IngridHit hit = it.next();
                    IngridHitDetail detail = hit.getHitDetail();
                    Object[] x1 = (Object[]) detail.get( "x1" );
                    Object[] y1 = (Object[]) detail.get( "y1" );
                    Object[] x2 = (Object[]) detail.get( "x2" );
                    Object[] y2 = (Object[]) detail.get( "y2" );
                    
                    if (y1 != null && x2 != null) {
                        s.append("[").append( y1[0].toString() ).append( "," ).append( x1[0].toString() ).append( "],[" ).append( y2[0].toString() )
                                .append( "," ).append( x2[0].toString() ).append("]");
                        response.getWriter().write( s.toString() );
                    }
                }
                response.getWriter().write( "]" );
            }

        } catch (Exception e) {
            log.error( "Error creating resource for resource ID: " + resourceID, e );
        }
    }
    
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        // define an REST URL to get the map data dynamically
        ResourceURL restUrl = response.createResourceURL();
        restUrl.setResourceID( "marker" );
        request.setAttribute( "restUrlMarker", restUrl.toString() );
        restUrl.setResourceID( "bbox" );
        request.setAttribute( "restUrlBBOX", restUrl.toString() );
        
        super.doView(request, response);
    }
    
    private ArrayList<String> getIndexValue(Object obj){
        ArrayList<String> array = new ArrayList<String>();
        if(obj instanceof String[]){
            String [] tmp = (String[]) obj;
            for (String s : tmp) {
                array.add( s );
            }
        }else if(obj instanceof ArrayList){
            array = (ArrayList) obj;
        }else {
            array.add( obj.toString() );
        }
        return array;
    }
}
