/*
 * Created on 01.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search;

import java.util.Vector;

/**
 * @author joachim
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchResultList extends Vector {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2941647923169041473L;

	/**
	 * 
	 */
	private int numberOfHits = 0;

	/**
	 * @return
	 */
	public int getNumberOfHits() {
		return numberOfHits;
	}

	/**
	 * @param numberOfHits
	 */
	public void setNumberOfHits(int numberOfHits) {
		this.numberOfHits = numberOfHits;
	}

}
