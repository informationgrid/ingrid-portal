/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.beans.query;


public class SearchResultBean {
	private ObjectSearchResultBean objectSearchResult;
	private AddressSearchResultBean addressSearchResult;
	private CSVSearchResultBean csvSearchResult;
	
	
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
