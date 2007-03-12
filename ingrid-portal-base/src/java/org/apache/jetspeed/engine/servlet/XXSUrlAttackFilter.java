/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the  "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
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

/**
 * Simple XXS Url attack protection blocking access whenever the request url contains a &lt; or &gt; character.
 * @version $Id: XXSUrlAttackFilter.java 513987 2007-03-02 22:06:45Z ate $
 * 
 */
public class XXSUrlAttackFilter implements Filter
{
    public void init(FilterConfig config) throws ServletException
    {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            HttpServletRequest hreq = (HttpServletRequest) request;
            if (isInvalid(hreq.getQueryString()) || isInvalid(hreq.getRequestURI()))
            {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isInvalid(String value)
    {
        return (value != null && (value.indexOf('<') != -1 || value.indexOf('>') != -1 || value.indexOf("%3e") != -1
                || value.indexOf("%3c") != -1 || value.indexOf("%3E") != -1 || value.indexOf("%3E") != -1));
    }

    public void destroy()
    {
    }
}
