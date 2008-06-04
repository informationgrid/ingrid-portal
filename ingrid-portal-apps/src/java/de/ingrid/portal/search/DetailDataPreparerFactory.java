/**
 * 
 */
package de.ingrid.portal.search;

/**
 * @author joachim
 *
 */
public class DetailDataPreparerFactory {

	private static DetailDataPreparerFactory instance = null;
	
	public static DetailDataPreparerFactory getInstance() {
		if (instance == null) {
			instance = new DetailDataPreparerFactory();
		}
		
		return instance;
	}
	
	
	public DetailDataPreparer getDetailDataPreparer(String version) {
		
		return null;
	}

}
