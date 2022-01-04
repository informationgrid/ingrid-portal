/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.mdek.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.ingrid.mdek.Config;
import de.ingrid.mdek.SpringConfiguration;
import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.security.AuthenticationProvider;
import de.ingrid.mdek.services.persistence.db.IDaoFactory;
import de.ingrid.mdek.services.persistence.db.IEntity;
import de.ingrid.mdek.services.persistence.db.IGenericDao;

@Service
public class MdekSecurityUtils {

	private static final Logger log = Logger.getLogger(MdekSecurityUtils.class);

	// Injected by Spring
	private static IDaoFactory daoFactory;

	// Injected by Spring
	private static AuthenticationProvider authProvider;

	// Injected by Spring
	private static SpringConfiguration springConfig;
	private static Config globalConfig;

	@Autowired
	public MdekSecurityUtils(@Value("#{daoFactory}") IDaoFactory dao, AuthenticationProvider authProvider, SpringConfiguration springConfig) {
	    MdekSecurityUtils.daoFactory = dao;
	    MdekSecurityUtils.authProvider = authProvider;
	    MdekSecurityUtils.springConfig = springConfig;
		MdekSecurityUtils.globalConfig = springConfig.globalConfig();
	}

	public static UserData getCurrentPortalUserData() {
	    return getCurrentPortalUserData(null);
	}
	
	public static UserData getCurrentPortalUserData(HttpServletRequest req) {
		// if we come from an DWR call, we need to get the request
	    HttpSession ses = null;
	    if (req == null) {
		    WebContext wctx = WebContextFactory.get();
		    req = wctx.getHttpServletRequest();
		    ses = wctx.getSession();
		} else {
		    ses = req.getSession();
		}
		
		String forcedIgeUser = authProvider.getForcedUser(req);
		
		// Principal is null after the JetSpeed session times out
		// The session will not be refreshed if the user only operates in the mdek app!
		Principal userPrincipal = req.getUserPrincipal();
		if (userPrincipal != null) {
			// userPrincipal found. return the UserData associated with the userName			
			// if the portal admin wants to access IGE as a different user
			if (log.isDebugEnabled()) {
				log.debug("User is '"+userPrincipal.getName()+"'; forcedIgeUser: "+forcedIgeUser);
				if(req.isUserInRole("admin")) {
					log.debug("User has role admin!");
				} else {
					log.debug("User has not role admin!");
				}
			}
			if (userPrincipal.getName().equals("admin") && forcedIgeUser != null) {
				ses.setAttribute("userName", forcedIgeUser);
				return getUserData(forcedIgeUser);
			} else {
				ses.setAttribute("userName", userPrincipal.getName());
				return getUserData(userPrincipal.getName());
			}
		} else {
			// userPrincipal not found. Check if we have stored the usrName in the session			
			String userName = (String) ses.getAttribute("userName");

			if (userName != null) {
				// userName was stored in the session. Return the UserData obj associated with the userName
				return getUserData(userName);

			} else {

				// Handle requests from users using Shibboleth SSO
				if (globalConfig.useShibboleth) {
					String shibUsername = getShibbolethUsername(req, ses, forcedIgeUser);
					if (shibUsername != null) {
						return getUserData(shibUsername);
					}
				}

				// UserPrincipal and userName not found. Throw an error since we can't determine the user
				throw new RuntimeException("USER_LOGIN_ERROR");
			}
		}
	}

	private static String getShibbolethUsername(HttpServletRequest req, HttpSession ses, String forcedIgeUser) {
		String shibUserNameKey = "shibUserName";
		String previousShibUserName = (String) ses.getAttribute(shibUserNameKey);

		if (previousShibUserName != null && (forcedIgeUser == null || previousShibUserName.equals(forcedIgeUser))) {
			// new request from same user
			return previousShibUserName;
		}

		String header = globalConfig.shibbolethUsernameHeader.trim();
		String uid = req.getHeader(header);
		if (uid != null) {
			uid = uid.trim();

			if (log.isDebugEnabled()) {
				log.debug("User is logged in via Shibboleth. User-ID: " + uid);
			}

			boolean isPortalSuperUser = false;
			String[] superUsers = globalConfig.shibbolethPortalSuperusers.split(";");
			for (int i = 0; i < superUsers.length && !isPortalSuperUser; i++) {
				isPortalSuperUser = isPortalSuperUser || uid.equals(superUsers[i].trim());
			}
			if (isPortalSuperUser && log.isDebugEnabled()) {
				log.debug("Shibboleth user is also portal SuperUser. User-ID: " + uid);
			}

			if (isPortalSuperUser && forcedIgeUser != null) {
				// Portal superuser logging in as different user
				ses.setAttribute(shibUserNameKey, forcedIgeUser);
				return forcedIgeUser;
			} else {
				ses.setAttribute(shibUserNameKey, uid);
				return uid;
			}
		}
		return null;
	}


	public static UserData getUserData(String portalLogin) {
		// Load user data directly from the db via hibernate
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);
		UserData sampleUser = new UserData();
		sampleUser.setPortalLogin(portalLogin);

