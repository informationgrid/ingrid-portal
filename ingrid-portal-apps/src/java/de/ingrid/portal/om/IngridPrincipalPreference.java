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
public class IngridPrincipalPreference {

    Long id;
    String principalName;
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
     * @return Returns the principalName.
     */
    public String getPrincipalName() {
        return principalName;
    }
    /**
     * @param principalName The principalName to set.
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }
    
}
