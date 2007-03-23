/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import org.quartz.StatefulJob;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public abstract class IngridMonitorAbstractJob implements StatefulJob {
    
    public static int STATUS_OK = 0;
    public static int STATUS_ERROR = 1;
    
    public static String ERROR_NO_HITS = "component.monitor.general.error.no.hits";
    public static String ERROR_UNSPECIFIC = "component.monitor.general.error.unspecific";
    public static String ERROR_TIMEOUT = "component.monitor.general.error.timout";
    public static String NO_ERROR = "component.monitor.general.error.none";

    public static String STATUS = "component.monitor.general.status";
    public static String STATUS_CODE = "component.monitor.general.status.code";
    
    public static String TIMEOUT = "component.monitor.general.timeout";
    public static String LAST_CHECK = "component.monitor.general.last.check";

    public static String QUERY = "component.monitor.general.query";
    
}
