/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.portalsite.impl;

import java.security.AccessController;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageManagerEventListener;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.portalsite.Menu;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;
import org.apache.jetspeed.portalsite.view.SiteView;
import org.apache.jetspeed.portalsite.view.SiteViewMenuDefinitionLocator;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfileLocatorProperty;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * This class encapsulates managed session state for and
 * interface to the portal-site component and subscribes
 * to page manager and session events to flush stale state
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: PortalSiteSessionContextImpl.java 359123 2005-12-26 23:06:00Z rwatler $
 */
public class PortalSiteSessionContextImpl implements PortalSiteSessionContext, PageManagerEventListener, HttpSessionActivationListener, HttpSessionBindingListener
{
    /**
     * log - logging instance
     */
    private final static Log log = LogFactory.getLog(PortalSiteSessionContextImpl.class);

    /**
     * pageManager - PageManager component
     */
    private PageManager pageManager;

    /**
     * profileLocators - map of session profile locators by locator names
     */
    private Map profileLocators;

    /**
     * userPrincipal - session user principal
     */
    private String userPrincipal;

    /**
     * siteView - session site view
     */
    private SiteView siteView;

    /**
     * folderPageHistory - map of last page visited by folder 
     */
    private Map folderPageHistory;

    /**
     * menuDefinitionLocatorCache - cached menu definition locators for
     *                              absolute menus valid for session
     */
    private Map menuDefinitionLocatorCache;

    /**
     * subscribed - flag that indicates whether this context
     *              is subscribed as event listeners
     */
    private boolean subscribed;

    /**
     * stale - flag that indicates whether the state
     *         managed by this context is stale
     */
    private boolean stale;

    /**
     * PortalSiteSessionContextImpl - constructor
     *
     * @param pageManager PageManager component instance
     */
    public PortalSiteSessionContextImpl(PageManager pageManager)
    {
        this.pageManager = pageManager;
        this.folderPageHistory = new HashMap();
    }

    /**
     * newRequestContext - create a new request context instance
     *
     * @param requestProfileLocators request profile locators
     * @return new request context instance
     */
    public PortalSiteRequestContext newRequestContext(Map requestProfileLocators)
    {
        // TODO - potentially cache N request contexts and reuse
        return new PortalSiteRequestContextImpl(this, requestProfileLocators);
    }

