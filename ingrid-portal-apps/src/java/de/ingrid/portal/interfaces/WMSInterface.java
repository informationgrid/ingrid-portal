/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.interfaces;

import de.ingrid.portal.interfaces.om.WMSSearchDescriptor;
import de.ingrid.portal.interfaces.om.WMSServiceDescriptor;
import org.apache.commons.configuration.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Defines the interface to be implemented to communicate with the mapbender WMS
 * server that has been extended for the ingrid project with some interface
 * methods.
 * 
 * @author joachim@wemove.com
 */
public interface WMSInterface {

	public static final String MAPBENDER_VERSION_2_1 = "2.1";
	
    /**
     * Returns the Configuration of the service.
     * 
     * @return The Configuration of the Service
     */
    Configuration getConfig();

    /**
     * Returns all WMS Services from WMS Server that are bound to the given
     * session id.
     * 
     * @param sessionID
     *            The session id
     * @return Collection of WMSServiceDescriptor
     */
    Collection getWMSServices(String sessionID);

    /**
     * Returns the current search parameters that are bound to the given session
     * id.
     * 
     * @param sessionID
     *            The session id
     * @return The current search parameter as WMSSearchDescriptor.
     */
    WMSSearchDescriptor getWMSSearchParameter(String sessionId);

    /**
     * Returns the URL of the WMS VIEWER to DISPLAY the map. The URL contains
     * the session id to fetch the map that is bound to the session id. This
     * enables different users to preserve their preferences on the WMS server.
     * 
     * @param sessionId
     *            The session id.
     * @param jsEnabled
     *            true when to fetch JavaScript version, false when to fetch
     *            HTML version
     * @param language
     *            pass locale, pass null if default language
     * @return The url of the WMS view script on the Server.
     */
    String getWMSViewerURL(String sessionId, Locale language);

    /**
     * Returns the URL of the WMS SEARCH "MAP" with search functionality. The
     * URL contains the session id to fetch the map that is bound to the session
     * id. This enables different users to preserve their preferences on the WMS
     * server.
     * 
     * @param sessionId
     *            The session id.
     * @param jsEnabled
     *            true when to fetch JavaScript version, false when to fetch
     *            HTML version
     * @param language
     *            pass locale, pass null if default language
     * @return The url of the WMS search script on the Server.
     */
    String getWMSSearchURL(String sessionId, Locale language);

    /**
     * Returns the URL of the WMS VIEWER or the WMS_SEARCH template of the
     * server. Includes a command to add a service, specified with the service
     * descriptor to the URL to the WMS server session. If the descriptions name
     * is null, no name will be put in the resulting url for this service. If
     * the url is null the descriptor will be ignored.
     * 
     * @param WMSServiceDescriptor
     *            The description of the WMS service to add. If the descriptions
     *            name is null, no name will be put in the resulting url.
     * @param sessionId
     *            The sessin id.
     * @param jsEnabled
     *            true when to fetch JavaScript version, false when to fetch
     *            HTML version
     * @param language
     *            pass locale, pass null if default language
     * @param isViewer
     *            pass true if the resulting URL should point to the viewer
     *            template, false for the search template.
     * @return The url of the WMS VIEWER Server including the command to add the
     *         service.
     */
    String getWMSAddedServiceURL(WMSServiceDescriptor service, String sessionId, boolean jsEnabled, Locale language,
            boolean isViewer);

    /**
     * Returns the URL of the WMS VIEWER or the WMS_SEARCH template of the
     * server. Includes a command to add all services, specified in the
     * ArrayList of WMSServiceDescriptor to the URL to the WMS server session.
     * If a descriptors name is null, no name will be put in the resulting url
     * for this service. If the url is null the descriptor will be ignored.
     * 
     * @param services
     *            The List of WMSServiceDescriptor
     * @param sessionId
     *            Thse session id.
     * @param jsEnabled
     *            true when to fetch JavaScript version, false when to fetch
     *            HTML version
     * @param language
     *            pass locale, pass null if default language
     * @param isViewer
     *            pass true if the resulting URL should point to the viewer
     *            template, false for the search template.
     * @return The url of the WMS Server including the command to add the
     *         services.
     */
    String getWMSAddedServiceURL(List services, String sessionId, Locale language, boolean isViewer);

    /**
     * Returns the admin interfaces for the WMS component. There can be more
     * admin interface URLs.
     * 
     * @return String Array with admin URLs
     */
    String[] getAdminInterfaceURLs();
    
    
    /**
     * Returns the current url encoded WMC document that describes the state of mapBender and is bound to the given session
     * id.
     * 
     * @param sessionID
     *            The session id
     * @return The current WMC XML document as String.
     */
    String getWMCDocument(String sessionId);

    /**
     * Sets a urlencoded WMC document that describes the state of mapBender and is bound to the given session
     * id.
     * 
     * @param wmc
     *            The WMC document as string.
     * @param sessionID
     *            The session id
     */
    void setWMCDocument(String wmc, String sessionId);
    
    
    /**
     * Returns the configured MapBender version in wms_interface.properties.
     * 
     * @return The MapBender version configured in wms_interface.properties
     */
    public String getMapbenderVersion();
    
    
}
