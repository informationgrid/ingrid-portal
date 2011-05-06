package de.ingrid.mdek.profile;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ingrid.mdek.beans.ProfileBean;
import de.ingrid.mdek.beans.Rubric;
import de.ingrid.mdek.beans.controls.Controls;
import de.ingrid.mdek.beans.controls.DateControl;
import de.ingrid.mdek.beans.controls.ExtendedControls;
import de.ingrid.mdek.beans.controls.NumberControl;
import de.ingrid.mdek.beans.controls.OptionEntry;
import de.ingrid.mdek.beans.controls.SelectControl;
import de.ingrid.mdek.beans.controls.TableColumn;
import de.ingrid.mdek.beans.controls.TableControl;
import de.ingrid.mdek.beans.controls.TextControl;
import de.ingrid.utils.enumeration.IDbEnum;
import de.ingrid.utils.xml.XMLUtils;

public class ProfileMapper {
    private final static Logger log = Logger.getLogger(ProfileMapper.class);

	/** Value of isVisible element in profile ! */
	public enum IsVisible implements IDbEnum  {
		OPTIONAL("optional"),
		SHOW("show"),
		HIDE("hide");
		IsVisible(String xmlValue) {
			this.xmlValue = xmlValue;
		}
		public String getDbValue() {
			return xmlValue;
		}
		String xmlValue;
	}

    public ProfileBean mapStringToBean(String profile) {
        return mapStreamToBean(new InputSource(new StringReader(profile)));
    }
    
    public ProfileBean mapStreamToBean(InputSource profileStream) {
        ProfileBean bean = new ProfileBean();
        List<Rubric> rubrics = bean.getRubrics();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document descriptorDoc;
        try {
            builder = factory.newDocumentBuilder();
        
            //String myString = getTestXML();
            
            descriptorDoc = builder.parse(profileStream);
            
            XPath xpath = XPathFactory.newInstance().newXPath();
            //xmlns="http://www.portalu.de/igc-profile" version="1.0.0"
            Node profileNode = (Node) xpath.evaluate("/profile", descriptorDoc, XPathConstants.NODE);
            bean.setNamespace(getAttribute(profileNode, "xmlns"));
            bean.setVersion(getAttribute(profileNode, "version"));
            bean.setName(getValue(descriptorDoc, "/profile/name"));
            bean.setLanguages(getSupportedLanguages(descriptorDoc));
            
            NodeList allRubrics = (NodeList) xpath.evaluate("/profile/layoutRubric", descriptorDoc, XPathConstants.NODESET);
            for (int i=0; i<allRubrics.getLength(); i++) {
                Rubric newRubric = new Rubric();
                newRubric.setId(getValue(allRubrics.item(i), "id"));
                newRubric.setLabel(getValues(allRubrics.item(i), "localizedLabel", "lang"));
                newRubric.setHelpMessage(getValues(allRubrics.item(i), "localizedHelp", "lang"));
                newRubric.setIsLegacy("true".equals(getAttribute(allRubrics.item(i), "isLegacy")));
                
                List<Controls> rubricControls = newRubric.getControls();
                NodeList controls = getControlsOfRubric(allRubrics.item(i));
                for (int j=0; j<controls.getLength(); j++) {
                    Node currentItem = controls.item(j);
                    Controls ctrl = null;
                    if ("textControl".equals(currentItem.getNodeName()))
                        ctrl = new TextControl();
                    else if ("selectControl".equals(currentItem.getNodeName()))
                        ctrl = new SelectControl();
                    else if ("tableControl".equals(currentItem.getNodeName()))
                        ctrl = new TableControl();
                    else if ("numberControl".equals(currentItem.getNodeName()))
                        ctrl = new NumberControl();
                    else if ("dateControl".equals(currentItem.getNodeName()))
                        ctrl = new DateControl();
                    else {
                        ctrl = new Controls();
                        ctrl.setIsLegacy(true);
                    }
                    
                    ctrl.setId(getValue(currentItem, "id"));
                    ctrl.setIsMandatory("true".equals(getValue(currentItem, "isMandatory")));
                    ctrl.setIsVisible(getValue(currentItem, "isVisible"));
                    ctrl.setScriptedProperties(getValue(currentItem, "scriptedProperties"));
                    
                    // we are finished here for legacy controls
                    if (ctrl.getClass() != Controls.class) {
                        ctrl.setLabel(getValues(currentItem, "localizedLabel", "lang"));
                        ((ExtendedControls)ctrl).setHelpMessage(getValues(currentItem, "localizedHelp", "lang"));
                        ((ExtendedControls)ctrl).setScriptedCswMapping(getValue(currentItem, "scriptedCswMapping"));
                        ((ExtendedControls)ctrl).setIndexName(getValue(currentItem, "indexName"));
                        ((ExtendedControls)ctrl).setWidth(getValue(currentItem, "layoutWidth"));
                    }
                    
                    if (ctrl.getClass() == TextControl.class) {
                        ((TextControl)ctrl).setNumLines(Integer.valueOf(getValue(currentItem, "layoutNumLines")));
                    } else if (ctrl.getClass() == TableControl.class) {
                        ((TableControl)ctrl).setNumTableRows(Integer.valueOf(getValue(currentItem, "layoutNumLines")));
                        ((TableControl)ctrl).setColumns(getColumns((TableControl)ctrl, currentItem));
                    } else if (ctrl.getClass() == SelectControl.class) {
                        ((SelectControl)ctrl).setAllowFreeEntries("true".equals(getAttribute(currentItem, "isExtendable")) ? true : false);
                        ((SelectControl)ctrl).setOptions(getSelectOptions(currentItem));
                    } else if (ctrl.getClass() == NumberControl.class) {
                        ((NumberControl)ctrl).setUnit(getValues(currentItem, "localizedLabelPostfix", "lang"));
                    }
                    
                    rubricControls.add(ctrl);
                }
                
                rubrics.add(newRubric);
            }
            
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Profile is corrupted or not present!");
            e.printStackTrace();
            bean = null;
        }
        
        return bean;
    }
    
