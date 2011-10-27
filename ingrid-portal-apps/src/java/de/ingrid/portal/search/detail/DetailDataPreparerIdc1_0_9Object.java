/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.udk.UtilsDate;

/**
 * - split object_access and object_use (no table, instead multiple lines).
 */
//TODO: remove annotation
@SuppressWarnings("unchecked")
public class DetailDataPreparerIdc1_0_9Object implements DetailDataPreparer {

    private final static Logger log = LoggerFactory.getLogger(DetailDataPreparerIdc1_0_9Object.class);
	
	private Context context;
	private String iPlugId;
	private RenderRequest request;
	private RenderResponse response;
	private IngridResourceBundle messages;
	private IngridSysCodeList sysCodeList;
	
	public DetailDataPreparerIdc1_0_9Object(Context context, String iPlugId, RenderRequest request, RenderResponse response) {
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
		general.put("modTime", UtilsDate.convertDateString(notNull(record.getString("t01_object.mod_time")).trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
		String objClassStr = record.getString("t01_object.obj_class");
		if (objClassStr != null) {
			general.put("udkObjClass", objClassStr);
			general.put("udkObjClassName", messages.getString("udk_obj_class_name_".concat(objClassStr)));
		}

		data.put("general", general);
		
		ArrayList elementsGeneral = new ArrayList();
		ArrayList elementsReference = new ArrayList();
		ArrayList elementsAreaTime = new ArrayList();
		ArrayList elementsSubject = new ArrayList();
		ArrayList elementsAvailability = new ArrayList();
		ArrayList elementsAdditionalInfo = new ArrayList();
		
		// alternate name
		String alternateName = record.getString("t01_object.dataset_alternate_name");
		// udk class
		addElementUdkClass(elementsGeneral, record.getString("t01_object.obj_class"));
		// description
        String description = record.getString("t01_object.obj_descr");
		if (description != null) {
			description = description.replaceAll("\n", "<br/>");
			description = description.replaceAll("<(?!b>|/b>|i>|/i>|u>|/u>|p>|/p>|br>|br/>|br />|strong>|/strong>|ul>|/ul>|ol>|/ol>|li>|/li>)[^>]*>", "");
        }

// tab "General"
		List listSuperiorObjects = getLinkListOfObjectsFromQuery("children.object_node.obj_uuid:".concat(record.getString("t01_object.obj_uuid")).concat(" iplugs:\"").concat(
				DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\"")); 
		List listSubordinatedObjects = getLinkListOfObjectsFromQuery("parent.object_node.obj_uuid:".concat(record.getString("t01_object.obj_uuid")).concat(" iplugs:\"")
				.concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\""));

		if((description != null && description.length() > 0) || alternateName != null || listSuperiorObjects.size() > 0 || listSubordinatedObjects.size() > 0){
			addSectionTitle(elementsGeneral, messages.getString("detail_description"));
			addElementEntryLabelAbove(elementsGeneral, description, alternateName, false);
			// superior objects
			addSuperiorObjects(elementsGeneral, listSuperiorObjects);
			// subordinated objects
			addSubordinatedObjects(elementsGeneral, listSubordinatedObjects);
			// close description
			closeDiv(elementsGeneral);
		}
		
		// addresses
		addAddresses(elementsGeneral, record);

// tab "Reference"
		HashMap crossReferencedMap = addCrossReferencedObjects(elementsReference, record.getString("t01_object.id"));
		
		if(getSubRecordsByColumnName(record, "t017_url_ref.line").size() > 0 || crossReferencedMap != null){
			addSectionTitle(elementsReference, messages.getString("references"));	
			// cross referenced objects
			if(crossReferencedMap != null){
				elementsReference.add(crossReferencedMap);
			}
			
			//	URL references
			addUrlReferences(elementsReference, record);
			closeDiv(elementsReference);
		}	
		
		// add index information
		addIndexInformation(elementsReference, record);
		
// tab "Subject"
		if(getSubRecordsByColumnName(record, "t011_obj_data_para.line").size() > 0 || getSubRecordsByColumnName(record, "t011_obj_serv.type").size() > 0
				|| getSubRecordsByColumnName(record, "t011_obj_literatur.publisher").size() > 0 || getSubRecordsByColumnName(record, "t011_obj_project.leader").size() > 0 
				|| getSubRecordsByColumnName(record, "t011_obj_geo.special_base").size() > 0 || getSubRecordsByColumnName(record, "t011_obj_serv_version.line").size() >0 
				|| getSubRecordsByColumnName(record, "t011_obj_serv_url.line").size() > 0){
			
			addSectionTitle(elementsSubject, messages.getString("subject_reference"));
    		// add subject data
			if (objClassStr.equals("5")) {
				addReferenceObjectClass5(elementsSubject, record);
			} else if (objClassStr.equals("3")) {
				addReferenceObjectClass3(elementsSubject, record);
			} else if (objClassStr.equals("6")) {
				addReferenceObjectClass6(elementsSubject, record);
			} else if (objClassStr.equals("2")) {
				addReferenceObjectClass2(elementsSubject, record);
			} else if (objClassStr.equals("4")) {
				addReferenceObjectClass4(elementsSubject, record);
			} else if (objClassStr.equals("1")) {
				addReferenceObjectClass1(elementsSubject, record);
			}
			closeDiv(elementsSubject);
		}
		
// tab "Area/Time"
		// add spatial reference data
		addSpatialReference(elementsAreaTime, record);
		
		// add time reference data
		addTimeReference(elementsAreaTime, record);

// tab "Availability"
		// add availability information
		addAvailabilityInformation(elementsAvailability, record);
		
		// add additional information
		addAdditionalInformation(elementsAdditionalInfo, record);

		// additional fields
		List tableRecords = getSubRecordsByColumnName(record, "t08_attr.data");
		if (tableRecords.size() > 0) {
	    	HashMap element = new HashMap();
	    	element.put("type", "table");
	    	element.put("title", messages.getString("additional_fields"));
			ArrayList body = new ArrayList();
			element.put("body", body);
			
			for (int i=0; i<tableRecords.size(); i++) {
				Record tableRecord = (Record)tableRecords.get(i);
				Record attrType = (Record)getSubRecordsByColumnName(tableRecord, "t08_attr_type.name").get(0);

				ArrayList row = new ArrayList();
	    		row.add(notNull(attrType.getString("t08_attr_type.name")));
	    		row.add(notNull(tableRecord.getString("t08_attr.data")));
	    		if (!isEmptyRow(row)) {
	    			body.add(row);
	    		}
			}
			
			if (body.size() > 0) {
				elementsAdditionalInfo.add(element);
	    	}
		}
		data.put("elementsGeneral", elementsGeneral);
		data.put("elementsReference", elementsReference);
		data.put("elementsAreaTime", elementsAreaTime);
		data.put("elementsSubject", elementsSubject);
		data.put("elementsAvailability", elementsAvailability);
		data.put("elementsAdditionalInfo", elementsAdditionalInfo);
		
		context.put("data", data);
	}
	
	
	private void addObjectAccess(List elements, Record record) {
		List listRecords = getSubRecordsByColumnName(record, "object_access.line");
		if (listRecords.size() > 0) {
			ArrayList lines = new ArrayList();
	    	for (int i=0; i<listRecords.size(); i++) {
	    		Record listRecord = (Record)listRecords.get(i);
	    		HashMap line = new HashMap();
	        	line.put("type", "textLine");
	        	line.put("body", listRecord.getString("object_access.restriction_value"));
	        	if (!isEmptyLine(line)) {
	        		lines.add(line);
	        	}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "textList");
		    	element.put("title", messages.getString("object_access.restriction_value"));
		    	element.put("textList", lines);
	    	    elements.add(element);
	    	}
		}
	}

