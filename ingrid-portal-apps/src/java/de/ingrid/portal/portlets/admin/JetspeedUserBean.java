/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package de.ingrid.portal.portlets.admin;

import java.security.Principal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * User state.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedUserBean.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class JetspeedUserBean
{
    private String principal;    
    private Map attributes = new LinkedHashMap();
    
    public JetspeedUserBean(User user)
    {
        Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);             
        this.principal = userPrincipal.getName();
        try
        {
            Preferences userAttributes = user.getUserAttributes();
            String[] keys = userAttributes.keys();
            for (int ix = 0; ix < keys.length; ix++)
            {
                attributes.put(keys[ix], userAttributes.get(keys[ix], null));
            }
        }
        catch (BackingStoreException e)
        {
        }
    }
    
    /**
     * @return Returns the principal.
     */
    public String getPrincipal()
    {
        return principal;
    }
    /**
     * @param principal The principal to set.
     */
    public void setPrincipal(String principal)
    {
        this.principal = principal;
    }
    
    public Principal createPrincipal(Subject subject, Class classe)
    {
        Principal principal = null;
        Iterator principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = (Principal) principals.next();
            if (classe.isInstance(p))
            {
                principal = p;
                break;
            }
        }
        return principal;
    }
    
    /**
     * @return Returns the attributes.
     */
    public Map getAttributes()
    {
        return attributes;
    }
}