package de.ingrid.mdek.beans;

import java.util.ArrayList;

public class SearchResultBean {
	public ObjectSearchResultBean objectSearchResult;
	public AddressSearchResultBean addressSearchResult;

	
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

}
