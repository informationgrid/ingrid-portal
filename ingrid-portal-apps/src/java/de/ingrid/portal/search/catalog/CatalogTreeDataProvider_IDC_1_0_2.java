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
	public List getSubEntities(String docParentUuid, String plugId, String plugType, Integer maxNumber) {
        List hits = new ArrayList();
        
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
    		hits = getSubordinatedObjects(docParentUuid, plugId, null);
    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    		hits = getSubordinatedAddresses(docParentUuid, plugId, null);
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
    		hits = getTopAddresses(plugId);
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

    /**
     * Get top ECS Addresses as a List of IngridHits (containing metadata ID and UDK_CLASS)
     *  
     * @param iPlugId plug id
     * @return List of IngridHit
     */
    public List getTopAddresses(String iPlugId) {
        String[] requestedMetadata = new String[] {
        		Settings.HIT_KEY_ADDRESS_CLASS,
        		Settings.HIT_KEY_ADDRESS_FIRSTNAME,
        		Settings.HIT_KEY_ADDRESS_LASTNAME,
        		Settings.HIT_KEY_ADDRESS_TITLE,
        		"T02_address.address_value",
        		Settings.HIT_KEY_ADDRESS_ADDRID
        };
        
        List result = IPlugHelperDscEcs.getHits("t02_address.id:[0 TO A] -parent.address_node.addr_uuid:[0 TO Z]".concat(
                " iplugs:\"".concat(IPlugHelperDscEcs.getAddressPlugIdFromPlugId(iPlugId)).concat("\"")), requestedMetadata, null);
        return result;
    }
    
    
    public boolean hasChildren(String uuid, String plugId, String plugType) {
    	List hits = null;
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
    		hits = getSubordinatedObjects(uuid, plugId, new Integer(1));
    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    		hits = getSubordinatedAddresses(uuid, plugId, new Integer(1));
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

    private List getSubordinatedAddresses(String addrUuid, String plugId, Integer maxResults) {
        String[] requestedMetadata = new String[] {
        		Settings.HIT_KEY_ADDRESS_CLASS,
        		Settings.HIT_KEY_ADDRESS_FIRSTNAME,
        		Settings.HIT_KEY_ADDRESS_LASTNAME,
        		Settings.HIT_KEY_ADDRESS_TITLE,
        		"T02_address.address_value",
        		Settings.HIT_KEY_ADDRESS_ADDRID
        };
    	return IPlugHelperDscEcs.getHits("parent.address_node.addr_uuid:".concat(addrUuid).concat(
        " iplugs:\"").concat(IPlugHelperDscEcs.getAddressPlugIdFromPlugId(plugId)).concat("\""), requestedMetadata, null, maxResults);
    }
    
}
