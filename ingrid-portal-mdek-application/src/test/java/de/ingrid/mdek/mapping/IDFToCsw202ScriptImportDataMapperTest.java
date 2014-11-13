/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.mapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.persistency.ICodeListPersistency;
import de.ingrid.codelists.persistency.IgeCodeListPersistency;
import de.ingrid.mdek.handler.HashMapProtocolHandler;
import de.ingrid.utils.xml.XMLUtils;

public class IDFToCsw202ScriptImportDataMapperTest extends TestCase {
	
	private ScriptImportDataMapper mapper;
	
	private String mapperScript 	= "/import/mapper/csw202_to_ingrid_igc.js";
	
	private String templateIGC 			= "/import/templates/igc_template_csw202.xml";
	
	public void setUp() {
		mapper = new ScriptImportDataMapper();
		
		CodeListService cls = new CodeListService();
        ICodeListPersistency persistency = new IgeCodeListPersistency();
        List<ICodeListPersistency> persistencies = new ArrayList<ICodeListPersistency>();
        persistencies.add(persistency);
        cls.setPersistencies(persistencies);
        cls.setDefaultPersistency(0);
        mapper.setCodeListService(cls);
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

	public final void testConvertIDF() throws Exception {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/idf_1_0_0.xml";
		
		InputStream data = null;
		try {
			// get example file from test resource directory
			// spring-dependency is used to access test-resources (search from every class path!)
			data = (new ClassPathResource(exampleXml)).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(data.toString());
		
		// strip IDF to plain CSW
		data = stripIDF(data);

		// System.out.println("start mapping: " + XMLUtils.toString(getDomFromSourceData(data)));
		HashMapProtocolHandler protocolHandler = new HashMapProtocolHandler();
		protocolHandler.setCurrentFilename("idf_1_0_0.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
/*
			// Version written to IGC Input XML
//			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", "H&amp;#246;henstatus 110"));
			// Version read by xpathExists, we use this one to avoid assertion failure
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/general/title", "H&#246;henstatus 110"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/technical-domain/map/datasource-identificator", "ingrid#Niedersachsen#E13A483B-4FAB-11D3-AE6B-00104B57C66D"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/technical-domain/map/degree-of-record", "011.0"));
			result.reset();
*/
			System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
		
	}

	private InputStream stripIDF(InputStream idfData) throws Exception {
        Source style = new StreamSource(new ClassPathResource("/de/ingrid/mdek/mapping/idf_1_0_0_to_iso_metadata.xsl").getInputStream());
        // create transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(style);

        SAXReader xmlReader = new SAXReader();
        org.dom4j.Document sourceDoc = xmlReader.read(idfData);

        DocumentSource source = new DocumentSource(sourceDoc);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);

        org.dom4j.Document resultDoc = result.getDocument();
        String resultString = resultDoc.asXML();
        InputStream is = new ByteArrayInputStream(resultString.getBytes("UTF-8"));

        return is;
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

	private boolean xpathExists(InputStream in, String path, String value) {
		boolean found = false;
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = null;
		try {
			node = (Node) xpath.evaluate(path, getDomFromSourceData(in), XPathConstants.NODE);
			if (node != null) {
				String nodeValue = node.getFirstChild().getNodeValue();
				if (nodeValue.equals(value)) {
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
