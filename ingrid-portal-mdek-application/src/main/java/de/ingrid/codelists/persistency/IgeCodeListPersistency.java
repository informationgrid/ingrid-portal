/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.codelists.persistency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.codelists.model.CodeList;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.MdekUtils;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.repository.IJobRepository;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

@Service
public class IgeCodeListPersistency implements ICodeListPersistency {

    private static final Logger log = Logger.getLogger(IgeCodeListPersistency.class);

    // injected by Spring
    @Autowired
    private ConnectionFacade connectionFacade;

    /**
     *  Nothing to do here since old interface is used to get syslists. Unless
     *  we use it to migrate the codelists from the database to an XML file,
     *  which is useful for the introduction of the central repository!
     */
    @Override
    public List<CodeList> read() {

        // mapping of codelist names
        Map<Integer, String> syslistMap = new HashMap<Integer, String> ()
            {{
                put(100,"Raumbezugssystem");    put(101,"Vertikaldatum");   put(102,"Maßeinheit");
                put(502,"Zeitbezug des Datensatzes - Typ"); put(505,"Adresstyp");
                put(510,"ISO Liste Text-Kodierung");    put(515,"Vektorformat - Geometrietyp");
                put(517,"ISO Liste Thesaurus Typ"); put(518,"Periodizität");    put(520,"Medienoption - Medium");
                put(523,"Zeitbezug - Status");  put(525,"Objektklasse 1 - Datensatz/Datenserie");
                put(526,"Objektklasse 1 - Digitale Repräsentation");    put(527,"ISO-Themenkategorie");
                put(528,"Vektorformat -Topologieinformation");  put(1100,"Freier Raumbezug");   put(1200,"Zeitbezug");
                put(1230,"Zeitbezug - Alle...");    put(1320,"Datenformat - Name"); put(1350,"Weitere Rechtliche Grundlagen");
                put(1370,"XML Export Kriterium");   put(1400,"Verschlagwortung - Umweltthemen - Kategorien");
                put(1410,"Verschlagwortung - Umweltthemen - Themen");   put(2000,"Objekt / Url-Verweis - Typ");
                put(2010,"Adresstyp für spezielle Objektklassen");  put(2251,"Url-Verweis - Intranet/Internet");
                put(3385,"Objektklasse 2 - Dokumenttyp");   put(3535,"Objektklasse 1 / 5 - Schlüssel-/Objektartenkatalog - Titel");
                put(3555,"Objektklasse 1 - Symbolkatalog - Titel"); put(3571,"Veröffentlichungsbreite");
                put(4300,"Adresse - Anrede");   put(4305,"Adresse - Titel");    put(4430,"Adresse - Kommunikation - Art");
                put(5100,"Objektklasse 3 - Art des Dienstes");    put(5105,"Objektklasse 3 - Name der Operation - CWS");
                put(5110,"Objektklasse 3 - Name der Operation - WMS");    put(5120,"Objektklasse 3 - Name der Operation - WFS");
                put(5130,"Objektklasse 3 - Name der Operation - WCTS");    put(5180,"Objektklasse 3 - Operation - Unterstützte Plattformen");
                put(5200,"Objektklasse 3 - Klassifikation des Dienstes");    put(5300,"Objektklasse 6 - Art des Dienstes");
                put(6000,"Konformität - Grad der Konformität"); put(6005,"Konformität - Spezifikation der Konformität");
                put(6010,"Verfügbarkeit - Zugriffsbeschränkungen");
                put(6100,"INSPIRE-Themen"); put(6200,"ISO Liste der Länder"); put(6250,"Verwaltungsgebiete");   put(6300,"INSPIRE-Datenformat");
                put(7109,"Datenqualität - Datenüberschuss - Art der Messung");
                put(7112,"Datenqualität - Konzeptionelle Konsistenz - Art der Messung");
                put(7113,"Datenqualität - Konsistenz des Wertebereichs - Art der Messung");
                put(7114,"Datenqualität - Formatkonsistenz - Art der Messung");
                put(7115,"Datenqualität - Topologische Konsistenz - Art der Messung");
                put(7120,"Datenqualität - Zeitliche Genauigkeit - Art der Messung");
                put(7125,"Datenqualität - Korrektheit der thematischen Klassifizierung - Art der Messung");
                put(7126,"Datenqualität - Genauigkeit nicht-quantitativer Attribute - Art der Messung");
                put(7127,"Datenqualität - Genauigkeit quantitativer Attribute - Art der Messung");
                put(7128,"Datenqualität - Relative Positionsgenauigkeit - Art der Messung");
                put(99999999,"Sprache der Ressource / des Metadatensatzes");
            }};



        IMdekClientCaller caller = connectionFacade.getMdekClientCaller();
        List<String> iplugList = caller.getRegisteredIPlugs();
        if (iplugList.isEmpty()) {
            throw new RuntimeException("Codelist-Update aborted! No IGE-IPlugs connected!");
        }

        // force to get initial codelist if one connected iPlug has never fetched any codelists
        if (getLowestTimestamp(iplugList) == -1) {
            return new ArrayList<>();
        }

        Integer[] codelistsIds = getSyslistIDs(iplugList.get(0));

        Map<Integer, List<String[]>> listDe = getSyslists(iplugList.get(0), codelistsIds, "de");
        Map<Integer, List<String[]>> listEn = getSyslists(iplugList.get(0), codelistsIds, "en");
        Map<Integer, List<String[]>> listIso = getSyslists(iplugList.get(0), codelistsIds, UtilsUDKCodeLists.LANG_ID_ISO_ENTRY);
        Map<Integer, List<String[]>> listReq = getSyslists(iplugList.get(0), codelistsIds, UtilsUDKCodeLists.LANG_ID_INGRID_QUERY_VALUE);

        List<CodeList> codelists = new ArrayList<>();
        for (Integer id : codelistsIds) {
            CodeList cl = new CodeList();
            cl.setId(String.valueOf(id));

            cl.setName(syslistMap.get(id));

            List<String[]> entriesDe = listDe.get(id);
            List<String[]> entriesEn = listEn.get(id);
            List<String[]> entriesIso = listIso.get(id);
            List<String[]> entriesReq = listReq.get(id);
            Map<Integer, Map<String, String[]>> allEntries = mergeLists(entriesDe, entriesEn, entriesIso, entriesReq);
            List<Integer> sortedList = new ArrayList<>();
            sortedList.addAll(allEntries.keySet());
            Collections.sort(sortedList);

            List<CodeListEntry> entries = new ArrayList<>();
            String defaultEntry = "";
            for (Integer key : sortedList) {
                Map<String, String[]> entrySet = allEntries.get(key);
                CodeListEntry cle = new CodeListEntry();
                cle.setId(String.valueOf(key));
                if (entrySet.get("de") != null) {
                    cle.setLocalisedEntry("de", entrySet.get("de")[0]);
                    cle.setData(String.valueOf(entrySet.get("de")[2]));
                }
                if (entrySet.get("en") != null) {
                    cle.setLocalisedEntry("en", entrySet.get("en")[0]);
                    cle.setData(String.valueOf(entrySet.get("en")[2]));
                }
                if (entrySet.get("iso") != null) {
                    cle.setLocalisedEntry(UtilsUDKCodeLists.LANG_ID_ISO_ENTRY, entrySet.get("iso")[0]);
                }
                if (entrySet.get("req") != null) {
                    cle.setLocalisedEntry(UtilsUDKCodeLists.LANG_ID_INGRID_QUERY_VALUE, entrySet.get("req")[0]);
                }

                if (entrySet.get("de") != null && MdekUtils.YES.equals(entrySet.get("de")[1]))
                    defaultEntry = cle.getId();
                entries.add(cle);
            }
            cl.setEntries(entries);

            cl.setDefaultEntry(defaultEntry);
            codelists.add(cl);
        }

        // find lowest timestamp to get all necessary codelists for an eventual update
        // add this timestamp to one codelist at least to be identified and used for the
        // update procedure
        codelists.get(0).setLastModified(getLowestTimestamp(iplugList));

        return codelists;
    }

