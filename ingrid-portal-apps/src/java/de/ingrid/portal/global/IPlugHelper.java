/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.ingrid.utils.PlugDescription;

/**
 * Helper class dealing with all aspects of iPlugs (PlugDescription).
 *
 * @author joachim@wemove.com
 */
public class IPlugHelper {

    /**
     * Return true if the given iPlug has a specific data type.
     * 
     * @param iPlug The PlugDescription to work on.
     * @param dataType The data type to search for
     * @return True if the iPlug has the data type, false if not.
     */
    public static boolean hasDataType(PlugDescription iPlug, String dataType) {
        String[] dataTypes = iPlug.getDataTypes();
        for (int i=0; i<dataTypes.length; i++) {
            if (dataTypes[i].equalsIgnoreCase(dataType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns only iPlugs of given type from given iPlugs-> sorted by given Comparator (pass null if no sort)  
     * 
     * @param iPlugs iPlugs of different types which will be filtered
     * @param plugTypes The DataTypes to look for
     * @param plugComparator comparator for sorting, pass null if no sorting !
     * @return Array containing filtered iPlugs or empty
     */
    public static PlugDescription[] filterIPlugs(PlugDescription[] iPlugs, String[] plugTypes, Comparator plugComparator) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < iPlugs.length; i++) {
            PlugDescription plug = iPlugs[i];
            for (int j = 0; j < plugTypes.length; j++) {
            	if (hasDataType(plug, plugTypes[j])) {
            		result.add(plug);
            		break;
                }
            }
        }

        // sort ?
        if (plugComparator != null) {
            Collections.sort(result, plugComparator);        	
        }

        return (PlugDescription[]) result.toArray(new PlugDescription[result.size()]);
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

                return plugSortCriteria[0].compareTo(plugSortCriteria[1]);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
