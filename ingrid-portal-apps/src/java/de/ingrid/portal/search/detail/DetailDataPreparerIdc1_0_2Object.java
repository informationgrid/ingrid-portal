/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

/**
 * @author joachim
 *
 */
public class DetailDataPreparerIdc1_0_2Object implements DetailDataPreparer {

    private final static Log log = LogFactory.getLog(DetailDataPreparerIdc1_0_2Object.class);
	
	private Context context;
	private String iPlugId;
	private RenderRequest request;
	private RenderResponse response;
	private IngridResourceBundle messages;
	private IngridSysCodeList sysCodeList;
	
	public DetailDataPreparerIdc1_0_2Object(Context context, String iPlugId, RenderRequest request, RenderResponse response) {
		this.context = context;
		this.iPlugId = iPlugId;
		this.request = request;
		this.response = response;
		messages = (IngridResourceBundle)context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.detail.DetailDataPreparer#prepare(de.ingrid.utils.dsc.Record)
	 */
	public void prepare(Record record) {

		HashMap data = new HashMap();
		HashMap general = new HashMap();
		general.put("title", record.getString("t01_object.obj_name"));
		general.put("modTime", UtilsDate.convertDateString(record.getString("t01_object.mod_time").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
		String objClassStr = record.getString("t01_object.obj_class");
		if (objClassStr != null) {
			general.put("udkObjClass", objClassStr);
			general.put("udkObjClassName", messages.getString("udk_obj_class_name_".concat(objClassStr)));
		}

		data.put("general", general);
		
		ArrayList elements = new ArrayList();
		int previousElementsSize = elements.size();

		// alternate name
		addElementEntry(elements, record.getString("t01_object.dataset_alternate_name"), null);
		// udk class
		addElementUdkClass(elements, record.getString("t01_object.obj_class"));
		// description
        String description = record.getString("t01_object.obj_descr");
		if (description != null) {
			description = description.replaceAll("\n", "<br/>");
			description = description.replaceAll("<(?!b>|/b>|i>|/i>|u>|/u>|p>|/p>|br>|br/>|br />|strong>|/strong>|ul>|/ul>|ol>|/ol>|li>|/li>)[^>]*>", "");
        }
		addElementEntry(elements, description, null);
		
		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}
		
		// superior objects
		addSuperiorObjects(elements, record.getString("t01_object.obj_uuid"));

		// subordinated objects
		addSubordinatedObjects(elements, record.getString("t01_object.obj_uuid"));

		// cross referenced objects
		addCrossReferencedObjects(elements, record.getString("t01_object.id"));
		
		//	URL references
		addUrlReferences(elements, record);

		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}

		//	addresses
		addAddresses(elements, record);

		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}
		
		// add subject data
		if (objClassStr.equals("5")) {
			addReferenceObjectClass5(elements, record);
		} else if (objClassStr.equals("3")) {
			addReferenceObjectClass3(elements, record);
		} else if (objClassStr.equals("2")) {
			addReferenceObjectClass2(elements, record);
		} else if (objClassStr.equals("4")) {
			addReferenceObjectClass4(elements, record);
		} else if (objClassStr.equals("1")) {
			addReferenceObjectClass1(elements, record);
		}
		
		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}

