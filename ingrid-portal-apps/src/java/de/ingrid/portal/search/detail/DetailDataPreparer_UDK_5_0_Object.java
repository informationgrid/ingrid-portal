/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.udk.UtilsDate;

/**
 * @author joachim
 *
 */
public class DetailDataPreparer_UDK_5_0_Object implements DetailDataPreparer {

    private final static Log log = LogFactory.getLog(DetailDataPreparer_UDK_5_0_Object.class);
	
	private Context context;
	private String iplugId;
	private List dateFields;
	private RenderRequest request;
	private HashMap replacementFields;
	
	
	public DetailDataPreparer_UDK_5_0_Object(Context context, String iPlugId, List dateFields, RenderRequest request, HashMap replacementFields) {
		this.context = context;
		this.iplugId = iPlugId;
		this.dateFields = dateFields;
		this.request = request;
		this.replacementFields = replacementFields;
	}
	
	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.detail.DetailDataPreparer#prepare(de.ingrid.utils.dsc.Record)
	 */
	public void prepare(Record record) throws Throwable {
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
                DetailDataPreparerHelper.getUDKAddressParents(parents, addressId, iplugId);
                addrParents.put(addressId, parents);
            }
        }
        context.put("addrParents", addrParents);

        context.put("superiorReferences", superiorReferences);
        context.put("subordinatedReferences", subordinatedReferences);
        context.put("crossReferences", crossReferences);
        

        context.put("record", record);
        HashMap recordMap = new LinkedHashMap();

        // search for column
        Column[] columns = record.getColumns();
        for (int i = 0; i < columns.length; i++) {

            if (columns[i].toIndex()) {
                String columnName = columns[i].getTargetName();
                columnName = columnName.toLowerCase();

                if (dateFields.contains(columnName)) {
                    recordMap.put(columnName, UtilsDate.parseDateToLocale(record.getValueAsString(columns[i])
                            .trim(), request.getLocale()));
                } else if (replacementFields.containsKey(columnName)) {
                    // replace value according to translation config
                    Map transMap = (Map) replacementFields.get(columnName);
                    String src = record.getValueAsString(columns[i]).trim();
                    if (transMap.containsKey(src)) {
                        recordMap.put(columnName, transMap.get(src));
                    } else {
                        recordMap.put(columnName, src);
                    }
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
        DetailDataPreparerHelper.addSubRecords(record, recordMap, request.getLocale(), false, (IngridResourceBundle)context.get("MESSAGES"), dateFields, replacementFields);

        // Replace all occurrences of <*> except the specified ones (<b>, </b>, <i>, ... are the ones NOT replaced)
        String summary = (String) DetailDataPreparerHelper.getFieldFromHashTree(recordMap, "summary");
        if (summary != null)
        	summary = summary.replaceAll("<(?!b>|/b>|i>|/i>|u>|/u>|p>|/p>|br>|br/>|br />|strong>|/strong>|ul>|/ul>|ol>|/ol>|li>|/li>)[^>]*>", "");

        recordMap.put("summary", summary);
        recordMap.put("t0", DetailDataPreparerHelper.getFieldFromHashTree(recordMap, "t0"));
        recordMap.put("t1", DetailDataPreparerHelper.getFieldFromHashTree(recordMap, "t1"));
        recordMap.put("t2", DetailDataPreparerHelper.getFieldFromHashTree(recordMap, "t2"));
        ArrayList addressList = (ArrayList) DetailDataPreparerHelper.getFieldFromHashTree(recordMap, "t012_obj_adr.obj_id");
        if (addressList != null) {
            Collections.sort(addressList, new AddressTypeComparator());
        }
        context.put("rec", recordMap);        
    }
	
    private ArrayList getSuperiorObjects(String objId, String iPlugId) {
        String[] requestedMetadata = new String[5];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_OBJ_OBJ_FROM;
        requestedMetadata[3] = Settings.HIT_KEY_OBJ_OBJ_TYP;
        requestedMetadata[4] = Settings.HIT_KEY_OBJ_OBJ_TO;
        HashMap filter = new HashMap();
        // gleiches Objekt raus filtern
        filter.put(Settings.HIT_KEY_OBJ_ID, objId);
        // HACK !!!
        // we also apply filter for parent relations WHEN FETCHING HITS !!!
        // SO WE ALWAYS GET REAL PARENTS !!!
        IPlugHelperDscEcs.addRelationFilter(filter, null, "0", objId);
        ArrayList result = DetailDataPreparerHelper.getHits("t012_obj_obj.object_to_id:".concat(objId).concat(
                " t012_obj_obj.typ:0 iplugs:\"".concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), requestedMetadata, filter);
        return result;
    }

    private ArrayList getSubordinatedObjects(String objId, String iPlugId) {
    	return IPlugHelperDscEcs.getSubordinatedObjects(objId, iPlugId);
    }

    private ArrayList getCrossReferencedObjects(String objId, String iPlugId) {
        String[] requestedMetadata = new String[5];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        requestedMetadata[2] = Settings.HIT_KEY_OBJ_OBJ_FROM;
        requestedMetadata[3] = Settings.HIT_KEY_OBJ_OBJ_TYP;
        requestedMetadata[4] = Settings.HIT_KEY_OBJ_OBJ_TO;
        // gleiches Objekt raus filtern
        HashMap filter = new HashMap();
        filter.put(Settings.HIT_KEY_OBJ_ID, objId);
        // HACK !!!
        // we also apply filter for "querverweise" relations WHEN FETCHING HITS !!!
        // SO WE ALWAYS GET REAL "querverweise" !!!
        IPlugHelperDscEcs.addRelationFilter(filter, objId, "1", null);
        ArrayList result = DetailDataPreparerHelper.getHits("t012_obj_obj.object_from_id:".concat(objId).concat(
                " t012_obj_obj.typ:1 iplugs:\"".concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), requestedMetadata, filter);
    	return result;
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

}
