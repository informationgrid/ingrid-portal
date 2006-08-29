package de.ingrid.portal.portlets.admin;

import java.security.Permissions;

import de.ingrid.portal.security.permission.IngridPartnerPermission;
import de.ingrid.portal.security.permission.IngridPortalPermission;

import junit.framework.TestCase;

public class UserAdminPortletTest extends TestCase {

    /*
     * Test method for 'de.ingrid.portal.portlets.admin.UserAdminPortlet.includeUserByPermission(Permissions, Permissions)'
     */
    public void testIncludeUserByPermission() {
        
        Permissions authUserPermissions = new Permissions();
        authUserPermissions.add(new IngridPortalPermission("admin"));
        Permissions userPermissions = new Permissions();
        userPermissions.add(new IngridPortalPermission("admin.portal"));
        assertEquals(UserAdminPortlet.includeUserByPermission(authUserPermissions, userPermissions), true);

        
        authUserPermissions = new Permissions();
        authUserPermissions.add(new IngridPortalPermission("admin.portal"));
        userPermissions = new Permissions();
        userPermissions.add(new IngridPortalPermission("admin"));
        assertEquals(UserAdminPortlet.includeUserByPermission(authUserPermissions, userPermissions), false);
        userPermissions.add(new IngridPortalPermission("admin.portal"));
        assertEquals(UserAdminPortlet.includeUserByPermission(authUserPermissions, userPermissions), false);
        userPermissions.add(new IngridPortalPermission("admin.portal.provider"));
        assertEquals(UserAdminPortlet.includeUserByPermission(authUserPermissions, userPermissions), true);
        
        authUserPermissions = new Permissions();
        authUserPermissions.add(new IngridPortalPermission("admin.portal.partner"));
        authUserPermissions.add(new IngridPartnerPermission("partner", "he"));
        userPermissions = new Permissions();
        userPermissions.add(new IngridPortalPermission("admin.portal.partner.provider.index"));
        assertEquals(UserAdminPortlet.includeUserByPermission(authUserPermissions, userPermissions), false);
        userPermissions.add(new IngridPartnerPermission("partner", "ni"));
        assertEquals(UserAdminPortlet.includeUserByPermission(authUserPermissions, userPermissions), false);
        userPermissions.add(new IngridPartnerPermission("partner", "he"));
        assertEquals(UserAdminPortlet.includeUserByPermission(authUserPermissions, userPermissions), true);
        
    }

}
