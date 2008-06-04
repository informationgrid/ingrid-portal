/**
 * 
 */
package de.ingrid.portal.search;

import java.util.HashMap;

import de.ingrid.utils.dsc.Record;

/**
 * Interface for all DetailData Preparer
 * 
 * 
 * @author joachim
 *
 */
public interface DetailDataPreparer {

	public HashMap prepare(Record record);
	
}