    /**
     * selectRequestPage - select page proxy for request given profile locators
     *
     * @param requestProfileLocators map of profile locators for request
     * @return selected page proxy for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Page selectRequestPage(Map requestProfileLocators) throws NodeNotFoundException
    {
        // validate and update session profile locators if modified
        if (updateSessionProfileLocators(requestProfileLocators))
        {
            // extract page request path from the locators
            String requestPath = Folder.PATH_SEPARATOR;
            ProfileLocator locator = (ProfileLocator)requestProfileLocators.get(ProfileLocator.PAGE_LOCATOR);
            if (locator != null)
            {
                // use 'page' locator to determine request page by executing
                // profile locator to determine path
                requestPath = getRequestPathFromLocator(locator);
            }
            else
            {
                // 'page' locator unavailable, use first locator since
                // all locators should have identical request paths, (do
                // not execute profile locator though to determine path:
                // simply use the request path)
                locator = (ProfileLocator)requestProfileLocators.values().iterator().next();
                requestPath = locator.getRequestPath();
            }
            
            // attempt to select request page or folder using
            // profile locators and site view
            Page requestPage = null;
            try
            {
                return selectRequestPage(requestPath);
            }
            catch (NodeNotFoundException nnfe)
            {
                if (requestPath.equals(Folder.PATH_SEPARATOR))
                {
                    throw nnfe;
                }
            }
            catch (SecurityException se)
            {
                if (requestPath.equals(Folder.PATH_SEPARATOR))
                {
                    throw se;
                }
            }
            
            // if no matched page or folder, fallback to request of
            // default page in root folder in page locator
            return selectRequestPage(Folder.PATH_SEPARATOR);
        }

        // no request page available
        throw new NodeNotFoundException("No request page available in site view.");
    }

    /**
     * getRequestPathFromLocator - execute profile locator to extract
     *                             request path using locator rules; this
     *                             is request specific and is not part of
     *                             the site view
     *
     * @param locator profile locator to execute
     * @return request path from profile locator
     */
    private String getRequestPathFromLocator(ProfileLocator locator)
    {
        // use profile iterator to process the initial full
        // set of profile locator properties searching for
        // the first non control/navigation, (i.e. page/path),
        // property that will force the request path if
        // non-null; otherwise default to locator request path
        String requestPath = locator.getRequestPath();
        Iterator locatorIter = locator.iterator();
        if (locatorIter.hasNext())
        {
            ProfileLocatorProperty [] properties = (ProfileLocatorProperty []) locatorIter.next();
            for (int i = 0; (i < properties.length); i++)
            {
                if (!properties[i].isControl() && !properties[i].isNavigation())
                {
                    // request page/path property; append to or replace
                    // using locator specified path
                    String path = properties[i].getValue();
                    if (path != null)
                    {
                        // specified page/path to be appended to request path if
                        // relative; otherwise specified page/path to replace
                        // request path
                        if (!path.startsWith(Folder.PATH_SEPARATOR))
                        {
                            // strip page from request path if required
                            // and append page/path to base request path
                            String basePath = requestPath;
                            if (basePath == null)
                            {
                                basePath = Folder.PATH_SEPARATOR;
                            }
                            else if (basePath.endsWith(Page.DOCUMENT_TYPE))
                            {
                                basePath = basePath.substring(0, basePath.lastIndexOf(Folder.PATH_SEPARATOR)+1);
                            }
                            else if (!basePath.endsWith(Folder.PATH_SEPARATOR))
                            {
                                basePath += Folder.PATH_SEPARATOR;
                            }
                            path = basePath + path;

                            // make sure path ends in page extension
                            // if folder not explicitly specified
                            if (!path.endsWith(Folder.PATH_SEPARATOR) && !path.endsWith(Page.DOCUMENT_TYPE))
                            {
                                path += Page.DOCUMENT_TYPE;
                            }
                        }

                        // detect profile locator request path modification
                        if (!path.equals(requestPath))
                        {
                            // if modified request path ends with default page,
                            // strip default page from path to allow folder level
                            // defaulting to take place: locator should not force
                            // selection of default page when selection of the
                            // folder is implied by use in locator page/path
                            if (path.endsWith(Folder.PATH_SEPARATOR + Folder.FALLBACK_DEFAULT_PAGE))
                            {
                                path = path.substring(0, path.length() - Folder.FALLBACK_DEFAULT_PAGE.length());
                            }
                            
                            // log modified page request
                            if (log.isDebugEnabled() && !path.equals(requestPath))
                            {
                                log.debug("Request page modified by profile locator: request path=" + path);
                            }
                        }
                        return path;
                    }
                    else
                    {
                        // interpret null path as using default
                        // locator request path
                        return requestPath;
                    }
                }
            }
        }

        // return locator request path
        return requestPath;
    }

