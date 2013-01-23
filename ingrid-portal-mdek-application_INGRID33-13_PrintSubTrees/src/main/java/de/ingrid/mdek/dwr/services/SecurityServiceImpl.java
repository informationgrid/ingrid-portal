package de.ingrid.mdek.dwr.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContextFactory;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.Permission;
import de.ingrid.mdek.beans.security.User;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.MdekCallerSecurity;
import de.ingrid.mdek.handler.SecurityRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.security.AuthenticationProvider;
import de.ingrid.mdek.security.PortalAuthenticationProvider;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class SecurityServiceImpl implements SecurityService {

	private final static Logger log = Logger.getLogger(SecurityServiceImpl.class);	

	// Injected by Spring
	private SecurityRequestHandler securityRequestHandler;
	
	// Injected by Spring
	private AuthenticationProvider authProvider;

	
	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getGroups(boolean)
     */
	public List<Group> getGroups(boolean includeCatAdminGroup) {
		try {
			return securityRequestHandler.getGroups(includeCatAdminGroup);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching groups.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getGroupDetails(java.lang.String)
     */
	public Group getGroupDetails(String name) {
		try {
			return securityRequestHandler.getGroupDetails(name);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching group details.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#createGroup(de.ingrid.mdek.beans.security.Group, boolean)
     */
	public Group createGroup(Group group, boolean refetch) {
		try {
			return securityRequestHandler.createGroup(group, true);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while creating group.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#storeGroup(de.ingrid.mdek.beans.security.Group, boolean)
     */
	public Group storeGroup(Group group, boolean refetch) {
		try {
			return securityRequestHandler.storeGroup(group, true);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing group.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#deleteGroup(java.lang.Long)
     */
	public void deleteGroup(Long groupId) {
		try {
			securityRequestHandler.deleteGroup(groupId);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while deleting group.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getUserDataForAddress(java.lang.String)
     */
	public UserData getUserDataForAddress(String addressUuid) {
		return MdekSecurityUtils.getUserDataForAddress(addressUuid);
	} 

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getUserNumForPlugId(java.lang.String)
     */ 
    public int getUserNumForPlugId(String plugId) {
        return MdekSecurityUtils.getUserNumForPlugId(plugId);
    }
	
	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getSubUsers(java.lang.Long)
     */
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

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getCurrentUser()
     */
	public User getCurrentUser() {
		UserData curUserData = MdekSecurityUtils.getCurrentPortalUserData();
		User curUser = getUserDetails(curUserData.getAddressUuid());
		curUser.setUserData(curUserData);
		return curUser;
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getUserDetails(java.lang.String)
     */
	public User getUserDetails(String userId) {
		try {
			return securityRequestHandler.getUserDetails(userId);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
	 * @see de.ingrid.mdek.dwr.services.SecurityService#getUserPermissions(java.lang.String)
	 */
	@Override
    public List<Permission> getUserPermissions(String userId) {
        try {
            return securityRequestHandler.getUserPermissions(userId);
        } catch (MdekException e) {
            // Wrap the MdekException in a RuntimeException so dwr can convert it
            log.debug("MdekException while fetching user permissions for user '" + userId + "'.", e);
            throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
        }
    }

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#createUser(de.ingrid.mdek.beans.security.User, java.lang.String, boolean)
     */
	public User createUser(User user, String portalLogin, boolean refetch) {
		try {
			User u = securityRequestHandler.createUser(user, refetch);
			UserData userData = new UserData();
			userData.setPortalLogin(portalLogin);
			userData.setAddressUuid(u.getAddressUuid());
			userData.setPlugId(MdekSecurityUtils.getCurrentPortalUserData().getPlugId());

			MdekSecurityUtils.createUserData(userData);
			u.setUserData(userData);

			// Add the proper role to the user
			authProvider.setIgeUser(portalLogin);

			return u;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while creating user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#storeUser(java.lang.String, de.ingrid.mdek.beans.security.User, java.lang.String, boolean)
     */
	public User storeUser(String oldUserLogin, User user, String portalLogin, boolean refetch) {
		try {
		    boolean loginDoesNotExist = false;
			User u = securityRequestHandler.storeUser(user, refetch);
			UserData userData = MdekSecurityUtils.getUserData(oldUserLogin);
			if (userData == null) {
			    userData = new UserData();
			    loginDoesNotExist = true;
			}
			userData.setPortalLogin(portalLogin);
			userData.setAddressUuid(user.getAddressUuid());
			userData.setPlugId(MdekSecurityUtils.getCurrentPortalUserData().getPlugId());
			
			// create user-portal-connection if user only exists in IGC
			if (loginDoesNotExist) {
			    MdekSecurityUtils.createUserData(userData);
                authProvider.setIgeUser(portalLogin);
			} else if (userData.getPortalLogin().equals(oldUserLogin)) {
			    // store the user if the portal-login association did not change
				MdekSecurityUtils.storeUserData(oldUserLogin, userData);
			} else {
				// store the user and set and cancel mdek-roles
			    authProvider.removeIgeUser(oldUserLogin);
				MdekSecurityUtils.storeUserData(oldUserLogin, userData);
				authProvider.setIgeUser(portalLogin);
			}
			return u;

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#deleteUser(java.lang.Long, java.lang.String)
     */
	public void deleteUser(Long userId, String addressUuid) {
		try {
			securityRequestHandler.deleteUser(userId);
			UserData user = MdekSecurityUtils.deleteUserDataForAddress(addressUuid);
			authProvider.removeIgeUser(user.getPortalLogin());
			
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while deleting user.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getCatalogAdmin()
     */
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
	
	
	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getUsersWithWritePermissionForObject(java.lang.String, boolean, boolean)
     */
    @Override
	public List<User> getUsersWithWritePermissionForObject(String objectUuid, boolean checkWorkflow, boolean includePermissions) {
		return securityRequestHandler.getUsersWithWritePermissionForObject(objectUuid, checkWorkflow, includePermissions);
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getUsersWithWritePermissionForAddress(java.lang.String, boolean, boolean)
     */
    @Override
	public List<User> getUsersWithWritePermissionForAddress(String addressUuid, boolean checkWorkflow, boolean includePermissions) {
		return securityRequestHandler.getUsersWithWritePermissionForAddress(addressUuid, checkWorkflow, includePermissions);
	}

    @Override
    public List<User> getResponsibleUsersForNewObject(String objectUuid, boolean checkWorkflow, boolean includePermissions) {
        return securityRequestHandler.getResponsibleUsersForNewObject(objectUuid, checkWorkflow, includePermissions);
    }

    @Override
    public List<User> getResponsibleUsersForNewAddress(String addressUuid, boolean checkWorkflow, boolean includePermissions) {
        return securityRequestHandler.getResponsibleUsersForNewAddress(addressUuid, checkWorkflow, includePermissions);
    }

    @Override
    public List<User> getUsersWithPermissionForObject(String objectUuid, boolean checkWorkflow, boolean includePermissions) {
        return securityRequestHandler.getUsersWithPermissionForObject(objectUuid, checkWorkflow, includePermissions);
    }
    
    @Override
    public List<User> getUsersWithPermissionForAddress(String addressUuid, boolean checkWorkflow, boolean includePermissions) {
        return securityRequestHandler.getUsersWithPermissionForAddress(addressUuid, checkWorkflow, includePermissions);
    }
	
	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getUsersOfGroup(java.lang.String)
     */
	public List<User> getUsersOfGroup(String groupName) {
		return securityRequestHandler.getUsersOfGroup(groupName);
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#getSecurityRequestHandler()
     */
	public SecurityRequestHandler getSecurityRequestHandler() {
		return securityRequestHandler;
	}

	/* (non-Javadoc)
     * @see de.ingrid.mdek.dwr.services.SecurityService#setSecurityRequestHandler(de.ingrid.mdek.handler.SecurityRequestHandler)
     */
	public void setSecurityRequestHandler(
			SecurityRequestHandler securityRequestHandler) {
		this.securityRequestHandler = securityRequestHandler;
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

    public void setAuthProvider(AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    public AuthenticationProvider getAuthProvider() {
        return authProvider;
    }

    public boolean authenticate(String username, String password) {
        boolean isValid = false; 
        if ("admin".equals(username)) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("igeAdminUser");
            String pw = resourceBundle.getString("admin.password");
            if (password.equals(pw)) {
                isValid = true;
            }
        } else {
            isValid = authProvider.authenticate(username, password);
            if (isValid) {
                // next check if user also is known in mdek database and therefore an IGE user
                // admin user does not have to be in mdek db
                if (MdekSecurityUtils.portalLoginExists(username)) {
                    isValid = true;
                }
            }
        }
        if (isValid) {
            setSessionAttribute("userName", username);
        }
        return isValid;
    }

    private void setSessionAttribute(String key, String value) {
        HttpSession session = WebContextFactory.get().getHttpServletRequest().getSession();
        session.setAttribute(key, value);
    }
    
    private Object getSessionAttribute(String key) {
        HttpSession session = WebContextFactory.get().getHttpServletRequest().getSession();
        return session.getAttribute(key);
    }

    public boolean createCatAdmin(String plugId, String login) {
        IMdekCallerSecurity mdekCallerSecurity = MdekCallerSecurity.getInstance();
        IngridDocument catAdminDoc = mdekCallerSecurity.getCatalogAdmin(plugId, "");
        IngridDocument catAdmin = MdekUtils.getResultFromResponse(catAdminDoc);
        
        try {
            UserData userData = new UserData();
            userData.setPortalLogin(login);
            userData.setAddressUuid((String)catAdmin.get(MdekKeys.UUID));
            userData.setPlugId(plugId);

            MdekSecurityUtils.createUserData(userData);

            // Add the proper role to the user
            authProvider.setIgeUser(login);

            return true;

        } catch (MdekException e) {
            // Wrap the MdekException in a RuntimeException so dwr can convert it
            log.debug("MdekException while creating user.", e);
            throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
        }
    }
    
    public boolean removeCatAdmin(String plugId, String login) {
        UserData user = MdekSecurityUtils.getUserDataFromLogin(login);
        if (user != null)
            MdekSecurityUtils.deleteUserDataForAddress(user.getAddressUuid());
        authProvider.removeIgeUser(login);
        
        return true;
    }

    @Override
    public List<String> getAvailableUsers() {
        List<String> allUsers = authProvider.getAllUserIds();
        Map<String, String> existingUsers = MdekSecurityUtils.getAllIgeUserLogins();
        List<String> availableUsers = new ArrayList<String>();
        
        for (String user : allUsers) {
            if (!existingUsers.keySet().contains(user)) {
                availableUsers.add(user);
            }
        }
        return availableUsers;
    }
    
    public Map<String, String> getIgeUsers() {
        return MdekSecurityUtils.getAllIgeUserLogins();
    }
    
    public boolean forceUserLogin(String login) {
        // check if admin is user in session now
        if ("admin".equals(getSessionAttribute("userName"))) {
            // reset session user to login
            setSessionAttribute("userName", login);
            return true;
        }
        return false;        
    }
    
    public boolean isPortalConnected() {
        if (authProvider instanceof PortalAuthenticationProvider) {
            return true;
        }
        return false;
    }
}
