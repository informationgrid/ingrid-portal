/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
        boolean hasContent = false;
        if (obj != null) {
            if (obj instanceof String && ((String)obj).length() > 0 && !((String)obj).equals("null")) {
                hasContent = true;
            }
            
            if (obj instanceof List && !((List)obj).isEmpty()) {
                hasContent = true;
            }
    
            if (obj instanceof HashMap && !((HashMap)obj).isEmpty()) {
                hasContent = true;
            }
    
            if (obj instanceof Object[] && ((Object[])obj).length > 0) {
                hasContent = true;
            }
        }

        return hasContent;
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
