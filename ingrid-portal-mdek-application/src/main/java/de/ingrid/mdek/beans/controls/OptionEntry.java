package de.ingrid.mdek.beans.controls;

public class OptionEntry {
    private String id;
    private String value;

    public OptionEntry() {
    }

    public OptionEntry(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
