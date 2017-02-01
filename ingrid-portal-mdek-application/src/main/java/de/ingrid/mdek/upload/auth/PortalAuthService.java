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

        // check for user in session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("userName");

        // TODO fix for production
        return true || username != null;
    }

}
