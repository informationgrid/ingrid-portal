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
package de.ingrid.portal.portlets.myportal;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class MyPortalOverviewPortlet extends AbstractVelocityMessagingPortlet {

    private static final Logger log = LoggerFactory.getLogger(MyPortalOverviewPortlet.class);
    private UserManager userManager;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
    }

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

        // check for just logged in users
        HttpSession session = ((RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest()
                .getSession(true);
        String loginStarted = (String) session.getAttribute(Settings.SESSION_LOGIN_STARTED);
        if (loginStarted != null && loginStarted.equals("1")) {
            session.removeAttribute(Settings.SESSION_LOGIN_STARTED);
            // do something "after login" stuff

            // initialize the session preference with persistent data from
            // personalization
            IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                    IngridSessionPreferences.SESSION_KEY);
            if (sessionPrefs != null) {
                Principal principal = request.getUserPrincipal();
                if (principal != null) {
                    HashMap persistentSearchSettings = (HashMap) IngridPersistencePrefs.getPref(principal.getName(),
                            IngridPersistencePrefs.SEARCH_SETTINGS);
                    if (persistentSearchSettings != null) {
                        sessionPrefs.putAll(persistentSearchSettings);
                    }
                }
            }
        }

        String userName = request.getUserPrincipal().getName();
        User user = null;
        try {
            user = userManager.getUser(userName);
        } catch (SecurityException e) {
            log.error("SecurityException.", e);
        }

        if (user != null) {
            context.put("userAttributes", user.getInfoMap());
        }

        super.doView(request, response);
    }

}
