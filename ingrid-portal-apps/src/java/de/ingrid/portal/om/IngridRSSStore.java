package de.ingrid.portal.om;

import java.util.Date;

public class IngridRSSStore {
    
    private String link;
    private String author;
    private String categories;
    private String copyright;
    private String description;
    private String language;
    private String title;
    private Date publishedDate;

    
    public String getId() {
        return this.link;
    }

    public void setId(String id) {
        this.link = id;
    }
    
    
    /**
     * @return Returns the author.
     */
    public String getAuthor() {
        return author;
    }
    /**
     * @param author The author to set.
     */
    public void setAuthor(String author) {
        this.author = author;
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
     * @return Returns the copyright.
     */
    public String getCopyright() {
        return copyright;
    }
    /**
     * @param copyright The copyright to set.
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
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
     * @return Returns the link.
     */
    public String getLink() {
        return link;
    }
    /**
     * @param link The link to set.
     */
    public void setLink(String link) {
        this.link = link;
    }
    /**
     * @return Returns the publishedDate.
     */
    public Date getPublishedDate() {
        return publishedDate;
    }
    /**
     * @param publishedDate The publishedDate to set.
     */
    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }
    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
