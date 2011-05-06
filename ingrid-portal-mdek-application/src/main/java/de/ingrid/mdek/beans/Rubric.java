package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.controls.Controls;

public class Rubric {
    private String              id;
    private Map<String, String> label;
    private Map<String, String> helpMessage;
    private boolean             isLegacy;
    private List<Controls>      controls = new ArrayList<Controls>();
    
    public void setId(String id) {this.id = id;}

    public String getId() {return id;}

    public void setLabel(Map<String, String> label) {this.label = label;}

    public Map<String, String> getLabel() {return label;}

    public void setHelpMessage(Map<String, String> helpMessage) {this.helpMessage = helpMessage;}

    public Map<String, String> getHelpMessage() {return helpMessage;}

    public void setControls(List<Controls> controls) {this.controls = controls;}

    public List<Controls> getControls() {return controls;}

    public void setIsLegacy(boolean isLegacy) {this.isLegacy = isLegacy;}

    public boolean getIsLegacy() {return isLegacy;}
}
