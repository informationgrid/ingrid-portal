/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.portal.global;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.util.*;

/**
 * Helper class dealing with all aspects of DSC_ECS iPlugs.
 *
 * @author joachim@wemove.com
 */
public class IPlugHelperDscEcs extends IPlugHelper {

    private static final Logger log = LoggerFactory.getLogger(IPlugHelperDscEcs.class);

    private static final String KEY_RELATION_FILTER = "RELATION_FILTER";
    private static final String KEY_RELATION_FILTER_FROM_OBJ_ID = "RELATION_FILTER_FROM_OBJ_ID";
    private static final String KEY_RELATION_FILTER_TYPE = "RELATION_FILTER_TYPE";
    private static final String KEY_RELATION_FILTER_TO_OBJ_ID = "RELATION_FILTER_TO_OBJ_ID";
    
	static final String FIELD_OBJECT_ROOT = "t01_object.root";
	static final String FIELD_ADDRESS_ROOT = "T02_address.root";

	/** Check whether UDK_5_0 plug description is "corrupt" so no top entities can be fetched ! */
	public static boolean isCorrupt(PlugDescription plug) {
		boolean isCorrupt = true;

		// check needed fields to determine top entities !
    	String[] fields = plug.getFields();
    	if (fields != null && fields.length > 0) {
    		List fieldList = Arrays.asList(fields);

    		if ((IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS) && fieldList.contains(FIELD_OBJECT_ROOT)) ||
    		    (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS) && fieldList.contains(FIELD_ADDRESS_ROOT))) {
            	isCorrupt = false;
            }
    	}

    	if (isCorrupt && log.isDebugEnabled()) {
			log.debug("CORRUPT PlugDescription ! We skip this plug: " + plug);
    	}

		return isCorrupt;
	}

    /**
     * Get top UDK_5_0 ECS Objects as a List of IngridHits (containing metadata ID and UDK_CLASS)
     *  
     * @param objId parent object
     * @param iPlugId plug id
     * @return List of IngridHit
     */
    public static List getTopObjects(String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        return getHits(FIELD_OBJECT_ROOT + ":1".concat(
                " iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), requestedMetadata, null);
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
    private static List getSubordinatedObjects(String objId, String iPlugId, Integer maxNumber) {
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
        return getHits("t012_obj_obj.object_from_id:".concat(objId).concat(
        	" t012_obj_obj.typ:0 iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")),
        	requestedMetadata, filter, maxNumber);
    }
    
    /**
     * Get ALL subordinated ECS Objects as a List of IngridHits (containing metadata ID and UDK_CLASS)
     *  
     * @param objId parent object
     * @param iPlugId plug id
     * @return List of IngridHit
     */
    public static List getSubordinatedObjects(String objId, String iPlugId) {
    	return getSubordinatedObjects(objId, iPlugId, null);
    }

    /**
     * Get top UDK_5_0 ECS Addresses as a List of IngridHits (containing metadata ID and UDK_CLASS)
     *  
     * @param objId parent object
     * @param iPlugId plug id
     * @return List of IngridHit
     */
    public static List getTopAddresses(String iPlugId) {
        String[] requestedMetadata = new String[7];
        requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
        requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_ADDRESS_FIRSTNAME;
        requestedMetadata[3] = Settings.HIT_KEY_ADDRESS_LASTNAME;
        requestedMetadata[4] = Settings.HIT_KEY_ADDRESS_TITLE;
        requestedMetadata[5] = Settings.HIT_KEY_ADDRESS_ADDRESS;
        requestedMetadata[6] = Settings.HIT_KEY_ADDRESS_ADDRID;
        return getHits(FIELD_ADDRESS_ROOT + ":1".concat(
        	" iplugs:\"".concat(getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
            requestedMetadata, null);
    }

    /**
     * Get a restricted Number of Sub Addresses or all Sub Addresses
     * @param maxNumber how many addresses requested ? pass null if all addresses !
     */
    private static List getAddressChildren(String addrId, String iPlugId, Integer maxNumber) {
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
        return getHits(
                "T022_adr_adr.adr_from_id:".concat(addrId).concat(" iplugs:\"".concat(getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter, maxNumber);
    }

    /**
     * Get ALL Sub Addresses !
     */
    public static List getAddressChildren(String addrId, String iPlugId) {
    	return getAddressChildren(addrId, iPlugId, null);
    }

    public static List getTopDocs(String plugId, String plugType) {
        ArrayList hits = new ArrayList();
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
    		hits = (ArrayList) IPlugHelperDscEcs.getTopObjects(plugId);
    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    		hits = (ArrayList) IPlugHelperDscEcs.getTopAddresses(plugId);
    	}

        return hits;
    }

    /**
     * Get a restricted Number of Sub UDK Documents or all Sub Documents
     * @param maxNumber how many docs requested ? NOTICE: returned number of docs may be
     * one less than requested, because parentDoc is also delivered by backend and filtered !
     * If you pass 1 as maxNumber it will be set to 2 internally to guarantee at least one subHit !
     */
    public static List getSubDocs(String docParentId, String plugId, String plugType, Integer maxNumber) {
        ArrayList hits = new ArrayList();
        
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
   			hits = (ArrayList) IPlugHelperDscEcs.getSubordinatedObjects(docParentId, plugId, maxNumber);

    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
    			hits = (ArrayList) IPlugHelperDscEcs.getAddressChildren(docParentId, plugId, maxNumber);
    	}

        return hits;
    }

    /**
     * Returns the corresponding address plug id of the iPlug identified by <em>plugId</em>. 
     * If the iPlug identified by <em>plugId</em> has the datatype "address" set, 
     * the parameter <em>plugId</em> will be returned unchanged. 
     * 
     * Otherwise it tries to obtain the corresponding object iPlug from the plugdescription. 
     * If this fails it will fall back to a convention:
     * 
     * It adds the postfix "_addr" to the plug id. Only plug id's that contain the string "udk-db" and do
     * not contain the postfix "_addr" will be changed.
     * 
     * @param plugId
     * @return
     */
    public static String getAddressPlugIdFromPlugId(String plugId) {
    	String correspondentIPlug = null;
    	PlugDescription cIPlugDescr = IBUSInterfaceImpl.getInstance().getIPlug(plugId);
    	if (cIPlugDescr != null) {
        	if (IPlugHelper.hasDataType(cIPlugDescr, "address")) {
        		correspondentIPlug = plugId;
        	} else {
        		correspondentIPlug = cIPlugDescr.getCorrespondentProxyServiceURL();
        	}
    	}
    	
    	if (correspondentIPlug == null || correspondentIPlug.trim().length() == 0 || correspondentIPlug.equals("null")) {
	        if (plugId != null && plugId.indexOf("udk-db") > -1 && !plugId.endsWith("_addr")) {
	        	correspondentIPlug = plugId.concat("_addr");
	        } else {
	        	correspondentIPlug = plugId;
	        }
    	}
    	return correspondentIPlug;
    }

    /**
     * Returns the corresponding object plug id of the iPlug identified by <em>plugId</em>. 
     * If the iPlug identified by <em>plugId</em> has NOT the datatype "address" set, 
     * the parameter <em>plugId</em> will be returned unchanged. 
     * 
     * Otherwise it tries to obtain the corresponding address iPlug from the plugdescription. 
     * If this fails it will fall back to a convention:
     * 
     * Returns the objects plug id of the corresponding address plug id. Address plug
     * id's are detected from the postfix "_addr". The postfix will be stripped.
     * Plug id's without this postfix remain unchanged.
     * 
     * @param plugId
     * @return
     */
    public static String getPlugIdFromAddressPlugId(String plugId) {
    	String correspondentIPlug = null;
    	PlugDescription cIPlugDescr = IBUSInterfaceImpl.getInstance().getIPlug(plugId);
    	
    	if (cIPlugDescr != null) {
        	if (!IPlugHelper.hasDataType(cIPlugDescr, "address")) {
        		correspondentIPlug = plugId;
        	} else {
        		correspondentIPlug = cIPlugDescr.getCorrespondentProxyServiceURL();
        	}
    	}
    	
    	if (correspondentIPlug == null || correspondentIPlug.trim().length() == 0 || correspondentIPlug.equals("null")) {
	        if (plugId == null) {
	        	correspondentIPlug = "";
	        } else if (plugId.endsWith("_addr")) {
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
    public static List getHits(String queryStr, String[] requestedMetaData, Map filter, Integer maxNumberOfHits) {
        ArrayList result = new ArrayList();
        
        // request hits in chunks of 20 results per page, when all hits requested !
        int chunkSize = 20;
        boolean fetchNextChunk = true;
        try {
            IngridQuery query = QueryStringParser.parse(queryStr.concat(" ranking:any"));
            IngridHits hits;
            // request hits in chunks or all at once, when restricted number of hits !
            int page = 0;
            do {
                page++;
                hits = IBUSInterfaceImpl.getInstance().searchAndDetail(query, chunkSize, page, (page-1) * chunkSize,
                		PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000), requestedMetaData);
                for (int j = 0; j < hits.getHits().length; j++) {
                	IngridHit hit = hits.getHits()[j];
                    IngridHitDetail detail = hit.getHitDetail();

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
                    if (maxNumberOfHits != null &&  result.size() >= maxNumberOfHits.intValue()) {
                		fetchNextChunk = false;
                		break;
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
    public static List getHits(String queryStr, String[] requestedMetaData, Map filter) {
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
    public static Map addRelationFilter(Map filterMap,
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
    public static IngridHitDetail filterRelationHit(String fromObjId, String relationType, String toObjId, IngridHitDetail detail) {
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
    		if (fromObjId != null && !fromObjIds[j].equals(fromObjId)) {
				continue;
    		}
    		if (relationType != null && !types[j].equals(relationType)) {
				continue;
    		}
    		if (toObjId != null && !toObjIds[j].equals(toObjId)) {
				continue;
    		}

    		// valid relation ! also valid result id ?
    		if (fromObjId == null && !fromObjIds[j].equals(hitObjId)) {
    			// searching from
        		log.warn("HIT object ID is different from found FROM object ID ! we skip hit ");
        		continue;
    		}
    		if (toObjId == null && !toObjIds[j].equals(hitObjId)) {
    			// searching to
        		log.warn("HIT object ID is different from found TO object ID ! we skip hit ");
        		continue;
    		}
    		
    		// VALID HIT !
    		retHitDetail = detail;
    	}
	
    	return retHitDetail;
    }

    /**
     * Inner class: PlugComparator for ECS plugs sorting -> sorted by "Partner"/"Name"/"Type" (Object before Address ECS)
     */
    public static class PlugComparatorECS implements Comparator {
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

                    StringBuilder sortString = new StringBuilder("");
                    for (int j = 0; j < partners.length; j++) {
                    	sortString.append(UtilsDB.getPartnerFromKey(partners[j]));
                    }
                    sortString.append(name);
                    sortString.append(type);
                    
                    plugSortCriteria[i] = sortString.toString();
            	}

            	// Get the collator for the German Locale (for correct sorting of �,�,� ...)  
            	Collator germanCollator = Collator.getInstance(Locale.GERMAN);
            	return germanCollator.compare(plugSortCriteria[0], plugSortCriteria[1]);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
