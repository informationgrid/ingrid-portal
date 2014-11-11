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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridCMS {

    private Long id;

    private String itemKey;

    private String itemDescription;

    private Date itemChanged;

    private String itemChangedBy;

    private Set localizedItems = new HashSet();
    
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
     * @return Returns the localizedItems.
     */
    public Set getLocalizedItems() {
        return localizedItems;
    }

    /**
     * @param localizedItems The localizedItems to set.
     */
    public void setLocalizedItems(Set localizedItems) {
        this.localizedItems = localizedItems;
    }

    /**
     * @return Returns the itemDescription.
     */
    public String getItemDescription() {
        return itemDescription;
    }

    /**
     * @param itemDescription The itemDescription to set.
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    
    /**
     * Returns a localized Item for the language lang.
     * 
     * @param lang The language.
     * @return The localized IngridCMSItem.
     */
    public IngridCMSItem getLocalizedEntry(String lang) {
        
        Set entries = this.getLocalizedItems();
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            IngridCMSItem c = (IngridCMSItem)it.next();
            if (c.getItemLang().equals(lang)) {
                return c;
            }
        }
        return null;
    }


}
