package de.ingrid.portal.portlets;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsDate;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.SearchState;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;



public class SearchDetailPortlet extends GenericVelocityPortlet
{
    private final static Log log = LogFactory.getLog(ServiceResultPortlet.class);

    private final static String TEMPLATE_DETAIL_GENERIC = "/WEB-INF/templates/search_detail_generic.vm";
    private final static String TEMPLATE_DETAIL_ECS = "/WEB-INF/templates/search_detail.vm";
    private final static String TEMPLATE_DETAIL_ECS_ADDRESS = "/WEB-INF/templates/search_detail_address.vm";
    
    // ecs fields that represent a date, used for date parsing and formating
    private ArrayList dateFields = new ArrayList();
    
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);

        dateFields.add("t01_object.mod_time");
        dateFields.add("t01_object.time_to");
        dateFields.add("t01_object.time_from");
        dateFields.add("t0113_dataset_reference.reference_date");
        dateFields.add("t02_address.mod_time");
        
    }    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(request.getLocale()));
        context.put("MESSAGES", messages);
        
        int documentId = Integer.parseInt(request.getParameter("docid"));
        String altDocumentId = request.getParameter("altdocid");
        String iplugId = request.getParameter("plugid");
        
        IngridHit hit = new IngridHit();
        hit.setDocumentId(documentId);
        hit.setPlugId(iplugId);
        if (altDocumentId != null) {
            hit.put("alt_document_id", altDocumentId);
        }


        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        
        try {
            PlugDescription plugDescription = ibus.getIPlug(iplugId);
            
            // flag to make column name readable (not lowercase, character substitution)
            boolean readableColumnNames = false;
            
            
            Record record = ibus.getRecord(hit);
            if (record == null) {
                log.error("No record found for document id:" + documentId + " using iplug: " + iplugId);
            } else {
                
                // extract the code lists
                String udkLangCode;
                if (request.getLocale().getLanguage().equals(new Locale("en", "", "").getLanguage())) {
                    udkLangCode = "94";
                } else {
                    udkLangCode = "121";
                }
                ArrayList codeListRecords = getAllTableRows(record, "sys_codelist_domain");
                HashMap codeLists = new HashMap();
                for (int i=0; i<codeListRecords.size(); i++) {
                    Record codeListRecord = (Record)codeListRecords.get(i);
                    String langId = (String)codeListRecord.get("sys_codelist_domain.lang_id");
                    if (langId.equals(udkLangCode)) {
                        String codeListId = (String)codeListRecord.get("sys_codelist_domain.codelist_id");
                        if (!codeLists.containsKey(codeListId)) {
                            codeLists.put(codeListId, new HashMap());
                        }
                        HashMap codeListHash = (HashMap)codeLists.get(codeListId);
    
                        String domainId = (String)codeListRecord.get("sys_codelist_domain.domain_id");
                        if (!codeListHash.containsKey(domainId)) {
                            codeListHash.put(domainId, new HashMap());
                        }
                        HashMap codeListDomainHash = (HashMap)codeListHash.get(domainId);
                        codeListDomainHash.putAll(codeListRecord);
                    }
                }
                context.put("codeLists", codeLists);
                
                // set language code list
                HashMap sysLangHashs = new HashMap();
                
                HashMap sysLangHash = new HashMap();
                sysLangHash.put("sys_language.lang_id", "121");
                sysLangHash.put("sys_language.name", "Deutsch");
                sysLangHash.put("sys_language.description", "Deutsch");
                sysLangHash.put("sys_language.def_lang", "1");
                sysLangHashs.put("121", sysLangHash);
                
                sysLangHash = new HashMap();
                sysLangHash.put("sys_language.lang_id", "94");
                sysLangHash.put("sys_language.name", "English");
                sysLangHash.put("sys_language.description", "English");
                sysLangHash.put("sys_language.def_lang", "1");
                sysLangHashs.put("94", sysLangHash);
                
                context.put("sysLangList", sysLangHashs);
                
                if (IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
                    setDefaultViewPage(TEMPLATE_DETAIL_ECS);
                    
                    // get references
                    ArrayList references = getAllTableRows(record, "t012_obj_obj");
                    Record referenceRecord = null;
                    String refType = null;
                    String objToId;
                    String objFromId;
                    ArrayList superiorReferences = new ArrayList();
                    ArrayList subordinatedReferences = new ArrayList();
                    ArrayList crossReferences = new ArrayList();
                    String objId = (String)record.get("T01_OBJECT.OBJ_ID");
                    
                    for (int i=0; i<references.size(); i++) {
                        referenceRecord = (Record) references.get(i);
                        refType = (String)referenceRecord.get("t012_obj_obj.typ");
                        objToId = (String)referenceRecord.get("t012_obj_obj.object_to_id");
                        objFromId = (String)referenceRecord.get("t012_obj_obj.object_from_id");
                        if (refType.equals("0")) {
                            // add superior reference
                            if (objToId.equals(objId)) {
                                superiorReferences.add(getUDKObjectHash((String)objFromId));
                            }
                            // add subordinated reference
                            if (objFromId.equals(objId)) {
                                subordinatedReferences.add(getUDKObjectHash((String)objToId));
                            }
                        } else if (refType.equals("1")) {
                            // add cross reference
                            if (objFromId.equals(objId)) {
                                crossReferences.add(getUDKObjectHash((String)objToId));
                            }
                        }
                    }
                    
                    // enrich addresses with institution and units
                    ArrayList addrRecords = getAllTableRows(record, "T02_ADDRESS");
                    HashMap addrParents = new HashMap();
                    // iterate over all addresses and add missing information for the address
                    for (int i=0; i<addrRecords.size(); i++) {
                        Record addrRecord = (Record)addrRecords.get(i);
                        String addressType = (String)addrRecord.get("T02_ADDRESS.TYP");
                        // get id of the address
                        String addressId = (String)addrRecord.get("T02_ADDRESS.ADR_ID");
                        if (addressType.equals("1") || addressType.equals("2")) {
                            HashMap parents = new HashMap();
                            // get the address record of the address
                            String queryStr = "T02_address.adr_id:" + addressId + " datatype:address ranking:score";
                            IngridQuery q = QueryStringParser.parse(queryStr);
                            IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, 10, 1, 10, PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000));
                            if (hits.getHits().length > 0) {
                                IngridHit refHit = hits.getHits()[0];
                                Record myRecord = IBUSInterfaceImpl.getInstance().getRecord(refHit);
                                // get parents of the address record
                                getUDKAddressParents(parents, myRecord);
                                addrParents.put(addressId, parents);
                            }
                        }
                    }
                    context.put("addrParents", addrParents);
                   
                    context.put("superiorReferences", superiorReferences);
                    context.put("subordinatedReferences", subordinatedReferences);
                    context.put("crossReferences", crossReferences);
                } else if (IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
                    setDefaultViewPage(TEMPLATE_DETAIL_ECS_ADDRESS);

                    // enrich address with institution and units
                    HashMap addrParents = new HashMap();
                    String addressType = (String)record.get("T02_ADDRESS.TYP");
                    // get id of the address
                    if (addressType.equals("1") || addressType.equals("2")) {
                        getUDKAddressParents(addrParents, record);
                    }
                    context.put("addrParents", addrParents);                    
                    
                    // get references
                    ArrayList references = getAllTableRows(record, "t022_adr_adr");
                    Record referenceRecord = null;
                    String addrToId;
                    String addrFromId;
                    ArrayList superiorReferences = new ArrayList();
                    ArrayList subordinatedReferences = new ArrayList();
                    String addrId = (String)record.get("T02_ADDRESS.ADR_ID");
                    
                    for (int i=0; i<references.size(); i++) {
                        referenceRecord = (Record) references.get(i);
                        addrToId = (String)referenceRecord.get("t022_adr_adr.adr_to_id");
                        addrFromId = (String)referenceRecord.get("t022_adr_adr.adr_from_id");
                        // add superior reference
                        if (addrToId.equals(addrId)) {
                            superiorReferences.add(getUDKAddressHash(addrFromId));
                        }
                        // add subordinated reference
                        if (addrFromId.equals(addrId)) {
                            subordinatedReferences.add(getUDKAddressHash(addrToId));
                        }
                    }
                    context.put("superiorReferences", superiorReferences);
                    context.put("subordinatedReferences", subordinatedReferences);
                    
                    // get ALL suborninated addresses in the complete hierarchie
                    ArrayList allAddressChildren = new ArrayList();
                    allAddressChildren.addAll(subordinatedReferences);
                    for (int i=0; i<subordinatedReferences.size(); i++) {
                        allAddressChildren.addAll(getUDKAddressChildren((Record)((HashMap)subordinatedReferences.get(i)).get("record")));
                    }
                    
                    // get related record of the subordinated address references
                    HashMap subordinatedObjRef = new HashMap();
                    for (int i=0; i<allAddressChildren.size(); i++) {
                        Record r = (Record)((HashMap)allAddressChildren.get(i)).get("record");
                        ArrayList refs = getAllTableRows(r, "T012_OBJ_ADR");
                        for (int j=0; j<refs.size(); j++) {
                            Record refRecord = (Record)refs.get(j);
                            ArrayList refObjects = getAllTableRows(refRecord, "T01_OBJECT");
                            for (int k=0; k<refObjects.size(); k++) {
                                Record refObj = (Record)refObjects.get(k);
                                if (!subordinatedObjRef.containsKey(refObj.get("T01_OBJECT.OBJ_ID"))) {
                                    subordinatedObjRef.put(refObj.get("T01_OBJECT.OBJ_ID"), refObj);
                                }
                            }
                        }
                    }
                    context.put("subordinatedObjRef", subordinatedObjRef);
                    
                    
                } else {
                    setDefaultViewPage(TEMPLATE_DETAIL_GENERIC);
                    readableColumnNames = true;
                }
                
                context.put("record", record);
                HashMap recordMap = new HashMap();
                
                // serch for column
                Column[] columns = record.getColumns();
                for (int i = 0; i < columns.length; i++) {
                    if (record.getValueAsString(columns[i]).trim().length() > 0) {
                        String columnName = columns[i].getTargetName();
                        if (readableColumnNames) {
                            columnName = columnName.replace('_', ' ');
                        } else {
                            columnName = columnName.toLowerCase();
                        }
                        
                        if (dateFields.contains(columnName)) {
                            recordMap.put(columnName, UtilsDate.parseDateToLocale(record.getValueAsString(columns[i]).trim(), request.getLocale()));
                        } else {
                            recordMap.put(columnName, record.getValueAsString(columns[i]).trim().replaceAll("\n", "<br />"));
                        }
                    }
                }
                addSubRecords(record, recordMap, request.getLocale(), readableColumnNames);
                
                context.put("rec", recordMap);
            }
        } catch(Throwable t){
            log.error("Error getting result record.", t);
        }

        super.doView(request, response);
    }

    private ArrayList getUDKAddressChildren(Record record) throws Exception {
        ArrayList result = new ArrayList();
        
        // get references
        ArrayList references = getAllTableRows(record, "t022_adr_adr");
        Record referenceRecord = null;
        String addrToId;
        String addrFromId;
        String addrId = (String)record.get("T02_ADDRESS.ADR_ID");
        
        for (int i=0; i<references.size(); i++) {
            referenceRecord = (Record) references.get(i);
            addrToId = (String)referenceRecord.get("t022_adr_adr.adr_to_id");
            addrFromId = (String)referenceRecord.get("t022_adr_adr.adr_from_id");
            // add subordinated reference
            if (addrFromId.equals(addrId)) {
                HashMap addrHash = getUDKAddressHash(addrToId);
                result.add(addrHash);
                String addrType = (String)((Record)addrHash.get("record")).get("T02_ADDRESS.TYP");
                if (addrType.equals("0") || addrType.equals("1")) {
                    result.addAll(getUDKAddressChildren((Record)addrHash.get("record")));
                }
            }
        }
        return result;
    }

    private void getUDKAddressParents(HashMap result, Record record) throws Exception {
        // get id of the address
        String addressId = (String)record.get("T02_ADDRESS.ADR_ID");
        // get type of the address
        String addressType = (String)record.get("T02_ADDRESS.TYP");
        // get parent only for units and persons
        if (addressType.equals("1") || addressType.equals("2")) {
            // get all referenced addresses
            ArrayList addrReferenceRecords = getAllTableRows(record, "t022_adr_adr");
            // iterate over all references and find first parent address
            for (int i=0; i < addrReferenceRecords.size(); i++) {
                Record refRecord = (Record)addrReferenceRecords.get(i);
                String addrToId = (String)refRecord.get("t022_adr_adr.adr_to_id");
                String addrFromId = (String)refRecord.get("t022_adr_adr.adr_from_id");
                if (addrToId != null && addrToId.equals(addressId)) {
                    // get the parent of the address
                    String queryStr = "T02_address.adr_id:" + addrFromId + " datatype:address ranking:score";
                    IngridQuery q = QueryStringParser.parse(queryStr);
                    IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, 10, 1, 10, PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000));
                    for (int j=0; j<hits.getHits().length; j++) {
                        IngridHit refHit = hits.getHits()[j];
                        Record myRecord = IBUSInterfaceImpl.getInstance().getRecord(refHit);
                        String myAddressType = (String)myRecord.get("T02_ADDRESS.TYP");
                        if (myAddressType.equals("0")) {
                            if (!result.containsKey("institutions")) {
                                result.put("institutions", new ArrayList());
                            }
                            ArrayList institutions = (ArrayList)result.get("institutions");
                            institutions.add(myRecord);
                        } else if (myAddressType.equals("1")) {
                            if (!result.containsKey("units")) {
                                result.put("units", new ArrayList());
                            }
                            ArrayList units = (ArrayList)result.get("units");
                            units.add(0, myRecord);
                            getUDKAddressParents(result, myRecord);
                        }
                    }
                }
            }
        }
    }
    
    private HashMap getUDKAddressHash(String addrId) throws Exception {
        String queryStr = "T02_address.adr_id:" + addrId + " datatype:address ranking:score";
        IngridQuery q = QueryStringParser.parse(queryStr);
        IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, 10, 1, 10, PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000));
        HashMap addrHash = null;
        if (hits.getHits().length > 0) {
            IngridHit refHit = hits.getHits()[0];
            addrHash = new HashMap();
            addrHash.put("record", IBUSInterfaceImpl.getInstance().getRecord(refHit));
            addrHash.put("hit", refHit);
        }
        return addrHash;
    }

    private HashMap getUDKObjectHash(String objId) throws Exception {
        String queryStr = "T01_object.obj_id:" + objId + " datatype:dsc_ecs ranking:score";
        IngridQuery q = QueryStringParser.parse(queryStr);
        IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, 10, 1, 10, PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000));
        HashMap objHash = null;
        if (hits.getHits().length > 0) {
            IngridHit refHit = hits.getHits()[0];
            IngridHitDetail refDetail =  IBUSInterfaceImpl.getInstance().getDetail(refHit, q, null);
            objHash = new HashMap();
            objHash.put("obj_id", objId);
            objHash.put("title", refDetail.getTitle());
            objHash.put("hit", refHit);
        }
        return objHash;
    }


    private void addSubRecords(Record record, HashMap map, Locale locale, boolean readableColumns) {
        addSubRecords(record, map, locale, 0, readableColumns);
    }
    
    
    private void addSubRecords(Record record, HashMap map, Locale locale, int level, boolean readableColumns) {
        level++;
        Column[] columns;
        ArrayList subRecordList;
        Record[] subRecords = record.getSubRecords();

        for (int i = 0; i < subRecords.length; i++) {
            HashMap subRecordMap = new HashMap();
            columns = subRecords[i].getColumns();
            for (int j = 0; j < columns.length; j++) {
                if (subRecords[i].getValueAsString(columns[j]).trim().length() > 0) {
                    String columnName = columns[j].getTargetName();
                    if (readableColumns) {
                        columnName = columnName.replace('_', ' ');
                    } else {
                        columnName = columnName.toLowerCase();
                    }
                    if (dateFields.contains(columnName)) {
                        subRecordMap.put(columnName, UtilsDate.parseDateToLocale(subRecords[i].getValueAsString(columns[j]).trim(), locale));
                    } else if (columnName.startsWith("t08_attr")) {
                        // dummy code add logic for attribute fields
                        System.out.println(columns[j].getTargetName());
                    } else {
                        subRecordMap.put(columnName, subRecords[i].getValueAsString(columns[j]).trim().replaceAll("\n", "<br />"));
                    }
                }
            }
            String targetName = columns[0].getTargetName();
            if (readableColumns) {
                targetName = targetName.replace('_', ' ');
            } else {
                targetName = targetName.toLowerCase();
            }
            if (map.containsKey(targetName) && map.get(targetName) instanceof ArrayList) {
                subRecordList = (ArrayList)map.get(targetName);
            } else {
                subRecordList = new ArrayList();
                map.put(targetName, subRecordList);
            }
            subRecordList.add(subRecordMap);
            // add subrecords
            addSubRecords(subRecords[i], subRecordMap, locale, level, readableColumns);
        }
        
    }
    
    
    private ArrayList getAllTableRows(Record record,  String tableName) {
        ArrayList result = new ArrayList();
        
//        Column[] columns;
        Record[] subRecords = record.getSubRecords();
        for (int i = 0; i < subRecords.length; i++) {

            Set keys = subRecords[i].keySet();
            Iterator it = keys.iterator();
            String firstKey = (String)it.next();
            if (firstKey.startsWith(tableName)) {
                result.add(subRecords[i]);
            }            
            
            result.addAll(getAllTableRows(subRecords[i], tableName));
        }
        return result;
    }
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String cmd = request.getParameter("cmd");
        if (cmd == null) {
            return;
        } else if (cmd.equals("doShowAddressDetail")) {
            String addrId = request.getParameter("addrId");
            try {
                HashMap addrHash = getUDKAddressHash(addrId);
                IngridHit hit = (IngridHit)addrHash.get("hit");
                response.setRenderParameter("docid", hit.getId().toString());
                response.setRenderParameter("plugid", hit.getPlugId());
                if (hit.get(".alt_document_id") != null) {
                    response.setRenderParameter("altdocid", (String)hit.get("alt_document_id"));
                }
            } catch (Exception e) {
                log.error("Error fetching address data for address id: " + addrId, e);
            }
        } else if (cmd.equals("doShowObjectDetail")) {
            String objId = request.getParameter("objId");
            try {
                HashMap objHash = getUDKObjectHash(objId);
                IngridHit hit = (IngridHit)objHash.get("hit");
                response.setRenderParameter("docid", hit.getId().toString());
                response.setRenderParameter("plugid", hit.getPlugId());
                if (hit.get(".alt_document_id") != null) {
                    response.setRenderParameter("altdocid", (String)hit.get("alt_document_id"));
                }
            } catch (Exception e) {
                log.error("Error fetching address data for address id: " + objId, e);
            }
        } else if (cmd.equals("doShowDocument")) {
            response.setRenderParameter("docid", request.getParameter("docid"));
            response.setRenderParameter("plugid", request.getParameter("plugid"));
            if (request.getParameter("alt_document_id") != null) {
                response.setRenderParameter("altdocid", request.getParameter("alt_document_id"));
            }
        }
        
    }
    
}