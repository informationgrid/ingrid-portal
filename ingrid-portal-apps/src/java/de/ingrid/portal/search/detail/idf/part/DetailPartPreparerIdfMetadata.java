/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.geo.utils.transformation.GmlToWktTransformUtil;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.capabilities.CapabilitiesUtils;

public class DetailPartPreparerIdfMetadata extends DetailPartPreparer{

    private static final Logger log = LoggerFactory.getLogger(DetailPartPreparerIdfMetadata.class);

    @Override
    public void init(Node node, String iPlugId, RenderRequest request, RenderResponse response, Context context) {
        super.init( node, iPlugId, request, response, context );

        this.templateName = "/WEB-INF/templates/detail/parts/detail_part_preparer_metadata.vm";
        this.localTagName = "idfMdMetadata";
    }

    public String getUdkObjectClassType() {
        String xpathExpression = ".";
        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = xPathUtils.getNode(rootNode, xpathExpression);
            if(node.hasChildNodes()){
                String hierachyLevel = "";
                String hierachyLevelName = "";

                xpathExpression = "./gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    hierachyLevel = xPathUtils.getString(node, xpathExpression).trim();
                }

                xpathExpression = "./gmd:hierarchyLevelName/*[self::gco:CharacterString or self::gmx:Anchor]";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    hierachyLevelName = xPathUtils.getString(node, xpathExpression).trim();
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
        String xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title/*[self::gco:CharacterString or self::gmx:Anchor]";
        Node node = xPathUtils.getNode(this.rootNode, xpathExpression);
        if(node != null && node.getTextContent().length() > 0){
            value = node.getTextContent();
        }
        return value;
    }

    public String getTimeStamp(String xpathExpression){
        String value = null;
        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = xPathUtils.getNode(rootNode, xpathExpression);
            String modTime = "";
            if(xPathUtils.nodeExists(node, "./gco:DateTime")){
                modTime = getDateFormatValue(xPathUtils.getString(node, "./gco:DateTime").trim());
            }else if(xPathUtils.nodeExists(node, "./gco:Date")){
                modTime = getDateFormatValue(xPathUtils.getString(node, "./gco:Date").trim());
            }else {
                modTime = getDateFormatValue(xPathUtils.getString(node, ".").trim());
            }
            if(modTime.length() > 0){
                value = modTime;
            }
        }
        return value;
    }

    public List<String> getAlternateTitle(){
        List<String> listSearch = new ArrayList<>();
        String xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:alternateTitle/*[self::gco:CharacterString or self::gmx:Anchor]";
        NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
        for (int i=0; i< nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String content = node.getTextContent();
            String codelistAdvGroup = "8010";
            String codelistValue = sysCodeList.getNameByCodeListValue(codelistAdvGroup, content);
            if(codelistValue.isEmpty()) {
                listSearch.add(content);
            }
        }
        return listSearch;
    }

    public List<String> getAlternateTitleListFromCodelist(String codelist){
        List<String> listSearch = new ArrayList<>();
        String xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:alternateTitle/*[self::gco:CharacterString or self::gmx:Anchor]";
        NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
        for (int i=0; i< nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String content = node.getTextContent();
            String codelistValue = sysCodeList.getNameByCodeListValue(codelist, content);
            if(!codelistValue.isEmpty()) {
                listSearch.add(codelistValue);
            }
        }
        return listSearch;
    }

    public String getDescription(){
        String value = null;

        String xpathExpressionAbstract = "./idf:abstract";
        Node abstractParentNode = rootNode;
        Node node = xPathUtils.getNode(rootNode, "./gmd:identificationInfo/*");

        if (!xPathUtils.nodeExists(abstractParentNode, xpathExpressionAbstract)) {
            xpathExpressionAbstract = "./gmd:abstract";
            abstractParentNode = node;
        }
        value = xPathUtils.getString(abstractParentNode, xpathExpressionAbstract);
        if (value != null) {
            value = removeLocalisation(value);
            value = value.trim();
        }

        return value;
    }

    public Map<String, Object> getMapImage(){
        return getMapImage(null);
    }

    public Map<String, Object> getMapImage(String partner){
        HashMap<String, Object> map = new HashMap<>();
        // showMap/Preview-Link
        if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false)) {
            if (getUdkObjectClassType().equals("1")) {
                String xpathMapUrl = "./idf:mapUrl";
                if(xPathUtils.nodeExists(rootNode, xpathMapUrl)) {
                    String mapUrlNodeText = xPathUtils.getNode(rootNode, xpathMapUrl).getTextContent();
                    if(mapUrlNodeText != null && !mapUrlNodeText.isEmpty()) {
                        map = addBigMapLink(rootNode, "", false, partner, mapUrlNodeText);
                    }
                }
                if(map.isEmpty()) {
                    // first try to get any valid WMS url from the crossReference section
                    String xpathCrossReference = "./idf:crossReference";
                    NodeList crossReferenceNodeList = xPathUtils.getNodeList(rootNode, xpathCrossReference);
                    if(crossReferenceNodeList.getLength() > 0) {
                        for (int i=0; i< crossReferenceNodeList.getLength(); i++) {
                            Node crossReferenceNode = crossReferenceNodeList.item(i);
                            String serviceUrl =  xPathUtils.getString(crossReferenceNode, "./idf:serviceUrl");
                            String extMapUrl =  xPathUtils.getString(crossReferenceNode, "./idf:mapUrl");
                            String serviceType =  xPathUtils.getString(crossReferenceNode, "./idf:serviceType");
                            String serviceVersion =  xPathUtils.getString(crossReferenceNode, "./idf:serviceVersion");
                            if(serviceUrl != null && !serviceUrl.isEmpty()) {
                                String getCapUrl = null;
                                String layerIdentifier = getLayerIdentifier(null);
                                if(!layerIdentifier.equals("NOT_FOUND")) {
                                    getCapUrl = UtilsSearch.addMapclientCapabilitiesInformation(serviceUrl, serviceVersion, serviceType, layerIdentifier);
                                } else {
                                    getCapUrl = UtilsSearch.addMapclientCapabilitiesInformation(serviceUrl, serviceVersion, serviceType);
                                }
                                if(getCapUrl != null && !getCapUrl.isEmpty()) {
                                    map = addBigMapLink(crossReferenceNode, getCapUrl, false, partner, extMapUrl);
                                    if(!hasAccessConstraints(crossReferenceNode)) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(map.isEmpty()) {
                    // search for it in onlineResources
                    String xpathExpression = "./gmd:distributionInfo/*/gmd:transferOptions";
                    boolean mapLinkAdded = false;
                    if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
                        NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
                        for (int i=0; i<nodeList.getLength();i++){
                            if(xPathUtils.nodeExists(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/*/gmd:linkage/gmd:URL")){
                                Node node = xPathUtils.getNode(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/*/gmd:linkage/gmd:URL");
                                String urlValue = xPathUtils.getString(node, ".").trim();
                                // do not display empty URLs
                                if (urlValue == null || urlValue.length() == 0) {
                                    continue;
                                }
                                String serviceType = xPathUtils.getString(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/*/gmd:function/gmd:CI_OnLineFunctionCode");
                                if ((serviceType != null && (serviceType.trim().equalsIgnoreCase("view") || serviceType.trim().equalsIgnoreCase("wms") || serviceType.trim().equalsIgnoreCase("wmts"))) &&
                                    ((urlValue.toLowerCase().contains("request=getcapabilities") && urlValue.toLowerCase().contains("service=wms")) || 
                                    (urlValue.toLowerCase().contains("request=getcapabilities") && urlValue.toLowerCase().contains("service=wmts")) ||
                                    (urlValue.toLowerCase().contains("wmtscapabilities.xml")))
                                ) {
                                    urlValue = UtilsSearch.getMapCapabilitiesWithoutServiceInfo(urlValue, getLayerIdentifier(null));
                                    // also add an identifier to select the correct layer in the map client 
                                    map = addBigMapLink(node, urlValue, true);
                                    // ADD FIRST ONE FOUND !!!
                                    mapLinkAdded = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!mapLinkAdded) {
                        // check if preview image is available
                        xpathExpression = "./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/*[self::gco:CharacterString or self::gmx:Anchor]";
                        map = getPreviewImage(xpathExpression);
                    }
                }
            // otherwise try within distribution info or SV_OperationMetadata
            } else if ( getUdkObjectClassType().equals("3")) {
                String capabilitiesUrl = getCapabilityUrl();
                String extMapUrl =  xPathUtils.getString(rootNode, "./idf:mapUrl");
                // get it directly from the operation
                map = addBigMapLink(rootNode, capabilitiesUrl, false, partner, extMapUrl);
            } else {
                // show preview image (with map link information if provided)
                map = getPreviewImage("./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/*[self::gco:CharacterString or self::gmx:Anchor]");
            }
        } else {
            String xpathMapUrl = "./idf:mapUrl";
            if(xPathUtils.nodeExists(rootNode, xpathMapUrl)) {
                String mapUrlNodeText = xPathUtils.getNode(rootNode, xpathMapUrl).getTextContent();
                if(mapUrlNodeText != null && !mapUrlNodeText.isEmpty()) {
                    map = addBigMapLink(rootNode, "", false, partner, mapUrlNodeText);
                }
            }
            if(map.isEmpty()) {
                // show preview image (with map link information if provided)
                map = getPreviewImage("./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/*[self::gco:CharacterString or self::gmx:Anchor]");
            }
        }

        return map;
    }

    public List<HashMap<String, Object>> getReference(String xpathExpression){
        return getReference(xpathExpression, false);
    }

    public List<HashMap<String, Object>> getReference(String xpathExpression, boolean isCoupled){
        ArrayList<HashMap<String, Object>> linkList = new ArrayList<>();

        int limitReferences = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_DETAIL_VIEW_LIMIT_REFERENCES, 100);

        String serviceType = null;

        serviceType = xPathUtils.getString(rootNode, "./gmd:identificationInfo/*/srv:serviceType");
        if(serviceType != null){
            serviceType = serviceType.trim();
        }

        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength();i++){

                if (linkList.size() >= limitReferences){
                    HashMap<String, Object> link = new HashMap<>();
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
                String extMapUrl = null;
                String objServiceType = null;
                String objServiceVersion = null;
                String[] graphicOverview = null;
                String tmp = null;
                String[] tmpList = null;

                xpathExpression = "./@uuid";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    uuid = tmp.trim();
                }

                xpathExpression = "./idf:objectName";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    title = tmp.trim();
                }

                xpathExpression = "./idf:objectType";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    type = tmp.trim();
                }

                xpathExpression = "./idf:attachedToField";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    attachedToField = tmp.trim();
                }

                xpathExpression = "./idf:attachedToField/@entry-id";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    entryId = tmp.trim();
                }

                xpathExpression = "./idf:description";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    description = tmp.trim();
                }

