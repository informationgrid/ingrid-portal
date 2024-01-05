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
package de.ingrid.portal.security.filter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import de.ingrid.portal.security.util.XSSUtil;
 
/** Our request wrapper subclassing get methods for stripping of malicous
 * content in parameter, header ...
 * This way we can process all content (also POST values).
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

	/** Our helper for security operations ! */
	private XSSUtil xssUtil = null;

    /** Our Wrapper stripping the parameter values !
     * Pass XSSUtil instance with configuration.
     * @param servletRequest original request
     * @param xssUtil configuration, e.g. from FilterConfig etc.
     */
    public XSSRequestWrapper(HttpServletRequest servletRequest, XSSUtil xssUtil) {
        super(servletRequest);
        
        this.xssUtil = xssUtil;
    }

    @Override
    public String getParameter(String parameter) {
        String origValue = super.getParameter(parameter);
    	return xssUtil.stripParameter(origValue, parameter);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] origValues = super.getParameterValues(parameter);
    	return xssUtil.stripParameterValues(origValues, parameter);
    }
 
    @Override
	public Map getParameterMap() {
        Map origMap = super.getParameterMap();
    	return xssUtil.stripParameterMap(origMap);
    }

    @Override
    public String getHeader(String name) {
        String origValue = super.getHeader(name);
    	return xssUtil.stripHeader(origValue, name);
    }
}
