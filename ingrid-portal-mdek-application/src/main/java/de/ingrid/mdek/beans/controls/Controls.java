package de.ingrid.mdek.beans.controls;

import java.util.Map;

public class Controls {
    public static String LEGACY_CONTROL     = "legacyControl";
    public static String TEXT_CONTROL       = "textControl";
    public static String NUMBER_CONTROL     = "numberControl";
    public static String DATE_CONTROL       = "dateControl";
    //public static String TEXTAREA_CONTROL   = "TEXTAREA_CONTROL";
    public static String SELECT_CONTROL     = "selectControl";
    public static String GRID_CONTROL       = "tableControl";
    
    private String              type;
    private String              id;
    private Map<String, String> label;       // localized "de" -> "German label"
    private boolean             isMandatory;
    private String              isVisible;
    private boolean             isLegacy;
    private String              scriptedProperties;
    
    public Controls() {
        this.type = LEGACY_CONTROL;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setIsMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }
    public String getIsVisible() {
        return isVisible;
    }
    public void setScriptedProperties(String scriptedProperties) {
        this.scriptedProperties = scriptedProperties;
    }
    public String getScriptedProperties() {
        return scriptedProperties;
    }
    public void setLabel(Map<String, String> label) {
        this.label = label;
    }
    public Map<String, String> getLabel() {
        return label;
    }

    public void setIsLegacy(boolean isLegacy) {
        this.isLegacy = isLegacy;
    }

    public boolean getIsLegacy() {
        return isLegacy;
    }
}
