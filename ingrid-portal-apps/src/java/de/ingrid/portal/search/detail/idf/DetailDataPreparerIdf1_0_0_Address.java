package de.ingrid.portal.search.detail.idf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.utils.udk.UtilsCountryCodelist;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.xml.XPathUtils;

//TODO: remove annotation
@SuppressWarnings("unchecked")
public class DetailDataPreparerIdf1_0_0_Address extends DetailDataPreparerIdf1_0_0 {
	
	private final static Log	log	= LogFactory.getLog(DetailDataPreparerIdf1_0_0_Address.class);
	
	public DetailDataPreparerIdf1_0_0_Address(Node node, Context context, RenderRequest request, String iPlugId, RenderResponse response) {
		super(node, context, request, iPlugId, response);
		this.rootNode = node;
		this.context = context;
		this.request = request;
		this.iPlugId = iPlugId;
		this.response = response;
		messages = (IngridResourceBundle) context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	public void prepare(ArrayList data) {
		
		if (rootNode != null) {
			if(log.isDebugEnabled()){
				log.debug("Start parsing of: '"+ rootNode.getLocalName() +"'");
			}
			initialArrayLists();
			
			HashMap general = new HashMap();
			ArrayList udkElements = new ArrayList();
			
			String xpathExpression;
			
			// Type & Title
			xpathExpression = "./idf:hierarchyParty";
			if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
				Node node = XPathUtils.getNode(rootNode, xpathExpression);
				String addressType = "";
				String addressTitle = "";
				if(XPathUtils.nodeExists(node, "./idf:addressType")){
					addressType = XPathUtils.getString(node, "./idf:addressType").trim();
				}
				
				if(addressType.equals("2")){
					if(XPathUtils.nodeExists(node, "./idf:addressIndividualName")){
						addressTitle = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
					}
				}else if(addressType.equals("3")){
					if(XPathUtils.nodeExists(node, "./idf:addressIndividualName")){
						addressTitle = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
					}else if(XPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
						addressTitle = XPathUtils.getString(node, "./idf:addressOrganisationName").trim();
					}
				}else{
					if(XPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
						addressTitle = XPathUtils.getString(node, "./idf:addressOrganisationName").trim();
					}	
					
				}
				
				if(addressTitle.length() > 0 ){
					general.put("title", addressTitle);
				}
				
				if(addressType.length() > 0 ){
					addElementAddressType(udkElements, addressType);
				}
			}
			
			// "last modification"
			xpathExpression = "./idf:last-modified";
			if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
				Node node = XPathUtils.getNode(rootNode, xpathExpression);
				String modTime = "";
				if(XPathUtils.nodeExists(node, "./gco:DateTime")){
					modTime = XPathUtils.getString(node, "./gco:DateTime").trim();
				}
				
				if(modTime.length() > 0 ){
					general.put("modTime", UtilsDate.convertDateString(modTime.replace("T", ""), "yyyy-MM-ddHH:mm:ss", "dd.MM.yyyy HH:mm:ss"));
				}
			}
			
	// Tab "General"
			// "Beschreibung"
			xpathExpression = ".";
			getGeneralTab(elementsGeneral, xpathExpression);
			
			// Addresses
			xpathExpression = ".";
			getAddresses(elementsGeneral, xpathExpression);
			
			// "Übergeordnete Address"
			xpathExpression ="./idf:superiorParty";
			getReference(elementsGeneral, xpathExpression, ReferenceType.SUPERIOR, false);
			
			// "Untergeordnete Address"
			xpathExpression ="./idf:subordinatedParty";
			getReference(elementsGeneral, xpathExpression, ReferenceType.SUBORDINATE, false);
			
			// "Verweise Objekte"
			xpathExpression ="./idf:objectReference";
			getReference(elementsReference, xpathExpression, ReferenceType.OBJECT, true);
			
			
			if(elementsGeneral.size() > 0){
				content.put(DATA_TAB_GENERAL, elementsGeneral);
			}

			if(elementsReference.size() > 0){
				content.put(DATA_TAB_REFERENCE, elementsReference);
			}

			if(content!=null){
				HashMap element = new HashMap();
				element.put("type", "gmd");
				element.put("body", content);
				element.put("general", general);
				element.put("udk", udkElements);
				data.add(element);
			}
			
		}
	}
	
	
	private void getGeneralTab(ArrayList elements, String xpathExpression) {
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			Node node = XPathUtils.getNode(rootNode, xpathExpression);
			if (node.hasChildNodes()) {
				String alternateName = "";
				String description = "";
				
				// alternate name
				
				String subXPathExpression = "./gmd:alternateTitle";
				if (XPathUtils.nodeExists(node, subXPathExpression)) {
					alternateName = XPathUtils.getString(node, subXPathExpression).trim();
				}
				
				// description
				subXPathExpression = "./gmd:positionName";
				if (XPathUtils.nodeExists(node, subXPathExpression)) {
					description = XPathUtils.getString(node, subXPathExpression).trim();
					
					if (description != null) {
						description = description.replaceAll("\n", "<br/>");
						description = description.replaceAll("<(?!b>|/b>|i>|/i>|u>|/u>|p>|/p>|br>|br/>|br />|strong>|/strong>|ul>|/ul>|ol>|/ol>|li>|/li>)[^>]*>", "");
					}
				}
				if ((description.length() > 0) || alternateName.length() > 0) {
					addSectionTitle(elements, messages.getString("detail_description"));
					addElementEntryLabelAbove(elements, description, alternateName, false);
					closeDiv(elements);
				}
			}
		}
	}

	private void getReference(ArrayList elements, String xpathExpression, ReferenceType referenceType, boolean isObject) {
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			ArrayList linkList = new ArrayList();
			for (int i=0; i<nodeList.getLength();i++){
				Node node = nodeList.item(i);
				String uuid = "";
				String title = "";
				String type = "";
				
				if(XPathUtils.nodeExists(node, "@uuid")){
					uuid = XPathUtils.getString(node, "@uuid").trim();
				}
				
				if(isObject){
					if(XPathUtils.nodeExists(node, "./idf:objectName")){
						title = XPathUtils.getString(node, "./idf:objectName").trim();
					}
					
					if(XPathUtils.nodeExists(node, "./idf:objectType")){
						type = XPathUtils.getString(node, "./idf:objectType").trim();
					}
				}else{
					if(XPathUtils.nodeExists(node, "./idf:addressType")){
						type = XPathUtils.getString(node, "./idf:addressType").trim();
					}
					
					if(type.equals("2")){
						if(XPathUtils.nodeExists(node, "./idf:addressIndividualName")){
							title = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
						}
					}else if(type.equals("3")){
						if(XPathUtils.nodeExists(node, "./idf:addressIndividualName")){
							title = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
						}else if(XPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
							title = XPathUtils.getString(node, "./idf:addressOrganisationName").trim();
						}
					}else{
						if(XPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
							title = XPathUtils.getString(node, "./idf:addressOrganisationName").trim();
						}
					}
				}
				
				HashMap link = new HashMap();
	        	link.put("hasLinkIcon", new Boolean(true));
	        	link.put("isExtern", new Boolean(false));
	        	link.put("title", title);
	        	if(this.iPlugId != null){
	        		if(uuid != null){
	        			if(log.isDebugEnabled()){
	        				log.debug("Create URL with iPlug: '" + this.iPlugId + "' and UUID: '" + uuid +"'");
	        			}
	        			PortletURL actionUrl = response.createActionURL();
	        			if(isObject){
	        				actionUrl.setParameter("cmd", "doShowObjectDetail");
	        				actionUrl.setParameter("objId", uuid);
				    	}else{
	        				actionUrl.setParameter("cmd", "doShowDocument");
	        				actionUrl.setParameter("docuuid", uuid);
				    	}
	        			
	        			actionUrl.setParameter("plugid", this.iPlugId);
			    		link.put("href", actionUrl.toString());
			        	
		        	}else{
		        		link.put("href", "");
		        	}
	        	}else{
	        		link.put("href", "");
	        	}
	        	linkList.add(link);
			}
			if (linkList != null && linkList.size() > 0){
				switch (referenceType) {
					case SUPERIOR:
						addReferenceToElement(elements, linkList, messages.getString("superior_address"), null);
						break;
					case SUBORDINATE:
						addReferenceToElement(elements, linkList, messages.getString("subordinated_addresses"), null);
						break;
					case CROSS:
						addReferenceToElement(elements, linkList, null, messages.getString("cross_references"));
						break;
					case OBJECT:
						addReferenceToElement(elements, linkList, messages.getString("search.detail.dataRelations"), null);
						break;
					default:
						break;
				}
			}
		}
	}

	

	
	// "Addressen"
	private void getAddresses(ArrayList elements, String xpathExpression) {
		ArrayList elementsAddress = new ArrayList();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node childNode = nodeList.item(i);
				if (childNode.hasChildNodes()) {
					String xpathExpressionContact = ".";
					if(XPathUtils.nodeExists(childNode, xpathExpressionContact)){
						Node subNode = XPathUtils.getNode(childNode, xpathExpressionContact);
						addSingleAddress(elementsAddress, subNode);	
					}
				}
			}
		}
		
		if(elementsAddress.size() > 0){
			HashMap elementAddress = new HashMap();
			elementAddress.put("type", "multiLineAddresses");
			elementAddress.put("title", messages.getString("addresses"));
			elementAddress.put("id", "addresses_id");
			elementAddress.put("elementsAddress", elementsAddress);
			elements.add(elementAddress);
		}
	}
	
	private void addSingleAddress(ArrayList elementsAddress, Node node) {
		if (node != null) {
			HashMap element;
			String xpathExpression;
			boolean isIdfResponsibleParty = false;
			
			if(node.getLocalName().trim().equals("idfResponsibleParty")){
				isIdfResponsibleParty = true;
			}else{
				isIdfResponsibleParty = false;
			}
			
			xpathExpression = "gmd:role/@gco:nilReason";
			if (XPathUtils.nodeExists(node, xpathExpression)) {
				
				element = addElementAddress("multiLine", "", "", "false", new ArrayList());
				ArrayList elements = (ArrayList) element.get("elements");
				
				if(isIdfResponsibleParty){
					if(XPathUtils.nodeExists(node, "./idf:hierarchyParty")){
						NodeList hierarchyPartyNodeList = XPathUtils.getNodeList(node, "./idf:hierarchyParty");
						for(int i=hierarchyPartyNodeList.getLength(); 0 < i ;i--){
							Node hierarchyPartyNode = hierarchyPartyNodeList.item(i-1);
							String uuid = "";
							String value = "";
							String type = "";
							uuid = XPathUtils.getString(hierarchyPartyNode, "./@uuid").trim();
							
							// "Type"
							if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressType")){
								type = XPathUtils.getString(hierarchyPartyNode, "./idf:addressType").trim();
							}
							
							if(type.equals("2")){
								// "Name"
								if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressIndividualName")){
									value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressIndividualName").trim();
									elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), getIndividualName(value), uuid));
								}
							}else if(type.equals("3")){
								if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressIndividualName")){
									value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressIndividualName").trim();
									elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), getIndividualName(value), uuid));
								}else if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressOrganisationName")){
									value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressOrganisationName").trim();
									elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), value, uuid));
								}
							}else{
								// "Organisation"
								if(XPathUtils.nodeExists(hierarchyPartyNode, "./idf:addressOrganisationName")){
									value = XPathUtils.getString(hierarchyPartyNode, "./idf:addressOrganisationName").trim();
									elements.add(addElementLink("linkLine", new Boolean(false), new Boolean(false), value, uuid));
								}
							}
						}
					}
				}else{
					// Organistation
					xpathExpression = "gmd:organisationName";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
						for(int i=0; i<valueTokenizer.countTokens();i++) {
							addElement(elements, "textLine", valueTokenizer.nextToken());
						}
					}
					
					// Name
					xpathExpression = "gmd:individualName";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						addElement(elements, "textLine", getIndividualName(value));
					}
				}
				
				addSpace(elements);
				// "Delivery point"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String deliveryPoint = XPathUtils.getString(node, xpathExpression).trim();
					if(deliveryPoint.matches("\\d*")){
						addElement(elements, "textLine", messages.getString("postbox_label") + " " + deliveryPoint);	
					}else{
						addElement(elements, "textLine", deliveryPoint);
					}
				}
				
				String postalCode = "";
				String city = "";
				
				// "Postcode"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					postalCode = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				// "City"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					city = XPathUtils.getString(node, xpathExpression).trim();
				}
				
				if (postalCode.length() > 0 || city.length() > 0) {
					addElement(elements, "textLine", postalCode.concat(" ").concat(city));
				}
				
				// "Country"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String country = XPathUtils.getString(node, xpathExpression).trim();
					if(country != null){
						String value = UtilsCountryCodelist.getNameFromCode(UtilsCountryCodelist.getCodeFromShortcut3(country), this.request.getLocale().toString());
						if(value != null){
							addElement(elements, "textLine", value);
						}else{
							addElement(elements, "textLine", country);
						}
					}
				}
				addSpace(elements);
				
				// "Mail"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList electronicMailAddressNodeList = XPathUtils.getNodeList(node, xpathExpression);
					for (int i = 0; i < electronicMailAddressNodeList.getLength(); i++) {
						String email = XPathUtils.getString(electronicMailAddressNodeList.item(i), ".").trim();
						elements.add(addElementEmailWeb("Email:", email, email, email, LinkType.EMAIL));
					}
				}
				
				// "Telefon"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", value, "Telefon:");
				}
				
				// "Fax"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", value, "Fax:");
				}
				
				// "URL"
				xpathExpression = "gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					elements.add(addElementEmailWeb("URL:", value, value, value, LinkType.WWW_URL));
				}
				
				elementsAddress.add(element);
			}
		}
	}
	
	private HashMap addElementEmailWeb(String title, String href, String body, String altText, LinkType linkType) {
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
				if (href.startsWith("http")) {
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
		if (type != null)
			element.put("type", type);
		if (body != null)
			element.put("body", body);
		if (title != null)
			element.put("title", title);
		
		elements.add(element);
	}
	
	private HashMap addElementLink(String type, Boolean hasLinkIcon, Boolean isExtern, String title, String uuid) {
		HashMap element = new HashMap();
		if (type != null)
			element.put("type", type);
		if (title != null)
			element.put("title", title);
		if (hasLinkIcon)
			element.put("sort", hasLinkIcon);
		if (isExtern != null)
			element.put("isExtern", isExtern);
		
		if (this.iPlugId != null){
			if(uuid != null){
				if(log.isDebugEnabled()){
    				log.debug("Create URL with iPlug: '" + this.iPlugId + "' and UUID: '" + uuid +"'");
    			}
				PortletURL actionUrl = response.createActionURL();
		    	actionUrl.setParameter("cmd", "doShowAddressDetail");
				actionUrl.setParameter("addrId", uuid);
				actionUrl.setParameter("plugid", this.iPlugId);
				element.put("href", actionUrl.toString());
			}else{
				element.put("href", "");
			}
		}else{
			element.put("href", "");
		}
			
		return element;
	}
	
	private HashMap addElementAddress(String type, String title, String body, String sort, ArrayList elements) {
		HashMap element = new HashMap();
		if (type != null)
			element.put("type", type);
		if (title != null)
			element.put("title", title);
		if (sort != null)
			element.put("sort", sort);
		if (body != null)
			element.put("body", body);
		if (elements != null)
			element.put("elements", elements);
		
		return element;
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
}