    public String mapBeanToXmlString(ProfileBean bean) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            Node root = doc.appendChild(doc.createElement("profile"));
            
            XMLUtils.createOrReplaceAttribute(root, "xmlns", bean.getNamespace());
            XMLUtils.createOrReplaceAttribute(root, "version", bean.getVersion());
            addNode(root, "name", bean.getName());
            addLanguageNode(root, bean.getLanguages());
            
            for (Rubric rubric : bean.getRubrics()) {
                addRubricNode(root, rubric);
            }
            
            // create a string from the document
            return XMLUtils.toString(doc);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
    private void addRubricNode(Node root, Rubric rubric) {
        Node rubricNode = root.getOwnerDocument().createElement("layoutRubric");
        XMLUtils.createOrReplaceAttribute(rubricNode, "isLegacy", String.valueOf(rubric.getIsLegacy()));
        
        // add id
        addNode(rubricNode, "id", rubric.getId());
        
        // add localized title
        for (String lang : rubric.getLabel().keySet()) {
            Node labelNode = addNode(rubricNode, "localizedLabel", rubric.getLabel().get(lang));
            // add attribute to the label node
            XMLUtils.createOrReplaceAttribute(labelNode, "lang", lang);
        }
        
        // add localized help
        for (String lang : rubric.getHelpMessage().keySet()) {
            Node helpNode = addNode(rubricNode, "localizedHelp", rubric.getHelpMessage().get(lang));
            // add attribute to the label node
            XMLUtils.createOrReplaceAttribute(helpNode, "lang", lang);
        }
        
        // add controls
        Node controlsNode = rubricNode.getOwnerDocument().createElement("controls");
        for (Controls control : rubric.getControls()) {
            addControlNode(controlsNode, control);
        }
        rubricNode.appendChild(controlsNode);
        root.appendChild(rubricNode);
    }

