/**
 * 
 */
package de.ingrid.portal.search.detail;

import de.ingrid.utils.dsc.Record;

/**
 * Interface for all DetailData Preparer
 * 
 * 
 * @author joachim
 *
 */
public interface DetailDataPreparer {

	public void prepare(Record record) throws Throwable;
	
}
