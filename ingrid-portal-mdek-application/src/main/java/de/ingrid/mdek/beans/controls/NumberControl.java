package de.ingrid.mdek.beans.controls;

import java.util.Map;

public class NumberControl extends ExtendedControls {
    private Map<String, String> unit; // localized
    
    public NumberControl() {
        this.setType(NUMBER_CONTROL);
    }

    public void setUnit(Map<String, String> unit) {this.unit = unit;}

    public Map<String, String> getUnit() {return unit;}
}
