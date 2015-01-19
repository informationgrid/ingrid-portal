/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.portlets.browser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.sso.SSOProvider;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * AbstractBrowserPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: AbstractBrowserPortlet.java,v 1.2 2005/01/01 00:01:29 taylor
 *          Exp $
 */
public class BrowserPortlet extends GenericVelocityPortlet implements Browser
{

    protected static final String SQL = "sql";

    protected static final String POOLNAME = "poolname";

    protected static final String START = "start";
    protected static final String FIND = "find";
    protected static final String SEARCH_STRING = "searchString";
    protected static final String SEARCH_COLUMN = "searchColumn";
    protected static final String FILTERED = "filtered";
    protected static final String FILTER = "filter";
    
    protected static final String CUSTOMIZE_TEMPLATE = "customizeTemplate";

    protected static final String WINDOW_SIZE = "WindowSize";

    protected static final String USER_OBJECT_NAMES = "user-object-names";

    protected static final String USER_OBJECT_TYPES = "user-object-types";

    protected static final String USER_OBJECTS = "user-objects";

    protected static final String SQL_PARAM_PREFIX = "sqlparam";

    protected static final String LINKS_READ = "linksRead";

    protected static final String ROW_LINK = "rowLinks";

    protected static final String TABLE_LINK = "tableLinks";

    protected static final String ROW_LINK_IDS = "row-link-ids";

    protected static final String ROW_LINK_TYPES = "row-link-types";

    protected static final String ROW_LINK_TARGETS = "row-link-targets";

    protected static final String TABLE_LINK_IDS = "table-link-ids";

    protected static final String TABLE_LINK_TYPES = "table-link-types";

    protected static final String TABLE_LINK_TARGETS = "table-link-targets";

    protected static final String BROWSER_TABLE_SIZE = "tableSize";

    protected static final String BROWSER_ACTION_KEY = "browser_action_key";

    protected static final String BROWSER_ITERATOR = "table";

    protected static final String BROWSER_TITLE_ITERATOR = "title";

    protected static final String NEXT = "next";

    protected static final String PREVIOUS = "prev";

    protected static final String FIRST = "first";

    protected static final String LAST = "last";
    
    protected static final String VELOCITY_NULL_ENTRY = "-";

    // portlet entry Id
    protected static final String PEID = "js_peid";

    protected static final String SORT_COLUMN_NAME = "js_dbcolumn";

    protected List sqlParameters = new Vector();
    
    /*
     * SSO link
     */
    protected PortletContext context;
    protected SSOProvider sso;