    /**
     * selectRequestPage - select page proxy for request for specified
     *                     path given profile locators and site view
     *                     associated with this context
     *
     * @param requestPath request path
     * @return selected page proxy for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    private Page selectRequestPage(String requestPath) throws NodeNotFoundException
    {
        // save access exceptions
        SecurityException accessException = null;

        // valid SiteView required from session profile locators
        SiteView view = getSiteView();
        if (view != null)
        {
            // default request to root folder if not specified
            if (requestPath == null)
            {
                requestPath = Folder.PATH_SEPARATOR;
            }
            
            // log page request
            if (log.isDebugEnabled())
            {
                log.debug("Request page: request path=" + requestPath);
            }

            // lookup request path in view for viewable page or folder
            // nodes; note: directly requested pages/folders may be hidden
            // or not viewable
            Node requestNode = null;
            try
            {
                // try page or folder request url
                requestNode = view.getNodeProxy(requestPath, null, false, false);
            }
            catch (NodeNotFoundException nnfe)
            {
                // if request path ends with default page, strip from
                // request url to retry for folder default
                if (requestPath.endsWith(Folder.PATH_SEPARATOR + Folder.FALLBACK_DEFAULT_PAGE))
                {
                    // retry folder request url
                    requestPath = requestPath.substring(0, requestPath.length() - Folder.FALLBACK_DEFAULT_PAGE.length());
                    requestNode = view.getNodeProxy(requestPath, null, true, false);
                }
                else
                {
                    // rethrow original exception
                    throw nnfe;
                }
            }
            
            // invoke default page logic to determine folder page
            if (requestNode instanceof Folder)
            {
                Folder requestFolder = (Folder)requestNode;
                
                // support subfolders specified as default pages;
                // find highest subfolder with a default page that
                // specifies a default folder, (not a default page).
                try
                {
                    String defaultFolderName = requestFolder.getDefaultPage();
                    if (defaultFolderName != null)
                    {
                        // do not follow broken default folders
                        Folder defaultRequestFolder = requestFolder;
                        // follow default folders to parent folders
                        while ((defaultRequestFolder != null) && (defaultFolderName != null) &&
                               defaultFolderName.equals(".."))
                        {
                            defaultRequestFolder = (Folder)defaultRequestFolder.getParent();
                            if (defaultRequestFolder != null)
                            {
                                defaultFolderName = defaultRequestFolder.getDefaultPage();
                            }
                            else
                            {
                                defaultFolderName = null;
                            }
                        }
                        // follow default folders to subfolders
                        while ((defaultRequestFolder != null) && (defaultFolderName != null) &&
                               !defaultFolderName.endsWith(Page.DOCUMENT_TYPE) && !defaultFolderName.equals(".."))
                        {
                            defaultRequestFolder = defaultRequestFolder.getFolder(defaultFolderName);
                            defaultFolderName = defaultRequestFolder.getDefaultPage();
                        }
                        // use default request folder
                        if (defaultRequestFolder != null)
                        {
                            requestFolder = defaultRequestFolder;
                        }
                    }
                }
                catch (NodeException ne)
                {
                }
                catch (SecurityException se)
                {
                    requestFolder = null;
                    accessException = se;
                }

                // only request folders with pages can be
                // selected by request; otherwise, fall back to
                // parent folders assuming that immediate parents
                // will have the most appropriate default page
                NodeSet requestFolderPages = null;
                if (requestFolder != null)
                {
                    try
                    {
                        requestFolderPages = requestFolder.getPages();
                        while (((requestFolderPages == null) || requestFolderPages.isEmpty()) && (requestFolder.getParent() != null))
                        {
                            requestFolder = (Folder)requestFolder.getParent();
                            requestFolderPages = requestFolder.getPages();
                        }
                    }
                    catch (NodeException ne)
                    {
                        requestFolderPages = null;
                    }
                    catch (SecurityException se)
                    {
                        requestFolderPages = null;
                        accessException = se;
                    }
                }
                if ((requestFolder != null) && (requestFolderPages != null) && !requestFolderPages.isEmpty())
                {
                    // attempt to lookup last visited page by folder proxy
                    // path, (proxies are hashed by their path), contains
                    // test must be performed since identical paths may
                    // occur in multiple site views
                    Page requestPage = (Page)folderPageHistory.get(requestFolder);
                    
                    if ((requestPage != null) && requestFolderPages.contains(requestPage))
                    {
                        // log selected request page
                        if (log.isDebugEnabled())
                        {
                            log.debug("Selected folder historical page: path=" + view.getManagedPage(requestPage).getPath());
                        }
                        return requestPage;
                    }
                    
                    // get default page for folder proxy if more than one
                    // page is available to choose from
                    if (requestFolderPages.size() > 1)
                    {
                        String defaultPageName = requestFolder.getDefaultPage();
                        if (defaultPageName == null)
                        {
                            // use fallback default if default page
                            // not explicitly specified
                            defaultPageName = Folder.FALLBACK_DEFAULT_PAGE;
                        }
                        try
                        {
                            // save last visited non-hidden page for folder proxy
                            // path, (proxies are hashed by their path), and
                            // return default page
                            requestPage = requestFolder.getPage(defaultPageName);
                            if (!requestPage.isHidden())
                            {
                                folderPageHistory.put(requestFolder, requestPage);
                            }
                            
                            // log selected request page
                            if (log.isDebugEnabled())
                            {
                                log.debug("Selected folder default page: path=" + view.getManagedPage(requestPage).getPath());
                            }
                            return requestPage;
                        }
                        catch (NodeException ne)
                        {
                        }
                        catch (SecurityException se)
                        {
                            accessException = se;
                        }
                    }
                    
                    // default page not available, select first page
                    // proxy in request folder; save last visited
                    // non-hidden page for folder proxy path, (proxies
                    // are hashed by their path), and return default page
                    requestPage = (Page)requestFolderPages.iterator().next();
                    if (!requestPage.isHidden())
                    {
                        folderPageHistory.put(requestFolder, requestPage);
                    }

                    // log selected request page
                    if (log.isDebugEnabled())
                    {
                        log.debug("Selected first folder page, path=" + view.getManagedPage(requestPage).getPath());
                    }
                    return requestPage;
                }
            }
            else if (requestNode instanceof Page)
            {
                Page requestPage = (Page)requestNode;
                
                // save last visited non-hidden page for folder proxy
                // path, (proxies are hashed by their path), and
                // return matched page
                Folder requestFolder = (Folder)requestPage.getParent();
                if (!requestPage.isHidden())
                {
                    folderPageHistory.put(requestFolder, requestPage);
                }

                // log selected request page
                if (log.isDebugEnabled())
                {
                    log.debug("Selected page, path=" + view.getManagedPage(requestPage).getPath());
                }
                return requestPage;
            }
        }
            
        // no page matched or accessible
        if (accessException != null)
        {
            throw accessException;
        }
        throw new NodeNotFoundException("No page matched " + requestPath + " request in site view.");
    }
    
    /**
     * getRequestRootFolder - select root folder proxy for given profile locators
     *
     * @param requestProfileLocators map of profile locators for request
     * @return root folder proxy for request
     * @throws NodeNotFoundException if not found
     * @throws SecurityException if view access not granted
     */
    public Folder getRequestRootFolder(Map requestProfileLocators) throws NodeNotFoundException
    {
        // validate and update session profile locators if modified
        if (updateSessionProfileLocators(requestProfileLocators))
        {
            // valid site view required from session profile locators
            SiteView view = getSiteView();
            if (view != null)
            {
                // return root folder proxy from session site view
                return view.getRootFolderProxy();
            }
        }

        // no root folder available
        throw new NodeNotFoundException("No root folder available in site view.");
    }

