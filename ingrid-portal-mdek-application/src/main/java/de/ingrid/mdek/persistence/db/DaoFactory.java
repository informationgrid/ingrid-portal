/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.persistence.db;

import org.hibernate.SessionFactory;

import de.ingrid.mdek.persistence.db.model.HelpMessage;
import de.ingrid.mdek.persistence.db.model.RepoUser;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.services.persistence.db.GenericHibernateDao;
import de.ingrid.mdek.services.persistence.db.IDaoFactory;
import de.ingrid.mdek.services.persistence.db.IEntity;
import de.ingrid.mdek.services.persistence.db.IGenericDao;

public class DaoFactory implements IDaoFactory {

    private final SessionFactory sessionFactory;

    DaoFactory(SessionFactory sf) {
        sessionFactory = sf;
    }

    public IGenericDao<IEntity> getDao(Class clazz) {
		IGenericDao dao = null;

		if (clazz.isAssignableFrom(HelpMessage.class)) {
			dao = new GenericHibernateDao<HelpMessage>(sessionFactory, HelpMessage.class);
		} else if (clazz.isAssignableFrom(UserData.class)) {
			dao = new GenericHibernateDao<UserData>(sessionFactory, UserData.class);
		} else if (clazz.isAssignableFrom(RepoUser.class)) {
            dao = new GenericHibernateDao<RepoUser>(sessionFactory, RepoUser.class);
		} else {
			throw new IllegalArgumentException("Unsupported class: " + clazz.getName());
		}

        return dao;
    }
}
