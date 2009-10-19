/**
 * 
 */
package de.ingrid.portal.search.catalog;

import java.util.List;

import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.utils.PlugDescription;

/**
 * @author joachim@wemove.com
 *
 */
public class CatalogTreeDataProvider_UDK_5_0 implements CatalogTreeDataProvider {

	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#isCorrupt(de.ingrid.utils.PlugDescription)
	 */
	public boolean isCorrupt(PlugDescription plug) {
		return IPlugHelperDscEcs.isCorrupt(plug);
	}

	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#getSubEntities(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public List getSubEntities(String docParentId, String plugId, String plugType, Integer maxNumber) {
		return IPlugHelperDscEcs.getSubDocs(docParentId, plugId, plugType, maxNumber);
	}

	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#getTopEntities(java.lang.String)
	 */
	public List getTopEntities(String plugId, String plugType) {
		return IPlugHelperDscEcs.getTopDocs(plugId, plugType);
	}

	public boolean hasChildren(String objUuid, String plugId, String plugType) {
		return (IPlugHelperDscEcs.getSubDocs(objUuid, plugId, plugType, null).size() > 0);
	}

}
