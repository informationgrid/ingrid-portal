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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.security.impl.cas.CASPortalFilter;

/**
 * LogoutServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id: LogoutServlet.java 926342 2010-03-22 21:18:19Z taylor $
 */
public class LogoutServlet extends HttpServlet
{
	private String casLogoutUrl = null; 

	public void init(ServletConfig config) throws ServletException  
	{
	    super.init(config);
	    casLogoutUrl = config.getInitParameter("casLogoutUrl"); // will return null if not existing
	  }
	
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        String destination = request.getParameter(LoginConstants.DESTINATION);
        
        if (casLogoutUrl != null)
        {
	        String casUserName = (String) request.getSession().getAttribute(CASPortalFilter.CAS_FILTER_USER);
	        if (casUserName != null)
	        {
	        	destination = this.casLogoutUrl;
	        }
        }

        /* wemove: Jetspeed removes locale after logout (JS 2.1). We keep 2.1 code setting locale again. */
        HttpSession session =  request.getSession(true);
        Object locale =  session.getAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE);
        session.invalidate();
        request.getSession(true).setAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, locale);

        if (destination == null)
        {
            destination = request.getContextPath() + "/"; 
        }
        response.sendRedirect(response.encodeURL(destination));
    }
}
