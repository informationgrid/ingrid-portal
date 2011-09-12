/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.context.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.search.detail.idf.DetailDataPreparerIdf1_0_0_Address;
import de.ingrid.portal.search.detail.idf.DetailDataPreparerIdf1_0_0_Kml;
import de.ingrid.portal.search.detail.idf.DetailDataPreparerIdf1_0_0_Md_Metadata;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

/**
 * - split object_access and object_use (no table, instead multiple lines).
 */
public class DetailDataPreparerIDF1_0_0Generic implements DetailDataPreparer {
	
	private final static Logger		log							= LoggerFactory.getLogger(DetailDataPreparerIDF1_0_0Generic.class);
	
	private Context					context;
	private String					iPlugId;
	private RenderRequest			request;
	private RenderResponse			response;
	private IngridResourceBundle	messages;
	private IngridSysCodeList		sysCodeList;
	
	public DetailDataPreparerIDF1_0_0Generic(Context context, String iPlugId, RenderRequest request, RenderResponse response) {
		this.context = context;
		this.iPlugId = iPlugId;
		this.request = request;
		this.response = response;
		messages = (IngridResourceBundle) context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ingrid.portal.search.detail.DetailDataPreparer#prepare(de.ingrid.utils
	 * .dsc.Record)
	 */
	public void prepare(Record record) throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
		
	    String idfString = IdfTool.getIdfDataFromRecord(record);
		ArrayList data = new ArrayList();
		
		if(idfString != null){
			if(log.isDebugEnabled()){
				log.debug(idfString);
			}
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document idfDoc = db.parse(new InputSource(new StringReader(idfString)));
			
    		evaluateNodes(data, idfDoc);
    		context.put("data", data);
		} 
		
	}
	
	private void evaluateNodes(ArrayList data, Document doc) {
		Node node;
		XPathUtils.getXPathInstance(new IDFNamespaceContext());
		
		Element root = doc.getDocumentElement();
		node = (Node) root;
		NodeList nodeList = node.getChildNodes();
		for (int i=0; i<nodeList.getLength();i++){
			transformNode(data, nodeList.item(i));
		}
	}
	
	private void transformNode(ArrayList data, Node node) {
        boolean isGenericIdfNode = false;
        if(node.getLocalName()!= null){
		    if(node.getLocalName().equals("head") && node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF)){
	        	if(XPathUtils.nodeExists(node, "./idf:title")){
	        		Node subNode = XPathUtils.getNode(node, "./idf:title");
	        	    context.put("title", subNode.getFirstChild().getNodeValue().trim());
	        	}
	        }else{
        		if ((node.getLocalName().equals("idfMdMetadata") && node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF)) 
        				|| (node.getLocalName().equals("MD_Metadata") && node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_GMD)) ) {
	    			Node subNode = XPathUtils.getNode(node, ".");
	        	    DetailDataPreparerIdf1_0_0_Md_Metadata metadata = new DetailDataPreparerIdf1_0_0_Md_Metadata(subNode, this.context, this.request, this.iPlugId, this.response);
	                metadata.prepare(data);
	            } else if((node.getLocalName().equals("idfResponsibleParty") && node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_IDF))
	            		|| (node.getLocalName().equals("CI_ResponsibleParty") && node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_GMD)) ) {
	            	Node subNode = XPathUtils.getNode(node, ".");
	            	DetailDataPreparerIdf1_0_0_Address address = new DetailDataPreparerIdf1_0_0_Address(subNode, this.context, this.request, this.iPlugId, this.response);
	            	address.prepare(data);	
	            } else {
	            	if(node.getLocalName().equals("kml") && node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_KML)){
	            		Node subNode = XPathUtils.getNode(node, ".");
	             	   	DetailDataPreparerIdf1_0_0_Kml kml = new DetailDataPreparerIdf1_0_0_Kml(subNode, this.context, this.request, this.iPlugId, this.response);
	                    kml.prepare(data);
	            	}else{
	            		isGenericIdfNode = true;
	                    renderGenericTag(data, node);
	                    
	                    if(node.hasChildNodes()){
	                    	for (int i=0; i< node.getChildNodes().getLength(); i++) {
	                	        transformNode(data, node.getChildNodes().item(i));
	                	    }
	                    }
	                    
	                    if (isGenericIdfNode && node.getNodeType() != Node.TEXT_NODE) {
	                        HashMap element = new HashMap();
	                        element.put("type", "html");
	                        String body="";
	                        if (node.getNodeType() == Node.TEXT_NODE) {
	                        	body = node.getNodeValue().trim();
	                            element.put("body", body);
	                        } else {
	                        	body = node.getLocalName().trim();
	                            element.put("body", "</" + body + ">");
	                        }
	                        if(body.length() > 0){
	                        	data.add(element);
	                        }
	                    }
	            	}
	            }
        	}
    	}else{
    		isGenericIdfNode = true;
            renderGenericTag(data, node);
            
            if(node.hasChildNodes()){
            	for (int i=0; i< node.getChildNodes().getLength(); i++) {
        	        transformNode(data, node.getChildNodes().item(i));
        	    }
            }
            
            if (isGenericIdfNode && node.getNodeType() != Node.TEXT_NODE) {
                HashMap element = new HashMap();
                element.put("type", "html");
                String body="";
                if (node.getNodeType() == Node.TEXT_NODE) {
                	body = node.getNodeValue().trim();
                    element.put("body", body);
                } else {
                	body = node.getLocalName().trim();
                    element.put("body", "</" + body + ">");
                }
                if(body.length() > 0){
                	data.add(element);
                }
            }
    	}	
	}
	
	private void renderGenericTag(ArrayList data, Node node){
		HashMap element = new HashMap();
        element.put("type", "html");
        String body="";
        if (node.getNodeType() == Node.TEXT_NODE) {
        	body = node.getNodeValue().trim();
        	element.put("body", body);
        } else {
            body = "<" + node.getLocalName().trim();
            for (int i=0; i<node.getAttributes().getLength(); i++) {
                body += " " + node.getAttributes().item(i).getNodeName().trim() + "=\"" + node.getAttributes().item(i).getNodeValue().trim() + "\"";
            }
            body += ">";
            element.put("body", body);
        }
        if(body.length() > 0){
        	data.add(element);
        }
	}
	
}