    /**
     * updateSessionProfileLocators - detect modification of and update cached
     *                                session profile locators
     *
     * @param requestProfileLocators map of profile locators for request
     * @return profile locators validation flag
     */
    private boolean updateSessionProfileLocators(Map requestProfileLocators)
    {
        // request profile locators are required
        if ((requestProfileLocators != null) && !requestProfileLocators.isEmpty())
        {
            // get current user principal; ignore derivative
            // changes in role and group principals
            String currentUserPrincipal = null;
            Subject subject = Subject.getSubject(AccessController.getContext());
            if (subject != null)
            {
                Iterator principals = subject.getPrincipals().iterator();
                while (principals.hasNext())
                {
                    Principal principal = (Principal) principals.next();
                    if (principal instanceof UserPrincipal)
                    {
                        if (currentUserPrincipal == null)
                        {
                            currentUserPrincipal = principal.getName();
                        }
                        else
                        {
                            currentUserPrincipal += "|" + principal.getName();
                        }
                    }
                }
            }

            // detect stale session, modification of user
            // principal, or changed profile locators for
            // this session context
            boolean userUpdate = false;
            boolean locatorsUpdate = false;
            boolean updated = false;
            synchronized (this)
            {
                userUpdate = (((userPrincipal == null) && (currentUserPrincipal != null)) ||
                              ((userPrincipal != null) && !userPrincipal.equals(currentUserPrincipal)));
                locatorsUpdate = ((profileLocators == null) ||
                                  !locatorsEquals(profileLocators, requestProfileLocators));
                if (stale || userUpdate || locatorsUpdate)
                {
                    // reset cached session profile locators, view,
                    // folder page history, menu definition locators,
                    // and stale flag
                    clearSessionProfileLocators();
                    profileLocators = requestProfileLocators;
                    userPrincipal = currentUserPrincipal;
                    updated = true;
                }
            }

            // log session context setup and update
            if (updated && log.isDebugEnabled())
            {
                StringBuffer debug = new StringBuffer();
                if (userUpdate)
                {
                    debug.append("Updated user");
                    if (locatorsUpdate)
                    {
                        debug.append("/locators");
                    }
                    if (stale)
                    {
                        debug.append("/stale");
                    }
                }
                else if (locatorsUpdate)
                {
                    debug.append("Updated locators");
                    if (stale)
                    {
                        debug.append("/stale");
                    }
                }
                else
                {
                    debug.append("Updated stale");
                }
                debug.append(" context: user=" + userPrincipal + ", profileLocators=(");
                if (profileLocators != null)
                {
                    boolean firstEntry = true;
                    Iterator entriesIter = profileLocators.entrySet().iterator();
                    while (entriesIter.hasNext())
                    {
                        Map.Entry entry = (Map.Entry)entriesIter.next();
                        String locatorName = (String)entry.getKey();
                        ProfileLocator locator = (ProfileLocator)entry.getValue();
                        if (!firstEntry)
                        {
                            debug.append(",");
                        }
                        else
                        {
                            firstEntry = false;
                        }
                        debug.append(locatorName);
                        debug.append("=");
                        debug.append(locator.toString());
                    }
                }
                else
                {
                    debug.append("null");
                }
                debug.append(")");
                log.debug(debug);
            }

            // return valid
            return true;
        }

        // return invalid
        return false;
    }

