/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.beans.object;

/**
 * @author André Wallat
 *
 */
public class SpatialReferenceSystemBean {
    /**
     * The id is the entryId of the corresponding codelist. If it's an unknown
     * SRS, then the id should be -1
     */
    private Integer id;
    
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer itemId) {
        this.id = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
