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
package de.ingrid.portal.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;

public class IngridPortalSchedulerServlet extends HttpServlet {

    private static final long serialVersionUID = 432942374839749234L;

    private static final Logger log = LoggerFactory.getLogger(IngridPortalSchedulerServlet.class);

    private static Scheduler sched = null;

    
    @Override
    public final synchronized void init(ServletConfig config) throws ServletException {
        synchronized (IngridPortalSchedulerServlet.class) {
            super.init(config);
            log.info("IngridPortalSchedulerServlet startet");

            //start ibus communication
            try {
                IBUSInterfaceImpl.getInstance();
            } catch (Exception e) {
                log.error("Failed to start iBus communication.", e);
            }
        }
    }

    
    /**
     * The <code>Servlet</code> destroy method.
     */
    @Override
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
