/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.context.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.search.detail.idf.DetailDataPreparerIdf1_0_0;
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
	
	private final static Log		log							= LogFactory.getLog(DetailDataPreparerIDF1_0_0Generic.class);
	
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
		HashMap data = new HashMap();
		
		if(idfString != null){
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document idfDoc = db.parse(new InputSource(new StringReader(idfString)));
			
    		evaluateNodes(data, idfDoc);
    		context.put("data", data);
		} 
		
	}
	
	private void evaluateNodes(HashMap data, Document doc) {
		Node node;
		boolean nodeExist;
		XPathUtils.getXPathInstance(new IDFNamespaceContext());
		
		Element root = doc.getDocumentElement();
		transformNode(data, root);
		
	}
	
	private void transformNode(HashMap data, Node node) {
        boolean isGenericIdfNode = false;
	    if (XPathUtils.nodeExists(node, "gmd:MD_Metadata")) {
            DetailDataPreparerIdf1_0_0_Md_Metadata gmd = new DetailDataPreparerIdf1_0_0_Md_Metadata(node, this.context, this.request);
            gmd.prepare(data);
            node = moveToNextNode(node);
        } else if (XPathUtils.nodeExists(node, "kml:kml")) {
            DetailDataPreparerIdf1_0_0_Kml kml = new DetailDataPreparerIdf1_0_0_Kml(node, this.context, this.request);
            kml.prepare(data);
            node = moveToNextNode(node);
        } else {
            isGenericIdfNode = true;
            List elements = (List)data.get("elements");
            if (elements == null) {
                elements = new ArrayList();
                data.put("elements", elements);
            }
            HashMap element = new HashMap();
            element.put("type", "html");
            if (node.getNodeType() == Node.TEXT_NODE) {
                element.put("body", node.getNodeValue());
            } else {
                String body = "<" + node.getLocalName();
                for (int i=0; i<node.getAttributes().getLength(); i++) {
                    body += " " + node.getAttributes().item(i).getNodeName() + "=\"" + node.getAttributes().item(i).getNodeValue() + "\"";
                }
                body += ">";
                element.put("body", body);
            }
            
            elements.add(element);
            
        }
        for (int i=0; i< node.getChildNodes().getLength(); i++) {
	        transformNode(data, node.getChildNodes().item(i));
	    }
        if (isGenericIdfNode && node.getNodeType() != Node.TEXT_NODE) {
            List elements = (List)data.get("elements");
            HashMap element = new HashMap();
            element.put("type", "html");
            if (node.getNodeType() == Node.TEXT_NODE) {
                element.put("body", node.getNodeValue());
            } else {
                element.put("body", "</" + node.getLocalName() + ">");
            }
            elements.add(element);
        }
        
        
	}
	
	private Node moveToNextNode(Node node) {
	    Node checkNode = node;
	    while (checkNode.getNextSibling() == null && checkNode.getParentNode() != null) {
	        checkNode = checkNode.getParentNode();
	    }
	    return checkNode.getNextSibling();
	}
	
	
}
