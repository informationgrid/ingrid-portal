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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;

import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.security.permission.IngridPartnerPermission;
import de.ingrid.portal.security.permission.IngridPortalPermission;
import de.ingrid.portal.security.permission.IngridProviderPermission;

/**
 * Utility Class for all security related issues.
 * 
 * @author joachim@wemove.com
 */
public class UtilsSecurity {

    private final static Log log = LogFactory.getLog(UtilsSecurity.class);

    public final static IngridPortalPermission ADMIN_INGRID_PORTAL_PERMISSION = new IngridPortalPermission("admin");

    public final static IngridPortalPermission ADMIN_PORTAL_INGRID_PORTAL_PERMISSION = new IngridPortalPermission(
            "admin.portal");

    public final static IngridPortalPermission ADMIN_PORTAL_STAR_INGRID_PORTAL_PERMISSION = new IngridPortalPermission(
            "admin.portal.*");

    public final static IngridPortalPermission ADMIN_PORTAL_PARTNER_INGRID_PORTAL_PERMISSION = new IngridPortalPermission(
            "admin.portal.partner");

    public final static IngridPortalPermission ADMIN_PORTAL_PARTNER_STAR_INGRID_PORTAL_PERMISSION = new IngridPortalPermission(
            "admin.portal.partner.*");

    public final static IngridPortalPermission ADMIN_PORTAL_PARTNER_PROVIDER_INDEX_INGRID_PORTAL_PERMISSION = new IngridPortalPermission(
            "admin.portal.partner.provider.index");

    public final static IngridPortalPermission ADMIN_PORTAL_PARTNER_PROVIDER_CATALOG_INGRID_PORTAL_PERMISSION = new IngridPortalPermission(
            "admin.portal.partner.provider.catalog");

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
     * Extracts partner ids from the given permissions. Return all partners for
     * permission admin and admin.portal
     * 
     * @param permissions
     *            The permissions.
     * @param partnerPermissionOnly
     *            If true, only IngridPartnerPermissions will be taken into
     *            account
     * @return List of partner ids.
     */
    public static ArrayList getPartnersFromPermissions(Permissions permissions, boolean partnerPermissionOnly) {
        ArrayList result = new ArrayList();

        // return all partners for powered admins
        if (!partnerPermissionOnly
                && (permissions.implies(ADMIN_PORTAL_INGRID_PORTAL_PERMISSION) || permissions
                        .implies(ADMIN_INGRID_PORTAL_PERMISSION))) {
            List partners = UtilsDB.getPartners();
            for (int i = 0; i < partners.size(); i++) {
                result.add(((IngridPartner) partners.get(i)).getIdent());
            }
            return result;
        }

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
     * Extracts provider ids from the given permissions.<br/> Return all
     * provider for permissions admin and admin.portal.<br/>Return all provider
     * of given partners for permissions admin.portal.partner
     * 
     * @param permissions
     *            The permissions.
     * @param providerPermissionsOnly
     *            If true, only IngridProviderPermissions will be taken into
     *            account
     * @return List of provider ids.
     */
    public static ArrayList getProvidersFromPermissions(Permissions permissions, boolean providerPermissionsOnly) {
        ArrayList result = new ArrayList();

        // return all providers for powered admins
        if (!providerPermissionsOnly
                && (permissions.implies(ADMIN_PORTAL_INGRID_PORTAL_PERMISSION) || permissions
                        .implies(ADMIN_INGRID_PORTAL_PERMISSION))) {
            List providers = UtilsDB.getProviders();
            for (int i = 0; i < providers.size(); i++) {
                result.add(((IngridProvider) providers.get(i)).getIdent());
            }
            return result;
        }

        // return all providers of a partner for powered admins
        if (!providerPermissionsOnly && (permissions.implies(ADMIN_PORTAL_PARTNER_INGRID_PORTAL_PERMISSION))) {
            List partners = getPartnersFromPermissions(permissions, false);
            for (int i = 0; i < partners.size(); i++) {
                List providers = UtilsDB.getProvidersFromPartnerKey((String) partners.get(i));
                for (int j = 0; j < providers.size(); j++) {
                    result.add(((IngridProvider) providers.get(j)).getIdent());
                }
            }
            return result;
        }

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
