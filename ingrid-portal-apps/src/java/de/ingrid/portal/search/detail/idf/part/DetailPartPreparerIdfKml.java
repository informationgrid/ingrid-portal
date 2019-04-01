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
package de.ingrid.portal.search.detail.idf.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.Settings;

public class DetailPartPreparerIdfKml extends DetailPartPreparer{

    @Override
    public void init(Node node, String iPlugId, RenderRequest request, RenderResponse response, Context context) {
        super.init( node, iPlugId, request, response, context );

        this.templateName = "/WEB-INF/templates/detail/part/detail_part_preparer_kml.vm";
        this.localTagName = "kml";
    }
    
    public Map getKML(){
        HashMap element = new HashMap();
        if (rootNode != null) {
            ArrayList<Object> elementsKml = new ArrayList<>();
            HashMap<String, Object> kmlDocument;
            Node childNode = xPathUtils.getNode(rootNode, "./kml:Document");
            
            kmlDocument = new HashMap<>();
            List<HashMap<String, String>> kmlDocumentPlacemark = new ArrayList<>();
            String docName =   xPathUtils.getString(childNode,"kml:name").trim();
            kmlDocument.put("kml_name", docName);
            boolean nodeExistPlacemark = xPathUtils.nodeExists(childNode, "kml:Placemark");
            if(nodeExistPlacemark){
                NodeList nodeListPlacemark = xPathUtils.getNodeList(childNode, "kml:Placemark");
                for(int j=0; j<nodeListPlacemark.getLength();j++){
                    Node nodePlacemark = nodeListPlacemark.item(j);
                    HashMap<String, String> placemark = new HashMap<>();
                    String placemarkName = xPathUtils.getString(nodePlacemark, "kml:name").trim();
                    placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE, placemarkName);
                    String placemarkDescription = xPathUtils.getString(nodePlacemark, "kml:description").trim();
                    placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR, placemarkDescription);
                    
                    String placemarkCoordinates = xPathUtils.getString(nodePlacemark, "kml:Point/kml:coordinates").trim();
                    if(placemarkCoordinates.indexOf(' ') > -1){
                        String [] coordinates = placemarkCoordinates.split(" ");
                        placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_X, coordinates[0]);
                        placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_Y, coordinates[1]);
                        kmlDocumentPlacemark.add(placemark);
                    }else if(placemarkCoordinates.indexOf(',') > -1){
                        String [] coordinates = placemarkCoordinates.split(",");
                        placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_X, coordinates[0]);
                        placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_Y, coordinates[1]);
                        kmlDocumentPlacemark.add(placemark);
                    }
                }
                kmlDocument.put("kml_placemark", kmlDocumentPlacemark);
                elementsKml.add(kmlDocument);
            }
            element = addKmlToContext(elementsKml, docName);
        }
        return element;
    }
    
    private HashMap addKmlToContext(List kml, String title) {
        HashMap element = new HashMap();
        element.put("type", "kml");
        element.put("title", title);
        String plugId = this.request.getParameter("plugid");
        String docId = this.request.getParameter("docid");
        if (plugId != null && docId != null) {
            element.put("docId", docId);
            element.put("plugId", plugId);
        }
        element.put("body", createTableOfPlacemark(kml));
        return element;
    }
    
    private ArrayList createTableOfPlacemark(List list) {
        
        ArrayList kmlTables = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            HashMap kmlTable = new HashMap();
            HashMap table = (HashMap) list.get(i);
            kmlTable.put("type", "table");
            kmlTable.put("subtitle", table.get("kml_name"));
            
            ArrayList head = new ArrayList();
            head.add(messages.getString("kml.placemark.title"));
            head.add(messages.getString("kml.placemark.description"));
            head.add(messages.getString("kml.placemark.longitude"));
            head.add(messages.getString("kml.placemark.latitude"));
            
            kmlTable.put("head", head);
            
            ArrayList body = new ArrayList();
            ArrayList listPlacemark = (ArrayList) table.get("kml_placemark");
            for (int j = 0; j < listPlacemark.size(); j++) {
                HashMap placemark = (HashMap) listPlacemark.get(j);
                ArrayList row = new ArrayList();
                row.add(notNull((String) placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE)));
                row.add(notNull((String) placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR)));
                row.add(notNull((String) placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_X)));
                row.add(notNull((String) placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_Y)));
                
                if (!isEmptyRow(row)) {
                    body.add(row);
                }
            }
            kmlTable.put("body", body);
            kmlTables.add(kmlTable);
        }
        
        return kmlTables;
    }
}
