/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.dwr.services;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.beans.Record;
import de.ingrid.mdek.dwr.services.capabilities.CapabilitiesParserFactory;
import de.ingrid.mdek.dwr.services.capabilities.ICapabilitiesParser;
import de.ingrid.utils.xpath.XPathUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetCapabilitiesService {

    private static final Logger log = Logger.getLogger( GetCapabilitiesService.class );

    private static final String ERROR_GETCAP_INVALID_URL = "ERROR_GETCAP_INVALID_URL";
    private static final String ERROR_GETCAP = "ERROR_GETCAP_ERROR";

    @Autowired
    private SysListCache sysListMapper;

    @Autowired CatalogService catalogService;

    // Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {}

    public CapabilitiesBean getCapabilities(String urlStr) {

        try {
            Document doc = getDocumentFromUrl(urlStr, true);
            return getCapabilitiesData( doc );

        } catch (MalformedURLException e) {
            log.error( "URL is malformed: " + urlStr, e );
            throw new RuntimeException( ERROR_GETCAP_INVALID_URL, e );

        } catch (IOException e) {
            log.error( "IO-Exception occured with url: " + urlStr, e );
            throw new RuntimeException( ERROR_GETCAP, e );

        } catch (Exception e) {
            log.error( "A general exception occured with url: " + urlStr, e );
            throw new RuntimeException( ERROR_GETCAP, e );
        }
    }

    /**
     * If an input stream starts with a BOM then it has to be removed/read before(!)
     * we parse it for XML handling.
     *
     * @param inputStream is the stream with the content to check for the BOM
     * @return an input stream with the correct starting position for reading
     * @throws IOException
     */
    private static InputStream checkForUtf8BOMAndDiscardIfAny(InputStream inputStream) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream( new BufferedInputStream( inputStream ), 3 );
        byte[] bom = new byte[3];
        if (pushbackInputStream.read( bom ) != -1 && !(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
            pushbackInputStream.unread( bom );
        }
        return pushbackInputStream;
    }

    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        ICapabilitiesParser capDoc = CapabilitiesParserFactory.getDocument( doc, sysListMapper, catalogService );
        return capDoc.getCapabilitiesData( doc );
    }

    public Record getRecordById(String urlStr) {
        Record record = null;
        try {

            Document doc = getDocumentFromUrl(urlStr, false);
            record = new Record();
            XPathUtils xpath = new XPathUtils();
            String id = xpath.getString( doc, "//identificationInfo/MD_DataIdentification//identifier/MD_Identifier/code/CharacterString" );
            String title = xpath.getString( doc, "//identificationInfo/MD_DataIdentification//citation/CI_Citation/title/CharacterString" );
            String uuid = xpath.getString( doc, "//MD_Metadata/fileIdentifier/CharacterString" );

            NodeList resources = xpath.getNodeList( doc, "//MD_DigitalTransferOptions/onLine/CI_OnlineResource/function/CI_OnLineFunctionCode");
            for (int j = 0; j < resources.getLength(); j++) {
                String codeListValue = resources.item( j ).getAttributes().getNamedItem( "codeListValue" ).getNodeValue();
                if ("download".equals( codeListValue )) {
                    String link = xpath.getString( resources.item( j ), "../../linkage/URL" );
                    record.addDownloadData( link );
                }
            }

            record.setIdentifier( id );
            record.setTitle( title );
            record.setUuid( uuid );
        } catch (Exception ex) {
            log.error( "Problem getting record by ID: " + urlStr, ex );
        }

        return record;
    }

    private Document getDocumentFromUrl(String urlStr, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {
        URL url = new URL( urlStr );
        // get the content in UTF-8 format, to avoid "MalformedByteSequenceException: Invalid byte 1 of 1-byte UTF-8 sequence"
        InputStream input = checkForUtf8BOMAndDiscardIfAny( url.openStream() );
        Reader reader = new InputStreamReader( input, StandardCharsets.UTF_8 );
        InputSource inputSource = new InputSource( reader );
        // Build a document from the xml response
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // nameSpaceAware is false by default. Otherwise we would have to
        // query for the correct namespace for every evaluation
        factory.setNamespaceAware( namespaceAware );
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse( inputSource );
    }

    public SysListCache getSysListMapper() {
        return sysListMapper;
    }

    public void setSysListMapper(SysListCache syslistCache) {
        this.sysListMapper = syslistCache;
    }

}
