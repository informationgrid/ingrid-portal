/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
package de.ingrid.mdek.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

public class PortalAuthenticationProvider implements AuthenticationProvider {
    
    private final static Logger log = Logger.getLogger(PortalAuthenticationProvider.class);  

    /**
     * Since authentication id done via the portal we can return always false here, since 
     * user should be written in session already.
     */
    @Override
    public boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public List<String> getAllUserIds() {
        List<String> userList = new ArrayList<String>();
        
        WebContext wctx = WebContextFactory.get();
        HttpSession session = wctx.getSession();

        ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");

        try {
            UserManager userManager = (UserManager) ctx.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
            RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
            List<String> users = userManager.getUserNames("");

            for (String user : users) {
                if (!user.equals("admin") && !user.equals("guest") && !user.equals("devmgr")
                     && !roleManager.isUserInRole(user, "admin")
                     && !roleManager.isUserInRole(user, "admin-portal")
                     && !roleManager.isUserInRole(user, "mdek")) {
                    userList.add(user);
                }
            }

        } catch (Exception err) {
            log.error("Exception: ", err);
            throw new RuntimeException("Error while fetching users from the portal context.", err);
        }

        Collections.sort(userList);
        return userList;
    }

    @Override
    public String getForcedUser(HttpServletRequest req) {
        // if we come from an DWR call, we need to get the request
        HttpSession ses = null;
        if (req == null) {
            WebContext wctx = WebContextFactory.get();
            req = wctx.getHttpServletRequest();
            ses = wctx.getSession();
        } else {
            ses = req.getSession();
        }

        ServletContext ctx = ses.getServletContext().getContext("/ingrid-portal-mdek");

        String forcedIgeUser = null;
        if (ctx != null) {
            // ID of session is shared between contexts /ingrid-portal-mdek and /ingrid-portal-mdek-application
            // due to set sessionCookiePath="/" in context files !
            // see https://tomcat.apache.org/tomcat-7.0-doc/config/context.html#Common_Attributes
            // But NO session attributes are transferred. So we set the "forced user" as context attribute
            // with the session id, so forced users are bound to session !!!
            // Is set as context attribute in ingrid-portal-mdek MdekAdminLoginPortlet.processActionIgeLogin() ! 
            String sharedSessionId = ses.getId();
            forcedIgeUser = (String) ctx.getAttribute("ige.force.userName:" + sharedSessionId);
        }
        return forcedIgeUser;
    }

    @Override
    public void setIgeUser(String username) {
        WebContext wctx = WebContextFactory.get();
        HttpSession session = wctx.getSession();
        ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");
        RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        try {
            roleManager.addRoleToUser(username, "mdek");
        } catch (SecurityException e) {
            log.debug("SecurityException while setting role 'mdek' to user: " + username, e);
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public void removeIgeUser(String username) {
        WebContext wctx = WebContextFactory.get();
        HttpSession session = wctx.getSession();
        ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");
        UserManager userManager = (UserManager) ctx.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (userManager.userExists(username)) {
            RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
            try {
                roleManager.removeRoleFromUser(username, "mdek");
            } catch (SecurityException e) {
                log.debug("SecurityException while removing role 'mdek' from user: " + username, e);
                throw new RuntimeException(e);
            }
        }
    }

}