		// add spatial reference data
		addSpatialReference(elements, record);

		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}
		
		// add time reference data
		addTimeReference(elements, record);

		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}
		
		// add availability information
		addAvailabilityInformation(elements, record);

		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}
		
		// add additional information
		addAdditionalInformation(elements, record);

		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}

		// add index information
		addIndexInformation(elements, record);
		
		data.put("elements", elements);
		
		context.put("data", data);
	}
	
	
	private void addIndexInformation(List elements, Record record) {

    	// index references
		List listRecords = getSubRecordsByColumnName(record, "t04_search.type");
    	if (listRecords.size() > 0) {
	    	ArrayList lines = new ArrayList();
	    	for (int i=0; i<listRecords.size(); i++) {
	    		Record listRecord = (Record)listRecords.get(i);
	    		HashMap line = new HashMap();
	        	line.put("type", "textLine");
	        	line.put("body", listRecord.getString("searchterm_value.term"));
	        	if (!isEmptyLine(line)) {
	        		lines.add(line);
	        	}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("title", messages.getString("search_terms"));
		    	element.put("elements", lines);
	    	    elements.add(element);
	    	}
    	}

    	// topic categories
		listRecords = getSubRecordsByColumnName(record, "t011_obj_topic_cat.line");
    	if (listRecords.size() > 0) {
	    	ArrayList lines = new ArrayList();
	    	for (int i=0; i<listRecords.size(); i++) {
	    		Record listRecord = (Record)listRecords.get(i);
	    		HashMap line = new HashMap();
	        	line.put("type", "textLine");
	        	line.put("body", sysCodeList.getName("527", listRecord.getString("t011_obj_topic_cat.topic_category")));
	        	if (!isEmptyLine(line)) {
	        		lines.add(line);
	        	}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("title", messages.getString("t011_obj_geo_topic_cat.topic_category"));
		    	element.put("elements", lines);
	    	    elements.add(element);
	    	}
    	}

    	// environment categories
		listRecords = getSubRecordsByColumnName(record, "t0114_env_category.cat_value");
    	if (listRecords.size() > 0) {
    		ArrayList lines = new ArrayList();
	    	for (int i=0; i<listRecords.size(); i++) {
	    		Record listRecord = (Record)listRecords.get(i);
	    		if (listRecord != null && listRecord.get("syslist.lang_id") != null && listRecord.get("syslist.lang_id").equals(request.getLocale().getLanguage())) {
		    		HashMap line = new HashMap();
		        	line.put("type", "textLine");
		        	line.put("body", listRecord.get("syslist.name"));
		        	if (!isEmptyLine(line)) {
		        		lines.add(line);
		        	}
	    		}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("title", messages.getString("t0114_env_category"));
		    	element.put("elements", lines);
	    	    elements.add(element);
	    	}
    	}
    	
    	// environment topics
		listRecords = getSubRecordsByColumnName(record, "t0114_env_topic.topic_name");
    	if (listRecords.size() > 0) {
    		ArrayList lines = new ArrayList();
	    	for (int i=0; i<listRecords.size(); i++) {
	    		Record listRecord = (Record)listRecords.get(i);
	    		if (listRecord != null && listRecord.get("syslist.lang_id") != null && listRecord.get("syslist.lang_id").equals(request.getLocale().getLanguage())) {
		    		HashMap line = new HashMap();
		        	line.put("type", "textLine");
		        	line.put("body", listRecord.get("syslist.name"));
		        	if (!isEmptyLine(line)) {
		        		lines.add(line);
		        	}
	    		}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("title", messages.getString("t0114_env_topic"));
		    	element.put("elements", lines);
	    	    elements.add(element);
	    	}
    	}
    	
	}

	private void addAdditionalInformation(List elements, Record record) {

   		if (UtilsVelocity.hasContent(record.getString("t01_object.metadata_language")).booleanValue()) {
   			addElementEntryInline(elements, messages.getString("iso.language.code.".concat(record.getString("t01_object.metadata_language"))), messages.getString("t01_object.metadata_language"));
   		}
   		if (UtilsVelocity.hasContent(record.getString("t01_object.data_language")).booleanValue()) {
   			addElementEntryInline(elements, messages.getString("iso.language.code.".concat(record.getString("t01_object.data_language"))), messages.getString("t01_object.data_language"));
   		}
		
    	// legal references
		List listRecords = getSubRecordsByColumnName(record, "t015_legist.name");
    	if (listRecords.size() > 0) {
	    	ArrayList lines = new ArrayList();
	    	for (int i=0; i<listRecords.size(); i++) {
	    		Record listRecord = (Record)listRecords.get(i);
	    		HashMap line = new HashMap();
	        	line.put("type", "textLine");
	        	line.put("body", listRecord.getString("t0113_dataset_reference.legist_value"));
	        	if (!isEmptyLine(line)) {
	        		lines.add(line);
	        	}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("title", messages.getString("legal_basis"));
		    	element.put("elements", lines);
	    	    elements.add(element);
	    	}
    	}
   		
    	addElementEntry(elements, record.getString("t01_object.dataset_usage"), messages.getString("t01_object.dataset_usage"));
   		addElementEntry(elements, record.getString("t01_object.info_note"), messages.getString("t01_object.info_note"));
	}
	
	
	private void addAvailabilityInformation(List elements, Record record) {
		List tableRecords = getSubRecordsByColumnName(record, "t0110_avail_format.line");
		if (tableRecords.size() > 0) {
	    	HashMap element = new HashMap();
	    	element.put("type", "table");
	    	element.put("title", messages.getString("data_format"));
			ArrayList head = new ArrayList();
			head.add(messages.getString("t0110_avail_format.name"));
			head.add(messages.getString("t0110_avail_format.version"));
			head.add(messages.getString("t0110_avail_format.file_decompression_technique"));
			head.add(messages.getString("t0110_avail_format.specification"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
	    	for (int i=0; i<tableRecords.size(); i++) {
	    		Record tableRecord = (Record)tableRecords.get(i);
	    		ArrayList row = new ArrayList();
	    		row.add(notNull(tableRecord.getString("t0110_avail_format.format_value")));
	    		row.add(notNull(tableRecord.getString("t0110_avail_format.ver")));
	    		row.add(notNull(tableRecord.getString("t0110_avail_format.file_decompression_technique")));
	    		row.add(notNull(tableRecord.getString("t0110_avail_format.specification")));
	    		if (!isEmptyRow(row)) {
	    			body.add(row);
	    		}
	    	}
	    	if (body.size() > 0) {
		    	elements.add(element);
		    	element = new HashMap();
		    	element.put("type", "space");
				elements.add(element);
	    	}
	    	
		}

		tableRecords = getSubRecordsByColumnName(record, "t0112_media_option.line");
		if (tableRecords.size() > 0) {
	    	HashMap element = new HashMap();
	    	element.put("type", "table");
	    	element.put("title", messages.getString("t0112_media_option.medium"));
			ArrayList head = new ArrayList();
			head.add(messages.getString("t0112_media_option.medium_name"));
			head.add(messages.getString("t0112_media_option.transfer_size"));
			head.add(messages.getString("t0112_media_option.medium_note"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
	    	for (int i=0; i<tableRecords.size(); i++) {
	    		Record tableRecord = (Record)tableRecords.get(i);
	    		ArrayList row = new ArrayList();
	    		row.add(notNull(sysCodeList.getName("520", tableRecord.getString("t0112_media_option.medium_name"))));
	    		row.add(notNull(tableRecord.getString("t0112_media_option.transfer_size")));
	    		row.add(notNull(tableRecord.getString("t0112_media_option.medium_note")));
	    		if (!isEmptyRow(row)) {
	    			body.add(row);
	    		}
	    	}
	    	elements.add(element);
	    	element = new HashMap();
	    	element.put("type", "space");
			elements.add(element);
		}
   		addElementEntry(elements, record.getString("t01_object.ordering_instructions"), messages.getString("t01_object.ordering_instructions"));
   		addElementEntry(elements, record.getString("t01_object.avail_access_note"), messages.getString("t01_object.avail_access_note"));
   		addElementEntry(elements, record.getString("t01_object.fees"), messages.getString("t01_object.fees"));
   		
	}

	
	private void addTimeReference(List elements, Record record) {
    	// time reference
		List refRecords = getSubRecordsByColumnName(record, "t01_object.id");
    	if (refRecords.size() > 0) {
	    	for (int i=0; i<refRecords.size(); i++) {
	    		Record refRecord = (Record)refRecords.get(i);
       	    	String timeType = refRecord.getString("t01_object.time_type");
	    		if(UtilsVelocity.hasContent(timeType).booleanValue()) {
	       	    	String entryLine = "";
	       	    	if (timeType.equals("von") && refRecord.getString("t01_object.time_from") != null && refRecord.getString("t01_object.time_to") != null) {
	       	    		entryLine = entryLine.concat(messages.getString("search.detail.time.from")).concat(": ");
	       	    		entryLine = entryLine.concat(UtilsDate.convertDateString(refRecord.getString("t01_object.time_from").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
	       	    		entryLine = entryLine.concat(" ").concat(messages.getString("search.detail.time.to")).concat(": ");
	       	    		entryLine = entryLine.concat(UtilsDate.convertDateString(refRecord.getString("t01_object.time_to").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
	       	    	} else if (timeType.equals("seit") && refRecord.getString("t01_object.time_from") != null) {
	       	    		entryLine = entryLine.concat(messages.getString("search.detail.time.since")).concat(": ");
	       	    		entryLine = entryLine.concat(UtilsDate.convertDateString(refRecord.getString("t01_object.time_from").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
	       	    	} else if (timeType.equals("am") && refRecord.getString("t01_object.time_from") != null) {
	       	    		entryLine = entryLine.concat(messages.getString("search.detail.time.at")).concat(": ");
	       	    		entryLine = entryLine.concat(UtilsDate.convertDateString(refRecord.getString("t01_object.time_from").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
	       	    	} else if (timeType.equals("bis") && refRecord.getString("t01_object.time_to") != null) {
	       	    		entryLine = entryLine.concat(messages.getString("search.detail.time.until")).concat(": ");
	       	    		entryLine = entryLine.concat(UtilsDate.convertDateString(refRecord.getString("t01_object.time_to").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
	       	    	}
	       	    	if (entryLine.length() > 0) {
	       	    		addElementEntry(elements, entryLine, messages.getString("time_reference_content"));
	       	    	}
	    		}
	    	}
    	}
    	
    	addElementEntry(elements, sysCodeList.getName("523", record.getString("t01_object.time_status")), messages.getString("t01_object.time_status"));
    	addElementEntry(elements, sysCodeList.getName("518", record.getString("t01_object.time_period")), messages.getString("t01_object.time_period"));
    	if (UtilsVelocity.hasContent(record.getString("t01_object.time_alle")).booleanValue() 
    			&& UtilsVelocity.hasContent(record.getString("t01_object.time_interval")).booleanValue()) {
        	String entryLine = record.getString("t01_object.time_alle").concat(" ").concat(record.getString("t01_object.time_interval"));
    		addElementEntry(elements, entryLine, messages.getString("t01_object.time_interval"));
    	}
    	
    	// time references
		List listRecords = getSubRecordsByColumnName(record, "t0113_dataset_reference.line");
    	if (listRecords.size() > 0) {
	    	ArrayList lines = new ArrayList();
	    	for (int i=0; i<listRecords.size(); i++) {
	    		Record listRecord = (Record)listRecords.get(i);
	    		HashMap line = new HashMap();
	        	line.put("type", "textLine");
	        	String textLine = sysCodeList.getName("502", listRecord.getString("t0113_dataset_reference.type"));
	        	textLine = textLine.concat(": ").concat(UtilsDate.convertDateString(listRecord.getString("t0113_dataset_reference.reference_date").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
	        	line.put("body", textLine);
	        	if (!isEmptyLine(line)) {
	        		lines.add(line);
	        	}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("title", messages.getString("time_reference_record"));
		    	element.put("elements", lines);
	    	    elements.add(element);
	    	}
    	}
    	addElementEntry(elements, record.getString("t01_object.time_descr"), messages.getString("t01_object.time_descr"));
	}

	
	private void addSpatialReference(List elements, Record record) {
    	// geo thesaurus references
		List refRecords = getSubRecordsByColumnName(record, "spatial_ref_value.name_value");
    	if (refRecords.size() > 0) {
	    	ArrayList lines = new ArrayList();
	    	for (int i=0; i<refRecords.size(); i++) {
	    		Record listRecord = (Record)refRecords.get(i);
	        	if (listRecord.getString("spatial_ref_value.type") != null && listRecord.getString("spatial_ref_value.type").equals("G")) {
		    		HashMap line = new HashMap();
		        	line.put("type", "textLine");
		        	String textLine = listRecord.getString("spatial_ref_value.name_value");
		        	if (textLine != null && listRecord.getString("spatial_ref_value.nativekey") != null) {
		        		textLine = textLine.concat(" (").concat(listRecord.getString("spatial_ref_value.nativekey")).concat(")");
		        	}
		        	line.put("body", textLine);
		        	if (!isEmptyLine(line)) {
		        		lines.add(line);
		        	}
	        	}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("title", messages.getString("t011_township.township_no"));
		    	element.put("elements", lines);
	    	    elements.add(element);
	    	}
    	}

    	// geo thesaurus references
		refRecords = getSubRecordsByColumnName(record, "spatial_ref_value.name_value");
    	if (refRecords.size() > 0) {
	    	ArrayList lines = new ArrayList();
	    	for (int i=0; i<refRecords.size(); i++) {
	    		Record listRecord = (Record)refRecords.get(i);
	        	if (listRecord.getString("spatial_ref_value.type") != null && listRecord.getString("spatial_ref_value.type").equals("F") && listRecord.getString("spatial_ref_value.name_value") != null ) {
		    		HashMap line = new HashMap();
		        	line.put("type", "textLine");
		        	line.put("body", listRecord.getString("spatial_ref_value.name_value"));
		        	if (!isEmptyLine(line)) {
		        		lines.add(line);
		        	}
	        	}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("title", messages.getString("t019_coordinates.bezug"));
		    	element.put("elements", lines);
	    	    elements.add(element);
	    	}
    	}
    	
    	// vertical extend
    	if (UtilsVelocity.hasContent(record.getString("t01_object.vertical_extent_minimum")).booleanValue()
    			|| UtilsVelocity.hasContent(record.getString("t01_object.vertical_extent_maximum")).booleanValue()
    			|| UtilsVelocity.hasContent(record.getString("t01_object.vertical_extent_unit")).booleanValue()
    			|| UtilsVelocity.hasContent(record.getString("t01_object.vertical_extent_vdatum")).booleanValue()
    		) {
	    	HashMap element = new HashMap();
	    	element.put("type", "table");
	    	element.put("title", messages.getString("t01_object.vertical_extent"));
			ArrayList head = new ArrayList();
			head.add(messages.getString("t01_object.vertical_extent_maximum"));
			head.add(messages.getString("t01_object.vertical_extent_minimum"));
			head.add(messages.getString("t01_object.vertical_extent_unit"));
			head.add(messages.getString("t01_object.vertical_extent_vdatum"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
			ArrayList row = new ArrayList();
			row.add(notNull(record.getString("t01_object.vertical_extent_maximum")));
			row.add(notNull(record.getString("t01_object.vertical_extent_minimum")));
			row.add(notNull(record.getString("t01_object.vertical_extent_unit")));
			row.add(notNull(record.getString("t01_object.vertical_extent_vdatum")));
    		if (!isEmptyRow(row)) {
    			body.add(row);
    		}
	    	if (body.size() > 0) {
		    	elements.add(element);
		    	element = new HashMap();
		    	element.put("type", "space");
				elements.add(element);
	    	}
    	}
    	
    	this.addElementEntry(elements, record.getString("t01_object.loc_descr"), messages.getString("t01_object.loc_descr"));
	}

	
	private void addReferenceObjectClass1(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_geo.special_base");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntry(elements, sysCodeList.getName("525", refRecord.getString("t011_obj_geo.hierarchy_level")), messages.getString("t011_obj_geo.hierarchy_level"));
    		
    		List tableRecords = getSubRecordsByColumnName(record, "t011_obj_geo_symc.line");
    		if (tableRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "table");
    	    	element.put("title", messages.getString("t011_obj_geo_symc"));
    			ArrayList head = new ArrayList();
    			head.add(messages.getString("t011_obj_geo_symc.symbol_cat"));
    			head.add(messages.getString("t011_obj_geo_symc.symbol_date"));
    			head.add(messages.getString("t011_obj_geo_symc.edition"));
    			element.put("head", head);
    			ArrayList body = new ArrayList();
    			element.put("body", body);
    	    	for (int i=0; i<tableRecords.size(); i++) {
    	    		Record tableRecord = (Record)tableRecords.get(i);
    	    		ArrayList row = new ArrayList();
    	    		row.add(notNull(tableRecord.getString("t011_obj_geo_symc.symbol_cat_value")));
    	    		row.add(UtilsDate.convertDateString(notNull(tableRecord.getString("t011_obj_geo_symc.symbol_date")).trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
    	    		row.add(notNull(tableRecord.getString("t011_obj_geo_symc.edition")));
    	    		if (!isEmptyRow(row)) {
    	    			body.add(row);
    	    		}
    	    	}
    	    	if (body.size() > 0) {
    		    	elements.add(element);
    		    	element = new HashMap();
    		    	element.put("type", "space");
    				elements.add(element);
    	    	}
    		}    	

    		tableRecords = getSubRecordsByColumnName(record, "t011_obj_geo_keyc.line");
    		if (tableRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "table");
    	    	element.put("title", messages.getString("t011_obj_geo_keyc"));
    			ArrayList head = new ArrayList();
    			head.add(messages.getString("t011_obj_geo_keyc.subject_cat"));
    			head.add(messages.getString("t011_obj_geo_keyc.key_date"));
    			head.add(messages.getString("t011_obj_geo_keyc.edition"));
    			element.put("head", head);
    			ArrayList body = new ArrayList();
    			element.put("body", body);
    	    	for (int i=0; i<tableRecords.size(); i++) {
    	    		Record tableRecord = (Record)tableRecords.get(i);
    	    		ArrayList row = new ArrayList();
    	    		row.add(notNull(tableRecord.getString("t011_obj_geo_keyc.keyc_value")));
    	    		row.add(UtilsDate.convertDateString(notNull(tableRecord.getString("t011_obj_geo_keyc.key_date")).trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
    	    		row.add(notNull(tableRecord.getString("t011_obj_geo_keyc.edition")));
    	    		if (!isEmptyRow(row)) {
    	    			body.add(row);
    	    		}
    	    	}
    	    	if (body.size() > 0) {
    		    	elements.add(element);
    		    	element = new HashMap();
    		    	element.put("type", "space");
    				elements.add(element);
    	    	}
    		}
    		
    		boolean showVectorData = false;
    		List listRecords = getSubRecordsByColumnName(record, "t011_obj_geo_spatial_rep.line");
    		if (listRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "multiLine");
    	    	element.put("title", messages.getString("t011_obj_geo_spatial_rep.type"));
    	    	ArrayList lines = new ArrayList();
    	    	element.put("elements", lines);
    	    	for (int i=0; i<listRecords.size(); i++) {
    	    		Record listRecord = (Record)listRecords.get(i);
    	        	HashMap line = new HashMap();
    	        	line.put("type", "textLine");
    	        	String repType = listRecord.getString("t011_obj_geo_spatial_rep.type");
    	        	line.put("body", sysCodeList.getName("526", repType));
    	        	if (!isEmptyLine(line)) {
    	        		lines.add(line);
    	        	}
    	        	if (repType != null && repType.equals("1")) {
    	        		showVectorData = true;
    	        	}
    	    	}
        	    elements.add(element);
    		}
    		
    		if (showVectorData) {
        		addElementEntryInline(elements, sysCodeList.getName("528", refRecord.getString("t011_obj_geo.vector_topology_level")), messages.getString("t011_obj_geo.vector_topology_level"));
    			
        		tableRecords = getSubRecordsByColumnName(record, "t011_obj_geo_vector.line");
        		if (tableRecords.size() > 0) {
        	    	HashMap element = new HashMap();
        	    	element.put("type", "table");
        	    	element.put("title", messages.getString("t011_obj_geo_vector"));
        			ArrayList head = new ArrayList();
        			head.add(messages.getString("t011_obj_geo_vector.geometric_object_type"));
        			head.add(messages.getString("t011_obj_geo_vector.geometric_object_count"));
        			element.put("head", head);
        			ArrayList body = new ArrayList();
        			element.put("body", body);
        	    	for (int i=0; i<tableRecords.size(); i++) {
        	    		Record tableRecord = (Record)tableRecords.get(i);
        	    		ArrayList row = new ArrayList();
        	    		row.add(notNull(tableRecord.getString("t011_obj_geo_vector.geometric_object_type")));
        	    		row.add(notNull(tableRecord.getString("t011_obj_geo_vector.geometric_object_count")));
        	    		if (!isEmptyRow(row)) {
        	    			body.add(row);
        	    		}
        	    	}
        	    	if (body.size() > 0) {
        		    	elements.add(element);
        		    	element = new HashMap();
        		    	element.put("type", "space");
        				elements.add(element);
        	    	}
        		}
    		}
    		if (refRecord.getString("t011_obj_geo.rec_grade") != null) {
    			addElementEntryInline(elements, refRecord.getString("t011_obj_geo.rec_grade").concat(" %"), messages.getString("t011_obj_geo.rec_grade"));
    		}
    		String referenceSystem = (refRecord.getString("t011_obj_geo.referencesystem_value") != null && refRecord.getString("t011_obj_geo.referencesystem_value").length() > 0) ? refRecord.getString("t011_obj_geo.referencesystem_value") : sysCodeList.getName("100", refRecord.getString("t011_obj_geo.referencesystem_key"));
    		addElementEntryInline(elements, referenceSystem, messages.getString("t011_obj_geo.referencesystem_id"));
    		
    		tableRecords = getSubRecordsByColumnName(record, "t011_obj_geo_scale.line");
    		if (tableRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "table");
    	    	element.put("title", messages.getString("t011_obj_geo_scale"));
    			ArrayList head = new ArrayList();
    			head.add(messages.getString("t011_obj_geo_scale.scale").concat(" 1:x"));
    			head.add(messages.getString("t011_obj_geo_scale.resolution_ground").concat(" m"));
    			head.add(messages.getString("t011_obj_geo_scale.resolution_scan").concat(" dpi"));
    			element.put("head", head);
    			ArrayList body = new ArrayList();
    			element.put("body", body);
    	    	for (int i=0; i<tableRecords.size(); i++) {
    	    		Record tableRecord = (Record)tableRecords.get(i);
    	    		ArrayList row = new ArrayList();
    	    		row.add(notNull(tableRecord.getString("t011_obj_geo_scale.scale")));
    	    		row.add(notNull(tableRecord.getString("t011_obj_geo_scale.resolution_ground")));
    	    		row.add(notNull(tableRecord.getString("t011_obj_geo_scale.resolution_scan")));
    	    		if (!isEmptyRow(row)) {
    	    			body.add(row);
    	    		}
    	    	}
    	    	if (body.size() > 0) {
    		    	elements.add(element);
    		    	element = new HashMap();
    		    	element.put("type", "space");
    				elements.add(element);
    	    	}
    		}    		
    		if (refRecord.getString("t011_obj_geo.pos_accuracy_vertical") != null) {
    			addElementEntry(elements, refRecord.getString("t011_obj_geo.pos_accuracy_vertical").concat(" m"), messages.getString("t011_obj_geo.pos_accuracy_vertical"));
    		}
    		if (refRecord.getString("t011_obj_geo.rec_exact") != null) {
    			addElementEntry(elements, refRecord.getString("t011_obj_geo.rec_exact").concat(" %"), messages.getString("t011_obj_geo.rec_exact"));
    		}
    		if (refRecord.getString("t011_obj_geo.special_base") != null) {
    			addElementEntry(elements, refRecord.getString("t011_obj_geo.special_base"), messages.getString("t011_obj_geo.special_base"));
    		}
    		if (refRecord.getString("t011_obj_geo.data_base") != null) {
    			addElementEntry(elements, refRecord.getString("t011_obj_geo.data_base"), messages.getString("t011_obj_geo.data_base"));
    		}
    		
    		listRecords = getSubRecordsByColumnName(record, "t011_obj_geo_supplinfo.line");
    		if (listRecords.size() > 0 && !(listRecords.size() == 1 && ((Record)(listRecords.get(0))).getString("t011_obj_geo_supplinfo.feature_type") == null)) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "multiLine");
    	    	element.put("title", messages.getString("t011_obj_geo_supplinfo.feature_type"));
    	    	ArrayList lines = new ArrayList();
    	    	element.put("elements", lines);
    	    	for (int i=0; i<listRecords.size(); i++) {
    	    		Record listRecord = (Record)listRecords.get(i);
    	        	HashMap line = new HashMap();
    	        	line.put("type", "textLine");
    	        	line.put("body", listRecord.getString("t011_obj_geo_supplinfo.feature_type"));
    	        	if (!isEmptyLine(line)) {
    	        		lines.add(line);
    	        	}
    	    	}
        	    elements.add(element);
    		}
    		addElementEntry(elements, refRecord.getString("t011_obj_geo.method"), messages.getString("t011_obj_geo.method"));
    	}
	}	

	
	private void addReferenceObjectClass4(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_project.leader");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntry(elements, refRecord.getString("t011_obj_project.leader"), messages.getString("t011_obj_project.leader"));
    		addElementEntry(elements, refRecord.getString("t011_obj_project.member"), messages.getString("t011_obj_project.member"));
    		addElementEntry(elements, refRecord.getString("t011_obj_project.description"), messages.getString("t011_obj_project.description"));
    	}
	}	
	
	private void addReferenceObjectClass2(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_literatur.publisher");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntry(elements, refRecord.getString("t011_obj_literature.author"), messages.getString("t011_obj_literatur.autor"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.publisher"), messages.getString("t011_obj_literatur.publisher"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.publish_in"), messages.getString("t011_obj_literatur.publish_in"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.volume"), messages.getString("t011_obj_literatur.volume"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.sides"), messages.getString("t011_obj_literatur.sides"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.publish_year"), messages.getString("t011_obj_literatur.publish_year"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.isbn"), messages.getString("t011_obj_literatur.isbn"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.publishing"), messages.getString("t011_obj_literatur.publishing"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.publish_loc"), messages.getString("t011_obj_literatur.publish_loc"));
    		addElementEntryInline(elements, refRecord.getString("t011_obj_literature.type_value"), messages.getString("t011_obj_literatur.typ"));
    		addElementEntry(elements, refRecord.getString("t011_obj_literature.doc_info"), messages.getString("t011_obj_literatur.doc_info"));
    		addElementEntry(elements, refRecord.getString("t011_obj_literature.loc"), messages.getString("t011_obj_literatur.loc"));
    		addElementEntry(elements, refRecord.getString("t011_obj_literature.description"), messages.getString("t011_obj_literatur.description"));
    	}
	}
	
	
	private void addReferenceObjectClass3(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_serv.description");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntry(elements, refRecord.getString("t011_obj_serv.type_value"), messages.getString("t011_obj_serv.type"));
    		String serviceType = refRecord.getString("t011_obj_serv.type_value");
    		addElementEntry(elements, refRecord.getString("t011_obj_serv.environment"), messages.getString("t011_obj_serv.environment"));
    		addElementEntry(elements, refRecord.getString("t011_obj_serv.history"), messages.getString("t011_obj_serv.history"));
    		addElementEntry(elements, refRecord.getString("t011_obj_serv.base"), messages.getString("t011_obj_serv.base"));
    		refRecords = getSubRecordsByColumnName(record, "t011_obj_serv_version.line");
    		if (refRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "multiLine");
    	    	element.put("title", messages.getString("t011_obj_serv_version.version"));
    	    	ArrayList lines = new ArrayList();
    	    	element.put("elements", lines);
    	    	for (int i=0; i<refRecords.size(); i++) {
    	    		refRecord = (Record)refRecords.get(i);
    	        	HashMap line = new HashMap();
    	        	line.put("type", "textLine");
    	        	line.put("body", refRecord.getString("t011_obj_serv_version.serv_version"));
    	        	if (!isEmptyLine(line)) {
    	        		lines.add(line);
    	        	}
    	    	}
        	    elements.add(element);
    		}
    		ArrayList<String> wmsServiceLinks = new ArrayList<String>();
    		refRecords = getSubRecordsByColumnName(record, "t011_obj_serv_operation.line");
    		if (refRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "table");
    	    	element.put("title", messages.getString("t011_obj_serv_operation"));
    			ArrayList head = new ArrayList();
    			head.add(messages.getString("t011_obj_serv_operation.name"));
    			head.add(messages.getString("t011_obj_serv_operation.descr"));
    			head.add(messages.getString("t011_obj_serv_operation.invocation_name"));
    			element.put("head", head);
    			ArrayList body = new ArrayList();
    			element.put("body", body);
    	    	for (int i=0; i<refRecords.size(); i++) {
    	    		refRecord = (Record)refRecords.get(i);
    	    		ArrayList row = new ArrayList();
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_operation.name_value")));
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_operation.descr")));
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_operation.invocation_name")));
    	    		List serviceLinkRecords = getSubRecordsByColumnName(refRecord, "t011_obj_serv_op_connpoint.line");
    	    		// only add getCap urls of WMS services
    	    		if (serviceType.equalsIgnoreCase("wms") && serviceLinkRecords.size() > 0 && refRecord.getString("t011_obj_serv_operation.name_value") != null && refRecord.getString("t011_obj_serv_operation.name_value").toLowerCase().equals("getcapabilities")) {
    	    	    	for (int j=0; j<serviceLinkRecords.size(); j++) {
    	    	    		wmsServiceLinks.add(((Record)serviceLinkRecords.get(i)).getString("t011_obj_serv_op_connpoint.connect_point"));
    	    	    	}
    	    		}
    	    		if (!isEmptyRow(row)) {
    	    			body.add(row);
    	    		}
    	    	}
    	    	if (body.size() > 0) {
    		    	elements.add(element);
    		    	element = new HashMap();
    		    	element.put("type", "space");
    				elements.add(element);
    	    	}

    		}
    		if (wmsServiceLinks.size() > 0) {
    	    	ArrayList linkList = new ArrayList();
    			HashMap element = new HashMap();
	        	element.put("type", "linkList");
	        	element.put("linkList", linkList);
	        	elements.add(element);
    	    	for (int i=0; i<wmsServiceLinks.size(); i++) {
    	        	if (UtilsVelocity.hasContent(wmsServiceLinks.get(i)).booleanValue()) {
	    	    		HashMap link = new HashMap();
	    	        	link.put("hasLinkIcon", new Boolean(true));
	    	        	link.put("isExtern", new Boolean(false));
	    	        	link.put("title", messages.getString("common.result.showMap"));
	    	        	link.put("href", "portal/main-maps.psml?wms_url=" + UtilsVelocity.urlencode(wmsServiceLinks.get(i).trim()));
	    	        	linkList.add(link);
    	        	}
    	    	}
    		}
    	}
	}
	
	
	private void addReferenceObjectClass5(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_data_para.line");
    	if (refRecords.size() > 0) {
			HashMap element = new HashMap();
			element.put("type", "table");
			element.put("title", messages.getString("t011_obj_data_para"));
			// add table headers
			ArrayList head = new ArrayList();
			head.add(messages.getString("t011_obj_data_para.parameter"));
			head.add(messages.getString("t011_obj_data_para.unit"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
	    	for (int i=0; i<refRecords.size(); i++) {
	    		Record refRecord = (Record)refRecords.get(i);
	    		ArrayList row = new ArrayList();
	    		row.add(notNull(refRecord.getString("t011_obj_data_para.parameter")));
	    		row.add(notNull(refRecord.getString("t011_obj_data_para.unit")));
	    		if (!isEmptyRow(row)) {
	    			body.add(row);
	    		}
	    	}
	    	if (body.size() > 0) {
		    	elements.add(element);
		    	element = new HashMap();
		    	element.put("type", "space");
				elements.add(element);
	    	}
	    	
    	}
    	refRecords = getSubRecordsByColumnName(record, "t011_obj_data.base");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntry(elements, refRecord.getString("t011_obj_data.base"), messages.getString("t011_obj_data.base"));
    		addElementEntry(elements, refRecord.getString("t011_obj_data.description"), messages.getString("t011_obj_data.description"));
    	}
	}
	
	
	
	private void addElementEntry(List elements, String body, String title) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "entry");
			element.put("title", title);
			element.put("body", body);
			elements.add(element);
		}
	}

	private void addElementEntryInline(List elements, String body, String title) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "entryInline");
			element.put("title", title);
			element.put("body", body);
			elements.add(element);
		}
	}
	
	private void addElementUdkClass(List elements, String udkClass) {
		if (UtilsVelocity.hasContent(udkClass).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "udkClass");
			element.put("udkClass", udkClass);
			element.put("udkClassName", messages.getString("udk_obj_class_name_".concat(udkClass)));
			elements.add(element);
		}
	}
	
    private void addSuperiorObjects(List elements, String objUuid) {
        List linkList = getLinkListOfObjectsFromQuery("children.object_node.obj_uuid:".concat(objUuid).concat(
                " iplugs:\"").concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\""));
        if (!linkList.isEmpty()) {
        	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("superior_references"));
        	element.put("linkList", linkList);
        	elements.add(element);
        }
    }

    private void addSubordinatedObjects(List elements, String objUuid) {
        List linkList = getLinkListOfObjectsFromQuery("parent.object_node.obj_uuid:".concat(objUuid).concat(
                " iplugs:\"").concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\""));
        if (!linkList.isEmpty()) {
        	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("subordinated_references"));
        	element.put("linkList", linkList);
        	elements.add(element);
        }
    }

    private void addCrossReferencedObjects(List elements, String objId) {
    	List linkList = getLinkListOfObjectsFromQuery("object_reference.obj_from_id:".concat(objId).concat(
                " iplugs:\"").concat(iPlugId).concat("\""));
        if (!linkList.isEmpty()) {
        	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("cross_references"));
        	element.put("linkList", linkList);
        	elements.add(element);
        }
    }
    
    private void addUrlReferences(List elements, Record record) {
        ArrayList linkList = new ArrayList();
    	List refRecords = getSubRecordsByColumnName(record, "t017_url_ref.line");
    	for (int i=0; i<refRecords.size(); i++) {
    		Record refRecord = (Record)refRecords.get(i);
        	HashMap link = new HashMap();
        	link.put("hasLinkIcon", new Boolean(true));
        	link.put("isExtern", new Boolean(true));
        	link.put("title", refRecord.getString("t017_url_ref.content"));
        	link.put("href", refRecord.getString("t017_url_ref.url_link"));
        	linkList.add(link);
    	}
        if (!linkList.isEmpty()) {
        	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("www_references"));
        	element.put("linkList", linkList);
        	elements.add(element);
        }
    }
    
    private void addAddresses(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t012_obj_adr.adr_id");
    	for (int i=0; i<refRecords.size(); i++) {
    		Record refRecord = (Record)refRecords.get(i);
    		addAddressType(elements, refRecord);
    	}
    }
    
    private void addAddressType(List elements, Record record) {
    	HashMap element = new HashMap();
    	element.put("type", "multiLine");
    	element.put("title", record.getString("t012_obj_adr.special_name"));
    	element.put("elements", new ArrayList());
    	elements.add(element);
    	
    	List refRecords = getSubRecordsByColumnName(record, "parent.address_node.addr_uuid");
    	for (int i=0; i<refRecords.size(); i++) {
    		Record refRecord = (Record)refRecords.get(i);
    		addAddress((List)(element.get("elements")), refRecord);
    	}
    }

    private void addAddress(List elements, Record record) {
    	HashMap element = new HashMap();
    	List refRecords = getSubRecordsByColumnName(record, "t02_address.adr_id");
    	Record addrRecord = null;
    	if (refRecords.size() > 0) {
    		addrRecord = (Record)refRecords.get(0);
    	} else {
    		if (log.isDebugEnabled()) {
    			log.debug("No published t02_address entry for existing address_node. Check mapping!");
    		}
    		return;
    	}
		
    	
    	// address type
    	int addressType = -1;
		try {
			addressType = Integer.parseInt(addrRecord.getString("t02_address.adr_type"));
		} catch (NumberFormatException e) {
			log.debug("Illegal address classification (institution, unit, ...) found: " + addrRecord.getString("t02_address.adr_type"));
		}

    	// get address parents
    	// TODO refactor common functions to base class
		if (addressType == 1 || addressType == 2) {
        	ArrayList addressParents = new ArrayList();
        	IngridHit hit = getAddress(record.getString("address_node.fk_addr_uuid"));
			while (hit != null) {
		    	element = new HashMap();
		    	element.put("type", "linkLine");
		    	element.put("hasLinkIcon", new Boolean(false));
		    	element.put("isExtern", new Boolean(false));
				element.put("title", ((IngridHitDetail)hit.get("detail")).getString("title"));
				PortletURL actionUrl = response.createActionURL();
		    	actionUrl.setParameter("cmd", "doShowAddressDetail");
				actionUrl.setParameter("addrId", ((IngridHitDetail)hit.get("detail")).getString("T02_address.adr_id"));
				actionUrl.setParameter("plugid", iPlugId);
				element.put("href", actionUrl.toString());
				addressParents.add(element);
        		if (UtilsVelocity.hasContent(((IngridHitDetail)hit.get("detail")).getString("parent.address_node.addr_uuid")).booleanValue()
        				&& !((IngridHitDetail)hit.get("detail")).getString(Settings.HIT_KEY_ADDRESS_CLASS).equals("0")) {
        			hit = getAddress(((IngridHitDetail)hit.get("detail")).getString("parent.address_node.addr_uuid"));
        		} else {
        			hit = null;
        		}
			}
            	
        	if (addressParents.size() > 0) {
        		for (int i=addressParents.size()-1;i>=0; i--) {
        			elements.add(addressParents.get(i));
        		}
        	}        		
    	}		
		
    	// generate action url to show address detail
    	PortletURL actionUrl = response.createActionURL();
    	actionUrl.setParameter("cmd", "doShowAddressDetail");
		actionUrl.setParameter("addrId", addrRecord.getString("t02_address.adr_uuid"));
		actionUrl.setParameter("plugid", iPlugId);
		
		if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.institution")).booleanValue()) {
			element = new HashMap();
			if (addressType == 0 || addressType == 3 || addressType == 1) {
		    	element.put("type", "linkLine");
		    	element.put("hasLinkIcon", new Boolean(false));
		    	element.put("isExtern", new Boolean(false));
				element.put("title", addrRecord.getString("t02_address.institution"));
				element.put("href", actionUrl.toString());
	    	} else {
		    	element.put("type", "textLine");
				element.put("body", addrRecord.getString("t02_address.institution"));
	    	}
			elements.add(element);
		}
    	// address, title, name with link
		if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.lastname")).booleanValue()) {
	    	String textLine = "";
	    	if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.address_value")).booleanValue()) {
	    		textLine = textLine.concat(addrRecord.getString("t02_address.address_value"));
	    	}
	    	if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.title_value")).booleanValue()) {
	    		if (textLine.length() > 0) {
	    			textLine = textLine.concat(" ");
	    		}
	    		textLine = textLine.concat(addrRecord.getString("t02_address.title_value"));
	    	}
	    	if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.firstname")).booleanValue()) {
	    		if (textLine.length() > 0) {
	    			textLine = textLine.concat(" ");
	    		}
	    		textLine = textLine.concat(addrRecord.getString("t02_address.firstname"));
	    	}
	    	if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.lastname")).booleanValue()) {
	    		if (textLine.length() > 0) {
	    			textLine = textLine.concat(" ");
	    		}
	    		textLine = textLine.concat(addrRecord.getString("t02_address.lastname"));
	    	}
			
	    	element = new HashMap();
	    	element.put("type", "linkLine");
	    	element.put("hasLinkIcon", new Boolean(false));
	    	element.put("isExtern", new Boolean(false));
			element.put("title", textLine);
			element.put("href", actionUrl.toString());
			elements.add(element);
		}
		// description
		if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.descr")).booleanValue()) {
	    	element = new HashMap();
	    	element.put("type", "textLine");
			element.put("body", addrRecord.getString("t02_address.descr"));
			elements.add(element);
		}
		// post box information
		if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.postbox")).booleanValue()) {
	    	element = new HashMap();
	    	element.put("type", "textLine");
			element.put("body", messages.getString("postbox_label").concat(" ").concat(addrRecord.getString("t02_address.postbox")));
			elements.add(element);
			if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.postbox_pc")).booleanValue()) {
				String textLine = addrRecord.getString("t02_address.postbox_pc");
				if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.country_code")).booleanValue()) {
					textLine = messages.getString("postal.country.code.".concat(addrRecord.getString("t02_address.country_code"))).concat("-").concat(textLine);
				}
				if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.city")).booleanValue()) {
					textLine = textLine.concat(" ").concat(addrRecord.getString("t02_address.city"));
				}
				element = new HashMap();
				element.put("type", "textLine");
				element.put("body", textLine);
				elements.add(element);
			}
	    	element = new HashMap();
	    	element.put("type", "space");
			elements.add(element);
			
		}
		if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.street")).booleanValue()) {
			if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.street")).booleanValue()) {
				element = new HashMap();
				element.put("type", "textLine");
				element.put("body", addrRecord.getString("t02_address.street"));
				elements.add(element);
			}
			if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.postcode")).booleanValue()) {
				String textLine = addrRecord.getString("t02_address.postcode");
				if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.country_code")).booleanValue()) {
					textLine = messages.getString("postal.country.code.".concat(addrRecord.getString("t02_address.country_code"))).concat("-").concat(textLine);
				}
				if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.city")).booleanValue()) {
					textLine = textLine.concat(" ").concat(addrRecord.getString("t02_address.city"));
				}
				element = new HashMap();
				element.put("type", "textLine");
				element.put("body", textLine);
				elements.add(element);
			}
	    	element = new HashMap();
	    	element.put("type", "space");
			elements.add(element);
		}
    	refRecords = getSubRecordsByColumnName(addrRecord, "t021_communication.comm_value");
    	for (int i=0; i<refRecords.size(); i++) {
    		Record refRecord = (Record)refRecords.get(i);
    		addCommunication(elements, refRecord);
    	}
    }

    
    private IngridHit getAddress(String addrUuid) {
        String[] requestedMetadata = new String[] {
        		// udk address metadata
        		Settings.HIT_KEY_ADDRESS_CLASS,
        		Settings.HIT_KEY_ADDRESS_FIRSTNAME,
        		Settings.HIT_KEY_ADDRESS_LASTNAME,
        		Settings.HIT_KEY_ADDRESS_TITLE,
        		"t02_address.address_value",
        		Settings.HIT_KEY_ADDRESS_ADDRID,
        		"parent.address_node.addr_uuid"
        };
        ArrayList result = DetailDataPreparerHelper.getHits("t02_address.adr_id:".concat(addrUuid).concat(
        " iplugs:\"").concat(DetailDataPreparerHelper.getAddressPlugIdFromPlugId(iPlugId)).concat("\""), requestedMetadata, null);
        if (result.size() > 0) {
        	return (IngridHit) result.get(0);
        } else {
        	return null;
        }
    }
    
    
    private void addCommunication(List elements, Record record) {
    	HashMap element = new HashMap();
		String textLine = record.getString("t021_communication.comm_value");
		String type = null;
		if (UtilsVelocity.hasContent(record.getString("t021_communication.commtype_value")).booleanValue()) {
			type = record.getString("t021_communication.commtype_value");
			if (textLine != null) {
				element.put("title", record.getString("t021_communication.commtype_value").concat(":"));
			}
		}
		
		if (type == null) {
			element.put("type", "textLine");
			element.put("body", textLine);
		} else if (type.equalsIgnoreCase("email") || type.equalsIgnoreCase("e-mail")) {
			element.put("type", "textLinkLine");
			element.put("href", "mailto:".concat(UtilsString.htmlescapeAll(textLine)));
			element.put("body", UtilsString.htmlescapeAll(textLine));
			element.put("altText", UtilsString.htmlescapeAll(textLine));
		} else if (type.equalsIgnoreCase("www") || type.equalsIgnoreCase("url")) {
			element.put("type", "textLinkLine");
			if (textLine.startsWith("http")) {
				element.put("href", textLine);
			} else {
				element.put("href", "http://".concat(textLine));
			}
			element.put("body", textLine);
			element.put("altText", textLine);
		} else {
			element.put("type", "textLine");
			element.put("body", textLine);
		}
    	elements.add(element);
    }
    
    private void addLine(List elements) {
    	HashMap element = new HashMap();
		element.put("type", "line");
		elements.add(element);
    }
    
    
    private List getLinkListOfObjectsFromQuery(String queryStr) {
        ArrayList result = DetailDataPreparerHelper.getHits(queryStr, new String[] {Settings.HIT_KEY_OBJ_ID}, null);
        ArrayList linkList = new ArrayList();
        for (int i=0; i<result.size(); i++) {
        	IngridHit hit = (IngridHit)result.get(i);
        	HashMap link = new HashMap();
        	link.put("hasLinkIcon", new Boolean(true));
        	link.put("isExtern", new Boolean(false));
        	link.put("title", ((HashMap)hit.get("detail")).get("title"));
        	PortletURL actionUrl = response.createActionURL();
        	actionUrl.setParameter("cmd", "doShowDocument");
    		actionUrl.setParameter("docid", hit.getId().toString());
    		actionUrl.setParameter("docuuid", (String)((HashMap)hit.get("detail")).get(Settings.HIT_KEY_OBJ_ID));
    		actionUrl.setParameter("plugid", iPlugId);
    		if (hit.getString("alt_document_id") != null) {
    			actionUrl.setParameter("altdocid", hit.getString("alt_document_id"));
    		}
        	link.put("href", actionUrl.toString());
        	linkList.add(link);
        }
    	return linkList;
    }
    
    
    /**
     * Get all subrecords that contain a given column name.
     * 
     * @param record The record to examine.
     * @param columnName The column name.
     * @return A List of records.
     */
    private List getSubRecordsByColumnName(Record record, String columnName) {
    	ArrayList result = new ArrayList();
        Record[] subRecords = record.getSubRecords();
        for (int i = 0; i < subRecords.length; i++) {
        	Record subRecord = subRecords[i];
        	Column[] columns =  subRecord.getColumns();
            for (int j = 0; j < columns.length; j++) {
            	Column column =  columns[j];
            	if (column.getTargetName().equalsIgnoreCase(columnName)) {
            		result.add(subRecord);
            		break;
            	}
            }
            result.addAll(getSubRecordsByColumnName(subRecord, columnName));
        }
        return result;
    }
    
    private boolean isEmptyRow(List row) {
    	for (int i=0; i<row.size(); i++) {
    		if (row.get(i) != null && row.get(i) instanceof String && ((String)row.get(i)).length() > 0) {
    			return false;
    		}
    	}
    	return true;
    }

    private boolean isEmptyLine(HashMap line) {
    	if (line.get("type") != null && line.get("type").equals("textLine")) {
        	if (line.get("body") != null && line.get("body") instanceof String && ((String)line.get("body")).length() > 0) {
        		return false;
        	}
    	}
    	return true;
    }
    
    
    private String notNull(String in) {
    	if (in == null) {
    		return "";
    	} else {
    		return in;
    	}
    }
    
	
}
