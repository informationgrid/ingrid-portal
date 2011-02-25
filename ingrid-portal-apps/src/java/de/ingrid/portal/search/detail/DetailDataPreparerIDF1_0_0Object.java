/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

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
public class DetailDataPreparerIDF1_0_0Object implements DetailDataPreparer {
	
	private final static Log		log							= LogFactory.getLog(DetailDataPreparerIDF1_0_0Object.class);
	
	private Context					context;
	private String					iPlugId;
	private RenderRequest			request;
	private RenderResponse			response;
	private IngridResourceBundle	messages;
	private IngridSysCodeList		sysCodeList;
	
	public DetailDataPreparerIDF1_0_0Object(Context context, String iPlugId, RenderRequest request, RenderResponse response) {
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
		
		DetailDataPreparerIdf1_0_0 idf = new DetailDataPreparerIdf1_0_0(doc, this.context, this.request);
		idf.prepare(data);
	
		nodeExist = XPathUtils.nodeExists(doc, "//gmd:MD_Metadata");
		if(nodeExist){
			node = XPathUtils.getNode(doc, "//gmd:MD_Metadata");
			DetailDataPreparerIdf1_0_0_Md_Metadata gmd = new DetailDataPreparerIdf1_0_0_Md_Metadata(node, this.context, this.request);
			gmd.prepare(data);
		}
		
		nodeExist = XPathUtils.nodeExists(doc, "//kml:kml");
		if(nodeExist){
			node = XPathUtils.getNode(doc, "//kml:kml");
			DetailDataPreparerIdf1_0_0_Kml kml = new DetailDataPreparerIdf1_0_0_Kml(node, this.context, this.request);
			kml.prepare(data);
		}
	}
}
