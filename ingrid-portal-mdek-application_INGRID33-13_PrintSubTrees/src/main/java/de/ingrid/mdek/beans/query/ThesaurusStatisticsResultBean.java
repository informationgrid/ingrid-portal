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
