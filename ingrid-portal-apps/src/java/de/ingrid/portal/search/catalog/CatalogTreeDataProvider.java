/**
 * 
 */
package de.ingrid.portal.search.catalog;

import java.util.List;

import de.ingrid.utils.PlugDescription;

/**
 * @author joachim@wemove.com
 *
 */
public interface CatalogTreeDataProvider {
	
	/** Check whether plug description is "corrupt" so no top entities can be fetched ! */
	public boolean isCorrupt(PlugDescription plug);

	public List getTopEntities(String plugId, String plugType);

	public List getSubEntities(String docParentId, String plugId, String plugType, Integer maxNumber);
	
	public boolean hasChildren(String objUuid, String plugId, String plugType);
	
}
