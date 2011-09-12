/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.SchedulerException;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.scheduler.IngridMonitorFacade;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridComponentMonitorStartListener implements ServletContextListener {

    private final static Logger log = LoggerFactory.getLogger(IngridComponentMonitorStartListener.class);

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        // start ibus communication
        try {
            IBUSInterfaceImpl.getInstance();
        } catch (Exception e) {
            log.error("Failed to start iBus communication.", e);
        }

        // start monitor scheduler
        // this is the only quartz komponent for now
        // TODO: refactor this to be monitor unspecific
        IngridMonitorFacade monitor = IngridMonitorFacade.instance();
        if (monitor == null) {
        	log.error("Failed to start ingrid component monitor component.");
        }
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // start monitor scheduler
        try {
            IngridMonitorFacade.instance().shutdownMonitor(true);
        } catch (SchedulerException e) {
            log.error("Failed to start ingrid monitor scheduler.", e);
        }
    }

}