    private void addControlNode(Node rubricNode, Controls control) {
        String type = control.getType();
        
        Node controlNode = rubricNode.getOwnerDocument().createElement(control.getType());
        addNode(controlNode, "id", control.getId());
        addNode(controlNode, "isMandatory", String.valueOf(control.getIsMandatory()));
        addNode(controlNode, "isVisible", control.getIsVisible());
        addNode(controlNode, "scriptedProperties", control.getScriptedProperties(), true);
        
        if (!type.equals(Controls.LEGACY_CONTROL)) {
            // add localized title
            addLocalizedNode(controlNode, control.getLabel(), "localizedLabel");
            // add localized help
            addLocalizedNode(controlNode, ((ExtendedControls)control).getHelpMessage(), "localizedHelp");
            if (((ExtendedControls)control).getScriptedCswMapping() != null) {
                addNode(controlNode, "scriptedCswMapping", ((ExtendedControls)control).getScriptedCswMapping(), true);
            }
            addNode(controlNode, "indexName", ((ExtendedControls)control).getIndexName());
            addNode(controlNode, "layoutWidth", ((ExtendedControls)control).getWidth());
            //addNode(controlNode, "widthUnit", ((ExtendedControls)control).getWidthUnit());
            
            if (type.equals(Controls.GRID_CONTROL)) {
                addNode(controlNode, "layoutNumLines", String.valueOf(((TableControl)control).getNumTableRows()));
                Node columnsNode = rubricNode.getOwnerDocument().createElement("columns");
                for (TableColumn column : ((TableControl)control).getColumns()) {
                    Node typeNode = rubricNode.getOwnerDocument().createElement(column.getType());
                    //addControlNode(columnsNode, column);
                    addNode(typeNode, "id", column.getId());
                    addLocalizedNode(typeNode, column.getLabel(), "localizedLabel");
                    addNode(typeNode, "indexName", column.getIndexName());
                    addNode(typeNode, "layoutWidth", column.getWidth());
                    XMLUtils.createOrReplaceAttribute(typeNode, "isExtendable", column.getAllowFreeEntries() ? "true" : "false");
                    if (column.getOptions() != null)
                        addLocalizedList(typeNode, column.getOptions());
                    columnsNode.appendChild(typeNode);
                }
                controlNode.appendChild(columnsNode);
            } else if (type.equals(Controls.TEXT_CONTROL)) {
                addNode(controlNode, "layoutNumLines", String.valueOf(((TextControl)control).getNumLines()));
            } else if (type.equals(Controls.SELECT_CONTROL)) {
                XMLUtils.createOrReplaceAttribute(controlNode, "isExtendable", ((SelectControl)control).getAllowFreeEntries() ? "true" : "false");
                addLocalizedList(controlNode, ((SelectControl)control).getOptions());
            } else if (type.equals(Controls.NUMBER_CONTROL)) {
                addLocalizedNode(controlNode, ((NumberControl)control).getUnit(), "localizedLabelPostfix");
            }
        }
        rubricNode.appendChild(controlNode);
    }

    /**
     * Appends a new node as a child of parentNode with the tag 'type' and the content 'value'
     * and attr as its attribute. If asCDATA is set to true, a data object is put as a value.
     * @param parentNode
     * @param string
     * @param id
     * @param asCDATA
     */
    private Node addNode(Node parentNode, String type, String value, boolean asCDATA) {
        Node newNode = parentNode.getOwnerDocument().createElement(type);
        if (value == null)
            value = "";
        
        if (asCDATA)
            newNode.appendChild(parentNode.getOwnerDocument().createCDATASection(value));
        else
            newNode.setTextContent(value);
        return parentNode.appendChild(newNode);
    }
    
    private Node addLocalizedNode(Node whereNode, Map<String, String> map, String nodeLabel) {
        Node node = null;
        for (String lang : map.keySet()) {
            node = addNode(whereNode, nodeLabel, map.get(lang));
            // add attribute to the label node
            XMLUtils.createOrReplaceAttribute(node, "lang", lang);
        }
        return node;
    }
    
    private Node addLocalizedList(Node whereNode, Map<String, List<OptionEntry>> map) {
        Node listNode = whereNode.getOwnerDocument().createElement("selectionList");
        for (String lang : map.keySet()) {
            Node itemsNode = whereNode.getOwnerDocument().createElement("items");
            // add attribute to the items node
            XMLUtils.createOrReplaceAttribute(itemsNode, "lang", lang);
            // add list of language specific items
            for (OptionEntry item : map.get(lang)) {
                Node child = addNode(itemsNode, "item", item.getValue());
                XMLUtils.createOrReplaceAttribute(child, "id", item.getId());
            }
            listNode.appendChild(itemsNode);
        }
        whereNode.appendChild(listNode);
        return listNode;
    }
    
    private Node addNode(Node parentNode, String type, String value) {
        return addNode(parentNode, type, value, false);
    }

    private void addLanguageNode(Node root, List<String> languages) {
        Node lang = root.getOwnerDocument().createElement("supportedLanguages");
        lang.setTextContent(StringUtils.join(languages.toArray(), ','));
        root.appendChild(lang);
    }

