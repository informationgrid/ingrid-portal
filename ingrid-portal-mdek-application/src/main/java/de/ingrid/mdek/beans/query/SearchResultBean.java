package de.ingrid.mdek.beans.query;

import java.util.ArrayList;

public class SearchResultBean {
	public ObjectSearchResultBean objectSearchResult;
	public AddressSearchResultBean addressSearchResult;
	public CSVSearchResultBean csvSearchResult;
	
	
	public ObjectSearchResultBean getObjectSearchResult() {
		return objectSearchResult;
	}
	public void setObjectSearchResult(ObjectSearchResultBean objectSearchResult) {
		this.objectSearchResult = objectSearchResult;
	}
	public AddressSearchResultBean getAddressSearchResult() {
		return addressSearchResult;
	}
	public void setAddressSearchResult(AddressSearchResultBean addressSearchResult) {
		this.addressSearchResult = addressSearchResult;
	}
	public CSVSearchResultBean getCsvSearchResult() {
		return csvSearchResult;
	}
	public void setCsvSearchResult(CSVSearchResultBean csvSearchResult) {
		this.csvSearchResult = csvSearchResult;
	}

}
