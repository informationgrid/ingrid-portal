/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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

	public static final String VERSION_IDC_1_0_3_DSC_OBJECT = "VERSION_IDC_1_0_3_DSC_OBJECT";
	
	public static final String VERSION_IDC_1_0_5_DSC_OBJECT = "VERSION_IDC_1_0_5_DSC_OBJECT";
	
	public static final String VERSION_IDC_1_0_8_DSC_OBJECT = "VERSION_IDC_1_0_8_DSC_OBJECT";

	public static final String VERSION_IDC_1_0_9_DSC_OBJECT = "VERSION_IDC_1_0_9_DSC_OBJECT";
	
	public static final String VERSION_UDK_5_0_DSC_OBJECT = "VERSION_UDK_5_0_DSC_OBJECT";

	public static final String VERSION_IDC_1_0_2_DSC_ADDRESS = "VERSION_IDC_1_0_2_DSC_ADDRESS";

	public static final String VERSION_IDC_1_0_5_DSC_ADDRESS = "VERSION_IDC_1_0_5_DSC_ADDRESS";
	
	public static final String VERSION_UDK_5_0_DSC_ADDRESS = "VERSION_UDK_5_0_DSC_ADDRESS";
	
	public static final String	VERSION_IDF_2_0_0_OBJECT	= "VERSION_IDF_2_0_0_OBJECT";
	
	public static final String	VERSION_IDF_2_0_0_ADDRESS	= "VERSION_IDF_2_0_0_ADDRESS";
	
	
	public static final String VERSION_UNKNOWN = "VERSION_UNKNOWN";
	
	public static String getIPlugVersion(PlugDescription plugDescription) {
		
		if (plugDescription == null) {
			return VERSION_UNKNOWN;
		}
		@SuppressWarnings("unchecked")
		ArrayList<String> fields = (ArrayList<String>)plugDescription.get(PlugDescription.FIELDS);
		
		// try to get the right iPlug Type (object/adress/generic)
		if (IPlugHelper.hasDataType(plugDescription, "IDF_1.0")) {
        	if(IPlugHelper.hasDataType(plugDescription, "metadata")){
        		return VERSION_IDF_2_0_0_OBJECT;
        	}else{
        		return VERSION_IDF_2_0_0_ADDRESS;
        	}
        } else if (fields != null && fields.contains("t011_obj_serv.has_access_constraint")) {
			return VERSION_IDC_1_0_9_DSC_OBJECT;
		} else if (fields != null && fields.contains("object_use.terms_of_use")) {
			return VERSION_IDC_1_0_8_DSC_OBJECT;
		} else if (fields != null && fields.contains("t01_object.data_language_key") && fields.contains("t01_object.metadata_language_key") && fields.contains("t02_address.country_key")) {
			return VERSION_IDC_1_0_5_DSC_OBJECT;
		} else if (fields != null && fields.contains("t01_object.obj_id") && fields.contains("parent.object_node.obj_uuid") && fields.contains("object_access.terms_of_use")) {
			return VERSION_IDC_1_0_3_DSC_OBJECT;
		} else if (fields != null && fields.contains("t01_object.obj_id") && fields.contains("parent.object_node.obj_uuid")) {
			return VERSION_IDC_1_0_2_DSC_OBJECT;
		} else if (fields != null && fields.contains("t02_address.adr_id") && fields.contains("parent.address_node.addr_uuid") && fields.contains("t02_address.country_key")) {
			return VERSION_IDC_1_0_5_DSC_ADDRESS;
		} else if (fields != null && fields.contains("t02_address.adr_id") && fields.contains("parent.address_node.addr_uuid")) {
			return VERSION_IDC_1_0_2_DSC_ADDRESS;
		} else if (fields != null && fields.contains("t01_object.obj_id") && 
				(IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)
                || IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_ECS)
                || IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_CSW)) ) {		
			return VERSION_UDK_5_0_DSC_OBJECT;
		} else if (IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
				return VERSION_UDK_5_0_DSC_ADDRESS;
		}
		return VERSION_UNKNOWN;
	}

}
