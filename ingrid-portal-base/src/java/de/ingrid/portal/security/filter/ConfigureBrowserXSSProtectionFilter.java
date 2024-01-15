/*
 * **************************************************-
 * ingrid-portal-utils
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/**
 * OWASP Enterprise Security API (ESAPI)
 *  
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * <a href="http://www.owasp.org/index.php/ESAPI">http://www.owasp.org/index.php/ESAPI</a>.
 *
 * Copyright (c) 2010 - The OWASP Foundation
 *  
 * The ESAPI is published by OWASP under the BSD license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 *  
 * @author     Jeff Williams <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @created    May 27, 2010
 */

package de.ingrid.portal.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * You can enable/disable XSS protection in the browser with the "enable" parameter. You can set the mode to "rewrite"
 * or "block". Rewrite mode tells the browser to rewrite pages suspected of containing XSS. Block mode tells the browser
 * to block all content from the site instead.
 *
 * <pre>
 * Examples:
 * X-XSS-Protection: 0
 * X-XSS-protection: 1; mode=block
 * </pre>
 *
 * <pre>
 *     <filter>
 *            <filter-name>ConfigureBrowserXSSProtectionFilter</filter-name>
 *            <filter-class>org.owasp.filters.ConfigureBrowserXSSProtectionFilter</filter-class>
 *            <init-param>
 *                 <param-name>enable</param-name>
 *                 <param-value>false</param-value>
 *             </init-param>
 *            <init-param>
 *                 <param-name>mode</param-name>
 *                 <param-value>rewrite</param-value>
 *             </init-param>
 *         </filter>
 *          
 *        <filter-mapping>  
 *            <filter-name>ConfigureBrowserXSSProtectionFilter</filter-name>
 *            <url-pattern>/*</url-pattern>
 *        </filter-mapping>
 *
 * </pre>
 */
public class ConfigureBrowserXSSProtectionFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(ConfigureBrowserXSSProtectionFilter.class);
	
    boolean enable = true;
    boolean block = false;

    /**
     * Initialize parameters from web.xml.
     *
     * @param filterConfig
     *            A filter configuration object used by a servlet container to pass information to a filter during
     *            initialization.
     */
    public void init(FilterConfig filterConfig) {
        enable = !filterConfig.getInitParameter("enable").equalsIgnoreCase("false");
        block = filterConfig.getInitParameter("mode").equalsIgnoreCase("block");

        if (LOG.isInfoEnabled()) {
        	LOG.info("Initializing adding response header X-XSS-protection: enable=" + enable + ", block=" + block);
        }
    }

    /**
     * Add X-XSS-protection response header to tell browsers (that decide to implement) how to protect against XSS. For
     * details, please refer to {@link http
     * ://www.h-online.com/security/news/item/Microsoft-to-fix-further-vulnerabilities-in-IE-8-XSS-filter-982864.html}.
     *
     * @param request
     *            The request object.
     * @param response
     *            The response object.
     * @param chain
     *            Refers to the {@code FilterChain} object to pass control to the next {@code Filter}.
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        HttpServletResponse res = (HttpServletResponse) response;

        String value = enable ? "1" : "0";
        if (block) {
            value += "; mode=block";
        }

        res.setHeader("X-XSS-protection", value);
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
    }

}
