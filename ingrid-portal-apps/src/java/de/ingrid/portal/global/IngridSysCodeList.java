/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.model.CodeList;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.utils.tool.SpringUtil;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridSysCodeList {

	private final Logger log = LoggerFactory.getLogger(IngridSysCodeList.class);

    Locale locale;
    
    private static SpringUtil springUtil;

    /**
     * @param locale
     */
    public IngridSysCodeList(Locale locale) {
        super();
        this.locale = locale;
        if (springUtil == null)
            springUtil = new SpringUtil("../spring.xml");
    }
    
    public String getName(long codeListId, long domainId) {
    	
        String langId = "en";
        if (locale.getLanguage().equals(new Locale("de", "", "").getLanguage())) {
            langId = "de";
        } else if (!locale.getLanguage().equals(new Locale("en", "", "").getLanguage())) {
        	// different language, we use english !
        	if (log.isDebugEnabled()) {
        		log.debug("Unknown language for syslists ('" + locale.getLanguage() + "'), we use english syslist");
        	}
        }
        
        CodeListService clService = springUtil.getBean("codeListService", CodeListService.class);
        return clService.getCodeListValue(String.valueOf(codeListId), String.valueOf(domainId), langId);
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
        CodeListService clService = springUtil.getBean("codeListService", CodeListService.class);
        
        CodeList cl = clService.getCodeList(codeListId);
        for (CodeListEntry entry : cl.getEntries()) {
            Map<String, String> locals = entry.getLocalisations();
            for (String key : locals.keySet()) {
                if (locals.get(key).equalsIgnoreCase(domainValue)) {
                    return getName(codeListId, entry.getId()); 
                }
            }
        }
    	
    	return "";
    }
}
