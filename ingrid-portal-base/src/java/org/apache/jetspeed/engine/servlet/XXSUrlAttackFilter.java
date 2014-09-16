/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.apache.jetspeed.engine.servlet;

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

/**
 * Simple XXS Url attack protection blocking access whenever the request url contains a &lt; or &gt; character.
 * @version $Id: XXSUrlAttackFilter.java 1070715 2011-02-15 01:16:24Z woonsan $
 * 
 */
public class XXSUrlAttackFilter implements Filter
{
	private static final Logger LOG = Logger.getLogger(XXSUrlAttackFilter.class);
	
    public void init(FilterConfig config) throws ServletException
    {
        if (LOG.isInfoEnabled()) {
        	LOG.info("XSS: Initializing Jetspeed XXSUrlAttackFilter !");
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            HttpServletRequest hreq = (HttpServletRequest) request;
            if (isInvalid(hreq.getQueryString()) || isInvalid(hreq.getRequestURI()))
            {
               	LOG.warn("XSS: Send response SC_BAD_REQUEST in Jetspeed XXSUrlAttackFilter !");
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isInvalid(String value)
    {
        if (value != null && (value.indexOf('>') != -1 || value.indexOf("%3e") != -1 || value.indexOf("%3E") != -1 || value.indexOf("&gt;") != -1) && (value.indexOf('<') != -1 || value.indexOf("%3c") != -1 || value.indexOf("%3C") != -1 || value.indexOf("&lt;") != -1)) {
            return true;
        } else {
            return false;
        }
    }

    public void destroy()
    {
    }
}
