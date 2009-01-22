package de.ingrid.mdek.util;

import java.security.Principal;
import java.util.Date;

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
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest req = wctx.getHttpServletRequest();

		HttpSession ses = wctx.getSession();
//		log.debug("last accessed time: "+new Date(ses.getLastAccessedTime()));
//		log.debug("max inactive interval: "+ses.getMaxInactiveInterval());

		Principal userPrincipal = req.getUserPrincipal(); 
		if (userPrincipal != null) {
			// userPrincipal found. return the UserData associated with the userName			
			ses.setAttribute("userName", userPrincipal.getName());
			return getUserData(userPrincipal.getName());

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

	public static UserData createUserData(UserData userData) {
		// Write the data directly to the db via hibernate
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);

		dao.beginTransaction();
		dao.makePersistent(userData);
		dao.commitTransaction();

		return userData;
	}

	public static UserData storeUserData(UserData userData) {
		// Update the data in the db via hibernate
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);

		UserData sampleUser = new UserData();
		sampleUser.setPortalLogin(userData.getPortalLogin());

		dao.beginTransaction();
		UserData u = (UserData) dao.findUniqueByExample(sampleUser);
		u.setAddressUuid(userData.getAddressUuid());
		dao.makePersistent(u);
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

}
