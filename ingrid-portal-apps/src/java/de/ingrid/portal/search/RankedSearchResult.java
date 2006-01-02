/*
 * Created on 01.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search;

/**
 * @author joachim
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RankedSearchResult extends SearchResult {

	/**
	 * Comment for <code>resultRanking</code>
	 */
	double resultRanking;

	/**
	 * @return Returns the resultRanking.
	 */
	public double getResultRanking() {
		return resultRanking;
	}

	/**
	 * @param resultRanking
	 *            The resultRanking to set.
	 */
	public void setResultRanking(double resultRanking) {
		this.resultRanking = resultRanking;
	}
}
