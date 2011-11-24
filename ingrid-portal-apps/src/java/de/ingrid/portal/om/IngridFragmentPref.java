package de.ingrid.portal.om;


public class IngridFragmentPref {

	private Long id;
	private Long fragmentId;
	private String fragmentName;
	private Long fragmentReadOnly;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFragmentId() {
		return fragmentId;
	}
	public void setFragmentId(Long fragmentId) {
		this.fragmentId = fragmentId;
	}
	public String getFragmentName() {
		return fragmentName;
	}
	public void setFragmentName(String fragmentName) {
		this.fragmentName = fragmentName;
	}
	public Long getFragmentReadOnly() {
		return fragmentReadOnly;
	}
	public void setFragmentReadOnly(Long fragmentReadOnly) {
		this.fragmentReadOnly = fragmentReadOnly;
	}
	
		
}