	private void addObjectUse(List elements, Record record) {
		List listRecords = getSubRecordsByColumnName(record, "object_use.line");
		if (listRecords.size() > 0) {
			ArrayList lines = new ArrayList();
	    	for (int i=0; i<listRecords.size(); i++) {
	    		Record listRecord = (Record)listRecords.get(i);
	    		HashMap line = new HashMap();
	        	line.put("type", "textLine");
	        	String termsOfUse = listRecord.getString("object_use.terms_of_use");
	        	// may contain new lines due to "Altdatenübernahme"
	        	if (termsOfUse != null) {
	        		termsOfUse = termsOfUse.replaceAll("\n", "<br/>");	        		
	        	}
	        	line.put("body", termsOfUse);
	        	if (!isEmptyLine(line)) {
	        		lines.add(line);
	        	}
	    	}
	    	if (lines.size() > 0) {
		    	HashMap element = new HashMap();
		    	element.put("type", "textList");
		    	element.put("title", messages.getString("object_access.terms_of_use"));
		    	element.put("textList", lines);
	    	    elements.add(element);
	    	}
		}
	}

	private void addIndexInformation(List elements, Record record) {
		List listType = getSubRecordsByColumnName(record, "t04_search.type");
		List listObjTopicCat = getSubRecordsByColumnName(record, "t011_obj_topic_cat.line");
		List listEnvCategory = getSubRecordsByColumnName(record, "t0114_env_category.cat_value");
		List listEnvTopic = getSubRecordsByColumnName(record, "t0114_env_topic.topic_name");
    	
		if(listType.size() > 0 || listObjTopicCat.size() > 0 || listEnvCategory.size() > 0 
				|| listEnvTopic.size() > 0 ){
			addSectionTitle(elements, messages.getString("thesaurus"));
			// index references
	    	if (listType.size() > 0) {
	    	    ArrayList textListEntries = new ArrayList();
		    	ArrayList textListEntriesInspire = new ArrayList();
		    	for (int i=0; i<listType.size(); i++) {
		    		Record listRecord = (Record)listType.get(i);
		    		if (listRecord.getString("searchterm_value.type").equals("I")) {
		    			HashMap listEntry = new HashMap();
		    			listEntry.put("type", "textList");
		    			listEntry.put("body", listRecord.getString("searchterm_value.term"));
			        	if (!isEmptyList(listEntry)) {
			        		textListEntriesInspire.add(listEntry);
			        	}
		    		} else {
			    		HashMap listEntry = new HashMap();
			        	listEntry.put("type", "textList");
			        	listEntry.put("body", listRecord.getString("searchterm_value.term"));
			        	if (!isEmptyList(listEntry)) {
			        		textListEntries.add(listEntry);
			        	}
		    		}
		    	}
		    	if (textListEntries.size() > 0) {
			    	HashMap element = new HashMap();
			    	element.put("type", "textList");
			    	element.put("title", messages.getString("search_terms"));
			    	element.put("textList", textListEntries);
		    	    elements.add(element);
		    	}
		    	if (textListEntriesInspire.size() > 0) {
			    	HashMap element = new HashMap();
			    	element.put("type", "textList");
			    	element.put("title", messages.getString("inspire_themes"));
			    	element.put("textList", textListEntriesInspire);
		    	    elements.add(element);
		    	}
		    	
	    	}
	
	    	// topic categories
	    	if (listObjTopicCat.size() > 0) {
		    	ArrayList textListEntries = new ArrayList();
		    	for (int i=0; i<listObjTopicCat.size(); i++) {
		    		Record listRecord = (Record)listObjTopicCat.get(i);
		    		HashMap listEntry = new HashMap();
		    		listEntry.put("type", "textList");
		    		listEntry.put("body", sysCodeList.getName("527", listRecord.getString("t011_obj_topic_cat.topic_category")));
		        	if (!isEmptyList(listEntry)) {
		        		textListEntries.add(listEntry);
		        	}
		    	}
		    	if (textListEntries.size() > 0) {
			    	HashMap element = new HashMap();
			    	element.put("type", "textList");
			    	element.put("title", messages.getString("t011_obj_geo_topic_cat.topic_category"));
			    	element.put("textList", textListEntries);
		    	    elements.add(element);
		    	}
	    	}
	
	    	// environment categories
	    	if (listEnvCategory.size() > 0) {
	    		ArrayList textListEntries = new ArrayList();
		    	for (int i=0; i<listEnvCategory.size(); i++) {
		    		Record listRecord = (Record)listEnvCategory.get(i);
		    		String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
		            if (listRecord != null && listRecord.get("syslist.lang_id") != null && listRecord.get("syslist.lang_id").equals(lang)) {
			    		HashMap listEntry = new HashMap();
			        	listEntry.put("type", "textList");
			        	listEntry.put("body", listRecord.get("syslist.name"));
			        	if (!isEmptyList(listEntry)) {
			        		textListEntries.add(listEntry);
			        	}
		    		}
		    	}
		    	if (textListEntries.size() > 0) {
			    	HashMap element = new HashMap();
			    	element.put("type", "textList");
			    	element.put("title", messages.getString("t0114_env_category"));
			    	element.put("textList", textListEntries);
		    	    elements.add(element);
		    	}
	    	}
	    	
	    	// environment topics
	    	if (listEnvTopic.size() > 0) {
	    		ArrayList textListEntries = new ArrayList();
		    	for (int i=0; i<listEnvTopic.size(); i++) {
		    		Record listRecord = (Record)listEnvTopic.get(i);
		    		String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
		            if (listRecord != null && listRecord.get("syslist.lang_id") != null && listRecord.get("syslist.lang_id").equals(lang)) {
			    		HashMap listEntry = new HashMap();
			        	listEntry.put("type", "textList");
			        	listEntry.put("body", listRecord.get("syslist.name"));
			        	if (!isEmptyList(listEntry)) {
			        		textListEntries.add(listEntry);
			        	}
		    		}
		    	}
		    	if (textListEntries.size() > 0) {
			    	HashMap element = new HashMap();
			    	element.put("type", "textList");
			    	element.put("title", messages.getString("t0114_env_topic"));
			    	element.put("textList", textListEntries);
		    	    elements.add(element);
		    	}
	    	}
	    	closeDiv(elements);
		}	
	}

