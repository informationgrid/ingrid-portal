/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.HashMap;
import java.util.List;

import javax.portlet.RenderRequest;

import org.apache.velocity.context.Context;

import de.ingrid.portal.search.IPlugVersionInspector;

/**
 * @author joachim
 *
 */
public class DetailDataPreparerFactory {

	private Context context;
	private String iplugId;
	private List dateFields;
	private RenderRequest request;
	private HashMap replacementFields;
	
	public DetailDataPreparerFactory(Context context, String iPlugId, List dateFields, RenderRequest request, HashMap replacementFields) {
		this.context = context;
		this.iplugId = iPlugId;
		this.dateFields = dateFields;
		this.request = request;
		this.replacementFields = replacementFields;
	}
	
	
	public DetailDataPreparer getDetailDataPreparer(String version) {
		
		if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
			return new DetailDataPreparerIdc1_0_2Object(this.context);
		} else if (version.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_OBJECT)) {
			return new DetailDataPreparer_UDK_5_0_Object(this.context, this.iplugId, this.dateFields, request, replacementFields);
		} else if (version.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_ADDRESS)) {
			return new DetailDataPreparer_UDK_5_0_Address(this.context, this.iplugId, this.dateFields, request, replacementFields);
		} else if (version.equals(IPlugVersionInspector.VERSION_UNKNOWN)) {
			return new DetailDataPreparerGeneric(this.context, this.dateFields, request, replacementFields);
		}
		
		return null;
	}

}
