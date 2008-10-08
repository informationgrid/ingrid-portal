package de.ingrid.mdek.beans.query;

public class SearchTermBean {
	public String term;
	public long numOccurences;

	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public long getNumOccurences() {
		return numOccurences;
	}
	public void setNumOccurences(long numOccurences) {
		this.numOccurences = numOccurences;
	}
}
