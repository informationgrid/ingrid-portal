/*
 * **************************************************-
 * ingrid-portal-utils
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
package de.ingrid.portal.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.ingrid.portal.security.util.XSSUtil;

public class XSSFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(XSSFilter.class);
	
	/** Our helper for security operations ! */
	private XSSUtil xssUtil = new XSSUtil();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (LOG.isInfoEnabled()) {
        	LOG.info("Initializing XSSFilter !");
        }
        
        xssUtil.clear();
        xssUtil.parseFilterConfig(filterConfig);
    }
 
    @Override
    public void destroy() {
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

    	if (request instanceof HttpServletRequest) {
            HttpServletRequest hreq = (HttpServletRequest) request;

    		if (LOG.isDebugEnabled()) {
        		xssUtil.debugRequest(hreq);
    		}

            if (isInvalid(hreq.getQueryString()) || isInvalid(hreq.getRequestURI()))
            {
               	LOG.warn("Send response SC_BAD_REQUEST in XSSFilter !");
            	HttpServletResponse hresp = (HttpServletResponse) response;
            	if (!hresp.isCommitted()) {
                    hresp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            	} else {
            		LOG.warn("Servlet response already committed, we DO NOT send error SC_BAD_REQUEST (cause already checked by other filter !?)");
            	}
            }

            // DO NOT WRAP ! PROBLEMS WITH PORTAL LOGIN AFTERWARDS ! :(((((((((
            // So we can't process POST parameters !!!
//    		request = new XSSRequestWrapper(hreq, xssUtil);
        }

        chain.doFilter(request, response);
    }

    private boolean isInvalid(String value)
    {
    	if (value == null) {
    		return false;
    	}
    	
    	String decodedValue = xssUtil.urlDecode(value);

		if (xssUtil.containsXSS(decodedValue)) {
			return true;
		}
		
		return false;
    }

}
