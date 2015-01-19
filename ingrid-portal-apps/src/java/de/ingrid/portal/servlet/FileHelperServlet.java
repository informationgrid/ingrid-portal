/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ingrid.portal.global.UtilsFileHelper;
import de.ingrid.portal.global.UtilsMimeType;

/**
 * FileHelperServlet prepare files for web.
 * (to display or as download) 
 * 
 * @author ktt
 *
 */
public class FileHelperServlet extends HttpServlet {
	
	private static final long	serialVersionUID	= -9167034970446594129L;
	
	public void init(ServletConfig servletConfig) throws ServletException {
		
		super.init(servletConfig);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		
		File file = new File(httpServletRequest.getSession().getAttribute(httpServletRequest.getQueryString()).toString());
		
		StringBuilder type = new StringBuilder("attachment; filename=");
 		type.append(file.getName());
 				
		// flush it in the response
		httpServletResponse.setHeader("Cache-Control", "no-store");
		httpServletResponse.setHeader("Pragma", "no-cache");
		httpServletResponse.setHeader("Content-Disposition", type.toString());
		httpServletResponse.setDateHeader("Expires", 0);
		httpServletResponse.setContentLength((int) file.length());
 		httpServletResponse.setContentType(UtilsMimeType.getMimeTypByFile(file));
		ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
		responseOutputStream.write(UtilsFileHelper.getBytesFromFile(file));
		responseOutputStream.flush();
		responseOutputStream.close();
	}
}