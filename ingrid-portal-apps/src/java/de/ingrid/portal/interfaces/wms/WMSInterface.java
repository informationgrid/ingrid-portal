/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.wms;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.configuration.Configuration;

import de.ingrid.portal.interfaces.wms.om.WMSSearchDescriptor;
import de.ingrid.portal.interfaces.wms.om.WMSServiceDescriptor;

/**
 * Defines the interface to be implemented to communicate with the 
 * mapbender WMS server that has been extended for the ingrid 
 * project with some interface methods.
 *
 * @author joachim@wemove.com
 */
public interface WMSInterface {


    /**
     * Returns the Configuration of the service.
     * 
     * @return The Configuration of the Service
     */
    Configuration getConfig();

    
    /**
     * Returns all WMS Services from WMS Server that are bound to the 
     * given session id.
     * 
     * @param sessionID The session id
     * @return Collection of WMSServiceDescriptor
     */
    Collection getWMSServices(String sessionID);

    /**
     * Returns the current search parameters that are bound to the given 
     * session id.
     * 
     * @param sessionID The session id
     * @return The current search parameter as WMSSearchDescriptor.
     */
    WMSSearchDescriptor getWMSSearchParameter(String sessionId);
    
    /**
     * Returns the URL of the WMS server to display the map. The URL contains
     * the session id to fetch the map that is bound to the session id. This
     * enables different users to preserve their preferences on the WMS server.
     * 
     * @param sessionId The session id.
     * @return The url of the WMS Server.
     */
    String getWMSURL(String sessionId);

    /**
     * Returns the URL of the WMS server. Includes a command to add a service, 
     * specified with the service descriptor to the URL to the WMS server session.
     * If the descriptions name is null, no name will be put in the resulting 
     * url for this service. If the url is null the descriptor will be ignored.
     * 
     * @param WMSServiceDescriptor The description of the WMS service to add. If 
     * the descriptions name is null, no name will be put in the resulting url. 
     * @param sessionId The sessin id.
     * @return The url of the WMS Server including the command to add the service.
     */
    String getWMSAddedServiceURL(WMSServiceDescriptor service, String sessionId);
    
    /**
     * Returns the URL of the WMS server. Includes a command to add all services, 
     * specified in the ArrayList of WMSServiceDescriptor to the URL to the WMS 
     * server session. If a descriptors name is null, no name will be put in 
     * the resulting url for this service. If the url is null the descriptor will 
     * be ignored.
     * 
     * @param services The ArrayList of WMSServiceDescriptor
     * @param sessionId Thse session id.
     * @return The url of the WMS Server including the command to add the services.
     */
    String getWMSAddedServiceURL(ArrayList services, String sessionId);

}
