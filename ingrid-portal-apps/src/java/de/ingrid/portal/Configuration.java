/*
 * **************************************************-
 * ingrid-base-webapp
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.portal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.weta.components.communication.configuration.XPathService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformers;
import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.EnvironmentVariableValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;
import com.tngtech.configbuilder.annotation.valueextractor.SystemPropertyValue;

@PropertiesFiles({ "ingrid-portal-apps" })
@PropertyLocations(fromClassLoader = true)
@LoadingOrder({CommandLineValue.class, SystemPropertyValue.class, PropertyValue.class, EnvironmentVariableValue.class, DefaultValue.class})
public class Configuration {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog( Configuration.class );

    public class StringToCommunications extends TypeTransformer<String, List<Communication>> {

        @Override
        public List<Communication> transform(String input) {
            List<Communication> list = new ArrayList<Communication>();
            String[] split = input.split( "##" );
            for (String comm : split) {
                String[] communication = comm.split( "," );
                if (communication.length == 3) {
                    Communication commObject = new Communication();
                    commObject.url = communication[0];
                    commObject.ip = communication[1];
                    commObject.port = communication[2];
                    list.add( commObject );
                }
            }
            return list;
        }
    }

    /**
     * COMMUNICATION - SETTINGS
     */
    @SystemPropertyValue("communication")
    @PropertyValue("communication.location")
    @DefaultValue("communication.xml")
    public String communicationLocation;

    @PropertyValue("communication.clientName")
    @DefaultValue("/ingrid-group:ingrid-portal")
    public String communicationProxyUrl;

    @TypeTransformers(Configuration.StringToCommunications.class)
    @PropertyValue("communications.ibus")
    @DefaultValue("")
    public List<Communication> ibusses;
    
    @PropertyValue("communication.server.timeout")
    @DefaultValue("10")
    public int ibusTimeout;
    
    @PropertyValue("communication.server.maxMsgSize")
    @DefaultValue("10485760")
    public int ibusMaxMsgSize;
    
    @PropertyValue("communication.server.threadCount")
    @DefaultValue("100")
    public int ibusThreadCount;

 

    public void initialize() throws IOException {
        writeCommunication();
    }

    public boolean writeCommunication() {
        File communicationFile = new File( getClasspathDir(), this.communicationLocation );
        if (ibusses == null || ibusses.isEmpty()) {
            // do not remove communication file if no
            if (communicationFile.exists()) {
                communicationFile.delete();
            }
            return true;
        }

        try {
            final XPathService communication = getCommunicationTemplate();
            Integer id = 0;

            communication.setAttribute( "/communication/client", "name", this.communicationProxyUrl );

            communication.removeNode( "/communication/client/connections/server", id );
            
            // create default nodes and attributes if server tag does not exist
            for (Communication ibus : ibusses) {

                communication.addNode( "/communication/client/connections", "server" );
                communication.addNode( "/communication/client/connections/server", "socket", id );
                communication.addAttribute( "/communication/client/connections/server/socket", "timeout", "" + ibusTimeout, id );
                communication.addNode( "/communication/client/connections/server", "messages", id );
                communication.addAttribute( "/communication/client/connections/server/messages", "maximumSize", "" + ibusMaxMsgSize, id );
                communication.addAttribute( "/communication/client/connections/server/messages", "threadCount", "" + ibusThreadCount, id );

                communication.addAttribute( "/communication/client/connections/server", "name", ibus.url, id );
                communication.addAttribute( "/communication/client/connections/server/socket", "port", "" + ibus.port, id );
                communication.addAttribute( "/communication/client/connections/server/socket", "ip", ibus.ip, id );
                id++;
            }

            communication.store( communicationFile );

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    private String getClasspathDir() {
        String filePath = Configuration.class.getResource( "/communication-template.xml" ).getPath();
        return filePath.substring( 0, filePath.indexOf( "communication-template.xml" ) );
    }
    
    private final XPathService getCommunicationTemplate() throws Exception {

        // open template xml or communication file
        final XPathService communication = new XPathService();
        final InputStream inputStream = Configuration.class.getResourceAsStream( "/communication-template.xml" );
        communication.registerDocument( inputStream );

        return communication;
    }
    
    private class Communication {
        public String url;
        public String port;
        public String ip;
    }
}
