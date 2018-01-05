/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.utils.capabilities.CapabilitiesUtils;
import de.ingrid.utils.capabilities.CapabilitiesUtils.ServiceType;
import de.ingrid.utils.udk.UtilsDate;

public class DetailPartPreparerIdfMetadata extends DetailPartPreparer{

    @Override
    public void init(Node node, String iPlugId, RenderRequest request, RenderResponse response, Context context) {
        super.init( node, iPlugId, request, response, context );

        this.templateName = "/WEB-INF/templates/detail/part/detail_part_preparer_metadata.vm";
        this.localTagName = "idfMdMetadata";
    }
    
    public String getUdkObjectClassType() {
        String xpathExpression = ".";
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if(node.hasChildNodes()){
                String hierachyLevel = "";
                String hierachyLevelName = "";
                
                xpathExpression = "./gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue";
                if(XPathUtils.nodeExists(node, xpathExpression)){
                    hierachyLevel = XPathUtils.getString(node, xpathExpression).trim();
                }
                
                xpathExpression = "./gmd:hierarchyLevelName/gco:CharacterString";
                if(XPathUtils.nodeExists(node, xpathExpression)){
                    hierachyLevelName = XPathUtils.getString(node, xpathExpression).trim();
                }
                
                if(hierachyLevel.equalsIgnoreCase("service")){
                    return "3";
                }else if(hierachyLevel.equalsIgnoreCase("application")){
                    return "6";
                }else if(hierachyLevelName.equalsIgnoreCase("job") && hierachyLevel.equals("nonGeographicDataset")){
                    return "0";
                }else if(hierachyLevelName.equalsIgnoreCase("document") && hierachyLevel.equals("nonGeographicDataset")){
                    return "2";
                }else if(hierachyLevelName.equalsIgnoreCase("project") && hierachyLevel.equals("nonGeographicDataset")){
                    return "4";
                }else if(hierachyLevelName.equalsIgnoreCase("database") && hierachyLevel.equals("nonGeographicDataset")){
                    return "5";
                }else if(hierachyLevel.equalsIgnoreCase("dataset") || hierachyLevel.equals("series")){
                    return "1";
                }else if(hierachyLevel.equalsIgnoreCase("tile")){
                    // tile should be mapped to "Geoinformation/Karte" explicitly, see INGRID-2225
                    return "1";
                } else {
                    // Default to "Geoinformation/Karte", see INGRID-2225
                    return "1";
                }
            }
        }
        return "1"; 
    }
    
    public String getTitle(){
        String value = null;
        String xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title";
        Node node = XPathUtils.getNode(this.rootNode, xpathExpression);
        if(node != null && node.getTextContent().length() > 0){
            value = node.getTextContent();
        }
        return value;
    }
    
    public String getTimeStamp(String xpathExpression){
        String value = null;
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            String modTime = "";
            if(XPathUtils.nodeExists(node, "./gco:DateTime")){
                modTime = getDateFormatValue(XPathUtils.getString(node, "./gco:DateTime").trim());
            }else if(XPathUtils.nodeExists(node, "./gco:Date")){
                modTime = getDateFormatValue(XPathUtils.getString(node, "./gco:Date").trim());
            }else {
                modTime = getDateFormatValue(XPathUtils.getString(node, ".").trim());
            }
            if(modTime.length() > 0){
                value = modTime;
            }
        }
        return value;
    }
    
    public String getDescription(){
        String value = null;
        
        String xpathExpressionAbstract = "./idf:abstract";
        Node abstractParentNode = rootNode;
        Node node = XPathUtils.getNode(rootNode, "./gmd:identificationInfo/*");
        
        if (!XPathUtils.nodeExists(abstractParentNode, xpathExpressionAbstract)) {
            xpathExpressionAbstract = "./gmd:abstract";
            abstractParentNode = node;
        }
        value = XPathUtils.getString(abstractParentNode, xpathExpressionAbstract);
        if (value != null) {
            value = value.trim();
        }

        return value;
    }
    
    public HashMap<String, Object> getMapImage(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        String xpathExpression = "";
        // showMap/Preview-Link
        
        xpathExpression = "./idf:crossReference/idf:serviceUrl";

        if ( getUdkObjectClassType().equals("1") ) {
            // first try to get any valid WMS url from the crossReference section
            String getCapUrl = getCapabilityUrlFromCrossReference( null );
            if ( getCapUrl != null ) {
                String url = "";
                
                // since this link will be going to the webmap-client, the service must be WMS!
                getCapUrl += CapabilitiesUtils.getMissingCapabilitiesParameter( getCapUrl, ServiceType.WMS );
                
                url = UtilsVelocity.urlencode( getCapUrl ) + "||";
                
                if(!getLayerIdentifier(null).equals("NOT_FOUND")) {
                    url = url + "" + UtilsVelocity.urlencode(getLayerIdentifier(null));
                }
                
                if(getCapUrl.length() > 0) {
                    map = addBigMapLink(url, false);
                }
                
            } else {
                // search for it in onlineResources
                xpathExpression = "./gmd:distributionInfo/*/gmd:transferOptions";
                map = getCapabilityUrls(xpathExpression);
            }
            
        // otherwise try within distribution info or SV_OperationMetadata
        } else if ( getUdkObjectClassType().equals("3") ) {
            String capabilitiesUrl = getCapabilityUrl();
            if(capabilitiesUrl != null){
                capabilitiesUrl += CapabilitiesUtils.getMissingCapabilitiesParameter( capabilitiesUrl, ServiceType.WMS );
                if (capabilitiesUrl != null) {
                    // get it directly from the operation
                    map = addBigMapLink(capabilitiesUrl + "||", true);
                }                
            }
        } else {
            // show preview image (with map link information if provided)
            xpathExpression = "./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/gco:CharacterString";
            map = getPreviewImage(xpathExpression);
        }
        
        return map;
    }
    
    public ArrayList<HashMap<String, Object>> getReference(String xpathExpression){
        return getReference(xpathExpression, false); 
    }
        
    public ArrayList<HashMap<String, Object>> getReference(String xpathExpression, boolean isCoupled){
        ArrayList<HashMap<String, Object>> linkList = new ArrayList<HashMap<String, Object>>();
        
        int limitReferences = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_DETAIL_VIEW_LIMIT_REFERENCES, 100);
        
        String serviceType = null;
        
        serviceType = XPathUtils.getString(rootNode, "./gmd:identificationInfo/*/srv:serviceType");
        if(serviceType != null){
            serviceType = serviceType.trim();
        }
        
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength();i++){
                
                if (linkList.size() >= limitReferences){
                    HashMap<String, Object> link = new HashMap<String, Object>();
                    link.put("type", "html");
                    link.put("body", messages.getString("info_limit_references"));
                    linkList.add(link);
                    break;
                }
                Node node = nodeList.item(i);
                String uuid = "";
                String title = "";
                String type = getUdkObjectClassType();
                String attachedToField = "";
                String entryId = "";
                String description = "";
                String serviceUrl = null;
                String tmp = null;
                
                xpathExpression = "./@uuid";
                tmp = XPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    uuid = tmp.trim();
                }
                
                xpathExpression = "./idf:objectName";
                tmp = XPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    title = tmp.trim();
                }
                
                xpathExpression = "./idf:objectType";
                tmp = XPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    type = tmp.trim();
                }
                
                xpathExpression = "./idf:attachedToField";
                tmp = XPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    attachedToField = tmp.trim();
                }
                
                xpathExpression = "./idf:attachedToField/@entry-id";
                tmp = XPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    entryId = tmp.trim();
                }
                
                xpathExpression = "./idf:description";
                tmp = XPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    description = tmp.trim();
                }

                xpathExpression = "./idf:serviceType";
                tmp = XPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    serviceType = tmp.trim();
                }
                
                xpathExpression = "./idf:serviceUrl";
                tmp = XPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    serviceUrl = tmp.trim();
                }
                
                HashMap<String, Object> link = new HashMap<String, Object>();
                link.put("hasLinkIcon", new Boolean(true));
                link.put("isExtern", new Boolean(false));
                link.put("title", title);
                link.put("objectClass", type);
                if (description.length() > 0) {
                    link.put("description", description);
                }
                if (attachedToField.length() > 0) {
                    link.put("attachedToField", attachedToField);
                }
                if(this.iPlugId != null){
                    if(uuid != null){
                        link.put("href", "?cmd=doShowDocument&docuuid="+uuid+"&plugid="+this.iPlugId);
                    }else{
                        link.put("href", "");
                    }
                }else{
                    link.put("href", "");
                }
                
                if(isCoupled){
                    // add map links to data objects from services
                    if (entryId.equals("3600") && type.equals("3")) {
                        // get link from operation (unique one)
                        if (serviceType != null && serviceType.trim().equals("view")) {
                            String capabilityUrl = serviceUrl;
                            if(serviceUrl == null){
                                capabilityUrl = getCapabilityUrl();
                            }
                            if ( capabilityUrl != null ) {
                                capabilityUrl += CapabilitiesUtils.getMissingCapabilitiesParameter( capabilityUrl, ServiceType.WMS );
                                link.put("mapLink", UtilsVelocity.urlencode(capabilityUrl) + "||" + UtilsVelocity.urlencode(getLayerIdentifier(node)));
                            }
                        }
                        // do not show link relation for coupled resources (INGRID-2285)
                        link.remove("attachedToField");
                        linkList.add(link);
                    } else if (entryId.equals("3600") && type.equals("1")) {
                        String capUrl = serviceUrl;
                        if(serviceUrl == null){
                            capUrl = getCapabilityUrlFromCrossReference( uuid );
                        }
                        if ( capUrl != null ) {
                            // add possible missing parameters
                            capUrl += CapabilitiesUtils.getMissingCapabilitiesParameter( capUrl );
                            link.put("mapLink",  UtilsVelocity.urlencode(capUrl) + "||" + UtilsVelocity.urlencode(getLayerIdentifier(node)));
                        }
                        // do not show link relation for coupled resources (INGRID-2285)
                        link.remove("attachedToField");
                        linkList.add(link);
                    }
                }else{
                    if(!entryId.equals("3600")){
                        linkList.add(link);
                    }
                }
            }
        }
        return linkList;
    }
    
    public ArrayList<HashMap<String, Object>> getExternLinks(String xpathExpression) {
        ArrayList<HashMap<String, Object>> linkList = new ArrayList<HashMap<String, Object>>();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength();i++){
                String url = "";
                String name = "";
                String description = "";
                String attachedToField = "";
                String size = "";
                float roundSize = 0;
                
                NodeList onLineList = XPathUtils.getNodeList(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine");
                
                for (int j=0; j<onLineList.getLength();j++){
                    
                //if(XPathUtils.nodeExists(nodeList.item(i),"./gmd:MD_DigitalTransferOptions/gmd:onLine")){
                    xpathExpression = "./*/gmd:linkage/gmd:URL";
                    if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        url = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }
                    
                    xpathExpression = "./*/gmd:description";
                    if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        description = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }
                    
                    xpathExpression = "./*/gmd:name";
                    if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        name = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }
                    
                    xpathExpression = "./*/idf:attachedToField";
                    if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        attachedToField = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }

                    // also mapped by T0112_media_option !!!
                    xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:transferSize";
                    if(XPathUtils.nodeExists(nodeList.item(i), xpathExpression)){
                        size = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                        roundSize = Float.valueOf(size).floatValue();
                        roundSize = (float) (Math.round(roundSize * 1000) / 1000.0);
                    }

                    if(url.length() > 0){
                        HashMap<String, Object> link = new HashMap<String, Object>();
                        link.put("hasLinkIcon", new Boolean(true));
                          link.put("isExtern", new Boolean(true));
                          link.put("href", url);

                          if(name.length() > 0){
                              link.put("title", name);
                            }else{
                                link.put("title", url);
                          }
                          if (description.length() > 0) {
                              link.put("description", description);
                          }
                          if (attachedToField.length() > 0) {
                              link.put("attachedToField", attachedToField);
                          }
                          if (size.length() > 0) {
                              link.put("linkInfo", "[" + roundSize + " MB]");
                          }
                          
                          linkList.add(link);
                    }
                }
            }
        }
        return linkList;
    }
    
    public ArrayList<String> getIndexInformationKeywords(String xpathExpression, String keywordType) {
        ArrayList<String> listSearch = new ArrayList<String>();
        ArrayList<String> listGemet = new ArrayList<String>();
        ArrayList<String> listInspire = new ArrayList<String>();
        
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String type = "";
                String thesaurusName = "";
                
                // type
                xpathExpression = "./gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    type = XPathUtils.getString(node, xpathExpression);
                }
                
                // thesaurus
                xpathExpression = "./gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    thesaurusName = XPathUtils.getString(node, xpathExpression).trim();
                }
                
                // keywords
                xpathExpression = "./gmd:MD_Keywords/gmd:keyword";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList keywordNodeList = XPathUtils.getNodeList(node, xpathExpression);
                    for (int j = 0; j < keywordNodeList.getLength(); j++) {
                        Node keywordNode = keywordNodeList.item(j);
                        String value = XPathUtils.getString(keywordNode, ".").trim(); 
                        if(value.length() < 1){
                            value = XPathUtils.getString(keywordNode, ".").trim();
                        }
                        boolean isHidden = false;
                        
                        if(value != null){
                            List hiddenKeywordList = PortalConfig.getInstance().getList(PortalConfig.PORTAL_DETAIL_VIEW_HIDDEN_KEYWORDS);
                            if(hiddenKeywordList != null){
                                for(int h=0; h < hiddenKeywordList.size(); h++){
                                    String hiddenValue = (String) hiddenKeywordList.get(h);
                                    if(value.toLowerCase().equals(hiddenValue.toLowerCase())){
                                        isHidden = true; 
                                        break;
                                    }
                                }
                            }
                            
                            if(isHidden == false){
                                // "Service Classification, version 1.0"
                                if (thesaurusName.indexOf("Service") < 0) {
                                    // "UMTHES Thesaurus"
                                    if (thesaurusName.indexOf("UMTHES") > -1) {
                                        listSearch.add(value);
                                    // "GEMET - Concepts, version 2.1"
                                    } else if (thesaurusName.indexOf("Concepts") > -1) {
                                        String tmpValue = sysCodeList.getNameByCodeListValue("5200", value);
                                        if(tmpValue.length() < 1){
                                            tmpValue = value;
                                        }
                                        listGemet.add(tmpValue);
                                        // "GEMET - INSPIRE themes, version 1.0"
                                    } else if (thesaurusName.indexOf("INSPIRE") > -1) {
                                        String tmpValue = sysCodeList.getNameByCodeListValue("6100", value);
                                        if(tmpValue.length() < 1){
                                            tmpValue = value;
                                        }
                                        listInspire.add(tmpValue);
                                        // "German Environmental Classification - Category, version 1.0"
                                    } else if (thesaurusName.indexOf("German Environmental Classification") > -1) {
                                        // do not used in detail view.
                                        
                                    } else if (thesaurusName.length() < 1 && type.length() < 1) {
                                        listSearch.add(value);
                                    } else{
                                        listSearch.add(value);
                                    }    
                                }
                            }
                        }
                    }
                }
            }
        }
        if(keywordType.equals("search")){
            sortList(listSearch);
            return listSearch;
        }else if(keywordType.equals("gemet")){
            sortList(listGemet);
            return listGemet;
        }else if(keywordType.equals("inspire")){
            sortList(listInspire);
            return listInspire;
        }else{
            sortList(listSearch);
            return listSearch;
        }
    }
    
    public HashMap getAvailability(String xpathExpression) {
        HashMap element = new HashMap();
        
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if (node.hasChildNodes()) {
                xpathExpression = "./gmd:distributionFormat/gmd:MD_Format";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList nodeList = XPathUtils.getNodeList(node, xpathExpression);
                    element.put("type", "table");
                    element.put("title", messages.getString("data_format"));
                    
                    ArrayList head = new ArrayList();
                    head.add(messages.getString("t0110_avail_format.name"));
                    head.add(messages.getString("t0110_avail_format.version"));
                    head.add(messages.getString("t0110_avail_format.file_decompression_technique"));
                    head.add(messages.getString("t0110_avail_format.specification"));
                    element.put("head", head);
                    ArrayList body = new ArrayList();
                    element.put("body", body);
                    
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node childNode = nodeList.item(i);
                        ArrayList row = new ArrayList();
                        
                        String name = "";
                        String version = "";
                        
                        xpathExpression = "./gmd:name";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            name = XPathUtils.getString(childNode, xpathExpression).trim();
                        }
                        
                        xpathExpression = "./gmd:version";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            version = XPathUtils.getString(childNode, xpathExpression).trim();
                        }
                        
                        if(!name.equals("Geographic Markup Language (GML)") && !version.equals("unknown")){
                            xpathExpression = "./gmd:name";
                            if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                                row.add(notNull(name));
                            } else {
                                row.add("");
                            }
                            
                            xpathExpression = "./gmd:version";
                            if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                                row.add(notNull(version));
                            } else {
                                row.add("");
                            }
                            
                            xpathExpression = "./gmd:fileDecompressionTechnique";
                            if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                                String value = XPathUtils.getString(childNode, xpathExpression).trim();
                                row.add(notNull(value));
                            } else {
                                row.add("");
                            }
                            
                            xpathExpression = "./gmd:specification";
                            if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                                String value = XPathUtils.getString(childNode, xpathExpression).trim();
                                row.add(notNull(value));
                            } else {
                                row.add("");
                            }
                        }
                        
                        if (!isEmptyRow(row)) {
                            body.add(row);
                        }
                    }
                }
            }
        }
        return element;
    }

    public HashMap getMediumOptions(String xpathExpression) {
        HashMap element = new HashMap();
        
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            xpathExpression = "./gmd:transferOptions";
            if (XPathUtils.nodeExists(node, xpathExpression)) {
                NodeList nodeList = XPathUtils.getNodeList(node, xpathExpression);
                element.put("type", "table");
                element.put("title", messages.getString("t0112_media_option.medium"));
                ArrayList head = new ArrayList();
                head.add(messages.getString("t0112_media_option.medium_name"));
                head.add(messages.getString("t0112_media_option.transfer_size") + " [MB]");
                head.add(messages.getString("t0112_media_option.medium_note"));
                element.put("head", head);
                ArrayList body = new ArrayList();
                element.put("body", body);
                
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nodeListItem = nodeList.item(i);
                    ArrayList row = new ArrayList();
                    if(XPathUtils.nodeExists(nodeListItem, "./gmd:MD_DigitalTransferOptions/gmd:offLine")){
                        
                        xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:name/gmd:MD_MediumNameCode/@codeListValue";
                        if (XPathUtils.nodeExists(nodeListItem, xpathExpression)) {
                            String value = XPathUtils.getString(nodeListItem, xpathExpression).trim();
                            row.add(notNull(sysCodeList.getNameByCodeListValue("520", value)));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:transferSize";
                        if (XPathUtils.nodeExists(nodeListItem, xpathExpression)) {
                            row.add(notNull(XPathUtils.getString(nodeListItem, xpathExpression)).trim());
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:mediumNote";
                        if (XPathUtils.nodeExists(nodeListItem, xpathExpression)) {
                            row.add(notNull(XPathUtils.getString(nodeListItem, xpathExpression)).trim());
                        } else {
                            row.add("");
                        }
                        
                        if (!isEmptyRow(row)) {
                            body.add(row);
                        }
                    }
                }
            }
        }
        return element;
    }
    
    public HashMap getConformityData(String xpathExpression) {
        HashMap element = new HashMap();
        
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            element.put("type", "table");
            element.put("title", messages.getString("object_conformity"));
            
            ArrayList head = new ArrayList();
            head.add(messages.getString("object_conformity.specification"));
            head.add(messages.getString("object_conformity.degree_value"));
            head.add(messages.getString("object_conformity.publication_date"));
            element.put("head", head);
            ArrayList body = new ArrayList();
            element.put("body", body);
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if(XPathUtils.nodeExists(node, "./gmd:DQ_DomainConsistency")){
                    ArrayList row = new ArrayList();
                    
                    xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:title";
                    if (XPathUtils.nodeExists(node, xpathExpression)) {
                        String value = XPathUtils.getString(node, xpathExpression).trim();
                        row.add(notNull(value));
                    } else {
                        row.add("");
                    }
                    
                    xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:pass";
                    if (XPathUtils.nodeExists(node, xpathExpression)) {
                        String value = XPathUtils.getString(node, xpathExpression).trim();
                        if(XPathUtils.nodeExists(node, xpathExpression + "/@gco:nilReason")){
                            row.add(notNull(sysCodeList.getName("6000", "3")));    
                        }else{
                            if(value.equals("true")){
                                row.add(notNull(sysCodeList.getName("6000", "1")));    
                            }else if(value.equals("false")){
                                row.add(notNull(sysCodeList.getName("6000", "2")));    
                            }else{
                                row.add("");
                            }
                        }
                    } else {
                        row.add("");    
                    }
                    
                    xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date";
                    if (XPathUtils.nodeExists(node, xpathExpression)) {
                        if(XPathUtils.nodeExists(node, xpathExpression + "/@gco:nilReason")){
                            row.add("");
                        }else {
                            String value = XPathUtils.getString(node, xpathExpression).trim();
                            row.add(notNull(getDateFormatValue(value)));
                        }
                    } else {
                        row.add("");
                    }
                    
                    if (!isEmptyRow(row)) {
                        body.add(row);
                    }
                }
            }
        }
        return element;
    }
    
    
    public ArrayList getServiceClassification(String xpathExpression) {
        ArrayList list = new ArrayList();
        
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String thesaurusName = "";
                
                // thesaurus
                xpathExpression = "./gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    thesaurusName = XPathUtils.getString(node, xpathExpression).trim();
                }
                
                // keywords
                xpathExpression = "./gmd:MD_Keywords/gmd:keyword";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList keywordNodeList = XPathUtils.getNodeList(node, xpathExpression);
                    for (int j = 0; j < keywordNodeList.getLength(); j++) {
                        Node keywordNode = keywordNodeList.item(j);
                        String value = XPathUtils.getString(keywordNode, ".").trim(); 
                        if(value.length() < 1){
                            value = XPathUtils.getString(keywordNode, ".").trim();
                        }
                        
                        // "Service Classification, version 1.0"
                        if (thesaurusName.indexOf("Service") > -1) {
                            String tmpValue = sysCodeList.getNameByCodeListValue("5200", value);
                            if(tmpValue.length() > 0){
                                value = tmpValue;
                            }
                            list.add(value);
                        } 
                    }
                }
            }
        }
        sortList(list);
        return list;
    }
    
    public HashMap<String, Object> getReferenceObject(String xpathExpression) {
        HashMap element = new HashMap();
        
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList childNodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList<String> listDominator = new ArrayList<String>();
            ArrayList<String> listMeter = new ArrayList<String>();
            ArrayList<String> listDpi = new ArrayList<String>();
            for (int j = 0; j < childNodeList.getLength(); j++) {
                xpathExpression = "./gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator";
                if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
                    listDominator.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
                }
                
                xpathExpression = "./gmd:distance/gco:Distance[@uom='meter']";
                if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
                    listMeter.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
                }
                
                xpathExpression = "./gmd:distance/gco:Distance[@uom='dpi']";
                if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
                    listDpi.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
                }
            }
            
            element.put("type", "table");
            element.put("title", messages.getString("t011_obj_serv_scale"));
            
            ArrayList head = new ArrayList();
            head.add(messages.getString("t011_obj_serv_scale.scale").concat(" 1:x"));
            head.add(messages.getString("t011_obj_serv_scale.resolution_ground").concat(" m"));
            head.add(messages.getString("t011_obj_serv_scale.resolution_scan").concat(" dpi"));
            element.put("head", head);
            ArrayList body = new ArrayList();
            element.put("body", body);
            
            for (int i = 0; i < listDominator.size(); i++) {
                ArrayList row = new ArrayList();
                int value;
                
                value = listDominator.size();
                if (value != 0) {
                    if(value > i){
                        row.add(notNull((String) listDominator.get(i)));
                    }else{
                        row.add("");
                    }
                } else {
                    row.add("");
                }
                
                value = listMeter.size();
                if (value != 0) {
                    if(value > i){
                        row.add(notNull((String) listMeter.get(i)));
                    }else {
                        row.add("");
                    }
                } else {
                    row.add("");
                }
                
                value = listDpi.size();
                if (value != 0) {
                    if(value > i){
                        row.add(notNull((String) listDpi.get(i)));
                    }else{
                        row.add("");
                    }
                } else {
                    row.add("");
                }
                
                if (!isEmptyRow(row)) {
                    body.add(row);
                }
            }
        }
        return element;
    }
    
    public HashMap getConnectionPoints(String xpathExpression) {
        String serviceType = getValueFromXPath("./gmd:identificationInfo/*/srv:serviceType");
        if(serviceType != null){
            serviceType = serviceType.trim();
        }
        HashMap element = new HashMap();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            
            for (int i=0; i<nodeList.getLength();i++){
                if(XPathUtils.nodeExists(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL")){
                    Node node = XPathUtils.getNode(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL");                
                    String urlValue = XPathUtils.getString(node, ".").trim();
                    // do not display empty URLs
                    if (urlValue == null || urlValue.length() == 0) {
                        continue;
                    }

                    // do not display empty operations
                    String operationName = XPathUtils.getString(nodeList.item(i), "./../srv:operationName").trim();
                    if (operationName == null || operationName.length() == 0) {
                        continue;
                    }

                    if (operationName.equals("GetCapabilities")) {
                        // add missing parameters
                        urlValue += CapabilitiesUtils.getMissingCapabilitiesParameter( urlValue, serviceType );

                        element.put("type", "textLabelLeft");
                        element.put("line", true);

                        HashMap elementCapabilitiesLink = new HashMap();
                        elementCapabilitiesLink.put("type", "linkLine");
                        elementCapabilitiesLink.put("hasLinkIcon", new Boolean(true));
                        elementCapabilitiesLink.put("isExtern", new Boolean(true));
                        elementCapabilitiesLink.put("title", urlValue);
                        elementCapabilitiesLink.put("href", urlValue);
                          element.put("body", elementCapabilitiesLink);

                        if (!hasAccessConstraints()) {
                            element.put("title", messages.getString("common.result.showGetCapabilityUrl"));
                            if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false) && (serviceType != null && serviceType.equals("view"))){
                                  HashMap elementMapLink = new HashMap();
                                elementMapLink.put("type", "linkLine");
                                elementMapLink.put("hasLinkIcon", new Boolean(true));
                                elementMapLink.put("isExtern", new Boolean(false));
                                elementMapLink.put("title", messages.getString("common.result.showMap"));
                                elementMapLink.put("href", "portal/main-maps.psml?layers=WMS||" + UtilsVelocity.urlencode(urlValue) + "||");
                                  element.put("link", elementMapLink);
                                element.put("linkLeft", true);
                            }
                        } else {
                            // do not display "show in map" link if the map has access constraints
                            element.put("title", messages.getString("common.result.showGetCapabilityUrlRestricted"));
                        }
                        // ADD FIRST ONE FOUND !!!
                        break;
                    }
                }
            }
        }
        return element;
    }
    
    public HashMap<String, Object> getAreaGeothesaurus(String xpathExpression){
        HashMap element = new HashMap();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList subjectEntries = new ArrayList();
            for (int i = 0; i < nodeList.getLength(); i++) {
                xpathExpression = "./gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code";
                Node node = nodeList.item(i);
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList childNodeList = XPathUtils.getNodeList(node, xpathExpression);
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        String domainValue = XPathUtils.getString(childNodeList.item(j), ".").trim();
                        subjectEntries.add(domainValue);
                    }
                }
                
                // "Geothesaurus-Raumbezug"
                xpathExpression = "./gmd:geographicElement/gmd:EX_GeographicBoundingBox";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList subNodeList = XPathUtils.getNodeList(node, xpathExpression);
                    element.put("type", "table");
                    element.put("title", messages.getString("geothesaurus_spacial_reference"));
                    
                    ArrayList head = new ArrayList();
                    head.add(messages.getString("geothesaurus_spacial_reference"));
                    head.add(messages.getString("spatial_ref_value_x1"));
                    head.add(messages.getString("spatial_ref_value_y1"));
                    head.add(messages.getString("spatial_ref_value_x2"));
                    head.add(messages.getString("spatial_ref_value_y2"));
                    element.put("head", head);
                    ArrayList body = new ArrayList();
                    element.put("body", body);
                    
                    ArrayList numbers = new ArrayList();
                    numbers.add(subjectEntries.size());
                    numbers.add(subNodeList.getLength());
                    int maxRows = getGreatestInt(numbers); 
                    
                    for (int j = 0; j < maxRows; j++) {
                        Node childNode = subNodeList.item(j);
                        ArrayList row = new ArrayList();
                        
                        if(subjectEntries.size() > 0){
                            if (subjectEntries.get(j)!= null) {
                                row.add(subjectEntries.get(j));
                            }else {
                                row.add("");
                            }
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:westBoundLongitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:southBoundLatitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:eastBoundLongitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:northBoundLatitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        if (!isEmptyRow(row)) {
                            body.add(row);
                        }
                    }
                }
            }
        }
        return element;
    }
    
    public HashMap<String, Object> getAreaHeight(String xpathExpression){
        HashMap element = new HashMap();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList subjectEntries = new ArrayList();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                xpathExpression = "./gmd:verticalElement";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    element.put("type", "table");
                    element.put("title", messages.getString("t01_object.vertical_extent"));
                    
                    ArrayList head = new ArrayList();
                    head.add(messages.getString("t01_object.vertical_extent_maximum"));
                    head.add(messages.getString("t01_object.vertical_extent_minimum"));
                    head.add(messages.getString("t01_object.vertical_extent_unit"));
                    head.add(messages.getString("t01_object.vertical_extent_vdatum"));
                    element.put("head", head);
                    ArrayList body = new ArrayList();
                    element.put("body", body);
                    if (XPathUtils.nodeExists(node, "./gmd:verticalElement/gmd:EX_VerticalExtent")) {
                        NodeList subNodeList = XPathUtils.getNodeList(node, "./gmd:verticalElement/gmd:EX_VerticalExtent");
                        for (int j = 0; j < subNodeList.getLength(); j++) {
                            Node childNode = subNodeList.item(j);
                            ArrayList row = new ArrayList();
                            
                            // "Maximum"
                            xpathExpression = "./gmd:maximumValue";
                            if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                row.add(notNull(XPathUtils.getString(childNode, xpathExpression)).trim());
                            }
                            
                            // "Minimum"
                            xpathExpression = "./gmd:minimumValue";
                            if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                row.add(notNull(XPathUtils.getString(childNode, xpathExpression)).trim());
                            }
                            
                            String rowValue;
                            // "Maßeinheit"
                            xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@gml:uom";
                            if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
                                row.add(notNull(sysCodeList.getNameByCodeListValue("102", rowValue)));
                            }else{
                                row.add("");
                            }
                            
                            // "Vertikaldatum"
                            String verticalDatum = "" ; 
                            xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:name";
                            if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
                                if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
                                    verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
                                }else{
                                    verticalDatum = rowValue;
                                }
                            }
                            
                            if(verticalDatum.length() < 1)
                                xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:identifier";
                                if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                    rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
                                    if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
                                        verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
                                    }else{
                                        verticalDatum = rowValue;
                                    }
                                }
                            if(verticalDatum.length() < 1){
                                xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:name";
                                if(XPathUtils.nodeExists(childNode, xpathExpression)){
                                    rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
                                    if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
                                        verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
                                    }else{
                                        verticalDatum = rowValue;
                                    }
                                }
                            }
                            
                            row.add(verticalDatum);
                            if (!isEmptyRow(row)) {
                                body.add(row);
                            }
                        }
                    }
                }
            }
        }
        return element;
    }
    
    public ArrayList getNodeListValueReferenceSystem(String xpathExpression) {
        ArrayList<String> list = new ArrayList<String>();
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength(); i++){
                String codeSpace = ""; 
                String code = "";
                xpathExpression = "./gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code";
                if (XPathUtils.nodeExists(nodeList.item(i), xpathExpression)) {
                    code = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                }
                
                xpathExpression = "./gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:codeSpace";
                if (XPathUtils.nodeExists(nodeList.item(i), xpathExpression)) {
                    codeSpace = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                }
                
                String value = "";
                if(code.length() > 0 && codeSpace.length() > 0){
                    if(code.indexOf("EPSG") > -1){
                        value = code;
                    }else{
                        value = codeSpace.concat(": " + code);
                    }
                }else if(codeSpace.length() > 0){
                    value = codeSpace;
                }else if(code.length() > 0){
                    value = code;
                }
                
                if(value.length() > 0){
                    list.add(value);
                }
            }
        }
        return list;
    }
    
    public String getGeoReport(String xpathExpression, String checkDescription, String checkNameOfMeasure) {
        String value = null;
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                String symbol = null;
                String nameOfMeasure = null;
                String description = null;
                String content = "";
                String title = "";
                
                if(node != null){
                    xpathExpression = "./gmd:nameOfMeasure";
                    if(XPathUtils.nodeExists(node, xpathExpression)){
                        nameOfMeasure = XPathUtils.getString(node, xpathExpression).trim();
                    }
                    xpathExpression = "./gmd:measureDescription";
                    if(XPathUtils.nodeExists(node, xpathExpression)){
                        description = XPathUtils.getString(node, xpathExpression).trim();
                    }
                    xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:value";
                    if(XPathUtils.nodeExists(node, xpathExpression)){
                        content = XPathUtils.getString(node, xpathExpression).trim();
                    }
                    xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:UnitDefinition/gml:catalogSymbol";
                    if(XPathUtils.nodeExists(node, xpathExpression)){
                        symbol = XPathUtils.getString(node, xpathExpression).trim();
                    }
                    
                    if(symbol != null){
                        if(description != null || nameOfMeasure != null){
                            if (checkDescription.equals(description) || checkNameOfMeasure.equalsIgnoreCase(nameOfMeasure)){
                                value = content + " " + symbol;
                                break;
                            }
                        }
                    }else{
                        if(description != null || nameOfMeasure != null){
                            if (checkDescription.equals(description) || checkNameOfMeasure.equalsIgnoreCase(nameOfMeasure)){
                                value = content;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return value;
    }
    
    public String addLinkToGetXML() {
        String htmlLink = null;
        String cswUrl = (String) PortalConfig.getInstance().getString(PortalConfig.CSW_INTERFACE_URL, "");
        if (!cswUrl.isEmpty()) {
            htmlLink = "<a href='"+cswUrl+"?REQUEST=GetRecordById&SERVICE=CSW&VERSION=2.0.2&id="+this.uuid+"&iplug="+this.iPlugId+"&elementSetName=full' target='_blank'>"+messages.getString("xml_link")+"</a>";
        }
        return htmlLink;
    }
    
    public String getPublishId(String value) {
        if (value == null) {
            return null;
        }
        String publishId = "1";
        if (value.length() > 0) {
            if (value.equals("unclassified")) {
                publishId = "1";
            } else if (value.equals("restricted")) {
                publishId = "2";
            } else if (value.equals("confidential")) {
                publishId = "3";
            }
        }
        return messages.getString("t01_object.publish_id_" + publishId);
    }

    /*
     * Private functiions
     *
     */
    private HashMap<String,Object> getCapabilityUrls(String xpathExpression) {
        boolean mapLinkAdded = false;
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            
            for (int i=0; i<nodeList.getLength();i++){
                if(XPathUtils.nodeExists(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/*/gmd:linkage/gmd:URL")){
                    Node node = XPathUtils.getNode(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/*/gmd:linkage/gmd:URL");
                    String urlValue = XPathUtils.getString(node, ".").trim();
                    // do not display empty URLs
                    if (urlValue == null || urlValue.length() == 0) {
                        continue;
                    }
                    if (urlValue.toLowerCase().indexOf("request=getcapabilities") != -1) {
                        // also add an identifier to select the correct layer in the map client 
                        map = addBigMapLink(UtilsVelocity.urlencode(urlValue) + "||" + UtilsVelocity.urlencode(getLayerIdentifier(null)), true);
                        // ADD FIRST ONE FOUND !!!
                        mapLinkAdded = true;
                        break;
                    }
                }
            }
            if (!mapLinkAdded) {
                // check if preview image is available
                xpathExpression = "./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/gco:CharacterString";
                map = getPreviewImage(xpathExpression);
            }
        }
        return map;
    }
    
    private HashMap<String,Object> getPreviewImage(String xpathExpression) {
        String url = getPreviewImageUrl(xpathExpression);
        HashMap<String,Object> elementCapabilities = new HashMap<String,Object>();
        if (url != null) {
            elementCapabilities.put("type", "multiLine");
            HashMap<String,Object> elementMapLink = new HashMap<String,Object>();
            elementMapLink.put("type", "linkLine");
            elementMapLink.put("isMapLink", new Boolean(true));
            elementMapLink.put("isExtern", new Boolean(false));
            elementMapLink.put("title", messages.getString("preview"));
            elementMapLink.put("href", url);
            elementMapLink.put("src", url);
            // put link in a list so that it is aligned correctly in detail view (<div class="width_two_thirds">)
            ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
            list.add(elementMapLink);
            elementCapabilities.put("elements", list);
            elementCapabilities.put("width", "full");
        }
        return elementCapabilities;
    }
    
    private String getLayerIdentifier(Node crossReference) {
        if (getUdkObjectClassType().equals("1")) {
            String href = XPathUtils.getString(rootNode, "./gmd:identificationInfo/gmd:MD_DataIdentification/@uuid");
            if (href != null) {
                return href;
            }
        } else {
            String origId = XPathUtils.getString(crossReference, "./@orig-uuid");
            String uuid   = XPathUtils.getString(crossReference, "./@uuid");
            String xpathExpression = "./gmd:identificationInfo/*/srv:operatesOn";
            
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
            
                String uuidRef = XPathUtils.getString(node, "./@uuidref");
                String href = XPathUtils.getString(node, "./@xlink:href");
                
                if (uuidRef != null && (uuidRef.equals(uuid) || uuidRef.equals(origId))) {
                    return href;
                }
            }
        }
        
        return "NOT_FOUND";
    }
    
    private HashMap<String, Object> addBigMapLink(String urlValue, boolean urlEncodeHref) {
        HashMap<String, Object> elementCapabilities = new HashMap<String, Object>();
        if (urlValue != null) {
            elementCapabilities.put("type", "multiLine");
            HashMap<String, Object> elementMapLink = new HashMap<String, Object>();
            elementMapLink.put("type", "linkLine");
            elementMapLink.put("isMapLink", new Boolean(true));
            elementMapLink.put("isExtern", new Boolean(false));

            if (hasAccessConstraints()) {
                // do not render "show in map" link if the map has access constraints (no href added).
                elementMapLink.put("title", messages.getString("common.alt.image.preview"));
            } else {
                elementMapLink.put("title", messages.getString("common.result.showMap.tooltip.short"));
                if(urlEncodeHref){
                    elementMapLink.put("href", UtilsVelocity.urlencode(urlValue));
                }else{
                    elementMapLink.put("href", urlValue);
                }
            }

            // use preview image if provided otherwise static image
            String imageUrl = getPreviewImageUrl(null);
            if (imageUrl == null ) imageUrl = "/ingrid-portal-apps/images/show_map.png";
            elementMapLink.put("src", imageUrl);
            // put link in a list so that it is aligned correctly in detail view (<div class="width_two_thirds">)
            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            list.add(elementMapLink);
            elementCapabilities.put("elements", list);
            elementCapabilities.put("width", "full");
        }
        return elementCapabilities;
    }
    
    private String getPreviewImageUrl(String xpathExpression) {
        if (xpathExpression == null)
            xpathExpression = "./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/gco:CharacterString";
        
        String value = null;
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if(node.getTextContent().length() > 0){
                value = node.getTextContent();
            }
        }
        return value;
    }
    
    private String getCapabilityUrl() {
        String url = null;
        String serviceType = null;
        String xpathExpression = "";
        
        xpathExpression = "./gmd:identificationInfo/*/srv:serviceType";
        if(XPathUtils.nodeExists(this.rootNode, xpathExpression)){
            serviceType = XPathUtils.getString(this.rootNode, xpathExpression);
        }
        if(serviceType != null){
            if (serviceType.trim().equals("view")) {
                Node capNode = XPathUtils.getNode( this.rootNode, "./gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata/srv:operationName/gco:CharacterString[text() = 'GetCapabilities']/../../srv:connectPoint//gmd:URL");
                if (capNode != null) {
                    if(capNode.getTextContent() != null){
                        url = capNode.getTextContent().trim();
                    }
                }
            }
        }
        return url;
    }
    
    private String getCapabilityUrlFromCrossReference( String uuid ) {
        String url = null;
        // get service url which should be in crossReference-Node, identified by uuid, serviceType and serviceOperation
        // just take the first url you can find if uuid has not been set
        Node serviceUrl = null;
        if (uuid == null)
            serviceUrl = XPathUtils.getNode( this.rootNode, "./idf:crossReference/idf:serviceType[text() = 'view']/../idf:serviceOperation[text() = 'GetCapabilities']/../idf:serviceUrl");
        else
            serviceUrl = XPathUtils.getNode( this.rootNode, "./idf:crossReference[@uuid='" + uuid + "']/idf:serviceType[text() = 'view']/../idf:serviceOperation[text() = 'GetCapabilities']/../idf:serviceUrl");
            
        if ( serviceUrl != null ) {
            url = serviceUrl.getTextContent();
        }
        return url;
    }

    private boolean hasAccessConstraints() {
        boolean hasAccessConstraints = false;
        String xpathExpression = "./idf:hasAccessConstraint";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            String hasAccessConstraintsValue = XPathUtils.getString(rootNode, xpathExpression).trim();
            if(hasAccessConstraintsValue.length() > 0){
                hasAccessConstraints = Boolean.parseBoolean(hasAccessConstraintsValue); 
            }
        }
        return hasAccessConstraints;
    }
}
