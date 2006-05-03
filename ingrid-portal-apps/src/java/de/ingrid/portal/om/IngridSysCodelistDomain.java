/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.om;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridSysCodelistDomain {

    private IngridSysCodelistDomainId id;
    private String name;
    private String description;
    private Integer defDomain;

    /**
     * @return Returns the defDomain.
     */
    public Integer getDefDomain() {
        return defDomain;
    }
    /**
     * @param defDomain The defDomain to set.
     */
    public void setDefDomain(Integer defDomain) {
        this.defDomain = defDomain;
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
     * @return Returns the id.
     */
    public IngridSysCodelistDomainId getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(IngridSysCodelistDomainId id) {
        this.id = id;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
