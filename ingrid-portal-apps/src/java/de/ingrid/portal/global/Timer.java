/*
 * Copyright (c) 1997-2008 by wemove GmbH
 */
package de.ingrid.portal.global;

/**
 * Helper class dealing with all aspects of iPlugs (PlugDescription).
 *
 * @author joachim@wemove.com
 */
public class Timer {

	private long startTime = 0;
	
	//private long duration = 0;
	
    public void start() {
    	startTime = System.currentTimeMillis();
    }
    
    public long stop() {
    	return System.currentTimeMillis() - startTime;    	
    }
}
