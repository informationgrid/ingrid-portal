/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.portal.global;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Helper class dealing with all aspects of DSC_ECS iPlugs.
 *
 * @author joachim@wemove.com
 */
public class IPlugHelperDscEcs extends IPlugHelper {

    static private final Log log = LogFactory.getLog(IPlugHelperDscEcs.class);

    static private final String KEY_RELATION_FILTER = "RELATION_FILTER";
    static private final String KEY_RELATION_FILTER_FROM_OBJ_ID = "RELATION_FILTER_FROM_OBJ_ID";
    static private final String KEY_RELATION_FILTER_TYPE = "RELATION_FILTER_TYPE";
    static private final String KEY_RELATION_FILTER_TO_OBJ_ID = "RELATION_FILTER_TO_OBJ_ID";

    /**
     * Get top ECS Objects as a List of IngridHits (containing metadata ID and UDK_CLASS)
     *  
     * @param objId parent object
     * @param iPlugId plug id
     * @return List of IngridHit
     */
    static public ArrayList getTopObjects(String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        ArrayList result = getHits("t01_object.root:1".concat(
                " iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), requestedMetadata, null);
        return result;
    }

    /**
     * Get Restricted Number of subordinated ECS Objects as a List of IngridHits (containing metadata ID and UDK_CLASS).
     * Or pass null to request ALL objects
     *  
     * @param objId parent object
     * @param iPlugId plug id
     * @param maxNumber how many objects requested ? pass null if all objects !
     * @return List of IngridHit
     */
    static private ArrayList getSubordinatedObjects(String objId, String iPlugId, Integer maxNumber) {
        String[] requestedMetadata = new String[5];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_OBJ_OBJ_FROM;
        requestedMetadata[3] = Settings.HIT_KEY_OBJ_OBJ_TYP;
        requestedMetadata[4] = Settings.HIT_KEY_OBJ_OBJ_TO;
        HashMap filter = new HashMap();
        // gleiches Objekt raus filtern
        filter.put(Settings.HIT_KEY_OBJ_ID, objId);
        // HACK !!!
        // we also apply filter for child relations WHEN FETCHING HITS !!!
        // SO WE ALWAYS GET REAL CHILDREN !!!
        addRelationFilter(filter, objId, "0", null);
        ArrayList result = getHits("t012_obj_obj.object_from_id:".concat(objId).concat(
        	" t012_obj_obj.typ:0 iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")),
        	requestedMetadata, filter, maxNumber);
        return result;
    }
    
    /**
     * Get ALL subordinated ECS Objects as a List of IngridHits (containing metadata ID and UDK_CLASS)
     *  
     * @param objId parent object
     * @param iPlugId plug id
     * @return List of IngridHit
     */
    static public ArrayList getSubordinatedObjects(String objId, String iPlugId) {
    	return getSubordinatedObjects(objId, iPlugId, null);
    }

    /**
     * Get top ECS Addresses as a List of IngridHits (containing metadata ID and UDK_CLASS)
     *  
     * @param objId parent object
     * @param iPlugId plug id
     * @return List of IngridHit
     */
    static public ArrayList getTopAddresses(String iPlugId) {
        String[] requestedMetadata = new String[7];
        requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
        requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_ADDRESS_FIRSTNAME;
        requestedMetadata[3] = Settings.HIT_KEY_ADDRESS_LASTNAME;
        requestedMetadata[4] = Settings.HIT_KEY_ADDRESS_TITLE;
        requestedMetadata[5] = Settings.HIT_KEY_ADDRESS_ADDRESS;
        requestedMetadata[6] = Settings.HIT_KEY_ADDRESS_ADDRID;
        ArrayList result = getHits("T02_address.root:1".concat(
        	" iplugs:\"".concat(getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
            requestedMetadata, null);
        return result;
    }

    /**
     * Get a restricted Number of Sub Addresses or all Sub Addresses
     * @param maxNumber how many addresses requested ? pass null if all addresses !
     */
    static private ArrayList getAddressChildren(String addrId, String iPlugId, Integer maxNumber) {
        String[] requestedMetadata = new String[7];
        requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
        requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_ADDRESS_FIRSTNAME;
        requestedMetadata[3] = Settings.HIT_KEY_ADDRESS_LASTNAME;
        requestedMetadata[4] = Settings.HIT_KEY_ADDRESS_TITLE;
        requestedMetadata[5] = Settings.HIT_KEY_ADDRESS_ADDRESS;
        requestedMetadata[6] = Settings.HIT_KEY_ADDRESS_ADDRID;
        HashMap filter = new HashMap();
        filter.put(Settings.HIT_KEY_ADDRESS_ADDRID, addrId);
        ArrayList result = getHits(
                "T022_adr_adr.adr_from_id:".concat(addrId).concat(" iplugs:\"".concat(getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter, maxNumber);
        return result;    	
    }

    /**
     * Get ALL Sub Addresses !
     */
    static public ArrayList getAddressChildren(String addrId, String iPlugId) {
    	return getAddressChildren(addrId, iPlugId, null);
    }

    static public ArrayList getTopDocs(String plugId, String plugType) {
        ArrayList hits = new ArrayList();
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
    		hits = IPlugHelperDscEcs.getTopObjects(plugId);
    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    		hits = IPlugHelperDscEcs.getTopAddresses(plugId);
    	}

        return hits;
    }

    /**
     * Get a restricted Number of Sub UDK Documents or all Sub Documents
     * @param maxNumber how many docs requested ? NOTICE: returned number of docs may be
     * one less than requested, because parentDoc is also delivered by backend and filtered !
     * If you pass 1 as maxNumber it will be set to 2 internally to guarantee at least one subHit !
     */
    static public ArrayList getSubDocs(String docParentId, String plugId, String plugType, Integer maxNumber) {
        ArrayList hits = new ArrayList();
        
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
   			hits = IPlugHelperDscEcs.getSubordinatedObjects(docParentId, plugId, maxNumber);

    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    			hits = IPlugHelperDscEcs.getAddressChildren(docParentId, plugId, maxNumber);
    	}

        return hits;
    }

    /**
     * Returns the address plug id of the corresponding plug id, by adding the postfix
     * "_addr" to the plug id. Only plug id's that contain the string "udk-db" and do
     * not contain the postfix "_addr" will be changed.
     * 
     * @param plugId
     * @return
     */
    static public String getAddressPlugIdFromPlugId(String plugId) {
    	String correspondentIPlug = IBUSInterfaceImpl.getInstance().getIPlug(plugId)
    		.getCorrespondentProxyServiceURL();
    	
    	if (correspondentIPlug == null || correspondentIPlug == "") {
	        if (plugId != null && plugId.indexOf("udk-db") > -1 && !plugId.endsWith("_addr")) {
	        	correspondentIPlug = plugId.concat("_addr");
	        } else {
	        	correspondentIPlug = plugId;
	        }
    	}
    	return correspondentIPlug;
    }

    /**
     * Returns the plug id of the corresponding address plug id. Address plug
     * id's are detected from the postfix "_addr". The postfix will be stripped.
     * Plug id's without this postfix remain unchanged.
     * 
     * @param plugId
     * @return
     */
    static public String getPlugIdFromAddressPlugId(String plugId) {
    	String correspondentIPlug = IBUSInterfaceImpl.getInstance().getIPlug(plugId)
    		.getCorrespondentProxyServiceURL();
    	
    	if (correspondentIPlug == null || correspondentIPlug == "") {
	        if (plugId == null) {
	        	correspondentIPlug = "";
	        } else  if (plugId.endsWith("_addr")) {
	        	correspondentIPlug = plugId.substring(0, plugId.lastIndexOf("_addr"));
	        } else {
	        	correspondentIPlug = plugId;
	        }
    	}
    	
    	return correspondentIPlug;
    }

    /**
     * Get a restricted Number of Hits or all hits.
     * @param maxNumberOfHits how many hits requested ? pass null if all hits !
     * @return
     */
    static public ArrayList getHits(String queryStr, String[] requestedMetaData, HashMap filter, Integer maxNumberOfHits) {
        ArrayList result = new ArrayList();
        
        // request hits in chunks of 20 results per page, when all hits requested !
        int chunkSize = 20;
        boolean fetchNextChunk = true;
        try {
            IngridQuery query = QueryStringParser.parse(queryStr.concat(" ranking:any datatype:any"));
            IngridHits hits;
            // request hits in chunks or all at once, when restricted number of hits !
            int page = 0;
            do {
                page++;
                hits = IBUSInterfaceImpl.getInstance().searchAndDetail(query, chunkSize, page, (page-1) * chunkSize,
                		PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000), requestedMetaData);
                for (int j = 0; j < hits.length(); j++) {
                	IngridHit hit = hits.getHits()[j];
                    IngridHitDetail detail = (IngridHitDetail) hit.getHitDetail();

                    // convert all requested detail fields into string ... -> needed for filters (relation type)
                    for (int i = 0; i < requestedMetaData.length; i++) {
                        detail.put(requestedMetaData[i], UtilsSearch.getDetailValue(detail, requestedMetaData[i]));
                    }
                    boolean include = true;
                    if (filter != null && filter.size() > 0) {
                        Iterator it = filter.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry entry = (Map.Entry) it.next();
                            String recordKey = (String) entry.getKey();
                            if (KEY_RELATION_FILTER.equals(recordKey)) {
                            	// HACK !!! we check whether hit satisfies given relation type !!!
                                HashMap relFilter = (HashMap) entry.getValue();
                                IngridHitDetail filteredHitDetail = 
                                	filterRelationHit((String)relFilter.get(KEY_RELATION_FILTER_FROM_OBJ_ID),
                               			(String)relFilter.get(KEY_RELATION_FILTER_TYPE),
                               			(String)relFilter.get(KEY_RELATION_FILTER_TO_OBJ_ID),
                               			detail);
                                if (filteredHitDetail == null) {
                                    include = false;
                                    break;
                                }                            	
                            	
                            } else {
                            	// "normal" filter processing ! we just compare one value !
                                String value = (String) entry.getValue();
                                if (value.equals(UtilsSearch.getDetailValue(detail, recordKey))) {
                                    include = false;
                                    break;
                                }                            	
                            }
                        }
                    }
                    if (include) {
                        hit.put(Settings.RESULT_KEY_DETAIL, detail);
                        result.add(hit);
                    }
                    // reduced number of hits requested and already fetched ? -> no further hits
                    if (maxNumberOfHits != null) {
                    	if (result.size() >= maxNumberOfHits.intValue()) {
                    		fetchNextChunk = false;
                    		break;
                    	}
                    }
                }
                // less than chunk size hits fetched ? -> there are no further hits
                if (hits.getHits().length != chunkSize) {
            		fetchNextChunk = false;                    	
                }
                // reduced number of hits requested and already fetched -> no further hits
            } while (fetchNextChunk);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.error("Problems getting hits from iBus!", e);
            } else {
                log.info("Problems getting hits from iBus!");
            }
        }
        return result;
    }

