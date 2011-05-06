package de.ingrid.mdek.beans.controls;

import java.util.Map;

public class ExtendedControls extends Controls{
    private String              width;
    private String              widthUnit = "%";
    private Map<String, String> helpMessage; // localized
    private String              scriptedCswMapping;
    private String              indexName;
    
    public void setWidth(String width) {
        this.width = width;
    }
    public String getWidth() {
        return width;
    }
    
    public void setScriptedCswMapping(String scriptedCswMapping) {
        this.scriptedCswMapping = scriptedCswMapping;
    }
    public String getScriptedCswMapping() {
        return scriptedCswMapping;
    }
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    public String getIndexName() {
        return indexName;
    }
    public void setHelpMessage(Map<String, String> helpMessage) {
        this.helpMessage = helpMessage;
    }
    public Map<String, String> getHelpMessage() {
        return helpMessage;
    }
    public void setWidthUnit(String widthUnit) {
        this.widthUnit = widthUnit;
    }
    public String getWidthUnit() {
        return widthUnit;
    }
}
