/**
 * 
 */
package de.ingrid.portal.search.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.search.detail.DetailDataPreparerHelper;
import de.ingrid.utils.IngridHit;

/**
 * @author Administrator
 *
 */
public class CatalogTreeDataProvider_IDC_1_0_2 implements CatalogTreeDataProvider {

	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#getSubEntities(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public List getSubEntities(String docParentId, String plugId, String plugType, Integer maxNumber) {
        List hits = new ArrayList();
        
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
    		hits = getSubordinatedObjects(docParentId, plugId, null);
    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    		hits = new ArrayList();
    	}

        return hits;
	}

	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#getTopEntities(java.lang.String)
	 */
	public List getTopEntities(String plugId, String plugType) {
        List hits = new ArrayList();
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
    		hits = getTopObjects(plugId);
    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    		hits = IPlugHelperDscEcs.getTopAddresses(plugId);
    	}

        return hits;
	}
	
    /**
     * Get top ECS Objects as a List of IngridHits (containing metadata ID and UDK_CLASS)
     *  
     * @param iPlugId plug id
     * @return List of IngridHit
     */
    public List getTopObjects(String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        ArrayList result = IPlugHelperDscEcs.getHits("t01_object.id:[0 TO A] -parent.object_node.obj_uuid:[0 TO Z]".concat(
                " iplugs:\"".concat(IPlugHelperDscEcs.getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), requestedMetadata, null);
        return result;
    }
    
    public boolean hasChildren(String objUuid, String plugId, String plugType) {
    	List hits = null;
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
    		hits = getSubordinatedObjects(objUuid, plugId, new Integer(1));
    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    		hits = new ArrayList();
    	}
        if (hits != null && hits.size() > 0) {
        	return true;
        } else {
        	return false;
        }
    }
    
    private List getSubordinatedObjects(String objUuid, String plugId, Integer maxResults) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
    	return IPlugHelperDscEcs.getHits("parent.object_node.obj_uuid:".concat(objUuid).concat(
        " iplugs:\"").concat(IPlugHelperDscEcs.getPlugIdFromAddressPlugId(plugId)).concat("\""), requestedMetadata, null, maxResults);
    }

}
