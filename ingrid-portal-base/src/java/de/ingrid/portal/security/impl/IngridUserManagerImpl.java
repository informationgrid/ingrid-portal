/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ingrid.portal.security.impl;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.PrincipalsSet;
import org.apache.jetspeed.security.impl.UserImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.util.ArgUtil;

import de.ingrid.portal.security.JetspeedPrincipalQueryContext;
import de.ingrid.portal.security.UserManager;
import de.ingrid.portal.security.UserResultList;
import de.ingrid.portal.security.spi.impl.JetspeedPrincipalLookupManagerFactory;

/**
 * <p>
 * Implementation for managing users and provides access to the {@link User}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id: IngridUserManagerImpl.java 475493 2006-11-15 23:32:29Z taylor $
 */
public class IngridUserManagerImpl implements UserManager
{

    private static final Log log = LogFactory.getLog(IngridUserManagerImpl.class);

    /** The authenticatino provider proxy. */
    private AuthenticationProviderProxy atnProviderProxy = null;

    /** The security mapping handler. */
    private SecurityMappingHandler securityMappingHandler = null;
    
    private JetspeedPrincipalLookupManagerFactory jetspeedPrincipalLookupManagerFactory = null;

    private String anonymousUser = "guest";
    private User guest = null;
    
    /**
     * @param securityProvider
     *            The security provider.
     */
    public IngridUserManagerImpl(SecurityProvider securityProvider)
    {
        this.atnProviderProxy = securityProvider
                .getAuthenticationProviderProxy();
        this.securityMappingHandler = securityProvider
                .getSecurityMappingHandler();
    }

    /**
     * @param securityProvider
     *            The security provider.
     * @param anonymousUser
     *            The anonymous user name
     */
    public IngridUserManagerImpl(SecurityProvider securityProvider,
            String anonymousUser)
    {
        this.atnProviderProxy = securityProvider
                .getAuthenticationProviderProxy();
        this.securityMappingHandler = securityProvider
                .getSecurityMappingHandler();
        if (anonymousUser != null)
        {
            this.anonymousUser = anonymousUser;
        }
    }

    /**
     * @param securityProvider
     *            The security provider.
     * @param roleHierarchyResolver
     *            The role hierachy resolver.
     * @param groupHierarchyResolver
     *            The group hierarchy resolver.
     */
    public IngridUserManagerImpl(SecurityProvider securityProvider,
            HierarchyResolver roleHierarchyResolver,
            HierarchyResolver groupHierarchyResolver)
    {
        securityProvider.getSecurityMappingHandler().setRoleHierarchyResolver(
                roleHierarchyResolver);
        securityProvider.getSecurityMappingHandler().setGroupHierarchyResolver(
                groupHierarchyResolver);
        this.atnProviderProxy = securityProvider
                .getAuthenticationProviderProxy();
        this.securityMappingHandler = securityProvider
                .getSecurityMappingHandler();
    }

