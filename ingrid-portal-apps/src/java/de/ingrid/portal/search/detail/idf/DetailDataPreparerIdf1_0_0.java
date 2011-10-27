package de.ingrid.portal.search.detail.idf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsVelocity;

//TODO: remove annotation
@SuppressWarnings("unchecked")
public class DetailDataPreparerIdf1_0_0 {
	
	private final static Logger	log							= LoggerFactory.getLogger(DetailDataPreparerIdf1_0_0.class);
	
	public final static String	DATA_TAB_GENERAL			= "elementsGeneral";
	public final static String	DATA_TAB_REFERENCE			= "elementsReference";
	public final static String	DATA_TAB_AREA_TIME			= "elementsAreaTime";
	public final static String	DATA_TAB_SUBJECT			= "elementsSubject";
	public final static String	DATA_TAB_AVAILABILITY		= "elementsAvailability";
	public final static String	DATA_TAB_ADDITIONAL_INFO	= "elementsAdditionalInfo";
	public final static String	DATA_TAB_ADDITIONAL_FIELD	= "elementsAdditionalField";
	public final static String	DATA_TAB_DATA_QUALITY		= "elementsDataQuality";
	
	public Node					rootNode;
	public NodeList				nodeList;
	public Context				context;
	public IngridResourceBundle	messages;
	public IngridSysCodeList	sysCodeList;
	public RenderRequest		request;
	public RenderResponse		response;
	public String				iPlugId;
	
	public ArrayList			elementsGeneral;
	public ArrayList			elementsReference;
	public ArrayList			elementsAreaTime;
	public ArrayList			elementsSubject;
	public ArrayList			elementsAvailability;
	public ArrayList			elementsAdditionalInfo;
	public ArrayList			elementsAdditionalField;
	public ArrayList			elementsDataQuality;
	
	public HashMap content;
	
	public enum LinkType {
		EMAIL, WWW_URL
	}
	
	public enum ReferenceType {
		SUBORDINATE, SUPERIOR, CROSS, OBJECT
	}
	
	public enum LabelType {
		LEFT, ABOVE, DURING
	}
	
