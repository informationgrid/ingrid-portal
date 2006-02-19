/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.global;

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
    
}
