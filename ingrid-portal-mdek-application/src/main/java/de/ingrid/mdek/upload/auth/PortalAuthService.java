/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
package de.ingrid.mdek.upload.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.ingrid.mdek.upload.storage.Action;

/**
 * PortalAuthService authorizes against the portal session user.
 */
public class PortalAuthService implements AuthService {

    @Override
    public boolean isAuthorized(HttpServletRequest request, String resource, String action) {
        // check if action exists
        Action fileAction = Action.lookup(action);
        if (fileAction == null) {
            return false;
        }
        if (fileAction == Action.READ) {
            return true;
        }

        // check for user in session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("userName");
        return username != null;
    }

}