    /**
     * Get ALL hits
     */
    static public ArrayList getHits(String queryStr, String[] requestedMetaData, HashMap filter) {
    	return getHits(queryStr, requestedMetaData, filter, null);
    }

    /**
     * Add a filter describing an object relation to a filter map
     * @param filterMap the map where the relation filter is added
     * @param fromObjId source object of relation
     * @param relationType type of relation
     * @param toObjId target object of relation
     * @return the filterMap containing the added relation filter
     */
    static public HashMap addRelationFilter(HashMap filterMap,
    		String fromObjId, String relationType, String toObjId) {
        HashMap relationFilter = new HashMap();
        relationFilter.put(KEY_RELATION_FILTER_FROM_OBJ_ID, fromObjId);
        relationFilter.put(KEY_RELATION_FILTER_TYPE, relationType);
        relationFilter.put(KEY_RELATION_FILTER_TO_OBJ_ID, toObjId);
        filterMap.put(KEY_RELATION_FILTER, relationFilter);
        
        return filterMap;
    }

    /**
     * Filter A HIT ! Returns passed Hit only, if it satisfies a specific object relation.
     * Pass the description of the relation (from, to, type) and the unfiltered hit detail.
     * This is necessary due to problems in backend to exactly resolve a relation query (index is flattended
     * and index fields contain multiple relation values). 
     * @param fromObjId udk ObjectID of the source, pass null if this is the one you search for
     * @param relationType udk type of the relation
     * @param toObjId udk ObjectID of the target, pass null if this is the one you search for
     * @param detail the hit detail to check; passed from ibus query
     * @return null if hit doesn't satisfy relation else the hit detail itself !
     */
    static public IngridHitDetail filterRelationHit(String fromObjId, String relationType, String toObjId, IngridHitDetail detail) {
		if (detail == null) {
			return null;
		}

		// hit data
    	String hitObjId = (String) detail.get(Settings.HIT_KEY_OBJ_ID);
    	String hitFromObjIds = (String) detail.get(Settings.HIT_KEY_OBJ_OBJ_FROM);
    	String hitTypes = (String) detail.get(Settings.HIT_KEY_OBJ_OBJ_TYP);
    	String hitToObjIds = (String) detail.get(Settings.HIT_KEY_OBJ_OBJ_TO);
        	
    	if (hitObjId == null || hitFromObjIds == null || hitTypes == null || hitToObjIds == null) {
    		log.warn("NO Relation Data in HIT !");
    		return null;
    	}

    	// split values in string into array
    	String[] hitObjIds = hitObjId.split(UtilsSearch.DETAIL_VALUES_SEPARATOR);
    	String[] fromObjIds = hitFromObjIds.split(UtilsSearch.DETAIL_VALUES_SEPARATOR);
    	String[] toObjIds = hitToObjIds.split(UtilsSearch.DETAIL_VALUES_SEPARATOR);
    	String[] types = hitTypes.split(UtilsSearch.DETAIL_VALUES_SEPARATOR);
        	
    	if (hitObjIds.length > 1) {
    		log.error("Multiple HIT Ids in ONE Hit, Ids: " + hitObjId);
    		return null;
    	}
    	if (fromObjIds.length != types.length ||
        	fromObjIds.length != toObjIds.length) {
    		log.error("WRONG Relation Data in HIT ! different number of \"from, to, type\" objects !");
    		return null;
    	}
    	if (fromObjIds.length == 0) {
    		log.warn("NO from Relation Data in HIT !");
    		return null;
    	}
        	
    	// ok, start filtering
    	IngridHitDetail retHitDetail = null;
    	for (int j = 0; j < fromObjIds.length; j++) {
    		if (fromObjId != null) {
    			if (!fromObjIds[j].equals(fromObjId)) {
    				continue;
    			}
    		}
    		if (relationType != null) {
    			if (!types[j].equals(relationType)) {
    				continue;
    			}
    		}
    		if (toObjId != null) {
    			if (!toObjIds[j].equals(toObjId)) {
    				continue;
    			}
    		}

    		// valid relation ! also valid result id ?
    		if (fromObjId == null) {
    			// searching from
    			if (!fromObjIds[j].equals(hitObjId)) {
            		log.warn("HIT object ID is different from found FROM object ID ! we skip hit ");
            		continue;
    			}
    		}
    		if (toObjId == null) {
    			// searching to
    			if (!toObjIds[j].equals(hitObjId)) {
            		log.warn("HIT object ID is different from found TO object ID ! we skip hit ");
            		continue;
    			}
    		}
    		
    		// VALID HIT !
    		retHitDetail = detail;
    	}
	
    	return retHitDetail;
    }

