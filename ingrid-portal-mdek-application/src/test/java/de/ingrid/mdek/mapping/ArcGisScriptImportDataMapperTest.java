package de.ingrid.mdek.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.ingrid.utils.xml.XPathUtils;

public class ArcGisScriptImportDataMapperTest extends TestCase {
	
	private ScriptImportDataMapper mapper;
	
	private String mapperScriptArcGIS 	= "/import/mapper/ArcGIS_to_ingrid_igc.js";
	
	private String templateIGC 			= "/import/templates/igc_template.xml";
	
	// CHOOSE YOUR EXAMPLE !

//	private String exampleXml 			= "/de/ingrid/mdek/mapping/sourceExample.xml";
//	private String exampleTitle 		= "xxxTEMPLATExxx";

	private String exampleXml 			= "/de/ingrid/mdek/mapping/HB_Amtlicher_Stadtplan.xml";
	private String exampleTitle 		= "Amtlicher Stadtplan Bremerhaven";

	
	public void setUp() {
		mapper = new ScriptImportDataMapper();
	}
	
	private void initClassVariables(String mapperScript, String template) {
		// use files in main resource directory
		//InputStream scriptIn = this.getClass().getResourceAsStream(mapperScript);
		//InputStream templateIn = this.getClass().getResourceAsStream(template);	

		DefaultResourceLoader drl = new DefaultResourceLoader();
		drl.getResource(mapperScript);

		mapper.setMapperScript(drl.getResource(mapperScript));
		mapper.setTemplate(drl.getResource(template));
		
		mapper.setDataProvider(initDataProvider());	
		
	}

	public final void testConvertOne() {
		// set variables that are needed for running correctly
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream result = mapper.convert(data);
		
		assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", exampleTitle));
	}


	private ImportDataProvider initDataProvider() {
		MockImportDataProviderImpl dataProvider = new MockImportDataProviderImpl();
		HashMap<Integer, Integer> mapKeys = new HashMap<Integer, Integer>();
		mapKeys.put(99999999, 178);
		HashMap<Integer, String> mapValues = new HashMap<Integer, String>();
		mapValues.put(99999999, "Klingonisch");
		
		dataProvider.setUserId("12345678910");
		dataProvider.setInitialKeys(mapKeys);
		dataProvider.setInitialValues(mapValues);
		
		return dataProvider;
	}

	public final void testConvertDepmstAbgasLyr() {
		
		exampleXml = "/de/ingrid/mdek/mapping/depmst_abgas.lyr.xml";
		
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data 		= null;
		try {
			// get example file from test resource directory
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream result = mapper.convert(data);
		
		Document doc = getDomFromSourceData(result);
		
		assertEquals("depmst_abgas.lyr", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/title"));
		assertEquals("{29A18127-C648-463B-9146-B16A07D99514}", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/original-control-identifier"));
		assertTrue(XPathUtils.getString(doc, "/igc/data-sources/data-source/general/abstract").indexOf("Deutsch") > -1);
		
	}	
	
	public final void testConvertDepmstAbgasShp() {
		
		exampleXml = "/de/ingrid/mdek/mapping/depmst_abgas.shp.xml";
		
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data 		= null;
		try {
			// get example file from test resource directory
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream result = mapper.convert(data);
		
		Document doc = getDomFromSourceData(result);
		
		assertEquals("Deponie-Messtellen: Abgas", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/title"));
		assertEquals("{CD2A5009-D1E1-4D58-B6E1-FA4B870724BE}", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/original-control-identifier"));
		assertTrue(XPathUtils.getString(doc, "/igc/data-sources/data-source/general/abstract").indexOf("Folgende Sprachen werden im beschriebenen Datensatz verwendet") > -1);
		assertTrue(XPathUtils.getString(doc, "/igc/data-sources/data-source/general/abstract").indexOf("Datum der Ausgabe/Version") > -1);
		assertTrue(XPathUtils.getString(doc, "/igc/data-sources/data-source/general/abstract").indexOf("Deponiegasverwertung") > -1);
		
	}
	
	public final void testTgr02068wat() throws IOException, SAXException {
		
		exampleXml = "/de/ingrid/mdek/mapping/tgr02068wat.xml";
		
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream result = mapper.convert(data);
		
//		assertTrue(validateIgcImportXML(result));
		
		Document doc = getDomFromSourceData(result);
		
		assertEquals("[ISO ed. Titel] Wasserk\u00f6rper Polygone", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/title"));
		assertEquals("{606D692B-004D-4BD1-9364-B18A75614B89}", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/original-control-identifier"));
		assertTrue(XPathUtils.getString(doc, "/igc/data-sources/data-source/general/abstract").indexOf("[ISO ed. Nummer der Ausgabe/Version]") > -1);
		
	}	
	
	public final void testConvertDepmstGwShp() {
		
		exampleXml = "/de/ingrid/mdek/mapping/depmst_gw.shp.xml";
		
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream result = mapper.convert(data);
		
		Document doc = getDomFromSourceData(result);
		
		assertEquals("Grundwassermessstellen an Deponien", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/title"));
		assertEquals("{CD2A5009-D1E1-4D58-B6E1-FA4B870724BE}", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/original-control-identifier"));
		assertTrue(XPathUtils.getString(doc, "/igc/data-sources/data-source/general/abstract").indexOf("Der Datenbestand enth\u00e4lt die Lageinforamtionen (Punkte) der Grundwasser") > -1);
		
	}		

	public final void testConvertDepmstSiwaShp() {
		
		exampleXml = "/de/ingrid/mdek/mapping/depmst_siwa.shp.xml";
		
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream result = mapper.convert(data);
		
		Document doc = getDomFromSourceData(result);
		
		assertEquals("Deponie-Messtellen: Sickerwasser", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/title"));
		assertEquals("{CD2A5009-D1E1-4D58-B6E1-FA4B870724BE}", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/original-control-identifier"));
		assertTrue(XPathUtils.getString(doc, "/igc/data-sources/data-source/general/abstract").indexOf("Der Datenbestand enth\u00e4lt die Lageinforamtionen (Punkte) der Sickerwasser-Messstellen der Deponien in NRW.") > -1);
		
	}		
	
	public final void testConvertISHK500Metadata() {
		
		exampleXml = "/de/ingrid/mdek/mapping/ISHK500_Metadata.xml";
		
		initClassVariables(mapperScriptArcGIS, templateIGC);
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream result = mapper.convert(data);
		
		Document doc = getDomFromSourceData(result);
		
		assertEquals("tgd.gd.HK500", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/title"));
		assertEquals("{B50413B9-323A-40A2-AD0B-EAF0D84319CA}", XPathUtils.getString(doc, "/igc/data-sources/data-source/general/original-control-identifier"));
		assertTrue(XPathUtils.getString(doc, "/igc/data-sources/data-source/general/abstract").indexOf("Folgende Sprachen werden im beschriebenen") > -1);
		
	}		
	
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
	
	private boolean validateIgcImportXML(InputStream in) throws IOException, SAXException {

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI );
		Source schemaFile = new StreamSource((new ClassPathResource("IGC_Semantisches-XML.xsd")).getInputStream());
		
		Schema schema = schemaFactory.newSchema(schemaFile );
		Validator validator = schema.newValidator();
		
		StreamSource streamSource = new StreamSource( in );

		validator.validate( streamSource );
		
		return true;
		
	}
	
}
