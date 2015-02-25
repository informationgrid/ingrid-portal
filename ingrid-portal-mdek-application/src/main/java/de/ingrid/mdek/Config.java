/*
 * **************************************************-
 * ingrid-base-webapp
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.mdek;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.weta.components.communication.configuration.XPathService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.EnvironmentVariableValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;
import com.tngtech.configbuilder.annotation.valueextractor.SystemPropertyValue;

@PropertiesFiles({ "mdek" })
@PropertyLocations(fromClassLoader = true)
@LoadingOrder({CommandLineValue.class, SystemPropertyValue.class, PropertyValue.class, EnvironmentVariableValue.class, DefaultValue.class})
public class Config {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog( Config.class );

    public static final int DEFAULT_TIMEOUT = 10;

    public static final int DEFAULT_MAXIMUM_SIZE = 3145728;

    public static final int DEFAULT_THREAD_COUNT = 100;

    /**
     * COMMUNICATION - SETTINGS
     */
    @SystemPropertyValue("communication")
    @PropertyValue("communication.properties")
    @DefaultValue("communication.xml")
    public String communicationLocation;

    @PropertyValue("communication.server.name")
    @DefaultValue("")
    public String ibusName;
    
    @PropertyValue("communication.server.port")
    @DefaultValue("")
    public String ibusPort;

    /**
     * CODELIST - REPOSITORY
     */
    @PropertyValue("codelist.requestUrl")
    @DefaultValue("http://localhost:8089/rest/getCodelists")
    public String codelistRequestUrl;

    @PropertyValue("codelist.username")
    public String codelistUsername;

    @PropertyValue("codelist.password")
    public String codelistPassword;

    @PropertyValue("codelist.communicationType")
    @DefaultValue("http")
    public String codelistCommunicationType;

    @PropertyValue("codelist.defaultPersistency")
    @DefaultValue("0")
    public int codelistDefaultPersistency;

    @PropertyValue("installation.standalone")
    @DefaultValue("false")
    public boolean noPortal;
    

    public void initialize() throws IOException {
        System.setProperty( "spring.profiles.active", "http" );
        writeCommunication();
    }

    public boolean writeCommunication() {
        File communicationFile = new File( this.communicationLocation );
        if (ibusName.isEmpty() || ibusPort.isEmpty()) {
            // do not remove communication file if no
            if (communicationFile.exists()) {
                communicationFile.delete();
            }
            return true;
        }

        try {
            final XPathService communication = getCommunicationTemplate();
            Integer id = 0;

            communication.removeNode( "/communication/server", id );
            
            // create default nodes and attributes if server tag does not exist

            communication.addNode( "/communication", "server", id );
            communication.addAttribute( "/communication/server", "name", ibusName, id );
            
            communication.addNode( "/communication/server", "socket", id );
            communication.addNode( "/communication/server", "messages", id );
            
            communication.addAttribute( "/communication/server/messages", "maximumSize", "" + DEFAULT_MAXIMUM_SIZE, id );
            communication.addAttribute( "/communication/server/messages", "threadCount", "" + DEFAULT_THREAD_COUNT, id );

            communication.addAttribute( "/communication/server/socket", "port", "" + ibusPort, id );
            communication.addAttribute( "/communication/server/socket", "timeout", "" + DEFAULT_TIMEOUT, id );

            communication.addNode( "/communication", "messages", id );
            communication.addAttribute( "/communication/messages", "handleTimeout", "30", id );
            communication.addAttribute( "/communication/messages", "queueSize", "2000", id );

            communication.store( communicationFile );

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    private final XPathService getCommunicationTemplate() throws Exception {

        // open template xml or communication file
        final XPathService communication = new XPathService();
        final InputStream inputStream = Config.class.getResourceAsStream( "/communication-template.xml" );
        communication.registerDocument( inputStream );

        return communication;
    }
    
}
