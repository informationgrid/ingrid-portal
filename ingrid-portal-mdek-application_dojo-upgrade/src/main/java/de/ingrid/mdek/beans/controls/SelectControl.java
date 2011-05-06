package de.ingrid.mdek.beans.controls;

import java.util.List;
import java.util.Map;

public class SelectControl extends ExtendedControls {
    private Map<String,List<OptionEntry>> options;// = new HashMap<String,List<String>>();
    private boolean allowFreeEntries; 
    
    public SelectControl() {
        this.setType(SELECT_CONTROL);
    }

    public Map<String,List<OptionEntry>> getOptions() { return options; }
    
    public void setOptions(Map<String,List<OptionEntry>> options) { this.options = options; }
    
    public String getOptionsAsString(String lang) {
        String optionsString = "";
        List<OptionEntry> optionList = this.options.get(lang);
        if (optionList == null || optionList.isEmpty())
            return "";
        
        for (OptionEntry option : optionList) {
            optionsString += "'"+option.getValue() + "',";
        }
        
        return optionsString.substring(0, optionsString.length() - 1);
    }
    
    public String getOptionsAsStringWithId(String lang) {
        String optionsString = "";
        List<OptionEntry> optionList = this.options.get(lang);
        if (optionList == null || optionList.isEmpty())
            return "";
        
        for (OptionEntry option : optionList) {
            optionsString += "{id:'"+option.getId()+"', value:'"+option.getValue() + "'},";
        }
        
        return optionsString.substring(0, optionsString.length() - 1);
    }
    
    public String getIdsAsString(String lang) {
        String optionsString = "";
        List<OptionEntry> optionList = this.options.get(lang);
        if (optionList == null || optionList.isEmpty())
            return "";
        
        for (OptionEntry option : optionList) {
            optionsString += "'"+option.getId()+"',";
        }
        
        return optionsString.substring(0, optionsString.length() - 1);
    }

    public void setAllowFreeEntries(boolean allowFreeEntries) {
        this.allowFreeEntries = allowFreeEntries;
    }

    public boolean getAllowFreeEntries() {
        return allowFreeEntries;
    }
}
