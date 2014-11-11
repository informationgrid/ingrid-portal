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

import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
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
    
    //public ShibbolethConfiguration(Map<String, String> headerMapping, boolean authenticate, PortalConfiguration config)
    public ShibbolethConfiguration(Map<String, String> headerMapping, boolean authenticate, PropertiesConfiguration config)
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