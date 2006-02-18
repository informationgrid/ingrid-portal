package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.DateUtil;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;



public class SearchDetailPortlet extends GenericVelocityPortlet
{
    private final static Log log = LogFactory.getLog(ServiceResultPortlet.class);

    private final static String TEMPLATE_DETAIL_GENERIC = "/WEB-INF/templates/search_detail_generic.vm";
    private final static String TEMPLATE_ECS = "/WEB-INF/templates/search_detail.vm";
    
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);
        setDefaultViewPage(TEMPLATE_ECS);

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
            context.put("record", record);
            HashMap recordMap = new HashMap();

            // serch for column
            Column[] columns = record.getColumns();
            for (int i = 0; i < columns.length; i++) {
                if (record.getValueAsString(columns[i]).trim().length() > 0) {
                    recordMap.put(columns[i].getTargetName().toLowerCase(), record.getValueAsString(columns[i]).trim());
                }
            }
            addSubRecords(record, recordMap);
            
            recordMap.put("t01_object.mod_time", DateUtil.parseDateString((String)recordMap.get("t01_object.mod_time")));
            
            context.put("rec", recordMap);
        } catch(Throwable t){
            log.error("Error getting result record.", t);
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {

    }
    
    private void addSubRecords(Record record, HashMap map) {
        addSubRecords(record, map, 0);
    }
    
    
    private void addSubRecords(Record record, HashMap map, int level) {
        level++;
        Column[] columns;
        ArrayList subRecordList;
        Record[] subRecords = record.getSubRecords();

        for (int i = 0; i < subRecords.length; i++) {
            HashMap subRecordMap = new HashMap();
            columns = subRecords[i].getColumns();
            for (int j = 0; j < columns.length; j++) {
                if (subRecords[i].getValueAsString(columns[j]).trim().length() > 0) {
                    subRecordMap.put(columns[j].getTargetName().toLowerCase(), subRecords[i].getValueAsString(columns[j]).trim());
                }
            }
            String targetName = columns[0].getTargetName().toLowerCase();
            if (map.containsKey(targetName) && map.get(targetName) instanceof ArrayList) {
                subRecordList = (ArrayList)map.get(targetName);
            } else {
                subRecordList = new ArrayList();
                map.put(targetName, subRecordList);
            }
            subRecordList.add(subRecordMap);
            // add subrecords
            addSubRecords(subRecords[i], subRecordMap, level);
        }
        
    }
    
    
    
}