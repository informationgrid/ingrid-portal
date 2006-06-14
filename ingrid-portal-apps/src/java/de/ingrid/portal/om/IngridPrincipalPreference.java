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
public class IngridPrincipalPreference {

    Long id;
    Long principalId;
    String prefName;
    String prefValue;
    Date modifiedDate;

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
     * @return Returns the modifiedDate.
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }
    /**
     * @param modifiedDate The modifiedDate to set.
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    /**
     * @return Returns the prefName.
     */
    public String getPrefName() {
        return prefName;
    }
    /**
     * @param prefName The prefName to set.
     */
    public void setPrefName(String prefName) {
        this.prefName = prefName;
    }
    /**
     * @return Returns the prefValue.
     */
    public String getPrefValue() {
        return prefValue;
    }
    /**
     * @param prefValue The prefValue to set.
     */
    public void setPrefValue(String prefValue) {
        this.prefValue = prefValue;
    }
    /**
     * @return Returns the principalId.
     */
    public Long getPrincipalId() {
        return principalId;
    }
    /**
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }
    
}
