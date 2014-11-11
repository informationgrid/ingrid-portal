/*
 * **************************************************-
 * Ingrid Portal Base
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.PortalReservedParameters;

/**
 * LogoutServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id: LogoutServlet.java 394066 2006-04-14 11:58:18Z ate $
 */
public class LogoutServlet extends HttpServlet
{

    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        String destination = request.getParameter(LoginConstants.DESTINATION);
        HttpSession session =  request.getSession(true);
       
        /* Jetspeed remove locale after logout */
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
