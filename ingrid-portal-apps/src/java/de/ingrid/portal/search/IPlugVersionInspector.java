/**
 * 
 */
package de.ingrid.portal.search;

import java.util.ArrayList;

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
	
	public static final String VERSION_UNKNOWN = "VERSION_UNKNOWN";
	
	public static String getIPlugVersion(PlugDescription plugDescription) {
		
		if (((ArrayList)plugDescription.get(PlugDescription.FIELDS)).contains("t01_object.mod_uuid")) {
			return VERSION_IDC_1_0_2_DSC_OBJECT;
		} else {
			return VERSION_UNKNOWN;
		}
	}

}
