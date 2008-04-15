package de.ingrid.mdek.dwr.services;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.handler.SecurityRequestHandler;

public class SecurityServiceImpl {

	private final static Logger log = Logger.getLogger(SecurityServiceImpl.class);	

	// Injected by Spring
	private SecurityRequestHandler securityRequestHandler;


	public List<Group> getGroups() {
		return securityRequestHandler.getGroups();
	}

	public Group getGroupDetails(String name) {
		return securityRequestHandler.getGroupDetails(name);
	}

	public Group createGroup(Group group, boolean refetch) {
		return securityRequestHandler.createGroup(group, true);
	}

	public Group storeGroup(Group group, boolean refetch) {
		return securityRequestHandler.storeGroup(group, true);
	}

	public SecurityRequestHandler getSecurityRequestHandler() {
		return securityRequestHandler;
	}

	public void setSecurityRequestHandler(
			SecurityRequestHandler securityRequestHandler) {
		this.securityRequestHandler = securityRequestHandler;
	}

	public void testSecurity() {
/*
		WebContext wctx = WebContextFactory.get();
		HttpSession session = wctx.getSession();
		log.debug("HTTPSession ID:"+session.getId());

		ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-apps");
		Enumeration e = ctx.getAttributeNames();
		log.debug("ServletContext (ingrid-portal-apps) Attributes:");
		while (e.hasMoreElements()) {
			String attrib = (String) e.nextElement();
			log.debug(attrib+" - "+ctx.getAttribute(attrib));
		}

		try {
			UserManager userManager = (UserManager) ctx.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
			RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
			PermissionManager permissionManager = (PermissionManager) ctx.getAttribute(CommonPortletServices.CPS_PERMISSION_MANAGER);

			Iterator<User> users = userManager.getUsers("");
			while (users.hasNext()) {
				User user = users.next();
				Preferences pref = user.getUserAttributes();
				log.debug("User Preferences:");
				for (String key : pref.keys()) {
					log.debug(key+" - "+pref.get(key, ""));
				}
			}
//			Principal userPrincipal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);
//            Permissions userPermissions = SecurityHelper.getMergedPermissions(userPrincipal, permissionManager, roleManager);

            // get the user roles
//            Collection userRoles = roleManager.getRolesForUser(userPrincipal.getName());

		} catch (Exception err) {
			log.error("Exception: ", err);
		}
*/
	}
}
