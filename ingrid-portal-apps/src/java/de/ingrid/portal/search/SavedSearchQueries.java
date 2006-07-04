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
