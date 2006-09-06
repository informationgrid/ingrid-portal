/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;

import de.ingrid.portal.security.permission.IngridPartnerPermission;
import de.ingrid.portal.security.permission.IngridProviderPermission;

/**
 * Utility Class for all security related issues.
 * 
 * @author joachim@wemove.com
 */
public class UtilsSecurity {

    private final static Log log = LogFactory.getLog(UtilsSecurity.class);

    /**
     * Merge role permissions with user permissions
     * 
     * @param p
     *            The Principal of the user to merge the role permission with.
     * @param permissionManager
     *            The JETSPEED permission manager.
     * @param roleManager
     *            The JETSPEED role manager.
     * @return The merged Permissions.
     */
    public static Permissions getMergedPermissions(Principal p, PermissionManager permissionManager,
            RoleManager roleManager) {
        Permissions result = permissionManager.getPermissions(p);
        try {
            Collection roles = roleManager.getRolesForUser(p.getName());
            Iterator roleIterator = roles.iterator();
            while (roleIterator.hasNext()) {
                // check for role based permission to show the user
                Role role = (Role) roleIterator.next();
                Permissions rp = permissionManager.getPermissions(role.getPrincipal());
                Enumeration en = rp.elements();
                while (en.hasMoreElements()) {
                    result.add((Permission) en.nextElement());
                }
            }
        } catch (SecurityException e) {
            if (log.isErrorEnabled()) {
                log.error("Error merging roles of principal '" + p.getName() + "'!", e);
            }
        }
        return result;
    }

    /**
     * Extracts partner ids from the given permissions.
     * 
     * @param permissions
     *            The permissions.
     * @return List of partner ids.
     */
    public static ArrayList getPartnersFromPermissions(Permissions permissions) {
        ArrayList result = new ArrayList();

        Enumeration en = permissions.elements();
        while (en.hasMoreElements()) {
            Permission p = (Permission) en.nextElement();
            if (p instanceof IngridPartnerPermission) {
                result.add(((IngridPartnerPermission) p).getPartner());
            }
        }
        return result;
    }

    /**
     * Extracts provider ids from the given permissions.
     * 
     * @param permissions
     *            The permissions.
     * @return List of provider ids.
     */
    public static ArrayList getProvidersFromPermissions(Permissions permissions) {
        ArrayList result = new ArrayList();

        Enumeration en = permissions.elements();
        while (en.hasMoreElements()) {
            Permission p = (Permission) en.nextElement();
            if (p instanceof IngridProviderPermission) {
                result.add(((IngridProviderPermission) p).getProvider());
            }
        }
        return result;
    }

}
