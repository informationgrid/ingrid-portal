package de.ingrid.mdek.syslists;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.model.CodeList;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.utils.udk.UtilsUDKCodeLists;
import de.ingrid.utils.xml.XMLSerializer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/application-context.xml"})
public class dbSyslistTransferTestLocal {
    
    @Autowired
    private CodeListService codeListService;

    private HashMap codeListsFromFile;
    
    @Before
    public void setUp() throws Exception {
    }
    
    /**
     * It's important that two persistent objects are configured via spring
     * and that the DB-one is used as the default (for reading)!
     */
    @Test
    public void readFromDbAndWriteToFile() {
        initializeAndWait();
        
        // read codelists from default source (db)
        List<CodeList> codelists = codeListService.getCodeLists();

        removeUnwantedSyslists(codelists);
        
        addAdditionalValues(codelists);
        
        
        // write codelists to xml file 
        codeListService.persistTo(0);  
    }

    private void removeUnwantedSyslists(List<CodeList> codelists) {
        // ignore the following codelists (they are only local and might not 
        // contain all languages!
        List<String> removeCodeLists = new ArrayList<String>(Arrays.asList( 
                new String[] {"1100", "1350", "1370", "3535", "3555", "6010", "6020"}));
        
        List<CodeList> codelistsToRemove = new ArrayList<CodeList>();
        
        for (CodeList codelist : codelists) {
            if (removeCodeLists.contains(codelist.getId()))
                codelistsToRemove.add(codelist);
        }
        codelists.removeAll(codelistsToRemove);
    }

    private void initializeAndWait() {
     // wait until MDEK is initialized correctly!
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // load xml file which contains information that need to be merged
        loadCodeListFile();
        
        
    }

    private void addAdditionalValues(List<CodeList> codelists) {
        String extraLangValue = "";
        for (CodeList codelist : codelists) {
            for (CodeListEntry cle : codelist.getEntries()) {
                
                // add iso and 0815-languages!
                extraLangValue = getCodeListEntryName(Long.valueOf(codelist.getId()), Long.valueOf(cle.getId()), 150150150L);
                if (extraLangValue != null && !extraLangValue.isEmpty()) cle.setLocalisedEntry(UtilsUDKCodeLists.LANG_ID_ISO_ENTRY, extraLangValue);
                extraLangValue = getCodeListEntryName(Long.valueOf(codelist.getId()), Long.valueOf(cle.getId()), 8150815L);
                if (extraLangValue != null && !extraLangValue.isEmpty()) cle.setLocalisedEntry(UtilsUDKCodeLists.LANG_ID_INGRID_QUERY_VALUE, extraLangValue);
            }
        }
        
        // add codelist 524, which is only in xml-file, but used for extra IDF-mapping
        codelists.add(migrateCodelist524());
    }
    
    private CodeList migrateCodelist524() {
        List<de.ingrid.utils.udk.CodeListEntry> list524En = getCodeList(524L, 123L);
        List<de.ingrid.utils.udk.CodeListEntry> list524De = getCodeList(524L, 150L);
        List<de.ingrid.utils.udk.CodeListEntry> list524Iso = getCodeList(524L, 150150150L);
        List<CodeListEntry> entries = new ArrayList<CodeListEntry>();
        for (int i=0; i<list524De.size(); i++) {
            CodeListEntry cle = new CodeListEntry();
            cle.setId(list524De.get(i).getDomainId().toString());
            if (list524De.size() > 0)
                cle.setLocalisedEntry("de", list524De.get(i).getValue());
            if (list524En.size() > 0)
                cle.setLocalisedEntry("en", list524En.get(i).getValue());
            if (list524Iso.size() > 0)
                cle.setLocalisedEntry("iso", list524Iso.get(i).getValue());
            entries.add(cle);
        }
        CodeList cl = new CodeList();
        cl.setId("524");
        cl.setName("Restriction");
        cl.setEntries(entries);
        return cl;
    }
    
    public void setCodeListService(CodeListService codeListService) {
        this.codeListService = codeListService;
    }

    public CodeListService getCodeListService() {
        return codeListService;
    }

    private void loadCodeListFile() {
        try {
            // Create the SessionFactory
            InputStream resourceAsStream = UtilsUDKCodeLists.class.getResourceAsStream("udk_codelists_serialized.xml");
            if (resourceAsStream == null) {
                resourceAsStream = UtilsUDKCodeLists.class.getClassLoader().getResourceAsStream(
                        "udk_codelists_serialized.xml");
            }
            XMLSerializer serializer = new XMLSerializer();
            codeListsFromFile = (HashMap) serializer.deSerialize(resourceAsStream);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    
    private String getCodeListEntryName(long lstId, long entryId, long lang) {
        String retValue = "";
        try {
            retValue = (String) ((Map)((Map)codeListsFromFile.get(lstId)).get(entryId)).get(lang);
            if (retValue != null) {
                retValue = retValue.trim();
            }
        } catch (NullPointerException e) {
        }
        
        return retValue;
    }
    
    private List<de.ingrid.utils.udk.CodeListEntry> getCodeList(long lstId, long lang) {
        List<de.ingrid.utils.udk.CodeListEntry> result = new ArrayList<de.ingrid.utils.udk.CodeListEntry>();
        HashMap domain = (HashMap) codeListsFromFile.get(lstId);
        for (Iterator it = domain.entrySet().iterator(); it.hasNext();) {
            Map.Entry domainEntry = (Map.Entry) it.next();
            String domainEntryValue = (String) ((HashMap) domainEntry.getValue()).get(lang);
            String domainEntryKey = domainEntry.getKey().toString();
            result.add(new de.ingrid.utils.udk.CodeListEntry(domainEntryValue, lstId, Long.valueOf(domainEntryKey), lang));
        }
        return result;
    }
}
