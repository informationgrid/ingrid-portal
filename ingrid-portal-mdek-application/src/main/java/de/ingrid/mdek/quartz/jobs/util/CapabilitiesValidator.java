package de.ingrid.mdek.quartz.jobs.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Logger;

import de.ingrid.mdek.quartz.jobs.util.URLState.State;

public class CapabilitiesValidator extends URLValidator {

	private final static Logger log = Logger.getLogger(CapabilitiesValidator.class);	

	public CapabilitiesValidator(HttpClient httpClient, URLState urlState) {
	    super(httpClient, urlState);
	}

	@Override
	public URLState call() {
	    long startTime = System.currentTimeMillis();
	    
	    // do basic tests
	    URLState state = super.call();

	    // if we return a valid page then check for correct content
	    if (state.getState() == State.VALID) {
	        if (log.isDebugEnabled()) {
	            log.debug("checking capability url (extended): "+state.getUrl());
	        }
    		
    		// is Element "Capabilities" present?
    		if (!responseString.contains("<csw:CapabilitiesX")) {
    		    state.setState(State.INVALID_CAPABILITIES);
    		}
    		
    		// TODO: implement more content checks!?
	    }


		long endTime = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
    		log.debug("done after "+(endTime - startTime)+"ms: "+state.getUrl());
    		log.debug("returning: "+state.getState()+"("+state.getResponseCode()+")");
		}
		return state;
	}
}
