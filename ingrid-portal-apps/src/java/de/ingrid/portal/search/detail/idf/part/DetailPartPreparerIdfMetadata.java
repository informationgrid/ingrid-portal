package de.ingrid.portal.search.detail.idf.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.search.detail.idf.DetailDataPreparerIdf1_0_0.LinkType;
import de.ingrid.utils.udk.UtilsCountryCodelist;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

public class DetailPartPreparerIdfMetadata extends DetailPartPreparer{

	@Override
	public void init(Node node, String iPlugId, RenderRequest request, RenderResponse response, Context context) {
		this.rootNode = node;
		this.iPlugId = iPlugId;
		this.request = request;
		this.response = response;
		this.templateName = "/WEB-INF/templates/detail/part/detail_part_preparer_metadata.vm";
		this.namespaceUri = IDFNamespaceContext.NAMESPACE_URI_IDF;
		this.localTagName = "idfMdMetadata";
		this.context = context;
		this.messages = (IngridResourceBundle) context.get("MESSAGES");
		this.sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	public String getUdkObjectClassType() {
		String xpathExpression = ".";
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if(node.hasChildNodes()){
                String hierachyLevel = "";
                String hierachyLevelName = "";
                
                xpathExpression = "./gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue";
                if(XPathUtils.nodeExists(node, xpathExpression)){
                    hierachyLevel = XPathUtils.getString(node, xpathExpression).trim();
                }
                
                xpathExpression = "./gmd:hierarchyLevelName";
                if(XPathUtils.nodeExists(node, xpathExpression)){
                    hierachyLevelName = XPathUtils.getString(node, xpathExpression).trim();
                }
                
                if(hierachyLevel.equalsIgnoreCase("service")){
                    return "3";
                }else if(hierachyLevel.equalsIgnoreCase("application")){
                    return "6";
                }else if(hierachyLevelName.equalsIgnoreCase("job") && hierachyLevel.equals("nonGeographicDataset")){
                    return "0";
                }else if(hierachyLevelName.equalsIgnoreCase("document") && hierachyLevel.equals("nonGeographicDataset")){
                    return "2";
                }else if(hierachyLevelName.equalsIgnoreCase("project") && hierachyLevel.equals("nonGeographicDataset")){
                    return "4";
                }else if(hierachyLevelName.equalsIgnoreCase("database") && hierachyLevel.equals("nonGeographicDataset")){
                    return "5";
                }else if(hierachyLevel.equalsIgnoreCase("dataset") || hierachyLevel.equals("series")){
                    return "1";
                }else if(hierachyLevel.equalsIgnoreCase("tile")){
                    // tile should be mapped to "Geoinformation/Karte" explicitly, see INGRID-2225
                    return "1";
                } else {
                    // Default to "Geoinformation/Karte", see INGRID-2225
                    return "1";
                }
            }
        }
        return "1"; 
    }
	
	public String getTitle(){
		String value = null;
		String xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title";
		Node node = XPathUtils.getNode(this.rootNode, xpathExpression);
        if(node.getTextContent().length() > 0){
        	value = node.getTextContent();
        }
		return value;
	}
	
