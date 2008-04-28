package de.ingrid.mdek.dwr.services;

import java.security.Permissions;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.User;
import de.ingrid.mdek.handler.SecurityRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.portal.security.util.SecurityHelper;

public class SecurityServiceImpl {

	private final static Logger log = Logger.getLogger(SecurityServiceImpl.class);	

	// Injected by Spring
	private SecurityRequestHandler securityRequestHandler;

	
	public List<Group> getGroups() {
		try {
			return securityRequestHandler.getGroups();
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching groups.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public Group getGroupDetails(String name) {
		try {
			return securityRequestHandler.getGroupDetails(name);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching group details.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public Group createGroup(Group group, boolean refetch) {
		try {
			return securityRequestHandler.createGroup(group, true);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while creating group.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public Group storeGroup(Group group, boolean refetch) {
		try {
			return securityRequestHandler.storeGroup(group, true);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing group.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public void deleteGroup(Long groupId) {
		try {
			securityRequestHandler.deleteGroup(groupId);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while deleting group.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public UserData getUserDataForAddress(String addressUuid) {
		return MdekSecurityUtils.getUserDataForAddress(addressUuid);
	} 

	public List<User> getSubUsers(Long userId) {
		try {
			List<User> users = securityRequestHandler.getSubUsers(userId);		
			List<User> resultList = new ArrayList<User>();
			for (User user : users) {
				User detailedUser = getUserDetails(user.getAddressUuid());
				detailedUser.setUserData(MdekSecurityUtils.getUserDataForAddress(user.getAddressUuid()));
				resultList.add(detailedUser);
			}
			return resultList;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching subusers.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public User getCurrentUser() {
		UserData curUserData = MdekSecurityUtils.getCurrentPortalUserData();
		User curUser = getUserDetails(curUserData.getAddressUuid());
		curUser.setUserData(curUserData);
		return curUser;
	}

	public User getUserDetails(String userId) {
		try {
			return securityRequestHandler.getUserDetails(userId);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public User createUser(User user, String portalLogin, boolean refetch) {
		try {
			User u = securityRequestHandler.createUser(user, refetch);
			UserData userData = new UserData();
			userData.setPortalLogin(portalLogin);
			userData.setAddressUuid(user.getAddressUuid());
			userData.setPlugId(MdekSecurityUtils.getCurrentPortalUserData().getPlugId());

			MdekSecurityUtils.createUserData(userData);
			u.setUserData(userData);
			return u;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while creating user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public User storeUser(User user, String portalLogin, boolean refetch) {
		try {
			User u = securityRequestHandler.storeUser(user, refetch);
			UserData userData = new UserData();
			userData.setPortalLogin(portalLogin);
			userData.setAddressUuid(user.getAddressUuid());
			userData.setPlugId(MdekSecurityUtils.getCurrentPortalUserData().getPlugId());

			MdekSecurityUtils.storeUserData(userData);
			return u;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public void deleteUser(Long userId, String addressUuid) {
		try {
			securityRequestHandler.deleteUser(userId);
			MdekSecurityUtils.deleteUserDataForAddress(addressUuid);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while deleting user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public User getCatalogAdmin() {
		try {
			return securityRequestHandler.getCatalogAdmin();
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching cat admin.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}
	
	
	public List<String> getPortalUsers() {
		ArrayList<String> userList = new ArrayList<String>();
		
		WebContext wctx = WebContextFactory.get();
		HttpSession session = wctx.getSession();

		ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");

		try {
			UserManager userManager = (UserManager) ctx.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
			Iterator<org.apache.jetspeed.security.User> users = userManager.getUsers("");

			while (users.hasNext()) {
				org.apache.jetspeed.security.User user = users.next();
				Principal userPrincipal = getPrincipal(user.getSubject(), UserPrincipal.class);
				userList.add(userPrincipal.getName());
			}

		} catch (Exception err) {
			log.error("Exception: ", err);
			throw new RuntimeException("Error while fetching users from the portal context.", err);
		}
		
		return userList;
	}

	public SecurityRequestHandler getSecurityRequestHandler() {
		return securityRequestHandler;
	}

	public void setSecurityRequestHandler(
			SecurityRequestHandler securityRequestHandler) {
		this.securityRequestHandler = securityRequestHandler;
	}

	public void testSecurity() {
		WebContext wctx = WebContextFactory.get();
		HttpSession session = wctx.getSession();
		log.debug("HTTPSession ID:"+session.getId());

/*
		Enumeration e = session.getAttributeNames();
		log.debug("HTTPSession Attributes:");
		while (e.hasMoreElements()) {
			String attrib = (String) e.nextElement();
			log.debug(attrib+" - "+session.getAttribute(attrib));
		}
*/

		ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");
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

			Iterator<org.apache.jetspeed.security.User> users = userManager.getUsers("");
			while (users.hasNext()) {
				org.apache.jetspeed.security.User user = users.next();
				Preferences pref = user.getUserAttributes();

				Principal userPrincipal = getPrincipal(user.getSubject(), UserPrincipal.class);
	            Permissions userPermissions = SecurityHelper.getMergedPermissions(userPrincipal, permissionManager, roleManager);

	            // get the user roles
	            Collection<Role> userRoles = roleManager.getRolesForUser(userPrincipal.getName());

				log.debug("User Preferences for "+userPrincipal.getName()+":");
				for (String key : pref.keys()) {
					log.debug(key+" - "+pref.get(key, ""));
				}

	            String roleString = "";
                Iterator<Role> it = userRoles.iterator();
                while (it.hasNext()) {
                    Role r = it.next();
                    roleString = roleString.concat(r.getPrincipal().getName());
                    if (it.hasNext()) {
                        roleString = roleString.concat(", ");
                    }
                }
                log.debug("User roles: "+roleString);
			}

			HttpServletRequest req = wctx.getHttpServletRequest();
			log.debug("Remote user: "+req.getRemoteUser());
			if (req.getUserPrincipal() != null)
				log.debug("User Principal: "+req.getUserPrincipal().getName());

			e = req.getAttributeNames();
			log.debug("HttpServletRequest Attributes:");
			while (e.hasMoreElements()) {
				String attrib = (String) e.nextElement();
				log.debug(attrib+" - "+req.getAttribute(attrib));
			}

		} catch (Exception err) {
			log.error("Exception: ", err);
		}
	}


    private static Principal getPrincipal(Subject subject, Class classe)
    {
        Principal principal = null;
        Iterator principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = (Principal) principals.next();
            if (classe.isInstance(p))
            {
                principal = p;
                break;
            }
        }
        return principal;
    }
}
