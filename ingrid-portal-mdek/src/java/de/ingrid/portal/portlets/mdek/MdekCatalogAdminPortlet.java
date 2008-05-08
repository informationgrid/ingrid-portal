/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.mdek;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;


/**
 * This portlet handles the administration processes for the catalog admin
 *
 * @author michael.benz@wemove.com
 */
public class MdekCatalogAdminPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MdekCatalogAdminPortlet.class);

    // VIEW TEMPLATES
    private final static String TEMPLATE_START = "/WEB-INF/templates/mdek/mdek_catalog_admin.vm";
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
    	setDefaultViewPage(TEMPLATE_START);
        super.doView(request, response);
	}

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

    }
}