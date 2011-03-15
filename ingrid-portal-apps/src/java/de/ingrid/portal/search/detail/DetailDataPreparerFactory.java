/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.HashMap;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

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
	private RenderResponse response;
	private HashMap replacementFields;
	
	public DetailDataPreparerFactory(Context context, String iPlugId, List dateFields, RenderRequest request, RenderResponse response,HashMap replacementFields) {
		this.context = context;
		this.iplugId = iPlugId;
		this.dateFields = dateFields;
		this.request = request;
		this.response = response;
		this.replacementFields = replacementFields;
	}
	
	
	public DetailDataPreparer getDetailDataPreparer(String version) {
		
		if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_9_DSC_OBJECT)) {
			return new DetailDataPreparerIdc1_0_9Object(context, iplugId, request, response);
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_8_DSC_OBJECT)) {
			return new DetailDataPreparerIdc1_0_8Object(context, iplugId, request, response);
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_OBJECT)) {
			return new DetailDataPreparerIdc1_0_5Object(context, iplugId, request, response);
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_3_DSC_OBJECT)) {
			return new DetailDataPreparerIdc1_0_3Object(context, iplugId, request, response);
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
			return new DetailDataPreparerIdc1_0_2Object(context, iplugId, request, response);
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_ADDRESS)) {
			return new DetailDataPreparerIdc1_0_5Address(context, iplugId, request, response);
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_ADDRESS)) {
			return new DetailDataPreparerIdc1_0_2Address(context, iplugId, request, response);
		} else if (version.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_OBJECT)) {
			return new DetailDataPreparer_UDK_5_0_Object(context, iplugId, dateFields, request, replacementFields);
		} else if (version.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_ADDRESS)) {
			return new DetailDataPreparer_UDK_5_0_Address(context, iplugId, dateFields, request, replacementFields);
		} else if (version.equals(IPlugVersionInspector.VERSION_IDF_1_0_0_OBJECT)) {
			// TODO: return IDF DetailPreparer
			return new DetailDataPreparerIDF1_0_0Generic(context, iplugId, request, response);
		} else if (version.equals(IPlugVersionInspector.VERSION_UNKNOWN)) {
			return new DetailDataPreparerGeneric(context, dateFields, request, replacementFields);
		}
		
		return null;
	}

}
