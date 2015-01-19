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

import java.util.Date;

public class IngridRSSSource {

    
    private Long id;
    private String provider;
    private String description;
    private String language;
    private String url;
    private String categories;
    
    private Integer numLastCount;
    private Date lastUpdate;
    //private Date lastMessageUpdate;
    private String error;

    
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return language;
    }
    /**
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        this.language = language;
    }
    /**
     * @return Returns the provider.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider The provider to set.
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
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
     * @return Returns the categories.
     */
    public String getCategories() {
        return categories;
    }
    /**
     * @param categories The categories to set.
     */
    public void setCategories(String categories) {
        this.categories = categories;
    }    
    
    /**
     * 
     * @return
     */
    public Integer getNumLastCount() {
    	return numLastCount;
    }
    
    /**
     * 
     * @param value
     */
    public void setNumLastCount(Integer value) {
    	this.numLastCount = value;
    }
    
    /**
     * 
     * @return
     */
    public Date getLastUpdate() {
    	return lastUpdate;
    }
    
    /**
     * 
     * @param date
     */
    public void setLastUpdate(Date date) {
    	this.lastUpdate = date;
    }
    
    /**
     * 
     * @return
     */
    //public Date getLastMessageUpdate() {
    //	return lastMessageUpdate;
    //}
    
    /**
     * 
     * @param date
     */
    //public void setLastMessageUpdate(Date date) {
    //	this.lastMessageUpdate = date;
    //}
    
    /**
     * 
     * @return
     */
    public String getError() {
    	return error;
    }
    
    /**
     * 
     * @param error
     */
    public void setError(String error) {
    	this.error = error;
    }


}
