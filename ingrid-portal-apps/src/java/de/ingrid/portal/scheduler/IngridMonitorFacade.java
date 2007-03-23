/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import de.ingrid.portal.servlet.IngridComponentMonitorStartListener;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorFacade {

    private final static Log log = LogFactory.getLog(IngridComponentMonitorStartListener.class);
    
    private static IngridMonitorFacade instance = null;

    private Scheduler scheduler = null;

    private IngridMonitorFacade() throws SchedulerException {
        StdSchedulerFactory sf = new StdSchedulerFactory();
        sf.initialize("quartz.monitor.properties");
        scheduler = sf.getScheduler();
        scheduler.start();
        
        log.info("ingrid component monitor startet.");
    }

    public static synchronized IngridMonitorFacade instance() throws SchedulerException {
        if (instance == null) {
            instance = new IngridMonitorFacade();
        }
        return instance;
    }

    public synchronized void shutdownMonitor(boolean arg) throws SchedulerException {
        if (scheduler != null) {
            scheduler.shutdown(arg);
        }
        log.info("ingrid component monitor shut down.");
    }

}