    private List<TableColumn> getColumns(TableControl ctrl, Node item) throws XPathExpressionException {
        List<TableColumn> beanColumns = new ArrayList<TableColumn>();
        NodeList columns = getAllTableColumns(item);
        
        for (int i=0; i<columns.getLength(); i++) {
            String nodeType = columns.item(i).getNodeName(); 
            String field = getValue(columns.item(i), "id");
            Map<String, String> name = getValues(columns.item(i), "localizedLabel", "lang");
            String width = getValue(columns.item(i), "layoutWidth");
            String index = getValue(columns.item(i), "indexName");
            boolean extendable = "true".equals(getAttribute(columns.item(i), "isExtendable")) ? true : false;
            Map<String,List<OptionEntry>> options = getSelectOptions(columns.item(i));
            
            TableColumn tc = new TableColumn(nodeType, field, name, width, index, options);
            tc.setAllowFreeEntries(extendable);
            beanColumns.add(tc);
        }
        
        return beanColumns;
    }
    
    private Map<String,List<OptionEntry>> getSelectOptions(Node node) throws XPathExpressionException {
        
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList resNodes = (NodeList) xpath.evaluate("selectionList/*", node, XPathConstants.NODESET);
        if (resNodes.getLength() == 0) return null;
        
        Map<String,List<OptionEntry>> options = new HashMap<String, List<OptionEntry>>();
        // for each language !
        for (int i=0; i<resNodes.getLength(); i++) {
            NodeList itemNodes = (NodeList) xpath.evaluate("item", resNodes.item(i), XPathConstants.NODESET);
            List<OptionEntry> list = new ArrayList<OptionEntry>();
            for (int j=0; j<itemNodes.getLength(); j++) {
                list.add(new OptionEntry(getAttribute(itemNodes.item(j), "id"), itemNodes.item(j).getTextContent()));
            }
            options.put(getAttribute(resNodes.item(i), "lang"), list);
        }
        return options;
    }

    private String mapType(String nodeName) {
        if (nodeName.equals("textControl"))
            return Controls.TEXT_CONTROL;
        else if (nodeName.equals("numberControl"))
            return Controls.NUMBER_CONTROL;
        else if (nodeName.equals("dateControl"))
            return Controls.DATE_CONTROL;
        else if (nodeName.equals("selectControl"))
            return Controls.SELECT_CONTROL;
        else
            log.error("Unknown column type: " + nodeName);
        
        return null;
    }

    private NodeList getAllTableColumns(Node node) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList resNode = (NodeList) xpath.evaluate("columns/*", node, XPathConstants.NODESET);
        return resNode;
    }

    /*public void mapBeanToXmlString(ProfileBean bean) {
        
    }*/
    
    /**
     * Return the value of a unique node under the given node.
     * 
     * @param node
     * @param attr
     * @return
     * @throws XPathExpressionException
     */
    private String getValue(Node node, String attr) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node resNode = (Node) xpath.evaluate(attr, node, XPathConstants.NODE);
        if (resNode == null) return null;
        return resNode.getTextContent();
    }
    
    /**
     * Return all values of multiple similar subnodes. For example different language definition.
     * 
     * @param node
     * @param attr
     * @return
     * @throws XPathExpressionException
     */
    private Map<String, String> getValues(Node node, String type, String distinguishedAttribute) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList resNodes = (NodeList) xpath.evaluate(type, node, XPathConstants.NODESET);
        Map<String, String> results = new HashMap<String, String>();
        for (int i=0; i<resNodes.getLength(); i++) {
            String attributeValue = getAttribute(resNodes.item(i), distinguishedAttribute); 
            results.put(attributeValue, resNodes.item(i).getTextContent());
        }
        return results;
    }
    
    private String getAttribute(Node item, String attr) {
        Node namedItem = item.getAttributes().getNamedItem(attr);
        if (namedItem == null)
            return "";
        
        return namedItem.getNodeValue();
    }
    
    private NodeList getControlsOfRubric(Node node) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList resNode = (NodeList) xpath.evaluate("controls/*", node, XPathConstants.NODESET);
        return resNode;
    }
    
    private List<String> getSupportedLanguages(Document descriptorDoc) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node resNode = (Node) xpath.evaluate("/profile/supportedLanguages", descriptorDoc, XPathConstants.NODE);
        return Arrays.asList(resNode.getTextContent().split(","));
        //return resNode;
    }
    
    /*
    private Map<String,List<String>> getSelectOptions(Node node) throws XPathExpressionException {
        Map<String,List<String>> options = new HashMap<String,List<String>>();//ArrayList<String>();
        
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList optionNodes = (NodeList) xpath.evaluate("selectionList/*", node, XPathConstants.NODESET);
        
        for (int i=0; i<optionNodes.getLength(); i++) {
            options.add(optionNodes.item(i).getTextContent());
        }
        
        return options;
    }*/
}
