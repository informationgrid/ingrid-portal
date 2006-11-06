/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.Locale;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridSysCodeList {

    Locale locale;

    /**
     * @param locale
     */
    public IngridSysCodeList(Locale locale) {
        super();
        this.locale = locale;
    }
    
    public String getName(long codeListId, long domainId) {
        long langId = 121;
        if (locale.getLanguage().equals(new Locale("en", "", "").getLanguage())) {
            langId = 94;
        }
        return UtilsDB.getCodeListEntryName(codeListId, domainId, langId);
    }
    
    public String getName(String codeListId, String domainId) {
        try {
            return getName(Long.parseLong(codeListId), Long.parseLong(domainId));
        } catch (NumberFormatException e) {
            if (domainId != null) {
                return domainId;
            } else {
                return "";
            }
        }
    }
    
}
