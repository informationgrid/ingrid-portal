package de.ingrid.mdek.beans;

public class SysList {
	private Integer id;
	private Integer[] entryIds;
	private String[] deEntries;
	private String[] enEntries;
	private Boolean maintainable;
	private Integer defaultIndex;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer[] getEntryIds() {
		return entryIds;
	}

	public void setEntryIds(Integer[] entryIds) {
		this.entryIds = entryIds;
	}

	public String[] getDeEntries() {
		return deEntries;
	}

	public void setDeEntries(String[] deEntries) {
		this.deEntries = deEntries;
	}

	public String[] getEnEntries() {
		return enEntries;
	}

	public void setEnEntries(String[] enEntries) {
		this.enEntries = enEntries;
	}

	public Boolean getMaintainable() {
		return maintainable;
	}

	public void setMaintainable(Boolean maintainable) {
		this.maintainable = maintainable;
	}

	public Integer getDefaultIndex() {
		return defaultIndex;
	}

	public void setDefaultIndex(Integer defaultIndex) {
		this.defaultIndex = defaultIndex;
	}
}