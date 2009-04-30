/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.Locale;

import de.ingrid.utils.udk.UtilsLanguageCodelist;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

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
    	
        long langId = UtilsLanguageCodelist.getCodeFromShortcut("de");
        if (locale.getLanguage().equals(new Locale("en", "", "").getLanguage())) {
            langId = UtilsLanguageCodelist.getCodeFromShortcut("en");
        }
        return UtilsUDKCodeLists.getCodeListEntryName(new Long(codeListId), new Long(domainId), new Long(langId));
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
