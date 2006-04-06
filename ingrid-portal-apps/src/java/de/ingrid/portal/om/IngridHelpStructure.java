/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.om;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridHelpStructure {

    private Long id;
    private Long parentId;
    private String helpKey;
    private int sortkey;

    
    /**
     * @return Returns the helpKey.
     */
    public String getHelpKey() {
        return helpKey;
    }
    /**
     * @param helpKey The helpKey to set.
     */
    public void setHelpKey(String helpKey) {
        this.helpKey = helpKey;
    }
    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return Returns the parentId.
     */
    public Long getParentId() {
        return parentId;
    }
    /**
     * @param parentId The parentId to set.
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    /**
     * @return Returns the sortkey.
     */
    public int getSortkey() {
        return sortkey;
    }
    /**
     * @param sortkey The sortkey to set.
     */
    public void setSortkey(int sortkey) {
        this.sortkey = sortkey;
    }
    
}
