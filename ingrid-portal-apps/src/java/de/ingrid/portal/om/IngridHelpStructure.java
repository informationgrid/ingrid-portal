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
