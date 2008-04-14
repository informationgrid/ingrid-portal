package de.ingrid.mdek.beans;

public class AddressExtSearchParamsBean {
	private String queryTerm;

	private String street;
	private String zipCode;
	private String city;

	private Integer relation;
	private Integer searchType;
	private Integer searchRange;

	public String getQueryTerm() {
		return queryTerm;
	}

	public void setQueryTerm(String queryTerm) {
		this.queryTerm = queryTerm;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getRelation() {
		return relation;
	}

	public void setRelation(Integer relation) {
		this.relation = relation;
	}

	public Integer getSearchType() {
		return searchType;
	}

	public void setSearchType(Integer searchType) {
		this.searchType = searchType;
	}

	public Integer getSearchRange() {
		return searchRange;
	}

	public void setSearchRange(Integer searchRange) {
		this.searchRange = searchRange;
	}
}
