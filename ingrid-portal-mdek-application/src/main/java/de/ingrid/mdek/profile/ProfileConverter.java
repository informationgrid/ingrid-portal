package de.ingrid.mdek.profile;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.ProfileBean;
import de.ingrid.mdek.beans.Rubric;
import de.ingrid.mdek.beans.controls.Controls;
import de.ingrid.mdek.beans.controls.DateControl;
import de.ingrid.mdek.beans.controls.ExtendedControls;
import de.ingrid.mdek.beans.controls.NumberControl;
import de.ingrid.mdek.beans.controls.SelectControl;
import de.ingrid.mdek.beans.controls.TableColumn;
import de.ingrid.mdek.beans.controls.TableControl;
import de.ingrid.mdek.beans.controls.TextControl;

public class ProfileConverter {
    private final static Logger log = Logger.getLogger(ProfileConverter.class);
    
    private PrintWriter out;

    private String language;
    
    private String defaultLanguage;
    
    public ProfileConverter(PrintWriter out, String language) {
        this.out = out;
        this.language = language;
    }
    
    /*public String convertXmlToJS() {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document descriptorDoc;
        try {
            builder = factory.newDocumentBuilder();
        
            //String myString = getTestXML();
            
            
            //descriptorDoc = builder.parse(new InputSource(new StringReader(myString)));
            descriptorDoc = builder.parse(ClassLoader.getSystemResourceAsStream("testProfileXML.xml"));
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList allRubrics = (NodeList) xpath.evaluate("/ProfileIGC/*", descriptorDoc, XPathConstants.NODESET);
            //typeList2.getLength();// 
            for (int i=0; i<allRubrics.getLength(); i++) {
                String rubricId = getValue(allRubrics.item(i), "id");
                processToJS(allRubrics.item(i), rubricId);
                
                NodeList controls = getChildrenOfRubric(allRubrics.item(i));
                for (int j=0; j<controls.getLength(); j++) {
                  processToJS(controls.item(j), rubricId);
                }
            }
            
            // create grid context menus
            addContextMenusToGrids(descriptorDoc);
            
            //firstChild.getAttributes().getUserData("id");
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
        }
        
        return "";
    }*/
    