    /**
     * clearSessionProfileLocators - clear cache session profile locators
     */
    private void clearSessionProfileLocators()
    {
        // clear cached session profile locators, view,
        // folder page history, menu definition locators,
        // and stale flag
        synchronized (this)
        {
            profileLocators = null;
            userPrincipal = null;
            siteView = null;
            folderPageHistory.clear();
            if (menuDefinitionLocatorCache != null)
            {
                menuDefinitionLocatorCache.clear();
            }
            stale = false;
        }
    }

    /**
     * getSiteView - lookup and/or create site view for
     *               profile locators of this context
     *
     * @return site view instance
     */
    public SiteView getSiteView()
    {
        if ((siteView == null) && (pageManager != null) && (profileLocators != null))
        {
            // create new site view
            siteView = new SiteView(pageManager, profileLocators);

            // log site view creation
            if (log.isDebugEnabled())
            {
                log.debug("Created site view: search paths=" + siteView.getSearchPathsString());
            }
        }
        return siteView;
    }

    /**
     * getPageManager - return PageManager component instance
     *
     * @return PageManager instance
     */
    public PageManager getPageManager()
    {
        return pageManager;
    }

    /**
     * getProfileLocators - get session profile locators
     */
    public Map getProfileLocators()
    {
        return profileLocators;
    }

    /**
     * getStandardMenuNames - get set of available standard menu names
     *  
     * @return menu names set
     */
    public Set getStandardMenuNames()
    {
        // return standard menu names defined for site view
        SiteView view = getSiteView();
        if (view != null)
        {
            return view.getStandardMenuNames();
        }
        return null;
    }

    /**
     * getMenuDefinitionLocators - get list of node proxy menu definition
     *                             locators from site view
     *
     * @param node site view node proxy
     * @return definition locator list
     */
    public List getMenuDefinitionLocators(Node node)
    {
        // return menu definition locators for node in site view
        SiteView view = getSiteView();
        if (view != null)
        {
            return view.getMenuDefinitionLocators(node);
        }
        return null;
    }

    /**
     * getMenuDefinitionLocator - get named node proxy menu definition
     *                            locator from site view
     *
     * @param node site view node proxy
     * @param name menu definition name
     * @return menu definition locator
     */
    public SiteViewMenuDefinitionLocator getMenuDefinitionLocator(Node node, String name)
    {
        // return named menu definition locator for node in site view
        SiteView view = getSiteView();
        if (view != null)
        {
            return view.getMenuDefinitionLocator(node, name);
        }
        return null;
    }

    /**
     * getManagedPage - get concrete page instance from page proxy
     *  
     * @param page page proxy
     * @return managed page
     */
    public Page getManagedPage(Page page)
    {
        // return managed page in site view
        SiteView view = getSiteView();
        if (view != null)
        {
            return view.getManagedPage(page);            
        }
        return null;
    }