	public enum TimeSpatialType {
		TIME, SPATIAL
	}
	
	
	public DetailDataPreparerIdf1_0_0(Node node, Context context, RenderRequest request, String iPlugId, RenderResponse response) {
		this.rootNode = node;
		this.context = context;
		this.request = request;
		this.response = response;
		messages = (IngridResourceBundle) context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	public void prepare(ArrayList data) {
		
	}
	
	public void generateHeader(ArrayList data){
		if (rootNode != null) {
			initialArrayLists();
		}
	}
	
	public void initialArrayLists() {
		if(content==null){
			content = new HashMap();
		}
		
		this.elementsGeneral = (ArrayList) content.get(DATA_TAB_GENERAL);
		if (this.elementsGeneral == null)
			this.elementsGeneral = new ArrayList();
		
		this.elementsReference = (ArrayList) content.get(DATA_TAB_REFERENCE);
		if (this.elementsReference == null)
			this.elementsReference = new ArrayList();
		
		this.elementsAreaTime = (ArrayList) content.get(DATA_TAB_AREA_TIME);
		if (this.elementsAreaTime == null)
			this.elementsAreaTime = new ArrayList();
		
		this.elementsSubject = (ArrayList) content.get(DATA_TAB_SUBJECT);
		if (this.elementsSubject == null)
			this.elementsSubject = new ArrayList();
		
		this.elementsAvailability = (ArrayList) content.get(DATA_TAB_AVAILABILITY);
		if (this.elementsAvailability == null)
			this.elementsAvailability = new ArrayList();
		
		this.elementsAdditionalInfo = (ArrayList) content.get(DATA_TAB_ADDITIONAL_INFO);
		if (this.elementsAdditionalInfo == null)
			this.elementsAdditionalInfo = new ArrayList();
		
		this.elementsAdditionalField = (ArrayList) content.get(DATA_TAB_ADDITIONAL_FIELD);
		if (this.elementsAdditionalField == null)
			this.elementsAdditionalField = new ArrayList();
		
		this.elementsDataQuality = (ArrayList) content.get(DATA_TAB_DATA_QUALITY);
		if (this.elementsDataQuality == null)
			this.elementsDataQuality = new ArrayList();
	}
	
	public void addElementEntry(List elements, String body, String title) {
		addElementEntry(elements, body, title, null);
	}
	
	public void addElementEntry(List elements, String body, String title, String alternateName) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "entry");
			if (title == null) {
				element.put("header", messages.getString("detail_description"));
			}
			if (alternateName == null) {
				element.put("title", title);
			} else {
				element.put("title", alternateName);
			}
			// show line breaks correctly in HTML
			element.put("body", body.replaceAll("\n", "<br />"));
			elements.add(element);
		}
	}
	
	public void addElementEntryLabelLeft(List elements, String body, String title) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "textLabelLeft");
			element.put("title", title);
			element.put("body", body);
			elements.add(element);
		}
	}
	
	public void addElementEntryLabelAbove(List elements, String body, String title, boolean line) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "textLabelAbove");
			element.put("title", title);
			element.put("line", line);
			element.put("body", body);
			elements.add(element);
		}
	}
	
	public void addElementEntryLabelDuring(List elements, String body, String title) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "textLabelDuring");
			element.put("title", title);
			element.put("body", body);
			elements.add(element);
		}
	}
	
	public void addElementUdkClass(List elements, String udkClass) {
		if (UtilsVelocity.hasContent(udkClass).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "udkClass");
			element.put("udkClass", udkClass);
			element.put("udkClassName", messages.getString("udk_obj_class_name_".concat(udkClass)));
			elements.add(element);
		}
	}
	
	public void addSuperiorObjects(List elements, List linkList) {
		if (!linkList.isEmpty()) {
			HashMap element = new HashMap();
			element.put("type", "linkList");
			element.put("title", messages.getString("superior_references"));
			element.put("linkList", linkList);
			elements.add(element);
		}
	}
	
	public void addReferenceToElement(List elements, List linkList, String sectionTitle, String title) {
		if (!linkList.isEmpty()) {
			if(sectionTitle != null){
				addSectionTitle(elements, sectionTitle);	
			}
			HashMap element = new HashMap();
			element.put("type", "linkList");
			element.put("title", title);
			element.put("linkList", linkList);
			elements.add(element);
			if(sectionTitle != null){
				closeDiv(elements);	
			}
		}
	}
	
	public void addLine(List elements) {
		HashMap element = new HashMap();
		element.put("type", "line");
		elements.add(element);
	}
	
	public void addSpace(List elements) {
		HashMap element = new HashMap();
		element.put("type", "space");
		elements.add(element);
	}
	
	public void openDiv(List elements) {
		HashMap element = new HashMap();
		element.put("type", "beginnDiv");
		elements.add(element);
	}
	
	public void closeDiv(List elements) {
		HashMap element = new HashMap();
		element.put("type", "endDiv");
		elements.add(element);
	}
	
	public void addSectionTitle(List elements, String title) {
		HashMap element = new HashMap();
		element.put("type", "section");
		element.put("title", title);
		elements.add(element);
		openDiv(elements);
	}
	
	public void addGroupTitle(List elements, String title) {
		HashMap element = new HashMap();
		element.put("type", "group");
		element.put("title", title);
		elements.add(element);
	}
	
	public boolean isEmptyRow(List row) {
		for (int i = 0; i < row.size(); i++) {
			if (row.get(i) != null && row.get(i) instanceof String && ((String) row.get(i)).length() > 0) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmptyLine(HashMap line) {
		if (line.get("type") != null && (line.get("type").equals("textLine") || line.get("type").equals("textLabelLeft"))) {
			if (line.get("body") != null && line.get("body") instanceof String && ((String) line.get("body")).length() > 0) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmptyList(HashMap listEntry) {
		if (listEntry.get("type") != null && listEntry.get("type").equals("textList") || listEntry.get("type").equals("linkList")) {
			if (listEntry.get("body") != null && listEntry.get("body") instanceof String && ((String) listEntry.get("body")).length() > 0) {
				return false;
			}
		}
		return true;
	}
	
	public String notNull(String in) {
		if (in == null) {
			return "";
		} else {
			return in;
		}
	}
	
	public String getValueForNode(Node node) {
		return node.getNodeValue().trim();
	}
	
	public String getNameForNode(Node node) {
		return node.getNodeName().trim();
	}
	
	public String getIndividualName(String value) {
		String[] valueSpitter = value.split(",");
		
		String name = "";
		for (int j=valueSpitter.length; 0 < j ;j--){
			name = name + " " + valueSpitter[j-1];
		}	
		return name;
	}

	
}