    public void convertProfileBeanToJS(ProfileBean bean) {
        List<String> tableControls = new ArrayList<String>();
        String jsCode = "";
        boolean additionalFieldPresent = false;
        
        // the first language also is the default language!
        this.defaultLanguage = bean.getLanguages().get(0);
        
        try {
            for (Rubric rubric : bean.getRubrics()) {
                createRubric(rubric);
                additionalFieldPresent = false;
                
                for (Controls control : rubric.getControls()) {
                  processToJS(control, rubric.getId());
                  
                  if (!control.getType().equals(Controls.LEGACY_CONTROL)) {
                      additionalFieldPresent = true;
                      // remember all table controls for adding context menus later
                      //if (control.getType().equals(Controls.GRID_CONTROL))
                      //    tableControls.add(control.getId());
                  }
                  
                  // remember js-code
                  jsCode += control.getScriptedProperties();
                }
                
                // end rubric with a filling div if an additional field was added
                if (additionalFieldPresent)
                    endRubric(rubric.getId());
                
                // remember js-code
                //jsCode += rubric.getScriptedProperties();
            }
            
            // create grid context menus
            //addContextMenusToGrids(tableControls);
            
            // write and execute special javascript code for additional fields
            out.println("try {");
            out.println(jsCode);
            out.println("} catch(e) {");
            out.println("    displayErrorMessage('Scripted Properties Error of one or more additional fields: ' + e);");
            out.println("}");
            
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void endRubric(String id) {
        out.println("addToSection(\""+id+"\", createDivElement(\"fill\"));");
    }

    private void addContextMenusToGrids(List<String> tables) throws XPathExpressionException {
        if (tables.size() > 0) {
            String idArray = "[\"" + StringUtils.join(tables.toArray(), "\",\"") + "\"]";
            out.println("createGeneralTableContextMenu("+idArray+");");
        }
    }

    private void processToJS(Controls control, String rubricId) throws XPathExpressionException {
        
        if (control.getType() == Controls.LEGACY_CONTROL) {
            return;
        } else if (control.getType() == Controls.TEXT_CONTROL) {
            createTextForm((TextControl)control, rubricId);
        } else if (control.getType() == Controls.GRID_CONTROL) {
            createTableForm((TableControl)control, rubricId);
        } else if (control.getType() == Controls.SELECT_CONTROL) {
            createSelectForm((SelectControl)control, rubricId);
        } else if (control.getType() == Controls.NUMBER_CONTROL) {
            createNumberForm((NumberControl)control, rubricId);
        } else if (control.getType() == Controls.DATE_CONTROL) {
            createDateForm((DateControl)control, rubricId);
        } else {
            log.error("Unknown type: " + control.getType());
        }
    }

    private void createRubric(Rubric rubric) throws XPathExpressionException {
        // do not create legacy rubrics (who already exist)
        if (rubric.getIsLegacy()) return;
        
        out.println("addElementToObjectForm(createRubric({id:\"" + rubric.getId() + "\",label:\""
                + rubric.getLabel().get(this.language) + "\", help:\""+rubric.getHelpMessage().get(this.language)+"\"}));");
    }

    private void createTextForm(TextControl control, String rubricId) throws XPathExpressionException {
        // if (rowsInPixel)
        String height = "";//, height:101";
        
        if ( control.getNumLines() == 1) {
            out.println("addToSection(\"" + rubricId
                    + "\", createDomTextbox({"+addGeneralParameter(control)+", style:\"width:" + control.getWidth() + control.getWidthUnit() + "\"}));");
        } else {
            out.println("addToSection(\"" + rubricId
                    + "\", createDomTextarea({"+addGeneralParameter(control)+", style:\"width:" + control.getWidth() + control.getWidthUnit() + "\", rows:"+control.getNumLines()+height+"}));");
        }
    }
    
    private void createNumberForm(NumberControl control, String rubricId) {
        out.println("addToSection(\"" + rubricId
                + "\", createDomNumberbox({"+addGeneralParameter(control)+", unit:'"+control.getUnit().get(this.language)+"', style:\"width:" + control.getWidth() + control.getWidthUnit() + "\"}));");
    }

    private void createDateForm(DateControl control, String rubricId) {
        out.println("addToSection(\"" + rubricId
                + "\", createDomDatebox({"+addGeneralParameter(control)+", " +
                "style:\"width:" + control.getWidth() + control.getWidthUnit() + "\"}));");
    }
    
    private void createSelectForm(SelectControl control, String rubricId) throws XPathExpressionException {
        out.println("addToSection(\"" + rubricId
                + "\", createDomSelectBox({"+addGeneralParameter(control)+", isExtendable: "+control.getAllowFreeEntries()+", style:\"width:" 
                + control.getWidth() + control.getWidthUnit() + "\", listEntries:"
                + "["+control.getOptionsAsStringWithId(this.language) + "]}));");
    }
    
    private void createTableForm(TableControl control, String rubricId) throws XPathExpressionException {
        //String id = getValue(item, "id");
        //String width = getValue(item, "layoutWidth");
        //String label = getValue(item, "localizedLabel");
        //String rows = getValue(item, "layoutNumLines");
        
        out.println("var structure = [");
        String structure = "";
        
        List<TableColumn> columns = control.getColumns();
            
        for (int i=0; i<control.getColumns().size(); i++) {
            boolean isLastColumn = false; //i==columns.size()-1 ? true : false;
            structure += createColumnField(columns.get(i), isLastColumn) + ",";
        }
        // remove last ',' -> there must be at least one column, so no extra
        // check needed!
        structure = structure.substring(0, structure.length() - 1);
        out.println(structure + "];");
        /*out.println("addToSection(\"" + rubricId
                + "\", createDomDataGrid({"+addGeneralParameter(control)+", rows:\""
                + control.getNumTableRows()+"\", style:\"width:" + control.getWidth()
                + control.getWidthUnit() + "\"}, structure));");
        out.println("dijit.byId(\""+control.getId()+"\").startup();");*/
        out.println("createDomDataGrid({"+addGeneralParameter(control)+", rows:\""
                + control.getNumTableRows()+"\", style:\"width:" + control.getWidth()
                + control.getWidthUnit() + "\"}, structure, \""+rubricId+"\");");
    }

    private String addGeneralParameter(ExtendedControls control) {
        String str = "id:\"" + control.getId() + "\",name:\""
        + getLocalizedValue(control.getLabel()) + "\", help:\""
        + getLocalizedValue(control.getHelpMessage())+"\", isMandatory: " + control.getIsMandatory()
        + ", visible:\""+control.getIsVisible()+"\"";
        
        return str;
    }
    
    private String getLocalizedValue(Map<String, String> message) {
        String locMsg = message.get(this.language);
        // get localized value from default language if this language has no entry
        if (locMsg == null || locMsg == "")
            locMsg = message.get(this.defaultLanguage);
        return locMsg;
    }

    private String createColumnField(TableColumn columnBean, boolean isLastColumn) throws XPathExpressionException {
        String column = "{";
        String width = columnBean.getWidth() + "px";
        String editor = "";
        if (isLastColumn)
            width = "auto";
        
        if (columnBean.getType().equals(Controls.NUMBER_CONTROL)) {
            editor = "type: DecimalCellEditor, formatter: LocalizedNumberFormatter, ";
        } else if (columnBean.getType().equals(Controls.SELECT_CONTROL)) {
            // what about syslist support?
            String options = "options: ["+columnBean.getOptionsAsString(this.language)+"], ";
            String values = "values: ["+columnBean.getIdsAsString(this.language)+"], ";
            if (columnBean.getAllowFreeEntries())
                editor = "type: ComboboxEditor, " + options + values;
            else
                editor = "type: SelectboxEditor, " + options + values;
            //editor += "formatter: function(value){return UtilList.getSelectDisplayValue(this, value);}, ";
            editor += "formatter: ListCellFormatter, ";
        } else if (columnBean.getType().equals(Controls.DATE_CONTROL)) {
            editor = "type: DateCellEditorToString, formatter: DateCellFormatter, ";
        }
        
        column += "field:'"+columnBean.getId()+"', name:'"+columnBean.getLabel().get(this.language)
            + "', width:'"+width+"', "+editor+"editable:true";
        
        return column + "}";
    }
    
/*
    private String getSelectOptions(Node node) throws XPathExpressionException {
        String options = "[";
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList optionNodes = (NodeList) xpath.evaluate("selectionList/items[@lang='de']/*", node, XPathConstants.NODESET);
        
        for (int i=0; i<optionNodes.getLength(); i++) {
            options += "\"" + optionNodes.item(i).getTextContent() + "\",";
        }
        
        return options.substring(0, options.length() - 1) + "]";
    }

    private NodeList getAllTableColumns(Node node) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList resNode = (NodeList) xpath.evaluate("columns/*", node, XPathConstants.NODESET);
        return resNode;
    }
    
    private NodeList getChildrenOfRubric(Node node) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList resNode = (NodeList) xpath.evaluate("controls/*", node, XPathConstants.NODESET);
        return resNode;
    }

    private String getValue(Node node, String attr) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node resNode = (Node) xpath.evaluate(attr, node, XPathConstants.NODE);
        if (resNode == null) return null;
        return resNode.getTextContent();
    }
*/

    /**
     * Set the class required to all ui Elements who shall be mandatory and also
     * set the visibility for legacy elements.
     */
    public void printVisibilityJSCode(ProfileBean bean) {
        out.println("function setVisibilityOfFields() {");
        out.println("console.debug(\"set visibility to fields\");");
        for (Rubric rubric : bean.getRubrics()) {
            for (Controls control : rubric.getControls()) {
                String id = control.getId();
                // handle only legacy elements, since additional already
                // will have the correct class during initialization
                if (control.getIsLegacy()) {
                    if (control.getIsMandatory())
                        out.println("dojo.addClass(\""+id+"\", \"required\");");
                    out.println("dojo.addClass(\""+id+"\", \""+control.getIsVisible()+"\");");
                }
            }
        }
        out.println("}");
    }
}
