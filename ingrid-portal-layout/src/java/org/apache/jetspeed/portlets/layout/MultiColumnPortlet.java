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
package org.apache.jetspeed.portlets.layout;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 */
public class MultiColumnPortlet extends LayoutPortlet
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(MultiColumnPortlet.class);

    protected final static String PARAM_NUM_COLUMN = "columns";
    protected final static int DEFAULT_NUM_COLUMN = 2;
    protected final static String PARAM_COLUMN_SIZES = "sizes";
    protected final static String DEFAULT_COLUMN_SIZES = "50%,50%";

    private int numColumns = 0;
    private String colSizes = null;
    private String portletName = null;
    private String layoutType;
    private List columnSizes = null;
    protected DecorationFactory decorators;
    private Map layouts ;

    public void init( PortletConfig config ) throws PortletException
    {
        super.init(config);
        layouts = new HashMap();
        this.numColumns = Integer.parseInt(config.getInitParameter(PARAM_NUM_COLUMN));
        if (0 == numColumns)
            numColumns = 1;
        this.colSizes = config.getInitParameter(PARAM_COLUMN_SIZES);
        if (colSizes != null || colSizes.trim().length() > 0)
        {
            columnSizes = getCellSizes(colSizes);            
        }
        this.portletName = config.getPortletName();
        this.layoutType = config.getInitParameter("layoutType");
        
        decorators = (DecorationFactory)getPortletContext().getAttribute(CommonPortletServices.CPS_DECORATION_FACTORY);
        if (null == decorators)
        {
            throw new PortletException("Failed to find the Decoration Factory on portlet initialization");
        }        
        
    }

    public void doView( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        RequestContext context = getRequestContext(request);
        PortletWindow window = context.getPortalURL().getNavigationalState().getMaximizedWindow();
        Page page = getRequestContext(request).getPage();
        Fragment f = getFragment(request, false);
        ColumnLayout layout;
        try
        {
            layout = new ColumnLayout(numColumns, layoutType, f.getFragments(), this.colSizes.split("\\,") );
            layout.addLayoutEventListener(new PageManagerLayoutEventListener(pageManager, page, layoutType));
        }
        catch (LayoutEventException e1)
        {
            throw new PortletException("Failed to build ColumnLayout "+e1.getMessage(), e1);
        }

       

        // if (targetState != null && targetState.isMaximized())
        if (window != null)
        {
            super.doView(request, response);
            return;
        }

        request.setAttribute("columnLayout", layout);
        request.setAttribute("numberOfColumns", new Integer(numColumns));
        request.setAttribute("decorationFactory", this.decorators);
        List columnSizes = this.columnSizes;

        // Determine custom column sizes in the psml
        String customSizes = f.getProperty(Fragment.SIZES_PROPERTY_NAME);
        if ( customSizes != null && customSizes.trim().length() > 0 )
        {
            columnSizes = getCellSizes(customSizes);
        }
                
        request.setAttribute("columnSizes", columnSizes);        

        // now invoke the JSP associated with this portlet
        super.doView(request, response);
        
        request.removeAttribute("decorationFactory");
        request.removeAttribute("columnLayout");
        request.removeAttribute("numberOfColumns");
        request.removeAttribute("columnSizes");
    }

    /**
     * Parses the size config info and returns a list of size values for the
     * current set
     * 
     * @param sizeList
     *            java.lang.String a comma separated string a values
     * @return a List of values
     */
    protected static List getCellSizes( String sizeList )
    {
        List list = new Vector();

        if (sizeList != null)
        {
            StringTokenizer st = new StringTokenizer(sizeList, ",");
            while (st.hasMoreTokens())
            {
                list.add(st.nextToken());
            }
        }

        return list;
    }

    protected static List getCellClasses( String classlist )
    {
        List list = new Vector();

        if (classlist != null)
        {
            StringTokenizer st = new StringTokenizer(classlist, ",");
            while (st.hasMoreTokens())
            {
                list.add(st.nextToken());
            }
        }

        return list;
    }

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        String decoratorChange = request.getParameter("decorator");
        String themeChange = request.getParameter("theme");
        String layoutChange = request.getParameter("layout");
        String editingPage = request.getParameter("editingPage");
        String fragmentChange = request.getParameter("fragmentToMove");
        String jsSubmitPage = request.getParameter("jsSubmitPage");
        String jsPageName = request.getParameter("jsPageName");

        if (jsSubmitPage != null && jsPageName != null && editingPage != null)
        {
            try
            {                
                if (jsPageName.indexOf(Folder.PATH_SEPARATOR) == -1 && jsPageName.length() > 0)
                {
                    Page currentPage = pageManager.getPage(editingPage);
                    if (currentPage != null)
                    {
                        Folder parent = (Folder)currentPage.getParent();
                        if (parent != null)
                        {
                            String path = parent.getPath();
                            if (path.endsWith(Folder.PATH_SEPARATOR))
                            {
                                path = path + jsPageName;
                            }
                            else
                            {
                                path = path + Folder.PATH_SEPARATOR + jsPageName;
                            }
                            Page page = pageManager.newPage(path);
                            // TODO: Get System Wide defaults for decorators
                            page.getRootFragment().setName("jetspeed-layouts::VelocityTwoColumns");
                            page.setDefaultDecorator("tigris", Fragment.LAYOUT);
                            page.setDefaultDecorator("tigris", Fragment.PORTLET);
                            page.setTitle(jsPageName);
                            pageManager.updatePage(page);
                        }
                    }                
                }
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to access page for editing: "+e.getMessage());
            }                        
        }
        else if (request.getParameter("move") != null 
                &&  fragmentChange != null
                && editingPage != null)
        {        
            Page page;
            try
            {
                page = pageManager.getPage(editingPage);
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to access page for editing: "+e.getMessage());
            }
          
            Fragment rootFragment = page.getRootFragment();
            Fragment fragmentToMove = page.getFragmentById(fragmentChange);           
            int moveCode = Integer.parseInt(request.getParameter("move"));
            
            ColumnLayout layout;
            try
            {
                layout = new ColumnLayout(numColumns, layoutType, rootFragment.getFragments(), this.colSizes.split("\\,") );
                layout.addLayoutEventListener(new PageManagerLayoutEventListener(pageManager, page, layoutType));
            }
            catch (LayoutEventException e1)
            {
                throw new PortletException("Failed to build ColumnLayout "+e1.getMessage(), e1);
            }

            try
            {                
                switch (moveCode)
                {
                case LayoutEvent.MOVED_UP:
                    layout.moveUp(fragmentToMove);
                    break;
                case LayoutEvent.MOVED_DOWN:
                    layout.moveDown(fragmentToMove);
                    break;
                case LayoutEvent.MOVED_RIGHT:
                    layout.moveRight(fragmentToMove);
                    break;
                case LayoutEvent.MOVED_LEFT:
                    layout.moveLeft(fragmentToMove);
                    break;
                default:
                    throw new PortletException("Invalid movement code " + moveCode);
                }
               
            }
            catch (SecurityException se)
            {
                // ignore page security constraint violations, only
                // permitted users can edit managed pages; page
                // update will remain transient
                log.info("Unable to update page " + page.getId() + " layout due to security permission/constraint.", se);
            }
            catch (Exception e)
            {
                if (e instanceof PortletException)
                {
                    throw (PortletException)e;
                }
                else
                {
                    throw new PortletException("Unable to process layout for page " + page.getId() + " layout: " + e.toString(), e);
                }
            }
        }
        else if (decoratorChange != null
                && fragmentChange != null                
                && editingPage != null)
        {
            Page page;
            try
            {
                page = pageManager.getPage(editingPage);
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to access page for editing: "+e.getMessage());
            }
          
            Fragment fragment = page.getFragmentById(fragmentChange);
            if (fragment != null)
            {
                if (decoratorChange.trim().length() == 0)
                    fragment.setDecorator(null);
                else
                    fragment.setDecorator(decoratorChange);
            }
            try
            {
                pageManager.updatePage(page);
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to update page for fragment decorator: "+e.getMessage());
            }
        }
        else if (themeChange != null &&
                 layoutChange != null &&
                 editingPage != null)                
        {
            Page page;
            try
            {
                page = pageManager.getPage(editingPage);
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to access page for editing: "+e.getMessage());
            }
            
            page.setDefaultDecorator(themeChange, Fragment.LAYOUT);
            page.getRootFragment().setName(layoutChange);
            
            try
            {
                pageManager.updatePage(page);
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to update page for fragment decorator: "+e.getMessage());
            }
            
        }
        else
        {        
            super.processAction(request, response);
        }
    }
}
