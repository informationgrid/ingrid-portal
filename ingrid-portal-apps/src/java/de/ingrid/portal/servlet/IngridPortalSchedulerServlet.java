/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

public class IngridPortalSchedulerServlet extends HttpServlet {

    public synchronized final void init(ServletConfig config) throws ServletException {
        synchronized (this.getClass()) {
            super.init(config);
            System.out.println("IngridPortalSchedulerServlet startet");

            try {
                SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
                Scheduler sched = schedFact.getScheduler();
                sched.start();
            } catch (SchedulerException e) {
                // TODO Auto-generated catch block
                System.out.println("Failed to start scheduler." + e.getMessage());
            }
        }
    }

}
