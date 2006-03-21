/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;

public class IngridPortalSchedulerServlet extends HttpServlet {

    private final static Log log = LogFactory.getLog(IngridPortalSchedulerServlet.class);
    private final static Log console = LogFactory.getLog("console");

    SchedulerFactory schedFact = null;
    Scheduler sched = null;
    IBUSInterfaceImpl iBusInterface = null;

    
    public synchronized final void init(ServletConfig config) throws ServletException {
        synchronized (this.getClass()) {
            super.init(config);
            System.out.println("IngridPortalSchedulerServlet startet");

            //start ibus communication
            try {
                IBUSInterfaceImpl.getInstance();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Failed to start iBus communication." + e.getMessage());
            }
            
            try {
                schedFact = new org.quartz.impl.StdSchedulerFactory();
                sched = schedFact.getScheduler();
                sched.start();
            } catch (SchedulerException e) {
                // TODO Auto-generated catch block
                System.out.println("Failed to start scheduler." + e.getMessage());
            }
            
        }
    }

    /**
     * The <code>Servlet</code> destroy method.
     */
    public final void destroy()
    {
        try {
            if (sched != null && !sched.isShutdown()) {
                // wait for jobs to complete
                log.info("waiting for scheduler to complete jobs...");
                sched.shutdown(true);
                log.info("waiting for scheduler to complete jobs... done.");
            }
            IBUSInterfaceImpl.resetBus();
            
        } catch (SchedulerException e) {
            log.fatal("Jetspeed: shutdown() failed: ", e);
            System.err.println(ExceptionUtils.getStackTrace(e));
        }
    }
    
    
}
