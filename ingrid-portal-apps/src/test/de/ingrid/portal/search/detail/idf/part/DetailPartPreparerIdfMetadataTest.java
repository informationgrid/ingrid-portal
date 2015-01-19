/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.search.detail.idf.part;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.velocity.context.Context;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

public class DetailPartPreparerIdfMetadataTest {

    private DetailPartPreparerIdfMetadata preparer;

    @Mock RenderRequest request;
    @Mock RenderResponse response;
    @Mock Context context;
    @Mock IngridResourceBundle resourceBundle;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        when(resourceBundle.getString( Matchers.anyString())).thenReturn( "<mockup>" );
        when(context.get( Matchers.anyString() )).thenReturn( resourceBundle );
        
        preparer = new DetailPartPreparerIdfMetadata();

        
        // add IDF Namespace-context!
        XPathUtils.getXPathInstance(new IDFNamespaceContext());
        
    }

    @Test
    public void testGetMapImageFromMap() throws Exception {
        prepareDoc( "idf_map.xml" );
        HashMap<String, Object> mapImage = preparer.getMapImage();
        List elements = (List) mapImage.get( "elements" );
        HashMap element = (HashMap) elements.get( 0 );
        assertEquals( true, element.get( "isMapLink" ));
        assertEquals( UtilsVelocity.urlencode( "http://www.umweltkarten-niedersachsen.de/arcgis/services/Natur_wms/MapServer/WMSServer?REQUEST=GetCapabilities&SERVICE=WMS" ) + "&ID=" + UtilsVelocity.urlencode("http://portalu.de/igc_test#selbst-erzeugte-id"), element.get( "href" ));
    }
    
    @Test
    public void testGetMapImageFromService() throws Exception {
        prepareDoc( "idf_service.xml" );
        HashMap<String, Object> mapImage = preparer.getMapImage();
        List elements = (List) mapImage.get( "elements" );
        HashMap element = (HashMap) elements.get( 0 );
        assertEquals( true, element.get( "isMapLink" ));
        assertEquals( UtilsVelocity.urlencode( "http://www.umweltkarten-niedersachsen.de/arcgis/services/Natur_wms/MapServer/WMSServer?REQUEST=GetCapabilities&SERVICE=WMS" ), element.get( "href" ));
    }
    
    @Test
    public void testGetReference() throws Exception {
        prepareDoc( "idf_service.xml" );
        ArrayList<HashMap<String, Object>> list = preparer.getReference( "./idf:crossReference", true );
        HashMap<String, Object> map = list.get( 0 );
        
        assertEquals( "http://www.umweltkarten-niedersachsen.de/arcgis/services/Natur_wms/MapServer/WMSServer?REQUEST=GetCapabilities&SERVICE=WMS&ID=http://portalu.de/igc_test#selbst-erzeugte-id", map.get( "mapLink" ));
    }
    
    
    private Document getDocumentFromFile( String filename ) throws FileNotFoundException, ParserConfigurationException,
            SAXException, IOException {
        InputStream reader = this.getClass().getClassLoader().getResourceAsStream( filename );
        InputSource inputSource = new InputSource( reader );

        // Build a document from the xml response
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // nameSpaceAware is false by default. Otherwise we would have to query
        // for the correct namespace for every evaluation
        factory.setNamespaceAware( true );
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( inputSource );
        return doc;
    }
    
    private void prepareDoc( String file ) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        Document idfDoc = getDocumentFromFile( file );
        Element root = idfDoc.getDocumentElement();
        
        preparer.init( root, "/test-iplug", request, response, context );
    }

}
