/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.dwr;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.dwr.services.GetCapabilitiesService;

/**
 * @author André Wallat
 *
 */
public class GetCapabilitiesServiceTest {

    private static final String capDir = "de/ingrid/mdek/capabilities/";
    
    @Mock private SysListCache sysListMapper;
    @InjectMocks GetCapabilitiesService service = new GetCapabilitiesService();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        //service = new GetCapabilitiesService();
        MockitoAnnotations.initMocks(this);
        
        //when(sysListMapper.getValueFromListId(100, 84, false)).thenReturn("This is a known SRS");
        when(sysListMapper.getValueFromListId(100, 3068, false)).thenReturn("EPSG 3068: DHDN / Soldner Berlin");
        when(sysListMapper.getValueFromListId(100, 4178, false)).thenReturn("EPSG 4178: Pulkovo 1942(83) / geographisch");
        when(sysListMapper.getValueFromListId(100, 4230, false)).thenReturn("EPSG 4230: ED50 / geographisch");
        when(sysListMapper.getValueFromListId(100, 4258, false)).thenReturn("EPSG 4258: ETRS89 / geographisch");
        when(sysListMapper.getValueFromListId(100, 4284, false)).thenReturn("EPSG 4284: Pulkovo 1942 / geographisch ");
        when(sysListMapper.getValueFromListId(100, 4313, false)).thenReturn("EPSG 4314: DHDN / geographisch");
        when(sysListMapper.getValueFromListId(100, 4326, false)).thenReturn("EPSG 4326: WGS 84 / geographisch");
        when(sysListMapper.getValueFromListId(100, 23031, false)).thenReturn("EPSG 23031: ED50 / UTM Zone 31N");
        when(sysListMapper.getValueFromListId(100, 23032, false)).thenReturn("EPSG 23032: ED50 / UTM Zone 32N");
        when(sysListMapper.getValueFromListId(100, 23033, false)).thenReturn("EPSG 23033: ED50 / UTM Zone 33N");
        when(sysListMapper.getValueFromListId(100, 25831, false)).thenReturn("EPSG 25831: ETRS89 / UTM Zone 31N");
        when(sysListMapper.getValueFromListId(100, 25832, false)).thenReturn("EPSG 25832: ETRS89 / UTM Zone 32N");
        when(sysListMapper.getValueFromListId(100, 25833, false)).thenReturn("EPSG 25833: ETRS89 / UTM Zone 33N");
        when(sysListMapper.getValueFromListId(100, 25834, false)).thenReturn("EPSG 25834: ETRS89 / UTM Zone 34N");
        when(sysListMapper.getValueFromListId(100, 28462, false)).thenReturn("EPSG 28462: Pulkovo 1942 / Gauss-Krüger 2N");
        when(sysListMapper.getValueFromListId(100, 28463, false)).thenReturn("EPSG 28463: Pulkovo 1942 / Gauss-Krüger 3N");
        when(sysListMapper.getValueFromListId(100, 31466, false)).thenReturn("EPSG 31466: DHDN / Gauss-Krüger Zone 2");
        when(sysListMapper.getValueFromListId(100, 31467, false)).thenReturn("EPSG 31467: DHDN / Gauss-Krüger Zone 3");
        when(sysListMapper.getValueFromListId(100, 31468, false)).thenReturn("EPSG 31468: DHDN / Gauss-Krüger Zone 4");
        when(sysListMapper.getValueFromListId(100, 31469, false)).thenReturn("EPSG 31469: DHDN / Gauss-Krüger Zone 5");
        when(sysListMapper.getValueFromListId(100, 32631, false)).thenReturn("EPSG 32631: WGS 84 / UTM Zone 31N");
        when(sysListMapper.getValueFromListId(100, 32632, false)).thenReturn("EPSG 32632: WGS 84 / UTM Zone 32N");
        when(sysListMapper.getValueFromListId(100, 32633, false)).thenReturn("EPSG 32633: WGS 84 / UTM Zone 33N");
        when(sysListMapper.getValueFromListId(100, 9000001, false)).thenReturn("DE_42/83 / GK_3");
        when(sysListMapper.getValueFromListId(100, 9000002, false)).thenReturn("DE_DHDN / GK_3");
        when(sysListMapper.getValueFromListId(100, 9000003, false)).thenReturn("DE_ETRS89 / UTM");
        when(sysListMapper.getValueFromListId(100, 9000005, false)).thenReturn("DE_PD/83 / GK_3");
        when(sysListMapper.getValueFromListId(100, 9000006, false)).thenReturn("DE_RD/83 / GK_3");
        when(sysListMapper.getValueFromListId(100, 9000007, false)).thenReturn("DE_DHDN / GK_3_RDN");
        when(sysListMapper.getValueFromListId(100, 9000008, false)).thenReturn("DE_DHDN / GK_3_RP101");
        when(sysListMapper.getValueFromListId(100, 9000009, false)).thenReturn("DE_DHDN / GK_3_RP180");
        when(sysListMapper.getValueFromListId(100, 9000010, false)).thenReturn("DE_DHDN / GK_3_NW177");
        when(sysListMapper.getValueFromListId(100, 9000011, false)).thenReturn("DE_DHDN / GK_3_HE100");
        when(sysListMapper.getValueFromListId(100, 9000012, false)).thenReturn("DE_DHDN / GK_3_BW100");
        when(sysListMapper.getValueFromListId(100, 9000013, false)).thenReturn("DE_PD/83 / GK_9-15, Bezug 12. Meridian (BY)");
        
