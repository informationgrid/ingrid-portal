/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.wms;

import java.util.Collection;

import de.ingrid.portal.interfaces.wms.om.WMSSearchDescriptor;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public interface WMSInterface {

    Collection getWMSServices(String sessionID);

    
    WMSSearchDescriptor getWMSSearchParameter(String sessionID);
    
}
