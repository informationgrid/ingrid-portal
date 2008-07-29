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

    /** number of Object references (AOR) to show initially and to add when "more" is pressed */
    final static int NUM_OBJ_REFS_PER_PAGE = 20;

    /** request parameter name of maximum number of direct object references to show */
    final static String REQUEST_PARAM_NAME_MAX_OBJ_REFS = "maxORs";
    /** request parameter name of maximum number of subordinated object references to show */
    final static String REQUEST_PARAM_NAME_MAX_SUB_OBJ_REFS = "maxSubORs";

	public void prepare(Record record) throws Throwable;
	
}