    private Map<Integer, Map<String, String[]>> mergeLists(List<String[]> de, List<String[]> en, List<String[]> iso, List<String[]> req) {
        Map<Integer, Map<String, String[]>> result = new HashMap<>();

        Map<String, List<String[]>> allLists = new HashMap<>();
        allLists.put("de", de);allLists.put("en", en);allLists.put("iso", iso);allLists.put("req", req);
        for (String key : allLists.keySet()) {
            for (String[] value : allLists.get(key)) {
                if (result.get(Integer.valueOf(value[1])) == null)
                    result.put(Integer.valueOf(value[1]), new HashMap<String, String[]>());
                result.get(Integer.valueOf(value[1])).put(key, new String[]{value[0],value[2],value[3]});
            }
        }

        return result;
    }


    private long getLowestTimestamp(List<String> iplugList) {
        Long lowestTimestamp = null;
        for (String iplug : iplugList) {
            IngridDocument response = connectionFacade.getMdekCallerCatalog().getLastModifiedTimestampOfSyslists(iplug, getCatAdminUuid(iplug));
            Long timestamp = MdekCatalogUtils.extractLastModifiedTimestampFromResponse(response);
            if (lowestTimestamp == null || lowestTimestamp > timestamp) {
                lowestTimestamp = timestamp;
            }
        }
        return lowestTimestamp;
    }


    private Integer[] getSyslistIDs(String iplug) {
        IngridDocument response = connectionFacade.getMdekCallerCatalog().getSysLists(iplug, null, null, getCatAdminUuid(iplug));
        return MdekCatalogUtils.extractSysListIdsFromResponse(response);
    }

