/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
package de.ingrid.portal.portlets.security;

/**
 * Common resources used by Security Portlets
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SecurityResources.java 348264 2005-11-22 22:06:45Z taylor $
 */
public interface SecurityResources
{
    public static final String CURRENT_USER = "current_user";
    public static final String PAM_CURRENT_USER = "org.apache.jetspeed.pam.user";
    public static final String REQUEST_SELECT_USER = "select_user";    

    public static final String PORTLET_URL = "portlet_url";
    public static final String REQUEST_SELECT_PORTLET = "select_portlet";
    public static final String REQUEST_SELECT_TAB = "selected_tab";
    public static final String PORTLET_ACTION = "portlet_action";
    
    // Message Topics
    public static final String TOPIC_USERS = "users";
    public static final String TOPIC_USER = "user";
    public static final String TOPIC_GROUPS = "groups";
    public static final String TOPIC_GROUP = "group";
    public static final String TOPIC_ROLES = "roles";
    public static final String TOPIC_ROLE = "role";    
    public static final String TOPIC_PROFILES = "profiles";

    /** Messages **/
    public static final String MESSAGE_SELECTED = "selected";
    public static final String MESSAGE_CHANGED = "changed";
    public static final String MESSAGE_STATUS = "status";
    public static final String MESSAGE_REFRESH = "refresh";
    public static final String MESSAGE_FILTERED = "filtered";    
    public static final String MESSAGE_REFRESH_PROFILES = "refresh.profiles";
    public static final String MESSAGE_REFRESH_ROLES = "refresh.roles";
    public static final String MESSAGE_REFRESH_GROUPS = "refresh.groups";
    
    
    /** the selected non-leaf node in the tree view */
    public static final String REQUEST_NODE = "node";
    /** the selected leaf node in the tree view */
    public static final String REQUEST_SELECT_NODE = "select_node";
       
    /** The Error Messages KEY */
    public static final String ERROR_MESSAGES = "errorMessages";
    
    
    /** user attribute keys **/
    public static final String USER_NAME_GIVEN = "user.name.given";
    public static final String USER_NAME_FAMILY = "user.name.family";
    public static final String USER_EMAIL = "user.business-info.online.email";
    
}
