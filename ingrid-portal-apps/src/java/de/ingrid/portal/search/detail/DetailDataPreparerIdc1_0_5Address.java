/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;

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

/**
 * @author joachim
 *
 */
public class DetailDataPreparerIdc1_0_5Address implements DetailDataPreparer {

    private final static Log log = LogFactory.getLog(DetailDataPreparerIdc1_0_5Address.class);

	private Context context;
	private String iPlugId;
	private RenderRequest request;
	private RenderResponse response;
	private IngridResourceBundle messages;
	private IngridSysCodeList sysCodeList;
	
	public DetailDataPreparerIdc1_0_5Address(Context context, String iPlugId, RenderRequest request, RenderResponse response) {
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
		String addrType = record.getString("t02_address.adr_type");
		String title = null;
		if (addrType.equals("2") || addrType.equals("3")) {
			title = addStrWithSeparator(title, record.getString("t02_address.address_value"), " ");
			title = addStrWithSeparator(title, record.getString("t02_address.title_value"), " ");
			title = addStrWithSeparator(title, record.getString("t02_address.firstname"), " ");
			title = addStrWithSeparator(title, record.getString("t02_address.lastname"), " ");
			if (addrType.equals("3")) {
				title = addStrWithSeparator(record.getString("t02_address.institution"), title, ", ");
			}
		} else {
			title = addStrWithSeparator(title, record.getString("t02_address.institution"), " ");
		}
		general.put("title", title);
		
		if (record.getString("t02_address.mod_time") != null) {
			general.put("modTime", UtilsDate.convertDateString(record.getString("t02_address.mod_time").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
		}
		data.put("general", general);
		
		String addrUuid = record.getString("t02_address.adr_uuid");
		
		ArrayList elements = new ArrayList();
		int previousElementsSize = elements.size();

		// address type
		addElementAddressType(elements, record.getString("t02_address.adr_type"));
		// description
		addElementEntry(elements, record.getString("t02_address.job"), null);
		
		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}
		
		addAddress(elements, record);
		
		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}
		
		addSubordinatedAdresses(elements, addrUuid);
		
		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}

		// add index information
		addIndexInformation(elements, record);
		if (previousElementsSize < elements.size()) {
			// add horizontal line
			addLine(elements);
			previousElementsSize = elements.size();
		}
		
		addObjectReferences(elements, record);
		
		addSubordiantedObjectReferences(elements, record);
		
		data.put("elements", elements);
		
