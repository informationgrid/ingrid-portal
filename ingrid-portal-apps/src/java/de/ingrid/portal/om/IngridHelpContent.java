/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.om;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridHelpContent {

    private Long id;
    private Long helpId;
    private String language;
    private String header;
    private String content;

    /**
     * @return Returns the content.
     */
    public String getContent() {
        return content;
    }
    /**
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * @return Returns the header.
     */
    public String getHeader() {
        return header;
    }
    /**
     * @param header The header to set.
     */
    public void setHeader(String header) {
        this.header = header;
    }
    /**
     * @return Returns the helpId.
     */
    public Long getHelpId() {
        return helpId;
    }
    /**
     * @param helpId The helpId to set.
     */
    public void setHelpId(Long helpId) {
        this.helpId = helpId;
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
    
}