    /**
     * Inner class: PlugComparator for ECS plugs sorting -> sorted by "Partner"/"Name"/"Type" (Object before Address ECS)
     */
    static public class PlugComparatorECS implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public final int compare(Object a, Object b) {
            try {
            	PlugDescription[] plugs = new PlugDescription[] {
            		(PlugDescription) a,
            		(PlugDescription) b
            	}; 
            	String[] plugSortCriteria = new String[plugs.length];
            	
            	for (int i = 0; i < plugs.length; i++) {
                    String[] partners = plugs[i].getPartners();
                    String name = plugs[i].getDataSourceName().toLowerCase();
                    // object ECS before Address ECS !
                    String type = hasDataType(plugs[i], Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS) ? "0" : "1";

                    StringBuffer sortString = new StringBuffer("");
                    for (int j = 0; j < partners.length; j++) {
                    	sortString.append(UtilsDB.getPartnerFromKey(partners[j]));
                    }
                    sortString.append(name);
                    sortString.append(type);
                    
                    plugSortCriteria[i] = sortString.toString();
            	}

            	// Get the collator for the German Locale (for correct sorting of ä,ö,ü ...)  
            	Collator germanCollator = Collator.getInstance(Locale.GERMAN);
            	return germanCollator.compare(plugSortCriteria[0], plugSortCriteria[1]);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
