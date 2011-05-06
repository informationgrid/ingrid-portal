package de.ingrid.mdek.util;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.services.persistence.db.IDaoFactory;
import de.ingrid.mdek.services.persistence.db.IEntity;
import de.ingrid.mdek.services.persistence.db.IGenericDao;

public class MdekSecurityUtils {

	private final static Logger log = Logger.getLogger(MdekSecurityUtils.class);

	// Injected by Spring
	private static IDaoFactory daoFactory;


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
		
		
		ServletContext ctx = ses.getServletContext().getContext("/ingrid-portal-mdek");
//		log.debug("last accessed time: "+new Date(ses.getLastAccessedTime()));
//		log.debug("max inactive interval: "+ses.getMaxInactiveInterval());

		String forcedIgeUser = null;
		if (ctx != null) {
			forcedIgeUser = (String) ctx.getAttribute("ige.force.userName");
		}
		
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
				// UserPrincipal and userName not found. Throw an error since we can't determine the user
				throw new RuntimeException("USER_LOGIN_ERROR");
			}
		}
//		return getUserData("Testmdadmin");
//		return getUserData("Testmdadmin2");
//		return getUserData("Testcatadmin");
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
		
		return u == null ? false : true;
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

}
