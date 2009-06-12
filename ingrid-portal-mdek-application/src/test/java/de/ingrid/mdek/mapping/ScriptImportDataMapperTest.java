package de.ingrid.mdek.mapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ScriptImportDataMapperTest extends TestCase {
	
	private ScriptImportDataMapper mapper;
	
	private String mapperScriptArcGIS 	= "/import/mapper/ArcGIS_to_ingrid_igc.js";//"/de/ingrid/mdek/mapping/ArcGIS_to_ingrid_igc.js";
	
	private String templateIGC 			= "/import/templates/igc_template.xml";//"/de/ingrid/mdek/mapping/igc_template.xml";
	
	private String exampleXml 			= "/de/ingrid/mdek/mapping/sourceExample.xml";
	
	public void setUp() {
		mapper = new ScriptImportDataMapper();
	}
	
	
	private void initClassVariables(String mapperScript, String template) {
		// use files in main resource directory
		InputStream scriptIn = this.getClass().getResourceAsStream(mapperScript);
		InputStream templateIn = this.getClass().getResourceAsStream(template);	

		mapper.setMapperScript(scriptIn);
		mapper.setTemplate(templateIn);
		
	}

	public final void testConvertEqual() {
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data 		= null;
		
		data 		= this.getClass().getResourceAsStream(templateIGC);//(new ClassPathResource(templateIGC)).getInputStream();
		//expected 	= this.getClass().getResourceAsStream(templateIGC);//(new ClassPathResource(templateIGC)).getInputStream();
		
		InputStream result = mapper.convert(data);
		
		//assertEquals(inputStreamToBytes(expected), inputStreamToBytes(result));
	}
	
	public final void testConvertOne() {
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data 		= null;
		try {
			// get example file from test resource directory
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream result = mapper.convert(data);
		
		assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", "xxxTEMPLATExxx"));
	}
	
	/*
	private byte[] inputStreamToBytes(InputStream in) {
	
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		
		try {
		
			while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out.toByteArray();
	} */
	
	private boolean xpathExists(InputStream in, String path, String value) {
		boolean found = false;
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = null;
		try {
			node = (Node) xpath.evaluate(path, getDomFromSourceData(in), XPathConstants.NODE);
			if (node != null) {
				if (node.getFirstChild().getNodeValue().equals(value)) {
					found = true;
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return found;
	}
	
	private Document getDomFromSourceData(InputStream data) {
		Document doc = null;
		try { 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
	
			doc = db.parse(data);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return doc;
	}
}
