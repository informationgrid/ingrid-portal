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

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsFileHelper;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.udk.UtilsDate;

/**
 * @author joachim
 *
 */
public class DetailDataPreparerGeneric implements DetailDataPreparer {

    private final static Log log = LogFactory.getLog(DetailDataPreparerGeneric.class);

	private Context context;
	private List dateFields;
	private RenderRequest request;
	private HashMap replacementFields;
    
    
	public DetailDataPreparerGeneric(Context context, List dateFields, RenderRequest request, HashMap replacementFields) {
		this.context = context;
		this.dateFields = dateFields;
		this.request = request;
		this.replacementFields = replacementFields;
		
	}
	
	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.detail.DetailDataPreparer#prepare(de.ingrid.utils.dsc.Record)
	 */
	public void prepare(Record record) throws Throwable {
        context.put("record", record);
        HashMap recordMap = new LinkedHashMap();

        // search for column
        Column[] columns = record.getColumns();
        for (int i = 0; i < columns.length; i++) {

            if (columns[i].toIndex()) {
                String columnName = columns[i].getTargetName();
                columnName = convert2readableColumnName(columnName, (IngridResourceBundle)context.get("MESSAGES"));

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
        
        
        if(record.getSubRecords() != null){
        	List fileList = new ArrayList();
        	
        	fileList = UtilsFileHelper.extractBinaryData(record, fileList);
        	context.put("fileList", fileList);
        }
        
        DetailDataPreparerHelper.addSubRecords(record, recordMap, request.getLocale(), true, (IngridResourceBundle)context.get("MESSAGES"), dateFields, replacementFields);

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
        DetailDataPreparerHelper.setFieldFromHashTree(recordMap, "Ino:id", null);
        DetailDataPreparerHelper.setFieldFromHashTree(recordMap, "Tamino Documenttype", null);
        
        context.put("rec", recordMap);
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

}
