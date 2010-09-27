package de.ingrid.mdek.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.mdek.handler.HashMapProtocolHandler;
import de.ingrid.utils.xml.XMLUtils;

public class Csw202ScriptImportDataMapperTest extends TestCase {
	
	private ScriptImportDataMapper mapper;
	
	private String mapperScript 	= "/import/mapper/csw202_to_ingrid_igc.js";
	
	private String templateIGC 			= "/import/templates/igc_template_csw202.xml";
	
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

	public final void testConvertObjectComplete() throws TransformerException, IOException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/inspire_datasetkomplett.xml";
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(data.toString());
		// System.out.println("start mapping: " + XMLUtils.toString(getDomFromSourceData(data)));
		HashMapProtocolHandler protocolHandler = new HashMapProtocolHandler();
		protocolHandler.setCurrentFilename("inspire_datasetkomplett.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", "Naturschutzgebiete Sachsen-Anhalt"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/technical-domain/map/datasource-identificator", "866EF2B4-33C5-436E-A4E3-BA59DDAF0703"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/additional-information/access-constraint/restriction", "no conditions apply"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/additional-information/use-constraint/terms-of-use", "no conditions apply"));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
		
	}

	public final void testConvertServiceComplete() throws TransformerException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/inspire_servicekomplett.xml";
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(data.toString());
		// System.out.println("start mapping: " + XMLUtils.toString(getDomFromSourceData(data)));
		HashMapProtocolHandler protocolHandler = new HashMapProtocolHandler();
		protocolHandler.setCurrentFilename("inspire_servicekomplett.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", "Test_Schutzgebiete"));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
		
	}

	public final void testConvertSTObjectsComplete() throws TransformerException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/st_xml19115.xml";
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("start mapping: " + XMLUtils.toString(getDomFromSourceData(data)));
		HashMapProtocolHandler protocolHandler1 = new HashMapProtocolHandler();
		protocolHandler1.setCurrentFilename("st_xml19115.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler1);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", "Naturschutzgebiete Sachsen-Anhalt"));
		} catch (Exception e1) {
			fail("Error transforming: " + exampleXml);
		}
		
		
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		exampleXml = "/de/ingrid/mdek/mapping/st_xml19119.xml";
		
		data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMapProtocolHandler protocolHandler2 = new HashMapProtocolHandler();
		protocolHandler2.setCurrentFilename("st_xml19119.xml");
		try {
			result = mapper.convert(data, protocolHandler2);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", "Naturschutzgebiete Sachsen-Anhalt"));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
	}
	

	public final void testConvertHHObjectsComplete() throws TransformerException, IOException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/csw202apIso10_dataset_Gewaesser_Muensterland.xml";
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("start mapping: " + XMLUtils.toString(getDomFromSourceData(data)));
		HashMapProtocolHandler protocolHandler = new HashMapProtocolHandler();
		protocolHandler.setCurrentFilename("csw202apIso10_dataset_Gewaesser_Muensterland.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", "Gewässerflächen im Münsterland nach INSPIRE DataSpecification Hydrography"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/technical-domain/map/datasource-identificator", "http://www.bkg.de#_6D115576E-4813-3C7D-786C2-563760A88D8"));
			result.reset();
			System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
	}
	

	public final void testConvertTHObjectsComplete() throws TransformerException, IOException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/th_20052010095904_iso19115.xml";
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("start mapping: " + XMLUtils.toString(getDomFromSourceData(data)));
		HashMapProtocolHandler protocolHandler = new HashMapProtocolHandler();
		protocolHandler.setCurrentFilename("th_20052010095904_iso19115.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(2, xpathCount(result, "//igc/addresses/address"));
			result.reset();
			System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
	}
	
	public final void testConvertInvalidFile() {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/depmst_abgas.lyr.xml";
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource("/de/ingrid/mdek/mapping/depmst_abgas.lyr.xml")).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("start mapping: " + XMLUtils.toString(getDomFromSourceData(data)));
		HashMapProtocolHandler protocolHandler = new HashMapProtocolHandler();
		protocolHandler.setCurrentFilename("depmst_abgas.lyr.xml");
		try {
			InputStream result = mapper.convert(data, protocolHandler);
			fail("Transformation should fail.");
		} catch (Exception e) {
			assertTrue(protocolHandler.getProtocol().indexOf("No valid ISO metadata record.") > -1);
		}
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

	/*
	 * 	public final void testConvertDepmstAbgasLyr() {
		
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
*/	
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

	private int xpathCount(InputStream in, String path) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = null;
		try {
			nodeList = (NodeList) xpath.evaluate(path, getDomFromSourceData(in), XPathConstants.NODESET);
			if (nodeList != null) {
				return nodeList.getLength();
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
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
