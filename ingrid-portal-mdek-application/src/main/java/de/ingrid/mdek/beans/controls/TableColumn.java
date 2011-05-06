package de.ingrid.mdek.beans.controls;

import java.util.List;
import java.util.Map;

public class TableColumn extends SelectControl {
    //private String             type;
    //private String             field;
    //private Map<String,String> label; // localized
    //private String             width;
    //private String             widthUnit;
    //private String             indexName;
    //private Map<String,List<String>> options; // localized
    
    // empty constructor for DWR!
    public TableColumn() {}
    
    public TableColumn(String type, String field, Map<String,String> label, String width, String indexName, Map<String,List<OptionEntry>> options) {
    //    this.type = type;
    //    this.field = field;
    //    this.label = label;
        setId(field);
        setType(type);
        setLabel(label);
        //this.width = width;
        setWidth(width);
        //this.setWidthUnit("px"); // use pixel for column width
        setWidthUnit("px");
        //this.indexName = indexName;
        setIndexName(indexName);
        //this.options = options;
        setOptions(options);
    }

    //public void setType(String type) {this.type = type;}
    //public String getType() {return type;}
    //public void setLabel(Map<String,String> label) {this.label = label;}
    //public Map<String,String> getLabel() {return label;}
    //public void setWidth(String width) {this.width = width;}
    //public String getWidth() {return width;}
    //public void setId(String id) {this.field = id;}
    //public String getId() {return field;}
    //public void setIndexName(String indexName) {this.indexName = indexName;}
    //public String getIndexName() {return indexName;}

    /*public void setWidthUnit(String widthUnit) {
        this.widthUnit = widthUnit;
    }

    public String getWidthUnit() {
        return widthUnit;
    }*/

    /*public void setOptions(Map<String,List<String>> options) {
        this.options = options;
    }

    public Map<String,List<String>> getOptions() {
        return options;
    }
    
    public String getOptionsAsString(String lang) {
        String optionsString = "";
        for (String option : this.options.get(lang)) {
            optionsString += "'"+option + "',";
        }
        
        return optionsString.substring(0, optionsString.length() - 1);
    }*/
}
