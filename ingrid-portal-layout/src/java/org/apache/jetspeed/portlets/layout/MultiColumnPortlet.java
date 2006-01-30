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
import java.util.ArrayList;
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
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.entity.PortletEntity;
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
    protected final static String DEFAULT_ONE_COLUMN_SIZES = "100%";
    protected final static String DEFAULT_TWO_COLUMN_SIZES = "50%,50%";
    protected final static String DEFAULT_THREE_COLUMN_SIZES = "34%,33%,33%";

    private int numColumns = 0;
    private String columnSizes = null;
    private String portletName = null;
    private String layoutType;
    protected DecorationFactory decorators;

    public void init( PortletConfig config ) throws PortletException
    {
        super.init(config);
        this.portletName = config.getPortletName();
        this.layoutType = config.getInitParameter("layoutType");
        if (this.layoutType == null)
        {
            throw new PortletException("Layout type not specified for " + this.portletName);
        }
        this.numColumns = Integer.parseInt(config.getInitParameter(PARAM_NUM_COLUMN));
        if (this.numColumns < 1)
        {
            this.numColumns = 1;
        }
        this.columnSizes = config.getInitParameter(PARAM_COLUMN_SIZES);
        if ((this.columnSizes == null) || (this.columnSizes.trim().length() == 0))
        {
            switch (this.numColumns)
            {
            case 1: this.columnSizes = DEFAULT_ONE_COLUMN_SIZES; break;
            case 2: this.columnSizes = DEFAULT_TWO_COLUMN_SIZES; break;
            case 3: this.columnSizes = DEFAULT_THREE_COLUMN_SIZES; break;
            default: this.columnSizes = null; break;
            }
        }
        if (this.columnSizes == null)
        {
            throw new PortletException("Column sizes cannot be defaulted for " + this.numColumns + " columns and are not specified for " + this.portletName);
        }
       
        this.decorators = (DecorationFactory)getPortletContext().getAttribute(CommonPortletServices.CPS_DECORATION_FACTORY);
        if (null == this.decorators)
        {
            throw new PortletException("Failed to find the Decoration Factory on portlet initialization");
        }        
    }

    public void doView( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        // get context and test for maximized window rendering
        RequestContext context = getRequestContext(request);
        PortletWindow window = context.getPortalURL().getNavigationalState().getMaximizedWindow();
        if (window != null)
        {
            super.doView(request, response);
            return;
        }

        // get fragment column sizes
        Fragment f = getFragment(request, false);
        String fragmentColumnSizes = columnSizes;
        String fragmentColumnSizesProperty = f.getProperty(Fragment.SIZES_PROPERTY_NAME);
        if (fragmentColumnSizesProperty != null)
        {
            fragmentColumnSizes = fragmentColumnSizesProperty;
        }
        String [] fragmentColumnSizesArray = fragmentColumnSizes.split("\\,");
        List fragmentColumnSizesList = new ArrayList(fragmentColumnSizesArray.length);
        for (int i = 0; (i < fragmentColumnSizesArray.length); i++)
        {
            fragmentColumnSizesList.add(fragmentColumnSizesArray[i]);
        }

        // construct layout object
        ColumnLayout layout;
        try
        {
            layout = new ColumnLayout(numColumns, layoutType, f.getFragments(), fragmentColumnSizesArray);
            layout.addLayoutEventListener(new PageManagerLayoutEventListener(pageManager, context.getPage(), layoutType));
        }
        catch (LayoutEventException e1)
        {
            throw new PortletException("Failed to build ColumnLayout "+e1.getMessage(), e1);
        }

        // invoke the JSP associated with this portlet
        request.setAttribute("columnLayout", layout);
        request.setAttribute("numberOfColumns", new Integer(numColumns));
        request.setAttribute("decorationFactory", this.decorators);
        request.setAttribute("columnSizes", fragmentColumnSizesList);        
        super.doView(request, response);
        request.removeAttribute("decorationFactory");
        request.removeAttribute("columnLayout");
        request.removeAttribute("numberOfColumns");
        request.removeAttribute("columnSizes");
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
                throw new PortletException("Unable to access page for editing: "+e.getMessage(), e);
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
                throw new PortletException("Unable to access page for editing: "+e.getMessage(), e);
            }
          
            Fragment rootFragment = page.getRootFragment();
            Fragment fragmentToMove = page.getFragmentById(fragmentChange);           
            int moveCode = Integer.parseInt(request.getParameter("move"));
            
            ColumnLayout layout;
            try
            {
                layout = new ColumnLayout(numColumns, layoutType, rootFragment.getFragments(), null);
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
            ContentPage page;
            try
            {
                page = pageManager.getContentPage(editingPage);
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to access page for editing: "+e.getMessage(), e);
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
                throw new PortletException("Unable to update page for fragment decorator: "+e.getMessage(), e);
            }
        }
        else if (themeChange != null &&
                 layoutChange != null &&
                 editingPage != null)                
        {
            // get page to be edited
            ContentPage page;
            try
            {
                // access content page to be edited
                page = pageManager.getContentPage(editingPage);
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to access page for editing: "+e.getMessage(), e);
            }

            // edit and update page
            boolean layoutPortletChanged = !layoutChange.equals(page.getRootFragment().getName());
            try
            {
                // update page theme and/or root fragment
                // layout portlet change
                page.setDefaultDecorator(themeChange, Fragment.LAYOUT);
                page.getRootFragment().setName(layoutChange);
                pageManager.updatePage(page);
            }
            catch (Exception e)
            {
                throw new PortletException("Unable to update page: "+e.getMessage(), e);
            }
            
            // update portlet entity and portlet window if layout portlet modified
            if (layoutPortletChanged)
            {
                try
                {
                    // update matching portlet entity
                    PortletEntity portletEntity = this.entityAccess.getPortletEntity(page.getRootFragment().getId());
                    this.entityAccess.updatePortletEntity(portletEntity, (ContentFragment)page.getRootFragment());
                    this.entityAccess.storePortletEntity(portletEntity);

                    // update matching portlet window
                    this.windowAccess.createPortletWindow(portletEntity, page.getRootFragment().getId());
                }
                catch (Exception e)
                {
                    throw new PortletException("Unable to update portlet entity or window: "+e.getMessage(), e);
                }
            }
        }
        else
        {        
            super.processAction(request, response);
        }
    }
}
