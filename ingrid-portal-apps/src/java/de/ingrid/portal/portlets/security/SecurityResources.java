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
    public final static String CURRENT_USER = "current_user";
    public final static String PAM_CURRENT_USER = "org.apache.jetspeed.pam.user";
    public final static String REQUEST_SELECT_USER = "select_user";    

    public final static String PORTLET_URL = "portlet_url";
    public final static String REQUEST_SELECT_PORTLET = "select_portlet";
    public final static String REQUEST_SELECT_TAB = "selected_tab";
    public final static String PORTLET_ACTION = "portlet_action";
    
    // Message Topics
    public final static String TOPIC_USERS = "users";
    public final static String TOPIC_USER = "user";
    public final static String TOPIC_GROUPS = "groups";
    public final static String TOPIC_GROUP = "group";
    public final static String TOPIC_ROLES = "roles";
    public final static String TOPIC_ROLE = "role";    
    public final static String TOPIC_PROFILES = "profiles";

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
    public final static String REQUEST_NODE = "node";
    /** the selected leaf node in the tree view */
    public final static String REQUEST_SELECT_NODE = "select_node";
       
    /** The Error Messages KEY */
    public static final String ERROR_MESSAGES = "errorMessages";
    
    
    /** user attribute keys **/
    public final static String USER_NAME_GIVEN = "user.name.given";
    public final static String USER_NAME_FAMILY = "user.name.family";
    
}