	public String getTimeStamp(String xpathExpression){
		String value = null;
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			String modTime = "";
			if(XPathUtils.nodeExists(node, "./gco:DateTime")){
				modTime = UtilsDate.convertDateString(XPathUtils.getString(node, "./gco:DateTime").trim(), "yyyy-MM-dd", "dd.MM.yyyy");
			}else if(XPathUtils.nodeExists(node, "./gco:Date")){
				modTime = UtilsDate.convertDateString(XPathUtils.getString(node, "./gco:Date").trim(), "yyyy-MM-dd", "dd.MM.yyyy");
			}
			if(modTime.length() > 0){
				value = modTime;	
			}
		}
		return value;
	}
	
	public String getDescription(){
		String value = null;
		
		String xpathExpressionAbstract = "./idf:abstract";
        Node abstractParentNode = rootNode;
        Node node = XPathUtils.getNode(rootNode, "./gmd:identificationInfo/*");
        
		if (!XPathUtils.nodeExists(abstractParentNode, xpathExpressionAbstract)) {
            xpathExpressionAbstract = "./gmd:abstract";
            abstractParentNode = node;
        }
		value = XPathUtils.getString(abstractParentNode, xpathExpressionAbstract).trim();                    

		return value;
	}
	
	public HashMap<String, Object> getMapImage(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		String xpathExpression = "";
		String capabilitiesUrl = getCapabilityUrl();
        // showMap/Preview-Link
        if (getUdkObjectClassType().equals("1") || getUdkObjectClassType().equals("3")) {
            if (getUdkObjectClassType().equals("1")) {
                // search for it in onlineResources
                xpathExpression = "./gmd:distributionInfo/*/gmd:transferOptions";
                map = getCapabilityUrls(xpathExpression);
            }else if (getUdkObjectClassType().equals("3")) {
            	 if (capabilitiesUrl != null) {
                    // get it directly from the operation
            		 map = addBigMapLink(capabilitiesUrl);
            	 }
            }
        } else {
            // show preview image (with map link information if provided)
            xpathExpression = "./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/gco:CharacterString";
            map = getPreviewImage(xpathExpression);
        }
        return map;
	}
	
	public ArrayList<HashMap<String, Object>> getReference(String xpathExpression){
		return getReference(xpathExpression, false); 
	}
		
	public ArrayList<HashMap<String, Object>> getReference(String xpathExpression, boolean isCoupled){
		ArrayList<HashMap<String, Object>> linkList = new ArrayList<HashMap<String, Object>>();
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i<nodeList.getLength();i++){
				Node node = nodeList.item(i);
				String uuid = "";
				String title = "";
				String type = "";
				String attachedToField = "";
				String entryId = "";
				String serviceType = "";
				String description = "";
				
				xpathExpression = "./@uuid";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					uuid = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				xpathExpression = "./idf:objectName";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					title = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				xpathExpression = "./idf:objectType";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					type = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				xpathExpression = "./idf:attachedToField";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					attachedToField = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				xpathExpression = "./idf:attachedToField/@entry-id";
                if(XPathUtils.nodeExists(node, xpathExpression)){
                    entryId = XPathUtils.getString(node, xpathExpression);
                }
                
                xpathExpression = "./gmd:identificationInfo/*/srv:serviceType";
                if(XPathUtils.nodeExists(this.rootNode, xpathExpression)){
                	serviceType = XPathUtils.getString(rootNode, xpathExpression);
                }
				
				xpathExpression = "./idf:description";
				if(XPathUtils.nodeExists(node, xpathExpression)){
					description = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				HashMap<String, Object> link = new HashMap<String, Object>();
	        	link.put("hasLinkIcon", new Boolean(true));
	        	link.put("isExtern", new Boolean(false));
	        	link.put("title", title);
	        	link.put("objectClass", type);
  	        	if (description.length() > 0) {
  	        		link.put("description", description);
  	        	}
  	        	if (attachedToField.length() > 0) {
  	        		link.put("attachedToField", attachedToField);
  	        	}          	        	
	        	if(this.iPlugId != null){
	        		if(uuid != null){
	        			PortletURL actionUrl = response.createActionURL();
			        	actionUrl.setParameter("cmd", "doShowDocument");
			    		actionUrl.setParameter("docuuid", uuid);
			    		actionUrl.setParameter("plugid", this.iPlugId);
			    		link.put("href", actionUrl.toString());
			        	
		        	}else{
		        		link.put("href", "");
		        	}
	        	}else{
	        		link.put("href", "");
	        	}
	        	
	        	if(isCoupled){
	        		// add map links to data objects from services
		        	if (entryId.equals("3600") && getUdkObjectClassType().equals("3")) {
		        	    // get link from operation (unique one)
		        	    if (serviceType.trim().equals("view")) {
		        	        link.put("mapLink", getCapabilityUrl() + "&ID=" + getLayerIdentifier(node));
		        	    }
		        	    // do not show link relation for coupled resources (INGRID-2285)
		        	    link.remove("attachedToField");
		        	    linkList.add(link);
		        	} else if (entryId.equals("3600") && getUdkObjectClassType().equals("1")) {
		        	    if (getCapabilityUrl() != null) {
		        	        // get link from online resource (possibilty it's wrong?)
		        	        link.put("mapLink",  getCapabilityUrl() + "&ID=" + getLayerIdentifier(node));
		        	    }
		        	    // do not show link relation for coupled resources (INGRID-2285)
		        	    link.remove("attachedToField");
		        	    linkList.add(link);
		        	}
	        	}else{
	        		if(!entryId.equals("3600")){
	        			linkList.add(link);
	        		}
	        	}
			}
		}
		return linkList;
	}
	
	public ArrayList<HashMap<String, Object>> getExternLinks(String xpathExpression) {
		ArrayList<HashMap<String, Object>> linkList = new ArrayList<HashMap<String, Object>>();
    	if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i<nodeList.getLength();i++){
        		String url = "";
        		String name = "";
        		String description = "";
        		String attachedToField = "";
        		String size = "";
        		float roundSize = 0;
        		
        		NodeList onLineList = XPathUtils.getNodeList(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine");
        		
        		for (int j=0; j<onLineList.getLength();j++){
        		    
        		//if(XPathUtils.nodeExists(nodeList.item(i),"./gmd:MD_DigitalTransferOptions/gmd:onLine")){
        			xpathExpression = "./*/gmd:linkage/gmd:URL";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			url = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}
            		
            		xpathExpression = "./*/gmd:description";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			description = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}
            		
            		xpathExpression = "./*/gmd:name";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			name = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}
            		
            		xpathExpression = "./*/idf:attachedToField";
            		if(XPathUtils.nodeExists(onLineList.item(j), xpathExpression)){
            			attachedToField = XPathUtils.getString(onLineList.item(j), xpathExpression).trim();
            		}

            		// also mapped by T0112_media_option !!!
            		xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:transferSize";
            		if(XPathUtils.nodeExists(nodeList.item(i), xpathExpression)){
            			size = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
            			roundSize = Float.valueOf(size).floatValue();
            			roundSize = (float) (Math.round(roundSize * 1000) / 1000.0);
            		}
            		
            		
            		if(url.length() > 0){
            			HashMap<String, Object> link = new HashMap<String, Object>();			
        				link.put("hasLinkIcon", new Boolean(true));
          	        	link.put("isExtern", new Boolean(true));
          	        	link.put("href", url);

          	        	if(name.length() > 0){
          	        		link.put("title", name);
          	  	        }else{
          	  	        	link.put("title", url);
          	        	}
          	        	if (description.length() > 0) {
          	        		link.put("description", description);
          	        	}
          	        	if (attachedToField.length() > 0) {
          	        		link.put("attachedToField", attachedToField);
          	        	}          	        	
          	        	if (size.length() > 0) {
          	        		link.put("linkInfo", "[" + roundSize + " MB]");
          	        	}
          	        	
          	        	linkList.add(link);
            		}
        		}
        	}
		}
		return linkList;
	}
	
	public ArrayList<String> getIndexInformationKeywords(String xpathExpression, String keywordType) {
		ArrayList<String> listSearch = new ArrayList<String>();
		ArrayList<String> listGemet = new ArrayList<String>();
		ArrayList<String> listInspire = new ArrayList<String>();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String type = "";
				String thesaurusName = "";
				
				// type
				xpathExpression = "./gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					type = XPathUtils.getString(node, xpathExpression);
				}
				
				// thesaurus
				xpathExpression = "./gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					thesaurusName = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				// keywords
				xpathExpression = "./gmd:MD_Keywords/gmd:keyword";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList keywordNodeList = XPathUtils.getNodeList(node, xpathExpression);
					for (int j = 0; j < keywordNodeList.getLength(); j++) {
						Node keywordNode = keywordNodeList.item(j);
						String value = XPathUtils.getString(keywordNode, ".").trim(); 
						if(value.length() < 1){
							value = XPathUtils.getString(keywordNode, ".").trim();
						}
						
						// "Service Classification, version 1.0"
						if (thesaurusName.indexOf("Service") < 0) {
							// "UMTHES Thesaurus"
							if (thesaurusName.indexOf("UMTHES") > -1) {
								listSearch.add(value);
							// "GEMET - Concepts, version 2.1"
							} else if (thesaurusName.indexOf("Concepts") > -1) {
								String tmpValue = sysCodeList.getNameByCodeListValue("5200", value);
								if(tmpValue.length() < 1){
									tmpValue = value;
								}
								listGemet.add(tmpValue);
								// "GEMET - INSPIRE themes, version 1.0"
							} else if (thesaurusName.indexOf("INSPIRE") > -1) {
								String tmpValue = sysCodeList.getNameByCodeListValue("6100", value);
								if(tmpValue.length() < 1){
									tmpValue = value;
								}
								listInspire.add(tmpValue);
								// "German Environmental Classification - Category, version 1.0"
							} else if (thesaurusName.indexOf("German Environmental Classification") > -1) {
								// do not used in detail view.
								
							} else if (thesaurusName.length() < 1 && type.length() < 1) {
								listSearch.add(value);
							} else{
								listSearch.add(value);
							}	
						}
					}
				}
			}
		}
		if(keywordType.equals("search")){
			sortList(listSearch);
			return listSearch;
		}else if(keywordType.equals("gemet")){
			sortList(listGemet);
			return listGemet;
		}else if(keywordType.equals("inspire")){
			sortList(listInspire);
			return listInspire;
		}else{
			sortList(listSearch);
			return listSearch;							
		}
	}
	
	public HashMap getAvailability(String xpathExpression) {
		HashMap element = new HashMap();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				xpathExpression = "./gmd:distributionFormat/gmd:MD_Format";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList nodeList = XPathUtils.getNodeList(node, xpathExpression);
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
					
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node childNode = nodeList.item(i);
						ArrayList row = new ArrayList();
						
						String name = "";
						String version = "";
						
						xpathExpression = "./gmd:name";
						if (XPathUtils.nodeExists(childNode, xpathExpression)) {
							name = XPathUtils.getString(childNode, xpathExpression).trim();
						} 						
						
						xpathExpression = "./gmd:version";
						if (XPathUtils.nodeExists(childNode, xpathExpression)) {
							version = XPathUtils.getString(childNode, xpathExpression).trim();
						}
						
						if(!name.equals("Geographic Markup Language (GML)") && !version.equals("unknown")){
							xpathExpression = "./gmd:name";
							if (XPathUtils.nodeExists(childNode, xpathExpression)) {
								row.add(notNull(name));
							} else {
								row.add("");
							}
							
							xpathExpression = "./gmd:version";
							if (XPathUtils.nodeExists(childNode, xpathExpression)) {
								row.add(notNull(version));
							} else {
								row.add("");
							}
							
							xpathExpression = "./gmd:fileDecompressionTechnique";
							if (XPathUtils.nodeExists(childNode, xpathExpression)) {
								String value = XPathUtils.getString(childNode, xpathExpression).trim();
								row.add(notNull(value));
							} else {
								row.add("");
							}
							
							xpathExpression = "./gmd:specification";
							if (XPathUtils.nodeExists(childNode, xpathExpression)) {
								String value = XPathUtils.getString(childNode, xpathExpression).trim();
								row.add(notNull(value));
							} else {
								row.add("");
							}
						}
						
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}
				}
			}
		}
		return element;
	}

	public HashMap getMediumOptions(String xpathExpression) {
		HashMap element = new HashMap();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			xpathExpression = "./gmd:transferOptions";
			if (XPathUtils.nodeExists(node, xpathExpression)) {
				NodeList nodeList = XPathUtils.getNodeList(node, xpathExpression);
				element.put("type", "table");
				element.put("title", messages.getString("t0112_media_option.medium"));
				ArrayList head = new ArrayList();
				head.add(messages.getString("t0112_media_option.medium_name"));
				head.add(messages.getString("t0112_media_option.transfer_size") + " [MB]");
				head.add(messages.getString("t0112_media_option.medium_note"));
				element.put("head", head);
				ArrayList body = new ArrayList();
				element.put("body", body);
				
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node nodeListItem = nodeList.item(i);
					ArrayList row = new ArrayList();
					if(XPathUtils.nodeExists(nodeListItem, "./gmd:MD_DigitalTransferOptions/gmd:offLine")){
						
						xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:name/gmd:MD_MediumNameCode/@codeListValue";
						if (XPathUtils.nodeExists(nodeListItem, xpathExpression)) {
							String value = XPathUtils.getString(nodeListItem, xpathExpression).trim();
							row.add(notNull(sysCodeList.getNameByCodeListValue("520", value)));
						} else {
							row.add("");
						}
						
						xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:transferSize";
						if (XPathUtils.nodeExists(nodeListItem, xpathExpression)) {
							row.add(notNull(XPathUtils.getString(nodeListItem, xpathExpression)).trim());
						} else {
							row.add("");
						}
						
						xpathExpression = "./gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium/gmd:mediumNote";
						if (XPathUtils.nodeExists(nodeListItem, xpathExpression)) {
							row.add(notNull(XPathUtils.getString(nodeListItem, xpathExpression)).trim());
						} else {
							row.add("");
						}
						
						if (!isEmptyRow(row)) {
							body.add(row);
						}
					}
				}
			}
		}
		return element;
	}
	
	public HashMap getConformityData(String xpathExpression) {
		HashMap element = new HashMap();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			element.put("type", "table");
			element.put("title", messages.getString("object_conformity"));
			
			ArrayList head = new ArrayList();
			head.add(messages.getString("object_conformity.specification"));
			head.add(messages.getString("object_conformity.degree_value"));
			head.add(messages.getString("object_conformity.publication_date"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if(XPathUtils.nodeExists(node, "./gmd:DQ_DomainConsistency")){
					ArrayList row = new ArrayList();
					
					xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:title";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						row.add(notNull(value));
					} else {
						row.add("");
					}
					
					xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:pass";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						if(XPathUtils.nodeExists(node, xpathExpression + "/@gco:nilReason")){
							row.add(notNull(sysCodeList.getName("6000", "3")));	
						}else{
							if(value.equals("true")){
								row.add(notNull(sysCodeList.getName("6000", "1")));	
							}else if(value.equals("false")){
								row.add(notNull(sysCodeList.getName("6000", "2")));	
							}else{
								row.add("");
							}
						}
					} else {
						row.add("");	
					}
					
					xpathExpression = "./gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult/gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						if(XPathUtils.nodeExists(node, xpathExpression + "/@gco:nilReason")){
							row.add("");
						}else {
							String value = XPathUtils.getString(node, xpathExpression).trim();
							row.add(notNull(UtilsDate.convertDateString(value, "yyyy-MM-dd", "dd.MM.yyyy")));
						}
					} else {
						row.add("");
					}
					
					if (!isEmptyRow(row)) {
						body.add(row);
					}
				}
			}
		}
		return element;
	}
	
	
	public ArrayList getServiceClassification(String xpathExpression) {
		ArrayList list = new ArrayList();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String thesaurusName = "";
				
				// thesaurus
				xpathExpression = "./gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					thesaurusName = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				// keywords
				xpathExpression = "./gmd:MD_Keywords/gmd:keyword";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList keywordNodeList = XPathUtils.getNodeList(node, xpathExpression);
					for (int j = 0; j < keywordNodeList.getLength(); j++) {
						Node keywordNode = keywordNodeList.item(j);
						String value = XPathUtils.getString(keywordNode, ".").trim(); 
						if(value.length() < 1){
							value = XPathUtils.getString(keywordNode, ".").trim();
						}
						
						// "Service Classification, version 1.0"
						if (thesaurusName.indexOf("Service") > -1) {
							String tmpValue = sysCodeList.getNameByCodeListValue("5200", value);
							if(tmpValue.length() > 0){
								value = tmpValue;
							}
							list.add(value);
						} 
					}
				}
			}
		}
		sortList(list);
		return list;
	}
	
	public HashMap<String, Object> getReferenceObject(String xpathExpression) {
		HashMap element = new HashMap();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList childNodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList<String> listDominator = new ArrayList<String>();
			ArrayList<String> listMeter = new ArrayList<String>();
			ArrayList<String> listDpi = new ArrayList<String>();
			for (int j = 0; j < childNodeList.getLength(); j++) {
				xpathExpression = "./gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator";
				if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
					listDominator.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
				}
				
				xpathExpression = "./gmd:distance/gco:Distance[@uom='meter']";
				if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
					listMeter.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
				}
				
				xpathExpression = "./gmd:distance/gco:Distance[@uom='dpi']";
				if (XPathUtils.nodeExists(childNodeList.item(j), xpathExpression)) {
					listDpi.add(XPathUtils.getString(childNodeList.item(j), xpathExpression).trim());
				}
			}
			
			element.put("type", "table");
			element.put("title", messages.getString("t011_obj_serv_scale"));
			
			ArrayList head = new ArrayList();
			head.add(messages.getString("t011_obj_serv_scale.scale").concat(" 1:x"));
			head.add(messages.getString("t011_obj_serv_scale.resolution_ground").concat(" m"));
			head.add(messages.getString("t011_obj_serv_scale.resolution_scan").concat(" dpi"));
			element.put("head", head);
			ArrayList body = new ArrayList();
			element.put("body", body);
			
			for (int i = 0; i < listDominator.size(); i++) {
				ArrayList row = new ArrayList();
				int value;
				
				value = listDominator.size();
				if (value != 0) {
					if(value > i){
						row.add(notNull((String) listDominator.get(i)));
					}else{
						row.add("");
					}
				} else {
					row.add("");
				}
				
				value = listMeter.size();
				if (value != 0) {
					if(value > i){
						row.add(notNull((String) listMeter.get(i)));	
					}else {
						row.add("");
					}
				} else {
					row.add("");
				}
				
				value = listDpi.size();
				if (value != 0) {
					if(value > i){
						row.add(notNull((String) listDpi.get(i)));									
					}else{
						row.add("");
					}
				} else {
					row.add("");
				}
				
				if (!isEmptyRow(row)) {
					body.add(row);
				}
			}
		}
		return element;
	}
	
	public HashMap getConnectionPoints(String xpathExpression) {
		String serviceType = getValueFromXPath("./gmd:identificationInfo/*/srv:serviceType");
		if(serviceType != null){
			serviceType = serviceType.trim();
		}
		HashMap element = new HashMap();
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			
	    	for (int i=0; i<nodeList.getLength();i++){
				if(XPathUtils.nodeExists(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL")){
					Node node = XPathUtils.getNode(nodeList.item(i), "./gmd:CI_OnlineResource/gmd:linkage/gmd:URL");				
					String urlValue = XPathUtils.getString(node, ".").trim();
					// do not display empty URLs
					if (urlValue == null || urlValue.length() == 0) {
					    continue;
					}

					// do not display empty operations
					String operationName = XPathUtils.getString(nodeList.item(i), "./../srv:operationName").trim();
					if (operationName == null || operationName.length() == 0) {
					    continue;
					}

					if (operationName.equals("GetCapabilities")) {
						if (urlValue.toLowerCase().indexOf("request=getcapabilities") == -1) {
			    			if (urlValue.indexOf("?") == -1) {
			    				urlValue = urlValue + "?";
			    			}
			    			if (!urlValue.endsWith("?") && !urlValue.endsWith("&")) {
			    				urlValue = urlValue + "&";
			    			}
			    			urlValue = urlValue + "REQUEST=GetCapabilities";
			    			if(serviceType != null){
			    				urlValue = Utils.getServiceTypeParameter(urlValue, serviceType);
			    			}
			    		}

						element.put("type", "textLabelLeft");
    					element.put("line", true);

    					HashMap elementCapabilitiesLink = new HashMap();
    					elementCapabilitiesLink.put("type", "linkLine");
    					elementCapabilitiesLink.put("hasLinkIcon", new Boolean(true));
    					elementCapabilitiesLink.put("isExtern", new Boolean(true));
    					elementCapabilitiesLink.put("title", urlValue);
    					elementCapabilitiesLink.put("href", urlValue);
  						element.put("body", elementCapabilitiesLink);


  						boolean hasAccessConstraints = false;
  						xpathExpression = "./idf:hasAccessConstraint";
  						if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
  							String hasAccessConstraintsValue = XPathUtils.getString(rootNode, xpathExpression).trim();
  							if(hasAccessConstraintsValue.length() > 0){
  								hasAccessConstraints = Boolean.parseBoolean(hasAccessConstraintsValue);	
  							}
  						}

    					if (!hasAccessConstraints) {
    						element.put("title", messages.getString("common.result.showGetCapabilityUrl"));
    						if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, false) && (serviceType != null && serviceType.equals("view"))){
    	  			        	HashMap elementMapLink = new HashMap();
	    						elementMapLink.put("type", "linkLine");
	    						elementMapLink.put("hasLinkIcon", new Boolean(true));
	    						elementMapLink.put("isExtern", new Boolean(false));
	    						elementMapLink.put("title", messages.getString("common.result.showMap"));
    							elementMapLink.put("href", "portal/main-maps.psml?wms_url=" + UtilsVelocity.urlencode(urlValue));
    	  						element.put("link", elementMapLink);
    	    					element.put("linkLeft", true);
    						}
			        	} else {
	    					// do not display "show in map" link if the map has access constraints
			        		element.put("title", messages.getString("common.result.showGetCapabilityUrlRestricted"));
			        	}
    					// ADD FIRST ONE FOUND !!!
    					break;
					}
				}
	    	}
		}
		return element;
	}
	
	public HashMap<String, Object> getAreaGeothesaurus(String xpathExpression){
		HashMap element = new HashMap();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList subjectEntries = new ArrayList();
            for (int i = 0; i < nodeList.getLength(); i++) {
                xpathExpression = "./gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code";
                Node node = nodeList.item(i);
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList childNodeList = XPathUtils.getNodeList(node, xpathExpression);
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        String domainValue = XPathUtils.getString(childNodeList.item(j), ".").trim();
                        subjectEntries.add(domainValue);
                    }
                }
                
                // "Geothesaurus-Raumbezug"
                xpathExpression = "./gmd:geographicElement/gmd:EX_GeographicBoundingBox";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    NodeList subNodeList = XPathUtils.getNodeList(node, xpathExpression);
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
                    
                    ArrayList numbers = new ArrayList();
                    numbers.add(subjectEntries.size());
                    numbers.add(subNodeList.getLength());
                    int maxRows = getGreatestInt(numbers); 
                    
                    for (int j = 0; j < maxRows; j++) {
                        Node childNode = subNodeList.item(j);
                        ArrayList row = new ArrayList();
                        
                        if(subjectEntries.size() > 0){
                            if (subjectEntries.get(j)!= null) {
                                row.add(subjectEntries.get(j));
                            }else {
                                row.add("");
                            }
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:westBoundLongitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:southBoundLatitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:eastBoundLongitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        xpathExpression = "./gmd:northBoundLatitude";
                        if (XPathUtils.nodeExists(childNode, xpathExpression)) {
                            String value = XPathUtils.getString(childNode, xpathExpression).trim();
                            row.add(notNull(value + "\u00B0"));
                        } else {
                            row.add("");
                        }
                        
                        if (!isEmptyRow(row)) {
                            body.add(row);
                        }
                    }
                }
            }
        }
        return element;
	}
	
	public HashMap<String, Object> getAreaHeight(String xpathExpression){
		HashMap element = new HashMap();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            ArrayList subjectEntries = new ArrayList();
            for (int i = 0; i < nodeList.getLength(); i++) {
            	Node node = nodeList.item(i);
                xpathExpression = "./gmd:verticalElement";
		        if (XPathUtils.nodeExists(node, xpathExpression)) {
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
		            if (XPathUtils.nodeExists(node, "./gmd:verticalElement/gmd:EX_VerticalExtent")) {
		                NodeList subNodeList = XPathUtils.getNodeList(node, "./gmd:verticalElement/gmd:EX_VerticalExtent");
		                for (int j = 0; j < subNodeList.getLength(); j++) {
		                    Node childNode = subNodeList.item(j);
		                    ArrayList row = new ArrayList();
		                    
		                    // "Maximum"
		                    xpathExpression = "./gmd:maximumValue";
		                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
		                        row.add(notNull(XPathUtils.getString(childNode, xpathExpression)).trim());
		                    }
		                    
		                    // "Minimum"
		                    xpathExpression = "./gmd:minimumValue";
		                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
		                        row.add(notNull(XPathUtils.getString(childNode, xpathExpression)).trim());
		                    }
		                    
		                    String rowValue;
		                    // "Maßeinheit"
		                    xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@gml:uom";
		                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
		                        rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
		                        row.add(notNull(sysCodeList.getNameByCodeListValue("102", rowValue)));
		                    }else{
		                        row.add("");
		                    }
		                    
		                    // "Vertikaldatum"
		                    String verticalDatum = "" ; 
		                    xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:name";
		                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
		                        rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
		                        if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
		                            verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
		                        }else{
		                            verticalDatum = rowValue;
		                        }
		                    }
		                    
		                    if(verticalDatum.length() < 1)
		                        xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:identifier";
		                        if(XPathUtils.nodeExists(childNode, xpathExpression)){
		                            rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
		                            if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
		                                verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
		                            }else{
		                                verticalDatum = rowValue;
		                            }
		                        }
		                    if(verticalDatum.length() < 1){
		                        xpathExpression = "./gmd:verticalCRS/gml:VerticalCRS/gml:name";
		                        if(XPathUtils.nodeExists(childNode, xpathExpression)){
		                            rowValue = XPathUtils.getString(childNode, xpathExpression).trim();
		                            if(sysCodeList.getNameByCodeListValue("101", rowValue).length() > 0){
		                                verticalDatum = sysCodeList.getNameByCodeListValue("101", rowValue);
		                            }else{
		                                verticalDatum = rowValue;
		                            }
		                        }
		                    }
		                    
		                    row.add(verticalDatum);
		                    if (!isEmptyRow(row)) {
		                        body.add(row);
		                    }
		                }
		            }
		        }
            }
        }
        return element;
	}
	
	public ArrayList getNodeListValueReferenceSystem(String xpathExpression) {
		ArrayList<String> list = new ArrayList<String>();
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength(); i++){
                String codeSpace = ""; 
                String code = "";
                xpathExpression = "./gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code";
                if (XPathUtils.nodeExists(nodeList.item(i), xpathExpression)) {
                    code = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                }
                
                xpathExpression = "./gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:codeSpace";
                if (XPathUtils.nodeExists(nodeList.item(i), xpathExpression)) {
                    codeSpace = XPathUtils.getString(nodeList.item(i), xpathExpression).trim();
                }
                
                String value = "";
                if(code.length() > 0 && codeSpace.length() > 0){
                    if(code.indexOf("EPSG") > -1){
                        value = code;
                    }else{
                        value = codeSpace.concat(": " + code);
                    }
                }else if(codeSpace.length() > 0){
                    value = codeSpace;
                }else if(code.length() > 0){
                    value = code;
                }
                
                if(value.length() > 0){
                	list.add(value);
                }
            }
        }
        return list;
    }
	
	public String getGeoReport(String xpathExpression, String checkDescription, String checkNameOfMeasure) {
		String value = null;
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i=0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				String symbol = null;
				String nameOfMeasure = null;
				String description = null;
				String content = "";
				String title = "";
				
				if(node != null){
					xpathExpression = "./gmd:nameOfMeasure";
					if(XPathUtils.nodeExists(node, xpathExpression)){
						nameOfMeasure = XPathUtils.getString(node, xpathExpression).trim();
					}
					xpathExpression = "./gmd:measureDescription";
					if(XPathUtils.nodeExists(node, xpathExpression)){
						description = XPathUtils.getString(node, xpathExpression).trim();
					}
					xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:value";
					if(XPathUtils.nodeExists(node, xpathExpression)){
						content = XPathUtils.getString(node, xpathExpression).trim();
					}
					xpathExpression = "./gmd:result/gmd:DQ_QuantitativeResult/gmd:valueUnit/gml:UnitDefinition/gml:catalogSymbol";
					if(XPathUtils.nodeExists(node, xpathExpression)){
						symbol = XPathUtils.getString(node, xpathExpression).trim();
					}
					
					if(symbol != null){
						if(description != null || nameOfMeasure != null){
							if (checkDescription.equals(description) || checkNameOfMeasure.equalsIgnoreCase(nameOfMeasure)){
								value = content + " " + symbol;
								break;
							}
						}
					}else{
						if(description != null || nameOfMeasure != null){
							if (checkDescription.equals(description) || checkNameOfMeasure.equalsIgnoreCase(nameOfMeasure)){
								value = content;
								break;
							}
						}
					}
				}
			}
		}
		return value;
	}
	
	public String addLinkToGetXML() {
		String htmlLink = null;
	    String cswUrl = (String) PortalConfig.getInstance().getString(PortalConfig.CSW_INTERFACE_URL, "");
        if (!cswUrl.isEmpty()) {
            htmlLink = "<a href="+cswUrl+"?REQUEST=GetRecordById&SERVICE=CSW&VERSION=2.0.2&id="+this.uuid+"&iplug="+this.iPlugId+"&elementSetName=full&elementSetName=full' target='_blank'>"+messages.getString("xml_link")+"</a>";
        }
        return htmlLink;
    }
	
	public String getPublishId(String value) {
		String publishId = "1";
		if (value.length() > 0) {
			if (value.equals("unclassified")) {
				publishId = "1";
			} else if (value.equals("restricted")) {
				publishId = "2";
			} else if (value.equals("confidential")) {
				publishId = "3";
			}
		}
		return messages.getString("t01_object.publish_id_" + publishId);
	}

	public HashMap getAddresses(String xpathExpression, boolean isForAdditionalInfo) {
		HashMap elementAddress = new HashMap();
		
        ArrayList elementsAddress = new ArrayList();
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);
                if (childNode.hasChildNodes()) {
                    xpathExpression = "./idf:idfResponsibleParty";
                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
                        Node subNode = XPathUtils.getNode(childNode, xpathExpression);
                        addSingleAddress(elementsAddress, subNode, isForAdditionalInfo);    
                    }
                    xpathExpression = "./gmd:CI_ResponsibleParty";
                    if(XPathUtils.nodeExists(childNode, xpathExpression)){
                        Node subNode = XPathUtils.getNode(childNode, xpathExpression);
                        addSingleAddress(elementsAddress, subNode, isForAdditionalInfo);    
                    }
                }
            }
        }
        
        // "Kontakte für idfResponsibleParty"
        xpathExpression = "./gmd:contact/idf:idfResponsibleParty";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++){
                addSingleAddress(elementsAddress, nodeList.item(i), isForAdditionalInfo);
            }
            
        }
        
        // "Kontakte für CI_ResponsibleParty"
        xpathExpression = "./gmd:contact/gmd:CI_ResponsibleParty";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++){
                addSingleAddress(elementsAddress, nodeList.item(i), isForAdditionalInfo);
            }
            
        }
        
        xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/idf:idfResponsibleParty";
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i = 0; i < nodeList.getLength(); i++){
                addSingleAddress(elementsAddress, nodeList.item(i), isForAdditionalInfo);
            }
        }
        
        if(isForAdditionalInfo){
            if(elementsAddress != null && elementsAddress.size() > 0){
            	HashMap a = (HashMap) elementsAddress.get(0);
            	if(a != null){
        			ArrayList ae = (ArrayList) a.get("elementsAddress");
        			if(ae != null && ae.size() > 0){
        				return a;
        			}
        		}
            }
        }else{
        	if(elementsAddress != null){
	        	switch (elementsAddress.size()) {
	        	case 1:
	        		HashMap a = (HashMap) elementsAddress.get(0);
	        		if(a != null){
	        			ArrayList ae = (ArrayList) a.get("elements");
	        			if(ae != null && ae.size() > 0){
	        				elementAddress.put("type", "multiLineAddresses");
	                        elementAddress.put("title", messages.getString("addresses"));
	                        elementAddress.put("id", "addresses_id");
	                        elementAddress.put("elementsAddress", elementsAddress);
	                        return elementAddress;
	        			}
	        		}
					break;
	
				default:
					if(elementsAddress.size() > 0){
						elementAddress.put("type", "multiLineAddresses");
		                elementAddress.put("title", messages.getString("addresses"));
		                elementAddress.put("id", "addresses_id");
		                elementAddress.put("elementsAddress", elementsAddress);
		                return elementAddress;
					}
					break;
	        	}
        	}
        }
        return elementAddress;
    }
	
	/*
	 * Private functiions
	 *
	 */
	private void addSingleAddress(ArrayList elementsAddress, Node node, boolean isForAdditionalInfo) {
        if (node != null) {
            HashMap element;
            String xpathExpression;
            boolean isIdfResponsibleParty = false;
            
            if(node.getLocalName().trim().equals("idfResponsibleParty")){
                isIdfResponsibleParty = true;
            }else{
                isIdfResponsibleParty = false;
            }
            
            xpathExpression = "./gmd:role/gmd:CI_RoleCode/@codeListValue";
            if (XPathUtils.nodeExists(node, xpathExpression)) {
                String role = XPathUtils.getString(node, xpathExpression).trim();
                String title = sysCodeList.getNameByCodeListValue("505", role);
                boolean isMetadatumContact = false;
                if(role.equals("pointOfContact")){
                    if(node.getParentNode().getNodeName().trim().equals("gmd:contact")){
                        isMetadatumContact = true;
                        title = title + " (" + messages.getString("pointofcontact_metadata")+ ")";
                    }
                }
                
                if (title.length() < 1) {
                    String newTitle = role;

                    if(role.equals("projectManager")){
                        newTitle = messages.getString("t011_obj_project.leader");
                    } else if (role.equals("projectParticipant")) {
                        newTitle = messages.getString("t011_obj_project.member");
                    }
                    title = newTitle;
                }
                
                
                if((isForAdditionalInfo && isMetadatumContact)){
                	// render only email of MetadatumContact, see INGRID32-146
                    addSingleMetaAddress(elementsAddress, node, title, isIdfResponsibleParty, true);
                }else if (!isForAdditionalInfo && !isMetadatumContact){
                    element = addElementAddress("multiLine", title, "", "false", new ArrayList());
                    ArrayList elements = (ArrayList) element.get("elements");
                    
                    addressEvaluateNode(elements, node, isIdfResponsibleParty, false);
                    
                    elementsAddress.add(element);
                }
            }
        }
    }
	
	
	 private void addSingleMetaAddress(ArrayList elementsAddress, Node node, String title, Boolean isIdfResponsibleParty, boolean renderOnlyEmail) {
	        
	        ArrayList elementsMetaAddress = new ArrayList();
	        String xpathExpression = "";
	        ArrayList elements = new ArrayList();
	        
	        addressEvaluateNode(elements,node, isIdfResponsibleParty, renderOnlyEmail);
	        if(elements.size() > 0){
	        	HashMap element = addElementAddress("multiLine", null, "", "false", elements);
	            elementsMetaAddress.add(element);
	        }
	        
	        HashMap elementAddress = new HashMap();
	        elementAddress.put("type", "multiLineAddresses");
	        elementAddress.put("title", title);
	        elementAddress.put("id", "addresses_id");
	        elementAddress.put("elementsAddress", elementsMetaAddress);
	        elementsAddress.add(elementAddress);
	        
	    }

    private void addressEvaluateNode(ArrayList elements, Node node, boolean isIdfResponsibleParty, boolean renderOnlyEmail) {

        String xpathExpression = "";
        
        // first extract email addresses to be used here ore later !
        // "Mail"
        ArrayList<HashMap> elementsEmail = new ArrayList<HashMap>();
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            NodeList electronicMailAddressNodeList = XPathUtils.getNodeList(node, xpathExpression);
            for (int i = 0; i < electronicMailAddressNodeList.getLength(); i++) {
                String value = XPathUtils.getString(electronicMailAddressNodeList.item(i), ".").trim();
                if(value != null && value.length() > 0){
                	elementsEmail.add(addElementEmailWeb(sysCodeList.getName("4430", "3"), value, value, value, LinkType.EMAIL));
                }
            }
        }
        if (renderOnlyEmail) {
            for (HashMap elementEmail : elementsEmail) {
            	elements.add(elementEmail);
            }
            return;
        }

        boolean dataAdded = false;

        if(isIdfResponsibleParty){
            if(XPathUtils.nodeExists(node, "./idf:hierarchyParty")){
                NodeList hierarchyPartyNodeList = XPathUtils.getNodeList(node, "./idf:hierarchyParty");
                for(int i=hierarchyPartyNodeList.getLength(); 0 < i ;i--){
                    Node hierarchyPartyNode = hierarchyPartyNodeList.item(i-1);
                    String uuid = "";
                    String value = "";
                    String type = "";
                    xpathExpression = "./@uuid";
                    if (XPathUtils.nodeExists(node, xpathExpression)) {
                        uuid = XPathUtils.getString(hierarchyPartyNode, xpathExpression).trim();
                    }
                    // "Type"
                    xpathExpression = "./idf:addressType";
                    if(XPathUtils.nodeExists(hierarchyPartyNode, xpathExpression)){
                        type = XPathUtils.getString(hierarchyPartyNode, xpathExpression).trim();
                    }
                    
                    if(type.equals("2")){
                    	// Person -> "Name"
                        xpathExpression = "./idf:addressIndividualName";
                        if(XPathUtils.nodeExists(hierarchyPartyNode, xpathExpression)){
                            value = XPathUtils.getString(hierarchyPartyNode, xpathExpression).trim();
                            elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), getIndividualName(value), uuid));
                        }
                    }else if(type.equals("3")){
                    	// Free Address -> "Name" or "Organisation"
                        if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressIndividualName")){
                            value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressIndividualName").trim();
                            elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), getIndividualName(value), uuid));
                        }else if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressOrganisationName")){
                            value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressOrganisationName").trim();
                            elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), value, uuid));
                        }
                    }else{
                        // "Organisation"
                        xpathExpression = "./idf:addressOrganisationName";
                        if(XPathUtils.nodeExists(hierarchyPartyNode, xpathExpression)){
                            value = XPathUtils.getString(hierarchyPartyNode, xpathExpression).trim();
                            elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), value, uuid));
                        }
                    }
                    dataAdded = true;
                }
            }else{
                // Organistation
                xpathExpression = "./gmd:organisationName";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    String value = XPathUtils.getString(node, xpathExpression).trim();
                    StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
                    for(int i=0; i<valueTokenizer.countTokens();i++) {
                        addElement(elements, "textLine", valueTokenizer.nextToken());
                    }
                    dataAdded = true;
                }
                
                // Name
                xpathExpression = "./gmd:individualName";
                if (XPathUtils.nodeExists(node, xpathExpression)) {
                    String value = XPathUtils.getString(node, xpathExpression).trim();
                    addElement(elements, "textLine", getIndividualName(value));
                    dataAdded = true;
                }
            }
        }else{
            // Organistation
            xpathExpression = "./gmd:organisationName";
            if (XPathUtils.nodeExists(node, xpathExpression)) {
                String value = XPathUtils.getString(node, xpathExpression).trim();
                StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
                for(int i=0; i<valueTokenizer.countTokens();i++) {
                    addElement(elements, "textLine", valueTokenizer.nextToken());
                }
                dataAdded = true;
            }
            
            // Name
            xpathExpression = "./gmd:individualName";
            if (XPathUtils.nodeExists(node, xpathExpression)) {
                String value = XPathUtils.getString(node, xpathExpression).trim();
                addElement(elements, "textLine", getIndividualName(value));
                dataAdded = true;
            }
        }
        
        // "Position"
        /* NOT IN USED
        xpathExpression = "./gmd:positionName";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String position = XPathUtils.getString(node, xpathExpression).trim();
            if (position != null) {
                position = position.replaceAll("\n", "<br/>");
                position = position.replaceAll("&lt;", "<");
                position = position.replaceAll("&gt;", ">");
            }
            addElement(elements, "textLine", position);
        }
        */
        if (XPathUtils.nodeExists(node, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address")){
        	if (dataAdded) {
                addSpace(elements);        		
            	dataAdded = false;
        	}
        }
        
        // "Street"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(node, xpathExpression);
            for(int i=0; i<nodeList.getLength();i++){
                String deliveryPoint = XPathUtils.getString(nodeList.item(i), ".").trim();
                if(deliveryPoint.startsWith("Postbox")){
                    String[] postbox = deliveryPoint.split(",");
                    for(int j=0; j < postbox.length; j++){
                        if(postbox[j].startsWith("Postbox")){
                            addElement(elements, "textLine", messages.getString("postbox_label") + " " + postbox[j].replace("Postbox ", ""));
                        }else{
                            addElement(elements, "textLine", postbox[j]);
                        }
                    }
                    addSpace(elements);
                }else if(deliveryPoint.matches("\\d*")){
                    addElement(elements, "textLine", messages.getString("postbox_label") + " " + deliveryPoint);
                }else{
                    addElement(elements, "textLine", deliveryPoint);
                }
            }
            dataAdded = true;
        }
        
        String postalCode = "";
        String city = "";
        
        // "Postcode"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            postalCode = XPathUtils.getString(node, xpathExpression).trim();
        }
        
        // "City"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            city = XPathUtils.getString(node, xpathExpression).trim();
        }
        
        if (postalCode.length() > 0 || city.length() > 0) {
            addElement(elements, "textLine", postalCode.concat(" ").concat(city));
            dataAdded = true;
        }
        
        // "Country"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String country = XPathUtils.getString(node, xpathExpression).trim();
            if(country != null){
                String value = UtilsCountryCodelist.getNameFromCode(UtilsCountryCodelist.getCodeFromShortcut3(country), this.request.getLocale().getLanguage().toString());
                if(value != null){
                    addElement(elements, "textLine", value);
                }else{
                    addElement(elements, "textLine", country);
                }
            }
            dataAdded = true;
        }

        if (dataAdded) {
            addSpace(elements);
        }
        // "Mail"
        // already extracted above !
        for (HashMap elementEmail : elementsEmail) {
        	elements.add(elementEmail);
        }
        
        // "Telefon"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String value = XPathUtils.getString(node, xpathExpression).trim();
            if(value != null && value.length() > 0){
                addElement(elements, "textLine", value, sysCodeList.getName("4430", "1"));
            }
        }
        
        // "Fax"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String value = XPathUtils.getString(node, xpathExpression).trim();
            if(value != null && value.length() > 0){
                addElement(elements, "textLine", value, sysCodeList.getName("4430", "2"));
            }
        }
        
        // "URL"
        xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
        if (XPathUtils.nodeExists(node, xpathExpression)) {
            String value = XPathUtils.getString(node, xpathExpression).trim();
            if(value != null && value.length() > 0){
                elements.add(addElementEmailWeb(sysCodeList.getName("4430", "4"), value, value, value, LinkType.WWW_URL));
            }
        }
        
    }
    
    private HashMap<String,Object> getCapabilityUrls(String xpathExpression) {
	    boolean mapLinkAdded = false;
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            
            for (int i=0; i<nodeList.getLength();i++){
                if(XPathUtils.nodeExists(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/idf:idfOnlineResource/gmd:linkage/gmd:URL")){
                    Node node = XPathUtils.getNode(nodeList.item(i), "./gmd:MD_DigitalTransferOptions/gmd:onLine/idf:idfOnlineResource/gmd:linkage/gmd:URL");                
                    String urlValue = XPathUtils.getString(node, ".").trim();
                    // do not display empty URLs
                    if (urlValue == null || urlValue.length() == 0) {
                        continue;
                    }
                    if (urlValue.toLowerCase().indexOf("request=getcapabilities") != -1) {
                        // also add an identifier to select the correct layer in the map client 
                    	map = addBigMapLink(urlValue + "&ID=" + getLayerIdentifier(null));
                        // ADD FIRST ONE FOUND !!!
                        mapLinkAdded = true;
                        break;
                    }
                }
            }
            if (!mapLinkAdded) {
                // check if preview image is available
                xpathExpression = "./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/gco:CharacterString";
            	map = getPreviewImage(xpathExpression);
            }
	    }
	    return map;
	}
	
	private HashMap<String,Object> getPreviewImage(String xpathExpression) {
	    String url = getPreviewImageUrl(xpathExpression);
	    HashMap<String,Object> elementCapabilities = new HashMap<String,Object>();
        if (url != null) {
	    	elementCapabilities.put("type", "multiLine");
            HashMap<String,Object> elementMapLink = new HashMap<String,Object>();
            elementMapLink.put("type", "linkLine");
            elementMapLink.put("isMapLink", new Boolean(true));
            elementMapLink.put("isExtern", new Boolean(false));
            elementMapLink.put("title", messages.getString("preview"));
            elementMapLink.put("href", url);
            elementMapLink.put("src", url);
            // put link in a list so that it is aligned correctly in detail view (<div class="width_two_thirds">)
            ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
            list.add(elementMapLink);
            elementCapabilities.put("elements", list);
            elementCapabilities.put("width", "full");
	    }
	    return elementCapabilities;
	}
	
	private String getLayerIdentifier(Node crossReference) {
	    if (getUdkObjectClassType().equals("1")) {
	        String href = XPathUtils.getString(rootNode, "./gmd:identificationInfo/gmd:MD_DataIdentification/@uuid");
	        if (href != null) {
	            return href;
	        }
	    } else {
    	    String origId = XPathUtils.getString(crossReference, "./@orig-uuid");
    	    String uuid   = XPathUtils.getString(crossReference, "./@uuid");
    	    String xpathExpression = "./gmd:identificationInfo/*/srv:operatesOn";
    	    
    	    NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
    	    
                String uuidRef = XPathUtils.getString(node, "./@uuidref");
                String href = XPathUtils.getString(node, "./@xlink:href");
                
                if (uuidRef != null && (uuidRef.equals(uuid) || uuidRef.equals(origId))) {
                    return href;
                }
            }
	    }
        
	    return "NOT_FOUND";
	}
	
	private HashMap<String, Object> addBigMapLink(String urlValue) {
		HashMap<String, Object> elementCapabilities = new HashMap<String, Object>();
	    if (urlValue != null) {
            elementCapabilities.put("type", "multiLine");
            HashMap<String, Object> elementMapLink = new HashMap<String, Object>();
            elementMapLink.put("type", "linkLine");
            elementMapLink.put("isMapLink", new Boolean(true));
            elementMapLink.put("isExtern", new Boolean(false));
            elementMapLink.put("title", messages.getString("common.result.showMap.tooltip.short"));
            elementMapLink.put("href", UtilsVelocity.urlencode(urlValue));
            // use preview image if provided otherwise static image
            String imageUrl = getPreviewImageUrl(null);
            if (imageUrl == null ) imageUrl = "/ingrid-portal-apps/images/show_map.png";
            elementMapLink.put("src", imageUrl);
            // put link in a list so that it is aligned correctly in detail view (<div class="width_two_thirds">)
            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            list.add(elementMapLink);
            elementCapabilities.put("elements", list);
            elementCapabilities.put("width", "full");
	    }
	    return elementCapabilities;
	}
	
	private String getPreviewImageUrl(String xpathExpression) {
	    if (xpathExpression == null)
	        xpathExpression = "./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic/gmd:fileName/gco:CharacterString";
	    
	    String value = null;
        if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if(node.getTextContent().length() > 0){
            	value = node.getTextContent();
            }
        }
        return value;
    }
	
	private String getCapabilityUrl() {
	    String url = null;
        HashMap link = (HashMap) getConnectionPoints("./gmd:identificationInfo/*/srv:containsOperations/srv:SV_OperationMetadata/srv:connectPoint").get("link");
        if (link != null) {
            url = (String) link.get("href");
            url = url.substring(url.indexOf("http"));
        }
        return url;
    }
}
