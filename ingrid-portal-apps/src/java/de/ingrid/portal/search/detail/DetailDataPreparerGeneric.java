/*
 * **************************************************-
 * Ingrid Portal Apps
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(DetailDataPreparerGeneric.class);

	private Context context;
	private List dateFields;
	private RenderRequest request;
	private HashMap replacementFields;
    
    
	public DetailDataPreparerGeneric(Context context, List dateFields, RenderRequest request, Map replacementFields) {
		this.context = context;
		this.dateFields = dateFields;
		this.request = request;
		this.replacementFields = (HashMap) replacementFields;
		
	}
	
	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.detail.DetailDataPreparer#prepare(de.ingrid.utils.dsc.Record)
	 */
	public void prepare(Record record) throws Throwable {
        context.put("record", record);
        HashMap recordMap = new LinkedHashMap();
        String valuesForColumn = "";
        
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
                	if(recordMap!= null && recordMap.get(columnName) != null){
                		valuesForColumn = valuesForColumn.concat(record.getValues().get(i).toString().concat("\n")); 
                		recordMap.put(columnName, valuesForColumn);
                	}else{
                		recordMap.put(columnName, record.getValueAsString(columns[i]).trim().replaceAll("\n","<br />"));
                	}
                }
            }
        }
        
        if(record.getSubRecords() != null){
        	List fileList = new ArrayList();
        	
        	fileList = UtilsFileHelper.extractBinaryData(record, fileList, request);
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
        context.put("title", recordMap.get("title"));
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
