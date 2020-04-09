/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.portal.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class HibernateUtil {

    private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory
            if (log.isDebugEnabled()) {
                log.debug("Create hibernate session factory.");
            }
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Exception ex) {
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static final ThreadLocal session = new ThreadLocal();

    public static Session currentSession() {
        Session s = null;
        try {
            s = (Session) session.get();
            if (s == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Session in thread is null, open new session in thread local.");
                }
                s = sessionFactory.openSession();
                session.set(s);
            } else {
                Connection con = s.connection();
                if (con == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Connection of session is null, close existing session, open new session in thread local.");
                    }
                    // close existing session
                    s.close();
                    s = sessionFactory.openSession();
                    session.set(s);
                } else if (con.isClosed()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Connection is closed, close existing session, open new session in thread local.");
                    }
                    // close existing session
                    s.close();
                    s = sessionFactory.openSession();
                    session.set(s);
                }
            }
        } catch (Exception e) {
            log.error("Error getting hibernate session.", e);
        }
        return s;
    }

    public static void closeSession() {
        Session s = (Session) session.get();
        session.set(null);
        if (s != null)
            s.close();
    }

    public static boolean isOracle() {
        boolean isOracle = false;
        Dialect dialect = ((SessionFactoryImplementor) sessionFactory).getDialect();
        
        // check against top OracleDialect class to also check subclasses ! (OracleDialect) 
        if (Oracle9Dialect.class.isAssignableFrom(dialect.getClass())) {
            isOracle = true;
        }

        return isOracle;
	}

    public static boolean isPostgres() {
        boolean isPostgres = false;
        Dialect dialect = ((SessionFactoryImplementor) sessionFactory).getDialect();
        
        if (PostgreSQLDialect.class.isAssignableFrom(dialect.getClass())) {
            isPostgres = true;
        }

        return isPostgres;
    }
}
