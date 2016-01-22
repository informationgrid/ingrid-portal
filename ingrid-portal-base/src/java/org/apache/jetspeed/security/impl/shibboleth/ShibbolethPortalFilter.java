/*
 * **************************************************-
 * InGrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserSubjectPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShibbolethPortalFilter implements Filter
{
    private static final Logger log = LoggerFactory.getLogger(ShibbolethPortalFilter.class);
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
		if (sRequest instanceof HttpServletRequest)
		{
			HttpServletRequest request = (HttpServletRequest) sRequest;
			if (userNameHeader == null)
			{
				synchronized (sem)
				{
					ShibbolethConfiguration config = cm.lookupComponent("org.apache.jetspeed.security.shibboleth.ShibbolethConfiguration");
					userNameHeader = config.getHeaderMapping().get(ShibbolethConfiguration.USERNAME);
					roleNameHeader = config.getHeaderMapping().get(ShibbolethConfiguration.ROLENAME);
					adminNameHeader = config.getHeaderMapping().get(ShibbolethConfiguration.ADMINNAME).split(";");
					roleNamesToMapToAdminPortal = config.getHeaderMapping().get(ShibbolethConfiguration.ROLENAMESTOMAP).split(";");
				}
			}
			String username = request.getHeader(userNameHeader);
			boolean createAccount = false;

			if (username != null && username.trim().isEmpty()) {
			    username = null;
			}
            if (username != null)
            {
			    if (isAdminUser(username))
	            {
	                if (log.isDebugEnabled()) {
	                    log.debug("username found which will be matched to admin: " + username);
	                }
	                UserManager userManager = cm.lookupComponent("org.apache.jetspeed.security.UserManager");
	                try {
	                    User admin = userManager.getUser("admin");
	                    Subject subject = userManager.getSubject(admin);
	                    sRequest = wrapperRequest(request, subject, admin);
	                } catch (SecurityException e) {
	                    log.error("Could not get subject of user 'admin'!", e);
	                }
	            }
			    else {
	                if (log.isDebugEnabled()) {
	                    log.debug("username found: " + username);
	                }
	                
	                Subject subject = (Subject) request.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
	                if (subject != null)
	                {
	                    Principal principal = SubjectHelper.getPrincipal(subject, UserSubjectPrincipal.class);
	                    if (principal != null)
	                    {
	                        if (principal.getName().equals(username))
	                        {
	                            sRequest = wrapperRequest(request, subject, principal);
                                // do not forget to set user attributes indicating external login (SSO) for adapting view templates !
//	                            log.debug("setUserAttributes PRINCIPAL IN SESSION");
	                            sRequest.setAttribute(PortalReservedParameters.PORTAL_FILTER_ATTRIBUTE, "true");
                                setUserAttributes(sRequest, username, checkForAdminPortalRole(request), createAccount);

	                            if (filterChain != null)
	                            {
//                                  log.debug("\n\nfilterChain.doFilter after PRINCIPAL IN SESSION");
	                                filterChain.doFilter(sRequest, sResponse);
//                                  log.debug("\n\nreturn after PRINCIPAL IN SESSION");
	                                return;
	                            }
	                        }
	                    }
	                }
	                UserManager userManager = cm.lookupComponent("org.apache.jetspeed.security.UserManager");

	                // check whether user exists ! If not activate create account functionality in MyPortalLoginPortlet !
                    try {
                        userManager.getUser(username);
                    } catch (SecurityException sex) {
                        createAccount = true;
                    }

	                ShibbolethConfiguration config = cm.lookupComponent(
	                        "org.apache.jetspeed.security.shibboleth.ShibbolethConfiguration");
	                boolean success = false;
	                if (config.isAuthenticate())
	                {
	                    try
	                    {
	                        // wemove: NO authentification, already authenticated via SSO
	                        //!!! authProvider.authenticate(username, username);
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
	                            subject = userManager.getSubject(user);
	                        }
	                        success = true;
	                    }
	                    catch (SecurityException sex)
	                    {
	                        success = false;
//                            log.debug("\n\nwrapperRequest(request, subject, null)");
	                        sRequest = wrapperRequest(request, subject, null);
	                    }
	                }
	                if (success)
	                {
	                    /*PortalAuthenticationConfiguration authenticationConfiguration =
	                            cm.lookupComponent("org.apache.jetspeed.administration.PortalAuthenticationConfiguration");
	                    if (authenticationConfiguration.isCreateNewSessionOnLogin() && httpSession != null && !httpSession.isNew())
	                    {
	                        request.getSession().invalidate();
	                    }
	                    else
	                    {
	                        UserContentCacheManager userContentCacheManager = cm.lookupComponent("userContentCacheManager");
	                        userContentCacheManager.evictUserContentCache(username, request.getSession().getId());
	                    }*/
	                    subject = null;
	                    try
	                    {
	                        // load the user principals (roles, groups, credentials)
	                        User user = userManager.getUser(username);
	                        if (user != null)
	                        {
	                            subject = userManager.getSubject(user);
                            }
	                    }
	                    catch (SecurityException sex)
	                    {
	                    }
	                    Principal principal = SubjectHelper.getPrincipal(subject, User.class);
//                        log.debug("\n\nwrapperRequest(request, subject, principal)");
	                    sRequest = wrapperRequest(request, subject, principal);
	                    request.getSession().removeAttribute(LoginConstants.ERRORCODE);
	                    HttpSession session = request.getSession(true);
	                    session.setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
	                }
	                else
	                {
//                        log.debug("\n\nERROR_INVALID_PASSWORD)");
	                    request.getSession().setAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_INVALID_PASSWORD);
	                }
			    }
			}
			else
			{
				if (log.isDebugEnabled()) {
					log.debug("username not found!!!");
				}
				Subject subject = (Subject) request.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
				if (subject != null)
				{
					Principal principal = SubjectHelper.getPrincipal(subject, User.class);
					ShibbolethConfiguration config = Jetspeed.getComponentManager().lookupComponent(
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
			    // set user attributes indicating external login (SSO)
//                log.debug("setUserAttributes AM ENDE");
			    setUserAttributes(sRequest, username, checkForAdminPortalRole(request), createAccount);
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

    /** Set SSO data in request ! Identifies SSO login (external) and is checked
     * in MyPortalLoginPortlet and myportal_overview and myportal_navigation
     * templates to adapt views !
     * @param sRequest set user attributes on this request
     * @param username the SSO login
     * @param isPortalAdmin set true if user has role "admin-portal"
     * @param createAccount pass true if new account should be created
     */
    private void setUserAttributes(ServletRequest sRequest, String username, Boolean isPortalAdmin, boolean createAccount) {
        sRequest.setAttribute("de.ingrid.user.auth.info", username);
        sRequest.setAttribute("de.ingrid.user.auth.isAdminPartner", isPortalAdmin);
        sRequest.setAttribute("de.ingrid.user.auth.createAccount", createAccount);            
        
        if (log.isDebugEnabled()) {
            log.debug( "Shibboleth request attributes set: "
                    + "\nde.ingrid.user.auth.info = " + username
                    + "\nde.ingrid.user.auth.isAdminPartner (PortalAdmin) = " + isPortalAdmin 
                    + "\nde.ingrid.user.auth.createAccount = " + createAccount); 
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
}
