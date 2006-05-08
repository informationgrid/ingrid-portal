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
public class QueryHistory extends ArrayList {

    private static final long serialVersionUID = 3403072521739947975L;

    public void add(String queryStr, String urlStr, String datasource) {
        HashMap map = new HashMap(2);
        map.put("query", queryStr);
        map.put("url", urlStr);
        map.put("ds", datasource);
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
