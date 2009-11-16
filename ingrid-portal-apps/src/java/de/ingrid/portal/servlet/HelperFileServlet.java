package de.ingrid.portal.servlet;

import java.io.File;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ingrid.portal.global.UtilsFileHelper;

public class HelperFileServlet extends HttpServlet {
	
	private static final long	serialVersionUID	= -9167034970446594129L;
	
	public void init(ServletConfig servletConfig) throws ServletException {
		
		super.init(servletConfig);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		
		File file = new File(httpServletRequest.getQueryString());
		
		StringBuilder type = new StringBuilder("attachment; filename=");
 		type.append(file.getName());
 				
		// flush it in the response
		httpServletResponse.setHeader("Cache-Control", "no-store");
		httpServletResponse.setHeader("Pragma", "no-cache");
		httpServletResponse.setHeader("Content-Disposition", type.toString());
		httpServletResponse.setDateHeader("Expires", 0);
		httpServletResponse.setContentLength((int) file.length());
 		httpServletResponse.setContentType(new MimetypesFileTypeMap().getContentType(file));
		ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
		responseOutputStream.write(UtilsFileHelper.getBytesFromFile(file));
		responseOutputStream.flush();
		responseOutputStream.close();
	
	}
	
	
	

}
