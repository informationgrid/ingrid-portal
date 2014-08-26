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

import java.util.Map;

import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.administration.PortalConfigurationConstants;


public class ShibbolethConfiguration
{
    public static final String USERNAME = "username";
    public static final String ROLENAME = "rolename";
    public static final String ADMINNAME = "adminname";
    public static final String ROLENAMESTOMAP = "roles-mapped-admin-portal";
    
    private Map<String, String> headerMapping;
    private boolean authenticate;
    private String guestUser;
    
    public ShibbolethConfiguration(Map<String, String> headerMapping, boolean authenticate, PortalConfiguration config)
    {
        this.headerMapping = headerMapping;
        this.authenticate = authenticate;
        this.guestUser = config.getString(PortalConfigurationConstants.USERS_DEFAULT_GUEST);
    }

    public boolean isAuthenticate()
    {
        return authenticate;
    }

    
    public Map<String, String> getHeaderMapping()
    {
        return headerMapping;
    }

    
    public String getGuestUser()
    {
        return guestUser;
    }
    
    
}