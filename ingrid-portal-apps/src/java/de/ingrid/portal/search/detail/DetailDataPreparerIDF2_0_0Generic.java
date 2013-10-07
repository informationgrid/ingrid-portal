/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.search.detail.idf.part.DetailPartPreparer;
import de.ingrid.portal.search.detail.idf.part.DetailPartPreparerIdfAddress;
import de.ingrid.portal.search.detail.idf.part.DetailPartPreparerIdfKml;
import de.ingrid.portal.search.detail.idf.part.DetailPartPreparerIdfMetadata;
import de.ingrid.portal.search.detail.idf.part.om.RenderElement;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

/**
 * - split object_access and object_use (no table, instead multiple lines).
 */
public class DetailDataPreparerIDF2_0_0Generic implements DetailDataPreparer {
	
	private final static Logger		log							= LoggerFactory.getLogger(DetailDataPreparerIDF2_0_0Generic.class);
	
	private Context					context;
	private String					iPlugId;
	private RenderRequest			request;
	private RenderResponse			response;
	private IngridResourceBundle	messages;
	private IngridSysCodeList		sysCodeList;
	
	private List<DetailPartPreparer> preparer;
	
	public DetailDataPreparerIDF2_0_0Generic(Context context, String iPlugId, RenderRequest request, RenderResponse response) {
		this.context = context;
		this.iPlugId = iPlugId;
		this.request = request;
		this.response = response;
		messages = (IngridResourceBundle) context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	/*
	 * (non-Javadoc)	 * 
	 * @see
	 * de.ingrid.portal.search.detail.DetailDataPreparer#prepare(de.ingrid.utils
	 * .dsc.Record)
	 */
	public void prepare(Record record) throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
		
	    String idfString = IdfTool.getIdfDataFromRecord(record);
		List<RenderElement> renderElements = new ArrayList();
		
		if(idfString != null){
			if(log.isDebugEnabled()){
				log.debug(idfString);
			} 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document idfDoc = db.parse(new InputSource(new StringReader(idfString)));
			
			XPathUtils.getXPathInstance(new IDFNamespaceContext());
			
			Element root = idfDoc.getDocumentElement();
			transformNode(renderElements, root);
    		context.put("renderElements", renderElements);
		} 
		
	}
	
	private void transformNode(List<RenderElement> renderElements, Node node) {
    	String localTagName = node.getLocalName();
    	String nameSpaceUri = node.getNamespaceURI();
    	boolean isGenericIdfNode = false;
        
    	if(localTagName != null && nameSpaceUri != null){
    		if(localTagName.equals("head") && nameSpaceUri.equals(IDFNamespaceContext.NAMESPACE_URI_IDF)){
        		// Render title node
            	if(XPathUtils.nodeExists(node, "./idf:title")){
            		Node subNode = XPathUtils.getNode(node, "./idf:title");
            	    context.put("title", subNode.getFirstChild().getNodeValue().trim());
            	}
            }else{
            	// Get title (if not exist on head tag) from metadata or address tag
            	getTitle(node);
            	
            	DetailPartPreparer dpp = findDetailPartPreparer(localTagName, nameSpaceUri);
            	if(dpp != null){
            		// Render 
            		dpp.init(node, this.iPlugId,this.request,this.response, this.context);
            		RenderElement renderElement = new RenderElement();
            		renderElement.setType("render");
            		renderElement.setPreparer(dpp);
            		renderElements.add(renderElement);
            	}else{
            		isGenericIdfNode = true;
            		renderHtmlTag(renderElements, node, isGenericIdfNode);
            	}
            }
    	}else{
    		isGenericIdfNode = true;
    		renderHtmlTag(renderElements, node, isGenericIdfNode);
    	}
	}
	