    /**
     * Static initialization of the logger for this class
     */
    protected Logger log = LoggerFactory.getLogger(BrowserPortlet.class);

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        
        context = getPortletContext();
        sso = (SSOProvider)context.getAttribute("cps:SSO");
        if (null == sso)
        {
            log.info("Warning: SSO provider not found.");
           //throw new PortletException("Failed to find SSO Provider on portlet initialization");
        }        
    }

    public void getRows(RenderRequest request, String sql, int windowSize)
            throws Exception
    {
    }

    public void getRows(RenderRequest request, String sql, int windowSize, String filter)
    throws Exception
    {
    }
    
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        int resultSetSize, next, prev, windowSize;

        response.setContentType("text/html");

        BrowserIterator iterator = getBrowserIterator(request);
        Context context = this.getContext(request);

        String sortColName = request.getParameter(SORT_COLUMN_NAME);
        int start = getStartVariable(request, START, sortColName, iterator);
        
        PortletPreferences prefs = request.getPreferences();

        windowSize = Integer.parseInt(prefs.getValue(WINDOW_SIZE, "10"));
        
        StatusMessage message = (StatusMessage)PortletMessaging.consume(request, "DatabaseBrowserPortlet", "action");
        if (message != null)
        {
            this.getContext(request).put("statusMsg", message);            
        }
        
        try
        {
            if (iterator == null)
            {
                String sql = getQueryString(request, context);
                // System.out.println("buildNormalContext SQL: "+sql);
                readUserParameters(request, context);
                String filter = request.getParameter(FILTER);
                if (filter != null)
                    getRows(request, sql, windowSize, filter);
                else                    
                    getRows(request, sql, windowSize);
                iterator = getBrowserIterator(request);
                start = 0;
            } 
            else
            {
                if (sortColName != null)
                {
                    iterator.sort(sortColName);
                }
            }

            resultSetSize = iterator.getResultSetSize();                    
            if (start >= resultSetSize)
            {
                if ((start - windowSize) > 0)
                    start = resultSetSize - windowSize;
                else
                    start = 0;            
            }        
            next = start + windowSize;
            prev = start - windowSize;
            if (prev < 0 && start > 0)
               prev = 0;
            iterator.setTop(start);
            
            
            readLinkParameters(request, context);

            if (iterator != null)
            {
                resultSetSize = iterator.getResultSetSize();

                if (next <= resultSetSize)
                {
                    context.put(NEXT, String.valueOf(next));
                }
                if (prev <= resultSetSize && prev >= 0)
                {
                    context.put(PREVIOUS, String.valueOf(prev));
                }

                context.put(BROWSER_ITERATOR, iterator);
                context.put(BROWSER_TITLE_ITERATOR, iterator
                        .getResultSetTitleList());
                context.put(BROWSER_TABLE_SIZE, new Integer(resultSetSize));
                context.put(WINDOW_SIZE, new Integer(windowSize));
                context.put(START, new Integer(start));
                /*
                 * System.out.println("buildNormalContext Sort column name=
                 * "+sortColName); System.out.println("buildNormalContext
                 * Iterator: "+iterator); System.out.println("buildNormalContext
                 * Titles= "+iterator.getResultSetTitleList());
                 * System.out.println("buildNormalContext
                 * windowSize="+windowSize+" prev="+prev+ " next="+next+"
                 * start="+start+" resultSetSize="+resultSetSize);
                 */
            }

        } catch (Exception e)
        {
            String msg = e.toString();
            Throwable cause = e.getCause();
            if (cause != null)
            {
                msg = msg + ", " + cause.getMessage();
            }
            
            context.put("statusMsg", new StatusMessage(msg, StatusMessage.ERROR));
            // log the error msg
            log.error("Exception", e);

            /*
             * TODO: error logging
             * 
             * rundata.setMessage("Error in Portals Gems Browser: " +
             * e.toString()); rundata.setStackTrace(StringUtils.stackTrace(e),
             * e);
             * rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
             */
        }

        super.doView(request, response);
    }

    public void doEdit(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        response.setContentType("text/html");
        doPreferencesEdit(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.EDIT)
        {
            processPreferencesAction(request, response);
            clearBrowserIterator(request);            
        } else
        {
            String browserAction = request.getParameter("db.browser.action");
            if (browserAction != null)
            {
                if (browserAction.equals("refresh"))
                {
                    clearBrowserIterator(request);
                }
                String start = request.getParameter(START);
                if (start != null)
                {
                    response.setRenderParameter(START, start);
                }
                String searchString = request.getParameter(SEARCH_STRING);
                if (searchString != null)
                {                    
                    String searchColumn = request.getParameter(SEARCH_COLUMN);
                    String filtered = (String)request.getParameter(FILTERED);                    
                    if (filtered != null)
                    {
                        clearBrowserIterator(request);                        
                        response.setRenderParameter(FILTER, searchString);
                    }
                    else
                    {
                        int index = find(this.getBrowserIterator(request), searchString, searchColumn);
                        if (index == -1)
                        {
                            try
                            {
                                StatusMessage sm = new StatusMessage("Could not find match for: " + searchString, StatusMessage.ALERT);        
                                PortletMessaging.publish(request, "DatabaseBrowserPortlet", "action", sm);
                            }
                            catch (Exception e)
                            {}
                        }
                        else
                        {
                            response.setRenderParameter(START, Integer.toString(index));                        
                        }
                    }
                }                
            }
        }
    }

    /**
     * Centralizes the calls to session - to retrieve the BrowserIterator.
     * 
     * @param data
     *            The turbine rundata context for this request.
     * 
     */
    protected BrowserIterator getBrowserIterator(PortletRequest request)
    {
        BrowserIterator iterator = (BrowserIterator) request
                .getPortletSession().getAttribute(BROWSER_ACTION_KEY,
                        PortletSession.PORTLET_SCOPE);
        return iterator;
    }

    /**
     * Centralizes the calls to session - to set the BrowserIterator.
     * 
     * @param data
     *            The turbine rundata context for this request.
     * @param iterator.
     * 
     */
    protected void setBrowserIterator(RenderRequest request,
            BrowserIterator iterator)
    {
        request.getPortletSession().setAttribute(BROWSER_ACTION_KEY, iterator);
    }

    /**
     * Centralizes the calls to session - to clear the BrowserIterator from the
     * temp storage.
     * 
     * @param data
     *            The turbine rundata context for this request.
     * 
     */
    protected void clearBrowserIterator(PortletRequest request)
    {
        request.getPortletSession().removeAttribute(BROWSER_ACTION_KEY);
    }

    protected int getStartVariable(RenderRequest request, String attrName,
            String sortColName, BrowserIterator iterator)
    {
        int start = -1;
        // if users want to overwrite how the sorting affects the cursor for
        // the window
        if (sortColName != null) 
            start = getStartIndex();

        if (start < 0)
        {
            // fallback routine for start
            String startStr = request.getParameter(attrName);
            if (startStr != null && startStr.length() > 0)
            {
                try
                {
                    start = Integer.parseInt(startStr);                    
                }
                catch (Exception e)
                {
                    if (iterator != null)
                        start = iterator.getTop();
                    else
                        start = 0;
                }
            } else if (start == -1 && iterator != null)
            {
                start = iterator.getTop();
            }

            if (start < 0) start = 0;
        }
        return start;
    }

    /**
     * to be used if sorting behavior to be overwritten
     */
    protected int getStartIndex()
    {
        return 0;
    }

    /**
     * This method returns the sql from the getQuery method which can be
     * overwritten according to the needs of the application. If the getQuery()
     * returns null, then it gets the value from the psml file. If the psml
     * value is null then it returns the value from the xreg file.
     * 
     */
    protected String getQueryString(RenderRequest request, Context context)
    {
        String sql = getQueryString(request);
        if (null == sql)
        {
            sql = getPreference(request, SQL, null);
        }
        return sql;
    }

    public String getQueryString(RenderRequest request)
    {
        return null;
    }

    protected String getPreference(RenderRequest request, String attrName,
            String attrDefValue)
    {
        return request.getPreferences().getValue(attrName, attrDefValue);
    }

    protected void readUserParameters(RenderRequest request, Context context)
    {
        List userObjectList;
        Object userObjRead = request.getPortletSession().getAttribute(
                USER_OBJECTS, PortletSession.PORTLET_SCOPE);
        if (userObjRead != null)
        {
            context.put(USER_OBJECTS, (List) userObjRead);
            // System.out.println("userObjectListSize: "+
            // ((List)userObjRead).size());
        } else
        {
            /*
             * TODO: implement user parameters
             * 
             * String userObjTypes=
             * getParameterFromRegistry(portlet,USER_OBJECT_TYPES,null); String
             * userObjNames=
             * getParameterFromRegistry(portlet,USER_OBJECT_NAMES,null); if(
             * userObjTypes != null && userObjTypes.length() > 0 ) {
             * userObjectList = new ArrayList(); int userObjectIndex = 0;
             * StringTokenizer tokenizer1 = new StringTokenizer(userObjNames,
             * ","); StringTokenizer tokenizer3 = new
             * StringTokenizer(userObjTypes, ",");
             * while(tokenizer1.hasMoreTokens() && tokenizer3.hasMoreTokens()) {
             * userObjectList.add(userObjectIndex, new
             * ActionParameter(tokenizer1.nextToken(), null,
             * tokenizer3.nextToken())); userObjectIndex++; }
             * context.put(USER_OBJECTS, userObjectList);
             * setParameterToTemp(portlet, rundata, USER_OBJECTS,
             * userObjectList); //System.out.println("readLink:
             * userObjectTypesListSize: "+userObjectList.size()); }
             */
        }
    }

    protected void readSqlParameters(RenderRequest request)
    {
        List sqlParamList = null;

        int i = 1;
        while (true)
        {
            String param = getPreference(request, SQL_PARAM_PREFIX + i, null);
            if (param == null)
            {
                break;
            } else
            {
                if (sqlParamList == null)
                {
                    sqlParamList = new ArrayList();
                }
                sqlParamList.add(param);
            }
            i++;
        }

        if (sqlParamList != null)
        {
            setSQLParameters(sqlParamList);
        }
    }

    public void setSQLParameters(List parameters)
    {
        this.sqlParameters = parameters;
    }

    protected void readLinkParameters(RenderRequest request, Context context)
    {
        // TODO: implement me
    }

    /**
     * This method should be overwritten every time the user object needs to be
     * populated with some user specific constraints. As an example if the user
     * wanted to track the parent of an object based on some calculation per
     * row, it could be done here.
     * 
     */
    public void populate(int rowIndex, int columnIndex, List row)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.modules.actions.portlets.browser.BrowserQuery#filter(java.util.List,
     *      RunData)
     */
    public boolean filter(List row, RenderRequest request)
    {
        return false;
    }

    public void publishStatusMessage(PortletRequest request, String portlet, String topic, Throwable e, String message)
    {
        String msg = message + ": " + e.toString();
        Throwable cause = e.getCause();
        if (cause != null)
        {
            msg = msg + ", " + cause.getMessage();
        }
        StatusMessage sm = new StatusMessage(msg, StatusMessage.ERROR);
        try
        {
            // TODO: fixme, bug in Pluto on portlet session
            PortletMessaging.publish(request, portlet, topic, sm);
        }
        catch (Exception ee)
        {
            System.err.println("Failed to publish message: " + e);
        }        
    }

    public int find(BrowserIterator iterator, String searchString, String searchColumn)
    {
        int index = 0;
        int column = 1; 
        
        if (searchColumn != null)
            column = Integer.parseInt(searchColumn);
        
        Iterator it = iterator.getResultSet().iterator();
        while (it.hasNext())
        {
            Object row = it.next();
            String item = "";
            if (row instanceof String)
                item = (String)row;
            else if (row instanceof List)
            {
                // TODO: this only works on String columns                
                item = (String)((List)row).get(column); 
            }
            if (item.startsWith(searchString))
            {
                return index;
            }
            index++;
        }
        
        return -1;
    }
}
