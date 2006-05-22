/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.HashMap;
import java.util.List;

/**
 * Helper class for velocity.
 *
 * @author joachim@wemove.com
 */
public class UtilsVelocity {

    /**
     * Checks an object for content. It checks the following Types:
     * 
     * String, List, HashMap
     * 
     * If obj does not contain one of the types AND is NOT NULL, false is returned.
     * 
     * @param obj The object.
     * @return true if obj has content, false of not.
     */
    public static Boolean hasContent(Object obj) {
        if (obj == null) {
            return new Boolean(false);
        }
        
        if (obj instanceof String) {
            if (((String)obj).length() > 0) {
                return new Boolean(true);
            }
        }
        
        if (obj instanceof List) {
            if (!((List)obj).isEmpty()) {
                return new Boolean(true);
            }
        }

        if (obj instanceof HashMap) {
            if (!((HashMap)obj).isEmpty()) {
                return new Boolean(true);
            }
        }
        
        return new Boolean(false);
        
    }
    
}
