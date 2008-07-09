/**
 * 
 */
package de.ingrid.portal.search;

import java.util.ArrayList;

import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.Settings;
import de.ingrid.utils.PlugDescription;

/**
 * Helper Class Obtains the iPlugVersion
 * 
 * 
 * @author joachim
 *
 */
public class IPlugVersionInspector {

	public static final String VERSION_IDC_1_0_2_DSC_OBJECT = "VERSION_IDC_1_0_2_DSC_OBJECT";
	
	public static final String VERSION_UDK_5_0_DSC_OBJECT = "VERSION_UDK_5_0_DSC_OBJECT";

	public static final String VERSION_IDC_1_0_2_DSC_ADDRESS = "VERSION_IDC_1_0_2_DSC_ADDRESS";
	
	public static final String VERSION_UDK_5_0_DSC_ADDRESS = "VERSION_UDK_5_0_DSC_ADDRESS";
	
	public static final String VERSION_UNKNOWN = "VERSION_UNKNOWN";
	
	public static String getIPlugVersion(PlugDescription plugDescription) {
		
		ArrayList fields = (ArrayList)plugDescription.get(PlugDescription.FIELDS);
		
		// try to get the right iPlug Type (object/adress/generic)
		if (fields != null && fields.contains("t01_object.obj_id") && fields.contains("parent.object_node.obj_uuid")) {
			return VERSION_IDC_1_0_2_DSC_OBJECT;
		} else if (fields != null && fields.contains("t02_address.adr_id") && fields.contains("parent.address_node.addr_uuid")) {
			return VERSION_IDC_1_0_2_DSC_ADDRESS;
		} else if (IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)
                || IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_ECS)
                || IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_CSW)) {		
			return VERSION_UDK_5_0_DSC_OBJECT;
		} else if (IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
				return VERSION_UDK_5_0_DSC_ADDRESS;
		}
		return VERSION_UNKNOWN;
	}

}
