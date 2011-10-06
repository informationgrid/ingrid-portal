/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.utils.udk.UtilsLanguageCodelist;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridSysCodeList {

	private final Logger log = LoggerFactory.getLogger(IngridSysCodeList.class);

    Locale locale;

    /**
     * @param locale
     */
    public IngridSysCodeList(Locale locale) {
        super();
        this.locale = locale;
    }
    
    public String getName(long codeListId, long domainId) {
    	
        long langId;
        if (locale.getLanguage().equals(new Locale("de", "", "").getLanguage())) {
            langId = UtilsLanguageCodelist.getCodeFromShortcut("de");
        } else if (locale.getLanguage().equals(new Locale("en", "", "").getLanguage())) {
            langId = UtilsLanguageCodelist.getCodeFromShortcut("en");
        } else {
        	// different language, we use english !
        	if (log.isDebugEnabled()) {
        		log.debug("Unknown language for syslists ('" + locale.getLanguage() + "'), we use english syslist");
        	}
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
    
    public String getNameByCodeListValue(String codeListId, String domainValue) {
    	
    	String id = UtilsUDKCodeLists.getCodeListDomainId(new Long (codeListId), domainValue, new Long(UtilsLanguageCodelist.getCodeFromShortcut("de")));
    	
    	if(id == null){
    		id = UtilsUDKCodeLists.getCodeListDomainId(new Long (codeListId), domainValue, new Long(UtilsLanguageCodelist.getCodeFromShortcut("en")));
    	}
    	
    	if(id == null){
    		id = UtilsUDKCodeLists.getIgcIdFromIsoCodeListEntry(new Long (codeListId), domainValue);
    	}
    	return getName(codeListId, id);
    }
}
