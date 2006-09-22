/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminPortalProfilePortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(AdminPortalProfilePortlet.class);
    
    
    private PageManager pageManager;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        pageManager = (PageManager) getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager) {
            throw new PortletException("Failed to find the Page Manager on portlet initialization");
        }
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action != null && action.equals("switchProfile")) {
            String profileName = request.getParameter("profile");
            String profileDescriptor = getPortletConfig().getPortletContext().getRealPath("/profiles/" + profileName + "/profile.xml");
            String pageName = null;
            try {
                XMLConfiguration profile = new XMLConfiguration(profileDescriptor);
                // set pages visible/invisible
                List pages = profile.getList("pages.page.name");
                for (int i=0; i<pages.size(); i++) {
                    pageName = (String)pages.get(i);
                    boolean hidden = profile.getBoolean("pages.page(" + i + ").hidden");
                    Page p = pageManager.getPage(Folder.PATH_SEPARATOR + pageName);
                    p.setHidden(hidden);
                    pageManager.updatePage(p);
                }
                
            } catch (ConfigurationException e) {
                log.error("Error reading profile configuration (" + profileDescriptor + ")", e);
            } catch (PageNotFoundException e) {
                log.error("Page not found from  (" + Folder.PATH_SEPARATOR + pageName + ")", e);
            } catch (NodeException e) {
                log.error("Error reading page (" + Folder.PATH_SEPARATOR + pageName + ")", e);
            }
        }
        
        // TODO Auto-generated method stub
        super.processAction(request, response);
    }

}
