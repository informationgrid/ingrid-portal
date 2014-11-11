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
package de.ingrid.portal.search.catalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.search.IPlugVersionInspector;

/**
 * @author joachim
 *
 */
public class CatalogTreeDataProviderFactory {

    private final static Logger log = LoggerFactory.getLogger(CatalogTreeDataProviderFactory.class);	
	
	public static CatalogTreeDataProvider getDetailDataPreparer(String version) {
		
		if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_OBJECT)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_3_DSC_OBJECT)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_ADDRESS)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_ADDRESS)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_OBJECT)) {
			return new CatalogTreeDataProvider_UDK_5_0();
		} else if (version.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_ADDRESS)) {
			return new CatalogTreeDataProvider_UDK_5_0();
		} else if (version.equals(IPlugVersionInspector.VERSION_UNKNOWN)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_8_DSC_OBJECT)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_9_DSC_OBJECT)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_IDF_2_0_0_OBJECT) || version.equals(IPlugVersionInspector.VERSION_IDF_2_0_0_ADDRESS)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else {
			log.error("No CatalogTreeDataProvider could be found for iPlug version '" + version + "'.");
		}
		
		return null;
	}

}
