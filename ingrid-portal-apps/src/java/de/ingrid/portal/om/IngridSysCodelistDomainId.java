/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.om;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridSysCodelistDomainId implements Serializable {

    private static final long serialVersionUID = 939633171890922199L;

    private Long codelistId;
    private Long domainId;
    private Long langId;

    /**
     * @return Returns the codelistId.
     */
    public Long getCodelistId() {
        return codelistId;
    }
    /**
     * @param codelistId The codelistId to set.
     */
    public void setCodelistId(Long codelistId) {
        this.codelistId = codelistId;
    }
    /**
     * @return Returns the domainId.
     */
    public Long getDomainId() {
        return domainId;
    }
    /**
     * @param domainId The domainId to set.
     */
    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
    /**
     * @return Returns the langId.
     */
    public Long getLangId() {
        return langId;
    }
    /**
     * @param langId The langId to set.
     */
    public void setLangId(Long langId) {
        this.langId = langId;
    }

    public boolean equals(Object other) {
        if ( !(other instanceof IngridSysCodelistDomainId) ) return false;
        IngridSysCodelistDomainId castOther = (IngridSysCodelistDomainId) other;
        return new EqualsBuilder()
            .append(this.getCodelistId(), castOther.getCodelistId())
            .append(this.getDomainId(), castOther.getDomainId())
            .append(this.getLangId(), castOther.getLangId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCodelistId())
            .append(getDomainId())
            .append(getLangId())
            .toHashCode();
    }    
    
}