		context.put("data", data);
	}

    private void addSubordinatedAdresses(List elements, String addrUuid) {
    	
        List linkList = getLinkListOfAddressesFromQuery("parent.address_node.addr_uuid:".concat(addrUuid).concat(
                " iplugs:\"").concat(DetailDataPreparerHelper.getAddressPlugIdFromPlugId(iPlugId)).concat("\""));
        if (!linkList.isEmpty()) {
            Collections.sort(linkList, new LinkListComparator());
        	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("subordinated_addresses"));
        	element.put("linkList", linkList);
        	elements.add(element);
        }
    }

	
	private void addIndexInformation(List elements, Record record) {

    	// index references
		List listRecords = getSubRecordsByColumnName(record, "t04_search.searchterm");
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

	private void addElementAddressType(List elements, String addressType) {
		if (UtilsVelocity.hasContent(addressType).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "addressType");
			element.put("addressType", addressType);
			element.put("addressTypeName", messages.getString("udk_adr_class_name_".concat(addressType)));
			elements.add(element);
		}
	}
	
    private void addAddress(List elements, Record record) {
    	HashMap element = new HashMap();
    	List innerElements = new ArrayList();
    	
    	element.put("type", "multiLine");
    	element.put("sort", "false");
    	element.put("title", record.getString("t012_obj_adr.special_name"));
    	element.put("elements", innerElements);
    	elements.add(element);
    	

    	// address type
    	int addressType = -1;
		try {
			addressType = Integer.parseInt(record.getString("t02_address.adr_type"));
		} catch (NumberFormatException e) {
			log.debug("Illegal address classification (institution, unit, ...) found: " + record.getString("t02_address.adr_type"));
		}
    	
		// add address parents
		if (addressType == 1 || addressType == 2) {
        	ArrayList addressParents = new ArrayList();
        	List refRecords = getSubRecordsByColumnName(record, "title2");
        	if (refRecords.size() > 0) {
        		Record refRecord = (Record)refRecords.get(0);
		    	element = new HashMap();
		    	element.put("type", "linkLine");
		    	element.put("hasLinkIcon", new Boolean(false));
		    	element.put("isExtern", new Boolean(false));
				element.put("title", refRecord.getString("t02_address.institution"));
				PortletURL actionUrl = response.createActionURL();
		    	actionUrl.setParameter("cmd", "doShowAddressDetail");
				actionUrl.setParameter("addrId", refRecord.getString("t02_address.adr_uuid"));
				actionUrl.setParameter("plugid", iPlugId);
				element.put("href", actionUrl.toString());
				addressParents.add(element);
	        	if (!refRecord.getString("t02_address.adr_type").equals("0")) {
					refRecords = getSubRecordsByColumnName(record, "title3");
		        	if (refRecords.size() > 0) {
		        		refRecord = (Record)refRecords.get(0);
				    	element = new HashMap();
				    	element.put("type", "linkLine");
				    	element.put("hasLinkIcon", new Boolean(false));
				    	element.put("isExtern", new Boolean(false));
						element.put("title", refRecord.getString("t02_address.institution"));
						actionUrl = response.createActionURL();
				    	actionUrl.setParameter("cmd", "doShowAddressDetail");
						actionUrl.setParameter("addrId", refRecord.getString("t02_address.adr_uuid"));
						actionUrl.setParameter("plugid", iPlugId);
						element.put("href", actionUrl.toString());
						addressParents.add(element);
			        	if (!refRecord.getString("t02_address.adr_type").equals("0")) {
			        		refRecords = getSubRecordsByColumnName(record, "parent3.address_node.addr_uuid");
			            	if (refRecords.size() > 0) {
			            		refRecord = (Record)refRecords.get(0);
			            		if (UtilsVelocity.hasContent(refRecord.getString("address_node.fk_addr_uuid")).booleanValue()) {
			            			IngridHit hit = getAddress(refRecord.getString("address_node.fk_addr_uuid"));
			            			while (hit != null) {
			            		    	element = new HashMap();
			            		    	element.put("type", "linkLine");
			            		    	element.put("hasLinkIcon", new Boolean(false));
			            		    	element.put("isExtern", new Boolean(false));
			            				element.put("title", ((IngridHitDetail)hit.get("detail")).getString("title"));
			            				actionUrl = response.createActionURL();
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
			            		}
			            	}			        		
			        	}
		        	}
	        	}
        	}
        	
        	if (addressParents.size() > 0) {
        		for (int i=addressParents.size()-1;i>=0; i--) {
        			innerElements.add(addressParents.get(i));
        		}
        	}
    	}
		
    	element = new HashMap();
    	element.put("type", "textLine");
		element.put("body", record.getString("t02_address.institution"));
		innerElements.add(element);
		
		// address, title, name with link
		if (UtilsVelocity.hasContent(record.getString("t02_address.lastname")).booleanValue()) {
	    	String textLine = null;
	    	textLine = addStrWithSeparator(textLine, record.getString("t02_address.address_value"), " ");
	    	textLine = addStrWithSeparator(textLine, record.getString("t02_address.title_value"), " ");
	    	textLine = addStrWithSeparator(textLine, record.getString("t02_address.firstname"), " ");
	    	textLine = addStrWithSeparator(textLine, record.getString("t02_address.lastname"), " ");
			
	    	element = new HashMap();
	    	element.put("type", "textLine");
			element.put("body", textLine);
			innerElements.add(element);
		}
		// description
		if (UtilsVelocity.hasContent(record.getString("t02_address.descr")).booleanValue()) {
	    	element = new HashMap();
	    	element.put("type", "textLine");
			element.put("body", record.getString("t02_address.descr"));
			innerElements.add(element);
		}
		// post box information
		if (UtilsVelocity.hasContent(record.getString("t02_address.postbox")).booleanValue()) {
	    	element = new HashMap();
	    	element.put("type", "textLine");
			element.put("body", messages.getString("postbox_label").concat(" ").concat(record.getString("t02_address.postbox")));
			innerElements.add(element);
			if (UtilsVelocity.hasContent(record.getString("t02_address.postbox_pc")).booleanValue()) {
				String textLine = record.getString("t02_address.postbox_pc");
				/*if (UtilsVelocity.hasContent(record.getString("t02_address.country_code")).booleanValue()) {
					textLine = messages.getString("postal.country.code.".concat(record.getString("t02_address.country_code"))).concat("-").concat(textLine);
				}*/
				if (UtilsVelocity.hasContent(record.getString("t02_address.city")).booleanValue()) {
					textLine = textLine.concat(" ").concat(record.getString("t02_address.city"));
				}
				element = new HashMap();
				element.put("type", "textLine");
				element.put("body", textLine);
				innerElements.add(element);
			}
			if (UtilsVelocity.hasContent(record.getString("t02_address.country_value")).booleanValue()) {
				element = new HashMap();
				String textLine = record.getString("t02_address.country_value");
				element.put("type", "textLine");
				element.put("body", textLine);
				innerElements.add(element);
			}
	    	element = new HashMap();
	    	element.put("type", "space");
			innerElements.add(element);
			
		}
		if (UtilsVelocity.hasContent(record.getString("t02_address.street")).booleanValue()) {
			if (UtilsVelocity.hasContent(record.getString("t02_address.street")).booleanValue()) {
				element = new HashMap();
				element.put("type", "textLine");
				element.put("body", record.getString("t02_address.street"));
				innerElements.add(element);
			}
			if (UtilsVelocity.hasContent(record.getString("t02_address.postcode")).booleanValue()) {
				String textLine = record.getString("t02_address.postcode");
				/*if (UtilsVelocity.hasContent(record.getString("t02_address.country_code")).booleanValue()) {
					textLine = messages.getString("postal.country.code.".concat(record.getString("t02_address.country_code"))).concat("-").concat(textLine);
				}*/
				if (UtilsVelocity.hasContent(record.getString("t02_address.city")).booleanValue()) {
					textLine = textLine.concat(" ").concat(record.getString("t02_address.city"));
				}
				element = new HashMap();
				element.put("type", "textLine");
				element.put("body", textLine);
				innerElements.add(element);
			}
			if (UtilsVelocity.hasContent(record.getString("t02_address.country_value")).booleanValue()) {
				element = new HashMap();
				String textLine = record.getString("t02_address.country_value");
				element.put("type", "textLine");
				element.put("body", textLine);
				innerElements.add(element);
			}
	    	element = new HashMap();
	    	element.put("type", "space");
			innerElements.add(element);
		}
    	List refRecords = getSubRecordsByColumnName(record, "t021_communication.comm_value");
		for (int i=0; i<refRecords.size(); i++) {
    		Record refRecord = (Record)refRecords.get(i);
    		addCommunication(innerElements, refRecord);
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
			element.put("body", UtilsString.htmlescapeAll((textLine)));
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
    
    
    private List getLinkListOfAddressesFromQuery(String queryStr) {
        String[] requestedMetadata = new String[] {
        		// udk address metadata
        		Settings.HIT_KEY_ADDRESS_CLASS,
        		Settings.HIT_KEY_ADDRESS_FIRSTNAME,
        		Settings.HIT_KEY_ADDRESS_LASTNAME,
        		Settings.HIT_KEY_ADDRESS_TITLE,
        		"t02_address.address_value",
        		Settings.HIT_KEY_ADDRESS_ADDRID
        };
        ArrayList result = DetailDataPreparerHelper.getHits(queryStr, requestedMetadata, null);
        ArrayList linkList = new ArrayList();
        for (int i=0; i<result.size(); i++) {
        	IngridHit hit = (IngridHit)result.get(i);
        	HashMap link = new HashMap();
        	link.put("hasLinkIcon", new Boolean(true));
        	link.put("isExtern", new Boolean(false));
        	String title = null;
        	IngridHitDetail detail = (IngridHitDetail)hit.get("detail");
    		if (detail.getString(Settings.HIT_KEY_ADDRESS_CLASS).equals("2")) {
    			title = addStrWithSeparator(title, detail.getString("t02_address.address_value"), " ");
    			title = addStrWithSeparator(title, detail.getString(Settings.HIT_KEY_ADDRESS_TITLE), " ");
    			title = addStrWithSeparator(title, detail.getString(Settings.HIT_KEY_ADDRESS_FIRSTNAME), " ");
    			title = addStrWithSeparator(title, detail.getString(Settings.HIT_KEY_ADDRESS_LASTNAME), " ");
    		} else {
    			title = addStrWithSeparator(title, detail.getString("title"), " ");
    		}
        	
        	link.put("title", title);
        	PortletURL actionUrl = response.createActionURL();
        	actionUrl.setParameter("cmd", "doShowAddressDetail");
    		actionUrl.setParameter("addrId", (String)(((HashMap)hit.get("detail")).get(Settings.HIT_KEY_ADDRESS_ADDRID)));
    		actionUrl.setParameter("plugid", iPlugId);
        	link.put("href", actionUrl.toString());
        	linkList.add(link);
        }
    	return linkList;
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
    
    private boolean isEmptyLine(HashMap line) {
    	if (line.get("type") != null && line.get("type").equals("textLine")) {
        	if (line.get("body") != null && line.get("body") instanceof String && ((String)line.get("body")).length() > 0) {
        		return false;
        	}
    	}
    	return true;
    }
    
    
    private String addStrWithSeparator(String base, String str, String separator) {
		if (UtilsVelocity.hasContent(str).booleanValue()) {
			if (base != null && base.length() > 0) {
				return base.concat(separator).concat(str);
			} else {
				return str;
			}
		}
		return base;
    }
    
    private void addObjectReferences(List elements, Record record) {
		// max number to show of direct object references
        int maxNumObjRefs = NUM_OBJ_REFS_PER_PAGE;
        try {
        	maxNumObjRefs = Integer.parseInt(request.getParameter(REQUEST_PARAM_NAME_MAX_OBJ_REFS));
        } catch (Exception ex) {}

    	record.getString("t02_address.adr_uuid");
    	boolean moreHits = false;
    	List directObjReferences = getObjectsByAddress(record.getString("t02_address.adr_uuid"), maxNumObjRefs);
    	if (directObjReferences.size() > 0) {
            if (directObjReferences.size() >= maxNumObjRefs) {
            	moreHits = true;
            }
        	List linkList = new ArrayList();
        	for (int i=0; i<directObjReferences.size(); i++) {
        		IngridHit hit = (IngridHit)directObjReferences.get(i);
        		IngridHitDetail detail = (IngridHitDetail)hit.get("detail");
            	HashMap link = new HashMap();
            	link.put("hasLinkIcon", new Boolean(true));
            	link.put("iconImage", "icn_udk_obj_" + detail.getString(Settings.HIT_KEY_UDK_CLASS) + ".gif");
            	link.put("iconImageAltText", messages.getString("udk_obj_class_name_".concat(detail.getString(Settings.HIT_KEY_UDK_CLASS))));
            	link.put("title", detail.getString("title"));
            	PortletURL actionUrl = response.createActionURL();
            	actionUrl.setParameter("cmd", "doShowObjectDetail");
        		actionUrl.setParameter("objId", detail.getString(Settings.HIT_KEY_OBJ_ID));
        		actionUrl.setParameter("plugid", DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId));
            	link.put("href", actionUrl.toString());
            	linkList.add(link);
        		
        	}
            Collections.sort(linkList, new LinkListComparator());
    		
    		HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("search.detail.dataRelations"));
        	element.put("linkList", linkList);
        	elements.add(element);

            if (moreHits) {
            	HashMap link = new HashMap();
            	link.put("type", "linkLine");
            	link.put("hasLinkIcon", new Boolean(false));
            	link.put("isExtern", new Boolean(false));
            	link.put("title", messages.getString("search.detail.dataRelations.next"));
    			PortletURL actionUrl = response.createActionURL();
    	    	actionUrl.setParameter("cmd", "doShowDocument");
    			if (request.getParameter("docid") != null) {
        	    	actionUrl.setParameter("docid", request.getParameter("docid"));
    			}
    			if (request.getParameter("docuuid") != null) {
        	    	actionUrl.setParameter("docuuid", request.getParameter("docuuid"));
    			}
    			
    			actionUrl.setParameter("plugid", iPlugId);
    			// max number to show of object references of direct address
    			actionUrl.setParameter("maxORs", Integer.toString(maxNumObjRefs + NUM_OBJ_REFS_PER_PAGE));
    			// max number to show of object references of sub addresses
    	        int maxNumSubObjRefs = NUM_OBJ_REFS_PER_PAGE;
    	        try {
    	        	maxNumSubObjRefs = Integer.parseInt(request.getParameter(REQUEST_PARAM_NAME_MAX_SUB_OBJ_REFS));
    	        } catch (Exception ex) {}
    			actionUrl.setParameter("maxSubORs", Integer.toString(maxNumSubObjRefs));
    			link.put("href", actionUrl.toString());
    			
    			// encapsulate in multiLine element to guarantee <p> rendering
		    	element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("elements", new ArrayList(Arrays.asList(new HashMap[] { link })));
	    	    elements.add(element);
            }
    	}
    }
    
    private List getObjectsByAddress(String addrId, int maxNumObj) {
        String[] requestedMetadata = new String[2];
        requestedMetadata[0] = Settings.HIT_KEY_OBJ_ID;
        requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        HashMap filter = new HashMap();
        ArrayList result = DetailDataPreparerHelper.getHits("T02_address.adr_id:".concat(addrId).concat(" iplugs:\"".concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter, maxNumObj);
        return result;
    }
    
    
    private void addSubordiantedObjectReferences(List elements, Record record) {
		// max number to show of object references of sub addresses
        int maxNumSubObjRefs = NUM_OBJ_REFS_PER_PAGE;
        try {
        	maxNumSubObjRefs = Integer.parseInt(request.getParameter(REQUEST_PARAM_NAME_MAX_SUB_OBJ_REFS));
        } catch (Exception ex) {}

    	List results = new ArrayList();
    	List refRecords = getSubRecordsByColumnName(record, "children.address_node.addr_uuid");
    	for (int i=0; i<refRecords.size(); i++) {
    		Record refRecord = (Record)refRecords.get(i);
    		results.add(refRecord.getString("address_node.addr_uuid"));
    		results.addAll(getAllAddressChildren(refRecord.getString("address_node.addr_uuid")));
    	}
    	if (results.size() > 0) {
    		List subordiantedObjectReferences = new ArrayList();
            boolean moreHits = false;
        	for (int i=0; i<results.size(); i++) {
            	boolean enoughRead = false;
            	List l = getObjectsByAddress((String)(results.get(i)), maxNumSubObjRefs);
                if (l.size() >= maxNumSubObjRefs) {
                	enoughRead = true;
                }
                for (int j = 0; j < l.size(); j++) {
            		subordiantedObjectReferences.add(l.get(j));
                    if (subordiantedObjectReferences.size() >= maxNumSubObjRefs) {
                    	enoughRead = true;
                    	break;
                    }
                }
                if (enoughRead) {
                	moreHits = true;
                	break;
                }
        	}
        	List linkList = new ArrayList();
        	List objIdList = new ArrayList();
        	for (int i=0; i<subordiantedObjectReferences.size(); i++) {
        		boolean inObjList = false;
        		IngridHit hit = (IngridHit)subordiantedObjectReferences.get(i);
        		IngridHitDetail detail = (IngridHitDetail)hit.get("detail");
        	
        		inObjList = objIdList.contains(detail.getString(Settings.HIT_KEY_OBJ_ID));
        		
        		if(!inObjList){
        			objIdList.add(detail.getString(Settings.HIT_KEY_OBJ_ID));
        			HashMap link = new HashMap();
                	link.put("hasLinkIcon", new Boolean(true));
                	link.put("iconImage", "icn_udk_obj_" + detail.getString(Settings.HIT_KEY_UDK_CLASS) + ".gif");
                	link.put("iconImageAltText", messages.getString("udk_obj_class_name_".concat(detail.getString(Settings.HIT_KEY_UDK_CLASS))));
                	link.put("title", detail.getString("title"));
                	PortletURL actionUrl = response.createActionURL();
                	actionUrl.setParameter("cmd", "doShowObjectDetail");
            		actionUrl.setParameter("objId", detail.getString(Settings.HIT_KEY_OBJ_ID));
            		actionUrl.setParameter("plugid", DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId));
                	link.put("href", actionUrl.toString());
                	linkList.add(link);
        		}      		
        	}
            Collections.sort(linkList, new LinkListComparator());
    		
    		HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("search.detail.dataRelations.addresses"));
        	element.put("linkList", linkList);
        	elements.add(element);

            if (moreHits) {
            	HashMap link = new HashMap();
            	link.put("type", "linkLine");
            	link.put("hasLinkIcon", new Boolean(false));
            	link.put("isExtern", new Boolean(false));
            	link.put("title", messages.getString("search.detail.dataRelations.addresses.next"));
    			PortletURL actionUrl = response.createActionURL();
    	    	actionUrl.setParameter("cmd", "doShowDocument");
    			if (request.getParameter("docid") != null) {
        	    	actionUrl.setParameter("docid", request.getParameter("docid"));
    			}
    			if (request.getParameter("docuuid") != null) {
        	    	actionUrl.setParameter("docuuid", request.getParameter("docuuid"));
    			}
    			actionUrl.setParameter("plugid", iPlugId);
    			// max number to show of object references of sub addresses
    			actionUrl.setParameter("maxSubORs", Integer.toString(maxNumSubObjRefs + NUM_OBJ_REFS_PER_PAGE));
    			// max number to show of object references of direct address
    	        int maxNumObjRefs = NUM_OBJ_REFS_PER_PAGE;
    	        try {
    	        	maxNumObjRefs = Integer.parseInt(request.getParameter(REQUEST_PARAM_NAME_MAX_OBJ_REFS));
    	        } catch (Exception ex) {}
    			actionUrl.setParameter("maxORs", Integer.toString(maxNumObjRefs));
    			link.put("href", actionUrl.toString());
    			
    			// encapsulate in multiLine element to guarantee <p> rendering
		    	element = new HashMap();
		    	element.put("type", "multiLine");
		    	element.put("elements", new ArrayList(Arrays.asList(new HashMap[] { link })));
	    	    elements.add(element);
            }
    	}
    }
    
    private List getAllAddressChildren(String addrId) {
        List results = new ArrayList();
    	List hits = getAddressChildren(addrId);
        for (int i = 0; i < hits.size(); i++) {
            IngridHit hit = (IngridHit) hits.get(i);
            IngridHitDetail detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);
            results.add((String) detail.get(Settings.HIT_KEY_ADDRESS_ADDRID));
            String addrType = (String) detail.get(Settings.HIT_KEY_ADDRESS_CLASS);
            if (addrType.equals("0") || addrType.equals("1")) {
            	results.addAll(getAllAddressChildren((String) detail.get(Settings.HIT_KEY_ADDRESS_ADDRID)));
            }
        }
        return results;
    }
    
    private List getAddressChildren(String addrId) {
        String[] requestedMetadata = new String[] {Settings.HIT_KEY_ADDRESS_ADDRID, Settings.HIT_KEY_ADDRESS_CLASS};
        HashMap filter = new HashMap();
        ArrayList result = DetailDataPreparerHelper.getHits("parent.address_node.addr_uuid:".concat(addrId).concat(" iplugs:\"".concat(DetailDataPreparerHelper.getAddressPlugIdFromPlugId(iPlugId)).concat("\"")),
                requestedMetadata, filter);
        return result;
    }
    
	
}
