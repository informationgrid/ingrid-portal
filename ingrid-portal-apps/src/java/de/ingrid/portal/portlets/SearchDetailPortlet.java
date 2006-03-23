package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.UtilsDate;
import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;



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
            Record record = ibus.getRecord(hit);
            PlugDescription plugDescription = ibus.getIPlug(iplugId);
            
            // flag to make column name readable (not lowercase, character substitution)
            boolean readableColumnNames = false;
            
            if (IPlugHelper.hasDataType(plugDescription, "dsc_ecs")) {
                setDefaultViewPage(TEMPLATE_DETAIL_ECS);
            } else if (IPlugHelper.hasDataType(plugDescription, "dsc_ecs_address")) {
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
                        recordMap.put(columnName, record.getValueAsString(columns[i]).trim());
                    }
                }
            }
            addSubRecords(record, recordMap, request.getLocale(), readableColumnNames);
            
            context.put("rec", recordMap);
        } catch(Throwable t){
            log.error("Error getting result record.", t);
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {

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
                        subRecordMap.put(columnName, subRecords[i].getValueAsString(columns[j]).trim());
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
    
    
    
}