/*
 * **************************************************-
 * Ingrid Portal Base
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.impl.shibboleth;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.login.filter.PortalRequestWrapper;
import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.UserSubjectPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.log4j.Logger;


public class ShibbolethPortalFilter implements Filter
{
	private static final Logger log = Logger.getLogger(ShibbolethPortalFilter.class);
	protected String userNameHeader;
	protected String roleNameHeader;
	protected String[] adminNameHeader;
	protected String[] roleNamesToMapToAdminPortal;
	
	protected Object sem = new Object();
	

	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain filterChain) throws IOException, ServletException
	{
        ComponentManager cm = Jetspeed.getComponentManager();
		AuthenticationProvider authProvider = (AuthenticationProvider) cm.getComponent("org.apache.jetspeed.security.AuthenticationProvider");
		if (sRequest instanceof HttpServletRequest)
		{
			HttpServletRequest request = (HttpServletRequest) sRequest;
			if (userNameHeader == null)
			{
				synchronized (sem)
				{
					ShibbolethConfiguration config = (ShibbolethConfiguration) cm.getComponent(
							"org.apache.jetspeed.security.shibboleth.ShibbolethConfiguration");
					userNameHeader = config.getHeaderMapping().get(ShibbolethConfiguration.USERNAME);
					roleNameHeader = config.getHeaderMapping().get(ShibbolethConfiguration.ROLENAME);
					adminNameHeader = config.getHeaderMapping().get(ShibbolethConfiguration.ADMINNAME).split(";");
					roleNamesToMapToAdminPortal = config.getHeaderMapping().get(ShibbolethConfiguration.ROLENAMESTOMAP).split(";");
				}
			}
			String username = request.getHeader(userNameHeader);
			
			if (username != null && !username.isEmpty() && isAdminUser(username))
			{
				if (log.isDebugEnabled()) {
					log.debug("username found which will be matched to admin: " + username);
				}
				UserManager userManager = (UserManager) cm.getComponent("org.apache.jetspeed.security.UserManager");
				UserPrincipal admin = new UserPrincipalImpl("admin");
				Subject subject;
				try {
					subject = userManager.getUser("admin").getSubject();
					sRequest = wrapperRequest(request, subject, admin);
				} catch (SecurityException e) {
					log.error("Could not get subject of user 'admin'!", e);
				}				
			} 
			else if (username != null)
			{
				log.debug("username found: " + username);
				
				Subject subject = (Subject) request.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
				if (subject != null)
				{
					Principal principal = getPrincipal(subject, UserSubjectPrincipal.class);
					if (principal != null)
					{
						if (principal.getName().equals(username))
						{
							sRequest = wrapperRequest(request, subject, principal);
							if (filterChain != null)
							{
								filterChain.doFilter(sRequest, sResponse);
								return;
							}
						}
					}
				}
				UserManager userManager = (UserManager) cm.getComponent("org.apache.jetspeed.security.UserManager");
				ShibbolethConfiguration config = (ShibbolethConfiguration) cm.getComponent(
						"org.apache.jetspeed.security.shibboleth.ShibbolethConfiguration");
				boolean success = false;
				if (config.isAuthenticate())
				{
					try
					{
						//!!!authProvider.authenticate(username, username);
						success = true;
					}
					catch (Exception e) //(SecurityException e)
					{
						throw new ServletException();
					}
				}
				else
				{
					try
					{
						// load the user principals (roles, groups, credentials)
						User user = userManager.getUser(username);
						if (user != null)
						{
							subject = user.getSubject();
						}
						success = true;
					}
					catch (SecurityException sex)
					{
						success = false;
						sRequest = wrapperRequest(request, subject, null);
					}
				}
				if (success)
				{
					/*PortalAuthenticationConfiguration authenticationConfiguration = (PortalAuthenticationConfiguration)
							cm.getComponent("org.apache.jetspeed.administration.PortalAuthenticationConfiguration");
					if (authenticationConfiguration.isCreateNewSessionOnLogin())
					{
						request.getSession().invalidate();
					}
					else
					{
                        //UserContentCacheManager userContentCacheManager = (UserContentCacheManager)cm.getComponent("userContentCacheManager");
                        //userContentCacheManager.evictUserContentCache(username, request.getSession().getId());
					}*/
					subject = null;
					try
					{
						// load the user principals (roles, groups, credentials)
						User user = userManager.getUser(username);
						if (user != null)
						{
							subject = user.getSubject();
						}
					}
					catch (SecurityException sex)
					{
					}
					Principal principal = getPrincipal(subject, UserPrincipal.class);
					sRequest = wrapperRequest(request, subject, principal);
					request.getSession().removeAttribute(LoginConstants.ERRORCODE);
					HttpSession session = request.getSession(true);
					session.setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
				}
				else
				{
					request.getSession().setAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_INVALID_PASSWORD);
				}
			}
			else
			{
				if (log.isDebugEnabled()) {
					String header = "";
                    Enumeration headerNames = request.getHeaderNames();
                    while (headerNames.hasMoreElements()) 
                        header += headerNames.nextElement() + ";";
					log.debug("username not found! Header: " + header);
				}
				
				Subject subject = (Subject) request.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
				if (subject != null)
				{
					Principal principal = getPrincipal(subject, UserPrincipal.class);
					ShibbolethConfiguration config = (ShibbolethConfiguration) Jetspeed.getComponentManager().getComponent(
							"org.apache.jetspeed.security.shibboleth.ShibbolethConfiguration");
					if (principal != null && principal.getName().equals(config.getGuestUser()))
					{
					}
					else
					{
						sRequest = wrapperRequest(request, subject, principal);
					}
				}
			}
			sRequest.setAttribute(PortalReservedParameters.PORTAL_FILTER_ATTRIBUTE, "true");
			if (username != null) {
				sRequest.setAttribute("de.ingrid.user.auth.info", username);
				sRequest.setAttribute("de.ingrid.user.auth.isAdminPartner", checkForAdminPortalRole(request));
			}
			if (log.isDebugEnabled()) {
				showRequestHeader(request);
			}
		}

		if (filterChain != null)
		{
			filterChain.doFilter(sRequest, sResponse);
		}
	}

	private void showRequestHeader(HttpServletRequest request) {
		Enumeration headerNames = request.getHeaderNames();
        log.debug("HEADER INFORMATION");
		while (headerNames.hasMoreElements()) { 
            String header = (String) headerNames.nextElement();
            String value = request.getHeader(header);
            log.debug(header + ": " + value);
        }
		
	}

	private Boolean checkForAdminPortalRole(HttpServletRequest request) {
		String rolesInRequest = request.getHeader(roleNameHeader);
		if (rolesInRequest == null) return false; 
		
		for (String role : roleNamesToMapToAdminPortal) {
			if (role.equals(rolesInRequest))
				return true;
		}
		return false;
	}

	private boolean isAdminUser(String username) {
		for (String name: this.adminNameHeader) {
			if (username.equals(name)) return true;
		}
		return false;
	}

	private ServletRequest wrapperRequest(HttpServletRequest request, Subject subject, Principal principal)
	{
		PortalRequestWrapper wrapper = new PortalRequestWrapper(request, subject, principal);
		return wrapper;
	}

	public void destroy()
	{
	}
	
	
	
	/**
     * <p>
     * Given a subject, finds the first principal of the given classe for that subject. If a
     * principal of the given classe is not found, null is returned.
     * </p>
     * 
     * @param subject The subject supplying the principals.
     * @param classe A class or interface derived from java.security.InternalPrincipal.
     * @return The first principal matching a principal classe parameter.
     */
    private static Principal getPrincipal(Subject subject, Class/*<? extends Principal>*/ classe)
    {
        Principal principal = null;
        Set<Principal> principalList = subject.getPrincipals();
        if (principalList != null)
        { 
        	Iterator<Principal> principals = subject.getPrincipals().iterator();
	        while (principals.hasNext())
	        {
	            Principal p = principals.next();
	            if (classe.isInstance(p))
	            {
	                principal = p;
	                break;
	            }
	        }
        }
        return principal;
    }
}