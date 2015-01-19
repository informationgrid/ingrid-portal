/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.portlets.admin;

import java.security.Permissions;
import java.util.ArrayList;

import de.ingrid.portal.security.permission.IngridPartnerPermission;
import de.ingrid.portal.security.permission.IngridPortalPermission;

import junit.framework.TestCase;

public class UserAdminPortletTest extends TestCase {

    /*
     * Test method for 'de.ingrid.portal.portlets.admin.AdminUserPortlet.includeUserByPermission(Permissions, Permissions)'
     * <li>for admins with permission "admin": all users </li>
     * <li>for admins with permission "admin.portal": users with only role
     * "user" </li>
     * <li>for admins with permission "admin.portal": users with a permission
     * "admin.portal.*" </li>
     * <li>for admins with permission "admin.portal.partner": users with
     * IngridPortalPermission("admin.portal.partner.*") AND
     * IngridPartnerPermission("partner".<partner of the admin></li>
     * <li>for admins with permission "admin.portal.partner": users with role
     * "ingrid-provider" AND the IngridPartnerPermission("partner.<partner of
     * the admin>")</li>
     * 
     */
    public void testIncludeUserByRoleAndPermission() {
        
        Permissions authUserPermissions = new Permissions();
        authUserPermissions.add(new IngridPortalPermission("admin"));
        Permissions userPermissions = new Permissions();
        userPermissions.add(new IngridPortalPermission("admin.portal"));
        ArrayList userRoles = new ArrayList();
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), true);
        userRoles = new ArrayList();
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), true);

        
        authUserPermissions = new Permissions();
        authUserPermissions.add(new IngridPortalPermission("admin.portal"));
        userPermissions = new Permissions();
        userPermissions.add(new IngridPortalPermission("admin"));
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), false);
        userPermissions.add(new IngridPortalPermission("admin.portal"));
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), false);
        userPermissions.add(new IngridPortalPermission("admin.portal.provider"));
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), true);
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), true);
        
        authUserPermissions = new Permissions();
        authUserPermissions.add(new IngridPortalPermission("admin.portal.partner"));
        authUserPermissions.add(new IngridPartnerPermission("partner.he"));
        userPermissions = new Permissions();
        userPermissions.add(new IngridPortalPermission("admin.portal.partner.provider.index"));
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), false);
        userPermissions.add(new IngridPartnerPermission("partner.ni"));
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), false);
        userPermissions.add(new IngridPartnerPermission("partner.he"));
        assertEquals(AdminUserPortlet.includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions), true);
        
    }

}
