/*
 * **************************************************-
 * Ingrid Portal Apps
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.net.IBusQueryResultIterator;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class ShowPartnerPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(ShowPartnerPortlet.class);

    private static final String[] REQUESTED_FIELDS_ADDRESS = new String[] { "t02_address.id", "t02_address.adr_id", "title", "t02_address.lastname", "t02_address.firstname", "t02_address.address_key", "t02_address.address_value", "t02_address.title_key", "t02_address.title", "t021_communication.commtype_key", "t021_communication.commtype_value", "t021_communication.comm_value", "children.address_node.addr_type" };
    
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "showpartner.title");
        response.setTitle(messages.getString(titleKey));
        
        String[] hideIPlugIdList = PortalConfig.getInstance().getStringArray(PortalConfig.HIDE_IPLUG_ID_LIST);
        if(hideIPlugIdList != null){
        	context.put("hideIPlugIdList", hideIPlugIdList);
        }
               
        String partnerQuery = PortalConfig.getInstance().getString( PortalConfig.PORTAL_PARTNER_LIST_QUERY );
        if(partnerQuery != null && partnerQuery.length() > 0){
            ArrayList<HashMap<String, String>> addresses = new ArrayList<HashMap<String, String>>();
            try {
                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( partnerQuery ), REQUESTED_FIELDS_ADDRESS, IBUSInterfaceImpl.getInstance()
                        .getIBus() );
                ArrayList<String> addedInstitution = new ArrayList<String>();
                while (it.hasNext()) {
                    IngridHit hit = it.next();
                    IngridHitDetail detail = hit.getHitDetail();

                    HashMap<String, String> address = new HashMap<String, String>();
                    String institution = null;
                    /*
                    if(detail.get("t02_address.address_value") != null){
                        address.put( "address_value", ((String[])detail.get("t02_address.address_value"))[0]);
                    }
                    if(detail.get("t02_address.title") != null){
                        address.put( "title", ((String[])detail.get("t02_address.title"))[0]);
                    }
                    if(detail.get("t02_address.firstname") != null){
                        address.put( "firstname", ((String[])detail.get("t02_address.firstname"))[0]);
                    }
                    if(detail.get("t02_address.lastname") != null){
                        address.put( "lastname", ((String[])detail.get("t02_address.lastname"))[0]);
                    }
                    */
                    if(detail.get("title") != null){
                        institution = (String) ((ArrayList)detail.get("title")).get(0);
                        if(institution != null){
                            address.put( "institution", institution.trim());
                        }
                    }
                    
                    if(institution != null){
                        if(detail.get("t021_communication.commtype_key") != null 
                                && detail.get("t021_communication.commtype_value") != null
                                && detail.get("t021_communication.comm_value") != null){
                            
                            ArrayList<String> typeKeys = null;
                            ArrayList<String> typeValues = null;
                            ArrayList<String> values = null;
                            
                            typeKeys = getIndexValue(detail.get("t021_communication.commtype_key"));
                            typeValues = getIndexValue(detail.get("t021_communication.commtype_value"));
                            values = getIndexValue(detail.get("t021_communication.comm_value"));
                            
                            for (int i = 0; i < typeKeys.size(); i++) {
                                String typeKey = (String) typeKeys.get(i);
                                String typeValue = null;
                                String value = null;
                                if(typeValues.size() > i){
                                    typeValue = ((String)typeValues.get(i)).toLowerCase();
                                    if(typeKey.equals("4") && typeValue.indexOf( "url" ) > -1){
                                        if(values.size() > i){
                                            value = (String) values.get(i);
                                        }
                                    }
                                }
                                if(value != null){
                                    address.put( "url", value );
                                }
                            }
                        }
                        if(address != null && address.size() > 0){
                            if(addedInstitution.indexOf( institution.toLowerCase().trim() ) == -1){
                                addresses.add( address );
                                addedInstitution.add( institution.toLowerCase().trim() );
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                log.error( "Error by executing search with query: " + partnerQuery);
            }
            if(addresses.size() > 0){
                Collections.sort(addresses, new Comparator<HashMap>(){
                    public int compare(HashMap left, HashMap right){
                        String leftKey = (String) left.get("lastname");
                        if(leftKey == null || leftKey.length() == 0){
                            leftKey = (String) left.get("institution");
                        }
                        String rightKey = (String) right.get( "lastname" );
                        if(rightKey == null || rightKey.length() == 0){
                            rightKey = (String) right.get("institution");
                        }
                        return leftKey.toLowerCase().compareTo(rightKey.toLowerCase());
                    }
                });
                context.put( "addresses", addresses );
            }
        } else {
            try {
                String partnerRestriction = PortalConfig.getInstance().getString(
                        PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
                if (partnerRestriction == null || partnerRestriction.length() == 0) {
                    context.put("partners", UtilsDB.getPartnerProviderMap(null));
                } else {
                    ArrayList filter = new ArrayList();
                    filter.add(partnerRestriction);
                    context.put("partners", UtilsDB.getPartnerProviderMap(filter));
                }

                // set up plug list for view, remove plugs with same name !
                PlugDescription[] plugs = IBUSInterfaceImpl.getInstance().getAllActiveIPlugs();
                ArrayList plugDescriptions = new ArrayList();
                String newName = null;
                String oldName = null;
                for (int i = 0; i < plugs.length; i++) {
                    newName = plugs[i].getDataSourceName();
                    boolean found = false;
                    for (int j = 0; j < plugDescriptions.size(); j++) {
                        oldName = ((PlugDescription) plugDescriptions.get(j)).getDataSourceName();
                        if (newName != null && oldName != null && newName.equals(oldName)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        plugDescriptions.add(plugs[i]);
                    }
                }

                // sort alphabetical
                if (plugDescriptions != null) {
                    Collections.sort(plugDescriptions, new PlugNameComparator());
                }

                context.put("plugs", plugDescriptions);
            } catch (Throwable t) {
                if (log.isErrorEnabled()) {
                    log.error("Problems processing partner/provider and iPlugs.", t);
                }
            }
        }

        super.doView(request, response);
    }

    /**
     * Inner class: PlugNameComparator for plugs sorting;
     *
     * @author Martin Maidhof
     */
    private class PlugNameComparator implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public final int compare(Object a, Object b) {
            try {
                String aName = ((PlugDescription) a).getDataSourceName().toLowerCase();
                String bName = ((PlugDescription) b).getDataSourceName().toLowerCase();

                return aName.compareTo(bName);
            } catch (Exception e) {
                return 0;
            }
        }
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