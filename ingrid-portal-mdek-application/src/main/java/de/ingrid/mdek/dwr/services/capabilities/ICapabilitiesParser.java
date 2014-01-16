/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.dwr.services.capabilities;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import de.ingrid.mdek.beans.CapabilitiesBean;

/**
 * This interface describes the information of a capability document.
 * @author André Wallat
 *
 */
public interface ICapabilitiesParser {

    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException;

}
