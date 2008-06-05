/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.HashMap;

import org.apache.velocity.context.Context;

import de.ingrid.utils.dsc.Record;

/**
 * @author joachim
 *
 */
public class DetailDataPreparerIdc1_0_2Object implements DetailDataPreparer {

	private Context context;
	
	public DetailDataPreparerIdc1_0_2Object(Context context) {
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.detail.DetailDataPreparer#prepare(de.ingrid.utils.dsc.Record)
	 */
	public void prepare(Record record) {
		HashMap data = new HashMap();
		context.put("data", data);
	}

}
