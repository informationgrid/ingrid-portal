package de.ingrid.mdek.util;

import org.apache.log4j.Logger;

import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.services.persistence.db.IDaoFactory;
import de.ingrid.mdek.services.persistence.db.IEntity;
import de.ingrid.mdek.services.persistence.db.IGenericDao;

public class MdekSecurityUtils {

	private final static Logger log = Logger.getLogger(MdekSecurityUtils.class);

	// Injected by Spring
	private static IDaoFactory daoFactory;


	public static UserData getCurrentPortalUserData() {
/*
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest req = wctx.getHttpServletRequest();

		log.debug("Remote user: "+req.getRemoteUser());
		if (req.getUserPrincipal() != null) {
			log.debug("User Principal: "+req.getUserPrincipal().getName());
			return getUserData(req.getUserPrincipal().getName());
		} else {
			throw new RuntimeException("User not logged in.");
		}
*/
		return getUserData("admin");
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
		UserData sampleUser = new UserData();
		sampleUser.setAddressUuid(addressUuid);

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

	
	public static void deleteUserDataForAddress(String addressUuid) {
		IGenericDao<IEntity> dao = daoFactory.getDao(UserData.class);
		UserData sampleUser = new UserData();
		sampleUser.setAddressUuid(addressUuid);

		dao.beginTransaction();
		UserData userData = (UserData) dao.findUniqueByExample(sampleUser);
		dao.makeTransient(userData);
		dao.commitTransaction();
	}


	public IDaoFactory getDaoFactory() {
		return daoFactory;
	}


	public void setDaoFactory(IDaoFactory daoFactory) {
		MdekSecurityUtils.daoFactory = daoFactory;
	}

}
