package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
        String iplugId = request.getParameter("plugid");

        IngridHit hit = new IngridHit();
        hit.setDocumentId(documentId);
        hit.setPlugId(iplugId);

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
                    ArrayList references = getAllTableRows(record, "T012_obj_obj");
                    Record referenceRecord = null;
                    String refType = null;
                    String objToId;
                    String objFromId;
                    ArrayList superiorReferences = new ArrayList();
                    ArrayList subordinatedReferences = new ArrayList();
                    ArrayList crossReferences = new ArrayList();
                    String objId = (String)record.get("T01_object.obj_id");
                    
                    for (int i=0; i<references.size(); i++) {
                        referenceRecord = (Record) references.get(i);
                        refType = (String)referenceRecord.get("T012_obj_obj.typ");
                        objToId = (String)referenceRecord.get("T012_obj_obj.object_to_id");
                        objFromId = (String)referenceRecord.get("T012_obj_obj.object_from_id");
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
                    
                    // get superior addresses
                    
                    // get all addresses
                    ArrayList addrRecords = getAllTableRows(record, "T02_address");
                    // get all address references
                    ArrayList addrReferenceRecords = getAllTableRows(record, "T022_adr_adr");
                    // create references hashmap that holds all references per address
                    HashMap addrReferences = new HashMap();
                    // iterate over all addresses and assign the references
                    for (int i=0; i<addrRecords.size(); i++) {
                        Record addrRecord = (Record)addrRecords.get(i);
                        // get id of the address
                        String addressId = (String)addrRecord.get("T02_ADDRESS.ADR_ID");
                        // create the hashmap that holds the references for a particular address 
                        HashMap addrReferenceHash = new HashMap();
                        addrReferences.put(addressId, addrReferenceHash);
                        // iterate over all address references
                        for (int j=0; j < addrReferenceRecords.size(); j++) {
                            // get address reference info from record
                            Record refRecord = (Record)addrReferenceRecords.get(i);
                            String addrToId = (String)refRecord.get("T022_ADR_ADR.ADR_TO_ID");
                            String addrFromId = (String)refRecord.get("T022_ADR_ADR.ADR_FROM_ID");
                            // get superior reference
                            if (addrToId != null && addrToId.equals(addressId)) {
                                HashMap udkAddress = getUDKAddressHash(addrFromId);
                                String addressType = (String)udkAddress.get("T02_address.typ");
                                // check for address types
                                if (addressType.equals("0")) {
                                    // add the referencing institution to the list
                                    if (!addrReferenceHash.containsKey("0")) {
                                        addrReferenceHash.put("0", new ArrayList());
                                    }
                                    ((ArrayList)addrReferenceHash.get("0")).add(udkAddress);
                                } else if (addressType.equals("1")) {
                                    // add the referencing unit to the list
                                    if (!addrReferenceHash.containsKey("1")) {
                                        addrReferenceHash.put("1", new ArrayList());
                                    }
                                    ((ArrayList)addrReferenceHash.get("1")).add(udkAddress);
                                } else if (addressType.equals("2")) {
                                    // add the referencing person to the list
                                    if (!addrReferenceHash.containsKey("2")) {
                                        addrReferenceHash.put("2", new ArrayList());
                                    }
                                    ((ArrayList)addrReferenceHash.get("2")).add(udkAddress);
                                } else if (addressType.equals("3")) {
                                    // add the referencing free address to the list
                                    if (!addrReferenceHash.containsKey("3")) {
                                        addrReferenceHash.put("3", new ArrayList());
                                    }
                                    ((ArrayList)addrReferenceHash.get("3")).add(udkAddress);
                                }
                            }
                        }
                    }
                    
                    context.put("addressReferences", addrReferences);
                    
                    context.put("superiorReferences", superiorReferences);
                    context.put("subordinatedReferences", subordinatedReferences);
                    context.put("crossReferences", crossReferences);
                } else if (IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
                    setDefaultViewPage(TEMPLATE_DETAIL_ECS_ADDRESS);
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
    
    private HashMap getUDKAddressHash(String addrId) throws Exception {
        String queryStr = "T02_address.adr_id:" + addrId + " datatype:address ranking:score";
        IngridQuery q = QueryStringParser.parse(queryStr);
        IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, 10, 1, 10, PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000));
        HashMap addrHash = null;
        if (hits.length() > 0) {
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
        if (hits.length() > 0) {
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
        
        Column[] columns;
        Record[] subRecords = record.getSubRecords();
        for (int i = 0; i < subRecords.length; i++) {
            columns = subRecords[i].getColumns();
            if (columns.length > 0) {
                String columnName = columns[0].getTargetName();
                if (columnName.startsWith(tableName)) {
                    result.add(subRecords[i]);
                }
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
        if (cmd.equals("doShowAddressDetail")) {
            String addrId = request.getParameter("addrId");
            try {
                HashMap addrHash = getUDKAddressHash(addrId);
                IngridHit hit = (IngridHit)addrHash.get("hit");
                response.setRenderParameter("docid", hit.getId().toString());
                response.setRenderParameter("plugid", hit.getPlugId());
            } catch (Exception e) {
                log.error("Error fetching address data for address id: " + addrId, e);
            }
        }
        
    }
    
}