	private void addAdditionalInformation(List elements, Record record) {

		List listLegalReferences = getSubRecordsByColumnName(record, "t015_legist.name");
   		List listXMLRecords = getSubRecordsByColumnName(record, "t014_info_impart.impart_value");
   		List listConformity = getSubRecordsByColumnName(record, "object_conformity.line");
   		String metadataLanguage = record.getString("t01_object.metadata_language_value");
   		String dataLanguage = record.getString("t01_object.data_language_value");
   		String publishId = record.getString("t01_object.publish_id"); 
   		String  datasetUsage = record.getString("t01_object.dataset_usage");
   		String  infoNote = record.getString("t01_object.info_note");
   		
		if (listLegalReferences.size() > 0 || listXMLRecords.size() > 0 || UtilsVelocity.hasContent(metadataLanguage).booleanValue() || UtilsVelocity.hasContent(dataLanguage).booleanValue()
				|| UtilsVelocity.hasContent(datasetUsage).booleanValue() || UtilsVelocity.hasContent(infoNote).booleanValue() || UtilsVelocity.hasContent(publishId).booleanValue()
				|| (listConformity.size() > 0 && (record.getString("t01_object.obj_class").equals("3") || record.getString("t01_object.obj_class").equals("1")))) {
			
			addSectionTitle(elements, messages.getString("additional_information"));
			if (UtilsVelocity.hasContent(metadataLanguage).booleanValue()) {
				addElementEntryLabelLeft(elements, metadataLanguage, messages.getString("t01_object.metadata_language"));
			}
			if (UtilsVelocity.hasContent(dataLanguage).booleanValue()) {
				addElementEntryLabelLeft(elements, dataLanguage, messages.getString("t01_object.data_language"));
			}
			if (UtilsVelocity.hasContent(publishId).booleanValue()) {
				addElementEntryLabelLeft(elements, messages.getString("t01_object.publish_id_" + publishId), messages.getString("t01_object.publish_id"));
			}
		
	    	// legal references
	    	if (listLegalReferences.size() > 0) {
		    	ArrayList textListEntries = new ArrayList();
		    	for (int i=0; i<listLegalReferences.size(); i++) {
		    		Record listRecord = (Record)listLegalReferences.get(i);
		    		HashMap listEntry = new HashMap();
		        	listEntry.put("type", "textList");
		        	listEntry.put("body", listRecord.getString("t015_legist.legist_value"));
		        	if (!isEmptyList(listEntry)) {
		        		textListEntries.add(listEntry);
		        	}
		    	}
		    	if (textListEntries.size() > 0) {
			    	HashMap element = new HashMap();
			    	element.put("type", "textList");
			    	element.put("title", messages.getString("legal_basis"));
			    	element.put("textList", textListEntries);
		    	    elements.add(element);
		    	}
	    	}
	    	
	    	// XML-export-criteria
	        if (listXMLRecords.size() > 0) {
	            ArrayList textListEntries = new ArrayList();
	            for (int i=0; i<listXMLRecords.size(); i++) {
	                Record listRecord = (Record)listXMLRecords.get(i);
	                HashMap listEntry = new HashMap();
	                listEntry.put("type", "textList");
	                listEntry.put("body", listRecord.getString("t014_info_impart.impart_value"));
	                if (!isEmptyList(listEntry)) {
	                    textListEntries.add(listEntry);
	                }
	            }
	            if (textListEntries.size() > 0) {
	                HashMap element = new HashMap();
	                element.put("type", "textList");
	                element.put("title", messages.getString("t014_info_impart.name"));
	                element.put("textList", textListEntries);
	                elements.add(element);
	            }
	        }
	        
	        if (UtilsVelocity.hasContent(datasetUsage).booleanValue()) {
		   		addElementEntryLabelLeft(elements, datasetUsage, messages.getString("t01_object.dataset_usage"));
	        }
	        if (UtilsVelocity.hasContent(infoNote).booleanValue()) {
	        	addElementEntryLabelLeft(elements, infoNote, messages.getString("t01_object.info_note"));
	        }
	    	
	    	// add conformity information (new for INSPIRE)
			if (record.getString("t01_object.obj_class").equals("3") || record.getString("t01_object.obj_class").equals("1")) {
				if (listConformity.size() > 0) {
			    	HashMap element = new HashMap();
			    	element.put("type", "table");
			    	element.put("title", messages.getString("object_conformity"));
					ArrayList head = new ArrayList();
					head.add(messages.getString("object_conformity.specification"));
					head.add(messages.getString("object_conformity.degree_value"));
					head.add(messages.getString("object_conformity.publication_date"));
					element.put("head", head);
					ArrayList body = new ArrayList();
					element.put("body", body);
			    	for (int i=0; i<listConformity.size(); i++) {
			    		Record tableRecord = (Record)listConformity.get(i);
			    		ArrayList row = new ArrayList();
			    		row.add(notNull(tableRecord.getString("object_conformity.specification")));
			    		row.add(notNull(tableRecord.getString("object_conformity.degree_value")));
			    		// publication_date should be always there
			    		if (tableRecord.getString("object_conformity.publication_date") != null) {
			    			row.add(UtilsDate.convertDateString(tableRecord.getString("object_conformity.publication_date").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
			    		}
			    		if (!isEmptyRow(row)) {
			    			body.add(row);
			    		}
			    	}
			    	if (body.size() > 0) {
				    	elements.add(element);
			    	}
			    	
				}
			}
	    	closeDiv(elements);
   		}
	}
	
	
	private void addAvailabilityInformation(List elements, Record record) {
		List listAvailFormat = getSubRecordsByColumnName(record, "t0110_avail_format.line");
		List listMediaOption = getSubRecordsByColumnName(record, "t0112_media_option.line");
		List listObjectAccess = getSubRecordsByColumnName(record, "object_access.line");
		List listObjectUse = getSubRecordsByColumnName(record, "object_use.line");
		String orderingInstructions = record.getString("t01_object.ordering_instructions");
		// String availAccess = record.getString("t01_object.avail_access_note");
		// String fees = record.getString("t01_object.fees");
		
		if(listAvailFormat.size() > 0  || listMediaOption.size() > 0 || UtilsVelocity.hasContent(orderingInstructions).booleanValue()
				|| listObjectUse.size() > 0 || listObjectAccess.size() > 0){
		
			addSectionTitle(elements, messages.getString("availability"));
			if (listAvailFormat.size() > 0) {
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
		    	for (int i=0; i<listAvailFormat.size(); i++) {
		    		Record tableRecord = (Record)listAvailFormat.get(i);
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
		    	}
		    	
			}
	
			if (listMediaOption.size() > 0) {
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
		    	for (int i=0; i<listMediaOption.size(); i++) {
		    		Record tableRecord = (Record)listMediaOption.get(i);
		    		ArrayList row = new ArrayList();
		    		row.add(notNull(sysCodeList.getName("520", tableRecord.getString("t0112_media_option.medium_name"))));
		    		row.add(notNull(tableRecord.getString("t0112_media_option.transfer_size")));
		    		row.add(notNull(tableRecord.getString("t0112_media_option.medium_note")));
		    		if (!isEmptyRow(row)) {
		    			body.add(row);
		    		}
		    	}
		    	elements.add(element);
		    }
	   		addElementEntryLabelLeft(elements, orderingInstructions, messages.getString("t01_object.ordering_instructions"));
	//   		addElementEntry(elements, record.getString("t01_object.avail_access_note"), messages.getString("t01_object.avail_access_note"));
	//   		addElementEntry(elements, record.getString("t01_object.fees"), messages.getString("t01_object.fees"));
	   		
	   		// add object access information (new for INSPIRE)
			addObjectAccess(elements, record);
		
			// add object use information (new for INSPIRE)
			addObjectUse(elements, record);
			closeDiv(elements);
		}
	}

	
	private void addTimeReference(List elements, Record record) {
    	// time reference
		List refRecords = getSubRecordsByColumnName(record, "t01_object.id");
		List listRecords = getSubRecordsByColumnName(record, "t0113_dataset_reference.line");
		String timeAlle = record.getString("t01_object.time_alle");
		String timeDescr = record.getString("t01_object.time_descr");
		String timeInterval = record.getString("t01_object.time_interval");
		String timeStatus =  record.getString("t01_object.time_status");
		String timePeriod = record.getString("t01_object.time_period");
		
		if(refRecords.size() > 0 || listRecords.size() > 0 || UtilsVelocity.hasContent(timeDescr).booleanValue() 
				|| UtilsVelocity.hasContent(timeAlle).booleanValue() || UtilsVelocity.hasContent(timeInterval).booleanValue() 
				|| UtilsVelocity.hasContent(timeStatus).booleanValue() || UtilsVelocity.hasContent(timePeriod).booleanValue() ){
			
			addSectionTitle(elements, messages.getString("time_reference"));
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
		       	    		addElementEntryLabelLeft(elements, entryLine, messages.getString("time_reference_content"));
		       	    	}
		    		}
		    	}
	    	}
	    	
