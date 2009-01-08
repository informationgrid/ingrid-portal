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
