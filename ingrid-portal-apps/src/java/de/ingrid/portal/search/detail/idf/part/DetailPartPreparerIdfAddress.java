package de.ingrid.portal.search.detail.idf.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.portal.search.detail.idf.DetailDataPreparerIdf1_0_0.LinkType;
import de.ingrid.portal.search.detail.idf.DetailDataPreparerIdf1_0_0.ReferenceType;
import de.ingrid.utils.udk.UtilsCountryCodelist;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

public class DetailPartPreparerIdfAddress extends DetailPartPreparer{

	@Override
	public void init(Node node, String iPlugId, RenderRequest request, RenderResponse response, Context context) {
		this.rootNode = node;
		this.iPlugId = iPlugId;
		this.request = request;
		this.response = response;
		this.templateName = "/WEB-INF/templates/detail/part/detail_part_preparer_address.vm";
		this.namespaceUri = IDFNamespaceContext.NAMESPACE_URI_IDF;
		this.localTagName = "idfResponsibleParty";
		this.context = context;
		this.messages = (IngridResourceBundle) context.get("MESSAGES");
		this.sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	public String getAddressClassType() {
		String xpathExpression = "./idf:hierarchyParty";
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
        	if(XPathUtils.nodeExists(node, "./idf:addressType")){
				return XPathUtils.getString(node, "./idf:addressType").trim();
			}
        }
        return "1"; 
    }
	
	public String getAddressClassTitle(String addressType) {
		String addressTitle = null;
		String xpathExpression = "./idf:hierarchyParty";
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
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
        }
		return addressTitle;
	}
	
	public ArrayList<HashMap<String, Object>> getReference(String xpathExpression, boolean isObject) {
		ArrayList<HashMap<String, Object>> linkList = new ArrayList<HashMap<String, Object>>();
		if(XPathUtils.nodeExists(rootNode, xpathExpression)){
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
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
		}
		return linkList;
	}

	// "Addressen"
	public HashMap getAddresses(String xpathExpression) {
		ArrayList elementsAddress = new ArrayList();
		HashMap elementAddress = new HashMap();
		
		if (XPathUtils.nodeExists(rootNode, xpathExpression)) {
			NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node childNode = nodeList.item(i);
				if (childNode.hasChildNodes()) {
					addSingleAddress(elementsAddress, childNode);	
				}
			}
		}
		
		if(elementsAddress.size() > 0){
			elementAddress.put("type", "multiLineAddresses");
			elementAddress.put("title", messages.getString("addresses"));
			elementAddress.put("id", "addresses_id");
			elementAddress.put("elementsAddress", elementsAddress);
		}
		return elementAddress;
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
			
			xpathExpression = "./gmd:role/@gco:nilReason";
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
					xpathExpression = "./gmd:organisationName";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						StringTokenizer valueTokenizer = new StringTokenizer(value, ",");
						for(int i=0; i<valueTokenizer.countTokens();i++) {
							addElement(elements, "textLine", valueTokenizer.nextToken());
						}
					}
					
					// Name
					xpathExpression = "./gmd:individualName";
					if (XPathUtils.nodeExists(node, xpathExpression)) {
						String value = XPathUtils.getString(node, xpathExpression).trim();
						addElement(elements, "textLine", getIndividualName(value));
					}
				}
				
				addSpace(elements);
				// "Delivery point"
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
				}
				addSpace(elements);
				
				// "Mail"
				xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					NodeList electronicMailAddressNodeList = XPathUtils.getNodeList(node, xpathExpression);
					for (int i = 0; i < electronicMailAddressNodeList.getLength(); i++) {
						String email = XPathUtils.getString(electronicMailAddressNodeList.item(i), ".").trim();
						elements.add(addElementEmailWeb("Email:", email, email, email, LinkType.EMAIL));
					}
				}
				
				// "Telefon"
				xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", value, "Telefon:");
				}
				
				// "Fax"
				xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					addElement(elements, "textLine", value, "Fax:");
				}
				
				// "URL"
				xpathExpression = "./gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL";
				if (XPathUtils.nodeExists(node, xpathExpression)) {
					String value = XPathUtils.getString(node, xpathExpression).trim();
					elements.add(addElementEmailWeb("URL:", value, value, value, LinkType.WWW_URL));
				}
				
				elementsAddress.add(element);
			}
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
}
