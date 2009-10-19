/**
 * 
 */
package de.ingrid.portal.search.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.portal.global.Settings;
import de.ingrid.utils.PlugDescription;

/**
 * @author Administrator
 *
 */
public class CatalogTreeDataProvider_IDC_1_0_2 implements CatalogTreeDataProvider {

    private final static Log log = LogFactory.getLog(CatalogTreeDataProviderFactory.class);	

	static final String FIELD_OBJECT_ID = "t01_object.id";
	static final String FIELD_PARENT_OBJ_UUID = "parent.object_node.obj_uuid";
	static final String FIELDS_ADDRESS_ID = "t02_address.id";
	static final String FIELD_PARENT_ADDR_UUID = "parent.address_node.addr_uuid";
	
	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#isCorrupt(de.ingrid.utils.PlugDescription)
	 */
	public boolean isCorrupt(PlugDescription plug) {
		boolean isCorrupt = true;

		// check needed fields to determine top entities !
    	String[] fields = plug.getFields();
    	if (fields != null && fields.length > 0) {
    		List fieldList = Arrays.asList(fields);

    		if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
    			if (fieldList.contains(FIELD_OBJECT_ID) && fieldList.contains(FIELD_PARENT_OBJ_UUID)) {
                	isCorrupt = false;
    			}
            } else if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    			if (fieldList.contains(FIELDS_ADDRESS_ID) && fieldList.contains(FIELD_PARENT_ADDR_UUID)) {
                	isCorrupt = false;
    			}
            }
    	}
    	
    	if (isCorrupt) {
			log.warn("CORRUPT PlugDescription ! We skip this plug: " + plug);
    	}

		return isCorrupt;
	}

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
        ArrayList result = IPlugHelperDscEcs.getHits(FIELD_OBJECT_ID + ":[0 TO A] -" + FIELD_PARENT_OBJ_UUID + ":[0 TO Z]".concat(
                " iplugs:\"".concat(iPlugId).concat("\"")), requestedMetadata, null);
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
        
        List result = IPlugHelperDscEcs.getHits(FIELDS_ADDRESS_ID + ":[0 TO A] -" + FIELD_PARENT_ADDR_UUID + ":[0 TO Z]".concat(
                " iplugs:\"".concat(iPlugId).concat("\"")), requestedMetadata, null);
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
    	return IPlugHelperDscEcs.getHits(FIELD_PARENT_OBJ_UUID + ":".concat(objUuid).concat(
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
    	return IPlugHelperDscEcs.getHits(FIELD_PARENT_ADDR_UUID + ":".concat(addrUuid).concat(
        " iplugs:\"").concat(IPlugHelperDscEcs.getAddressPlugIdFromPlugId(plugId)).concat("\""), requestedMetadata, null, maxResults);
    }
    
}
