/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.utils;

import java.util.List;

import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.om.IngridPartner;

/**
 * Global STATIC data and utility methods for Database.
 * 
 * @author Martin Maidhof
 */
public class UtilsDB {

    public static List getPartners() {
        HibernateManager hibernateManager = HibernateManager.getInstance();
        return hibernateManager.loadAllData(IngridPartner.class, 0);
    }
}
