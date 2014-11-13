/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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

	public HelpMessage getHelpEntry(Integer guiId, Integer entityClass, String language, String defaultLanguage) {
		IGenericDao<IEntity> dao = daoFactory.getDao(HelpMessage.class);
		HelpMessage sampleMessage = new HelpMessage();
		sampleMessage.setGuiId(guiId);
		if (guiId != null)
			sampleMessage.setEntityClass(entityClass);
		sampleMessage.setLanguage(language);

		dao.beginTransaction();
		HelpMessage helpMessage = (HelpMessage) dao.findUniqueByExample(sampleMessage);
		if (helpMessage == null) {
			// Try to load any helpMessage for the specified guiId
			sampleMessage.setEntityClass(null);
			helpMessage = (HelpMessage) dao.findUniqueByExample(sampleMessage);
		}
		if (helpMessage == null) {
			// Use default language instead of passed locale, also set class again
			sampleMessage.setLanguage(defaultLanguage);
			if (guiId != null)
				sampleMessage.setEntityClass(entityClass);
			helpMessage = (HelpMessage) dao.findUniqueByExample(sampleMessage);
		}
		if (helpMessage == null) {
			// Try to load any helpMessage for the specified guiId (in default language)
			sampleMessage.setEntityClass(null);
			helpMessage = (HelpMessage) dao.findUniqueByExample(sampleMessage);
		}
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
