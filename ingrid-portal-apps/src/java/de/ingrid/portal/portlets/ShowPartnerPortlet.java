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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
import de.ingrid.portal.search.UtilsSearch;
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

    private static final Logger log = LoggerFactory.getLogger(ShowPartnerPortlet.class);

    private static final String[] REQUESTED_FIELDS_ADDRESS = new String[] { "t02_address.id", "t02_address.adr_id", "title", "t02_address.lastname", "t02_address.firstname", "t02_address.address_key", "t02_address.address_value", "t02_address.title_key", "t02_address.title", "t021_communication.commtype_key", "t021_communication.commtype_value", "t021_communication.comm_value", "children.address_node.addr_type", "t02_address.parents.title", "partner", "provider" };
    
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    @Override
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
            try {
                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( partnerQuery ), REQUESTED_FIELDS_ADDRESS, IBUSInterfaceImpl.getInstance().getIBus() );
                ArrayList<String> addedInstitution = new ArrayList<>();
                TreeMap<String, HashMap<String, Object>> partnersMap = new TreeMap<>();
                
                while (it.hasNext()) {
                    IngridHit hit = it.next();
                    IngridHitDetail detail = hit.getHitDetail();

                    String[] partnersOfHit = (String[]) detail.get("partner");
                    for (String partnerOfHit : partnersOfHit) {
                        HashMap<String, Object> partnerProvider = partnersMap.get( partnerOfHit );
                        if(partnerProvider == null){
                            HashMap<String, Object> partner = new HashMap<>();
                            partner.put( "ident", partnerOfHit );
                            partner.put( "name", UtilsDB.getPartnerFromKey( partnerOfHit ) );
                            partnerProvider = new HashMap<>();
                            partnerProvider.put( "partner", partner );
                            partnerProvider.put( "providers", new ArrayList<>() );
                            partnersMap.put( partnerOfHit, partnerProvider );
                        }
                        
                        ArrayList<HashMap<String, HashMap<String, String>>> providers = (ArrayList<HashMap<String, HashMap<String, String>>>) partnerProvider.get( "providers" );
                        HashMap<String, String> provider = new HashMap<>();
                        String name = null;
                        if(detail.get("title") != null){
                            name = UtilsSearch.getDetailValue(detail, "title");
                            if(name != null && detail.get("t02_address.parents.title") != null) {
                                if(detail.get("t02_address.parents.title") instanceof String[]){
                                    name = UtilsSearch.getDetailValue(detail, "t02_address.parents.title") + "<br>" + name.trim();
                                } else {
                                    ArrayList<String> parentsTitle = (ArrayList<String>) detail.get("t02_address.parents.title");
                                    if(parentsTitle != null){
                                        for(String title : parentsTitle){
                                            name = title + "<br>" + name.trim();
                                        }
                                    }
                                }
                            }
                            provider.put( "name", name.trim());
                        }
                        
                        if(name != null && name.length() > 0){
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
                                    String typeKey = typeKeys.get(i);
                                    String typeValue = null;
                                    String value = null;
                                    if(typeValues.size() > i){
                                        typeValue = (typeValues.get(i)).toLowerCase();
                                        if(typeKey.equals("4") && typeValue.indexOf( "url" ) > -1 && values.size() > i){
                                            value = values.get(i);
                                        }
                                    }
                                    if(value != null){
                                        value = value.trim();
                                        if(!value.startsWith( "http" )){
                                            value = "http://" + value;
                                        }
                                        provider.put( "url", value );
                                    }
                                }
                            }
                            if(provider != null && provider.size() > 0 &&
                                    addedInstitution.indexOf( (detail.getPlugId() + "_" + detail.getId()).toLowerCase().trim() ) == -1){
                                HashMap<String, HashMap<String, String>> providerMap = new HashMap<>();
                                providerMap.put( "provider", provider );
                                providers.add( providerMap );
                                addedInstitution.add( (detail.getPlugId() + "_" + detail.getId()).toLowerCase().trim() );
                            }
                        }
                    }
                }
                Iterator<Map.Entry<String, HashMap<String, Object>>> itPartnerMap = partnersMap.entrySet().iterator();
                if(itPartnerMap != null){
                    while (itPartnerMap.hasNext()) {
                        Map.Entry<String, HashMap<String, Object>>  partner = itPartnerMap.next();
                        if(partner.getValue() != null){
                            ArrayList<HashMap<String, HashMap<String, String>>> providers = (ArrayList<HashMap<String, HashMap<String, String>>>) partner.getValue().get( "providers" );
                            if(!providers.isEmpty()){
                                Collections.sort(providers, new Comparator<HashMap>(){
                                    public int compare(HashMap left, HashMap right){
                                        String leftKey = ((HashMap<String, String>) left.get("provider")).get("name");
                                        String rightKey = ((HashMap<String, String>) right.get("provider")).get("name");
                                        return leftKey.toLowerCase().compareTo(rightKey.toLowerCase());
                                    }
                                });
                            }else{
                                itPartnerMap.remove();
                            }
                        }
                    }
                }
                context.put( "partners", partnersMap );
            } catch (ParseException e) {
                log.error( "Error by executing search with query: " + partnerQuery, e);
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
            } catch (Exception t) {
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
        ArrayList<String> array = new ArrayList<>();
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
