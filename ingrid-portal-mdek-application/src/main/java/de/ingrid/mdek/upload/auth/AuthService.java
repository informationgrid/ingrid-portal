package de.ingrid.mdek.upload.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * AuthService defines the interface for classes, that handle authorization requests.
 */
public interface AuthService {

    /**
     * Check if a request is authorized to perform an action on a resource
     *
     * @param request
     * @param resource
     * @param action
     * @return boolean
     */
    public boolean isAuthorized(HttpServletRequest request, String resource, String action);
}
