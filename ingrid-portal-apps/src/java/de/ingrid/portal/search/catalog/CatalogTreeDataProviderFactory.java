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
		} else if (version.equals(IPlugVersionInspector.VERSION_IDF_1_0_0_OBJECT) || version.equals(IPlugVersionInspector.VERSION_IDF_1_0_0_ADDRESS)) {
			return new CatalogTreeDataProvider_IDC_1_0_2();
		} else {
			log.error("No CatalogTreeDataProvider could be found for iPlug version '" + version + "'.");
		}
		
		return null;
	}

}
