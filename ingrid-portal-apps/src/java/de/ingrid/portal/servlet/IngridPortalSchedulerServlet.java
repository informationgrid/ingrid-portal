/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;

public class IngridPortalSchedulerServlet extends HttpServlet {

    private static final long serialVersionUID = 432942374839749234L;

    private final static Log log = LogFactory.getLog(IngridPortalSchedulerServlet.class);

    Scheduler sched = null;

    
    public synchronized final void init(ServletConfig config) throws ServletException {
        synchronized (this.getClass()) {
            super.init(config);
            log.info("IngridPortalSchedulerServlet startet");

            //start ibus communication
            try {
                IBUSInterfaceImpl.getInstance();
            } catch (Exception e) {
                log.error("Failed to start iBus communication.", e);
            }
            
            /*            try {
                StdSchedulerFactory sf = new StdSchedulerFactory();
                sf.initialize("quartz.properties");
                sched = sf.getScheduler();
                sched.start();
                
            } catch (SchedulerException e) {
                log.error("Failed to start scheduler.", e);
            }
            */
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
            IBUSInterfaceImpl.shutdown();
            
        } catch (SchedulerException e) {
            log.fatal("Jetspeed: shutdown() failed: ", e);
        }
    }

    
}
