/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.audit.AuditActivity;
import org.apache.jetspeed.cache.UserContentCacheManager;
import org.apache.jetspeed.components.ComponentManager;

/**
 * LoginRedirectorServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id: LoginRedirectorServlet.java 1536817 2013-10-29 17:11:29Z taylor $
 */
public class LoginRedirectorServlet extends HttpServlet
{

    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession(true);
        String destination = (String) session
                .getAttribute(LoginConstants.DESTINATION);
        if (destination == null || destination.equals(request.getContextPath()))
            destination = request.getContextPath() + "/";
        else
            session.removeAttribute(LoginConstants.DESTINATION);

        String username = (String)session.getAttribute(LoginConstants.USERNAME);
        
        session.removeAttribute(LoginConstants.USERNAME);
        session.removeAttribute(LoginConstants.PASSWORD);
        session.removeAttribute(LoginConstants.RETRYCOUNT);

        /* wemove: Jetspeed removes locale after login. We keep it. */
        //session.removeAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE);

        ComponentManager cm = Jetspeed.getComponentManager();
        UserContentCacheManager userContentCacheManager = cm.lookupComponent("userContentCacheManager");
        userContentCacheManager.evictUserContentCache(username, session.getId());
        AuditActivity audit = cm.lookupComponent("org.apache.jetspeed.audit.AuditActivity");
        if (audit != null)
        {
            audit.logUserActivity(username, request.getRemoteAddr(), AuditActivity.AUTHENTICATION_SUCCESS, "Active Authentication");
        }
        
        response.sendRedirect(response.encodeURL(destination));
    }

    public final void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }    
}