    private Map<Integer, List<String[]>> getSyslists(String iplug, Integer[] listIds, String languageCode) {
        IngridDocument response = connectionFacade.getMdekCallerCatalog().getSysLists(iplug, listIds, languageCode, getCatAdminUuid(iplug));
        return MdekCatalogUtils.extractSysListFromResponse(response);
    }



    @Override
    public boolean write(List<CodeList> codelists) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Write updated codelists to database ...");
            }
            List<IngridDocument> doc = mapToIngridDocument(codelists);

            // find highest timestamp which will be used for future requests to
            // the codelist repository
            Long highestTimestamp = 0L;
            for (CodeList codelist : codelists) {
                if (highestTimestamp < codelist.getLastModified()) {
                    highestTimestamp = codelist.getLastModified();
                }
            }

            IMdekClientCaller caller = connectionFacade.getMdekClientCaller();
            List<String> iplugList = caller.getRegisteredIPlugs();
            log.debug("Number of iplugs found: "+iplugList.size());
            for (String plugId : iplugList) {
                String user = getCatAdminUuid(plugId);
                boolean success = updateConnectedIgePlugs(plugId, doc, highestTimestamp, user);
                if (!success) {
                    log.error("Could not update codelists in iPlug: " + plugId);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Successfully written codelists to database!");
            }
        } catch (Exception e) {
            log.error("Error on write.", e);
            return false;
        }

        return true;
    }

    private boolean updateConnectedIgePlugs(String plugId, List<IngridDocument> doc, Long timestamp, String catAdminUuid) {
        IngridDocument response = connectionFacade.getMdekCallerCatalog().storeSysLists(plugId, doc, timestamp, catAdminUuid);
        return (Boolean) response.get(IJobRepository.JOB_INVOKE_SUCCESS);
    }

    private List<IngridDocument> mapToIngridDocument(List<CodeList> codelists) throws Exception {
        List<IngridDocument> lists = new ArrayList<>();
        IngridDocument doc = null;
        try {
            for (CodeList codelist : codelists) {
                doc = new IngridDocument();
                // FIXME: this is for compatibility reasons.
                //  remove try/catch when non-numerical codelist IDs are supported everywhere
                try {
                    doc.putInt(MdekKeys.LST_ID, Integer.valueOf(codelist.getId()));
                } catch (NumberFormatException e) {
                    log.debug("Could not parse listId from listKey: " + codelist.getId() + " (skipping)");
                    continue;
                }
                doc.put(MdekKeys.LST_NAME, codelist.getName());
                doc.put(MdekKeys.LST_DESCRIPTION, codelist.getDescription());
                doc.putBoolean(MdekKeys.LST_MAINTAINABLE, false);
                if (codelist.getDefaultEntry() != null && !codelist.getDefaultEntry().isEmpty())
                    doc.putInt(MdekKeys.LST_DEFAULT_ENTRY_ID, Integer.valueOf(codelist.getDefaultEntry()));
                // language specific default entry
                IngridDocument[] entriesDoc = new IngridDocument[codelist.getEntries().size()];
                int i=0;
                for (CodeListEntry entry : codelist.getEntries()) {
                    IngridDocument docEntry = new IngridDocument();
                    docEntry.putInt(MdekKeys.LST_ENTRY_ID, Integer.valueOf(entry.getId()));
                    docEntry.put(MdekKeys.LST_ENTRY_DESCRIPTION, entry.getDescription());
                    docEntry.put(MdekKeys.LST_ENTRY_DATA, entry.getData());
                    IngridDocument locals = new IngridDocument();
                    for (String langKey : entry.getLocalisations().keySet()) {
                        locals.put(langKey, entry.getLocalisedEntry(langKey));
                    }
                    docEntry.put(MdekKeys.LST_LOCALISED_ENTRY_NAME_MAP, locals);
                    entriesDoc[i++] = docEntry;
                }
                doc.setArray(MdekKeys.LST_ENTRIES, entriesDoc);
                lists.add(doc);
            }
        } catch (Exception e) {
            if(doc != null) {
                log.error("Error during mapping of codelist to IngridDocument: " + doc.get(MdekKeys.LST_ID));
            }
            throw e;
        }

        return lists;
    }

    private String getCatAdminUuid(String plugId) {
        try {
            IMdekCallerSecurity mdekCallerSecurity = connectionFacade.getMdekCallerSecurity();
            IngridDocument response = mdekCallerSecurity.getCatalogAdmin(plugId, "");
            IngridDocument result = de.ingrid.mdek.util.MdekUtils.getResultFromResponse(response);
            return (String) result.get(MdekKeysSecurity.IDC_USER_ADDR_UUID);
        } catch (Exception e) {
            return "";
        }
    }

    public void setConnectionFacade(ConnectionFacade cf) {
        this.connectionFacade = cf;
    }

    @Override
    public boolean writePartial(List<CodeList> codelists) {
        return write(codelists);
    }

    @Override
    public boolean canDoPartialUpdates() {
        return true;
    }

    @Override
    public boolean remove(String id) {
        return false;
    }
}
