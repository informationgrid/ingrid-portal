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
package de.ingrid.portal.search;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SavedSearchQueries extends ArrayList {

    /**
     * TODO: Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 6034638555827734271L;

    /**
     * Add a saved query to the object.
     * 
     * @param queryStr
     * @param urlStr
     * @param datasource
     */
    public void add(String queryStr, String urlStr, String datasource, String title) {
        HashMap map = new HashMap(4);
        map.put("query", queryStr);
        map.put("url", urlStr);
        map.put("ds", datasource);
        map.put("title", title);
        if (this.contains(map)) {
            int atIndex = this.indexOf(map);
            if (atIndex > 0) {
                this.remove(atIndex);
                
                this.add(0, map);
            }
            return;
        } else {
            this.add(0, map);
        }
    }
    
}
