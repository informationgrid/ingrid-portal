/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.json.JsonUtil;
import de.ingrid.utils.udk.*;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;
import org.apache.velocity.context.Context;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.bind.DatatypeConverter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class DetailPartPreparer {

    private static final Logger log = LoggerFactory.getLogger(DetailPartPreparer.class);

    protected NodeList                nodeList;
    protected IngridSysCodeList       sysCodeList;
    protected RenderRequest           request;
    protected RenderResponse          response;
    protected String                  iPlugId;
    protected String                  uuid;
    protected String                  docid;
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
        this.sysCodeList = context.get("codeList") != null ? (IngridSysCodeList) context.get("codeList") : new IngridSysCodeList(request.getLocale());
        this.uuid = this.request.getParameter("docuuid");
        this.docid = this.request.getParameter("docid");

        xPathUtils = new XPathUtils(new IDFNamespaceContext());
    }

    public String getiPlugId() {
        return iPlugId;
    }

    public void setValueToContext(String key, String value) {
        this.context.put( key, value );
    }

    public void setTitleElementValueToContext(String key, String value) {
        if(this.rootNode.getNodeName().equalsIgnoreCase(value)) {
            if(this.context.get(key) == null) {
                this.context.put( key, this.rootNode.getTextContent() );
            }
        }
    }


    public String getValueFromXPath(String xpathExpression) {
        return getValueFromXPath(xpathExpression, null);
    }

    public String getValueFromXPath( String xpathExpression, String codeListId) {
        return getValueFromXPath(xpathExpression, codeListId, this.rootNode);
    }

    public String getValueFromXPath(String xpathExpression, String codeListId, Node root) {
        String value = null;
        if(xPathUtils.nodeExists(root, xpathExpression)) {
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
        }
        if(value != null) {
            value = removeLocalisation(value);
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

    public String getDateValueWithoutTimeFromXPath(String xpathExpression) {
        return getDateValueFromXPath(xpathExpression, false);
    }

    public String getDateValueFromXPath(String xpathExpression) {
        return getDateValueFromXPath(xpathExpression, true);
    }

    public String getDateValueFromXPath(String xpathExpression, Boolean withTime) {
        String value = null;
        Node node = xPathUtils.getNode(this.rootNode, xpathExpression);
        if(node != null && node.getTextContent().length() > 0){
            value = node.getTextContent().trim();
            return getDateFormatValue(value, withTime);
        }
        return value;
    }

    public String getDateFormatValue (String value) {
        return getDateFormatValue(value, true);
    }

    public String getDateFormatValue (String value, Boolean withTime) {
        if(value != null && !value.isEmpty()) {
            try {
                Calendar cal = DatatypeConverter.parseDateTime(value);
                if(cal != null){
                    if(cal.getTime() != null && withTime){
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
                if(log.isDebugEnabled())
                    log.debug("Error on getDateFormatValue() for input: " + value);
            }
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
            if (newValue.startsWith( "Nutzungseinschränkungen:" )) {
                newValue = newValue.replace("Nutzungseinschränkungen:", "");
            }
            if (newValue.startsWith( "Nutzungsbedingungen:" )) {
                newValue = newValue.replace("Nutzungsbedingungen:", "");
            }
        }

        return newValue.trim();
    }

    public List<String> getUseConstraints() {
        return getUseConstraints(true, false);
    }
    public List<String> getUseConstraints(boolean displayJSON, boolean replaceUseConstraintsSourcePrefix) {
        final String restrictionCodeList = "524";
        final String licenceList = "6500";
        final String resourceConstraintsXpath = "//gmd:identificationInfo/*/gmd:resourceConstraints/gmd:MD_LegalConstraints[gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions']";
        final String restrictionCodeXpath = "./gmd:useConstraints/gmd:MD_RestrictionCode[not(@codeListValue='otherRestrictions')]";
        final String constraintsTextXpath = "./gmd:otherConstraints/*[self::gco:CharacterString or self::gmx:Anchor]";
        final String constraintsTextXpathAnchor = "./gmd:otherConstraints/gmx:Anchor";
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

            // also remember further otherConstraints may be used in BKG profile (#1194)
            List<String> furtherOtherConstraints = new ArrayList<>();
            for (int indexConstraint = 0; indexConstraint < constraintsNodes.getLength(); indexConstraint++) {

                String constraints = constraintsNodes.item(indexConstraint).getTextContent();
                if(log.isDebugEnabled()) {
                    log.debug(String.format("Discovered use constraints: %s", constraints));
                }

                // FIXME >>> Change after redmine ticket #848 is resolved >>>
                // Temporary solution to get rid of prefix in front of the codelist value
                constraints = removePraefix(constraints);
                // <<< End of temporary solution <<<

                if (constraints == null || constraints.trim().isEmpty()) {
                    if (restrictionCode != null && !result.contains(restrictionCode )) {
                        result.add(restrictionCode);
                    }
                    continue;
                }


                String constraintSource = constraintsNodes.item(indexConstraint).getTextContent();
                if (constraintSource == null || constraintSource.trim().isEmpty()) {
                    log.warn("Empty otherConstraints ! We skip this one");
                    continue;
                }
                constraintSource = removePraefix(constraintSource);
                // parse JSON
                boolean isJSON = false;
                // try to get the license source from other constraints (#1066)
                String url = null;
                String name = null;
                String source = null;
                try {
                    IngridDocument json = JsonUtil.parseJsonToIngridDocument(constraintSource);
                    if(json != null) {
                        if(json.containsKey("url") && json.containsKey("name") && json.containsKey("quelle")) {
                            url = (String) json.get("url");
                            name = (String) json.get("name");
                            source = (String) json.get("quelle");
                            isJSON = true;
                        } else {
                            log.error("Other JSON-Format! We skip this one: " + constraintSource);
                            continue;
                        }
                    } else {
                        isJSON = false;
                    }
                } catch (ParseException e) {
                    isJSON = false;
                    if (constraintSource.startsWith( "{" )) {
                        log.error("Error parsing json from use constraints '" + constraintSource + "'", e);
                    }
                }
                String restrictionInfo = "";
                if (restrictionCode != null && restrictionCode.trim().length() > 0) {
                    restrictionInfo = restrictionCode + ": ";
                }

                if (!isJSON) {
                    // no JSON but might be further other constraint (BKG), we also render !
                    if(indexConstraint < constraintsNodes.getLength() - 1) {
                        String tmpSource = constraintsNodes.item(indexConstraint  + 1).getTextContent();
                        if(tmpSource.startsWith("Quellenvermerk: ")) {
                            boolean isSourceOfJson = false;
                            if(indexConstraint + 1 < constraintsNodes.getLength() - 1){
                                String tmpOtherConstraints = constraintsNodes.item(indexConstraint  + 2).getTextContent().trim();
                                if(tmpOtherConstraints.startsWith( "{" ) && tmpOtherConstraints.endsWith("}" )) {
                                    String tmpJsonSource = null;
                                    try {
                                        IngridDocument json = JsonUtil.parseJsonToIngridDocument(tmpOtherConstraints);
                                        if(json != null && json.containsKey("quelle")) {
                                            tmpJsonSource = json.getString("quelle");
                                            if(tmpSource.indexOf(tmpJsonSource) > -1) {
                                                isSourceOfJson = true;
                                            }
                                        } else {
                                            isSourceOfJson = false;
                                        }
                                    } catch (ParseException e) {
                                        isSourceOfJson = false;
                                    }
                                }
                            }
                            String value = constraintSource;
                            if(!isSourceOfJson) {
                                value = String.format("%s%s <br><span> %s </span>", restrictionInfo, constraintSource, tmpSource);
                            }
                            furtherOtherConstraints.add( value );
                        } else {
                            if(replaceUseConstraintsSourcePrefix && constraintSource.startsWith("Quellenvermerk: ")) {
                                constraintSource = constraintSource.replace(messages.getString("Quellenvermerk: "), "");
                            }
                            furtherOtherConstraints.add( removeLocalisation(constraintSource) );
                        }
                    } else {
                        if(replaceUseConstraintsSourcePrefix && constraintSource.startsWith("Quellenvermerk: ")) {
                            constraintSource = constraintSource.replace(messages.getString("Quellenvermerk: "), "");
                        }
                        furtherOtherConstraints.add( removeLocalisation(constraintSource) );
                    }
                } else if(displayJSON){
                    String finalValue = getValueFromCodeList(licenceList, constraints);
                    if (finalValue == null || finalValue.trim().isEmpty()) {
                        if (!constraints.startsWith("{") && !constraints.endsWith("}")) {
                            finalValue = constraints;
                        }
                    }

                    if(source != null && !source.isEmpty()) {
                        String tmpQuelle = messages.getString("constraints.use.costraints.source") + source;
                        if(furtherOtherConstraints.contains(tmpQuelle)){
                            furtherOtherConstraints.remove(tmpQuelle);
                        }
                        finalValue += tmpQuelle;
                    }

                    String value;
                    if (url != null && !url.trim().isEmpty()) {
                        // we have a URL from JSON
                        if (name != null && !name.trim().isEmpty() && !name.trim().equals( finalValue.trim() ) ) {
                            // we have a different license name from JSON, render it with link
                            value = String.format(messages.getString("constraints.use.link"), restrictionInfo, url, name, removeLocalisation(finalValue));
                        } else {
                            // no license name, render whole text with link
                            value = String.format(messages.getString("constraints.use.link.noname"), restrictionInfo, url, removeLocalisation(finalValue));
                        }
                    } else {
                        // NO URL
                        if (name != null && !name.trim().isEmpty() && !name.trim().equals( finalValue.trim() )) {
                            value = String.format("%s%s <br><span> %s </span>", restrictionInfo, name, finalValue);
                        } else if (restrictionCode != null){
                            value = String.format("%s%s", restrictionInfo, finalValue);
                        } else {
                            value = finalValue;
                        }
                    }

                    if (!result.contains(value)) {
                        result.add(value);
                    }
                }
            }

            // also add other constraints if present !
            for (String furtherConstraint : furtherOtherConstraints) {
                boolean exist = false;
                for (String resultItem : result) {
                    String[] splitFurtherConstraint = furtherConstraint.split("<br>");
                    if(splitFurtherConstraint.length > 1) {
                        for (String splitFurtherConstraintEntry : splitFurtherConstraint) {
                            if(resultItem.indexOf(splitFurtherConstraintEntry.replaceAll("<span>", "").replaceAll("</span>", "")) == -1) {
                              exist = false;
                              break;
                            }
                        }
                    } else {
                        if(resultItem.indexOf(splitFurtherConstraint[0]) > -1) {
                            exist = true;
                            break;
                        }
                    }
                }
                if (!exist) {
                    result.add(furtherConstraint);
                }
            }
        }
        return result;
    }

    public List<String> getUseLimitations() {
        final String resourceConstraintsXpath = "//gmd:identificationInfo/*/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useLimitation/*[self::gco:CharacterString or self::gmx:Anchor]";

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

            constraints = removePraefix(constraints);

            if(log.isDebugEnabled()) {
                log.debug(String.format("Use limitations are now: %s", constraints));
            }
            // <<< End of temporary solution <<<

            if (constraints != null && !constraints.trim().isEmpty()) {
                if (!result.contains(constraints)) {
                    result.add(constraints);
                }
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
            // get gco:CharacterString child to strip localisation REDMINE-1882
            Node nextSibling = xPathUtils.getNode(sibling, "./*[self::gco:CharacterString or self::gmx:Anchor]");
            if(nextSibling != null) {
                sibling = nextSibling;
            }
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
                        if(xPathUtils.nodeExists(node, xpathExpression)){
                            value = removeLocalisation(xPathUtils.getString(node, xpathExpression));
                            break;
                        }
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
                    if(headXpathExpression != null && !headXpathExpression.isEmpty() && xPathUtils.nodeExists(node, headXpathExpression)){
                        NodeList valueNodeList = xPathUtils.getNodeList(node, headXpathExpression);
                        StringBuilder valueConcated = new StringBuilder("");
                        for (int k=0; k<valueNodeList.getLength();k++) {
                            if (valueConcated.length() > 0) {
                                valueConcated.append(";");
                            }
                            String value = valueNodeList.item( k ).getTextContent().trim();
                            if(headXpathExpression.endsWith("date") || headXpathExpression.endsWith("datum")){
                                value = UtilsDate.convertDateString(value, "yyyy-MM-dd", "dd.MM.yyyy");
                                valueConcated.append(value);
                                break;
                            }
                            if(headCodeList != null && headCodeList.size() > j){
                                String codelist = headCodeList.get(j);
                                if(codelist != null && !codelist.isEmpty()) {
                                    String tmpValue = sysCodeList.getNameByCodeListValue(codelist, value).trim();
                                    if(tmpValue.length() > 0){
                                        value = tmpValue;
                                    }
                                }
                                valueConcated.append(value);
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
        return valueHTMLEscape(value, false);
    }

    public String valueHTMLEscape(String value, boolean changeToHTMLNewLine){
        if(value != null){
            if(changeToHTMLNewLine) {
                value = value.replaceAll("\n", "<br/>");
            } else {
                value = value.replaceAll("\n", " ");
            }
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
                String href = "?docuuid=" + uuid + "&type=address";
                if(PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_DETAIL_USE_PARAMETER_PLUGID)) {
                    href += "&plugid=" + this.iPlugId;
                }
                element.put("href", href);
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

    public String removeLocalisation (String locString){
        if(locString != null) {
            return locString.split("#locale-")[0];
        }
        return locString;
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
