package de.ingrid.portal.search.detail.idf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeAlle;
import de.ingrid.utils.udk.TM_PeriodDurationToTimeInterval;
import de.ingrid.utils.udk.UtilsCountryCodelist;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsLanguageCodelist;
import de.ingrid.utils.xml.XPathUtils;

//TODO: remove annotation
@SuppressWarnings("unchecked")
public class DetailDataPreparerIdf1_0_0_Md_Metadata  extends DetailDataPreparerIdf1_0_0{
	
	private final static Log		log	= LogFactory.getLog(DetailDataPreparerIdf1_0_0_Md_Metadata.class);
	
	public enum LinkType{
		EMAIL,
		WWW_URL
	}
	
	public DetailDataPreparerIdf1_0_0_Md_Metadata(Node node, Context context, RenderRequest request) {
		super(node, context, request);
		this.rootNode = node;
		this.context = context;
		this.request = request;
		messages = (IngridResourceBundle) context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}

	
	public void prepare(HashMap data) {
		
		if (rootNode != null) {
			
			initialArrayLists(data);
			// check for hierachyLevelName
			String metadataDataNodePath = "";
			if(context.get(UDK_OBJ_CLASS_TYPE) != null){
				if(context.get(UDK_OBJ_CLASS_TYPE).equals("6")){
					metadataDataNodePath = "srv:SV_ServiceIdentification";
				}else{
					metadataDataNodePath = "gmd:MD_DataIdentification";
				}
			}
			
			String xpathExpression;
			
			// Tab "General"
			// Description
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath ;
			getGeneralTab(elementsGeneral, xpathExpression);
			
			// Addresses			
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:pointOfContact";
			getAddresses(elementsGeneral, xpathExpression);
			
			// Verweise
			addSectionTitle(elementsReference, messages.getString("thesaurus"));
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:topicCategory";
			getIndexInformationTopic(elementsReference, xpathExpression);			
			
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:descriptiveKeywords";
			getIndexInformationKeywords(elementsReference, xpathExpression);			
			closeDiv(elementsReference);
			
			// Tab "Verfügbarkeit"
			xpathExpression = "gmd:distributionInfo/gmd:MD_Distribution";
			getAvailability(elementsAvailability, xpathExpression);
			
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/gmd:resourceConstraints/gmd:MD_LegalConstraints";
			getLimitation(elementsAvailability, xpathExpression);
			
			//Tab "Raum/Zeit"
			//AREA
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/*/gmd:EX_Extent";
			getAreaSection(elementsAreaTime, xpathExpression);
			
			//TIME
			xpathExpression = "gmd:identificationInfo/"+ metadataDataNodePath +"/*/gmd:EX_Extent";
			getTimeSection(elementsAreaTime, xpathExpression);
			
			// Tab "Zusätzliche Info"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath;
			getAdditionalInformation(elementsAdditionalInfo, xpathExpression);
			
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality";
			getDataQuality(elementsAdditionalInfo, xpathExpression);
			
			xpathExpression = "gmd:resourceSpecificUsage/srv:MD_Usage";
			getUtilization(elementsAdditionalInfo, xpathExpression);
			
			// Tab "Fachbezug"
			xpathExpression = "gmd:identificationInfo/" + metadataDataNodePath;
			getTypeOfService(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification";
			getSystemEnvironment(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality";
			getHistory(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:dataQualityInfo/gmd:DQ_DataQuality";
			getBasisData(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification";
			getSubjectDescription(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification";
			getVersion(elementsSubject, xpathExpression);
						
			xpathExpression = "gmd:identificationInfo/gmd:MD_DataIdentification";
			getReferenceObject(elementsSubject, xpathExpression);
			
			xpathExpression = "gmd:identificationInfo/srv:SV_ServiceIdentification/srv:containsOperations";
			getServiceOperations(elementsSubject, xpathExpression);
			
			data.put(DATA_TAB_GENERAL, elementsGeneral);
			data.put(DATA_TAB_REFERENCE, elementsReference);
			data.put(DATA_TAB_AVAILABILITY, elementsAvailability);
			data.put(DATA_TAB_ADDITIIONAL_INFO, elementsAdditionalInfo);
			data.put(DATA_TAB_SUBJECT, elementsSubject);
			data.put(DATA_TAB_AREA_TIME, elementsAreaTime);
		}
	}
	
	
	private void getServiceOperations(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				nodeXPathExpression = "srv:SV_OperationMetadata";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					ArrayList listName = new ArrayList();
					ArrayList listDescr = new ArrayList();
					ArrayList listInvocation = new ArrayList();
					for(int j=0; j<childNodeList.getLength();j++){
						if(XPathUtils.nodeExists(childNodeList.item(j), "srv:operationName")){
							String values = XPathUtils.getString(childNodeList.item(j), "srv:operationName").trim();
							StringTokenizer stValues = new StringTokenizer(values, ",");
							while (stValues.hasMoreTokens()) {
								listName.add(stValues.nextToken());
							}
						}
						
						if(XPathUtils.nodeExists(childNodeList.item(j), "srv:operationDescription")){
							String values = XPathUtils.getString(childNodeList.item(j), "srv:operationDescription").trim();
							StringTokenizer stValues = new StringTokenizer(values, ",");
							while (stValues.hasMoreTokens()) {
								listDescr.add(stValues.nextToken());
							}
						}
						
						if(XPathUtils.nodeExists(childNodeList.item(j), "srv:invocationName")){
							String values = XPathUtils.getString(childNodeList.item(j), "srv:invocationName").trim();
							StringTokenizer stValues = new StringTokenizer(values, ",");
							while (stValues.hasMoreTokens()) {
								listInvocation.add(stValues.nextToken());
							}
						}
					}
					
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
					
					//TODO:Check greatest arrayList
			    	for (int i=0; i<listName.size(); i++) {
			    		ArrayList row = new ArrayList();
			    		if(listName.get(i) != null)
			    			row.add(notNull((String)listName.get(i)));
			    		else
			    			row.add("");
					    
			    		if(listDescr.get(i) != null)
				    		row.add(notNull((String)listDescr.get(i)));
					    else
			    			row.add("");
					    
			    		if(listInvocation.get(i) != null)
				    		row.add(notNull((String)listInvocation.get(i)));
					    else
			    			row.add("");
			    		
					    if (!isEmptyRow(row)) {
					    	body.add(row);
					    }
				    }
			    	if (body.size() > 0) {
			    		elements.add(element);
					   	addSpace(elements);
				    }
				}
			}
		}
	}

	private void getVersion(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				
				nodeXPathExpression = "srv:serviceTypeVersion";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					HashMap element = new HashMap();
	    	    	element.put("type", "textList");
	    	    	element.put("title", messages.getString("t011_obj_serv_version.version"));
	    	    	ArrayList textListEntries = new ArrayList();
	    	    	element.put("textList", textListEntries);
	    	    	for (int i=0; i<childNodeList.getLength();i++){
	    	    		String domainValue = XPathUtils.getString(childNodeList.item(i), ".").trim();
						HashMap listEntry = new HashMap();
	    	        	listEntry.put("type", "textList");
	    	        	listEntry.put("body", domainValue);
	    	        	if (!isEmptyList(listEntry)) {
	    	        		textListEntries.add(listEntry);
	    	        	}
					}
	    	    	elements.add(element);
			    }
			}
		}
	}

	private void getAvailability(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				addSectionTitle(elements, messages.getString("availability"));
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:distributionFormat/gmd:MD_Format";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					NodeList formatNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
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
			    	for (int i=0; i<formatNodeList.getLength(); i++) {
			    		Node childNode = formatNodeList.item(i);
			    		ArrayList row = new ArrayList();
			    		
			    		if(XPathUtils.nodeExists(childNode, "gmd:name")){
			    			String value = XPathUtils.getString(childNode, "gmd:name").trim();
				    		row.add(notNull(value));
			    		}else{
			    			row.add("");
			    		}
			    		
			    		if(XPathUtils.nodeExists(childNode, "gmd:version")){
			    			String value = XPathUtils.getString(childNode, "gmd:version").trim();
				    		row.add(notNull(value));
			    		}else{
			    			row.add("");
			    		}
			    		
			    		if(XPathUtils.nodeExists(childNode, "gmd:fileDecompressionTechnique")){
			    			String value = XPathUtils.getString(childNode, "gmd:fileDecompressionTechnique").trim();
				    		row.add(notNull(value));
			    		}else{
			    			row.add("");
			    		}
			    		
			    		if(XPathUtils.nodeExists(childNode, "gmd:specification")){
			    			String value = XPathUtils.getString(childNode, "gmd:specification").trim();
				    		row.add(notNull(value));
			    		}else{
			    			row.add("");
			    		}

			    		if (!isEmptyRow(row)) {
				    		body.add(row);
				    	}
				    }
			    	if (body.size() > 0) {
			    		elements.add(element);
				    }
				}
				nodeXPathExpression = "gmd:transferOptions/gmd:MD_DigitalTransferOptions";
				getMediumOptions(elements, nodeXPathExpression, node);
				
				nodeXPathExpression = "gmd:distributor/gmd:MD_Distributor";
				getOrderingInformation(elements, nodeXPathExpression, node);
				closeDiv(elements);
			}
		}
	}


	private void getDataQuality(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:report";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					NodeList dataQualityNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
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
			    	for (int i=0; i<dataQualityNodeList.getLength(); i++) {
			    		Node childNode = XPathUtils.getNode(dataQualityNodeList.item(i), "gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation");
			    		ArrayList row = new ArrayList();
			    		row.add(notNull(XPathUtils.getString(childNode, "gmd:title")).trim());
				    	row.add(notNull(XPathUtils.getString(childNode, "gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue")).trim());
				    	row.add(notNull(XPathUtils.getString(childNode, "gmd:date/gmd:CI_Date/gmd:date")).trim());
				    	if (!isEmptyRow(row)) {
				    		body.add(row);
				    	}
				    }
			    	if (body.size() > 0) {
			    		addSpace(elements);
			    		elements.add(element);
					}
				}
			}
		}
	}

	private void getAdditionalInformation(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			addSectionTitle(elements, messages.getString("additional_information"));
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:language/gmd:LanguageCode/@codeListValue";
				if(XPathUtils.nodeExists(rootNode, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(rootNode, nodeXPathExpression);
					String value = XPathUtils.getString(childNode, ".").trim();
			    	addElementEntryLabelLeft(elements,  UtilsLanguageCodelist.getNameFromIso639_2(value, this.request.getLocale().toString()), messages.getString("t01_object.metadata_language"));
			    }
				
				nodeXPathExpression = "gmd:language/gmd:LanguageCode/@codeListValue";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(rootNode, nodeXPathExpression);
			    	String value = XPathUtils.getString(childNode, ".").trim();
					addElementEntryLabelLeft(elements, UtilsLanguageCodelist.getNameFromIso639_2(value, this.request.getLocale().toString()), messages.getString("t01_object.data_language"));
			    }
				
				nodeXPathExpression = "gmd:resourceConstraints/gmd:MD_SecurityConstraints/gmd:classification/gmd:MD_ClassificationCode/@codeListValue";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String value = XPathUtils.getString(childNode, ".").trim();
					String publishId ="";
					if(value.equals("unclassified")){
						publishId="1";
					}else if(value.equals("restricted")){
						publishId="2";
					}else if(value.equals("confidential")){
						publishId="3";
					}
					if(publishId.length() > 0){
						addElementEntryLabelLeft(elements, messages.getString("t01_object.publish_id_" + publishId), messages.getString("t01_object.publish_id"));
					}
				}
				
				nodeXPathExpression = "gmd:resourceSpecificUsage/gmd:MD_Usage/gmd:specificUsage";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.dataset_usage"));
			    }
				
				nodeXPathExpression = "gmd:purpose";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.info_note"));
			    }
			}
		}
	}

	private void getOrderingInformation(ArrayList elements, String xpathExpression, Node node) {
		if(XPathUtils.nodeExists(node, xpathExpression)){
			NodeList orderingNodeList = XPathUtils.getNodeList(node, xpathExpression);
			for (int i=0; i<orderingNodeList.getLength(); i++) { 
				Node childNode = orderingNodeList.item(i);
				String childXPathExpression;
				childXPathExpression = "gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:orderingInstructions";
				if(XPathUtils.nodeExists(childNode, childXPathExpression)){
					String content = XPathUtils.getString(childNode, "gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:orderingInstructions").trim();
					addElementEntryLabelLeft(elements, content, messages.getString("t01_object.ordering_instructions"));
				}
			}
			
		}
	}

	private void getMediumOptions(ArrayList elements, String xpathExpression, Node node) {
		if(XPathUtils.nodeExists(node, xpathExpression)){
			NodeList mediaNodeList = XPathUtils.getNodeList(node, xpathExpression);
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
	    	for (int i=0; i<mediaNodeList.getLength(); i++) {
	    		Node childNode = mediaNodeList.item(i);
	    		ArrayList row = new ArrayList();
	    		String medium;
	    		String childXPathExpression;
	    		
	    		childXPathExpression = "gmd:offLine/gmd:MD_Medium/gmd:name/gmd:MD_MediumNameCode/@codeListValue";
	    		if(XPathUtils.nodeExists(childNode, childXPathExpression)){
	    			medium = XPathUtils.getString(childNode, childXPathExpression).trim();
	    			row.add(notNull(sysCodeList.getNameByCodeListValue("520", medium)));
	    		}else{
	    			row.add("");
	    		}
	    		
	    		childXPathExpression = "gmd:transferSize";
	    		if(XPathUtils.nodeExists(childNode, childXPathExpression)){
	    			row.add(notNull(XPathUtils.getString(childNode, childXPathExpression)).trim());
			    }else{
	    			row.add("");
	    		}
	    		
	    		childXPathExpression = "gmd:offLine/gmd:MD_Medium/gmd:mediumNote";
	    		if(XPathUtils.nodeExists(childNode, childXPathExpression)){
	    			row.add(notNull(XPathUtils.getString(childNode, childXPathExpression)).trim());
	    		}else{
	    			row.add("");
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

	private void getUtilization(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				
				nodeXPathExpression = "specificUsage";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	String domainValue = XPathUtils.getString(childNode, ".").trim();
					addElementEntryLabelLeft(elements, domainValue, messages.getString("t01_object.dataset_usage"));
			    }
			}
		}
	}

	private void getTypeOfService(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				nodeXPathExpression = "srv:serviceType";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String content = XPathUtils.getString(childNode, ".").trim();
			    	addElementEntryLabelLeft(elements, sysCodeList.getNameByCodeListValue("5100", content), messages.getString("t011_obj_serv.type"));
			    }
			}
		}
	}

	private void getSystemEnvironment(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				
				nodeXPathExpression = "gmd:environmentDescription";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t011_obj_serv.environment"));
			    }
			}
		}
	}

	private void getSubjectDescription(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression = "gmd:environmentDescription";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, "."), messages.getString("t011_obj_serv.description"));
			    }
			}
		}
	}

	private void getHistory(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression = "gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, "."), messages.getString("t011_obj_serv.history"));
			    }
			}
		}
	}

	private void getBasisData(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression = "gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, "."), messages.getString("t011_obj_serv.base"));
			    }
			}
		}
	}

	private void getReferenceObject(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:spatialResolution/gmd:MD_Resolution";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					ArrayList listDominator = new ArrayList();
					ArrayList listMeter = new ArrayList();
					ArrayList listDpi = new ArrayList();
					for(int j=0; j<childNodeList.getLength();j++){
						if(XPathUtils.nodeExists(childNodeList.item(j), "gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator")){
							listDominator.add(XPathUtils.getString(childNodeList.item(j), "gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator").trim());	
						}
						
						if(XPathUtils.nodeExists(childNodeList.item(j), "gmd:distance/gco:Distance[@uom='meter']")){
							listMeter.add(XPathUtils.getString(childNodeList.item(j), "gmd:distance/gco:Distance[@uom='meter']").trim());	
						}
						
						if(XPathUtils.nodeExists(childNodeList.item(j), "gmd:distance/gco:Distance[@uom='dpi']")){
							listDpi.add(XPathUtils.getString(childNodeList.item(j), "gmd:distance/gco:Distance[@uom='dpi']").trim());	
						}
					}
					
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
			    	for (int i=0; i<listDominator.size(); i++) {
			    		ArrayList row = new ArrayList();
			    		int value; 
			    		
			    		value = listDominator.size();
			    		if(value != 0){
			    			row.add(notNull((String)listDominator.get(i)));
						}else{
							row.add("");
						}
			    		
			    		value = listMeter.size();
			    		if(value != 0){
			    			row.add(notNull((String)listMeter.get(i)));
						}else{
							row.add("");
						}
						
			    		value = listDpi.size();
			    		if(value != 0){
			    			row.add(notNull((String)listDpi.get(i)));
						}else{
							row.add("");
						}
			    		
					    
					    if (!isEmptyRow(row)) {
					    	body.add(row);
					    }
				    }
			    	if (body.size() > 0) {
			    		elements.add(element);
					   	addSpace(elements);
				    }
				}
			}
		}
	}

	private void getIndexInformationKeywords(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
			ArrayList elementsSearch = new ArrayList();
			ArrayList elementsInspire = new ArrayList();
			ArrayList elementsUmthes = new ArrayList();
			ArrayList elementsGemet = new ArrayList();
			
			for(int i=0; i<nodeList.getLength();i++){
				Node node = nodeList.item(i);
				ArrayList keywordList;
				String type="";
				String thesaurusName="";
				String thesaurusDate="";
				String thesaurusType="";
				String nodeXPathExpression;
				
				//type
		    	nodeXPathExpression = "gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue";
				boolean existType = XPathUtils.nodeExists(node, nodeXPathExpression);
				if(existType){
					type = XPathUtils.getString(node, nodeXPathExpression);
				}
				
				//thesaurus
				nodeXPathExpression = "gmd:MD_Keywords/gmd:thesaurusName";
				boolean existThesaurus = XPathUtils.nodeExists(node, nodeXPathExpression);
				if(existThesaurus){
					thesaurusName = XPathUtils.getString(node, nodeXPathExpression+ "/gmd:CI_Citation/gmd:title").trim();
					thesaurusDate = XPathUtils.getString(node, nodeXPathExpression+ "/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date").trim();
					thesaurusType = XPathUtils.getString(node, nodeXPathExpression+ "/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue").trim();
				}
				
				//keywords
				nodeXPathExpression = "gmd:MD_Keywords/gmd:keyword";
				boolean existKeyword = XPathUtils.nodeExists(node, nodeXPathExpression);
				if(existKeyword){
					keywordList = new ArrayList();
					NodeList keywordNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					for (int j=0; j<keywordNodeList.getLength();j++){
						Node keywordNode = keywordNodeList.item(j);
						String value = XPathUtils.getString(keywordNode, ".").trim();
						keywordList.add(value);
						
						HashMap listEntry = new HashMap();
		    	        listEntry.put("type", "textList");
		    	        listEntry.put("body", value);
		    	        
		    	        if(thesaurusName.equals("Service Classification, version 1.0")){
		    	        	if (!isEmptyList(listEntry)) {
		    	        		elementsSearch.add(listEntry);
			    	        }
						}else if(thesaurusName.equals("UMTHES Thesaurus")){
		    	        	if (!isEmptyList(listEntry)) {
		    	        		elementsUmthes.add(listEntry);
			    	        }
						}else if(thesaurusName.equals("GEMET - Concepts, version 2.1")){
		    	        	if (!isEmptyList(listEntry)) {
		    	        		elementsGemet.add(listEntry);
			    	        }
						}else if(thesaurusName.equals("GEMET - INSPIRE themes, version 1.0")){
		    	        	if (!isEmptyList(listEntry)) {
		    	        		elementsInspire.add(listEntry);
			    	        }
						}else if(type.equals("theme")){
							if (!isEmptyList(listEntry)) {
								elementsInspire.add(listEntry);
			    	        }
						}else {
							if (!isEmptyList(listEntry)) {
								elementsSearch.add(listEntry);
			    	        }
						}
					}
				}
			}
			
			if(elementsSearch.size() > 0){
				HashMap element = new HashMap();
		    	element.put("type", "textList");
		    	element.put("title", messages.getString("search_terms"));
		    	element.put("textList", elementsSearch);
		    	elements.add(element);
		    }
			
			if(elementsInspire.size() > 0){
				HashMap element = new HashMap();
		    	element.put("type", "textList");
		    	element.put("title", messages.getString("inspire_themes"));
		    	element.put("textList", elementsInspire);
		    	elements.add(element);
		    }
			
			if(elementsGemet.size() > 0){
				HashMap element = new HashMap();
		    	element.put("type", "textList");
		    	element.put("title", messages.getString("search_terms"));
		    	element.put("textList", elementsGemet);
		    	elements.add(element);
		    }
			
			
			if(elementsUmthes.size() > 0){
				HashMap element = new HashMap();
		    	element.put("type", "textList");
		    	element.put("title", messages.getString("search_terms"));
		    	element.put("textList", elementsUmthes);
		    	elements.add(element);
		    }
			
		}
	}

	private void getIndexInformationTopic(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			String nodeXPathExpression;
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			HashMap element = new HashMap();
	    	element.put("type", "textList");
	    	element.put("title", messages.getString("t011_obj_geo_topic_cat.topic_category"));
	    	ArrayList textListEntries = new ArrayList();
	    	element.put("textList", textListEntries);
	    	for(int i=0; i<nodeList.getLength();i++){
				nodeXPathExpression = "gmd:MD_TopicCategoryCode";
				if(XPathUtils.nodeExists(nodeList.item(i), nodeXPathExpression)){
					String domainValue = XPathUtils.getString(nodeList.item(i), ".").trim();
					HashMap listEntry = new HashMap();
	    	        listEntry.put("type", "textList");
	    	        listEntry.put("body", sysCodeList.getNameByCodeListValue("527", domainValue));
	    	        if (!isEmptyList(listEntry)) {
	    	        	textListEntries.add(listEntry);
	    	        }
				}
		    }
			elements.add(element);
		}
	}

	private void getLimitation(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:useLimitation";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	String domainValue = XPathUtils.getString(childNode, ".").trim();
					addElementEntryLabelLeft(elements, domainValue, messages.getString("object_access.terms_of_use"));
			    }
			}
		}
	}

	private void getAddresses(ArrayList elements, String xpathExpression) {
		// Addressen
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList elementsAddress = new ArrayList();
			for(int i=0; i<nodeList.getLength();i++){
				Node childNode = nodeList.item(i);
				if(childNode.hasChildNodes()){
					String xpathExpressionContact = "gmd:CI_ResponsibleParty";
					Node subNode = XPathUtils.getNode(childNode, xpathExpressionContact);
					addSingleAddress(elementsAddress, subNode);
				}
			}
			
			String xpathExpressionContact = "gmd:contact/gmd:CI_ResponsibleParty";
			if(XPathUtils.nodeExists(rootNode, xpathExpressionContact)){
				Node subNode = XPathUtils.getNode(rootNode, xpathExpressionContact);
				addSingleAddress(elementsAddress, subNode);
			}
			
			addSpace(elementsGeneral);
			HashMap elementAddress = new HashMap();
			elementAddress.put("type", "multiLineAddresses");
			elementAddress.put("title", messages.getString("addresses"));
			elementAddress.put("id", "addresses_id");
			elementAddress.put("elementsAddress", elementsAddress);
	    	elementsGeneral.add(elementAddress);
		}
	}

	private void addSingleAddress(ArrayList elementsAddress, Node subNode) {
		if(subNode != null){
			HashMap element;
			String xpathExpression;
			
			xpathExpression = "gmd:role/gmd:CI_RoleCode/@codeListValue";
			if(XPathUtils.nodeExists(subNode, xpathExpression)){
				String role = XPathUtils.getString (subNode, xpathExpression).trim();
				element = addElementAddress("multiLine", sysCodeList.getNameByCodeListValue("505", role), "", "false", new ArrayList());
				ArrayList elements = (ArrayList) element.get("elements");
			
				// Organistation
				xpathExpression = "gmd:organisationName";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					String organisationName = XPathUtils.getString (subNode, xpathExpression).trim();
					StringTokenizer stOrganisationName = new StringTokenizer(organisationName, ",");
					int iOrgName= 0;
					while (stOrganisationName.hasMoreTokens()) {
						switch (iOrgName) {
							case 0:
								elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), stOrganisationName.nextToken()));
								iOrgName++;
								break;
							case 1:
								elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), stOrganisationName.nextToken()));
								iOrgName++;
								break;
							default:
								break;
						}
					}
				}
				
				// Name
				xpathExpression = "gmd:individualName";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					String individualName = XPathUtils.getString (subNode, xpathExpression).trim();
					StringTokenizer stIndividualName = new StringTokenizer(individualName, ",");
					int iIndName = 0;
					String firstName = "";
					String lastName = "";
					String nameTitle = "";
					
					while (stIndividualName.hasMoreTokens()) {
						switch (iIndName) {
							case 0:
								lastName = stIndividualName.nextToken();
								iIndName++;
								break;
							case 1:
								firstName = stIndividualName.nextToken();
								iIndName++;
								break;
							case 2:
								StringTokenizer tokenizer = new StringTokenizer(stIndividualName.nextToken(), " ");
								String firstTitle = "";
								String secTitle = "";
								int j = 0;
								while (tokenizer.hasMoreTokens()) {
									switch (j) {
										case 0:
											firstTitle = tokenizer.nextToken();
											j++;
											break;
										case 1:
											secTitle = tokenizer.nextToken();
											j++;
											break;
										default:
											break;
									}
								}
								nameTitle = secTitle.concat(" ").concat(firstTitle); 
								iIndName++;
								break;
							default:
								break;
						}
					}
					elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), nameTitle.concat(" ").concat(firstName).concat(" ").concat(lastName)));
				}
				
				//Delivery point
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					String deliveryPoint = XPathUtils.getString (subNode, xpathExpression).trim();
					addElement(elements, "textLine", deliveryPoint);
				}
				
				
				String postalCode = "";
				String city = "";
				
				// Postcode
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					postalCode = XPathUtils.getString (subNode, xpathExpression).trim();
				}
				
				// City
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					city = XPathUtils.getString (subNode, xpathExpression).trim();
				}
				
				if(postalCode.length() > 0 || city.length() > 0){
					addElement(elements, "textLine", postalCode.concat(" ").concat(city));
				}
				
				// Country
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					String country = XPathUtils.getString (subNode, xpathExpression).trim();
					addElement(elements, "textLine", UtilsCountryCodelist.getNameFromCode(UtilsCountryCodelist.getCodeFromShortcut3(country),this.request.getLocale().toString()));
				}
				addSpace(elements);
				
				//Mail
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					NodeList electronicMailAddressNodeList = XPathUtils.getNodeList(subNode, xpathExpression);
					for (int iMailAddress=0; iMailAddress<electronicMailAddressNodeList.getLength();iMailAddress++){
						String email = XPathUtils.getString(electronicMailAddressNodeList.item(iMailAddress), ".").trim();
						elements.add(addElementEmail("textLinkLine", "Email:", email, email, email, LinkType.EMAIL));
					}
				}
				
				//Phone
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					String value = XPathUtils.getString (subNode, xpathExpression).trim();
					addElement(elements, "textLine", value, "Telefon:");
				}
				
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile";
				if(XPathUtils.nodeExists(subNode, xpathExpression)){
					String value = XPathUtils.getString (subNode, xpathExpression).trim();
					addElement(elements, "textLine", value, "Fax:");
				}
				elementsAddress.add(element);
			}
		}
	}

	private void getTimeSection(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				addSectionTitle(elements, messages.getString("time_reference"));
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String beginPosition = XPathUtils.getString(childNode, "gml:beginPosition").trim();
					String endPosition = XPathUtils.getString(childNode, "gml:endPosition").trim();
			    	String entryLine = "";
					if(beginPosition != null && endPosition != null){
						if(beginPosition.equals(endPosition)){
							entryLine = entryLine.concat(messages.getString("search.detail.time.at")).concat(": ");
							entryLine = entryLine.concat(UtilsDate.convertDateString(beginPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
						}else if(beginPosition != null && endPosition == null){
							entryLine = entryLine.concat(messages.getString("search.detail.time.since")).concat(": ");
							entryLine = entryLine.concat(UtilsDate.convertDateString(beginPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
						}else if(beginPosition == null && endPosition != null){
							entryLine = entryLine.concat(messages.getString("search.detail.time.to")).concat(": ");
							entryLine = entryLine.concat(UtilsDate.convertDateString(endPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
						}else if(!beginPosition.equals(endPosition)){
							entryLine = entryLine.concat(messages.getString("search.detail.time.from")).concat(": ");
							entryLine = entryLine.concat(UtilsDate.convertDateString(beginPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
							entryLine = entryLine.concat(" ");
							entryLine = entryLine.concat(messages.getString("search.detail.time.to")).concat(": ");
							entryLine = entryLine.concat(UtilsDate.convertDateString(endPosition, "yyyy-MM-dd", "dd.MM.yyyy"));
						}
						if(entryLine.length() >0){
							addElementEntryLabelLeft(elements, entryLine, messages.getString("t01_object.loc_descr"));	
						}
					}
			    }
				
				nodeXPathExpression = "../../gmd:status/gmd:MD_ProgressCode";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	String domainValue = XPathUtils.getString(childNode, "./@codeListValue").trim();
					addElementEntryLabelLeft(elements, sysCodeList.getNameByCodeListValue("523", domainValue), messages.getString("t01_object.time_status"));
			    }
									
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	String domainValue = XPathUtils.getString(childNode, "./@codeListValue").trim();
					addElementEntryLabelLeft(elements, sysCodeList.getNameByCodeListValue("518", domainValue), messages.getString("t01_object.time_period"));
			    }
				
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:userDefinedMaintenanceFrequency";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
					String content = XPathUtils.getString(childNode, ".").trim();
					String value = new TM_PeriodDurationToTimeAlle().parse(content);
					String unit = new TM_PeriodDurationToTimeInterval().parse(content);
			    	addElementEntryLabelLeft(elements, value.concat(" ").concat(unit) , messages.getString("t01_object.time_interval"));
			    }
				
				nodeXPathExpression = "../../gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date";					
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
					for(int i=0; i<childNodeList.getLength();i++){
						String time = XPathUtils.getString(childNodeList.item(i), "gmd:date").trim();
						String type = XPathUtils.getString(childNodeList.item(i), "gmd:dateType/gmd:CI_DateTypeCode/@codeListValue").trim();
						String typeId = "";
						if(type.equals("creation")){
							typeId = "1";
						}else if(type.equals("revision")){
							typeId = "3";
						}
						
						if(time.indexOf("T") == -1){
							addElementEntryLabelLeft(elements, UtilsDate.convertDateString(time, "yyyy-MM-dd", "dd.MM.yyyy"), sysCodeList.getName("502", typeId));
						}else{
							addElementEntryLabelLeft(elements, UtilsDate.convertDateString(time.replaceAll("T",""), "yyyy-MM-ddHH:mm:ss", "dd.MM.yyyy"), sysCodeList.getName("502", typeId));	
						}
					}
			    }
				
				nodeXPathExpression = "../../gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceNote";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
			    	addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.time_descr"));
			    }
				closeDiv(elements);
			}
		}
	}

	private void getAreaSection(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				addSectionTitle(elements, messages.getString("t011_obj_geo.coord"));
				String nodeXPathExpression;
				NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
				for(int i=0; i<nodeList.getLength();i++){
					nodeXPathExpression = "gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code";
					node = nodeList.item(i);
					if(XPathUtils.nodeExists(node, nodeXPathExpression)){
						NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
						HashMap element = new HashMap();
		    	    	element.put("type", "textList");
		    	    	element.put("title", messages.getString("t011_township.township_no"));
		    	    	ArrayList textListEntries = new ArrayList();
		    	    	element.put("textList", textListEntries);
		    	    	for (int j=0; j<childNodeList.getLength();j++){
		    	    		String domainValue = XPathUtils.getString(childNodeList.item(j), ".").trim();
							HashMap listEntry = new HashMap();
		    	        	listEntry.put("type", "textList");
		    	        	listEntry.put("body", domainValue);
		    	        	if (!isEmptyList(listEntry)) {
		    	        		textListEntries.add(listEntry);
		    	        	}
						}
		    	    	elements.add(element);

				    }
					
					nodeXPathExpression = "gmd:geographicElement/gmd:EX_GeographicBoundingBox";
					if(XPathUtils.nodeExists(node, nodeXPathExpression)){
						NodeList subNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
						HashMap element = new HashMap();
				    	element.put("type", "table");
				    	element.put("title", messages.getString("geothesaurus_spacial_reference"));
						
				    	ArrayList head = new ArrayList();
				    	head.add(messages.getString("geothesaurus_spacial_reference"));
						head.add(messages.getString("spatial_ref_value_x1"));
						head.add(messages.getString("spatial_ref_value_y1"));
						head.add(messages.getString("spatial_ref_value_x2"));
						head.add(messages.getString("spatial_ref_value_y2"));
						element.put("head", head);
						ArrayList body = new ArrayList();
						element.put("body", body);
				    	for (int j=0; j<subNodeList.getLength(); j++) {
				    		Node childNode = subNodeList.item(j);
				    		ArrayList row = new ArrayList();
				    		row.add(notNull(XPathUtils.getString(childNode, "gmd:extentTypeCode")).trim());
					    	row.add(notNull(XPathUtils.getString(childNode, "gmd:westBoundLongitude")).trim());
					    	row.add(notNull(XPathUtils.getString(childNode, "gmd:southBoundLatitude")).trim());
					    	row.add(notNull(XPathUtils.getString(childNode, "gmd:eastBoundLongitude")).trim());
					    	row.add(notNull(XPathUtils.getString(childNode, "gmd:northBoundLatitude")).trim());
					    	if (!isEmptyRow(row)) {
					    		body.add(row);
					    	}
					    }
					    
				    	if (body.size() > 0) {
				    		elements.add(element);
					    }
				    }
					
					//TODO: "Region oder Naturraum" missing
					nodeXPathExpression = "gmd:description";
					if(XPathUtils.nodeExists(node, nodeXPathExpression)){
						NodeList childNodeList = XPathUtils.getNodeList(node, nodeXPathExpression);
						HashMap element = new HashMap();
		    	    	element.put("type", "textList");
		    	    	element.put("title", messages.getString(messages.getString("t019_coordinates.bezug")));
		    	    	ArrayList textListEntries = new ArrayList();
		    	    	element.put("textList", textListEntries);
		    	    	for (int ij=0; ij<childNodeList.getLength();ij++){
		    	    		String domainValue = XPathUtils.getString(childNodeList.item(ij), ".").trim();
							HashMap listEntry = new HashMap();
		    	        	listEntry.put("type", "textList");
		    	        	listEntry.put("body", domainValue);
		    	        	if (!isEmptyList(listEntry)) {
		    	        		textListEntries.add(listEntry);
		    	        	}
						}
		    	    	elements.add(element);
		    	    }
					
					nodeXPathExpression = "gmd:verticalElement/gmd:EX_VerticalExtent";
					if(XPathUtils.nodeExists(node, nodeXPathExpression)){
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
				    	for (int j=0; j<nodeList.getLength(); j++) {
				    		Node childNode = nodeList.item(j);
				    		ArrayList row = new ArrayList();
				    		row.add(notNull(XPathUtils.getString(childNode, "gmd:maximumValue")).trim());
					    	row.add(notNull(XPathUtils.getString(childNode, "gmd:minimumValue")).trim());
					    	String rowValue;
					    	rowValue = XPathUtils.getString(childNode, "gmd:verticalCRS/gml:VerticalCRS/gml:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@gml:uom").trim();
					    	row.add(notNull(sysCodeList.getNameByCodeListValue("102", rowValue)));
					    	rowValue = XPathUtils.getString(childNode, "gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:name").trim();
					    	row.add(notNull(sysCodeList.getNameByCodeListValue("101", rowValue)));
					    	if (!isEmptyRow(row)) {
					    		body.add(row);
					    	}
					    }
					    
				    	if (body.size() > 0) {
				    		elements.add(element);
						 }

				    }
					
					nodeXPathExpression = "gmd:description";
					if(XPathUtils.nodeExists(node, nodeXPathExpression)){
						Node childNode = XPathUtils.getNode(node, nodeXPathExpression);
				    	addElementEntryLabelLeft(elements, XPathUtils.getString(childNode, ".").trim(), messages.getString("t01_object.loc_descr"));
				    }
				}
				closeDiv(elements);
			}
		}
	}

	private void getGeneralTab(ArrayList elements, String xpathExpression) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if(node.hasChildNodes()){
				String nodeXPathExpression;
				nodeXPathExpression = "gmd:abstract";
				
				// Beschreibung
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					String alternateName = "";
					String description = "";
					
					// alternate name
					nodeXPathExpression = "gmd:citation/gmd:CI_Citation/gmd:alternateTitle";
					if(XPathUtils.nodeExists(node, nodeXPathExpression)){
						alternateName = XPathUtils.getString (node, nodeXPathExpression).trim();
					}
					
					// description
					nodeXPathExpression = "gmd:abstract";
					if(XPathUtils.nodeExists(node, nodeXPathExpression)){
						description = XPathUtils.getString (node, nodeXPathExpression).trim();
					
						if (description != null) {
							description = description.replaceAll("\n", "<br/>");
							description = description.replaceAll("<(?!b>|/b>|i>|/i>|u>|/u>|p>|/p>|br>|br/>|br />|strong>|/strong>|ul>|/ul>|ol>|/ol>|li>|/li>)[^>]*>", "");
				        }
					}
					if((description.length() > 0) || alternateName.length() > 0){
						addSectionTitle(elements, messages.getString("detail_description"));
						addElementEntryLabelAbove(elements, description, alternateName, false);
						// TODO: superior objects
						// addSuperiorObjects(elementsGeneral, listSuperiorObjects);
						// TODO: subordinated objects
						//addSubordinatedObjects(elementsGeneral, listSubordinatedObjects);
						// close description
						closeDiv(elements);
					}
				}
				
				nodeXPathExpression = "gmd:descriptiveKeywords";
				if(XPathUtils.nodeExists(node, nodeXPathExpression)){
					addReference(node);
				}
			}
		}
	}

	private HashMap addReference(Node node) {
		ArrayList keywords = new ArrayList();
		if(XPathUtils.nodeExists(node, "//gmd:keyword")){
			NodeList nodeListKeywords = XPathUtils.getNodeList(node, "//gmd:keyword");
			for (int i=0; i<nodeListKeywords.getLength();i++){
				Node keywordNode = nodeListKeywords.item(i);
				HashMap listEntry = new HashMap();
    			listEntry.put("type", "textList");
    			listEntry.put("body", XPathUtils.getString (keywordNode, ".").trim());
    			keywords.add(listEntry);
			}
		}
		String type = XPathUtils.getString (node, "//gmd:MD_KeywordTypeCode/@codeListValue").trim();
		HashMap element = new HashMap();
		element.put("type", "textList");
		element.put("title", messages.getString("cross_references"));
		element.put("textList", keywords);
		
		return element;
	}

	private HashMap addElementEmail(String type, String title, String href, String body, String altText, LinkType linkType) {
		HashMap element = new HashMap();
		element.put("type", "textLinkLine");
		element.put("title", title);
		element.put("href", "mailto:".concat(UtilsString.htmlescapeAll(href)));
		element.put("body", UtilsString.htmlescapeAll(body));
		element.put("altText", UtilsString.htmlescapeAll(altText));
		switch (linkType) {
			case EMAIL:
				element.put("href", "mailto:".concat(UtilsString.htmlescapeAll(href)));
				break;
			case WWW_URL:
				if (title.startsWith("http")) {
					element.put("href", href);
				} else {
					element.put("href", "http://".concat(href));
				}
				break;
			default:
				break;
		}
		
		return element;
	}

	private void addElement(ArrayList elements, String type, String body) {
		addElement(elements, type, body, null);
	}
	
	private void addElement(ArrayList elements, String type, String body, String title) {
		HashMap element = new HashMap();
		if(type != null)
			element.put("type", type);
		if(body != null)
			element.put("body", body);
		if(title != null)
			element.put("title", title);
		
		elements.add(element);
	}

	private HashMap addElementLink(String type, Boolean hasLinkIcon, Boolean isExtern, String title) {
		HashMap element = new HashMap();
		if(type != null)
			element.put("type", type);
		if(title != null)
			element.put("title", title);
		if(hasLinkIcon)
			element.put("sort", hasLinkIcon);
		if(isExtern != null)
			element.put("isExtern", isExtern);
		
		return element;
	}

	private HashMap addElementAddress(String type, String title, String body, String sort, ArrayList elements){
		HashMap element = new HashMap();
		if(type != null)
			element.put("type", type);
		if(title != null)
			element.put("title", title);
		if(sort != null)
			element.put("sort", sort);
		if(body != null)
			element.put("body", body);
		if(elements != null)
			element.put("elements", elements);
		
		return element;
	}
}
