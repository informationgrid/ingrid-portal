/**
 * 
 */
package de.ingrid.portal.search.catalog;

import de.ingrid.portal.search.IPlugVersionInspector;

/**
 * @author joachim
 *
 */
public class CatalogTreeDataProviderFactory {

	public static CatalogTreeDataProvider getDetailDataPreparer(String version) {
		
		if (version.equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else if (version.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_OBJECT)) {
			return new CatalogTreeDataProvider_UDK_5_0();
		} else if (version.equals(IPlugVersionInspector.VERSION_UDK_5_0_DSC_ADDRESS)) {
			return new CatalogTreeDataProvider_UDK_5_0();
		} else if (version.equals(IPlugVersionInspector.VERSION_UNKNOWN)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		}
		
		return null;
	}

}
