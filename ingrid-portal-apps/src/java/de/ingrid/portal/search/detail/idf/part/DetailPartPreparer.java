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
package de.ingrid.portal.search.detail.idf.part;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeAlle;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeInterval;
import de.ingrid.utils.udk.UtilsCountryCodelist;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsLanguageCodelist;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class DetailPartPreparer {

    private final static Logger log = LoggerFactory.getLogger(DetailPartPreparer.class);

    public NodeList                nodeList;
    public IngridSysCodeList       sysCodeList;
    public RenderRequest           request;
    public RenderResponse          response;
    public String                  iPlugId;
    public String                  uuid;
    public Context                 context;
    public IngridResourceBundle    messages;
    
    public String templateName = "";
    public String localTagName = "";
    public String namespaceUri = "";
    public Node rootNode = null;

    protected XPathUtils XPathUtils = null;

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

        XPathUtils = new XPathUtils(new IDFNamespaceContext());
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
        Node node = XPathUtils.getNode(root, xpathExpression);
        if(node != null){
            if(node.getTextContent().length() > 0){
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
        }
        return value;
    }
    
    public String getDateValueFromXPath(String xpathExpression) {
        String value = null;
        Node node = XPathUtils.getNode(this.rootNode, xpathExpression);
        if(node != null){
            if(node.getTextContent().length() > 0){
                value = node.getTextContent().trim();
                return getDateFormatValue(value);
            }
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
            }
            return new SimpleDateFormat("dd.MM.yyyy").format(cal.getTime());
        } catch (Exception e) {
        }
        return value;
    }
    
    public ArrayList<String> getListOfValuesFromXPath(String xpathExpression, String xpathSubExpression) {
        return getListOfValuesFromXPath(xpathExpression, xpathSubExpression, null);
    }
    
    public ArrayList<String> getListOfValuesFromXPath(String xpathExpression, String xpathSubExpression, String codeListId) {
        return getListOfValuesFromXPath(xpathExpression, xpathSubExpression, codeListId, null);
    }
    
    public ArrayList<String> getListOfValuesFromXPath(String xpathExpression, String xpathSubExpression, String codeListId, ArrayList<String> consideredValues) {
        ArrayList<String> list = new ArrayList<String>();
        NodeList nodeList = XPathUtils.getNodeList(this.rootNode, xpathExpression);
        if(nodeList != null){
            for (int j=0; j < nodeList.getLength();j++){
                Node nodeListNode = nodeList.item(j);{
                    NodeList nodeListSub = XPathUtils.getNodeList(nodeListNode, xpathSubExpression);
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
        }
        sortList(list);
        return list;
    }

    public ArrayList<String> getSiblingsValuesFromXPath(String xpathExpression, String siblingType) {
        return getSiblingsValuesFromXPath(xpathExpression, siblingType, false);
    }
    
    public ArrayList<String> getSiblingsValuesFromXPath(String xpathExpression, String siblingType, boolean includeSelection) {
        return getSiblingsValuesFromXPath(xpathExpression, siblingType, includeSelection, null);
    }
    
    public ArrayList<String> getSiblingsValuesFromXPath(String xpathExpression, String siblingType, boolean includeSelection, String codeListId) {
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
    public ArrayList<String> getSiblingsValuesFromXPath(String xpathExpression, String siblingNodeName, boolean includeSelection, String codeListId, ArrayList<String> consideredValues) {
        ArrayList<String> list = new ArrayList<String>();
        
        List<Node> siblingList = XPathUtils.getSiblingsFromXPath(rootNode, xpathExpression, siblingNodeName, includeSelection);
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
            if (consideredValues != null) {
                if (consideredValues.contains( value )) {
                    continue;
                }
            }
            // transform with code list ?
            if(codeListId != null){
                String tmpValue = getValueFromCodeList(codeListId, value);
                if (tmpValue.length() > 0) {
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
        return XPathUtils.nodeExists(node, xpathExpression);
    }
    
    public boolean aNodeOfListExist(ArrayList<String> xpathExpressions){
        boolean exists = false;
        if(xpathExpressions != null){
            for (int i=0; i<xpathExpressions.size();i++){
                boolean tmpExist = nodeExist(xpathExpressions.get(i));
                if(tmpExist == true){
                    return tmpExist;
                }
            }
        }
        return exists;
    }
    
    public Node getNodeFromXPath(String xpathExpression){
        return XPathUtils.getNode(this.rootNode, xpathExpression);
    }

    public NodeList getNodeListFromXPath(String xpathExpression){
        return getNodeListFromXPath(xpathExpression, this.rootNode);
    }
    
    public NodeList getNodeListFromXPath(String xpathExpression, Node node){
        return XPathUtils.getNodeList(node, xpathExpression);
    }
    
    public String getValueFromNodeListDependOnValue(NodeList nodeList, String xpathExpression, String xpathExpressionDependOn, String dependOn){
        String value = "";
        if(nodeList != null){
            for (int i=0; i<nodeList.getLength();i++){
                Node node = nodeList.item(i);
                if(XPathUtils.nodeExists(node, xpathExpressionDependOn)){
                    String xpathValue = XPathUtils.getString(node, xpathExpressionDependOn).trim();
                    if(xpathValue.equals(dependOn)){
                        value = XPathUtils.getString(node, xpathExpression).trim();
                        break;
                    }
                }
            }
        }
        return value;
    }
    
    public HashMap<String, Object> getNodeListTable(String title, String xpathExpression, ArrayList<String> headTitles, ArrayList<String> headXpathExpressions) {
        return getNodeListTable(title, xpathExpression, headTitles, headXpathExpressions, null);
    }
    
    public HashMap<String, Object> getNodeListTable(String title, String xpathExpression, ArrayList<String> headTitles, ArrayList<String> headXpathExpressions, ArrayList<String> headCodeList) {
        return getNodeListTable(title, xpathExpression, headTitles, headXpathExpressions, headCodeList, null);
    }
    
    public HashMap<String, Object> getNodeListTable(String title, String xpathExpression, ArrayList<String> headTitles, ArrayList<String> headXpathExpressions, ArrayList<String> headCodeList, ArrayList<String> headTypes) {
        HashMap<String, Object> element = new HashMap<String, Object>();
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            
            element.put("type", "table");
            element.put("title", title);
            
            ArrayList<String> head = new ArrayList<String>();
            head.addAll(headTitles);
            element.put("head", head);
            if(headTypes != null){
                ArrayList<String> types = new ArrayList<String>();
                types.addAll(headTypes);
                element.put("types", types);
            }
            ArrayList<ArrayList<String>> body = new ArrayList<ArrayList<String>>();
            element.put("body", body);
            
            for (int i=0; i<nodeList.getLength();i++){
                Node node = nodeList.item(i);
                ArrayList<String> row = new ArrayList<String>();
                
                for (int j=0; j<headXpathExpressions.size();j++){
                    String headXpathExpression = headXpathExpressions.get(j);
                    if(XPathUtils.nodeExists(node, headXpathExpression)){
                        String value = XPathUtils.getString(node, headXpathExpression).trim();
                        if(headXpathExpression.endsWith("date")){
                            value = UtilsDate.convertDateString(value, "yyyy-MM-dd", "dd.MM.yyyy");
                        }
                        if(headCodeList != null){
                            for (int k=0; k<headCodeList.size();k++){
                                String codelist = headCodeList.get(k);
                                String tmpValue = sysCodeList.getNameByCodeListValue(codelist, value).trim();
                                if(tmpValue.length() > 0){
                                    value = tmpValue;
                                    break;
                                }
                            }
                        }
                        row.add(value);
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
        if(value != null){
            value = sysCodeList.getNameByCodeListValue(codelist, value);
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
        return UtilsLanguageCodelist.getNameFromIso639_2(value, this.request.getLocale().getLanguage().toString());
    }

    public String getCountryValue(String value){
        if(UtilsCountryCodelist.getCodeFromShortcut3(value) == null){
            return value;
        }
        return UtilsCountryCodelist.getNameFromCode(UtilsCountryCodelist.getCodeFromShortcut3(value), this.request.getLocale().getLanguage().toString());
    }
    public ArrayList<String> mergeList(ArrayList<String> list1, ArrayList<String> list2){
        ArrayList<String> mergedList = new ArrayList<String>();
        if(list1 != null){
            mergedList.addAll(list1);    
        }
        
        if(list2 != null){
            mergedList.addAll(list2);    
        }
        sortList(mergedList);
        return mergedList; 
    }
    
    public void sortList(ArrayList<String> list){
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
    
    public boolean isEmptyList(HashMap listEntry) {
        if (listEntry.get("type") != null && listEntry.get("type").equals("textList") || listEntry.get("type").equals("linkList")) {
            if (listEntry.get("body") != null && listEntry.get("body") instanceof String && ((String) listEntry.get("body")).length() > 0) {
                return false;
            }
        }
        return true;
    }
    
    public int getGreatestInt(ArrayList numbers) {
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
            if (row.get(i) != null && row.get(i) instanceof String && ((String) row.get(i)).length() > 0) {
                return false;
            }
        }
        return true;
    }
    
    public String getIndividualName(String value) {
        String[] valueSpitter = value.split(",");
        
        String name = "";
        for (int j=valueSpitter.length; 0 < j ;j--){
            name = name + " " + valueSpitter[j-1];
        }    
        return name;
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
            if(value.indexOf("T") > -1){
                String[] split = value.split("T");
                String content = UtilsDate.convertDateString(split[0], "yyyy-MM-dd", "dd.MM.yyyy");
                if(split[1].equals("00:00:00")){
                    return content;
                }
                return content + " " + split[1];
            }else{
                String content = UtilsDate.convertDateString(value, "yyyy-MM-dd", "dd.MM.yyyy");;
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
        HashMap<String, String> element = new HashMap<String, String>();
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
