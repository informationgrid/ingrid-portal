/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.udk.UtilsDate;

/**
 * @author joachim
 *
 */
public class DetailDataPreparer_UDK_5_0_Address implements DetailDataPreparer {

    private final static Log log = LogFactory.getLog(DetailDataPreparer_UDK_5_0_Address.class);
	
	private Context context;
	private String iplugId;
	private List dateFields;
	private RenderRequest request;
	private HashMap replacementFields;
	
	public DetailDataPreparer_UDK_5_0_Address(Context context, String iPlugId, List dateFields, RenderRequest request, HashMap replacementFields) {
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
        // enrich address with institution and units
        HashMap addrParents = new LinkedHashMap();
        String addressType = (String) record.get("T02_ADDRESS.TYP");
        String addrId = (String) record.get("T02_ADDRESS.ADR_ID");
        // get id of the address
        if (addressType.equals("1") || addressType.equals("2")) {
        	DetailDataPreparerHelper.getUDKAddressParents(addrParents, addrId, iplugId);
        }
        context.put("addrParents", addrParents);

        // get references
        ArrayList superiorReferences = new ArrayList();
        IngridHit h = DetailDataPreparerHelper.getParentAddress(addrId, iplugId);
        if (h != null) {
            superiorReferences.add(DetailDataPreparerHelper.getParentAddress(addrId, iplugId));
        }
        ArrayList subordinatedReferences = DetailDataPreparerHelper.getAddressChildren(addrId, iplugId);

        context.put("superiorReferences", superiorReferences);
        context.put("subordinatedReferences", subordinatedReferences);

        // get ALL subordinated addresses in the complete hierarchie
        ArrayList allAddressChildren = new ArrayList();
        allAddressChildren.addAll(subordinatedReferences);
        for (int i = 0; i < subordinatedReferences.size(); i++) {
            String myAddrId = (String) ((IngridHitDetail) ((IngridHit) subordinatedReferences.get(i))
                    .get(Settings.RESULT_KEY_DETAIL)).get("T02_address.adr_id");
            String myAddrType = (String) ((IngridHitDetail) ((IngridHit) subordinatedReferences.get(i))
                    .get(Settings.RESULT_KEY_DETAIL)).get("T02_address.typ");
            if (myAddrType.equals("0") || myAddrType.equals("1")) {
                allAddressChildren.addAll(getAllAddressChildren(myAddrId, iplugId));
            }
        }
        Collections.sort(subordinatedReferences, new AddressRecordComparator());


        // get related record of the subordinated address references
        HashMap subordinatedObjRef = new LinkedHashMap();
        for (int i = 0; i < allAddressChildren.size(); i++) {
            ArrayList l = getObjectsByAddress((String) ((IngridHitDetail) ((IngridHit) allAddressChildren
                    .get(i)).get(Settings.RESULT_KEY_DETAIL)).get("T02_address.adr_id"), iplugId);
            for (int j = 0; j < l.size(); j++) {
                IngridHit objHit = (IngridHit) l.get(j);
                IngridHitDetail detail = (IngridHitDetail) objHit.get(Settings.RESULT_KEY_DETAIL);
                String objId = (String) detail.get(Settings.HIT_KEY_OBJ_ID);
                if (!subordinatedObjRef.containsKey(objId)) {
                    subordinatedObjRef.put(objId, objHit);
                }
            }
        }
        context.put("subordinatedObjRef", subordinatedObjRef);
        
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
        DetailDataPreparerHelper.addSubRecords(record, recordMap, request.getLocale(), false, (IngridResourceBundle)context.get("context"), dateFields, replacementFields);

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
	
    private ArrayList getAllAddressChildren(String addrId, String iPlugId) {
        ArrayList result = DetailDataPreparerHelper.getAddressChildren(addrId, iPlugId);
        int size = result.size();
        for (int i = 0; i < size; i++) {
            IngridHit hit = (IngridHit) result.get(i);
            IngridHitDetail detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);
            String addrType = (String) detail.get(Settings.HIT_KEY_ADDRESS_CLASS);
            if (addrType.equals("0") || addrType.equals("1")) {
                result.addAll(getAllAddressChildren((String) detail.get(Settings.HIT_KEY_ADDRESS_ADDRID), iPlugId));
            }
        }
        return result;
    }
    
    private ArrayList getObjectsByAddress(String addrId, String iPlugId) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        HashMap filter = new HashMap();
        ArrayList result = DetailDataPreparerHelper.getHits("T02_address.adr_id:".concat(addrId).concat(" iplugs:\"".concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter);
        return result;
    }

	

}