		dao.beginTransaction();
		UserData userData = (UserData) dao.findUniqueByExample(sampleUser);
		dao.commitTransaction();

		return userData;
	}

	public static UserData getUserDataForAddress(String addressUuid) {
		// Load user data directly from the db via hibernate
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);
		// Find the user via addressUuid and the current iPlug
		UserData sampleUser = new UserData();
		sampleUser.setAddressUuid(addressUuid);
		sampleUser.setPlugId(getCurrentPortalUserData().getPlugId());

		dao.beginTransaction();
		UserData userData = (UserData) dao.findUniqueByExample(sampleUser);
		dao.commitTransaction();

		return userData;
	}
	
	public static int getUserNumForPlugId(String plugId) {
	    // get plugId of this catalog
	    if (plugId == null)
	        plugId = getCurrentPortalUserData().getPlugId();
	    
        // Load user data directly from the db via hibernate
        IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);
        // Find the user via addressUuid and the current iPlug
        UserData sampleUser = new UserData();
        sampleUser.setPlugId(plugId);

        dao.beginTransaction();
        int num = (dao.findByExample(sampleUser)).size();
        dao.commitTransaction();

        return num;
    }

	public static UserData createUserData(UserData userData) {
		// Write the data directly to the db via hibernate
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);

		dao.beginTransaction();
		dao.makePersistent(userData);
		dao.commitTransaction();

		return userData;
	}

	public static boolean portalLoginExists(String login) {
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);

		UserData sampleUser = new UserData();
		sampleUser.setPortalLogin(login);

		dao.beginTransaction();
		UserData u = (UserData) dao.findUniqueByExample(sampleUser);
		
		return u != null;
	}
	
	public static String userFromAddress(String address) {
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);

		UserData sampleUser = new UserData();
		sampleUser.setAddressUuid(address);

		dao.beginTransaction();
		UserData u = (UserData) dao.findUniqueByExample(sampleUser);
		dao.commitTransaction();
		
		return u == null ? null : u.getPortalLogin();
	}
	
	public static UserData storeUserData(String oldUserLogin, UserData userData) {
		// Update the data in the db via hibernate
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);

		UserData sampleUser = new UserData();
		sampleUser.setPortalLogin(oldUserLogin);

		dao.beginTransaction();
		UserData u = (UserData) dao.findUniqueByExample(sampleUser);
		
		// there always should be returned a user entry!
		if (u == null) {
		    log.error("OldUserLogin "+oldUserLogin+" not found in database!");
		} else {
			u.setPortalLogin(userData.getPortalLogin());
			u.setAddressUuid(userData.getAddressUuid());
			dao.makePersistent(u);
		}
		dao.commitTransaction();

		return userData;
	}

	
	public static UserData deleteUserDataForAddress(String addressUuid) {
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);
		UserData sampleUser = new UserData();
		sampleUser.setAddressUuid(addressUuid);

		dao.beginTransaction();
		UserData userData = (UserData) dao.findUniqueByExample(sampleUser);
		dao.makeTransient(userData);
		dao.commitTransaction();

		return userData;
	}


	public IDaoFactory getDaoFactory() {
		return daoFactory;
	}


	public void setDaoFactory(IDaoFactory daoFactory) {
		MdekSecurityUtils.daoFactory = daoFactory;
	}


	public static String getCurrentUserUuid() {
		return getCurrentPortalUserData().getAddressUuid();
	}
	
	public static String getCurrentUserUuid(HttpServletRequest req) {
        return getCurrentPortalUserData(req).getAddressUuid();
    }

	public static String getHash(String password) {
        byte[] input = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            input = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Could not create hash from password!", e);
        }
        return String.valueOf(new String(input).hashCode());
	}
	
    public void setAuthProvider(AuthenticationProvider authProvider) {
        MdekSecurityUtils.authProvider = authProvider;
    }

    public AuthenticationProvider getAuthProvider() {
        return authProvider;
    }

    public static String getLoginFromUuidAndIPlug(String uuid, String plugid) {
        // Load user data directly from the db via hibernate
        IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);
        UserData sampleUser = new UserData();
        sampleUser.setAddressUuid(uuid);
        sampleUser.setPlugId(plugid);
        
        dao.beginTransaction();
        UserData userData = (UserData) dao.findUniqueByExample(sampleUser);
        dao.commitTransaction();

        if (userData != null)
            return userData.getPortalLogin();
        
        return null;
    }
    
    public static UserData getUserDataFromLogin(String login) {
        // Load user data directly from the db via hibernate
        IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);
        UserData sampleUser = new UserData();
        sampleUser.setPortalLogin(login);
        
        dao.beginTransaction();
        UserData userData = (UserData) dao.findUniqueByExample(sampleUser);
        dao.commitTransaction();

        return userData;
    }
    
    public static Map<String, String> getAllIgeUserLogins() {
        IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);

        dao.beginTransaction();
        List<UserData> userList = (List) dao.findAll();  // Can't cast to List<RepoUser>
        dao.commitTransaction();
        
        Map<String, String> users = new HashMap<>();
        for (UserData user : userList) {
            users.put(user.getPortalLogin(), user.getPlugId());
        }

        return users;
    }
}
