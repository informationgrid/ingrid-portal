/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
     * Return true if the given iPlug has a specific partner.
     * 
     * @param iPlug The PlugDescription to work on.
     * @param partnerId The partner to search for (pass id)
     * @return True if the iPlug has the partner, false if not.
     */
    public static boolean hasPartner(PlugDescription iPlug, String partnerId) {
        String[] partners = iPlug.getPartners();
        for (int i=0; i<partners.length; i++) {
            if (partners[i].equals(partnerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns only iPlugs of given type from given iPlugs  
     * 
     * @param iPlugs iPlugs of different types which will be filtered
     * @param plugTypes The DataTypes to look for
     * @return Array containing filtered iPlugs or empty
     */
    public static PlugDescription[] filterIPlugsByType(PlugDescription[] iPlugs, String[] plugTypes) {
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

        return (PlugDescription[]) result.toArray(new PlugDescription[result.size()]);
    }

    /**
     * Returns only iPlugs of given Partner from given iPlugs  
     * 
     * @param iPlugs iPlugs of different partners which will be filtered
     * @param partners The partners to look for
     * @return Array containing filtered iPlugs or empty
     */
    public static PlugDescription[] filterIPlugsByPartner(PlugDescription[] iPlugs, ArrayList partners) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < iPlugs.length; i++) {
            PlugDescription plug = iPlugs[i];
            for (int j = 0; j < partners.size(); j++) {
            	if (hasPartner(plug, (String)partners.get(j))) {
            		result.add(plug);
            		break;
                }
            }
        }

        return (PlugDescription[]) result.toArray(new PlugDescription[result.size()]);
    }

    public static PlugDescription[] sortPlugs(PlugDescription[] iPlugs, Comparator plugComparator) {
        List plugList = Arrays.asList(iPlugs);
        Collections.sort(plugList, plugComparator);

        return (PlugDescription[]) plugList.toArray(new PlugDescription[plugList.size()]);
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
