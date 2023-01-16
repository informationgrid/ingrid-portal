/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.beans.query;

import java.util.List;

public class ThesaurusStatisticsResultBean {
	private long numHitsTotal;
	private long numTermsTotal;

	private List<SearchTermBean> searchTermList;

	public long getNumHitsTotal() {
		return numHitsTotal;
	}

	public void setNumHitsTotal(long numHitsTotal) {
		this.numHitsTotal = numHitsTotal;
	}

	public long getNumTermsTotal() {
		return numTermsTotal;
	}

	public void setNumTermsTotal(long numTermsTotal) {
		this.numTermsTotal = numTermsTotal;
	}

	public List<SearchTermBean> getSearchTermList() {
		return searchTermList;
	}

	public void setSearchTermList(List<SearchTermBean> results) {
		this.searchTermList = results;
	}
}
