/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.portal.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;

public class IngridPortalSchedulerServlet extends HttpServlet {

    private static final long serialVersionUID = 432942374839749234L;

    private final static Logger log = LoggerFactory.getLogger(IngridPortalSchedulerServlet.class);

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
            
            /*
             * comment out because there is only one quartz instance in the future.
             * See IngridComponentMonitorStartListener.java for initialization.
             * 
             *             try {
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
            log.error("Jetspeed: shutdown() failed: ", e);
        }
    }

    
}
