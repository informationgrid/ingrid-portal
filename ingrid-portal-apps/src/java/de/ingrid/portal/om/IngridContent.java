/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.om;

import java.util.Date;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridContent {

    private Long id;

    private String itemKey;

    private String itemLang;

    private String itemTitle;

    private String itemValue;

    private Date itemChanged;

    private String itemChangedBy;

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the itemChanged.
     */
    public Date getItemChanged() {
        return itemChanged;
    }

    /**
     * @param itemChanged
     *            The itemChanged to set.
     */
    public void setItemChanged(Date itemChanged) {
        this.itemChanged = itemChanged;
    }

    /**
     * @return Returns the itemChangedBy.
     */
    public String getItemChangedBy() {
        return itemChangedBy;
    }

    /**
     * @param itemChangedBy
     *            The itemChangedBy to set.
     */
    public void setItemChangedBy(String itemChangedBy) {
        this.itemChangedBy = itemChangedBy;
    }

    /**
     * @return Returns the itemKey.
     */
    public String getItemKey() {
        return itemKey;
    }

    /**
     * @param itemKey
     *            The itemKey to set.
     */
    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    /**
     * @return Returns the itemLang.
     */
    public String getItemLang() {
        return itemLang;
    }

    /**
     * @param itemLang
     *            The itemLang to set.
     */
    public void setItemLang(String itemLang) {
        this.itemLang = itemLang;
    }

    /**
     * @return Returns the itemTitle.
     */
    public String getItemTitle() {
        return itemTitle;
    }

    /**
     * @param itemTitle
     *            The itemTitle to set.
     */
    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    /**
     * @return Returns the itemValue.
     */
    public String getItemValue() {
        return itemValue;
    }

    /**
     * @param itemValue
     *            The itemValue to set.
     */
    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

}