			addElementEntryLabelLeft(elements, sysCodeList.getName("523", timeStatus) , messages.getString("t01_object.time_status"));
			addElementEntryLabelLeft(elements, sysCodeList.getName("518", timePeriod), messages.getString("t01_object.time_period"));
	    	if (UtilsVelocity.hasContent(timeAlle).booleanValue() 
	    			&& UtilsVelocity.hasContent(timeInterval).booleanValue()) {
	        	String entryLine = timeAlle.concat(" ").concat(timeInterval);
	    		addElementEntryLabelLeft(elements, entryLine, messages.getString("t01_object.time_interval"));
	    	}
	    	
	    	// time references
	    	if (listRecords.size() > 0) {
		    	for (int i=0; i<listRecords.size(); i++) {
		    		Record listRecord = (Record)listRecords.get(i);
		    		addElementEntryLabelLeft(elements, UtilsDate.convertDateString(notNull(listRecord.getString("t0113_dataset_reference.reference_date")).trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"), sysCodeList.getName("502", listRecord.getString("t0113_dataset_reference.type")));
		    	}
		    }
	    	addElementEntryLabelLeft(elements, timeDescr, messages.getString("t01_object.time_descr"));
	    	closeDiv(elements);
		}
	}

	
	private void addSpatialReference(List elements, Record record) {
    	// geo thesaurus references
		List refLocationRecords = getSubRecordsByColumnName(record, "spatial_ref_value.name_value");
		if(refLocationRecords == null || refLocationRecords.size() < 1 ){
			refLocationRecords = getSubRecordsByColumnName(record, "location");
		}
		
		String locDescr = record.getString("t01_object.loc_descr");
		String extentMinimum = record.getString("t01_object.vertical_extent_minimum");
		String extentMaximum = record.getString("t01_object.vertical_extent_maximum");
		String extentUnit = record.getString("t01_object.vertical_extent_unit");
		String extentVDatum = record.getString("t01_object.vertical_extent_vdatum");
		
		if (refLocationRecords.size() > 0 || UtilsVelocity.hasContent(locDescr).booleanValue() 
				|| UtilsVelocity.hasContent(extentMinimum).booleanValue() || UtilsVelocity.hasContent(extentMaximum).booleanValue() 
				|| UtilsVelocity.hasContent(extentUnit).booleanValue() || UtilsVelocity.hasContent(extentVDatum).booleanValue()) {
			
			addSectionTitle(elements, messages.getString("t011_obj_geo.coord"));
			if (refLocationRecords.size() > 0) {
				ArrayList textListEntries = new ArrayList();
				ArrayList body = new ArrayList();
				ArrayList head = new ArrayList();
				for (int i = 0; i < refLocationRecords.size(); i++) {
					Record listRecord = (Record) refLocationRecords.get(i);
					if (listRecord.getString("spatial_ref_value.type") != null && listRecord.getString("spatial_ref_value.type").equals("G")) {
						HashMap listEntry = new HashMap();
						listEntry.put("type", "textList");
						String textLine = listRecord.getString("spatial_ref_value.name_value");
						if (textLine != null && listRecord.getString("spatial_ref_value.nativekey") != null) {
							textLine = textLine.concat(" (").concat(listRecord.getString("spatial_ref_value.nativekey")).concat(")");
						}
						listEntry.put("body", textLine);
						if (!isEmptyList(listEntry)) {
							textListEntries.add(listEntry);
						}
						ArrayList row = new ArrayList();
						row.add(notNull(listRecord.getString("spatial_ref_value.name_value")));
						row.add(notNull(listRecord.getString("spatial_ref_value.x1")));
						row.add(notNull(listRecord.getString("spatial_ref_value.y1")));
						row.add(notNull(listRecord.getString("spatial_ref_value.x2")));
						row.add(notNull(listRecord.getString("spatial_ref_value.y2")));
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}else{
						ArrayList row = new ArrayList();
						row.add(notNull(listRecord.getString("spatial_ref_value.name_value")));
						row.add(notNull(listRecord.getString("spatial_ref_value.x1")));
						row.add(notNull(listRecord.getString("spatial_ref_value.y1")));
						row.add(notNull(listRecord.getString("spatial_ref_value.x2")));
						row.add(notNull(listRecord.getString("spatial_ref_value.y2")));
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}
					
				}
				if (textListEntries.size() > 0) {
					HashMap element = new HashMap();
					element.put("type", "textList");
					element.put("title", messages.getString("t011_township.township_no"));
					element.put("textList", textListEntries);
					elements.add(element);
				}
				
				if (body.size() > 0) {
					HashMap element = new HashMap();
					element.put("type", "table");
					element.put("title", messages.getString("geothesaurus_spacial_reference"));
					head.add(messages.getString("geothesaurus_spacial_reference"));
					head.add(messages.getString("spatial_ref_value_x1"));
					head.add(messages.getString("spatial_ref_value_y1"));
					head.add(messages.getString("spatial_ref_value_x2"));
					head.add(messages.getString("spatial_ref_value_y2"));
					element.put("head", head);
					element.put("body", body);
					elements.add(element);
				}
			}
			if(getSubRecordsByColumnName(record, "t011_obj_geo.special_base").size() > 0){
				Record refRecord = (Record) getSubRecordsByColumnName(record, "t011_obj_geo.special_base").get(0);
				String referenceSystem = (refRecord.getString("t011_obj_geo.referencesystem_value") != null && refRecord.getString("t011_obj_geo.referencesystem_value").length() > 0) ? refRecord
						.getString("t011_obj_geo.referencesystem_value")
						: sysCodeList.getName("100", refRecord.getString("t011_obj_geo.referencesystem_key"));
				addElementEntryLabelLeft(elements, referenceSystem, messages.getString("t011_obj_geo.referencesystem_id"));
			}
	    	
	    	// geo thesaurus references
	    	if (refLocationRecords.size() > 0) {
		    	ArrayList textListEntries = new ArrayList();
		    	for (int i=0; i<refLocationRecords.size(); i++) {
		    		Record listRecord = (Record)refLocationRecords.get(i);
		        	if (listRecord.getString("spatial_ref_value.type") != null && listRecord.getString("spatial_ref_value.type").equals("F") && listRecord.getString("spatial_ref_value.name_value") != null ) {
			    		HashMap listEntry = new HashMap();
			        	listEntry.put("type", "textList");
			        	listEntry.put("body", listRecord.getString("spatial_ref_value.name_value"));
			        	if (!isEmptyList(listEntry)) {
			        		textListEntries.add(listEntry);
			        	}
		        	}
		    	}
		    	if (textListEntries.size() > 0) {
			    	HashMap element = new HashMap();
			    	element.put("type", "textList");
			    	element.put("title", messages.getString("t019_coordinates.bezug"));
			    	element.put("textList", textListEntries);
		    	    elements.add(element);
		    	}
	    	}
	    	
	    	// vertical extend
	    	if (UtilsVelocity.hasContent(extentMinimum).booleanValue()
	    			|| UtilsVelocity.hasContent(extentMaximum).booleanValue()
	    			|| UtilsVelocity.hasContent(extentUnit).booleanValue()
	    			|| UtilsVelocity.hasContent(extentVDatum).booleanValue()
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
				row.add(notNull(extentMaximum));
				row.add(notNull(extentMinimum));
				row.add(notNull(sysCodeList.getName("102", extentUnit)));
				row.add(notNull(sysCodeList.getName("101", extentVDatum)));
	    		if (!isEmptyRow(row)) {
	    			body.add(row);
	    		}
		    	if (body.size() > 0) {
			    	elements.add(element);
			    }
	    	}
	    	
	    	this.addElementEntryLabelLeft(elements, locDescr, messages.getString("t01_object.loc_descr"));
	    	closeDiv(elements);
		}
	}

	
	private void addReferenceObjectClass1(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_geo.special_base");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		
    		if (refRecord.getString("t011_obj_geo.special_base") != null) {
    			addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_geo.special_base"), messages.getString("t011_obj_geo.special_base"));
    		}
    		
    		addElementEntryLabelLeft(elements, sysCodeList.getName("525", refRecord.getString("t011_obj_geo.hierarchy_level")), messages.getString("t011_obj_geo.hierarchy_level"));
    		
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
    		    }
    		}
    		
    		boolean showVectorData = false;
    		List listRecords = getSubRecordsByColumnName(record, "t011_obj_geo_spatial_rep.line");
    		if (listRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "textList");
    	    	element.put("title", messages.getString("t011_obj_geo_spatial_rep.type"));
    	    	ArrayList textListEntries = new ArrayList();
    	    	element.put("textList", textListEntries);
    	    	for (int i=0; i<listRecords.size(); i++) {
    	    		Record listRecord = (Record)listRecords.get(i);
    	        	HashMap listEntry = new HashMap();
    	        	listEntry.put("type", "textList");
    	        	String repType = listRecord.getString("t011_obj_geo_spatial_rep.type");
    	        	listEntry.put("body", sysCodeList.getName("526", repType));
    	        	if (!isEmptyList(listEntry)) {
    	        		textListEntries.add(listEntry);
    	        	}
    	        	if (repType != null && repType.equals("1")) {
    	        		showVectorData = true;
    	        	}
    	    	}
        	    elements.add(element);
    		}
    		
    		if (showVectorData) {
    			addElementEntryLabelLeft(elements, sysCodeList.getName("528", refRecord.getString("t011_obj_geo.vector_topology_level")), messages.getString("t011_obj_geo.vector_topology_level"));
    			
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
        	    		row.add(notNull(sysCodeList.getName("515", tableRecord.getString("t011_obj_geo_vector.geometric_object_type"))));
        	    		row.add(notNull(tableRecord.getString("t011_obj_geo_vector.geometric_object_count")));
        	    		if (!isEmptyRow(row)) {
        	    			body.add(row);
        	    		}
        	    	}
        	    	if (body.size() > 0) {
        		    	elements.add(element);
        		    }
        		}
    		}
    		if (refRecord.getString("t011_obj_geo.rec_grade") != null) {
    			addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_geo.rec_grade").concat(" %"), messages.getString("t011_obj_geo.rec_grade"));
    		}
    		
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
    		    }
    		}    		
    		if (refRecord.getString("t011_obj_geo.pos_accuracy_vertical") != null) {
    			addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_geo.pos_accuracy_vertical").concat(" m"), messages.getString("t011_obj_geo.pos_accuracy_vertical"));
    		}
    		if (refRecord.getString("t011_obj_geo.rec_exact") != null) {
    			addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_geo.rec_exact").concat(" m"), messages.getString("t011_obj_geo.rec_exact"));
    		}
    		if (refRecord.getString("t011_obj_geo.data_base") != null) {
    			addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_geo.data_base"), messages.getString("t011_obj_geo.data_base"));
    		}
    		
    		listRecords = getSubRecordsByColumnName(record, "t011_obj_geo_supplinfo.line");
    		if (listRecords.size() > 0 && !(listRecords.size() == 1 && ((Record)(listRecords.get(0))).getString("t011_obj_geo_supplinfo.feature_type") == null)) {
    			// sort the list
    			Object[] sortedListRecords = new Record[listRecords.size()];
        		for (Object object : listRecords) {
        			sortedListRecords[(Integer.valueOf((String)((Record)object).get("t011_obj_geo_supplinfo.line")))-1] = object;
    			}
        		// write sorted records as a list again
        		listRecords = Arrays.asList(sortedListRecords);
        		
    	    	HashMap element = new HashMap();
    	    	element.put("type", "textList");
    	    	element.put("sort", "false");
    	    	element.put("title", messages.getString("t011_obj_geo_supplinfo.feature_type"));
    	    	ArrayList textListEntries = new ArrayList();
    	    	element.put("textList", textListEntries);
    	    	for (int i=0; i<listRecords.size(); i++) {
    	    		Record listRecord = (Record)listRecords.get(i);
    	        	HashMap listEntry = new HashMap();
    	        	listEntry.put("type", "textList");
    	        	listEntry.put("body", listRecord.getString("t011_obj_geo_supplinfo.feature_type"));
    	        	if (!isEmptyList(listEntry)) {
    	        		textListEntries.add(listEntry);
    	        	}
    	    	}
        	    elements.add(element);
    		}
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_geo.method"), messages.getString("t011_obj_geo.method"));
    		// add datasource indetificatioon (new for INSPIRE)
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_geo.datasource_uuid"), messages.getString("t011_obj_geo.datasource_uuid"));
    	}
	}	

	
	private void addReferenceObjectClass4(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_project.leader");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_project.leader"), messages.getString("t011_obj_project.leader"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_project.member"), messages.getString("t011_obj_project.member"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_project.description"), messages.getString("t011_obj_project.description"));
    	}
	}	
	
	private void addReferenceObjectClass2(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_literatur.publisher");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.author"), messages.getString("t011_obj_literatur.autor"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.publisher"), messages.getString("t011_obj_literatur.publisher"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.publish_in"), messages.getString("t011_obj_literatur.publish_in"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.volume"), messages.getString("t011_obj_literatur.volume"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.sides"), messages.getString("t011_obj_literatur.sides"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.publish_year"), messages.getString("t011_obj_literatur.publish_year"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.isbn"), messages.getString("t011_obj_literatur.isbn"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.publishing"), messages.getString("t011_obj_literatur.publishing"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.publish_loc"), messages.getString("t011_obj_literatur.publish_loc"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.type_value"), messages.getString("t011_obj_literatur.typ"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.doc_info"), messages.getString("t011_obj_literatur.doc_info"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.loc"), messages.getString("t011_obj_literatur.loc"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_literature.description"), messages.getString("t011_obj_literatur.description"));
    	}
	}
	
	
	private void addReferenceObjectClass3(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_serv.type");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.type_value"), messages.getString("t011_obj_serv.type"));
    		boolean hasAccessConstraints = (refRecord.getString(Settings.HIT_KEY_OBJ_SERV_HAS_ACCESS_CONSTRAINT) != null && refRecord.getString(Settings.HIT_KEY_OBJ_SERV_HAS_ACCESS_CONSTRAINT).equals("Y")) ? true : false;
    		String serviceType = refRecord.getString("t011_obj_serv.type_value");
    		String serviceTypeKey = refRecord.getString("t011_obj_serv.type_key");
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.environment"), messages.getString("t011_obj_serv.environment"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.history"), messages.getString("t011_obj_serv.history"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.base"), messages.getString("t011_obj_serv.base"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.description"), messages.getString("t011_obj_serv.description"));
    		// type of service (new for INSPIRE)
    		refRecords = getSubRecordsByColumnName(record, "t011_obj_serv_type.line");
    		if (refRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "textList");
    	    	element.put("title", messages.getString("t011_obj_serv_type"));
    	    	ArrayList textListEntries = new ArrayList();
    	    	element.put("textList", textListEntries);
    	    	for (int i=0; i<refRecords.size(); i++) {
    	    		refRecord = (Record)refRecords.get(i);
    	        	HashMap listEntry = new HashMap();
    	        	listEntry.put("type", "textList");
    	        	listEntry.put("body", refRecord.getString("t011_obj_serv_type.serv_type_value"));
    	        	if (!isEmptyList(listEntry)) {
    	        		textListEntries.add(listEntry);
    	        	}
    	    	}
        	    elements.add(element);
    		}
    		refRecords = getSubRecordsByColumnName(record, "t011_obj_serv_version.line");
    		if (refRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "textList");
    	    	element.put("title", messages.getString("t011_obj_serv_version.version"));
    	    	ArrayList textListEntries = new ArrayList();
    	    	element.put("textList", textListEntries);
    	    	for (int i=0; i<refRecords.size(); i++) {
    	    		refRecord = (Record)refRecords.get(i);
    	        	HashMap listEntry = new HashMap();
    	        	listEntry.put("type", "textList");
    	        	listEntry.put("body", refRecord.getString("t011_obj_serv_version.serv_version"));
    	        	if (!isEmptyList(listEntry)) {
    	        		textListEntries.add(listEntry);
    	        	}
    	    	}
        	    elements.add(element);
    		}
    		// table resolution scale (new for INSPIRE)
    		refRecords = getSubRecordsByColumnName(record, "t011_obj_serv_scale.line");
    		if (refRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "table");
    	    	element.put("title", messages.getString("t011_obj_serv_scale"));
    			ArrayList head = new ArrayList();
    			head.add(messages.getString("t011_obj_serv_scale.scale").concat(" 1:x"));
    			head.add(messages.getString("t011_obj_serv_scale.resolution_ground").concat(" m"));
    			head.add(messages.getString("t011_obj_serv_scale.resolution_scan").concat(" dpi"));
    			element.put("head", head);
    			ArrayList body = new ArrayList();
    			element.put("body", body);
    	    	for (int i=0; i<refRecords.size(); i++) {
    	    		refRecord = (Record)refRecords.get(i);
    	    		ArrayList row = new ArrayList();
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_scale.scale")));
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_scale.resolution_ground")));
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_scale.resolution_scan")));
    	    		if (!isEmptyRow(row)) {
    	    			body.add(row);
    	    		}
    	    	}
    	    	if (body.size() > 0) {
    		    	elements.add(element);
    		    }
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
    	    		if (((serviceTypeKey != null && serviceTypeKey.equals("2")) || (serviceType != null && (serviceType.toLowerCase().indexOf("wms") != -1 || serviceType.toLowerCase().indexOf("view") != -1))) && serviceLinkRecords.size() > 0 && refRecord.getString("t011_obj_serv_operation.name_value") != null && refRecord.getString("t011_obj_serv_operation.name_value").toLowerCase().equals("getcapabilities")) {
    	    	    	for (int j=0; j < serviceLinkRecords.size(); j++) {
    	    	    		// add getCap if not set
    	    	    		String serviceUrl = ((Record)serviceLinkRecords.get(i)).getString("t011_obj_serv_op_connpoint.connect_point"); 
    	    	    		if (serviceUrl != null && serviceUrl.toLowerCase().indexOf("request=getcapabilities") == -1) {
    	    	    			if (serviceUrl.indexOf("?") == -1) {
    	    	    				serviceUrl = serviceUrl + "?";
    	    	    			}
    	    	    			if (!serviceUrl.endsWith("?")) {
    	    	    				serviceUrl = serviceUrl + "&";
    	    	    			}
    	    	    			serviceUrl = serviceUrl + "REQUEST=GetCapabilities&SERVICE=WMS";
    	    	    		}
    	    	    		wmsServiceLinks.add(serviceUrl);
    	    	    	}
    	    		}
    	    		if (!isEmptyRow(row)) {
    	    			body.add(row);
    	    		}
    	    	}
    	    	if (body.size() > 0) {
    		    	elements.add(element);
    		    }

    		}
    		if (wmsServiceLinks.size() > 0) {
    	    	ArrayList linkList = new ArrayList();
    	    	for (int i=0; i<wmsServiceLinks.size(); i++) {
    	        	if (UtilsVelocity.hasContent(wmsServiceLinks.get(i)).booleanValue()) {
    	        	  Map link;
    	        	  
    	        	  if (UtilsVelocity.hasContent(wmsServiceLinks.get(i).trim()).booleanValue()) {
							HashMap elementLink = new HashMap();
							elementLink.put("type", "linkLine");
							elementLink.put("hasLinkIcon", new Boolean(true));
							elementLink.put("isExtern", new Boolean(false));
							elementLink.put("title", messages.getString("common.result.showMap"));
							elementLink.put("href", "main-maps.psml?wms_url=" + UtilsVelocity.urlencode(wmsServiceLinks.get(i).trim()));
							
							HashMap element = new HashMap();
							element.put("type", "textLabelLeft");
							// do not display "show in map" link if the map has access constraints
		    	        	if (!hasAccessConstraints) {
	  							element.put("title", messages.getString("common.result.showGetCapabilityUrl"));
	  							element.put("link", elementLink);
	  						}else{
	  							element.put("title", messages.getString("common.result.showGetCapabilityUrlRestricted"));
	  						}
							element.put("body", wmsServiceLinks.get(i).trim().split("\\?")[0].toString());
							elements.add(element);
						}
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
		    }
	    	
    	}
    	refRecords = getSubRecordsByColumnName(record, "t011_obj_data.base");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_data.base"), messages.getString("t011_obj_data.base"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_data.description"), messages.getString("t011_obj_data.description"));
    	}
	}
	
	private void addReferenceObjectClass6(List elements, Record record) {
    	List refRecords = getSubRecordsByColumnName(record, "t011_obj_serv.type");
    	if (refRecords.size() > 0) {
    		Record refRecord = (Record)refRecords.get(0);
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.type_value"), messages.getString("t011_obj_serv.type"));
    		String serviceType = refRecord.getString("t011_obj_serv.type_value");
    		String serviceTypeKey = refRecord.getString("t011_obj_serv.type_key");
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.environment"), messages.getString("t011_obj_serv.environment"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.history"), messages.getString("t011_obj_serv.history"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.base"), messages.getString("t011_obj_serv.base"));
    		addElementEntryLabelLeft(elements, refRecord.getString("t011_obj_serv.description"), messages.getString("t011_obj_serv.description"));
    		
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
    		// service urls
    		refRecords = getSubRecordsByColumnName(record, "t011_obj_serv_url.line");
    		if (refRecords.size() > 0) {
    	    	HashMap element = new HashMap();
    	    	element.put("type", "table");
    	    	element.put("title", messages.getString("t011_obj_serv_url"));
    			ArrayList head = new ArrayList();
    			head.add(messages.getString("t011_obj_serv_url.name"));
    			head.add(messages.getString("t011_obj_serv_url.url"));
    			head.add(messages.getString("t011_obj_serv_url.description"));
    			element.put("head", head);
    			ArrayList body = new ArrayList();
    			element.put("body", body);
    	    	for (int i=0; i<refRecords.size(); i++) {
    	    		refRecord = (Record)refRecords.get(i);
    	    		ArrayList row = new ArrayList();
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_url.name")));
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_url.url")));
    	    		row.add(notNull(refRecord.getString("t011_obj_serv_url.description")));
    	    		if (!isEmptyRow(row)) {
    	    			body.add(row);
    	    		}
    	    	}
    	    	if (body.size() > 0) {
    		    	elements.add(element);
    		    }

    		}
    	}
	}
	
	private void addElementEntry(List elements, String body, String title) {
		addElementEntry(elements, body, title, null);
	}

	private void addElementEntry(List elements, String body, String title, String alternateName) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "entry");
			if(title == null){
				element.put("header", messages.getString("detail_description"));
			}
			if(alternateName == null){
				element.put("title", title);
			}else {
				element.put("title", alternateName);
			}
			// show line breaks correctly in HTML
			element.put("body", body.replaceAll("\n", "<br />"));
			elements.add(element);
		}
	}
	
	private void addElementEntryLabelLeft(List elements, String body, String title) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "textLabelLeft");
			element.put("title", title);
			element.put("body", body);
			elements.add(element);
		}
	}
	
	private void addElementEntryLabelAbove(List elements, String body, String title, boolean line) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "textLabelAbove");
			element.put("title", title);
			element.put("line", line);
			element.put("body", body);
			elements.add(element);
		}
	}
	
	private void addElementEntryLabelDuring(List elements, String body, String title) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "textLabelDuring");
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
	
	private void addSuperiorObjects(List elements, List linkList) {
		if (!linkList.isEmpty()) {
			HashMap element = new HashMap();
			element.put("type", "linkList");
			element.put("title", messages.getString("superior_references"));
			element.put("linkList", linkList);
			elements.add(element);
		}
	}
	
	private void addSubordinatedObjects(List elements, List linkList) {
		if (!linkList.isEmpty()) {
			HashMap element = new HashMap();
			element.put("type", "linkList");
			element.put("title", messages.getString("subordinated_references"));
			element.put("linkList", linkList);
			elements.add(element);
		}
	}
	
	private HashMap addCrossReferencedObjects(List elements, String objId) {
		if (objId == null) {
			return null;
		}
		ArrayList<IngridHit> result = DetailDataPreparerHelper.getHits("object_reference.obj_from_id:".concat(objId).concat(" iplugs:\"").concat(iPlugId).concat("\""), new String[] {
				Settings.HIT_KEY_OBJ_ID, "object_reference.obj_to_uuid" }, null);
		String query = "";
		boolean firstUuid = true;
		for (IngridHit hit : result) {
			String[] referenceIds = ((IngridHitDetail) hit.get("detail")).getString("object_reference.obj_to_uuid").split(UtilsSearch.DETAIL_VALUES_SEPARATOR);
			for (String str : referenceIds) {
				if (firstUuid) {
					query += Settings.HIT_KEY_OBJ_ID + ":" + str;
					firstUuid = false;
				} else {
					query += " OR " + Settings.HIT_KEY_OBJ_ID + ":" + str;
				}
			}
		}
		if (query.length() > 0) {
			query = "(" + query + ")";
			
			List linkList = getLinkListOfObjectsFromQuery(query.concat(" iplugs:\"").concat(iPlugId).concat("\""));
			if (!linkList.isEmpty()) {
				HashMap element = new HashMap();
				element.put("type", "linkList");
				element.put("title", messages.getString("cross_references"));
				element.put("linkList", linkList);
				return element;
			}
		}
		return null;
	}

    
    private void addUrlReferences(List elements, Record record) {
        ArrayList linkList = new ArrayList();
    	List refRecords = getSubRecordsByColumnName(record, "t017_url_ref.line");
    	for (int i=0; i<refRecords.size(); i++) {
    		Record refRecord = (Record)refRecords.get(i);
        	HashMap link = new HashMap();
        	link.put("hasLinkIcon", new Boolean(true));
        	link.put("isExtern", new Boolean(true));
        	if (refRecord.getString("t017_url_ref.content") != null && refRecord.getString("t017_url_ref.content").length() > 0) {
        		link.put("title", refRecord.getString("t017_url_ref.content"));
        	} else {
        		link.put("title", refRecord.getString("t017_url_ref.url_link"));
        	}
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
    	ArrayList elementsAddress = new ArrayList();
    	for (int i=0; i<refRecords.size(); i++) {
    		Record refRecord = (Record)refRecords.get(i);
    		addAddressType(elementsAddress, refRecord);
    	}
		HashMap element = new HashMap();
    	element.put("type", "multiLineAddresses");
    	element.put("title", messages.getString("addresses"));
    	element.put("id", "addresses_id");
    	element.put("elementsAddress", elementsAddress);
    	elements.add(element);
    }
    
    private void addAddressType(List elements, Record record) {
    	HashMap element = new HashMap();
    	element.put("type", "multiLine");
    	element.put("sort", "false");
    	if (record.getString("t012_obj_adr.type") != null && !record.getString("t012_obj_adr.type").equals("-1")) {
    		String translatedString = sysCodeList.getName("505", record.getString("t012_obj_adr.type"));
    		if (translatedString != null && translatedString.length() > 0) {
    			element.put("title", sysCodeList.getName("505", record.getString("t012_obj_adr.type")));
    		} else {
    			element.put("title", record.getString("t012_obj_adr.special_name"));
    		}
    	} else {
        	element.put("title", record.getString("t012_obj_adr.special_name"));
    	}
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
			if ((addressType == 0 || addressType == 3 || addressType == 1) && !addrRecord.getString("t02_address.adr_uuid").equals("undefined")) {
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
			if (!addrRecord.getString("t02_address.adr_uuid").equals("undefined")) {
		    	element.put("type", "linkLine");
		    	element.put("hasLinkIcon", new Boolean(false));
		    	element.put("isExtern", new Boolean(false));
				element.put("title", textLine);
				element.put("href", actionUrl.toString());
	    	} else {
		    	element.put("type", "textLine");
				element.put("body", textLine);
	    	}
	    	
			elements.add(element);
		}
		if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.descr")).booleanValue()|| UtilsVelocity.hasContent(addrRecord.getString("t02_address.postbox")).booleanValue()) {
		    addSpace(elements);
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
				/*if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.country_code")).booleanValue()) {
					String countryCode = messages.getString("postal.country.code.".concat(addrRecord.getString("t02_address.country_code")));
					if (countryCode.startsWith("postal.country.code.")) {
						textLine = addrRecord.getString("t02_address.country_code").concat("-").concat(textLine);
					} else {
						textLine = messages.getString("postal.country.code.".concat(addrRecord.getString("t02_address.country_code"))).concat("-").concat(textLine);
					}
				}*/
				if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.city")).booleanValue()) {
					textLine = textLine.concat(" ").concat(addrRecord.getString("t02_address.city"));
				}
				if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.city")).booleanValue()) {
					addrRecord.getString("t02_address.postbox_pc");
					element = new HashMap();
					element.put("type", "textLine");
					element.put("body", textLine);
					elements.add(element);
				}
			}
			if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.country_value")).booleanValue()) {
				element = new HashMap();
				String textLine = addrRecord.getString("t02_address.country_value");
				element.put("type", "textLine");
				element.put("body", textLine);
				elements.add(element);
			}
		}
		if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.street")).booleanValue()) {
			addSpace(elements);
			if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.street")).booleanValue()) {
				element = new HashMap();
				element.put("type", "textLine");
				element.put("body", addrRecord.getString("t02_address.street"));
				elements.add(element);
			}
			if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.postcode")).booleanValue()) {
				String textLine = addrRecord.getString("t02_address.postcode");
				/*if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.country_code")).booleanValue()) {
					String countryCode = messages.getString("postal.country.code.".concat(addrRecord.getString("t02_address.country_code")));
					if (countryCode.startsWith("postal.country.code.")) {
						textLine = addrRecord.getString("t02_address.country_code").concat("-").concat(textLine);
					} else {
						textLine = messages.getString("postal.country.code.".concat(addrRecord.getString("t02_address.country_code"))).concat("-").concat(textLine);
					}
				}*/
				if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.city")).booleanValue()) {
					textLine = textLine.concat(" ").concat(addrRecord.getString("t02_address.city"));
				}
				element = new HashMap();
				element.put("type", "textLine");
				element.put("body", textLine);
				elements.add(element);
			}
			
			if (UtilsVelocity.hasContent(addrRecord.getString("t02_address.country_value")).booleanValue()) {
				element = new HashMap();
				String textLine = addrRecord.getString("t02_address.country_value");
				element.put("type", "textLine");
				element.put("body", textLine);
				elements.add(element);
			}
		}
		addSpace(elements);
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
    
    private void addSpace(List elements) {
    	HashMap element = new HashMap();
		element.put("type", "space");
		elements.add(element);
    }
    
    private void openDiv(List elements) {
    	HashMap element = new HashMap();
    	element.put("type", "beginnDiv");
		elements.add(element);
    }
    
    private void closeDiv(List elements) {
    	HashMap element = new HashMap();
		element.put("type", "endDiv");
		elements.add(element);
    }
    
    private void addSectionTitle(List elements, String title) {
		HashMap element = new HashMap();
		element.put("type", "section");
		element.put("title", title);
		elements.add(element);
		openDiv(elements);
    }
    
    private void addGroupTitle(List elements, String title) {
    	HashMap element = new HashMap();
		element.put("type", "group");
		element.put("title", title);
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
    	if (line.get("type") != null && (line.get("type").equals("textLine") || line.get("type").equals("textLabelLeft"))) {
        	if (line.get("body") != null && line.get("body") instanceof String && ((String)line.get("body")).length() > 0) {
        		return false;
        	}
    	}
    	return true;
    }
    
    private boolean isEmptyList(HashMap listEntry) {
    	if (listEntry.get("type") != null && listEntry.get("type").equals("textList") || listEntry.get("type").equals("linkList")) {
        	if (listEntry.get("body") != null && listEntry.get("body") instanceof String && ((String)listEntry.get("body")).length() > 0) {
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
