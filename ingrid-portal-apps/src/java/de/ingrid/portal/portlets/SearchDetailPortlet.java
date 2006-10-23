package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
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
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsDate;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class SearchDetailPortlet extends GenericVelocityPortlet {
    private final static Log log = LogFactory.getLog(SearchDetailPortlet.class);

    private final static String TEMPLATE_DETAIL_GENERIC = "/WEB-INF/templates/search_detail_generic.vm";

    private final static String TEMPLATE_DETAIL_ECS = "/WEB-INF/templates/search_detail.vm";

    private final static String TEMPLATE_DETAIL_ECS_ADDRESS = "/WEB-INF/templates/search_detail_address.vm";

    // ecs fields that represent a date, used for date parsing and formating
    private ArrayList dateFields = new ArrayList();

    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        dateFields.add("t01_object.mod_time");
        dateFields.add("t01_object.time_to");
        dateFields.add("t0");
        dateFields.add("t01_object.time_from");
        dateFields.add("t1");
        dateFields.add("t0113_dataset_reference.reference_date");
        dateFields.add("t02_address.mod_time");
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // add velocity utils class
        context.put("tool", new UtilsVelocity());

        try {
            int documentId = Integer.parseInt(request.getParameter("docid"));
            String altDocumentId = request.getParameter("altdocid");
            String iplugId = request.getParameter("plugid");

            context.put("plugId", iplugId);
            
            IngridHit hit = new IngridHit();
            hit.setDocumentId(documentId);
            hit.setPlugId(iplugId);
            if (altDocumentId != null) {
                hit.put("alt_document_id", altDocumentId);
            }

            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();

            PlugDescription plugDescription = ibus.getIPlug(iplugId);

            // flag to make column name readable (not lowercase, character substitution '_' => ' ')
            boolean readableColumnNames = false;

            Record record = ibus.getRecord(hit);
            
//            XStream xstream = new XStream();
//            String serializedObject = xstream.toXML(record);
            
            if (record == null) {
                log.error("No record found for document id:" + documentId + " using iplug: " + iplugId);
            } else {

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

                // put codelist fetcher into context
                context.put("codeList", new IngridSysCodeList(request.getLocale()));

                if (IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)
                        || IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_ECS)
                        || IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_CSW)) {
                    setDefaultViewPage(TEMPLATE_DETAIL_ECS);

                    String objId = (String) record.get("T01_OBJECT.OBJ_ID");
                    // get references
                    ArrayList superiorReferences = null;
                    ArrayList subordinatedReferences = null;
                    ArrayList crossReferences = null;
                    try {
                        superiorReferences = getSuperiorObjects(objId, iplugId);
                        subordinatedReferences = getSubordinatedObjects(objId, iplugId);
                        crossReferences = getCrossReferencedObjects(objId, iplugId);
                    } catch (Throwable t) {
                        log.error("Error getting related objects, obj_id = " + objId, t);

                        // we throw again, so no record is rendered !
                        throw t;
                    }

                    // enrich addresses with institution and units
                    ArrayList addrRecords = getAllTableRows(record, "T02_ADDRESS");
                    HashMap addrParents = new LinkedHashMap();
                    // iterate over all addresses and add missing information for the address
                    for (int i = 0; i < addrRecords.size(); i++) {
                        Record addrRecord = (Record) addrRecords.get(i);
                        String addressType = (String) addrRecord.get("T02_ADDRESS.TYP");
                        // get id of the address
                        String addressId = (String) addrRecord.get("T02_ADDRESS.ADR_ID");
                        if (addressType.equals("1") || addressType.equals("2")) {
                            HashMap parents = new LinkedHashMap();
                            getUDKAddressParents(parents, addressId, iplugId);
                            addrParents.put(addressId, parents);
                        }
                    }
                    context.put("addrParents", addrParents);

                    context.put("superiorReferences", superiorReferences);
                    context.put("subordinatedReferences", subordinatedReferences);
                    context.put("crossReferences", crossReferences);
                } else if (IPlugHelper.hasDataType(plugDescription, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
                    setDefaultViewPage(TEMPLATE_DETAIL_ECS_ADDRESS);

                    // enrich address with institution and units
                    HashMap addrParents = new LinkedHashMap();
                    String addressType = (String) record.get("T02_ADDRESS.TYP");
                    String addrId = (String) record.get("T02_ADDRESS.ADR_ID");
                    // get id of the address
                    if (addressType.equals("1") || addressType.equals("2")) {
                        getUDKAddressParents(addrParents, addrId, iplugId);
                    }
                    context.put("addrParents", addrParents);

                    // get references
                    ArrayList superiorReferences = new ArrayList();
                    IngridHit h = getParentAddress(addrId, iplugId);
                    if (h != null) {
                        superiorReferences.add(getParentAddress(addrId, iplugId));
                    }
                    ArrayList subordinatedReferences = getAddressChildren(addrId, iplugId);

                    context.put("superiorReferences", superiorReferences);
                    context.put("subordinatedReferences", subordinatedReferences);

                    // get ALL subordinated addresses in the complete hierarchie
                    ArrayList allAddressChildren = new ArrayList();
                    allAddressChildren.addAll(subordinatedReferences);
                    for (int i = 0; i < subordinatedReferences.size(); i++) {
                        String myAddrId = (String) ((IngridHitDetail) ((IngridHit) subordinatedReferences.get(i))
                                .get("detail")).get("T02_address.adr_id");
                        String myAddrType = (String) ((IngridHitDetail) ((IngridHit) subordinatedReferences.get(i))
                                .get("detail")).get("T02_address.typ");
                        if (myAddrType.equals("0") || myAddrType.equals("1")) {
                            allAddressChildren.addAll(getAllAddressChildren(myAddrId, iplugId));
                        }
                    }

                    // get related record of the subordinated address references
                    HashMap subordinatedObjRef = new LinkedHashMap();
                    for (int i = 0; i < allAddressChildren.size(); i++) {
                        ArrayList l = getObjectsByAddress((String) ((IngridHitDetail) ((IngridHit) allAddressChildren
                                .get(i)).get("detail")).get("T02_address.adr_id"), iplugId);
                        for (int j = 0; j < l.size(); j++) {
                            IngridHit objHit = (IngridHit) l.get(j);
                            IngridHitDetail detail = (IngridHitDetail) objHit.get("detail");
                            String objId = (String) detail.get(Settings.HIT_KEY_OBJ_ID);
                            if (!subordinatedObjRef.containsKey(objId)) {
                                subordinatedObjRef.put(objId, objHit);
                            }
                        }
                    }
                    context.put("subordinatedObjRef", subordinatedObjRef);

                } else {
                    setDefaultViewPage(TEMPLATE_DETAIL_GENERIC);
                    readableColumnNames = true;
                }

                context.put("record", record);
                HashMap recordMap = new LinkedHashMap();

                // search for column
                Column[] columns = record.getColumns();
                for (int i = 0; i < columns.length; i++) {

                    if (columns[i].toIndex()) {
                        String columnName = columns[i].getTargetName();
                        if (readableColumnNames) {
                            // convert to readable column names
                            columnName = convert2readableColumnName(columnName, messages);
                        } else {
                            columnName = columnName.toLowerCase();
                        }

                        if (dateFields.contains(columnName)) {
                            recordMap.put(columnName, UtilsDate.parseDateToLocale(record.getValueAsString(columns[i])
                                    .trim(), request.getLocale()));
                        } else {
                            recordMap.put(columnName, record.getValueAsString(columns[i]).trim().replaceAll("\n",
                                    "<br />"));
                        }
                    }
                }
                /*
                 // FOR TESTING !!!
                 HashMap myHash = new LinkedHashMap();
                 ArrayList tmpList = new ArrayList();
                 tmpList.add(myHash);
                 HashMap myHash2 = new LinkedHashMap();
                 tmpList.add(myHash2);
                 recordMap.put("t011_obj_serv.obj_id", tmpList);

                 //myHash.put("t011_obj_serv.type", "mm");
                 //myHash.put("t011_obj_serv.environment", "mm");
                 //myHash.put("t011_obj_serv.history", "mm");
                 //myHash.put("t011_obj_serv.base", "mm");
                 //myHash.put("t011_obj_serv_version.obj_id", "mm");
                 //myHash.put("t011_obj_serv_operation.obj_id", "mm");

                 //myHash2.put("t011_obj_literatur.autor", "mm");
                 */
                addSubRecords(record, recordMap, request.getLocale(), readableColumnNames, messages);

                recordMap.put("summary", getFieldFromHashTree(recordMap, "summary"));
                ArrayList addressList = (ArrayList) getFieldFromHashTree(recordMap, "t012_obj_adr.obj_id");
                if (addressList != null) {
                    Collections.sort(addressList, new AddressTypeComparator());
                }
                if (this.getDefaultViewPage().equals(TEMPLATE_DETAIL_GENERIC)) {
                    setFieldFromHashTree(recordMap, "Ino:id", null);
                    setFieldFromHashTree(recordMap, "Tamino Documenttype", null);
                }
                context.put("rec", recordMap);
            }
        } catch (Throwable t) {
            log.error("Error getting result record.", t);
        }

        super.doView(request, response);
    }

    /**
     * Converts string to readable column name:
     * 
     *  <ul>
     *  <li>try to find the column name in the localization files, returns if found</li>
     *  <li>check for special columns (title, summary), returns if found</li>
     *  <li>replaces '_' with ' '</li>
     *  <li>uppercase words first character (use stopwords configured in ingrid.portal.apps.properties)</li>
     *  </ul>
     * 
     * 
     * @param columnName The column name.
     * @param messages The resoure bundle.
     * @return The readable column name.
     */
    private String convert2readableColumnName(String columnName, IngridResourceBundle messages) {

        // try to find the column name in the localization
        String localizedName = messages.getString(columnName);
        if (!localizedName.equals(columnName)) {
            return localizedName;
        }

        final String reservedColumnNames = "|title|summary|";
        final String ucUpperStopWords = PortalConfig.getInstance().getString(
                PortalConfig.DETAILS_GENERIC_UCFIRST_STOPWORDS, "");

        if (reservedColumnNames.indexOf("|".concat(columnName).concat("|")) != -1) {
            return columnName;
        }

        // replace '_' with ' '
        columnName = columnName.replace('_', ' ');
        // uppercase words first character
        String[] words = columnName.split(" ");
        columnName = "";
        for (int j = 0; j < words.length; j++) {
            if (j > 0) {
                columnName = columnName.concat(" ");
            }
            if (words[j].length() > 0) {
                if (ucUpperStopWords.indexOf("|".concat(words[j]).concat("|")) == -1) {
                    columnName = columnName.concat(words[j].replaceFirst(UtilsString.regExEscape(words[j].substring(0,
                            1)), words[j].substring(0, 1).toUpperCase()));
                } else {
                    columnName = columnName.concat(words[j]);
                }
            }
        }
        return columnName;
    }

    private void addSubRecords(Record record, HashMap map, Locale locale, boolean readableColumns,
            IngridResourceBundle messages) {
        addSubRecords(record, map, locale, 0, readableColumns, messages);
    }

    private void addSubRecords(Record record, HashMap map, Locale locale, int level, boolean readableColumns,
            IngridResourceBundle messages) {
        level++;
        Column[] columns;
        ArrayList subRecordList;
        Record[] subRecords = record.getSubRecords();

        for (int i = 0; i < subRecords.length; i++) {
            HashMap subRecordMap = new LinkedHashMap();
            columns = subRecords[i].getColumns();
            for (int j = 0; j < columns.length; j++) {
                if (columns[j].toIndex()) {
                    String columnName = columns[j].getTargetName();
                    if (readableColumns) {
                        // convert to readable column names
                        columnName = convert2readableColumnName(columnName, messages);
                    } else {
                        columnName = columnName.toLowerCase();
                    }
                    if (dateFields.contains(columnName)) {
                        subRecordMap.put(columnName, UtilsDate.parseDateToLocale(subRecords[i].getValueAsString(
                                columns[j]).trim(), locale));
                    } else {
                        subRecordMap.put(columnName, subRecords[i].getValueAsString(columns[j]).trim().replaceAll("\n",
                                "<br />"));
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
                subRecordList = (ArrayList) map.get(targetName);
            } else {
                subRecordList = new ArrayList();
                map.put(targetName, subRecordList);
            }
            subRecordList.add(subRecordMap);
            // add subrecords
            addSubRecords(subRecords[i], subRecordMap, locale, level, readableColumns, messages);
        }

    }

    private void getUDKAddressParents(HashMap result, String addrId, String iPlugId) throws Exception {
        // get id of the address
        String addressId = addrId;
        // set initial address type to 1
        String addressType = "";
        do {
            // get the paren address hit + detail
            IngridHit parent = getParentAddress(addressId, iPlugId);
            if (parent == null) {
                // no parent found
                break;
            }
            addressType = UtilsSearch.getDetailValue((IngridHitDetail) parent.get("detail"),
                    Settings.HIT_KEY_ADDRESS_CLASS);
            addressId = UtilsSearch.getDetailValue((IngridHitDetail) parent.get("detail"),
                    Settings.HIT_KEY_ADDRESS_ADDRID);
            if (addressType.equals("0")) {
                if (!result.containsKey("institutions")) {
                    result.put("institutions", new ArrayList());
                }
                ArrayList institutions = (ArrayList) result.get("institutions");
                institutions.add(parent);
            } else if (addressType.equals("1")) {
                if (!result.containsKey("units")) {
                    result.put("units", new ArrayList());
                }
                ArrayList units = (ArrayList) result.get("units");
                units.add(0, parent);
            }
            // exit loop if parent was NO unit
        } while (addressType.equals("1"));
    }

    /**
     * Iterate over the hashmap ArrayList structure and return the requested field.
     * 
     * @param hash
     * @param fieldName
     * @return
     */
    private Object getFieldFromHashTree(HashMap hash, String fieldName) {
        Iterator it = hash.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.equals(fieldName)) {
                return hash.get(key);
            }
            if (hash.get(key) instanceof ArrayList) {
                ArrayList array = (ArrayList) hash.get(key);
                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i) instanceof HashMap) {
                        Object val = getFieldFromHashTree((HashMap) array.get(i), fieldName);
                        if (val != null) {
                            return val;
                        }
                    }
                }
            }

        }
        return null;
    }

    /**
     * Iterate over the hashmap ArrayList structure and set the requested field.
     * 
     * @param hash
     * @param fieldName
     * @return
     */
    private boolean setFieldFromHashTree(HashMap hash, String fieldName, String value) {
        Iterator it = hash.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.equals(fieldName)) {
                hash.put(key, value);
                return true;
            }
            if (hash.get(key) instanceof ArrayList) {
                ArrayList array = (ArrayList) hash.get(key);
                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i) instanceof HashMap) {
                        if (setFieldFromHashTree((HashMap) array.get(i), fieldName, value)) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    private ArrayList getAllTableRows(Record record, String tableName) {
        ArrayList result = new ArrayList();

        Record[] subRecords = record.getSubRecords();
        for (int i = 0; i < subRecords.length; i++) {

            Set keys = subRecords[i].keySet();
            Iterator it = keys.iterator();
            String firstKey = (String) it.next();
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
            String plugId = getAddressPlugIdFromPlugId(request.getParameter("plugid"));
            try {
                IngridHit hit = getAddressHit(addrId, plugId);
                response.setRenderParameter("docid", hit.getId().toString());
                response.setRenderParameter("plugid", hit.getPlugId());
                if (hit.get(".alt_document_id") != null) {
                    response.setRenderParameter("altdocid", (String) hit.get("alt_document_id"));
                }
            } catch (Exception e) {
                log.error("Error fetching address data for address id: " + addrId, e);
            }
        } else if (cmd.equals("doShowObjectDetail")) {
            String objId = request.getParameter("objId");
            String plugId = getPlugIdFromAddressPlugId(request.getParameter("plugid"));
            try {
                IngridHit hit = getObjectHit(objId, plugId);
                response.setRenderParameter("docid", hit.getId().toString());
                response.setRenderParameter("plugid", hit.getPlugId());
                if (hit.get(".alt_document_id") != null) {
                    response.setRenderParameter("altdocid", (String) hit.get("alt_document_id"));
                }
            } catch (Exception e) {
                log.error("Error fetching object data for object id: " + objId, e);
            }
        } else if (cmd.equals("doShowDocument")) {
            response.setRenderParameter("docid", request.getParameter("docid"));
            response.setRenderParameter("plugid", request.getParameter("plugid"));
            if (request.getParameter("alt_document_id") != null) {
                response.setRenderParameter("altdocid", request.getParameter("alt_document_id"));
            }
        }

    }

    private ArrayList getSuperiorObjects(String objId, String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        HashMap filter = new HashMap();
        filter.put(Settings.HIT_KEY_OBJ_ID, objId);
        ArrayList result = getHits("t012_obj_obj.object_to_id:".concat(objId).concat(
                " t012_obj_obj.typ:0 iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), requestedMetadata, filter);
        return result;
    }

    private ArrayList getSubordinatedObjects(String objId, String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        HashMap filter = new HashMap();
        filter.put(Settings.HIT_KEY_OBJ_ID, objId);
        ArrayList result = getHits("t012_obj_obj.object_from_id:".concat(objId).concat(
                " t012_obj_obj.typ:0 iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), requestedMetadata, filter);
        return result;
    }

    private ArrayList getCrossReferencedObjects(String objId, String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        HashMap filter = new HashMap();
        filter.put(Settings.HIT_KEY_OBJ_ID, objId);
        ArrayList result = getHits("t012_obj_obj.object_from_id:".concat(objId).concat(
                " t012_obj_obj.typ:1 iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), requestedMetadata, filter);
        return result;
    }

    private ArrayList getObjectsByAddress(String addrId, String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        HashMap filter = new HashMap();
        ArrayList result = getHits("T02_address.adr_id:".concat(addrId).concat(" iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter);
        return result;
    }

    private IngridHit getParentAddress(String addrId, String iPlugId) {
        String[] requestedMetadata = new String[7];
        requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
        requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_ADDRESS_FIRSTNAME;
        requestedMetadata[3] = Settings.HIT_KEY_ADDRESS_LASTNAME;
        requestedMetadata[4] = Settings.HIT_KEY_ADDRESS_TITLE;
        requestedMetadata[5] = Settings.HIT_KEY_ADDRESS_ADDRESS;
        requestedMetadata[6] = Settings.HIT_KEY_ADDRESS_ADDRID;
        HashMap filter = new HashMap();
        filter.put(Settings.HIT_KEY_ADDRESS_ADDRID, addrId);
        ArrayList result = getHits("T022_adr_adr.adr_to_id:".concat(addrId).concat(" iplugs:\"".concat(getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter);
        if (result.size() > 0) {
            return (IngridHit) result.get(0);
        } else {
            return null;
        }
    }

    private ArrayList getAddressChildren(String addrId, String iPlugId) {
        String[] requestedMetadata = new String[7];
        requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
        requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_ADDRESS_FIRSTNAME;
        requestedMetadata[3] = Settings.HIT_KEY_ADDRESS_LASTNAME;
        requestedMetadata[4] = Settings.HIT_KEY_ADDRESS_TITLE;
        requestedMetadata[5] = Settings.HIT_KEY_ADDRESS_ADDRESS;
        requestedMetadata[6] = Settings.HIT_KEY_ADDRESS_ADDRID;
        HashMap filter = new HashMap();
        filter.put(Settings.HIT_KEY_ADDRESS_ADDRID, addrId);
        ArrayList result = getHits(
                "T022_adr_adr.adr_from_id:".concat(addrId).concat(" iplugs:\"".concat(getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter);
        return result;
    }

    private IngridHit getAddressHit(String addrId, String iPlugId) {
        String[] requestedMetadata = new String[7];
        requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
        requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_ADDRESS_FIRSTNAME;
        requestedMetadata[3] = Settings.HIT_KEY_ADDRESS_LASTNAME;
        requestedMetadata[4] = Settings.HIT_KEY_ADDRESS_TITLE;
        requestedMetadata[5] = Settings.HIT_KEY_ADDRESS_ADDRESS;
        requestedMetadata[6] = Settings.HIT_KEY_ADDRESS_ADDRID;
        ArrayList result = getHits("T02_address.adr_id:".concat(addrId).concat(" iplugs:\"".concat(getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
                requestedMetadata, null);
        if (result.size() > 0) {
            return (IngridHit) result.get(0);
        } else {
            return null;
        }
    }

    private IngridHit getObjectHit(String objId, String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        ArrayList result = getHits("T01_object.obj_id:".concat(objId).concat(" iplugs:\"".concat(getPlugIdFromAddressPlugId(iPlugId)).concat("\"")),
                requestedMetadata, null);
        if (result.size() > 0) {
            return (IngridHit) result.get(0);
        } else {
            return null;
        }
    }

    private ArrayList getAllAddressChildren(String addrId, String iPlugId) {
        ArrayList result = getAddressChildren(addrId, iPlugId);
        int size = result.size();
        for (int i = 0; i < size; i++) {
            IngridHit hit = (IngridHit) result.get(i);
            IngridHitDetail detail = (IngridHitDetail) hit.get("detail");
            String addrType = (String) detail.get(Settings.HIT_KEY_ADDRESS_CLASS);
            if (addrType.equals("0") || addrType.equals("1")) {
                result.addAll(getAllAddressChildren((String) detail.get(Settings.HIT_KEY_ADDRESS_ADDRID), iPlugId));
            }
        }
        return result;
    }

    private ArrayList getHits(String queryStr, String[] requestedMetaData, HashMap filter) {
        ArrayList result = new ArrayList();
        try {
            IngridQuery query = QueryStringParser.parse(queryStr.concat(" ranking:any datatype:any"));
            IngridHits hits;
            // request hits in chunks of 20 results per page
            int page = 0;
            do {
                page++;
                hits = IBUSInterfaceImpl.getInstance().search(query, 20, page, 20,
                        PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 3000));
                IngridHitDetail details[] = IBUSInterfaceImpl.getInstance().getDetails(hits.getHits(), query,
                        requestedMetaData);
                for (int j = 0; j < details.length; j++) {
                    IngridHitDetail detail = (IngridHitDetail) details[j];
                    boolean include = true;
                    if (filter != null && filter.size() > 0) {
                        Iterator it = filter.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry entry = (Map.Entry) it.next();
                            String recordKey = (String) entry.getKey();
                            String value = (String) entry.getValue();
                            if (value.equals(UtilsSearch.getDetailValue(detail, recordKey))) {
                                include = false;
                                break;
                            }
                        }
                    }
                    if (include) {
                        // flatten alle detail fields
                        for (int i = 0; i < requestedMetaData.length; i++) {
                            detail.put(requestedMetaData[i], UtilsSearch.getDetailValue(detail, requestedMetaData[i]));
                        }
                        hits.getHits()[j].put("detail", detail);
                        result.add(hits.getHits()[j]);
                    }
                }
            } while (hits.getHits().length == 20);
        } catch (Exception e) {
            log.error("Problems getting hits from iBus!", e);
        }
        return result;
    }

    /**
     * Returns the plug id of the corresponding address plug id. Address plug
     * id's are detected from the postfix "_addr". The postfix will be stripped.
     * Plug id's without this postfix remain unchanged.
     * 
     * @param plugId
     * @return
     */
    public String getPlugIdFromAddressPlugId(String plugId) {
        if (plugId.endsWith("_addr")) {
            return plugId.substring(0, plugId.lastIndexOf("_addr"));
        } else {
            return plugId;
        }
    }

    /**
     * Returns the address plug id of the corresponding plug id, by adding the postfix
     * "_addr" to the plug id. Only plug id's that contain the string "udk-db" and do
     * not contain the postfix "_addr" will be changed.
     * 
     * @param plugId
     * @return
     */
    public String getAddressPlugIdFromPlugId(String plugId) {
        if (plugId.indexOf("udk-db") > -1 && !plugId.endsWith("_addr")) {
            return plugId.concat("_addr");
        } else {
            return plugId;
        }
    }

    /**
     * Inner class: AddressTypeComperator for address sorting;
     *
     * @author joachim@wemove.com
     */
    private class AddressTypeComparator implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public final int compare(Object a, Object b) {
            int ia;
            int ib;
            try {
                ia = Integer.parseInt((String) ((HashMap) a).get("t012_obj_adr.typ"));
                ib = Integer.parseInt((String) ((HashMap) b).get("t012_obj_adr.typ"));
            } catch (Exception e) {
                return 0;
            }

            if (ia > ib)
                return 1;
            else if (ia < ib)
                return -1;
            else
                return 0;
        }
    }

}