/**
 * 
 */
package de.ingrid.portal.search.catalog;

import java.util.List;

import de.ingrid.utils.IngridHit;

/**
 * @author joachim@wemove.com
 *
 */
public interface CatalogTreeDataProvider {
	
	public List getTopEntities(String plugId, String plugType);

	public List getSubEntities(String docParentId, String plugId, String plugType, Integer maxNumber);
	
	public boolean hasChildren(String objUuid, String plugId, String plugType);
	
}
