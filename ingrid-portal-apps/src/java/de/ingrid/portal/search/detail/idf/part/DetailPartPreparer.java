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
package de.ingrid.portal.search.detail.idf.part;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.json.JsonUtil;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeAlle;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeInterval;
import de.ingrid.utils.udk.UtilsCountryCodelist;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsLanguageCodelist;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class DetailPartPreparer {

    private static final Logger log = LoggerFactory.getLogger(DetailPartPreparer.class);

    protected NodeList                nodeList;
    protected IngridSysCodeList       sysCodeList;
    protected RenderRequest           request;
    protected RenderResponse          response;
    protected String                  iPlugId;
    protected String                  uuid;
    protected Context                 context;
    protected IngridResourceBundle    messages;
    
    protected String templateName = "";
    protected String localTagName = "";
    protected String namespaceUri = "";
    protected Node rootNode = null;

    protected XPathUtils xPathUtils = null;

    public enum LinkType {
        EMAIL, WWW_URL
    }
    
    public enum ReferenceType {
        SUBORDINATE, SUPERIOR, CROSS, OBJECT
    }
    
    public enum LabelType {
        LEFT, ABOVE, DURING
    }
    
    public enum TimeSpatialType {
        TIME, SPATIAL
    }
    
    /** Default initialization ! May be overwritten in subclasses. */
    public void init(Node node, String iPlugId, RenderRequest request, RenderResponse response, Context context) {
        this.rootNode = node;
        this.iPlugId = iPlugId;
        this.request = request;
        this.response = response;
        this.context = context;

        this.namespaceUri = IDFNamespaceContext.NAMESPACE_URI_IDF;
        this.messages = (IngridResourceBundle) context.get("MESSAGES");
        this.sysCodeList = new IngridSysCodeList(request.getLocale());
        this.uuid = this.request.getParameter("docuuid");

        xPathUtils = new XPathUtils(new IDFNamespaceContext());
    }

    public String getiPlugId() {
        return iPlugId;
    }

    public void setValueToContext(String key, String value) {
        this.context.put( key, value );
    }

    
    public String getValueFromXPath(String xpathExpression) {
        return getValueFromXPath(xpathExpression, null);
    }
    
    public String getValueFromXPath( String xpathExpression, String codeListId) {
        return getValueFromXPath(xpathExpression, codeListId, this.rootNode);
    }
    
    public String getValueFromXPath(String xpathExpression, String codeListId, Node root) {
        String value = null;
        Node node = xPathUtils.getNode(root, xpathExpression);
        if(node != null && node.getTextContent().length() > 0){
            value = node.getTextContent().trim();
            if(value != null && codeListId != null){
                String tmpValue = getValueFromCodeList(codeListId, value);
                if(tmpValue.length() > 0){
                    value = tmpValue;
                }
            }
            if(value != null){
                if(value.equals("false")){
                    value = messages.getString("general.no"); 
                }else if(value.equals("true")){
                    value = messages.getString("general.yes");
                }
            }
        }
        return value;
    }
    
    public String getDecodeValue(String value) {
        if (value != null){
           try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                log.error("Error on getDecodeValue.", e);
            }
        }
        return value;
    }
    
    public String getDateValueFromXPath(String xpathExpression) {
        String value = null;
        Node node = xPathUtils.getNode(this.rootNode, xpathExpression);
        if(node != null && node.getTextContent().length() > 0){
            value = node.getTextContent().trim();
            return getDateFormatValue(value);
        }
        return value;
    }

    public String getDateFormatValue (String value){
        try {
            Calendar cal = javax.xml.bind.DatatypeConverter.parseDateTime(value);
            if(cal != null){
                if(cal.getTime() != null){
                    int hours = cal.getTime().getHours();
                    int minutes = cal.getTime().getMinutes();
                    int seconds = cal.getTime().getSeconds();
                    if(hours > 0 || minutes > 0 || seconds > 0){
                        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
                    }
                }
                return new SimpleDateFormat("dd.MM.yyyy").format(cal.getTime());
            }
        } catch (Exception e) {
            log.error("Error on getDateFormatValue.", e);
        }
        return value;
    }
    
    public List<String> getListOfValuesFromXPath(String xpathExpression, String xpathSubExpression) {
        return getListOfValuesFromXPath(xpathExpression, xpathSubExpression, null);
    }
    
    public List<String> getListOfValuesFromXPath(String xpathExpression, String xpathSubExpression, String codeListId) {
        return getListOfValuesFromXPath(xpathExpression, xpathSubExpression, codeListId, null);
    }
    
    public List<String> getListOfValuesFromXPath(String xpathExpression, String xpathSubExpression, String codeListId, List<String> consideredValues) {
        ArrayList<String> list = new ArrayList<>();
        NodeList tmpNodeList = xPathUtils.getNodeList(this.rootNode, xpathExpression);
        if(tmpNodeList != null){
            for (int j=0; j < tmpNodeList.getLength();j++){
                Node nodeListNode = tmpNodeList.item(j);
                NodeList nodeListSub = xPathUtils.getNodeList(nodeListNode, xpathSubExpression);
                if(nodeListSub != null){
                    for (int i=0; i < nodeListSub.getLength();i++){
                        Node subNode = nodeListSub.item(i);
                        String value = subNode.getTextContent().trim();
                        if(value != null && value.length() > 0){
                            boolean isConsidered = false;
                            if(consideredValues != null){
                                for (int k=0; k < consideredValues.size();k++){
                                    if(value.equals(consideredValues.get(k))){
                                        isConsidered = true;
                                        break;
                                    }
                                }
                            }
                            if(!isConsidered){
                                if(codeListId != null){
                                    String tmpValue = getValueFromCodeList(codeListId, value);
                                    if(tmpValue.length() > 0){
                                        value = tmpValue;
                                    }
                                }
                                list.add(value);
                            }
                        }
                    }
                }
            }
        }
        sortList(list);
        return list;
    }

    public String removePraefix(String value) {
        String newValue = value;
        if (newValue != null) {
            if (newValue.startsWith( "Nutzungseinschränkungen: " )) {
                newValue = newValue.replace("Nutzungseinschränkungen: ", "");
            }
            if (newValue.startsWith( "Nutzungsbedingungen: " )) {
                newValue = newValue.replace("Nutzungsbedingungen: ", "");
            }            
        }
        
        return newValue;
    }

    public List<String> getUseConstraints() {
        final String restrictionCodeList = "524";
        final String licenceList = "6500";
        final String resourceConstraintsXpath = "//gmd:identificationInfo/*/gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions']";
        final String restrictionCodeXpath = "./gmd:MD_LegalConstraints/gmd:useConstraints/gmd:MD_RestrictionCode[not(@codeListValue='otherRestrictions')]";
        final String constraintsTextXpath = "./gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString";
        final String constraintsTextXpathAnchor = "./gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor";
        List<String> result = new ArrayList<>();

        NodeList resourceConstraintsNodes = xPathUtils.getNodeList(this.rootNode, resourceConstraintsXpath);
        if (resourceConstraintsNodes == null) {
            // Don't continue if no results found
            return result;
        }

        for(int i=0; i<resourceConstraintsNodes.getLength(); i++) {
            Node node = resourceConstraintsNodes.item(i);

            NodeList restrictionCodeNodes = xPathUtils.getNodeList(node, restrictionCodeXpath);
            NodeList constraintsNodes = xPathUtils.getNodeList(node, constraintsTextXpath + "|" + constraintsTextXpathAnchor);

            String restrictionCode = null;
            if (restrictionCodeNodes != null && restrictionCodeNodes.getLength() != 0) {

                NamedNodeMap attrs = restrictionCodeNodes.item(0).getAttributes();
                Node n = attrs.getNamedItem("codeListValue");
                if (n != null) {
                    restrictionCode = n.getTextContent();
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Discovered restriction code: %s", restrictionCode));
                    }
                    if (restrictionCode != null) {
                        restrictionCode = getValueFromCodeList(restrictionCodeList, restrictionCode);
                    }
                }
            }

            if (constraintsNodes == null || constraintsNodes.getLength() == 0) {
                continue;
            }

            String constraints = constraintsNodes.item(0).getTextContent();
            if(log.isDebugEnabled()) {
                log.debug(String.format("Discovered use constraints: %s", constraints));
            }

            // FIXME >>> Change after redmine ticket #848 is resolved >>>
            // Temporary solution to get rid of prefix in front of the codelist value
            constraints = removePraefix(constraints);
            // <<< End of temporary solution <<<

            if (constraints == null || constraints.trim().isEmpty()) {
                if (!result.contains(restrictionCode )) {
                    result.add(restrictionCode);
                }
                continue;
            }

            // try to get the license source from other constraints (#1066)
            String url = null;
            String name = null;
            // also remember further otherConstraints may be used in BKG profile (#1194)
            List<String> furtherOtherConstraints = new ArrayList<>();
            for (int indexConstraint=1; indexConstraint < constraintsNodes.getLength(); indexConstraint++) {
                String constraintSource = constraintsNodes.item(indexConstraint).getTextContent();
                if (constraintSource == null || constraintSource.trim().isEmpty()) {
                    log.warn("Empty otherConstraints ! We skip this one");
                    continue;
                }
                constraintSource = constraintSource.trim();
                // parse JSON
                boolean isJSON = false;
                try {
                    IngridDocument json = JsonUtil.parseJsonToIngridDocument(constraintSource);
                    url = (String) json.get("url");
                    name = (String) json.get("name");
                    isJSON = true;
                } catch (ParseException e) {
                    isJSON = false;
                    if (constraintSource.startsWith( "{" )) {
                        log.error("Error parsing json from use constraints '" + constraintSource + "'", e);
                    }
                }
                
                if (!isJSON) {
                    // no JSON but might be further other constraint (BKG), we also render !
                    furtherOtherConstraints.add( constraintSource );
                }
            }

            String finalValue = getValueFromCodeList(licenceList, constraints);
            if (finalValue == null || finalValue.trim().isEmpty()) {
                finalValue = constraints;
            }

            String value;
            String restrictionInfo = "";
            if (restrictionCode != null && restrictionCode.trim().length() > 0) {
                restrictionInfo = restrictionCode + ": ";
            }

            if (url != null && !url.trim().isEmpty()) {
                // we have a URL from JSON

                if (name != null && !name.trim().isEmpty() && !name.trim().equals( finalValue.trim() )) {
                    // we have a different license name from JSON, render it with link
                    value = String.format("%s<a target='_blank' href='%s'><svg class='icon'><use xlink:href='#external-link'></svg> %s</a><br>%s", restrictionInfo, url, name, finalValue);
                } else {
                    // no license name, render whole text with link
                    value = String.format("%s<a target='_blank' href='%s'><svg class='icon'><use xlink:href='#external-link'></svg> %s</a>", restrictionInfo, url, finalValue);
                }
            } else {
                // NO URL
                if (name != null && !name.trim().isEmpty() && !name.trim().equals( finalValue.trim() )) {
                    value = String.format("%s%s<br>%s", restrictionInfo, name, finalValue);
                } else if (restrictionCode != null){
                    value = String.format("%s%s", restrictionInfo, finalValue);
                } else {
                    value = finalValue;
                }
            }

            if (!result.contains(value)) {
                result.add(value);
            }
            
            // also add other constraints if present !
            for (String furtherConstraint : furtherOtherConstraints) {
                if (!result.contains(furtherConstraint)) {
                    result.add(furtherConstraint);                    
                }
            }
        }

        return result;
    }

    public List<String> getUseLimitations() {
        final String resourceConstraintsXpath = "//gmd:identificationInfo/*/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useLimitation/gco:CharacterString";

        List<String> result = new ArrayList<>();

        NodeList useLimitations = xPathUtils.getNodeList(this.rootNode, resourceConstraintsXpath);
        if (useLimitations == null) {
            // Don't continue if no results found
            return result;
        }

        for(int i=0; i<useLimitations.getLength(); i++) {
            Node node = useLimitations.item(i);

            // FIXME >>> Change after redmine ticket #848 is resolved >>>
            // Temporary solution to get rid of prefix in front of the codelist value
            String constraints = node.getTextContent();
            if(log.isDebugEnabled()) {
                log.debug(String.format("Discovered use limitations: %s", constraints));
            }

            int index = constraints.indexOf(':');
            if (index >= 0) {
                constraints = constraints.substring(index+1).trim();
            }
            if(log.isDebugEnabled()) {
                log.debug(String.format("Use limitations are now: %s", constraints));
            }
            // <<< End of temporary solution <<<

            if (constraints != null && !constraints.trim().isEmpty()) {
                result.add(constraints);
            }
        }

        return result;
    }

    public List<String> getSiblingsValuesFromXPath(String xpathExpression, String siblingType) {
        return getSiblingsValuesFromXPath(xpathExpression, siblingType, false);
    }
    
    public List<String> getSiblingsValuesFromXPath(String xpathExpression, String siblingType, boolean includeSelection) {
        return getSiblingsValuesFromXPath(xpathExpression, siblingType, includeSelection, null);
    }
    
    public List<String> getSiblingsValuesFromXPath(String xpathExpression, String siblingType, boolean includeSelection, String codeListId) {
        return getSiblingsValuesFromXPath(xpathExpression, siblingType, includeSelection, codeListId, null);
    }
    
    /** Get values of sibling(s) of node(s).<br>
     * NOTICE: values starting with "{" and ending with "}" (JSON !) are not included
     * @param xpathExpression get siblings of this node(s)
     * @param siblingNodeName name of the sibling nodes to return values from, pass null if all siblings matter
     * @param includeSelection include the value of the node of which the siblings are detected 
     * @param codeListId pass code list if values have to be transformed, else pass null
     * @param consideredValues values to skip
     * @return list of values to render
     */
    public List<String> getSiblingsValuesFromXPath(String xpathExpression, String siblingNodeName, boolean includeSelection, String codeListId, List<String> consideredValues) {
        ArrayList<String> list = new ArrayList<>();
        
        List<Node> siblingList = xPathUtils.getSiblingsFromXPath(rootNode, xpathExpression, siblingNodeName, includeSelection);
        if(siblingList == null) {
            return list;
        }

        for (Node sibling : siblingList) {
            String value = sibling.getTextContent().trim();
            if(value == null ||  value.length() == 0) {
                continue;
            }
            // exclude JSON values
            if (value.startsWith( "{" ) && value.endsWith( "}" )) {
                continue;                            
            }
            // exclude considered values
            if (consideredValues != null && consideredValues.contains( value )) {
                continue;
            }
            // transform with code list ?
            if(codeListId != null){
                String tmpValue = getValueFromCodeList(codeListId, value);
                if (tmpValue.length() == 0) {
                    tmpValue = getValueFromCodeList(codeListId, value, true);
                }
                if(tmpValue.length() > 0) {
                    value = tmpValue;
                }
            }
            if (!list.contains( value )) {
                list.add(value);
            }
        }
        sortList(list);
        return list;
    }

    public boolean nodeExist(String xpathExpression){
        return nodeExist(xpathExpression, this.rootNode);
    }
    
    public boolean nodeExist(String xpathExpression, Node node){
        return xPathUtils.nodeExists(node, xpathExpression);
    }
    
    public boolean aNodeOfListExist(List<String> xpathExpressions){
        boolean exists = false;
        if(xpathExpressions != null){
            for (int i=0; i<xpathExpressions.size();i++){
                boolean tmpExist = nodeExist(xpathExpressions.get(i));
                if(tmpExist){
                    return tmpExist;
                }
            }
        }
        return exists;
    }
    
    public Node getNodeFromXPath(String xpathExpression){
        return xPathUtils.getNode(this.rootNode, xpathExpression);
    }

    public NodeList getNodeListFromXPath(String xpathExpression){
        return getNodeListFromXPath(xpathExpression, this.rootNode);
    }
    
    public NodeList getNodeListFromXPath(String xpathExpression, Node node){
        return xPathUtils.getNodeList(node, xpathExpression);
    }
    
    public Map getTreeFromXPathBy(String xpathExpression, String xpathSubEntry, List<String> xpathSubEntryList){
        return getTreeFromXPathBy(xpathExpression, xpathSubEntry, xpathSubEntryList, this.rootNode);
    }
    
    public Map getTreeFromXPathBy(String xpathExpression, String xpathSubEntry, List<String> xpathSubEntryList, Node node){
        HashMap root = new HashMap();
        root.put("type", "root");
        NodeList tmpNodelist =  xPathUtils.getNodeList(node, xpathExpression);
        boolean createNewFolder = false;
        String xpathOldSubEntryValue = "";
        if(tmpNodelist != null) {
            for (int i=0; i<tmpNodelist.getLength();i++){
                Node tmpNode = tmpNodelist.item(i);
                if(tmpNode != null) {
                    HashMap leaf = new HashMap();
                    leaf.put("type", "leaf");
                    for (String entry : xpathSubEntryList) {
                        String value = getValueFromXPath(entry, null, tmpNode);
                        if(value != null) {
                            leaf.put(entry, value);
                        }
                    }
                     // Create tree structure
                    String xpathSubEntryValue = getValueFromXPath(xpathSubEntry, null, tmpNode);
                    if(xpathSubEntryValue != null) {
                        if(xpathSubEntryValue.startsWith("http")) {
                           if (root.get("children") == null) {
                               root.put( "children", new ArrayList<HashMap>() );
                           }
                           ArrayList rootChildren = (ArrayList) root.get("children");
                           rootChildren.add(leaf);
                           createNewFolder = true;
                        } else {
                            String[] paths = xpathSubEntryValue.split("/");
                            String[] pathsOld = xpathOldSubEntryValue.split("/");
                            if(paths != null) {
                                int counter = 0;
                                HashMap folder = root;
                                HashMap parentFolder = null;
                                while (counter != paths.length) {
                                    String path = paths[counter];
                                    // Remove directory path in label
                                    if(leaf != null && leaf.get("label") != null) {
                                        String label = (String) leaf.get("label");
                                        if(label != null && label.indexOf('/') > -1 && path.length() > 0) {
                                            label = label.replaceFirst(getDecodeValue(path) + "/", "");
                                        }
                                        leaf.put("label", label);
                                    }
                                    counter++;
                                    if(counter > PortalConfig.getInstance().getInt(PortalConfig.PORTAL_DETAIL_UPLOAD_PATH_INDEX, 4) && path.length() != 0) {
                                        if(folder.get("children") == null) {
                                            folder.put( "children", new ArrayList<HashMap>() );
                                        }
                                        ArrayList<HashMap> children = (ArrayList) folder.get("children");
                                        
                                        HashMap subMap = null;
                                        for (int j=children.size()-1; j>=0;j--){
                                            HashMap tmpMap = children.get(j);
                                            if(tmpMap.get("type").equals("folder") && tmpMap.get("label").equals(path)) {
                                                subMap = tmpMap;
                                                break;
                                            }
                                        }
                                        if(counter != paths.length) {
                                            if(subMap != null) {
                                                if(counter < pathsOld.length && !path.equals(pathsOld[counter-1])) {
                                                    if(parentFolder != null && parentFolder.get("children") != null) {
                                                        children = (ArrayList) parentFolder.get("children");
                                                        createNewFolder = true;
                                                    }
                                                } else {
                                                    parentFolder = subMap;
                                                }
                                            }
                                            if(subMap == null || createNewFolder) {
                                                subMap = new HashMap();
                                                subMap.put("type", "folder");
                                                subMap.put("label", path);
                                                children.add(subMap);
                                                folder.put("children", children);
                                                createNewFolder = false;
                                            }
                                        } else {
                                            // Add leaf to folder
                                            subMap = leaf;
                                            children.add(subMap);
                                            folder.put("children", children);
                                        }
                                        folder = subMap;
                                    }
                                }
                            }
                        }
                    }
                    xpathOldSubEntryValue = xpathSubEntryValue;
                }
            }
        }
        return root;
    }

    public String getValueFromNodeListDependOnValue(NodeList nodeList, String xpathExpression, String xpathExpressionDependOn, String dependOn){
        String value = "";
        if(nodeList != null){
            for (int i=0; i<nodeList.getLength();i++){
                Node node = nodeList.item(i);
                if(xPathUtils.nodeExists(node, xpathExpressionDependOn)){
                    String xpathValue = xPathUtils.getString(node, xpathExpressionDependOn).trim();
                    if(xpathValue.equals(dependOn)){
                        value = xPathUtils.getString(node, xpathExpression).trim();
                        break;
                    }
                }
            }
        }
        return value;
    }
    
    public Map<String, Object> getNodeListTable(String title, String xpathExpression, List<String> headTitles, List<String> headXpathExpressions) {
        return getNodeListTable(title, xpathExpression, headTitles, headXpathExpressions, null);
    }
    
    public Map<String, Object> getNodeListTable(String title, String xpathExpression, List<String> headTitles, List<String> headXpathExpressions, List<String> headCodeList) {
        return getNodeListTable(title, xpathExpression, headTitles, headXpathExpressions, headCodeList, null);
    }
    
    public Map<String, Object> getNodeListTable(String title, String xpathExpression, List<String> headTitles, List<String> headXpathExpressions, List<String> headCodeList, List<String> headTypes) {
        HashMap<String, Object> element = new HashMap<>();
        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList tmpNodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            
            element.put("type", "table");
            element.put("title", title);
            
            ArrayList<String> head = new ArrayList<>();
            head.addAll(headTitles);
            element.put("head", head);
            if(headTypes != null){
                ArrayList<String> types = new ArrayList<>();
                types.addAll(headTypes);
                element.put("types", types);
            }
            ArrayList<ArrayList<String>> body = new ArrayList<>();
            element.put("body", body);
            
            for (int i=0; i<tmpNodeList.getLength();i++){
                Node node = tmpNodeList.item(i);
                ArrayList<String> row = new ArrayList<>();
                
                for (int j=0; j<headXpathExpressions.size();j++){
                    String headXpathExpression = headXpathExpressions.get(j);
                    if(xPathUtils.nodeExists(node, headXpathExpression)){
                        NodeList valueNodeList = xPathUtils.getNodeList(node, headXpathExpression);
                        StringBuilder valueConcated = new StringBuilder("");
                        for (int k=0; k<valueNodeList.getLength();k++) {
                            if (valueConcated.length() > 0) {
                                valueConcated.append(";");
                            }
                            String value = valueNodeList.item( k ).getTextContent().trim();
                            if(headXpathExpression.endsWith("date")){
                                value = UtilsDate.convertDateString(value, "yyyy-MM-dd", "dd.MM.yyyy");
                                valueConcated.append(value);
                                break;
                            }
                            if(headCodeList != null){
                                for (int l=0; l<headCodeList.size();l++){
                                    String codelist = headCodeList.get(l);
                                    String tmpValue = sysCodeList.getNameByCodeListValue(codelist, value).trim();
                                    if(tmpValue.length() > 0){
                                        value = tmpValue;
                                        valueConcated.append(value);
                                        break;
                                    }
                                }
                            } else {
                                valueConcated.append(value);
                            }
                        }
                        row.add(valueConcated.toString());
                    }else{
                        row.add("");
                    }
                }
                
                if (!isEmptyRow(row)) {
                    body.add(row);
                }
            }
        }
        return element;
    }
    
    public StringTokenizer stringTokenizer(String value){
        return new StringTokenizer(value, ",");
    }
    
    public String valueHTMLEscape(String value){
        if(value != null){
            value = value.replaceAll("\n", "<br/>");
            value = value.replaceAll("&lt;", "<");
            value = value.replaceAll("&gt;", ">");
        }
        return value;
    }
    
    public String getValueFromCodeList(String codelist, String value){
        return getValueFromCodeList(codelist, value, false);
    }

    public String getValueFromCodeList(String codelist, String value, boolean checkDataId){
        if(value != null){
            value = sysCodeList.getNameByCodeListValue(codelist, value, checkDataId);
        }
        return value;
    }

    public String getNameFromCodeList(String codelist, String value){
        if(value != null){
            value = sysCodeList.getName(codelist, value);
        }
        return value;
    }
    

    public String getLanguageValue(String value){
        return UtilsLanguageCodelist.getNameFromIso639_2(value, this.request.getLocale().getLanguage());
    }

    public List<String> getLanguageValues(List<String> keys){
        ArrayList<String> myList = new ArrayList<>();
        for(String key : keys) {
            String langValue = getLanguageValue( key );
            if (langValue != null) {
                myList.add( langValue );
            }
        }
        return myList;
    }

    public String getCountryValue(String value){
        if(UtilsCountryCodelist.getCodeFromShortcut3(value) == null){
            return value;
        }
        return UtilsCountryCodelist.getNameFromCode(UtilsCountryCodelist.getCodeFromShortcut3(value), this.request.getLocale().getLanguage());
    }
    public List<String> mergeList(List<String> list1, List<String> list2){
        ArrayList<String> mergedList = new ArrayList<>();
        if(list1 != null){
            mergedList.addAll(list1);    
        }
        
        if(list2 != null){
            mergedList.addAll(list2);    
        }
        sortList(mergedList);
        return mergedList; 
    }
    
    public void sortList(List<String> list){
        Collections.sort(list, new Comparator<Object>(){
            public int compare(Object left, Object right){
                String leftKey = (String)left;
                String rightKey = (String)right;
                return leftKey.toLowerCase().compareTo(rightKey.toLowerCase());
            }
        });
    }
    
    public String notNull(String in) {
        if (in == null) {
            return "";
        } else {
            return in;
        }
    }
    
    public boolean isEmptyList(Map listEntry) {
        boolean isEmptyList = true;
        if ((listEntry.get("type") != null && listEntry.get("type").equals("textList") || listEntry.get("type").equals("linkList")) &&
                (listEntry.get("body") instanceof String && ((String) listEntry.get("body")).length() > 0)) {
            isEmptyList = false;
        }
        return isEmptyList;
    }
    
    public int getGreatestInt(List numbers) {
        int i = 0;
        int maximum = Integer.parseInt(numbers.get(i).toString());
        while (i < numbers.size()) {
            if (Integer.parseInt(numbers.get(i).toString()) > maximum) {
                maximum =  Integer.parseInt(numbers.get(i).toString());
            }
            i++;
        }
        return maximum;
    }
    
    public boolean isEmptyRow(List row) {
        for (int i = 0; i < row.size(); i++) {
            if (row.get(i) instanceof String && ((String) row.get(i)).length() > 0) {
                return false;
            }
        }
        return true;
    }
    
    public String getIndividualName(String value) {
        String[] valueSpitter = value.split(",");
        
        StringBuilder name = new StringBuilder("");
        for (int j=valueSpitter.length; 0 < j ;j--){
            name.append(" " + valueSpitter[j-1]);
        }    
        return name.toString();
    }
    
    public String timePeriodDurationToTimeAlle(String value){
        if(value != null){
            String content = new TM_PeriodDurationToTimeAlle().parse(value);
            if(content.length() > 0){
                return content;
            }else{
                return value;
            }
        }
        return null;
    }

    public String timePeriodDurationToTimeInterval(String value){
        if(value != null){
            String content = new TM_PeriodDurationToTimeInterval().parse(value);
            if(content.length() > 0){
                return content;
            }else{
                return value;
            }
        }
        return null;
    }
    
    public String convertDateString(String value){
        if(value != null){
            if(value.indexOf('T') > -1){
                String[] split = value.split("T");
                String content = UtilsDate.convertDateString(split[0], "yyyy-MM-dd", "dd.MM.yyyy");
                if(split[1].equals("00:00:00")){
                    return content;
                }
                return content + " " + split[1];
            }else{
                String content = UtilsDate.convertDateString(value, "yyyy-MM-dd", "dd.MM.yyyy");
                if(content.length() > 0){
                    return content;
                }else{
                    return value;
                }
            }
        }
        return null;
    }
    
    public String getLanguage(){
        return this.request.getLocale().getLanguage();
    }
    
    public void addSpace(List<HashMap<String, String>> elements) {
        HashMap<String, String> element = new HashMap<>();
        element.put("type", "space");
        elements.add(element);
    }
    
    protected HashMap addElementEmailWeb(String title, String href, String body, String altText, LinkType linkType) {
        HashMap element = new HashMap();
        element.put("type", "textLinkLine");
        element.put("title", title);
        element.put("href", "mailto:".concat(UtilsString.htmlescapeAll(href)));
        element.put("body", UtilsString.htmlescapeAll(body));
        element.put("altText", UtilsString.htmlescapeAll(altText));
        switch (linkType) {
            case EMAIL:
                element.put("href", "mailto:".concat(UtilsString.htmlescapeAll(href)));
                break;
            case WWW_URL:
                if (href.startsWith("http")) {
                    element.put("href", href);
                } else {
                    element.put("href", "http://".concat(href));
                }
                break;
            default:
                break;
        }
        
        return element;
    }
    
    protected void addElement(ArrayList elements, String type, String body) {
        addElement(elements, type, body, null);
    }
    
    protected void addElement(ArrayList elements, String type, String body, String title) {
        HashMap element = new HashMap();
        if (type != null)
            element.put("type", type);
        if (body != null)
            element.put("body", body);
        if (title != null)
            element.put("title", title);
        
        elements.add(element);
    }
    
    protected HashMap addElementLink(String type, Boolean hasLinkIcon, Boolean isExtern, String title, String uuid) {
        HashMap element = new HashMap();
        if (type != null)
            element.put("type", type);
        if (title != null)
            element.put("title", title);
        if (hasLinkIcon)
            element.put("sort", hasLinkIcon);
        if (isExtern != null)
            element.put("isExtern", isExtern);
        
        if (this.iPlugId != null){
            if(uuid != null){
                element.put("href", "?cmd=doShowAddressDetail&docuuid="+uuid+"&plugid="+this.iPlugId);
            }else{
                element.put("href", "");
            }
        }else{
            element.put("href", "");
        }
            
        return element;
    }
    
    protected HashMap addElementAddress(String type, String title, String body, String sort, ArrayList elements) {
        HashMap element = new HashMap();
        if (type != null)
            element.put("type", type);
        if (title != null)
            element.put("title", title);
        if (sort != null)
            element.put("sort", sort);
        if (body != null)
            element.put("body", body);
        if (elements != null)
            element.put("elements", elements);
        
        return element;
    }
    
    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getLocalTagName() {
        return localTagName;
    }

    public void setLocalTagName(String localTagName) {
        this.localTagName = localTagName;
    }

    public String getNamespaceUri() {
        return namespaceUri;
    }

    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }
}