    /**
     * @param securityProvider
     *            The security provider.
     * @param roleHierarchyResolver
     *            The role hierachy resolver.
     * @param groupHierarchyResolver
     *            The group hierarchy resolver.
     * @param anonymousUser
     *            The anonymous user name
     */
    public IngridUserManagerImpl(SecurityProvider securityProvider,
            HierarchyResolver roleHierarchyResolver,
            HierarchyResolver groupHierarchyResolver, String anonymousUser)
    {
        securityProvider.getSecurityMappingHandler().setRoleHierarchyResolver(
                roleHierarchyResolver);
        securityProvider.getSecurityMappingHandler().setGroupHierarchyResolver(
                groupHierarchyResolver);
        this.atnProviderProxy = securityProvider
                .getAuthenticationProviderProxy();
        this.securityMappingHandler = securityProvider
                .getSecurityMappingHandler();
        if (anonymousUser != null)
        {
            this.anonymousUser = anonymousUser;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.security.UserManager#getAnonymousUser()
     */
    public String getAnonymousUser()
    {
        return this.anonymousUser;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#authenticate(java.lang.String,
     *      java.lang.String)
     */
    public boolean authenticate(String username, String password)
    {
        ArgUtil.notNull(new Object[]
        { username, password}, new String[]
        { "username", "password"},
                "authenticate(java.lang.String, java.lang.String)");

        boolean authenticated = false;
        try
        {
            if (!getAnonymousUser().equals(username))
            {
                authenticated = atnProviderProxy.authenticate(username,
                        password);
                if (authenticated && log.isDebugEnabled())
                {
                    log.debug("Authenticated user: " + username);
                }
            }
        } catch (SecurityException e)
        {
            // ignore: not authenticated
        }
        return authenticated;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String,
     *      java.lang.String)
     */
    public void addUser(String username, String password)
            throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { username}, new String[]
        { "username"}, "addUser(java.lang.String, java.lang.String)");

        createUser(username, password, atnProviderProxy
                .getDefaultAuthenticationProvider(),false);
    }

    

    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addUser(String username, String password, String atnProviderName)
            throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { username}, new String[]
        { "username"}, "addUser(java.lang.String, java.lang.String)");

        createUser(username, password, atnProviderName, false);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#importUser(java.lang.String,
     *      java.lang.String, boolean)
     */
    public void importUser(String username, String password, boolean passThrough)
            throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { username}, new String[]
        { "username"}, "addUser(java.lang.String, java.lang.String)");

        createUser(username, password, atnProviderProxy
                .getDefaultAuthenticationProvider(),passThrough);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#importUser(java.lang.String,
     *      java.lang.String, java.lang.String, boolean)
     */
    public void importUser(String username, String password, String atnProviderName, boolean passThrough)
            throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { username}, new String[]
        { "username"}, "addUser(java.lang.String, java.lang.String)");

        createUser(username, password, atnProviderName,passThrough);
    }
    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    protected void createUser(String username, String password, String atnProviderName, boolean raw)
            throws SecurityException
    {
        ArgUtil
                .notNull(new Object[]
                { username, atnProviderName}, new String[]
                { "username", "atnProviderName"},
                        "addUser(java.lang.String, java.lang.String, java.lang.String)");

//        if (getAnonymousUser().equals(username)) { throw new SecurityException(
//                SecurityException.ANONYMOUS_USER_PROTECTED.create(username)); }

        // Check if user already exists.
        if (userExists(username)) { 
            throw new SecurityException(SecurityException.USER_ALREADY_EXISTS.create(username));
        }

        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Add the preferences.
        Preferences preferences = Preferences.userRoot().node(fullPath);
        if (log.isDebugEnabled())
        {
            log.debug("Added user preferences node: " + fullPath);
        }
        try
        {
            if ((null != preferences)
                    && preferences.absolutePath().equals(fullPath))
            {
                // Add user principal.
                atnProviderProxy.addUserPrincipal(userPrincipal);
                if (password != null)
                {
                    try
                    {
                        // Set private password credential
                    	if (raw)
                            atnProviderProxy.importPassword(username, password,atnProviderName);
                    	else
                    		atnProviderProxy.setPassword(username, null, password,atnProviderName);
                    }
                    catch (SecurityException se1)
                    {
                        try
                        {
                            // rollback created user
                            atnProviderProxy.removeUserPrincipal(userPrincipal);
                        }
                        catch (SecurityException se2)
                        {
                            log.error("Failed to rollback created user after its password turned out to be invalid", se2);
                        }
                        throw se1;
                    }
                }
                if (log.isDebugEnabled())
                {
                    log.debug("Added user: " + fullPath);
                }
            }
        } catch (SecurityException se)
        {
            log.error(se.getMessage(), se);

            // Remove the preferences node.
            try
            {
                preferences.removeNode();
            } catch (BackingStoreException bse)
            {
                bse.printStackTrace();
            }
            throw se;
        }
    }

    
    
    /**
     * @see org.apache.jetspeed.security.UserManager#removeUser(java.lang.String)
     * 
     * TODO Enforce that only administrators can do this.
     */
    public void removeUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { username}, new String[]
        { "username"}, "removeUser(java.lang.String)");

        if (getAnonymousUser().equals(username)) { throw new SecurityException(
                SecurityException.ANONYMOUS_USER_PROTECTED.create(username)); }
        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        atnProviderProxy.removeUserPrincipal(userPrincipal);
        // Remove preferences
        Preferences preferences = Preferences.userRoot().node(fullPath);
        try
        {
            preferences.removeNode();
        } catch (BackingStoreException bse)
        {
            bse.printStackTrace();
        }
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        ArgUtil.notNull(new Object[]
        { username}, new String[]
        { "username"}, "userExists(java.lang.String)");

        return atnProviderProxy.getUserPrincipal(username) != null;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUser(java.lang.String)
     */
    public User getUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { username}, new String[]
        { "username"}, "getUser(java.lang.String)");

        // optimize guest lookups as they can be excessive
        if (guest != null && getAnonymousUser().equals(username))
        {
            // TODO: need to handle caching issues            
            return guest;
        }
        
        Set principals = new PrincipalsSet();
        String fullPath = (new UserPrincipalImpl(username)).getFullPath();

        Principal userPrincipal = atnProviderProxy.getUserPrincipal(username);
        if (null == userPrincipal) { 
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
        }

        principals.add(userPrincipal);
        principals.addAll(securityMappingHandler.getRolePrincipals(username));
        principals.addAll(securityMappingHandler.getGroupPrincipals(username));

        Subject subject = null;
        if (getAnonymousUser().equals(username))
        {
            subject = new Subject(true, principals, new HashSet(),
                    new HashSet());
        } else
        {
            subject = new Subject(true, principals, atnProviderProxy
                    .getPublicCredentials(username), atnProviderProxy
                    .getPrivateCredentials(username));
        }
        Preferences preferences = Preferences.userRoot().node(fullPath);
        User user = new UserImpl(subject, preferences);
        if (getAnonymousUser().equals(username))
        {
            guest = user;
        }
        return user;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsers(java.lang.String)
     */
    public Iterator getUsers(String filter) throws SecurityException
    {
        List users = new LinkedList();
        Iterator userPrincipals = atnProviderProxy.getUserPrincipals(filter)
                .iterator();
        while (userPrincipals.hasNext())
        {
            String username = ((Principal) userPrincipals.next()).getName();
            User user = getUser(username);
            users.add(user);
        }
        return users.iterator();
    }
    
    
    /* (non-Javadoc)
     * @see de.ingrid.portal.security.UserManager#getUsersExtended(de.ingrid.portal.security.JetspeedPrincipalQueryContext)
     */
    @Override
    public UserResultList getUsersExtended(JetspeedPrincipalQueryContext queryContext) throws SecurityException {
        return new UserResultList(jetspeedPrincipalLookupManagerFactory.getJetspeedPrincipalLookupManager().getPrincipals(queryContext));        
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUserNames(java.lang.String)
     */
    public Iterator getUserNames(String filter) throws SecurityException
    {
        List usernames = new LinkedList();
        Iterator userPrincipals = atnProviderProxy.getUserPrincipals(filter).iterator();
        while (userPrincipals.hasNext())
        {
            usernames.add(((Principal) userPrincipals.next()).getName());
        }
        return usernames.iterator();
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsersInRole(java.lang.String)
     */
    public Collection getUsersInRole(String roleFullPathName)
            throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { roleFullPathName}, new String[]
        { "roleFullPathName"}, "getUsersInRole(java.lang.String)");

        Collection users = new ArrayList();

        Set userPrincipals = securityMappingHandler
                .getUserPrincipalsInRole(roleFullPathName);
        Iterator userPrincipalsIter = userPrincipals.iterator();
        while (userPrincipalsIter.hasNext())
        {
            Principal userPrincipal = (Principal) userPrincipalsIter.next();
            users.add(getUser(userPrincipal.getName()));
        }
        return users;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsersInGroup(java.lang.String)
     */
    public Collection getUsersInGroup(String groupFullPathName)
            throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { groupFullPathName}, new String[]
        { "groupFullPathName"}, "getUsersInGroup(java.lang.String)");

        Collection users = new ArrayList();

        Set userPrincipals = securityMappingHandler
                .getUserPrincipalsInGroup(groupFullPathName);
        Iterator userPrincipalsIter = userPrincipals.iterator();
        while (userPrincipalsIter.hasNext())
        {
            Principal userPrincipal = (Principal) userPrincipalsIter.next();
            users.add(getUser(userPrincipal.getName()));
        }
        return users;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPassword(java.lang.String,
     *      java.lang.String, java.lang.String)
     * 
     * TODO Enforce that only administrators can do this.
     */
    public void setPassword(String username, String oldPassword,
            String newPassword) throws SecurityException
    {
        ArgUtil
                .notNull(new Object[]
                { username, newPassword}, new String[]
                { "username", "newPassword"},
                        "setPassword(java.lang.String, java.lang.String, java.lang.String)");

        if (getAnonymousUser().equals(username)) { throw new SecurityException(
                SecurityException.ANONYMOUS_USER_PROTECTED.create(username)); }
        atnProviderProxy.setPassword(username, oldPassword, newPassword);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPasswordEnabled(java.lang.String,
     *      boolean)
     */
    public void setPasswordEnabled(String userName, boolean enabled)
            throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { userName,}, new String[]
        { "userName"}, "setPasswordEnabled(java.lang.String, boolean)");

        if (getAnonymousUser().equals(userName)) { throw new SecurityException(
                SecurityException.ANONYMOUS_USER_PROTECTED.create(userName)); }
        atnProviderProxy.setPasswordEnabled(userName, enabled);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPasswordUpdateRequired(java.lang.String,
     *      boolean)
     */
    public void setPasswordUpdateRequired(String userName,
            boolean updateRequired) throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { userName,}, new String[]
        { "userName"}, "setPasswordUpdateRequired(java.lang.String, boolean)");

        if (getAnonymousUser().equals(userName)) { throw new SecurityException(
                SecurityException.ANONYMOUS_USER_PROTECTED.create(userName)); }
        atnProviderProxy.setPasswordUpdateRequired(userName, updateRequired);
    }
    
    
    /**
     * @see org.apache.jetspeed.security.UserManager#setUserEnabled(java.lang.String, boolean)
     */
    public void setUserEnabled(String userName, boolean enabled) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { userName, }, new String[] { "userName" },
                "setUserEnabled(java.lang.String, boolean)");

        if (getAnonymousUser().equals(userName))
        {
            throw new SecurityException(SecurityException.ANONYMOUS_USER_PROTECTED.create(userName));
        }

        UserPrincipalImpl userPrincipal = (UserPrincipalImpl)atnProviderProxy.getUserPrincipal(userName);
        if (null == userPrincipal) 
        { 
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(userName));
        }
        if ( enabled != userPrincipal.isEnabled() )
        {
            userPrincipal.setEnabled(enabled);
            atnProviderProxy.updateUserPrincipal(userPrincipal);
        }
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPasswordExpiration(java.lang.String, java.sql.Date)
     */
    public void setPasswordExpiration(String userName, Date expirationDate) throws SecurityException
    {
        ArgUtil.notNull(new Object[]
        { userName,}, new String[]
        { "userName"}, "setPasswordExpiration(java.lang.String, java.sql.Date)");

        if (getAnonymousUser().equals(userName)) 
        { 
            throw new SecurityException(SecurityException.ANONYMOUS_USER_PROTECTED.create(userName)); 
        }
        atnProviderProxy.setPasswordExpiration(userName, expirationDate);
    }
    
    public void setJetspeedPrincipalLookupManagerFactory(
            JetspeedPrincipalLookupManagerFactory jetspeedPrincipalLookupManagerFactory) {
        this.jetspeedPrincipalLookupManagerFactory = jetspeedPrincipalLookupManagerFactory;
    }

}