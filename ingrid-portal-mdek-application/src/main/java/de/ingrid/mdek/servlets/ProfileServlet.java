/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.mdek.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.xml.sax.InputSource;

import de.ingrid.mdek.dwr.services.CatalogServiceImpl;
import de.ingrid.utils.ige.profile.ProfileConverter;
import de.ingrid.utils.ige.profile.ProfileMapper;
import de.ingrid.utils.ige.profile.beans.ProfileBean;

public class ProfileServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 2186538892678490988L;

    private PrintWriter out;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            // type and encoding has to be set BEFORE writer is fetched!!!
            response.setContentType("text/javascript");
            response.setCharacterEncoding("UTF-8");
            // DO NOT CACHE THIS CONTENT!!!
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            response.setDateHeader("Expires", 0); // Proxies.
            out = response.getWriter();
        } catch (IOException e) {
            // log.error("Could not open PrintWriter.", e);
            throw new ServletException(e);
        }

        String lang = request.getParameter("lang");
        String debug = request.getParameter("debug");
        if (lang == null ) lang = "en";
        ProfileConverter converter = new ProfileConverter(out, lang);

        // get bean to access backend
        ServletContext context = getServletContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(context);
        CatalogServiceImpl catalog = (CatalogServiceImpl) applicationContext.getBean("catalogService");
        ProfileBean profileBean = null;
        if ("true".equals(debug)) {
        	ProfileMapper pM = new ProfileMapper();
        	InputStream fis = ProfileServlet.class.getResourceAsStream("/profileXmlAdditionalFields.xml");
            profileBean = pM.mapStreamToBean(new InputSource(fis));
        } else
        	profileBean = catalog.getProfileData(request);
        
        out.println("function createAdditionalFieldsDynamically() {");
        //ProfileMapper pM = new ProfileMapper();
        converter.convertProfileBeanToJS(profileBean);
        //out.println("addEmptySpan(\"contentDiv2\");");
        out.println("}");
        
        converter.printVisibilityJSCode(profileBean);

    }
    
}
