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
package de.ingrid.portal.om;

public abstract class IngridFormToQuery {

    private Long id;

    private String formValue;

    private String queryValue;

    private int sortkey;

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
     * @return Returns the formValue.
     */
    public String getFormValue() {
        return formValue;
    }

    /**
     * @param formValue The formValue to set.
     */
    public void setFormValue(String formValue) {
        this.formValue = formValue;
    }

    /**
     * @return Returns the queryValue.
     */
    public String getQueryValue() {
        return queryValue;
    }

    /**
     * @param queryValue The queryValue to set.
     */
    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
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