        when(sysListMapper.getValueFromListId(2000, 5066, false)).thenReturn("Verweis zu Dienst");
        
        when(sysListMapper.getKeyFromListId(6000, "conformant")).thenReturn(1);
    }

    /**
     * Test method for {@link de.ingrid.mdek.dwr.services.GetCapabilitiesService#getCapabilitiesCSW(org.w3c.dom.Document)}.
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    @Test
    public void testGetCapabilitiesCSW() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_CSW.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("CSW"));
        assertThat(result.getTitle(), is("con terra GmbH test catalogue Server"));
        assertThat(result.getFees(), is("NONE"));
        assertThat(result.getAccessConstraints().get(0), is("Basic authentication (RFC 2617) is required for all data manipulation requests"));
        assertThat(result.getOnlineResources().get(0).getUrl(), is("mailto:voges@conterra.de"));
        assertThat(result.getResourceLocators().get(0).getUrl(), is("http://www.wemove.com/my-resource-locator"));
        assertThat(result.getResourceLocators().get(0).getRelationType(), is(5066));
        assertThat(result.getResourceLocators().get(0).getRelationTypeName(), is("Verweis zu Dienst"));
        assertThat(result.getDataServiceType(), is(1));
        String[] expected = { "CS-W", "ISO19119", "ISO19115", "con terra", "Catalogue Service", "metadata", "Orthoimagery", "Orto immagini"};
        assertThat(result.getKeywords(), hasItems(expected));
        assertThat(result.getTimeReferences().get(0).getDate(), is((new SimpleDateFormat("yyyy-MM-dd")).parse("2009-07-02")));
        assertThat(result.getTimeReferences().get(0).getType(), is(1));
        assertThat(result.getTimeReferences().get(1).getDate(), is((new SimpleDateFormat("yyyy-MM-dd")).parse("2012-10-22")));
        assertThat(result.getTimeReferences().get(1).getType(), is(2));
        assertThat(result.getTimeReferences().get(2).getDate(), is((new SimpleDateFormat("yyyy-MM-dd")).parse("2013-11-26")));
        assertThat(result.getTimeReferences().get(2).getType(), is(3));
        
        assertThat(result.getTimeSpans().get(0).getFrom(), is((new SimpleDateFormat("yyyy-MM-dd")).parse("2009-08-20")));
        assertThat(result.getTimeSpans().get(0).getTo(), is((new SimpleDateFormat("yyyy-MM-dd")).parse("2010-04-29")));
        
        assertThat(result.getConformities().get(0).getLevel(), is(1));
        assertThat(result.getConformities().get(0).getSpecification(), is("COMMISSION REGULATION (EU) No 1089/2010 of 23 November 2010 implementing Directive 2007/2/EC of the European Parliament and of the Council as regards interoperability of spatial data sets and services"));
        assertThat(result.getConformities().get(1).getLevel(), is(1));
        assertThat(result.getConformities().get(1).getSpecification(), is("REGOLAMENTO (UE) N. 1089/2010 DELLA COMMISSIONE del 23 novembre 2010 recante attuazione della direttiva 2007/2/CE del Parlamento europeo e del Consiglio per quanto riguarda l'interoperabilità dei set di dati territoriali e dei servizi di dati territoriali"));
        
        assertThat(result.getAddress().getFirstname(), is("Uwe"));
        assertThat(result.getAddress().getLastname(), is("Voges"));
        assertThat(result.getAddress().getStreet(), is("Marting-Luther-King-Weg 24"));
        assertThat(result.getAddress().getCity(), is("Münster"));
        assertThat(result.getAddress().getPostcode(), is("48165"));
        assertThat(result.getAddress().getCountry(), is("Germany"));
        assertThat(result.getAddress().getState(), is("NRW"));
        assertThat(result.getAddress().getPhone(), is("+49-251-7474-402"));
        assertThat(result.getAddress().getEmail(), is("voges@conterra.de"));
        
        // operations
        assertThat(result.getOperations().size(), is(5));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("http://www.conterra.de/catalog?"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("DescribeRecord"));
        assertThat(result.getOperations().get(2).getName(), is("GetRecords"));
        assertThat(result.getOperations().get(3).getName(), is("GetRecordById"));
        assertThat(result.getOperations().get(4).getName(), is("Harvest"));
        
        
    }
    
    @Test
    public void testGetCapabilitiesCSWKnownAddress() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_CSW_knownAddress.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getAddress().getUuid(), is("2B83F58E-60C2-11D6-884A-0000F4ABB4D8"));
        
    }
    
    @Test
    public void testGetCapabilitiesWMS111() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_WMS_111.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("WMS"));
        assertThat(result.getDataServiceType(), is(2));
        assertThat(result.getVersions().get(0), is("1.1.1"));
        assertThat(result.getTitle(), is("Acme Corp. Map Server"));
        assertThat(result.getFees(), is("none"));
        assertThat(result.getAccessConstraints().get(0), is("none"));
        // TODO: ? assertThat(result.getOnlineResource(), is("mailto:voges@conterra.de"));
        //TODO: assertThat(result.getResourceLocator(), is("???"));
        String[] expected = { "bird", "roadrunner", "ambush", "road", "transportation", "atlas", "river", "canal", "waterway" };
        assertThat(result.getKeywords(), hasItems(expected));
        
        assertThat(result.getCoupledResources().get(0).getUuid(), is("123456"));
        assertThat(result.getCoupledResources().get(1).getUuid(), is("78910"));
        
        assertThat(result.getSpatialReferenceSystems().get(0).getId(), is(-1));
        assertThat(result.getSpatialReferenceSystems().get(0).getName(), is("EPSG:26986"));
        
        assertThat(result.getAddress().getFirstname(), is("Jeff"));
        assertThat(result.getAddress().getLastname(), is("deLaBeaujardiere"));
        assertThat(result.getAddress().getStreet(), is("NASA Goddard Space Flight Center, Code 933"));
        assertThat(result.getAddress().getCity(), is("Greenbelt"));
        assertThat(result.getAddress().getPostcode(), is("20771"));
        assertThat(result.getAddress().getCountry(), is("USA"));
        assertThat(result.getAddress().getState(), is("MD"));
        assertThat(result.getAddress().getPhone(), is("+1 301 286-1569"));
        assertThat(result.getAddress().getEmail(), is("delabeau@iniki.gsfc.nasa.gov"));
        
        assertThat(result.getOperations().size(), is(3));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("http://hostname:port/path"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("GetMap"));
        assertThat(result.getOperations().get(2).getName(), is("GetFeatureInfo"));
    }
    
    @Test
    public void testGetCapabilitiesWMS130() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_WMS_130.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("WMS"));
        assertThat(result.getVersions().get(0), is("1.3.0"));
        assertThat(result.getTitle(), is("Acme Corp. Map Server"));
        assertThat(result.getFees(), is("none"));
        assertThat(result.getAccessConstraints().get(0), is("none"));
        assertThat(result.getOnlineResources().get(0).getUrl(), is("http://hostname/my-online-resource"));

        assertThat(result.getResourceLocators().get(0).getUrl(), is("http://ogc.beta.agiv.be/ogc/wms/vrbgINSP?"));
        assertThat(result.getResourceLocators().get(0).getRelationType(), is(5066));
        assertThat(result.getDataServiceType(), is(2));
        String[] expected = { "bird", "roadrunner", "ambush", "road", "transportation", "atlas", "river", "canal", "waterway", "Administratieveeenheden", "boundaries" };
        assertThat(result.getKeywords(), hasItems(expected));
        
        assertThat(result.getCoupledResources().get(0).getRef1ObjectIdentifier(), is("123456"));
        assertThat(result.getCoupledResources().get(0).getSpatialRefLocationTable().size(), is(1));
        assertThat(result.getCoupledResources().get(0).getSpatialRefLocationTable().get(0).getLatitude1(), is(41.75));
        assertThat(result.getCoupledResources().get(0).getSpatialRefLocationTable().get(0).getLongitude1(), is(-71.63));
        assertThat(result.getCoupledResources().get(0).getSpatialRefLocationTable().get(0).getLatitude2(), is(42.90));
        assertThat(result.getCoupledResources().get(0).getSpatialRefLocationTable().get(0).getLongitude2(), is(-70.78));
        assertThat(result.getCoupledResources().get(1).getRef1ObjectIdentifier(), is("78910"));
        assertThat(result.getCoupledResources().get(1).getThesaurusTermsTable().size(), is(3));
        assertThat(result.getCoupledResources().get(1).getThesaurusTermsTable().get( 0 ).getTitle(), is("road"));
        assertThat(result.getCoupledResources().get(1).getThesaurusTermsTable().get( 1 ).getTitle(), is("transportation"));
        assertThat(result.getCoupledResources().get(1).getThesaurusTermsTable().get( 2 ).getTitle(), is("atlas"));
        
        assertThat(result.getSpatialReferenceSystems().get(0).getId(), is(-1));
        assertThat(result.getSpatialReferenceSystems().get(0).getName(), is("CRS:84"));
        assertThat(result.getSpatialReferenceSystems().get(1).getId(), is(4230));
        assertThat(result.getSpatialReferenceSystems().get(1).getName(), is("EPSG 4230: ED50 / geographisch"));
        
        assertThat(result.getTimeReferences().get(0).getDate(), is((new SimpleDateFormat("yyyy-MM-dd")).parse("2003-01-01")));
        assertThat(result.getTimeReferences().get(0).getType(), is(1));
        assertThat(result.getTimeReferences().get(1).getDate(), is((new SimpleDateFormat("yyyy-MM-dd")).parse("2003-05-10")));
        assertThat(result.getTimeReferences().get(1).getType(), is(2));
//        
        assertThat(result.getConformities().get(0).getLevel(), is(3));
        assertThat(result.getConformities().get(0).getSpecification(), is("Verordening (EG) nr. 976/2009 van de Commissie van 19 oktober 2009 tot uitvoering van Richtlijn 2007/2/EG van het Europees Parlement en de Raad wat betreft de netwerkdiensten"));
        
        assertThat(result.getAddress().getFirstname(), is("Jeff"));
        assertThat(result.getAddress().getLastname(), is("Smith"));
        assertThat(result.getAddress().getStreet(), is("NASA Goddard Space Flight Center"));
        assertThat(result.getAddress().getCity(), is("Greenbelt"));
        assertThat(result.getAddress().getPostcode(), is("20771"));
        assertThat(result.getAddress().getCountry(), is("USA"));
        assertThat(result.getAddress().getState(), is("MD"));
        assertThat(result.getAddress().getPhone(), is("+1 301 555-1212"));
        assertThat(result.getAddress().getEmail(), is("user@host.com"));
        
        assertThat(result.getOperations().size(), is(3));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("http://hostname/path?"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("GetMap"));
        assertThat(result.getOperations().get(2).getName(), is("GetFeatureInfo"));
    }
    
    @Test
    public void testGetCapabilitiesWMS130Niedersachsen() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_WMS_130_niedersachsen.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("WMS"));
        assertThat(result.getDataServiceType(), is(2));
        assertThat(result.getVersions().get(0), is("1.3.0"));
        assertThat(result.getTitle(), is("Naturschutz"));
        assertThat(result.getFees(), is("none"));
        assertThat(result.getAccessConstraints().get(0), is("none"));
        
        // Keywords
        String[] expected = { "UNB,Naturrämliche Regionen, Unterregionen, Landesgrenze mit 12-sm Zone,Minutengitter, TK-Quadtranten, Feldblöcke, Biosphärenreservat, Nationalparke, Naturschutzgebiete, Landschaftsschtugebiete, Naturparke, Naturdenkmale, FFH-Gebiete, EU-Vogelschutzgebiete, Flächen zur Bestandserfassung, Landesweite Biotopkartierung, Für die Fauna wertvolle Bereiche, Förderkulissen des Kooperationsprogrammes Naturschutz, Moorschutzprogramme, Fließgewässerschtuzsystem, Gebiete mit gesamtstaatlich repräsentativer Bedeutung" };
        assertThat(result.getKeywords(), hasItems(expected));
        
        assertThat(result.getSpatialReferenceSystems().get(0).getId(), is(-1));//84));
        assertThat(result.getSpatialReferenceSystems().get(0).getName(), is("CRS:84"));
        assertThat(result.getSpatialReferenceSystems().get(1).getId(), is(4326));
        assertThat(result.getSpatialReferenceSystems().get(2).getId(), is(-1));//4647));
        assertThat(result.getSpatialReferenceSystems().get(2).getName(), is("EPSG:4647"));
        assertThat(result.getSpatialReferenceSystems().get(3).getId(), is(25832));
        assertThat(result.getSpatialReferenceSystems().get(4).getId(), is(31466));
        assertThat(result.getSpatialReferenceSystems().get(5).getId(), is(31467));
        assertThat(result.getSpatialReferenceSystems().get(6).getId(), is(31468));
        assertThat(result.getSpatialReferenceSystems().get(7).getId(), is(4258));
        assertThat(result.getSpatialReferenceSystems().get(8).getId(), is(-1));//3034));
        assertThat(result.getSpatialReferenceSystems().get(8).getName(), is("EPSG:3034"));
        assertThat(result.getSpatialReferenceSystems().get(9).getId(), is(-1));//3035));
        assertThat(result.getSpatialReferenceSystems().get(9).getName(), is("EPSG:3035"));
        assertThat(result.getSpatialReferenceSystems().get(10).getId(), is(-1));//3044));
        assertThat(result.getSpatialReferenceSystems().get(10).getName(), is("EPSG:3044"));
        
        // check for a bounding box that contains all layers
        // these coordinates did not need a transformation
        assertThat(result.getBoundingBoxes().size(), is(1));
        assertThat(result.getBoundingBoxes().get(0).getName(), is("Raumbezug von: Naturschutz"));
        assertThat(result.getBoundingBoxes().get(0).getLatitude1(), is(51.2575719691118));
        assertThat(result.getBoundingBoxes().get(0).getLongitude1(), is(6.32836880550564));
        assertThat(result.getBoundingBoxes().get(0).getLatitude2(), is(54.0294452991844));
        assertThat(result.getBoundingBoxes().get(0).getLongitude2(), is(11.6580524828798));
        
        assertThat(result.getAddress().getFirstname(), is("Dorothea"));
        assertThat(result.getAddress().getLastname(), is("Pielke"));
        assertThat(result.getAddress().getStreet(), is("Archivstraße 2"));
        assertThat(result.getAddress().getCity(), is("Hannover"));
        assertThat(result.getAddress().getPostcode(), is("30173"));
        assertThat(result.getAddress().getCountry(), is("DE"));
        assertThat(result.getAddress().getState(), is("Niedersachsen"));
        assertThat(result.getAddress().getPhone(), is("none"));
        assertThat(result.getAddress().getEmail(), is("dorothea.pielke@mu.niedersachsen.de"));
        
        assertThat(result.getOperations().size(), is(3));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("http://www.umweltkarten-niedersachsen.de/arcgis/services/Natur_wms/MapServer/WMSServer?"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("GetMap"));
        assertThat(result.getOperations().get(2).getName(), is("GetFeatureInfo"));
    }

    @Test
    public void testGetCapabilitiesWFS20() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_WFS_20.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("WFS"));
        assertThat(result.getDataServiceType(), is(3));
        assertThat(result.getVersions().get(0), is("2.0.0"));
        assertThat(result.getVersions().get(1), is("1.1.0"));
        assertThat(result.getVersions().get(2), is("1.0.0"));
        assertThat(result.getTitle(), is("OGC Member WFS"));
        assertThat(result.getFees(), is("NONE"));
        assertThat(result.getAccessConstraints().get(0), is("NONE"));
        
        // Online Resource
        assertThat(result.getOnlineResources().get(0).getUrl(), is("http://www.BlueOx.org/contactUs"));
        
        // Keywords
        String[] expected = { "FGDC", "NSDI", "Framework Data Layer", "BlueOx" };
        assertThat(result.getKeywords(), hasItems(expected));
        
        // check address
        assertThat(result.getAddress().getFirstname(), is("Paul"));
        assertThat(result.getAddress().getLastname(), is("Bunyon"));
        assertThat(result.getAddress().getStreet(), is("North Country"));
        assertThat(result.getAddress().getCity(), is("Small Town"));
        assertThat(result.getAddress().getPostcode(), is("12345"));
        assertThat(result.getAddress().getCountry(), is("USA"));
        assertThat(result.getAddress().getState(), is("Rural County"));
        assertThat(result.getAddress().getPhone(), is("1.800.BIG.WOOD"));
        assertThat(result.getAddress().getEmail(), is("Paul.Bunyon@BlueOx.org"));
        
        assertThat(result.getOperations().size(), is(3));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("http://www.BlueOx.org/wfs/wfs.cgi?"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("DescribeFeatureType"));
        assertThat(result.getOperations().get(2).getName(), is("GetFeature"));
        //assertThat(result.getOperations().get(3).getName(), is("LockFeature"));
       // assertThat(result.getOperations().get(4).getName(), is("Transaction"));
    }
    
    @Test
    public void testGetCapabilitiesWFS20All() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_WFS_20_all.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("WFS"));
        assertThat(result.getDataServiceType(), is(3));
        assertThat(result.getVersions().get(0), is("2.0.0"));
        assertThat(result.getVersions().get(1), is("1.1.0"));
        assertThat(result.getVersions().get(2), is("1.0.0"));
        assertThat(result.getTitle(), is("OGC Member WFS"));
        assertThat(result.getFees(), is("NONE"));
        assertThat(result.getAccessConstraints().get(0), is("NONE"));
        
        assertThat(result.getResourceLocators().get(0).getUrl(), is("http://my-download-resource-locator"));
        assertThat(result.getResourceLocators().get(0).getRelationType(), is(5066));
        
        // Online Resource
        assertThat(result.getOnlineResources().get(0).getUrl(), is("http://www.BlueOx.org/contactUs"));
        
        // Keywords
        String[] expected = { "FGDC", "NSDI", "Framework Data Layer", "BlueOx", "forest", "north", "woods", "arborial", "diversity", "lakes", "boundaries", "water", "hydro", "downloadMe" };
        assertThat(result.getKeywords(), hasItems(expected));
        
        assertThat(result.getTimeReferences().get(0).getDate(), is((new SimpleDateFormat("yyyy-MM-dd")).parse("2007-11-13")));
        assertThat(result.getTimeReferences().get(0).getType(), is(1));

        assertThat(result.getConformities().get(0).getLevel(), is(3));
        assertThat(result.getConformities().get(0).getSpecification(), is("Please enter a title"));
        
        // check address
        assertThat(result.getAddress().getFirstname(), is("Paul"));
        assertThat(result.getAddress().getLastname(), is("Bunyon"));
        assertThat(result.getAddress().getStreet(), is("North Country"));
        assertThat(result.getAddress().getCity(), is("Small Town"));
        assertThat(result.getAddress().getPostcode(), is("12345"));
        assertThat(result.getAddress().getCountry(), is("USA"));
        assertThat(result.getAddress().getState(), is("Rural County"));
        assertThat(result.getAddress().getPhone(), is("1.800.BIG.WOOD"));
        assertThat(result.getAddress().getEmail(), is("Paul.Bunyon@BlueOx.org"));
        
        // operations
        assertThat(result.getOperations().size(), is(5));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("http://www.BlueOx.org/wfs/wfs.cgi?"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("DescribeFeatureType"));
        assertThat(result.getOperations().get(2).getName(), is("GetFeature"));
        assertThat(result.getOperations().get(3).getName(), is("LockFeature"));
        assertThat(result.getOperations().get(4).getName(), is("Transaction"));

    }
    
    @Test
    public void testGetCapabilitiesWCS() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_WCS.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("WCS"));
        assertThat(result.getDataServiceType(), is(6));
        assertThat(result.getVersions().get(0), is("1.0.0"));
        assertThat(result.getTitle(), is("deegree WCS"));
        assertThat(result.getDescription(), is("deegree WCS being OGC WCS 1.0.0 reference implementation"));
        
        assertThat(result.getFees(), is("NONE"));
        assertThat(result.getAccessConstraints().get(0), is("NONE"));
        assertThat(result.getAccessConstraints().get(1), is("SOME"));
        
        // Online Resource
        assertThat(result.getOnlineResources().get(0).getUrl(), is("http://www.geodatenzentrum.de"));
        
        // Keywords
        String[] expected = { "deegree", "DGM", "DGM25" };
        assertThat(result.getKeywords(), hasItems(expected));
        
        // check address
        assertThat(result.getAddress().getFirstname(), is("Manfred"));
        assertThat(result.getAddress().getLastname(), is("Endrullis"));
        assertThat(result.getAddress().getStreet(), is(nullValue()));
        assertThat(result.getAddress().getCity(), is("Potsdam"));
        assertThat(result.getAddress().getPostcode(), is("14777"));
        assertThat(result.getAddress().getCountry(), is("DE"));
        assertThat(result.getAddress().getState(), is("Brandenburg"));
        assertThat(result.getAddress().getPhone(), is("+49 341 5634369"));
        assertThat(result.getAddress().getEmail(), is(nullValue()));
        
        assertThat(result.getOperations().size(), is(3));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("http://localhost:8080/wpvs/services?"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("DescribeCoverage"));
        assertThat(result.getOperations().get(2).getName(), is("GetCoverage"));
    }
    
    @Test
    public void testGetCapabilitiesWCS110() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_WCS_112.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("WCS"));
        assertThat(result.getDataServiceType(), is(6));
        assertThat(result.getVersions().get(0), is("1.0.0"));
        assertThat(result.getVersions().get(1), is("1.1.2"));
        assertThat(result.getTitle(), is("CubeWerx Demonstation WCS"));
        assertThat(result.getDescription(), is("A demonstration server used to illustrate CubeWerx's compilance with the Web Coverage Service 1.1.0 implementation specification"));
        
        assertThat(result.getFees(), is("NONE"));
        assertThat(result.getAccessConstraints().get(0), is("NONE"));
        //TODO: assertThat(result.getAccessConstraints(), is("SOME"));
        
        // Online Resource
        assertThat(result.getOnlineResources().get(0).getUrl(), is("http://www.cubewerx.com/~pvretano"));
        
        // Keywords
        String[] expected = { "Web Coverage Service", "06-083", "CubeWerx", "GeoTIFF", "Imagery" };
        assertThat(result.getKeywords(), hasItems(expected));
        
        // check address
        assertThat(result.getAddress().getFirstname(), is("A."));
        assertThat(result.getAddress().getLastname(), is("Vretanos"));
        assertThat(result.getAddress().getStreet(), is("15 rue Gamelin"));
        assertThat(result.getAddress().getCity(), is("Gatineau"));
        assertThat(result.getAddress().getPostcode(), is("J8Y 6N5"));
        assertThat(result.getAddress().getCountry(), is("Canada"));
        assertThat(result.getAddress().getState(), is("Quebec"));
        assertThat(result.getAddress().getPhone(), is("123-456-7890"));
        assertThat(result.getAddress().getEmail(), is("pvretano[at]cubewerx[dot]com"));
        
        assertThat(result.getOperations().size(), is(3));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("http://demo.cubewerx.com/demo/cubeserv/cubeserv.cgi?service=WMS&"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("DescribeCoverage"));
        assertThat(result.getOperations().get(2).getName(), is("GetCoverage"));
    }

    @Test
    public void testGetCapabilitiesWCTS() throws Exception {
        Document doc = getDocumentFromFile(capDir + "capabilities_WCTS.xml");
        
        CapabilitiesBean result = service.getCapabilitiesData(doc);
        
        assertThat(result, notNullValue());
        assertThat(result.getServiceType(), is("WCTS"));
        assertThat(result.getDataServiceType(), is(4));
        assertThat(result.getVersions().get(0), is("0.0.0"));
        assertThat(result.getTitle(), is("Web Coordinate Transformation Service"));
        assertThat(result.getDescription(), is("Network service for transforming coordinates from one CRS to another"));
        
        assertThat(result.getFees(), is("NONE"));
        assertThat(result.getAccessConstraints().get(0), is("NONE"));
        //TODO: assertThat(result.getAccessConstraints(), is("SOME"));
        
        // Online Resource
        //assertThat(result.getOnlineResources().get(0).getUrl(), is("http://www.cubewerx.com/~pvretano"));
        
        // Keywords
        String[] expected = { "Coordinate Reference System", "transformation", "conversion", "coordinate operation" };
        assertThat(result.getKeywords(), hasItems(expected));
        
        // check address
        assertThat(result.getAddress().getFirstname(), is("Andreas"));
        assertThat(result.getAddress().getLastname(), is("Poth"));
        assertThat(result.getAddress().getStreet(), is("Meckenheimer Allee 176"));
        assertThat(result.getAddress().getCity(), is("Bonn"));
        assertThat(result.getAddress().getPostcode(), is("53115"));
        assertThat(result.getAddress().getCountry(), is("Germany"));
        assertThat(result.getAddress().getState(), is("NRW"));
        assertThat(result.getAddress().getPhone(), is("++49 228 732838"));
        assertThat(result.getAddress().getEmail(), is("poth@lat-lon.de"));
        
        assertThat(result.getOperations().size(), is(4));
        assertThat(result.getOperations().get(0).getAddressList().get(0), is("www.lat-lon.de/transform"));
        assertThat(result.getOperations().get(0).getName(), is("GetCapabilities"));
        assertThat(result.getOperations().get(1).getName(), is("Transform"));
        assertThat(result.getOperations().get(2).getName(), is("IsTransformable"));
        //assertThat(result.getOperations().get(3).getName(), is("GetTransformation"));
        assertThat(result.getOperations().get(3).getName(), is("DescribeTransformation"));
        //assertThat(result.getOperations().get(5).getName(), is("DescribeCRS"));
        //assertThat(result.getOperations().get(6).getName(), is("DescribeMethod"));
    }
    
    
    private Document getDocumentFromFile(String filename) throws FileNotFoundException, ParserConfigurationException, SAXException,
            IOException {
        InputStream reader = this.getClass().getClassLoader().getResourceAsStream(filename);
        InputSource inputSource = new InputSource(reader);

        // Build a document from the xml response
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // nameSpaceAware is false by default. Otherwise we would have to query for the correct namespace for every evaluation
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputSource);
        return doc;
    }

}
