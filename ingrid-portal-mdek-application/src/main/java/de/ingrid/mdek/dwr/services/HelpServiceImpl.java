package de.ingrid.mdek.dwr.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.persistence.db.model.HelpMessage;
import de.ingrid.mdek.services.persistence.db.IDaoFactory;
import de.ingrid.mdek.services.persistence.db.IEntity;
import de.ingrid.mdek.services.persistence.db.IGenericDao;


public class HelpServiceImpl {

	private final static Logger log = Logger.getLogger(HelpServiceImpl.class);	

	// Injected by Spring
	private IDaoFactory daoFactory;

	public HelpMessage getHelpEntry(Integer guiId, Integer entityClass) {
		IGenericDao<IEntity> dao = daoFactory.getDao(HelpMessage.class);
		HelpMessage sampleMessage = new HelpMessage();
		sampleMessage.setGuiId(guiId);
		sampleMessage.setEntityClass(entityClass);

		dao.beginTransaction();
		HelpMessage helpMessage = (HelpMessage) dao.findUniqueByExample(sampleMessage);
		dao.commitTransaction();

		return helpMessage;
	}

	public List<HelpMessage> getAllHelpEntries() {
		IGenericDao<IEntity> dao = daoFactory.getDao(HelpMessage.class);

		dao.beginTransaction();
		List<HelpMessage> helpList = (List) dao.findAll();	// Can't cast to List<HelpMessage>
		dao.commitTransaction();

		return new ArrayList(helpList);
	}

	private HelpMessage testPersistHelpMessage(HelpMessage m) {
		try {
			IGenericDao<IEntity> dao = daoFactory.getDao(HelpMessage.class);

			dao.beginTransaction();
			dao.makePersistent(m);
			dao.commitTransaction();

			dao.beginTransaction();
			HelpMessage helpMessage = (HelpMessage) dao.getById(m.getId());
			dao.commitTransaction();

			return helpMessage;

		} catch (Exception e) {
			log.error("Error: ", e);
			return null;
		}		
	}
	
	public IDaoFactory getDaoFactory() {
		return daoFactory;
	}

	public void setDaoFactory(IDaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
}
