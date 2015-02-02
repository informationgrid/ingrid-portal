/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsFileHelper;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.udk.UtilsDate;

/**
 * @author joachim
 *
 */
public class DetailDataPreparerHelper {

	public static void getUDKAddressParents(HashMap result, String addrId, String iPlugId) throws Exception {
        // get id of the address
        String addressId = addrId;
        // set initial address type to 1
        String addressType = "";
        do {
            // get the paren address hit + detail
            IngridHit parent = getParentAddress(addressId, iPlugId);
            if (parent == null) {
                // no parent found
                break;
            }
            addressType = UtilsSearch.getDetailValue((IngridHitDetail) parent.get(Settings.RESULT_KEY_DETAIL),
                    Settings.HIT_KEY_ADDRESS_CLASS);
            addressId = UtilsSearch.getDetailValue((IngridHitDetail) parent.get(Settings.RESULT_KEY_DETAIL),
                    Settings.HIT_KEY_ADDRESS_ADDRID);
            if (addressType.equals("0")) {
                if (!result.containsKey("institutions")) {
                    result.put("institutions", new ArrayList());
                }
                ArrayList institutions = (ArrayList) result.get("institutions");
                institutions.add(0,parent);
            } else if (addressType.equals("1")) {
                if (!result.containsKey("units")) {
                    result.put("units", new ArrayList());
                }
                ArrayList units = (ArrayList) result.get("units");
                units.add(0, parent);
            }
            // exit loop if parent was NO unit
        } while (true);
    }
	
	public static IngridHit getParentAddress(String addrId, String iPlugId) {
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
        ArrayList result = getHits("T022_adr_adr.adr_to_id:".concat(addrId).concat(" iplugs:\"".concat(getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter);
        if (result.size() > 0) {
            return (IngridHit) result.get(0);
        } else {
            return null;
        }
    }
	
    public static String getAddressPlugIdFromPlugId(String plugId) {
    	return IPlugHelperDscEcs.getAddressPlugIdFromPlugId(plugId);
    }

    public static String getPlugIdFromAddressPlugId(String plugId) {
    	return IPlugHelperDscEcs.getPlugIdFromAddressPlugId(plugId);
    }
    
    public static ArrayList getHits(String queryStr, String[] requestedMetaData, HashMap filter, Integer maxNumHits) {
    	return IPlugHelperDscEcs.getHits(queryStr, requestedMetaData, filter, maxNumHits);
    }
    
    public static ArrayList getHits(String queryStr, String[] requestedMetaData, HashMap filter) {
    	return IPlugHelperDscEcs.getHits(queryStr, requestedMetaData, filter);
    }
    
    public static ArrayList getAddressChildren(String addrId, String iPlugId) {
    	return IPlugHelperDscEcs.getAddressChildren(addrId, iPlugId);
    }
    
    
    private static void addSubRecords(Record record, HashMap map, Locale locale, int level, boolean readableColumns,
            IngridResourceBundle messages, List dateFields, HashMap replacementFields, HashMap filter) {
        level++;
        Column[] columns;
        ArrayList subRecordList;
        Record[] subRecords = record.getSubRecords();

        for (int i = 0; i < subRecords.length; i++) {
            HashMap subRecordMap = new LinkedHashMap();
            columns = subRecords[i].getColumns();
            if (columns.length == 0) {
            	continue;
            }
            // filter relations !
            String targetName = columns[0].getTargetName();
            boolean include = true;
            if (filter != null && filter.size() > 0) {
                Iterator it = filter.entrySet().iterator();
                String targetNameLowerCase = targetName.toLowerCase();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String filterKeyLowerCase = ((String) entry.getKey()).toLowerCase(locale);
                    if (targetNameLowerCase.startsWith(filterKeyLowerCase)) {
                    	// found relation is filtered, we ignore this record !
                    	include = false;
                    	break;
                    }
                }
            }
            if (!include) {
            	continue;
            }

            for (int j = 0; j < columns.length; j++) {
				if (!columns[j].getType().equals(Column.BINARY)) {
					if (columns[j].toIndex()) {
						String columnName = columns[j].getTargetName();
						if (!columnName.equals(UtilsFileHelper.FILE_TITLE) && !columnName.equals(UtilsFileHelper.MIME)){
							columnName = columnName.toLowerCase();
							
							if (dateFields.contains(columnName)) {
								subRecordMap.put(columnName, UtilsDate.parseDateToLocale(subRecords[i].getValueAsString(columns[j]).trim(), locale));
							} else if (replacementFields.containsKey(columnName)) {
								// replace value according to translation config
								Map transMap = (Map) replacementFields.get(columnName);
								String src = subRecords[i].getValueAsString(columns[j]).trim();
								if (transMap.containsKey(src)) {
									subRecordMap.put(columnName, transMap.get(src));
								} else {
									subRecordMap.put(columnName, src);
								}
							} else {
								subRecordMap.put(columnName, subRecords[i].getValueAsString(columns[j]).trim().replaceAll("\n", "<br />"));
							}
						}
					}
				}
            }
            if (readableColumns) {
                targetName = targetName.replace('_', ' ');
            } else {
                targetName = targetName.toLowerCase();
            }
            if (map.containsKey(targetName) && map.get(targetName) instanceof ArrayList) {
                subRecordList = (ArrayList) map.get(targetName);
            } else {
                subRecordList = new ArrayList();
                map.put(targetName, subRecordList);
            }
            subRecordList.add(subRecordMap);
            // add subrecords
            addSubRecords(subRecords[i], subRecordMap, locale, level, readableColumns, messages, dateFields, replacementFields, filter);
        }

    } 

    public static void addSubRecords(Record record, HashMap map, Locale locale, boolean readableColumns,
            IngridResourceBundle messages, List dateFields, HashMap replacementFields) {
        addSubRecords(record, map, locale, 0, readableColumns, messages, dateFields, replacementFields, null);
    }
    
    public static void addSubRecords(Record record, HashMap map, Locale locale, boolean readableColumns,
            IngridResourceBundle messages, List dateFields, HashMap replacementFields, HashMap filter) {
        addSubRecords(record, map, locale, 0, readableColumns, messages, dateFields, replacementFields, filter);
    }
    
    /**
     * Iterate over the hashmap ArrayList structure and return the requested field.
     * 
     * @param hash
     * @param fieldName
     * @return
     */
    public static Object getFieldFromHashTree(HashMap hash, String fieldName) {
        Iterator it = hash.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.equals(fieldName)) {
                return hash.get(key);
            }
            if (hash.get(key) instanceof ArrayList) {
                ArrayList array = (ArrayList) hash.get(key);
                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i) instanceof HashMap) {
                        Object val = getFieldFromHashTree((HashMap) array.get(i), fieldName);
                        if (val != null) {
                            return val;
                        }
                    }
                }
            }

        }
        return null;
    }

    /**
     * Iterate over the hashmap ArrayList structure and set the requested field.
     * 
     * @param hash
     * @param fieldName
     * @return
     */
    public static boolean setFieldFromHashTree(HashMap hash, String fieldName, String value) {
        Iterator it = hash.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.equals(fieldName)) {
                hash.put(key, value);
                return true;
            }
            if (hash.get(key) instanceof ArrayList) {
                ArrayList array = (ArrayList) hash.get(key);
                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i) instanceof HashMap) {
                        if (setFieldFromHashTree((HashMap) array.get(i), fieldName, value)) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }
    
    
	
}
