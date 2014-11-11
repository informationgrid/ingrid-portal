/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
            	if (!((String)obj).equals("null")) {
                    return new Boolean(true);            		
            	}
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

        if (obj instanceof Object[]) {
            if (((Object[])obj).length > 0) {
                return new Boolean(true);
            }
        }

        return new Boolean(false);
    }
    
    public static String urlencode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }
    
    public static String htmlescapeAll(String str) {
    	return UtilsString.htmlescapeAll(str);
    }
    
    
}