                xpathExpression = "./idf:serviceType";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    objServiceType = tmp.trim();
                }

                xpathExpression = "./idf:serviceVersion";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    objServiceVersion = tmp.trim();
                }

                xpathExpression = "./idf:serviceUrl";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    serviceUrl = tmp.trim();
                }

                xpathExpression = "./idf:mapUrl";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    extMapUrl = tmp.trim();
                }

                xpathExpression = "./idf:graphicOverview";
                tmpList = xPathUtils.getStringArray(node, xpathExpression);
                if(tmpList != null){
                    graphicOverview = tmpList;
                }

                HashMap<String, Object> link = new HashMap<>();
                link.put("hasLinkIcon", true);
                link.put("isExtern", false);
                link.put("title", title);
                link.put("objectClass", type);
                link.put("serviceType", UtilsSearch.getHitShortcut(objServiceVersion, objServiceType));
                link.put("graphicOverview", graphicOverview);
                link.put("extMapUrl", extMapUrl);
                if (description.length() > 0) {
                    link.put("description", description);
                }
                if (attachedToField.length() > 0) {
                    link.put("attachedToField", attachedToField);
                }
                if(this.iPlugId != null){
                    if(uuid != null){
                        String href = "?docuuid=" + uuid;
                        if(PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_DETAIL_USE_PARAMETER_PLUGID)) {
                            href += "&plugid=" + this.iPlugId;
                        }
                        link.put("href", href);
                    }else{
                        link.put("href", "");
                    }
                }else{
                    link.put("href", "");
                }

                if(isCoupled && entryId.equals("3600")){
                    // add map links to data objects from services
                    if (type.equals("3")) {
                        if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false)) {
                            StringBuilder capabilityUrl = null;
                            // get link from operation (unique one)
                            if ((serviceType != null && serviceType.trim().equals("view")) || (objServiceType != null && objServiceType.trim().equals("view")) && !hasAccessConstraints(node)) {
                                if(serviceUrl != null){
                                    serviceUrl = UtilsSearch.addMapclientCapabilitiesInformation(serviceUrl, objServiceVersion, objServiceType, getLayerIdentifier(node));
                                    if(serviceUrl != null) {
                                        capabilityUrl = new StringBuilder(serviceUrl);
                                    }
                                }
                            } else {
                                String tmpUrl = getCapabilityUrl(getLayerIdentifier(node));
                                if(tmpUrl != null) {
                                    capabilityUrl =  new StringBuilder(tmpUrl);
                                }
                            }
                            if ( capabilityUrl != null && capabilityUrl.length() != 0) {
                                link.put("mapLink", capabilityUrl.toString());
                            }
                        }
                        // do not show link relation for coupled resources (INGRID-2285)
                        link.remove("attachedToField");
                        linkList.add(link);
                    } else if (type.equals("1")) {
                        StringBuilder capUrl = new StringBuilder();
                        if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false)) {
                            if(serviceUrl != null){
                                capUrl.append(serviceUrl);
                            } else {
                                String xpathCrossReference = "./idf:crossReference[@uuid='" + uuid + "']/idf:serviceType[text() = 'view']/../idf:serviceOperation[text() = 'GetCapabilities']/../idf:serviceUrl";
                                NodeList crossReferenceNodeList = xPathUtils.getNodeList(rootNode, xpathCrossReference);
                                if(crossReferenceNodeList.getLength() > 0) {
                                    for (int j=0; j< crossReferenceNodeList.getLength(); j++) {
                                        Node crossReferenceNode = crossReferenceNodeList.item(j);
                                        String getCapUrl = crossReferenceNode.getTextContent();
                                        if(!getCapUrl.isEmpty()) {
                                            if(!hasAccessConstraints(crossReferenceNode.getParentNode())) {
                                                capUrl.append(crossReferenceNode.getTextContent());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if ( capUrl != null && capUrl.length() != 0 && !hasAccessConstraints(node)) {
                                // add possible missing parameters
                                capUrl.append(CapabilitiesUtils.getMissingCapabilitiesParameter( capUrl.toString() ));
                                link.put("mapLink",  UtilsVelocity.urlencode(capUrl.toString() + "||" + getLayerIdentifier(node)));
                            }
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

    public List<HashMap<String, Object>> getExternLinks(String xpathExpression) {
        return getExternLinks(xpathExpression, false);
    }

    public List<HashMap<String, Object>> getExternLinks(String xpathExpression, boolean isDownload) {
        ArrayList<HashMap<String, Object>> linkList = new ArrayList<>();
        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength();i++){
                NodeList onLineList = xPathUtils.getNodeList(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine");

                for (int j=0; j<onLineList.getLength();j++){
                    String url = "";
                    String name = "";
                    String description = "";
                    String attachedToField = "";
                    String applicationProfile = "";
                    String size = "";
                    float roundSize = 0;

                    xpathExpression = "./*/gmd:linkage/gmd:URL";
                    if(xPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        url = xPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }

                    xpathExpression = "./*/gmd:description";
                    if(xPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        description = xPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }

                    xpathExpression = "./*/gmd:name";
                    if(xPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        name = xPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }

                    xpathExpression = "./*/idf:attachedToField";
                    if(xPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        attachedToField = xPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }

                    if(attachedToField.isEmpty()) {
                        xpathExpression = "./*/gmd:function/gmd:CI_OnLineFunctionCode/@codeListValue";
                        if(xPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                            attachedToField = xPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                            if(!attachedToField.isEmpty()) {
                                attachedToField = sysCodeList.getNameByCodeListValue("2000", attachedToField);
                            }
                        }
                    }

                    xpathExpression = "./*/gmd:applicationProfile";
                    if(xPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
                        applicationProfile = xPathUtils.getString(onLineList.item(j), xpathExpression).trim();
                    }

                    // also mapped by T0112_media_option !!!
                    xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:transferSize";
                    if(xPathUtils.nodeExists(nodeList.item(i), xpathExpression)){
                        size = xPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                        roundSize = Float.parseFloat(size);
                        roundSize = (float) (Math.round(roundSize * 1000) / 1000.0);
                    }

                    if(url.length() > 0){
                        HashMap<String, Object> link = new HashMap<>();
                        link.put("hasLinkIcon", true);
                        if (isDownload) {
                            link.put("isDownload", isDownload);

                        } else {
                            link.put("isExtern", true);
                        }

                        if(!applicationProfile.isEmpty()) {
                            link.put("serviceType", applicationProfile);
                        }

                        String atomPrefix = PortalConfig.getInstance().getString(PortalConfig.ATOM_DOWNLOAD_CLIENT_PREFIX, "");
                        String atomClientUrl = PortalConfig.getInstance().getString( PortalConfig.ATOM_DOWNLOAD_CLIENT_URL, "" );

                        if(!atomClientUrl.isEmpty() && !atomPrefix.isEmpty() && url.contains( atomPrefix )) {
                            String urlAtom = transformAtomDownloadLink( atomClientUrl, url );
                            link.put( "href", urlAtom );
                        } else {
                            link.put("href", url);
                        }

                        if(name.length() > 0){
                            link.put("title", name);
                        } else {
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

    public List<HashMap<String, Object>> getDistributionLinks(String xpathExpression) {
        ArrayList<HashMap<String, Object>> linkList = new ArrayList<>();
        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength();i++){
                Node node = nodeList.item(i);
                String format = "";
                String accessURL = "";
                String modified = "";
                String title = "";
                String description = "";
                HashMap<String, String> license  = null;
                String byClause = "";
                ArrayList<String> languages = null;
                String availability = "";

                xpathExpression = "./idf:format";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    format = xPathUtils.getString(node, xpathExpression).trim();
                }

                xpathExpression = "./idf:accessURL";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    accessURL = xPathUtils.getString(node, xpathExpression).trim();
                }

                xpathExpression = "./idf:modified";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    modified = xPathUtils.getString(node, xpathExpression).trim();
                }

                xpathExpression = "./idf:title";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    title = xPathUtils.getString(node, xpathExpression).trim();
                }

                xpathExpression = "./idf:description";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    description = xPathUtils.getString(node, xpathExpression).trim();
                }

                xpathExpression = "./idf:byClause";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    byClause = xPathUtils.getString(node, xpathExpression).trim();
                }

                xpathExpression = "./idf:availability";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    availability = xPathUtils.getString(node, xpathExpression).trim();
                }

                xpathExpression = "./idf:license";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    Node tmpNode = xPathUtils.getNode(node, xpathExpression);
                    license = new HashMap<>();
                    String key = null;
                    String value = null;
                    if(xPathUtils.nodeExists(tmpNode, "./@key")){
                        key = xPathUtils.getString(tmpNode, "./@key").trim();
                        license.put("key", key);
                    }
                    if(xPathUtils.nodeExists(tmpNode, ".")){
                        value = xPathUtils.getString(tmpNode, ".").trim();
                        license.put("value", value);
                    }
                }
                
                xpathExpression = "./idf:languages/idf:language";
                if(xPathUtils.nodeExists(node, xpathExpression)){
                    NodeList tmpNodes = xPathUtils.getNodeList(node, xpathExpression);
                    languages = new ArrayList<>();
                    for (int j=0; j<tmpNodes.getLength();j++){
                        Node tmpNode = tmpNodes.item(j);
                        String tmpValue = tmpNode.getTextContent();
                        if(tmpValue != null && !tmpValue.trim().isEmpty()) {
                            languages.add(tmpNode.getTextContent());
                        }
                    }
                }
                if(accessURL != null && !accessURL.isEmpty()) {
                    HashMap<String, Object> link = new HashMap<>();
                    link.put("hasLinkIcon", true);
                    link.put("isExtern", true);
                    link.put("format", format);
                    link.put("href", accessURL);
                    link.put("modified", modified);
                    link.put("title", !title.isEmpty() ? title : accessURL);
                    link.put("description", description);
                    link.put("license", license);
                    link.put("byClause", byClause);
                    link.put("languages", languages);
                    link.put("availability", availability);
                    linkList.add(link);
                }
            }
        }
        return linkList;
    }

    public HashMap<String, List<String>> getDefaultIndexInformationKeywords(String xpathExpression) {
        HashMap<String, List<String>> map = new HashMap<>();

        List<String> listInspire = new ArrayList<>();
        List<String> listInveskos = new ArrayList<>();
        List<String> listPriority = new ArrayList<>();
        List<String> listSpatial = new ArrayList<>();
        List<String> listGemet = new ArrayList<>();
        List<String> listHvd = new ArrayList<>();
        List<String> listSearch = new ArrayList<>();

        List hiddenKeywordList = PortalConfig.getInstance().getList(PortalConfig.PORTAL_DETAIL_VIEW_HIDDEN_KEYWORDS);
        String opendata = xPathUtils.getString(rootNode, "//gmd:identificationInfo/*/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/*[self::gco:CharacterString or self::gmx:Anchor][contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'opendata') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'opendataident')]");

        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String type = "";
                String thesaurusName = "";

                // type
                xpathExpression = "./gmd:type/gmd:MD_KeywordTypeCode/@codeListValue";
                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    type = xPathUtils.getString(node, xpathExpression);
                }

                // thesaurus
                xpathExpression = "./gmd:thesaurusName/gmd:CI_Citation/gmd:title";
                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    thesaurusName = xPathUtils.getString(node, xpathExpression).trim();
                }

                // keywords
                xpathExpression = "./gmd:keyword/*[self::gco:CharacterString or self::gmx:Anchor]";

                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    String[] keywordNodeList = xPathUtils.getStringArray(node, xpathExpression);
                    for (int j = 0; j < keywordNodeList.length; j++) {
                        String value = keywordNodeList[j].trim();

                        if(value != null && !value.isEmpty()){
                            if(hiddenKeywordList.indexOf(value) > -1){
                                continue;
                            }
                            if (!thesaurusName.isEmpty()) {
                                // "Service Classification, version 1.0"
                                if (!StringUtils.containsIgnoreCase(thesaurusName, "Service")) {
                                    // "GEMET - Concepts, version 2.1"
                                    if (StringUtils.containsIgnoreCase(thesaurusName, "Concepts")) {
                                        String tmpValue = sysCodeList.getNameByCodeListValue("5200", value);
                                        if(tmpValue.isEmpty()){
                                            tmpValue = value;
                                        }
                                        if(listGemet.indexOf(tmpValue) == -1) {
                                            listGemet.add(tmpValue);
                                        }
                                    } else if (StringUtils.containsIgnoreCase(thesaurusName, "priority")) {
                                        String tmpValue = sysCodeList.getNameByCodeListValue("6350", value);
                                        if(tmpValue.isEmpty()){
                                            tmpValue = value;
                                        }
                                        if(listPriority.indexOf(tmpValue) == -1) {
                                            listPriority.add(tmpValue);
                                        }
                                    } else if (StringUtils.containsIgnoreCase(thesaurusName, "INSPIRE")) {
                                        String tmpValue = sysCodeList.getNameByCodeListValue("6100", value);
                                        if(tmpValue.isEmpty()){
                                            tmpValue = value;
                                        }
                                        if(listInspire.indexOf(tmpValue) == -1) {
                                            listInspire.add(tmpValue);
                                        }
                                    } else if (StringUtils.containsIgnoreCase(thesaurusName, "Spatial scope") || sysCodeList.hasCodeListDataKeyValue("6360", value, "thesaurusTitle", thesaurusName)) {
                                        String tmpValue = sysCodeList.getNameByCodeListValue("6360", value);
                                        if(tmpValue.isEmpty()){
                                            tmpValue = value;
                                        }
                                        if(listSpatial.indexOf(tmpValue) == -1) {
                                            listSpatial.add(tmpValue);
                                        }
                                    } else if (StringUtils.containsIgnoreCase(thesaurusName, "IACS Data")) {
                                        if(listInveskos.indexOf(value) == -1) {
                                            listInveskos.add(value);
                                        }
                                    } else if (opendata != null 
                                            && StringUtils.containsIgnoreCase(thesaurusName, "High-value")) {
                                        if(listHvd.indexOf(value) == -1) {
                                            listHvd.add(value);
                                        }
                                    } else if (StringUtils.containsIgnoreCase(thesaurusName, "UMTHES")) {
                                        if(listSearch.indexOf(value) == -1) {
                                            listSearch.add(value);
                                        }
                                    } else{
                                        // try to match keyword to  Opendata Category
                                        String tmpValue = sysCodeList.getNameByData("6400", value);
                                        if(tmpValue.isEmpty()){
                                            tmpValue = value;
                                        }
                                        if(listSearch.indexOf(tmpValue) == -1) {
                                            listSearch.add(tmpValue);
                                        }
                                    }
                                }
                            } else{
                                // try to match keyword to  Opendata Category
                                String tmpValue = sysCodeList.getNameByData("6400", value);
                                if(tmpValue.isEmpty()){
                                    tmpValue = value;
                                }
                                if(listSearch.indexOf(tmpValue) == -1) {
                                    listSearch.add(tmpValue);
                                }
                            }
                        }
                    }
                }
            }
        }
        sortList(listInspire);
        sortList(listInveskos);
        sortList(listPriority);
        sortList(listSpatial);
        sortList(listGemet);
        sortList(listHvd);
        sortList(listSearch);
        map.put("inspire", listInspire);
        map.put("inveskos", listInveskos);
        map.put("priority", listPriority);
        map.put("spatial", listSpatial);
        map.put("gemet", listGemet);
        map.put("hvd", listHvd);
        map.put("search", listSearch);

        return map;
    }

    public List<String> getIndexInformationKeywords(String xpathExpression) {
        return getIndexInformationKeywords(xpathExpression, null, false);
    }

    public List<String> getIndexInformationKeywords(String xpathExpression, String codelist, boolean checkCodelistData) {
        List<String> list = new ArrayList<>();

        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            String[] nodeList = xPathUtils.getStringArray(rootNode, xpathExpression);

            for (int i = 0; i < nodeList.length; i++) {
                String value = nodeList[i].trim();
                
                if(value != null){
                    String[] hiddenKeywordList = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_DETAIL_VIEW_HIDDEN_KEYWORDS);
                    if(hiddenKeywordList != null){
                        for(int h=0; h < hiddenKeywordList.length; h++){
                            String hiddenValue = hiddenKeywordList[h];
                            if(value.equalsIgnoreCase(hiddenValue)){
                                break;
                            }
                        }
                    }

                    if (codelist != null) {
                        String tmpValue = "";
                        if (checkCodelistData) {
                            tmpValue = sysCodeList.getNameByData(codelist, value);
                        } else {
                            tmpValue = sysCodeList.getNameByCodeListValue(codelist, value);
                        }
                        if(!tmpValue.isEmpty()) {
                            value = tmpValue;
                        }
                    }
                    if(!value.isEmpty()) {
                        list.add(value);
                    }
                }
            }
        }

        sortList(list);
        return list;
    }

    /**
     * @param xpathExpressions is the xpath where the relevant elements are, including author names, publish year, title, doi.
     * rootName is the mandatory rootXpath that repeats itself for the possibility of multiple authors.
     */
    public List<HashMap<String, Object>> getListAPACitation(Map<String, String> xpathExpressions) {
        return getListAPACitation(xpathExpressions, null);
    }

    /**
     * @param rootXpathExpression is the recurrent rootXpath that contains multiple citations; every element should have
     * relative xpath to the rootXpath.
     * when extracting a single citation, the second argument should not be given; the xpath for every element should
     * be absolute xpath.
     */
    public List<HashMap<String, Object>> getListAPACitation(Map<String, String> xpathExpressions, String rootXpathExpression) {
        List<HashMap<String, Object>> listCitation = new ArrayList<>();
        if(rootXpathExpression != null && xPathUtils.nodeExists(rootNode, rootXpathExpression)){
            // return multiple citations from recurrent rootXpath
            NodeList tmpNodeList = xPathUtils.getNodeList(rootNode, rootXpathExpression);
            for (int i = 0; i < tmpNodeList.getLength(); i++){
                Node node = tmpNodeList.item(i);
                HashMap<String, Object> element = getAPACitationElements(node, xpathExpressions);
                if (!element.isEmpty()){listCitation.add(element);}
            }
        } else {
            // return a single citation from absolute xpath
            Node root = this.rootNode;
            HashMap<String, Object> element = getAPACitationElements(root, xpathExpressions);
            if (!element.isEmpty()){listCitation.add(element);}
        }
        return listCitation;
    }

    public HashMap<String, Object> getAPACitationElements(Node node, Map<String, String> xpathExpressions){
        HashMap<String, Object> element = new HashMap<>();
        if (xpathExpressions.containsKey("rootName") && xPathUtils.nodeExists(node, xpathExpressions.get("rootName"))){
            String value = getAPACitationValueFromXpath("name", node, xpathExpressions.get("author_person"), xpathExpressions.get("rootName"));
            if ((value == null || value.trim().isEmpty()) && xpathExpressions.containsKey("author_org")) {
                String org = getAPACitationValueFromXpath("org_name", node, xpathExpressions.get("author_org"));
                if (org != null) {element.put("authors", org);}
            } else {
                element.put("authors", value);
            }
        }
        if (xpathExpressions.containsKey("year") && xPathUtils.nodeExists(node, xpathExpressions.get("year"))){
            String value = getAPACitationValueFromXpath("year", node, xpathExpressions.get("year"));
            if (value != null) {element.put("year", value);}
        }
        if (xpathExpressions.containsKey("title") && xPathUtils.nodeExists(node, xpathExpressions.get("title"))){
            String value = getAPACitationValueFromXpath("title", node, xpathExpressions.get("title"));
            if (value != null) {element.put("title", value);}
        }
        if (xpathExpressions.containsKey("uuid") && xPathUtils.nodeExists(node, xpathExpressions.get("uuid"))){
            String value = getAPACitationValueFromXpath("uuid", node, xpathExpressions.get("uuid"));
            if (value != null) {element.put("uuid", value);}
        }
        if (xpathExpressions.containsKey("publisher") && xPathUtils.nodeExists(node, xpathExpressions.get("publisher"))){
            String value = getAPACitationValueFromXpath("publisher", node, xpathExpressions.get("publisher"));
            if (value != null) {element.put("publisher", value);}
        }
        if (xpathExpressions.containsKey("doi") && xPathUtils.nodeExists(node, xpathExpressions.get("doi"))){
            String value = getAPACitationValueFromXpath("doi", node, xpathExpressions.get("doi"));
            if (value != null) {element.put("doi", value);}
        }
        if (xpathExpressions.containsKey("doi_type") && xPathUtils.nodeExists(node, xpathExpressions.get("doi_type"))){
            String value = getAPACitationValueFromXpath("doi_type", node, xpathExpressions.get("doi_type"));
            if ("Dataset".equals(value)) {element.put("doi_type", messages.getString("doi.dataset"));}
        }
        return element;
    }

    public String getAPACitationValueFromXpath(String type, Node node, String xpathExpression){
        return getAPACitationValueFromXpath(type, node, xpathExpression, null);
    }

    public String getAPACitationValueFromXpath(String type, Node node, String xpathExpression, String rootXpathExpression){
        if (type.equals("name")){
            StringBuilder value = new StringBuilder();
            NodeList nameList = xPathUtils.getNodeList(node, rootXpathExpression);
            for (int i = 0; i < nameList.getLength(); i++){
                Node nameNode = xPathUtils.getNode(nameList.item(i), xpathExpression);
                if (nameNode != null && nameNode.getTextContent().length() > 0){
                    if (!value.toString().equals("")){value.append(", ");}
                    String name = nameNode.getTextContent().trim();
                    // last name
                    List<String> nameSplits = Arrays.asList(name.split(","));
                    value.append(String.format("%s,", nameSplits.get(0)));
                    // first name
                    String[] firstnameSplits = nameSplits.get(1).trim().split(" ");
                    for (String split : firstnameSplits) {
                        value.append(String.format(" %s.", split.charAt(0)));
                    }
                }
            }
            return value.toString();
        } else {
            Node valueNode = xPathUtils.getNode(node, xpathExpression);
            if (valueNode != null && valueNode.getTextContent().length() > 0){
                String value = valueNode.getTextContent().trim();
                switch (type){
                    case "year":
                        Pattern pattern = Pattern.compile("\\d{4}");
                        Matcher matcher = pattern.matcher(value);
                        if (matcher.find()) {
                            return matcher.group(0);
                        }
                    default:
                        return value;
                }
            }
        }
        return null;
    }

    public Map getAvailability(String xpathExpression) {
        HashMap element = new HashMap();

        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = xPathUtils.getNode(rootNode, xpathExpression);
            if (node.hasChildNodes()) {
                xpathExpression = "./gmd:distributionFormat/gmd:MD_Format";
                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList nodeList = xPathUtils.getNodeList(node, xpathExpression);
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
                        if (xPathUtils.nodeExists(childNode, xpathExpression)) {
                            name = xPathUtils.getString(childNode, xpathExpression).trim();
                        }

                        xpathExpression = "./gmd:version";
                        if (xPathUtils.nodeExists(childNode, xpathExpression)) {
                            version = xPathUtils.getString(childNode, xpathExpression).trim();
                        }

                        if(!name.equals("Geographic Markup Language (GML)") && !version.equals("unknown")){
                            xpathExpression = "./gmd:name";
                            if (xPathUtils.nodeExists(childNode, xpathExpression)) {
                                row.add(notNull(name));
                            } else {
                                row.add("");
                            }

                            xpathExpression = "./gmd:version";
                            if (xPathUtils.nodeExists(childNode, xpathExpression)) {
                                row.add(notNull(version));
                            } else {
                                row.add("");
                            }

                            xpathExpression = "./gmd:fileDecompressionTechnique";
                            if (xPathUtils.nodeExists(childNode, xpathExpression)) {
                                String value = xPathUtils.getString(childNode, xpathExpression).trim();
                                row.add(notNull(value));
                            } else {
                                row.add("");
                            }

                            xpathExpression = "./gmd:specification";
                            if (xPathUtils.nodeExists(childNode, xpathExpression)) {
                                String value = xPathUtils.getString(childNode, xpathExpression).trim();
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

    public Map getMediumOptions(String xpathExpression) {
        HashMap element = new HashMap();

        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = xPathUtils.getNode(rootNode, xpathExpression);
            xpathExpression = "./gmd:transferOptions";
            if (xPathUtils.nodeExists(node, xpathExpression)) {
                NodeList nodeList = xPathUtils.getNodeList(node, xpathExpression);
                element.put("type", "table");
                element.put("title", messages.getString("t0112_media_option.medium"));
                ArrayList head = new ArrayList();
                head.add(messages.getString("t0112_media_option.medium_name"));
                head.add(messages.getString("t0112_media_option.transfer_size"));
                head.add(messages.getString("t0112_media_option.medium_note"));
                element.put("head", head);
                ArrayList body = new ArrayList();
                element.put("body", body);

                String unit = "MB";
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nodeListItem = nodeList.item(i);
                    ArrayList row = new ArrayList();
                    if(xPathUtils.nodeExists(nodeListItem, "./gmd:MD_DigitalTransferOptions/gmd:offLine")){

                        xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:name/gmd:MD_MediumNameCode/@codeListValue";
                        if (xPathUtils.nodeExists(nodeListItem, xpathExpression)) {
                            String value = xPathUtils.getString(nodeListItem, xpathExpression).trim();
                            row.add(notNull(sysCodeList.getNameByCodeListValue("520", value)));
                        } else {
                            row.add("");
                        }

                        xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:unitsOfDistribution/*[self::gco:CharacterString or self::gmx:Anchor]";
                        if (xPathUtils.nodeExists(nodeListItem, xpathExpression)) {
                            String value = notNull(xPathUtils.getString(nodeListItem, xpathExpression)).trim();
                            if(!value.isEmpty()) {
                                unit = value;
                            }
                        }

                        xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:transferSize";
                        if (xPathUtils.nodeExists(nodeListItem, xpathExpression)) {
                            String transferSize = notNull(xPathUtils.getString(nodeListItem, xpathExpression)).trim();
                            row.add(transferSize + " " + unit);
                        } else {
                            row.add("");
                        }

                        xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:mediumNote";
                        if (xPathUtils.nodeExists(nodeListItem, xpathExpression)) {
                            row.add(notNull(xPathUtils.getString(nodeListItem, xpathExpression)).trim());
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

    public Map getConformityData(String xpathExpression) {
        HashMap element = new HashMap();

        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            element.put("type", "table");
            element.put("title", messages.getString("object_conformity"));

            ArrayList head = new ArrayList();
            head.add(messages.getString("object_conformity.specification"));
            head.add(messages.getString("object_conformity.publication_date"));
            head.add(messages.getString("object_conformity.degree_value"));
            head.add(messages.getString("object_conformity.explanation"));
            element.put("head", head);
            ArrayList body = new ArrayList();
            element.put("body", body);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if(xPathUtils.nodeExists(node, "./gmd:DQ_DomainConsistency")){
                    ArrayList row = new ArrayList();

                    xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:title/*[self::gco:CharacterString or self::gmx:Anchor]";
                    if (xPathUtils.nodeExists(node, xpathExpression)) {
                        String value = xPathUtils.getString(node, xpathExpression).trim();
                        row.add(notNull(value));
                    } else {
                        row.add("");
                    }

                    xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date";
                    if (xPathUtils.nodeExists(node, xpathExpression)) {
                        if(xPathUtils.nodeExists(node, xpathExpression + "/@gco:nilReason")){
                            row.add("");
                        }else {
                            String value = xPathUtils.getString(node, xpathExpression).trim();
                            row.add(notNull(getDateFormatValue(value)));
                        }
                    } else {
                        row.add("");
                    }

                    xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:pass";
                    if (xPathUtils.nodeExists(node, xpathExpression)) {
                        String value = xPathUtils.getString(node, xpathExpression).trim();
                        if(xPathUtils.nodeExists(node, xpathExpression + "/@gco:nilReason")){
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

                    xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:explanation/*[self::gco:CharacterString or self::gmx:Anchor]";
                    if (xPathUtils.nodeExists(node, xpathExpression)) {
                        String value = xPathUtils.getString(node, xpathExpression).trim();
                        // only add explanation if non standard value REDMINE-1817
                        if (value.equals("see the referenced specification")){
                            row.add("");
                        } else {
                            row.add(notNull(value));
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


    public List getServiceClassification(String xpathExpression) {
        ArrayList<String> list = new ArrayList<String>();

        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String thesaurusName = "";

                // thesaurus
                xpathExpression = "./gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title";
                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    thesaurusName = xPathUtils.getString(node, xpathExpression).trim();
                }

                // "Service Classification, version 1.0"
                if (thesaurusName.contains("Service")) {
                    // keywords
                    xpathExpression = "./gmd:MD_Keywords/gmd:keyword/*[self::gco:CharacterString or self::gmx:Anchor]";
                    if (xPathUtils.nodeExists(node, xpathExpression)) {
                        String[] keywordNodeList = xPathUtils.getStringArray(node, xpathExpression);
                        for (int j = 0; j < keywordNodeList.length; j++) {
                            String value = keywordNodeList[j].trim();
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

    public Map<String, Object> getReferenceObject(String xpathExpression) {
        HashMap element = null;

        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList childNodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList<String> listDominator = new ArrayList<>();
            ArrayList<String> listMeter = new ArrayList<>();
            ArrayList<String> listDpi = new ArrayList<>();
            for (int j = 0; j < childNodeList.getLength(); j++) {
                xpathExpression = "./gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator";
                if (xPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
                    String tmpValue = xPathUtils.getString(childNodeList.item(j), xpathExpression).trim();
                    if(!tmpValue.isEmpty()) {
                        listDominator.add(tmpValue);
                    }
                }
                xpathExpression = "./gmd:distance/gco:Distance/@uom";
                String[] distanceDPI = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_DETAIL_DISTANCE_DPI);
                String[] distanceMeter = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_DETAIL_DISTANCE_METER);
                if (xPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
                    String uom = xPathUtils.getString(childNodeList.item(j), xpathExpression).trim();
                    if (Arrays.asList(distanceDPI).indexOf(uom) > -1) {
                        xpathExpression = "./gmd:distance/gco:Distance[@uom='" + uom + "']";
                        if (xPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
                            String tmpValue = xPathUtils.getString(childNodeList.item(j), xpathExpression).trim();
                            if(!tmpValue.isEmpty()) {
                                listDpi.add(tmpValue.concat(" " + uom));
                            }
                        }
                    } else if(Arrays.asList(distanceMeter).indexOf(uom) > -1){
                        xpathExpression = "./gmd:distance/gco:Distance[@uom='" + uom + "']";
                        // IGE set uom as 'meter'
                        if(uom.equals("meter")) {
                            uom = "m";
                        }
                        if (xPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
                            String tmpValue = xPathUtils.getString(childNodeList.item(j), xpathExpression).trim();
                            if(!tmpValue.isEmpty()) {
                                listMeter.add(tmpValue.concat(" " + uom));
                            }
                        }
                    }
                }
            }

            if(!listDominator.isEmpty() || !listMeter.isEmpty() || !listDpi.isEmpty()) {
                element = new HashMap();
            
                element.put("type", "table");
                element.put("title", messages.getString("t011_obj_serv_scale"));
                ArrayList head = new ArrayList();
                head.add(messages.getString("t011_obj_serv_scale.scale").concat(" 1:x"));
                head.add(messages.getString("t011_obj_serv_scale.resolution_ground"));
                head.add(messages.getString("t011_obj_serv_scale.resolution_scan"));
                element.put("head", head);
                ArrayList body = new ArrayList();
                element.put("body", body);
                body.add(listDominator);
                body.add(listMeter);
                body.add(listDpi);
            }
        }
        return element;
    }

    public Map getConnectionPoints(String xpathExpression) {
        // xpathExpression = './gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata/srv:connectPoint'

        String serviceType = getValueFromXPath("./gmd:identificationInfo/*/srv:serviceType");
        if(serviceType != null){
            serviceType = serviceType.trim();
        }
        String serviceTypeVersion = null;
        HashMap element = new HashMap();
        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);

            for (int i=0; i<nodeList.getLength();i++){
                if(xPathUtils.nodeExists(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL")){
                    String url = xPathUtils.getString(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL").trim();
                    StringBuilder urlValue = new StringBuilder(url);
                    // do not display empty URLs
                    if (urlValue.length() == 0) {
                        continue;
                    }

                    // do not display empty operations
                    String operationName = xPathUtils.getString(nodeList.item(i), "./../srv:operationName").trim();
                    if (operationName == null || operationName.length() == 0) {
                        continue;
                    }

                    if (operationName.equals("GetCapabilities")) {
                    // add missing parameters
                    if (url.toLowerCase().indexOf("request=getcapabilities") == -1) {
                        if (url.indexOf("?") != -1) {
                            // if url or parameters already contains a ? or & at the end then do not add another one!
                            if (!(url.lastIndexOf("?") == url.length() - 1 || url.length() > 0)
                                    && !(url.lastIndexOf("&") == url.length() - 1)) {
                                urlValue.append("&");
                            }
                            urlValue.append("REQUEST=GetCapabilities");
                        }
                    }
                    if (url.toLowerCase().indexOf("service=") == -1) {
                        if (url.indexOf("?") != -1) {
                            String[] nodeListServiceTypeVersions = xPathUtils.getStringArray(rootNode, "./gmd:identificationInfo/*/srv:serviceTypeVersion/*[self::gco:CharacterString or self::gmx:Anchor]");
                            for (int j=0; j<nodeListServiceTypeVersions.length;j++){
                                String tmpServiceTypeVersion = nodeListServiceTypeVersions[j].trim();
                                if (!tmpServiceTypeVersion.isEmpty()) {
                                    String tmpValue = CapabilitiesUtils.extractServiceFromServiceTypeVersion(tmpServiceTypeVersion);
                                    if (tmpValue != null) {
                                        urlValue.append("&SERVICE=" + tmpValue);
                                        serviceTypeVersion = tmpServiceTypeVersion;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (urlValue.toString().toLowerCase().indexOf("service=") == -1) {
                        if (url.indexOf("?") != -1) {
                            String[] nodeListParameters = xPathUtils.getStringArray(nodeList.item(i), "./../srv:parameters/srv:SV_Parameter/srv:name/gco:aName/*[self::gco:CharacterString or self::gmx:Anchor]");
                            for (int j=0; j<nodeListParameters.length;j++){
                                String parameter = nodeListParameters[j].trim();
                                if (!parameter.isEmpty()) {
                                    if (parameter.toLowerCase().indexOf("service=") > -1) {
                                        urlValue.append("&" + parameter);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    element.put("type", "textLabelLeft");
                    element.put("line", true);

                    HashMap elementCapabilitiesLink = new HashMap();
                    elementCapabilitiesLink.put("type", "linkLine");
                    elementCapabilitiesLink.put("hasLinkIcon", true);
                    elementCapabilitiesLink.put("isExtern", true);
                    elementCapabilitiesLink.put("title", messages.getString("search.detail.showGetCapabilityUrl.title"));
                    elementCapabilitiesLink.put("href", urlValue);
                    element.put("body", elementCapabilitiesLink);

                    if (!hasAccessConstraints()) {
                        element.put("title", messages.getString("search.detail.showGetCapabilityUrl"));
                        if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false)
                                && (serviceType != null && (serviceType.trim().equalsIgnoreCase("view") || serviceType.trim().equalsIgnoreCase("wms")))){
                            HashMap elementMapLink = new HashMap();
                            elementMapLink.put("type", "linkLine");
                            elementMapLink.put("hasLinkIcon", true);
                            elementMapLink.put("isExtern", false);
                            elementMapLink.put("title", messages.getString("common.result.showMap"));
                            elementMapLink.put("href", UtilsSearch.addMapclientCapabilitiesInformation(urlValue.toString(), serviceTypeVersion, serviceType)); 
                            if (xPathUtils.nodeExists(rootNode, "./idf:mapUrl")) {
                                elementMapLink.put("extMapUrl", xPathUtils.getString(rootNode, "./idf:mapUrl"));
                            }
                            element.put("link", elementMapLink);
                            element.put("linkLeft", true);
                        }
                    } else {
                        // do not display "show in map" link if the map has access constraints
                        element.put("title", messages.getString("search.detail.showGetCapabilityUrlRestricted"));
                    }
                    // ADD FIRST ONE FOUND !!!
                    break;
                }
            }
        }
        }
        return element;
    }

    public List<HashMap<String, Object>> getConnectionPointList(String xpathExpression) {
        ArrayList<HashMap<String, Object>> linkList = new ArrayList<>();
        String serviceType = getValueFromXPath("./gmd:identificationInfo/*/srv:serviceType");
        if(serviceType != null){
            serviceType = serviceType.trim();
        }
        String serviceTypeVersion = null;
        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);

            for (int i=0; i<nodeList.getLength();i++){
                if(xPathUtils.nodeExists(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL")){
                    Node node = xPathUtils.getNode(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL");
                    String url = xPathUtils.getString(node, ".").trim();
                    StringBuilder urlValue = new StringBuilder(url);
                    // do not display empty URLs
                    if (urlValue.length() == 0) {
                        continue;
                    }

                    // do not display empty operations
                    String operationName = xPathUtils.getString(nodeList.item(i), "./../srv:operationName").trim();
                    if (operationName == null || operationName.length() == 0) {
                        continue;
                    }
                    String description = "";
                    if(xPathUtils.nodeExists(nodeList.item(i), "./../srv:operationDescription")){
                        description = xPathUtils.getString(nodeList.item(i), "./../srv:operationDescription").trim();
                    }
                    
                    // add missing parameters
                    if(serviceType != null && (
                            serviceType.trim().equalsIgnoreCase("view") || 
                            serviceType.trim().equalsIgnoreCase("wms") || 
                            serviceType.trim().equalsIgnoreCase("wmts") || 
                            operationName.toLowerCase().trim().indexOf("getcap") > -1
                        )
                    ){
                        if (url.toLowerCase().indexOf("request=getcapabilities") == -1) {
                            if (url.indexOf("?") != -1) {
                                // if url or parameters already contains a ? or & at the end then do not add another one!
                                if (!(url.lastIndexOf("?") == url.length() - 1 || url.length() > 0)
                                        && !(url.lastIndexOf("&") == url.length() - 1)) {
                                    urlValue.append("&");
                                }
                                urlValue.append("REQUEST=GetCapabilities");
                            }
                        }
                        if (url.toLowerCase().indexOf("service=") == -1) {
                            if (url.indexOf("?") != -1) {
                                String[] nodeListServiceTypeVersions = xPathUtils.getStringArray(rootNode, "./gmd:identificationInfo/*/srv:serviceTypeVersion/*[self::gco:CharacterString or self::gmx:Anchor]");
                                for (int j=0; j<nodeListServiceTypeVersions.length;j++){
                                    String tmpServiceTypeVersion = nodeListServiceTypeVersions[j].trim();
                                    if (!tmpServiceTypeVersion.isEmpty()) {
                                        String tmpValue = CapabilitiesUtils.extractServiceFromServiceTypeVersion(tmpServiceTypeVersion);
                                        if (tmpValue != null) {
                                            if (!tmpValue.trim().contains(" ")) {
                                                urlValue.append("&SERVICE=" + tmpValue);
                                                serviceTypeVersion = tmpServiceTypeVersion;
                                                break;
                                            } else {
                                                urlValue = new StringBuilder(url);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (urlValue.toString().toLowerCase().indexOf("service=") == -1) {
                            if (url.indexOf("?") != -1) {
                                String[] nodeListParameters = xPathUtils.getStringArray(nodeList.item(i), "./../srv:parameters/srv:SV_Parameter/srv:name/gco:aName/*[self::gco:CharacterString or self::gmx:Anchor]");
                                for (int j=0; j<nodeListParameters.length;j++){
                                    String parameter = nodeListParameters[j].trim();
                                    if (!parameter.isEmpty()) {
                                        if (parameter.toLowerCase().indexOf("service=") > -1) {
                                            urlValue.append("&" + parameter);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HashMap elementCapabilitiesLink = new HashMap();
                    elementCapabilitiesLink.put("type", "linkLine");
                    elementCapabilitiesLink.put("hasLinkIcon", true);
                    elementCapabilitiesLink.put("isExtern", true);
                    elementCapabilitiesLink.put("title", urlValue);
                    elementCapabilitiesLink.put("href", urlValue);
                    if (!description.isEmpty()) {
                        elementCapabilitiesLink.put("description", description);
                    }
                    linkList.add(elementCapabilitiesLink);
                }
            }
        }
        return linkList;
    }
    public Map<String, Object> getAreaGeothesaurus(String xpathExpression){
        HashMap element = new HashMap();
        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList subjectEntries = new ArrayList();
            for (int i = 0; i < nodeList.getLength(); i++) {
                xpathExpression = "./gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code/gco:CharacterString";
                Node node = nodeList.item(i);
                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList childNodeList = xPathUtils.getNodeList(node, xpathExpression);
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        String domainValue = xPathUtils.getString(childNodeList.item(j), ".").trim();
                        subjectEntries.add(domainValue);
                    }
                }

                // "Geothesaurus-Raumbezug"
                xpathExpression = "./gmd:geographicElement/gmd:EX_GeographicBoundingBox";
                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList subNodeList = xPathUtils.getNodeList(node, xpathExpression);
                    element.put("type", "table");
                    element.put("title", messages.getString("geothesaurus_spacial_reference"));

                    ArrayList head = new ArrayList();
                    head.add("");
                    head.add(messages.getString("spatial_ref_value_x1"));
                    head.add(messages.getString("spatial_ref_value_y1"));
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

                        if(!subjectEntries.isEmpty()){
                            if (j < subjectEntries.size() && subjectEntries.get(j) != null) {
                                row.add(subjectEntries.get(j));
                            }else {
                                row.add("");
                            }
                        } else {
                            row.add("");
                        }

                        if (xPathUtils.nodeExists(childNode, "./gmd:westBoundLongitude") && xPathUtils.nodeExists(childNode, "./gmd:southBoundLatitude")) {
                            String valueW = xPathUtils.getString(childNode, "./gmd:westBoundLongitude").trim();
                            String valueS = xPathUtils.getString(childNode, "./gmd:southBoundLatitude").trim();
                            row.add((Math.round(Double.parseDouble(valueW) * 1000) / 1000.0) + "\u00B0/" + (Math.round(Double.parseDouble(valueS) * 1000) / 1000.0) + "\u00B0");
                        } else {
                            row.add("");
                        }
                        if (xPathUtils.nodeExists(childNode, "./gmd:eastBoundLongitude") && xPathUtils.nodeExists(childNode, "./gmd:northBoundLatitude")) {
                            String valueE = xPathUtils.getString(childNode, "./gmd:eastBoundLongitude").trim();
                            String valueN = xPathUtils.getString(childNode,  "./gmd:northBoundLatitude").trim();
                            row.add((Math.round(Double.parseDouble(valueE) * 1000) / 1000.0) + "\u00B0/" + (Math.round(Double.parseDouble(valueN) * 1000) / 1000.0) + "\u00B0");
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

    public Map<String, Object> getAreaHeight(String xpathExpression){
        HashMap element = new HashMap();
        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                xpathExpression = "./gmd:verticalElement";
                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    element.put("type", "table");
                    element.put("title", messages.getString("t01_object.vertical_extent"));

                    ArrayList head = new ArrayList();
                    head.add(messages.getString("t01_object.vertical_extent_minimum"));
                    head.add(messages.getString("t01_object.vertical_extent_maximum"));
                    head.add(messages.getString("t01_object.vertical_extent_unit"));
                    head.add(messages.getString("t01_object.vertical_extent_vdatum"));
                    element.put("head", head);
                    ArrayList body = new ArrayList();
                    element.put("body", body);
                    if (xPathUtils.nodeExists(node, "./gmd:verticalElement/gmd:EX_VerticalExtent")) {
                        NodeList subNodeList = xPathUtils.getNodeList(node, "./gmd:verticalElement/gmd:EX_VerticalExtent");
                        for (int j = 0; j < subNodeList.getLength(); j++) {
                            Node childNode = subNodeList.item(j);
                            ArrayList row = new ArrayList();

                            // "Minimum"
                            xpathExpression = "./gmd:minimumValue";
                            if(xPathUtils.nodeExists(childNode, xpathExpression)){
                                row.add(notNull(xPathUtils.getString(childNode, xpathExpression)).trim());
                            }

                            // "Maximum"
                            xpathExpression = "./gmd:maximumValue";
                            if(xPathUtils.nodeExists(childNode, xpathExpression)){
                                row.add(notNull(xPathUtils.getString(childNode, xpathExpression)).trim());
                            }

                            String rowValue;
                            // "Maßeinheit"
                            xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@uom";
                            if(xPathUtils.nodeExists(childNode, xpathExpression)){
                                rowValue = xPathUtils.getString(childNode, xpathExpression).trim();
                                row.add(notNull(sysCodeList.getNameByCodeListValue("102", rowValue)));
                            }else{
                                row.add("");
                            }

                            // "Vertikaldatum"
                            String verticalDatum = "" ;
                            xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:name";
                            if(xPathUtils.nodeExists(childNode, xpathExpression)){
                                rowValue = xPathUtils.getString(childNode, xpathExpression).trim();
                                if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
                                    verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
                                }else{
                                    verticalDatum = rowValue;
                                }
                            }

                            if(StringUtils.isEmpty(verticalDatum)) {
                                xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:identifier";
                                if(xPathUtils.nodeExists(childNode, xpathExpression)){
                                    rowValue = xPathUtils.getString(childNode, xpathExpression).trim();
                                    if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
                                        verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
                                    }else{
                                        verticalDatum = rowValue;
                                    }
                                }
                            }
                            if(StringUtils.isEmpty(verticalDatum)){
                                xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:name";
                                if(xPathUtils.nodeExists(childNode, xpathExpression)){
                                    rowValue = xPathUtils.getString(childNode, xpathExpression).trim();
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

    public List getPolygon(String xpathExpression) {
        List<String> result = new ArrayList<>();
        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node != null) {
                    try {
                        result.add(GmlToWktTransformUtil.gml3_2ToWktString(node));
                    } catch (Exception e) {
                        log.error("Error transform GML to string: ", e);
                    }
                }
            }
        }
        return result;
    }

    public List getNodeListValueReferenceSystem(String xpathExpression) {
        ArrayList<Map> list = new ArrayList<>();
        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength(); i++){
                String codeSpace = "";
                String code = "";
                xpathExpression = "./gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code";
                if (xPathUtils.nodeExists(nodeList.item(i), xpathExpression)) {
                    code = xPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                }

                xpathExpression = "./gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:codeSpace";
                if (xPathUtils.nodeExists(nodeList.item(i), xpathExpression)) {
                    codeSpace = xPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                }

                String value = "";
                if(!code.isEmpty() && !codeSpace.isEmpty()){
                    if(code.contains("EPSG")){
                        value = code;
                    }else{
                        value = codeSpace.concat(":" + code);
                    }
                }else if(!codeSpace.isEmpty()){
                    value = codeSpace;
                }else if(!code.isEmpty()){
                    value = code;
                }
                if (!value.isEmpty()) {
                    String[] startsWithReplaces = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_DETAIL_REFERENCE_SYSTEM_LINK_REPLACE);
                    for (String startsWithReplace : startsWithReplaces) {
                        if(value.startsWith(startsWithReplace)) {
                            value = value.replace(startsWithReplace, "");
                        }
                    }
                    Map link = new HashMap();
                    link.put("title", value);
                    link.put("hasLinkIcon", true);

                    Pattern p = Pattern.compile("EPSG( |:)[0-9]*");  // insert your pattern here
                    Matcher m = p.matcher(value);
                    if (m.find()) {
                       value = value.substring(m.start(), m.end());
                    }

                    if (value.startsWith("EPSG")) {
                        int endIndex = value.indexOf(':', 5);
                        String epsgCode = value.substring(5, endIndex == -1 ? value.length() : endIndex);

                        link.put("isExtern", true);
                        link.put("href", PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_REFERENCE_SYSTEM_LINK, "https://epsg.io/") + epsgCode);
                    } else {
                        link.put("noLink", true);
                    }
                    list.add(link);
                }
            }
        }
        return list;
    }

    public String getGeoReport(String xpathExpression, String checkDescription, String checkNameOfMeasure) {
        StringBuilder value = new StringBuilder("");
        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                String symbol = null;
                String nameOfMeasure = null;
                String description = null;
                String content = "";

                if(node != null){
                    xpathExpression = "./gmd:nameOfMeasure";
                    if(xPathUtils.nodeExists(node, xpathExpression)){
                        nameOfMeasure = xPathUtils.getString(node, xpathExpression).trim();
                    }
                    xpathExpression = "./gmd:measureDescription";
                    if(xPathUtils.nodeExists(node, xpathExpression)){
                        description = xPathUtils.getString(node, xpathExpression).trim();
                    }
                    xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:value";
                    if(xPathUtils.nodeExists(node, xpathExpression)){
                        content = xPathUtils.getString(node, xpathExpression).trim();
                    }
                    xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:UnitDefinition/gml:catalogSymbol";
                    if(xPathUtils.nodeExists(node, xpathExpression)){
                        symbol = xPathUtils.getString(node, xpathExpression).trim();
                    }

                    if ((description != null || nameOfMeasure != null) && (checkDescription.equals(description) || checkNameOfMeasure.equalsIgnoreCase(nameOfMeasure))){
                        value.append(content);
                        if(symbol != null){
                            value.append(" " + symbol);
                        }
                        break;
                    }
                }
            }
        }
        return value.toString();
    }

    public String addLinkToGetXML() {
        String htmlLink = null;
        String cswUrl = PortalConfig.getInstance().getString(PortalConfig.CSW_INTERFACE_URL, "");
        String id = this.uuid;
        if(id == null) {
            id = this.docid;
        }
        if (!cswUrl.isEmpty()) {
            htmlLink = "<a target=\"_blank\" class=\"external-link\" href=\""+cswUrl+"?REQUEST=GetRecordById&SERVICE=CSW&VERSION=2.0.2&id="+id+"&iplug="+this.iPlugId+"&elementSetName=full\">"+messages.getString("xml_link")+"</a>";
        }
        return htmlLink;
    }

    public HashMap addLinkElementToGetXML() {
        HashMap elementLink = null;
        String cswUrl = PortalConfig.getInstance().getString(PortalConfig.CSW_INTERFACE_URL, "");
        if (!cswUrl.isEmpty()) {
            String id = this.uuid;
            if(id == null) {
                id = this.docid;
            }
            elementLink = new HashMap();
            elementLink.put("type", "linkLine");
            elementLink.put("hasLinkIcon", true);
            elementLink.put("isDownload", true);
            elementLink.put("title", messages.getString("xml_link"));
            elementLink.put("href", cswUrl + "?REQUEST=GetRecordById&SERVICE=CSW&VERSION=2.0.2&id=" + id);
        }
        return elementLink;
    }

    public HashMap addLinkElementToGetRDF() {
        HashMap elementLink = null;
        String rdfUrl = PortalConfig.getInstance().getString(PortalConfig.RDF_INTERFACE_URL, "");
        if (!rdfUrl.isEmpty()) {
            String id = this.uuid;
            if(id != null) {
                elementLink = new HashMap();
                elementLink.put("type", "linkLine");
                elementLink.put("hasLinkIcon", true);
                elementLink.put("isDownload", true);
                elementLink.put("title", messages.getString("rdf_link"));
                elementLink.put("href", rdfUrl + "" + id);
            }
        }
        return elementLink;
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

    public Map<String, String> getDOI() {
        String doiId = getValueFromXPath("./idf:doi/id");
        String doiType = getValueFromXPath("./idf:doi/type");

        Map<String, String> element =null;
        if(doiId != null || doiType != null) {
            element = new HashMap<>();
            if(doiId != null) {
                element.put("id", doiId);
            }
            if(doiType != null) {
                element.put("type", doiType);
            }
        }

        return element;
    }


    public List getRegionKey() {
        String regionKey = getValueFromXPath("./idf:regionKey/key");
        String regionKeyUrl = getValueFromXPath("./idf:regionKey/url");
        ArrayList<Map> list = new ArrayList<>();

        if (regionKey != null && regionKeyUrl != null) {

            Map link = new HashMap();
            link.put("title", regionKey);
            link.put("hasLinkIcon", true);
            link.put("isExtern", true);
            link.put("href", regionKeyUrl);
            list.add(link);
        }
        return list;
    }

    /*
     * Private functiions
     *
     */
    private HashMap<String,Object> getPreviewImage(String xpathExpression) {
        ArrayList<HashMap<String, String>> urls = getPreviewImageUrl(xpathExpression);
        HashMap<String,Object> elementCapabilities = new HashMap<>();
        if(!urls.isEmpty()) {
            elementCapabilities.put("type", "multiLineImage");
            ArrayList<HashMap<String,Object>> list = new ArrayList<>();
            for (HashMap<String, String> url : urls) {
                // put link in a list so that it is aligned correctly in detail view (<div class="width_two_thirds">)
                HashMap<String,Object> elementMapLink = new HashMap<>();
                elementMapLink.put("type", "linkLine");
                elementMapLink.put("isMapLink", true);
                elementMapLink.put("isExtern", false);
                elementMapLink.put("title", messages.getString("preview"));
                elementMapLink.put("description", url.get("description"));
                elementMapLink.put("href", url.get("name"));
                elementMapLink.put("src", url.get("name"));
                list.add(elementMapLink);
            }
            elementCapabilities.put("elements", list);
            elementCapabilities.put("width", "full");
        }
        return elementCapabilities;
    }

    private String getLayerIdentifier(Node crossReference) {
        if (getUdkObjectClassType().equals("1")) {
            String href = xPathUtils.getString(rootNode, "./gmd:identificationInfo/gmd:MD_DataIdentification/@uuid");
            if (href != null) {
                return href;
            }
        } else {
            String origId = xPathUtils.getString(crossReference, "./@orig-uuid");
            String uuid   = xPathUtils.getString(crossReference, "./@uuid");
            String xpathExpression = "./gmd:identificationInfo/*/srv:operatesOn";

            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);

                String uuidRef = xPathUtils.getString(node, "./@uuidref");
                String href = xPathUtils.getString(node, "./@xlink:href");

                if (uuidRef != null && (uuidRef.equals(uuid) || uuidRef.equals(origId))) {
                    return href;
                }
            }
        }

        return "NOT_FOUND";
    }

    private HashMap<String, Object> addBigMapLink(String urlValue, boolean urlEncodeHref) {
        return addBigMapLink(rootNode, urlValue, urlEncodeHref);
    }

    private HashMap<String, Object> addBigMapLink(Node node, String urlValue, boolean urlEncodeHref) {
        return addBigMapLink(node, urlValue, urlEncodeHref, null, null);
    }

    private HashMap<String, Object> addBigMapLink(Node node, String urlValue, boolean urlEncodeHref, String partner, String extMapUrl) {
        HashMap<String, Object> elementCapabilities = new HashMap<>();
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        // use preview image if provided otherwise static image
        ArrayList<HashMap<String, String>> imageUrls = getPreviewImageUrl(null);
        if (imageUrls.isEmpty()) {
            HashMap<String, Object> elementMapLink = new HashMap<>();

            if (hasAccessConstraints(node) || !PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false) || urlValue == null) {
                // do not render "show in map" link if the map has access constraints (no href added).
                elementMapLink.put("title", messages.getString("preview"));
            } else {
                String href = urlValue;
                if(urlEncodeHref){
                    href = UtilsVelocity.urlencode(urlValue);
                }
                elementMapLink.put("title", messages.getString("common.result.showMap.tooltip.short"));
                elementMapLink.put("href", href);
            }
            if(extMapUrl != null) {
                elementMapLink.put("title", messages.getString("common.result.showMap.tooltip.short"));
                elementMapLink.put("extMapUrl", extMapUrl);
            }
            if(elementCapabilities.get("href") != null || elementMapLink.get("href") != null || elementCapabilities.get("extMapUrl") != null) {
                String mapImage = "image_map";
                if(partner != null) {
                    mapImage += "_" + partner;
                }
                mapImage += ".png";
                elementMapLink.put("src", "/ingrid-portal-apps/images/maps/" + mapImage);
                
            }
            if(!elementMapLink.isEmpty()) {
                elementMapLink.put("type", "linkLine");
                elementMapLink.put("isMapLink", true);
                elementMapLink.put("isExtern", false);
                list.add(elementMapLink);
            }
        } else {
            for (HashMap<String, String> imageUrl : imageUrls) {
                HashMap<String, Object> elementMapLink = new HashMap<>();

                if (hasAccessConstraints(node) || !PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false) || urlValue == null) {
                    // do not render "show in map" link if the map has access constraints (no href added).
                    elementMapLink.put("title", messages.getString("preview"));
                } else {
                    String href = urlValue;
                    if(urlEncodeHref){
                        href = UtilsVelocity.urlencode(urlValue);
                    }
                    elementMapLink.put("title", messages.getString("common.result.showMap.tooltip.short"));
                    elementMapLink.put("href", href);
                }
                elementMapLink.put("src", imageUrl.get("name"));
                if(elementMapLink.get("href") == null) {
                    elementMapLink.put("href", imageUrl.get("name"));
                }
                elementMapLink.put("description", imageUrl.get("description"));
                if(extMapUrl != null) {
                    elementMapLink.put("title", messages.getString("common.result.showMap.tooltip.short"));
                    elementMapLink.put("extMapUrl", extMapUrl);
                }
                if(!elementMapLink.isEmpty()) {
                    elementMapLink.put("type", "linkLine");
                    elementMapLink.put("isMapLink", true);
                    elementMapLink.put("isExtern", false);
                    list.add(elementMapLink);
                }
            }
        }
        if(!list.isEmpty()) {
            elementCapabilities.put("type", "multiLineImage");
            elementCapabilities.put("elements", list);
            elementCapabilities.put("width", "full");
        }
        return elementCapabilities;
    }

    private ArrayList<HashMap<String, String>> getPreviewImageUrl(String xpathExpression) {
        if (xpathExpression == null)
            xpathExpression = "./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/*[self::gco:CharacterString or self::gmx:Anchor]";

        ArrayList<HashMap<String, String>> values = new ArrayList<>();
        if (xPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("name", node.getTextContent());
                xpathExpression = "../../gmd:fileDescription/*[self::gco:CharacterString or self::gmx:Anchor]";
                if (xPathUtils.nodeExists(node, xpathExpression)) {
                    Node tmpNode = xPathUtils.getNode(node, xpathExpression);
                    map.put("description", tmpNode.getTextContent());
                }
                values.add(map);
            }
        }
        return values;
    }

    private String getCapabilityUrl() {
        return getCapabilityUrl(null);
    }

    private String getCapabilityUrl(String additionalURLContent) {
        String url = null;
        String serviceType = null;
        String serviceVersion = null;
        String xpathExpression = "";

        xpathExpression = "./gmd:identificationInfo/*/srv:serviceType";
        if(xPathUtils.nodeExists(this.rootNode, xpathExpression)){
            serviceType = xPathUtils.getString(this.rootNode, xpathExpression);
        }
        xpathExpression = "./gmd:identificationInfo/*/srv:serviceTypeVersion";
        if(xPathUtils.nodeExists(this.rootNode, xpathExpression)){
            serviceVersion = xPathUtils.getString(this.rootNode, xpathExpression);
        }
        Node capNode = xPathUtils.getNode( this.rootNode, "./gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata/srv:operationName/*[self::gco:CharacterString or self::gmx:Anchor][text() = 'GetCapabilities']/../../srv:connectPoint//gmd:URL");
        if(capNode != null && capNode.getTextContent() != null){
            url = UtilsSearch.addMapclientCapabilitiesInformation(capNode.getTextContent().trim(), serviceVersion, serviceType, additionalURLContent);
        }
        return url;
    }

    public boolean hasAccessConstraints() {
        return hasAccessConstraints(rootNode);
    }

    public boolean hasAccessConstraints(Node node) {
        boolean hasAccessConstraints = false;
        String xpathExpression = "./idf:hasAccessConstraint";
        if (xPathUtils.nodeExists(node, xpathExpression)) {
            String hasAccessConstraintsValue = xPathUtils.getString(node, xpathExpression).trim();
            if(hasAccessConstraintsValue.length() > 0){
                hasAccessConstraints = Boolean.parseBoolean(hasAccessConstraintsValue);
            }
        }
        return hasAccessConstraints;
    }

    private String transformAtomDownloadLink(String atomUrl, String url) {
        String atomObjUuid = url.substring(url.lastIndexOf("/")+1);
        String href = atomUrl + atomObjUuid;
        return href;
    }
}