	private void getTitle(Node node) {
		String title = (String) context.get("title");
    	if(title == null || title.length() == 0){
    		String xpathExpression = "./gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title";
    		if (XPathUtils.nodeExists(node, xpathExpression)) {
    			Node tmpNode = XPathUtils.getNode(node, xpathExpression);
    			String tmpTitle = tmpNode.getTextContent().trim();
    	        if(tmpTitle.length() > 0){
    	        	 context.put("title",tmpTitle);
    	        }
    		}
    	}
    	
    	title = (String) context.get("title");
    	if(title == null || title.length() == 0){
    		String xpathExpression = "./idf:hierarchyParty";
			if (XPathUtils.nodeExists(node, xpathExpression)) {
				Node tmpNode = XPathUtils.getNode(node, xpathExpression);
				String addressType = "";
				String tmpTitle = "";
				if(XPathUtils.nodeExists(tmpNode, "./idf:addressType")){
					addressType = XPathUtils.getString(tmpNode, "./idf:addressType").trim();
				}
				
				if(addressType.equals("2")){
					if(XPathUtils.nodeExists(tmpNode, "./idf:addressIndividualName")){
						tmpTitle = getIndividualName(XPathUtils.getString(tmpNode, "./idf:addressIndividualName").trim());
					}
				}else if(addressType.equals("3")){
					if(XPathUtils.nodeExists(tmpNode, "./idf:addressIndividualName")){
						tmpTitle = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
					}else if(XPathUtils.nodeExists(tmpNode, "./idf:addressOrganisationName")){
						tmpTitle = XPathUtils.getString(tmpNode, "./idf:addressOrganisationName").trim();
					}
				}else{
					if(XPathUtils.nodeExists(tmpNode, "./idf:addressOrganisationName")){
						tmpTitle = XPathUtils.getString(tmpNode, "./idf:addressOrganisationName").trim();
					}	
					
				}
				
				if(tmpTitle.length() > 0 ){
					context.put("title",tmpTitle);
				}
			}
    	}
	}

	private DetailPartPreparer findDetailPartPreparer(String localTagName, String nameSpaceUri){
		DetailPartPreparer dpp = null;
		if(localTagName != null && nameSpaceUri != null){
			if ((localTagName.equals("idfMdMetadata") && nameSpaceUri.equals(IDFNamespaceContext.NAMESPACE_URI_IDF)) 
					|| (localTagName.equals("MD_Metadata") && nameSpaceUri.equals(IDFNamespaceContext.NAMESPACE_URI_GMD)) ) {
	    		dpp = new DetailPartPreparerIdfMetadata();
	        } else if((localTagName.equals("idfResponsibleParty") && nameSpaceUri.equals(IDFNamespaceContext.NAMESPACE_URI_IDF))
	        		|| (localTagName.equals("CI_ResponsibleParty") && nameSpaceUri.equals(IDFNamespaceContext.NAMESPACE_URI_GMD)) ) {
	        	// Render address node
	        	dpp = new DetailPartPreparerIdfAddress();
	        } else if(localTagName.equals("kml") && nameSpaceUri.equals(IDFNamespaceContext.NAMESPACE_URI_KML)){
	    		// Render KML node
	        	dpp = new DetailPartPreparerIdfKml();
	    	}
		}
		return dpp;
	}
	
	private void renderGenericTag(List<RenderElement> renderElements, Node node){
		RenderElement renderElement = new RenderElement();
        renderElement.setType("html");
        String body="";
        if (node.getNodeType() == Node.TEXT_NODE) {
        	body = node.getNodeValue().trim();
        	renderElement.setBody(body);
        } else {
            body = "<" + node.getLocalName().trim();
            for (int i=0; i<node.getAttributes().getLength(); i++) {
                body += " " + node.getAttributes().item(i).getNodeName().trim() + "=\"" + node.getAttributes().item(i).getNodeValue().trim() + "\"";
            }
            body += ">";
            renderElement.setBody(body);
        }
        if(body.length() > 0){
        	renderElements.add(renderElement);
        }
	}
	
	private void renderHtmlTag(List<RenderElement> renderElements, Node node, boolean isGenericIdfNode){
		renderGenericTag(renderElements, node);
        
        if(node.hasChildNodes()){
        	for (int i=0; i< node.getChildNodes().getLength(); i++) {
    	        transformNode(renderElements, node.getChildNodes().item(i));
    	    }
        }
        
        if (isGenericIdfNode && node.getNodeType() != Node.TEXT_NODE) {
        	RenderElement renderElement = new RenderElement();
        	renderElement.setType("html");
            String body="";
            if (node.getNodeType() == Node.TEXT_NODE) {
            	body = node.getNodeValue().trim();
            	renderElement.setBody(body);
            } else {
            	body = node.getLocalName().trim();
            	renderElement.setBody("</" + body + ">");
            }
            if(body.length() > 0){
            	renderElements.add(renderElement);
            }
        }
	}
	
	private String getIndividualName(String value) {
		String[] valueSpitter = value.split(",");
		
		String name = "";
		for (int j=valueSpitter.length; 0 < j ;j--){
			name = name + " " + valueSpitter[j-1];
		}	
		return name;
	}
}
