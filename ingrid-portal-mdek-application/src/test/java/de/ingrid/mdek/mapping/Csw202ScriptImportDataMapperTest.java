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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.persistency.ICodeListPersistency;
import de.ingrid.codelists.persistency.XmlCodeListPersistency;
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
		
		CodeListService cls = new CodeListService();
		XmlCodeListPersistency persistency = new XmlCodeListPersistency();
		persistency.setPathToXml("src/test/resources/codelists.xml");
		
		List<ICodeListPersistency> persistencies = new ArrayList<ICodeListPersistency>();
		persistencies.add(persistency);
		cls.setPersistencies(persistencies);
		cls.setDefaultPersistency(0);
		mapper.setCodeListService(cls);
		
	}
	
	

    public final void testConvertgeoInformationKarteComplete() throws TransformerException, IOException {
        // set variables that are needed for running correctly
        initClassVariables(mapperScript, templateIGC);
        
        String exampleXml = "/de/ingrid/mdek/mapping/Geo_Information_Karte_vollstaendig-test2.xml";
        
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
        protocolHandler.setCurrentFilename("Geo_Information_Karte_vollstaendig-test2.xml");
        InputStream result;
        try {
            result = mapper.convert(data, protocolHandler);
            assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/technical-domain/map/publication-scale/scale", "100000"));
            result.reset();
        } catch (Exception e) {
            fail("Error transforming: " + exampleXml);
        }
        
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
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/general/title", "Naturschutzgebiete Sachsen-Anhalt"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/technical-domain/map/datasource-identificator", "866EF2B4-33C5-436E-A4E3-BA59DDAF0703"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/additional-information/access-constraint/restriction", "no conditions apply"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/additional-information/use-constraint/terms-of-use", "no conditions apply"));
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
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/general/title", "Test_Schutzgebiete"));
			// service-type set as attribute in node !
			result.reset();
			assertEquals(1, xpathCount(result, "//igc/data-sources/data-source/data-source-instance/technical-domain/service/service-type"));
            result.reset();
            assertEquals(1, xpathCount(result, "//igc/data-sources/data-source/data-source-instance/technical-domain/service/service-classification[./@id='202']"));
            result.reset();
            assertEquals(0, xpathCount(result, "//igc/data-sources/data-source/data-source-instance/subject-terms/uncontrolled-term[text()='infoMapAccessService']"));
            
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
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/general/title", "Naturschutzgebiete Sachsen-Anhalt"));
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
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/general/title", "Naturschutzgebiete Sachsen-Anhalt"));
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
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/general/title", "Gewässerflächen im Münsterland nach INSPIRE DataSpecification Hydrography"));
			result.reset();
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/technical-domain/map/datasource-identificator", "http://www.bkg.de#_6D115576E-4813-3C7D-786C2-563760A88D8"));
			result.reset();
			System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
	}
	
	public final void testConvertCSWImport1() throws TransformerException, IOException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/csw202apIso10_dataset_Ueberschwemmungsflaechen.xml";
		
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
		protocolHandler.setCurrentFilename("csw202apIso10_dataset_Ueberschwemmungsflaechen.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/general/title", "Karte der Überschwemmungsflächen der Gewässer II. und III. Ordnung"));
			result.reset();
			System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
	}

	public final void testConvertCSWImport2() throws TransformerException, IOException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/csw202apIso10_application_HessenAnalyser_MultiLang.xml";
		
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
		protocolHandler.setCurrentFilename("csw202apIso10_application_HessenAnalyser_MultiLang.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/general/title", "Hessen Wasser Analyser"));
			result.reset();
			System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
			fail("HierachyLevel application should not be supported!");
		} catch (Exception e) {}
	}

	/** See https://dev.wemove.com/jira/browse/INGRID-2321 */
	public final void testConvertCSWImport_INGRID_2321() throws TransformerException, IOException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/MD_Metadata_11111-0001_dataset.xml";
		
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
		protocolHandler.setCurrentFilename("MD_Metadata_11111-0001_dataset.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/technical-domain/map/datasource-identificator", "DE-ST#30303031-3131-4031-312d-303030310000"));
			result.reset();
			System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
	}

	/** See https://dev.wemove.com/jira/browse/INGRID-2334 */
	public final void testConvertCSWImport_INGRID_2334() throws TransformerException, IOException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/MD_Metadata_001-03-4_dataset.xml";
		
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
		protocolHandler.setCurrentFilename("MD_Metadata_11111-0001_dataset.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/subject-terms/controlled-term[@id='310' and @source='INSPIRE']", "Population distribution — demography"));
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
			assertEquals(3, xpathCount(result, "//igc/addresses/address/address-instance"));
			result.reset();
			System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
		} catch (Exception e) {
			fail("Error transforming: " + exampleXml);
		}
	}

	public final void testConvertSTService() throws TransformerException, IOException {
		// set variables that are needed for running correctly
		initClassVariables(mapperScript, templateIGC);
		
		String exampleXml = "/de/ingrid/mdek/mapping/th_0544200808464974_iso19119.xml";
		
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
		protocolHandler.setCurrentFilename("th_0544200808464974_iso19119.xml");
		InputStream result;
		try {
			result = mapper.convert(data, protocolHandler);
			assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/available-linkage[2]/linkage-url", "invalid link"));
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
	
    public final void testConvertInspireAnex1Dataset() throws TransformerException, IOException {
        // set variables that are needed for running correctly
        initClassVariables(mapperScript, templateIGC);
        
        String exampleXml = "/de/ingrid/mdek/mapping/dataset_inspire_annex_I_admin_units_2010-07-01.xml";
        
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
        protocolHandler.setCurrentFilename("dataset_inspire_annex_I_admin_units_2010-07-01.xml");
        InputStream result;
        try {
            result = mapper.convert(data, protocolHandler);
            assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/temporal-domain/dataset-reference[1]/dataset-reference-date", "20020404000000000"));
            result.reset();
/*
// DOES NOT WORK ANYMORE DUE TO CORRECT MAPPING !!!!
// Reference File "dataset_inspire_annex_I_admin_units_2010-07-01.xml" IS WRONG concerning "DQ_ConformanceResult/specification/citation/CI_Citation"
// correct is "DQ_ConformanceResult/specification/CI_Citation"
            assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/additional-information/conformity/conformity-specification", "D2.8.I.4 Data Specification on Administrative units – Draft Guidelines"));
            result.reset();
            assertEquals(true, xpathExists(result, "//igc/data-sources/data-source/data-source-instance/additional-information/conformity/conformity-publication-date", "20100426000000000"));
            result.reset();
*/
            System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
        } catch (Exception e) {
            fail("Error transforming: " + exampleXml);
        }
    }	
    
    public final void testConvertServiceInspireAnnexITransNet() throws TransformerException, IOException {
        // set variables that are needed for running correctly
        initClassVariables(mapperScript, templateIGC);
        
        String exampleXml = "/de/ingrid/mdek/mapping/service_inspire_annex_I_trans_net_2010-07-01.xml";
        
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
        protocolHandler.setCurrentFilename("service_inspire_annex_I_trans_net_2010-07-01.xml");
        InputStream result;
        try {
            result = mapper.convert(data, protocolHandler);
            assertEquals(0, xpathCount(result, "//igc/addresses/address/country"));
            result.reset();
            System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
        } catch (Exception e) {
            fail("Error transforming: " + exampleXml);
        }
    }    
    
    public final void testConvertReferenceSystem() throws TransformerException, IOException {
        // set variables that are needed for running correctly
        initClassVariables(mapperScript, templateIGC);
        
        String[] exampleXmls = new String[] {
        		"/de/ingrid/mdek/mapping/csw202apIso10_dataset_referenceSystem.xml",
        		"/de/ingrid/mdek/mapping/csw202apIso10_service_referenceSystem.xml"
        		};
        
        for (String exampleXml : exampleXmls) {
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
            protocolHandler.setCurrentFilename(exampleXml);
            InputStream result;
            try {
                result = mapper.convert(data, protocolHandler);
                String coordXPath = "//igc/data-sources/data-source/data-source-instance/spatial-domain/coordinate-system";
                assertEquals(1, xpathCount(result, coordXPath));
                result.reset();

                // mapped from "EPSG:25833" to correct syslist name and id !
    			assertEquals(true, xpathExists(result, coordXPath, "EPSG 25833: ETRS89 / UTM Zone 33N"));
    			result.reset();
                assertEquals(1, xpathCount(result, coordXPath + "[./@id='25833']"));
                result.reset();
//                System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
            } catch (Exception e) {
                fail("Error transforming: " + exampleXml);
            }
        }
    }    
    
    public final void testConvertVerticalSystem() throws TransformerException, IOException {
        // set variables that are needed for running correctly
        initClassVariables(mapperScript, templateIGC);
        
        String[] exampleXmls = new String[] {
                "/de/ingrid/mdek/mapping/csw202apIso10_service_WMS_Wasser.xml"
                };
        
        for (String exampleXml : exampleXmls) {
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
            protocolHandler.setCurrentFilename(exampleXml);
            InputStream result;
            try {
                result = mapper.convert(data, protocolHandler);
                String vdatumXPath = "/igc/data-sources/data-source/data-source-instance/spatial-domain/vertical-extent/vertical-extent-vdatum";
                assertEquals(1, xpathCount(result, vdatumXPath));
                result.reset();

                // mapped from "EPSG:25833" to correct syslist name and id !
                assertEquals(true, xpathExists(result, vdatumXPath, "Normaal Amsterdams Peil (NAP)"));
                result.reset();
                assertEquals(1, xpathCount(result, vdatumXPath + "[./@id='-1']"));
                result.reset();
//                System.out.println("result: " + XMLUtils.toString(getDomFromSourceData(result)));
            } catch (Exception e) {
                fail("Error transforming: " + exampleXml);
            }
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
