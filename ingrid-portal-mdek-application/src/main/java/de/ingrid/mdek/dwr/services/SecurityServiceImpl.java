package de.ingrid.mdek.dwr.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
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

public class SecurityServiceImpl {

	private final static Logger log = Logger.getLogger(SecurityServiceImpl.class);	

	// Injected by Spring
	private SecurityRequestHandler securityRequestHandler;

	
	public List<Group> getGroups(boolean includeCatAdminGroup) {
		try {
			return securityRequestHandler.getGroups(includeCatAdminGroup);
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

			// Add the proper role to the user
			addRoleToUser(portalLogin, "mdek");

			return u;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while creating user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (SecurityException e) {
			log.debug("SecurityException while creating user.", e);
			throw new RuntimeException(e);
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
			UserData user = MdekSecurityUtils.deleteUserDataForAddress(addressUuid);
			removeRoleFromUser(user.getPortalLogin(), "mdek");
			
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while deleting user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (SecurityException e) {
			log.debug("SecurityException while deleting user.", e);
			throw new RuntimeException(e);
		}
	}

	public User getCatalogAdmin() {
		try {
			User u = securityRequestHandler.getCatalogAdmin();
			u.setUserData(MdekSecurityUtils.getUserDataForAddress(u.getAddressUuid()));
			return u;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching cat admin.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}
	
	
	public List<String> getPortalUsers() {
		List<String> userList = new ArrayList<String>();
		
		WebContext wctx = WebContextFactory.get();
		HttpSession session = wctx.getSession();

		ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");

		try {
			UserManager userManager = (UserManager) ctx.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
			RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
			Iterator<String> users = userManager.getUserNames("");

			while (users.hasNext()) {
				String user = users.next();
		    	if (!user.equals("admin") && !user.equals("guest") && !user.equals("devmgr")
		          	 && !roleManager.isUserInRole(user, "admin")
		       	     && !roleManager.isUserInRole(user, "admin-portal")
		       		 && !roleManager.isUserInRole(user, "mdek")) {
					userList.add(user);
				}
			}

		} catch (Exception err) {
			log.error("Exception: ", err);
			throw new RuntimeException("Error while fetching users from the portal context.", err);
		}

		Collections.sort(userList);
		return userList;
	}

	public List<User> getUsersWithWritePermissionForObject(String objectUuid, boolean checkWorkflow, boolean includePermissions) {
		return securityRequestHandler.getUsersWithWritePermissionForObject(objectUuid, checkWorkflow, includePermissions);
	}
	
	public List<User> getUsersWithWritePermissionForAddress(String addressUuid, boolean checkWorkflow, boolean includePermissions) {
		return securityRequestHandler.getUsersWithWritePermissionForAddress(addressUuid, checkWorkflow, includePermissions);
	}

	public List<User> getUsersOfGroup(String groupName) {
		return securityRequestHandler.getUsersOfGroup(groupName);
	}

	public SecurityRequestHandler getSecurityRequestHandler() {
		return securityRequestHandler;
	}

	public void setSecurityRequestHandler(
			SecurityRequestHandler securityRequestHandler) {
		this.securityRequestHandler = securityRequestHandler;
	}

	private void addRoleToUser(String userName, String role) throws SecurityException {
		WebContext wctx = WebContextFactory.get();
		HttpSession session = wctx.getSession();
		ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");
		RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
		roleManager.addRoleToUser(userName, role);
	}

	private void removeRoleFromUser(String userName, String role) throws SecurityException {
		WebContext wctx = WebContextFactory.get();
		HttpSession session = wctx.getSession();
		ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");
		RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
		roleManager.removeRoleFromUser(userName, role);		
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
