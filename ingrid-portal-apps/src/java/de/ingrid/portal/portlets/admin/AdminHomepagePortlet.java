/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
package de.ingrid.portal.portlets.admin;

import de.ingrid.portal.global.IngridResourceBundle;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.velocity.context.Context;

import javax.portlet.*;
import java.io.IOException;

/**
 * Portlet for configuration of default home page (only possible as admin).
 * 
 * @author martin@wemove.com
 */
public class AdminHomepagePortlet extends ConfigureHomepagePortlet {

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        
        // set localized title for this page
        response.setTitle(messages.getString("personalize.home.title"));
        
        if (pagePath == null) {
            pagePath = Folder.PATH_SEPARATOR + "default-page.psml";        	
        }

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        if (pagePath == null) {
            pagePath = Folder.PATH_SEPARATOR + "default-page.psml";        	
        }
        
        super.processAction(request, response);
    }
}
