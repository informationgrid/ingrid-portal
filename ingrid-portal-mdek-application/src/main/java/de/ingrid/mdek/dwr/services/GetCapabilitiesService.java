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
package de.ingrid.mdek.dwr.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.dwr.services.capabilities.CapabilitiesParserFactory;
import de.ingrid.mdek.dwr.services.capabilities.ICapabilitiesParser;

public class GetCapabilitiesService {

    private final static Logger log = Logger.getLogger( GetCapabilitiesService.class );

    private static String ERROR_GETCAP_INVALID_URL = "ERROR_GETCAP_INVALID_URL";
    private static String ERROR_GETCAP = "ERROR_GETCAP_ERROR";

    @Autowired
    private SysListCache sysListMapper;

    // Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {}

    public CapabilitiesBean getCapabilities(String urlStr) {

        try {
            URL url = new URL( urlStr );
            // get the content in UTF-8 format, to avoid "MalformedByteSequenceException: Invalid byte 1 of 1-byte UTF-8 sequence"
            InputStream input = checkForUtf8BOMAndDiscardIfAny( url.openStream() );
            Reader reader = new InputStreamReader( input, "UTF-8" );
            InputSource inputSource = new InputSource( reader );

            // Build a document from the xml response
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // nameSpaceAware is false by default. Otherwise we would have to
            // query for the correct namespace for every evaluation
            factory.setNamespaceAware( true );
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse( inputSource );

            return getCapabilitiesData( doc );

        } catch (MalformedURLException e) {
            log.debug( "URL is malformed: " + urlStr, e );
            throw new RuntimeException( ERROR_GETCAP_INVALID_URL, e );

        } catch (IOException e) {
            log.debug( "IO-Exception occured with url: " + urlStr, e );
            throw new RuntimeException( ERROR_GETCAP, e );

        } catch (Exception e) {
            log.debug( "A general exception occured with url: " + urlStr, e );
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
        if (pushbackInputStream.read( bom ) != -1) {
            if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
                pushbackInputStream.unread( bom );
            }
        }
        return pushbackInputStream;
    }

    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        ICapabilitiesParser capDoc = CapabilitiesParserFactory.getDocument( doc, sysListMapper );
        return capDoc.getCapabilitiesData( doc );
    }

    public SysListCache getSysListMapper() {
        return sysListMapper;
    }

    public void setSysListMapper(SysListCache syslistCache) {
        this.sysListMapper = syslistCache;
    }

}