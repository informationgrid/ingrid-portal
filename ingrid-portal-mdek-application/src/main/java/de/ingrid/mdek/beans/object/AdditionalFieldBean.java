package de.ingrid.mdek.beans.object;

import java.util.List;

public class AdditionalFieldBean {
	private String identifier;
	private String value;
	private String listId;
	private List<List<AdditionalFieldBean>> tableRows;

	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getListId() {
		return listId;
	}
	public void setListId(String listId) {
		this.listId = listId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
    public void setTableRows(List<List<AdditionalFieldBean>> tableRows) {
        this.tableRows = tableRows;
    }
    public List<List<AdditionalFieldBean>> getTableRows() {
        return tableRows;
    }
}
