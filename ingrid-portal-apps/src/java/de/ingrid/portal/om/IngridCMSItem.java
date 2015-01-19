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
package de.ingrid.portal.om;

import java.util.Date;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridCMSItem {

    private Long id;

    private Long fkIngridCmsId;

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

    /**
     * @return Returns the fkIngridCmsId.
     */
    public Long getFkIngridCmsId() {
        return fkIngridCmsId;
    }

    /**
     * @param fkIngridCmsId
     *            The fkIngridCmsId to set.
     */
    public void setFkIngridCmsId(Long fkIngridCmsId) {
        this.fkIngridCmsId = fkIngridCmsId;
    }

    /**
     * @return Returns the itemChanged.
     */
    public Date getItemChanged() {
        return itemChanged;
    }

    /**
     * @param itemChanged The itemChanged to set.
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
     * @param itemChangedBy The itemChangedBy to set.
     */
    public void setItemChangedBy(String itemChangedBy) {
        this.itemChangedBy = itemChangedBy;
    }

    /**
     * @return Returns the itemTitle.
     */
    public String getItemTitle() {
        return itemTitle;
    }

    /**
     * @param itemTitle The itemTitle to set.
     */
    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

}