    /**
     * getMenuDefinitionLocatorCache - get menu definition locators cache
     *                                 for absolute menus
     *
     * @return menu definition locators cache
     */
    public Map getMenuDefinitionLocatorCache()
    {
        return menuDefinitionLocatorCache;
    }

    /**
     * setMenuDefinitionLocatorCache - set menu definition locators cache
     *                                 for absolute menus
     *
     * @return menu definition locators cache
     */
    public void setMenuDefinitionLocatorCache(Map cache)
    {
        menuDefinitionLocatorCache = cache;
    }

    /**
     * locatorsEquals - test profile locator maps for equivalence
     *                  ignoring request specifics
     *
     * @param locators0 request profile locator map
     * @param locators1 request profile locator map
     * @return boolean flag indicating equivalence
     */
    private static boolean locatorsEquals(Map locators0, Map locators1)
    {
        // trivial comparison
        if (locators0 == locators1)
        {
            return true;
        }

        // compare locator map sizes
        if (locators0.size() != locators1.size())
        {
            return false;
        }

        // compare locator map entries
        Iterator entriesIter = locators0.entrySet().iterator();
        if (entriesIter.hasNext())
        {
            Map.Entry entry = (Map.Entry)entriesIter.next();
            ProfileLocator locator0 = (ProfileLocator)entry.getValue();
            ProfileLocator locator1 = (ProfileLocator)locators1.get(entry.getKey());
            if (locator1 == null)
            {
                return false;
            }

            // compare locators using the most specific,
            // (i.e. first), locator properties array
            // returned by the locator iterator
            ProfileLocatorProperty [] properties0 = (ProfileLocatorProperty [])locator0.iterator().next();
            ProfileLocatorProperty [] properties1 = (ProfileLocatorProperty [])locator1.iterator().next();
            if ((properties0 != null) || (properties1 != null))
            {
                if ((properties0 == null) || (properties1 == null) || (properties0.length != properties1.length))
                {
                    return false;
                }
                
                // compare ordered locator properties
                for (int i = 0, limit = properties0.length; (i < limit); i++)
                {
                    // compare property names, control flags, navigation flags,
                    // and values. note: properties values are compared only for
                    // control or navigation properties; otherwise they are
                    // assumed to contain variable request paths that should
                    // be treated as equivalent
                    if (!properties0[i].getName().equals(properties1[i].getName()) ||
                        (properties0[i].isControl() && !properties1[i].isControl()) ||
                        (properties0[i].isNavigation() && !properties1[i].isNavigation()) ||
                        ((properties0[i].isControl() || properties0[i].isNavigation()) && 
                         (((properties0[i].getValue() == null) && (properties1[i].getValue() != null)) ||
                          ((properties0[i].getValue() != null) && !properties0[i].getValue().equals(properties1[i].getValue())))))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * locatorRequestPath - extract request specific path from profile locator
     *                      using request path from locator
     *
     * @param locator request profile locator
     * @return request path
     */
    private static String locatorRequestPath(ProfileLocator locator)
    {
        // use request path in locator as default
        return locatorRequestPath(locator, locator.getRequestPath());
    }

    /**
     * locatorRequestPath - extract request specific path from profile locator
     *
     * @param locator request profile locator
     * @param requestPath request path
     * @return request path
     */
    private static String locatorRequestPath(ProfileLocator locator, String requestPath)
    {
        // search locator using the most specific,
        // (i.e. first), locator properties array
        // returned by the locator iterator and return
        // first valued property that is not a control
        // or navigation type
        ProfileLocatorProperty [] properties = (ProfileLocatorProperty [])locator.iterator().next();
        for (int i = 0, limit = properties.length; (i < limit); i++)
        {
            if (!properties[i].isControl() && !properties[i].isNavigation() && (properties[i].getValue() != null))
            {
                // use specified locator path
                String locatorPath = properties[i].getValue();

                // return specified locatorPath if absolute
                if (locatorPath.startsWith(Folder.PATH_SEPARATOR))
                {
                    return locatorPath;
                }

                // page names and relative paths are assumed relative to
                // request path and that any locator paths with no url
                // separator should have the page extension appended
                // get default page if page path null
                if ((locatorPath.indexOf(Folder.PATH_SEPARATOR) == -1) && !locatorPath.endsWith(Page.DOCUMENT_TYPE))
                {
                    locatorPath += Page.DOCUMENT_TYPE;
                }
            
                // append locator path to request path, replacing
                // requested pages and preserving requested folders
                boolean rootFolderRequest = requestPath.equals(Folder.PATH_SEPARATOR);
                boolean folderRequest = (!requestPath.endsWith(Page.DOCUMENT_TYPE));
                int lastSeparatorIndex = requestPath.lastIndexOf(Folder.PATH_SEPARATOR_CHAR);
                if ((lastSeparatorIndex > 0) && (!folderRequest || requestPath.endsWith(Folder.PATH_SEPARATOR)))
                {
                    // append locator to request path base path
                    return requestPath.substring(0, lastSeparatorIndex) + Folder.PATH_SEPARATOR + locatorPath;
                }
                else if (!rootFolderRequest && folderRequest)
                {
                    // append locator to request path root folder
                    return requestPath + Folder.PATH_SEPARATOR + locatorPath;
                }
                else
                {
                    // use root folder locator
                    return Folder.PATH_SEPARATOR + locatorPath;
                }
            }
        }
        return requestPath;
    }

    /**
     * newNode - invoked when the definition of a node is
     *           created by the page manager or when the
     *           node creation is otherwise detected
     *
     * @param node new managed node if known
     */
    public void newNode(Node node)
    {
        // equivalent to node updated event
        updatedNode(node);
    }

    /**
     * updatedNode - invoked when the definition of a node is
     *               updated by the page manager or when the
     *               node modification is otherwise detected
     *
     * @param node updated managed node if known
     */
    public void updatedNode(Node node)
    {
        // set stale flag to force session context state reset
        synchronized (this)
        {
            stale = true;
        }

        // log updated node event
        if (log.isDebugEnabled())
        {
            if (node != null)
            {
                log.debug("Page manager update event, (node=" + node.getPath() + "): set session context state stale");
            }
            else
            {
                log.debug("Page manager update event: set session context state stale");
            }
        }
    }

    /**
     * removedNode - invoked when the definition of a node is
     *               removed by the page manager or when the
     *               node removal is otherwise detected
     *
     * @param node removed managed node if known
     */
    public void removedNode(Node node)
    {
        // equivalent to node updated event
        updatedNode(node);
    }

    /**
     * sessionDidActivate - notification that the session has just
     *                      been activated
     *
     * @param event session activation event
     */
    public void sessionDidActivate(HttpSessionEvent event)
    {
        // set stale flag to force session context state reset
        synchronized (this)
        {
            stale = true;
        }

        // log activation event
        if (log.isDebugEnabled())
        {
            log.debug("Session activation event: set session context state stale");
        }
    }
    
    /**
     * sessionWillPassivate - notification that the session is about
     *                        to be passivated
     *
     * @param event session activation event
     */
    public void sessionWillPassivate(HttpSessionEvent event)
    {
        // clear session context state
        clearSessionProfileLocators();

        // log activation event
        if (log.isDebugEnabled())
        {
            log.debug("Session deactivation event: clear session context state");
        }
    }

    /**
     * valueBound - notifies this context that it is being bound to
     *              a session and identifies the session
     *
     * @param event session binding event
     */
    public void valueBound(HttpSessionBindingEvent event)
    {
        // subscribe this session context to page manager events
        synchronized (this)
        {
            if (!subscribed)
            {
                pageManager.addListener(this);
                subscribed = true;
            }
        }

        // log binding event
        if (log.isDebugEnabled())
        {
            log.debug("Session bound event: setup page manager listener");
        }
    }
    
    /**
     * valueUnbound - notifies this context that it is being unbound
     *                from a session and identifies the session
     *
     * @param event session binding event
     */
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        // unsubscribe this session context to page manager events
        synchronized (this)
        {
            if (subscribed)
            {
                pageManager.removeListener(this);
                subscribed = false;
            }
        }

        // clear session context state
        clearSessionProfileLocators();

        // log binding event
        if (log.isDebugEnabled())
        {
            log.debug("Session unbound event: clear page manager listener and session context state");
        }
    }
}
