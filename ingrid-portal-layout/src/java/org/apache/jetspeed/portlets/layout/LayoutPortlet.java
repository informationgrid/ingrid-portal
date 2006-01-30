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
import java.util.Locale;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.velocity.JetspeedPowerTool;
import org.apache.jetspeed.velocity.JetspeedPowerToolFactory;
import org.apache.pluto.om.window.PortletWindow;

/**
 */
public class LayoutPortlet extends org.apache.portals.bridges.common.GenericServletPortlet
{
    public static final String GENERIC_TEMPLATE_TYPE = "generic";

    public static final String FRAGMENT_PROCESSING_ERROR_PREFIX = "fragment.processing.error.";

    public static final String FRAGMENT_ATTR = "fragment";

    public static final String LAYOUT_ATTR = "layout";

    public static final String HIDDEN = "hidden";

    public static final String LAYOUT_TEMPLATE_TYPE = "layout";

    public static final String DECORATOR_TYPE = "decorator";
    
    
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(LayoutPortlet.class);
    
    protected PortletRegistry registry;
    protected PageManager pageManager;
    protected JetspeedPowerToolFactory jptFactory;
    protected TemplateLocator templateLocator;
    protected PortletEntityAccessComponent entityAccess;
    protected PortletWindowAccessor windowAccess;
    protected TemplateLocator decorationLocator;
    
    public void init( PortletConfig config ) throws PortletException
    {
        super.init(config);
        
        registry = (PortletRegistry)getPortletContext().getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }        
        pageManager = (PageManager)getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager)
        {
            throw new PortletException("Failed to find the Page Manager on portlet initialization");
        }        
        jptFactory = (JetspeedPowerToolFactory)getPortletContext().getAttribute(CommonPortletServices.CPS_JETSPEED_POWERTOOL_FACTORY);
        if (null == jptFactory)
        {
            throw new PortletException("Failed to find the JPT Factory on portlet initialization");
        }
        
        entityAccess = (PortletEntityAccessComponent) getPortletContext().getAttribute(CommonPortletServices.CPS_ENTITY_ACCESS_COMPONENT);
        if (null == entityAccess)
        {
            throw new PortletException("Failed to find the Entity Access on portlet initialization");
        }                
        
        windowAccess = (PortletWindowAccessor) getPortletContext().getAttribute(CommonPortletServices.CPS_WINDOW_ACCESS_COMPONENT);
        if (null == windowAccess)
        {
            throw new PortletException("Failed to find the Window Access on portlet initialization");
        }        
        
        templateLocator = (TemplateLocator) getPortletContext().getAttribute("TemplateLocator");
        decorationLocator = (TemplateLocator) getPortletContext().getAttribute("DecorationLocator");
        
    }

    public void doHelp( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        response.setContentType("text/html");
        JetspeedPowerTool jpt = getJetspeedPowerTool(request);

        PortletPreferences prefs = request.getPreferences();
        String absHelpPage = "";

        // request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, getPage(request));
        // request.setAttribute("fragment", getFragment(request, false));        

        if (prefs != null)
        {

            try
            {
                String helpPage = prefs.getValue(PARAM_HELP_PAGE, null);
                if (helpPage == null)
                {
                    helpPage = this.getInitParameter(PARAM_HELP_PAGE);
                    if (helpPage == null)
                        helpPage = "columns";
                }
                

                // TODO: Need to retreive layout.properties instead of
                // hard-coding ".vm"
                absHelpPage = jpt.getTemplate(helpPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE + "-help.vm",
                        JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAppRelativePath();
                log.debug("Path to help page for LayoutPortlet " + absHelpPage);
                request.setAttribute(PARAM_VIEW_PAGE, absHelpPage);
            }
            catch (TemplateLocatorException e)
            {
                throw new PortletException("Unable to locate view page " + absHelpPage, e);
            }
        }
        super.doView(request, response);

    }
    
    /**
     * 
     */
    public void doView( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        response.setContentType("text/html");
        RequestContext context = getRequestContext(request);
        PortletWindow window = context.getPortalURL().getNavigationalState().getMaximizedWindow();
        boolean maximized = (window != null);

        if (maximized)
        {
            request.setAttribute("layout", getMaximizedLayout(request));
        }
        else
        {
            request.setAttribute("layout", getFragment(request, false));
        }

        PortletPreferences prefs = request.getPreferences();
        if (prefs != null)
        {
            String absViewPage = null;
            String viewPage = null;
            try
            {
                JetspeedPowerTool jpt = getJetspeedPowerTool(request);
                if (maximized)
                {
                    viewPage = prefs.getValue(PARAM_MAX_PAGE, null);
                    if (viewPage == null)
                    {
                        viewPage = this.getInitParameter(PARAM_MAX_PAGE);
                        if (viewPage == null)
                            viewPage = "maximized";
                    }
                }
                else
                {
                    viewPage = prefs.getValue(PARAM_VIEW_PAGE, null);
                    if (viewPage == null)
                    {
                        viewPage = this.getInitParameter(PARAM_VIEW_PAGE);
                        if (viewPage == null)
                            viewPage = "columns";
                    }
                }
                // TODO: Need to retrieve layout.properties instead of
                // hard-coding ".vm"
                absViewPage = jpt.getTemplate(viewPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE + ".vm",
                        JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAppRelativePath();
                log.debug("Path to view page for LayoutPortlet " + absViewPage);
                request.setAttribute(PARAM_VIEW_PAGE, absViewPage);
            }
            catch (TemplateLocatorException e)
            {
                throw new PortletException("Unable to locate view page " + absViewPage, e);
            }
        }

        super.doView(request, response);

        request.removeAttribute(PortalReservedParameters.PAGE_ATTRIBUTE);
        request.removeAttribute("fragment");
        request.removeAttribute("layout");
        request.removeAttribute("dispatcher");
    }
    
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        String page = request.getParameter("page");
        String deleteFragmentId = request.getParameter("deleteId");
        String portlets = request.getParameter("portlets");
        if (deleteFragmentId != null && deleteFragmentId.length() > 0)
        {
            removeFragment(page, deleteFragmentId);
        }
        else if (portlets != null && portlets.length() > 0)
        {
            int count = 0;
            StringTokenizer tokenizer = new StringTokenizer(portlets, ",");            
            while (tokenizer.hasMoreTokens())
            {
                String portlet = tokenizer.nextToken();
                try
                {
                    if (portlet.startsWith("box_"))
                    {
                        portlet = portlet.substring("box_".length());                        
                        addPortletToPage(page, portlet);
                        count++;
                    }
                }
                catch (Exception e)
                {
                    log.error("failed to add portlet to page: " + portlet);
                }
            }
            
        }       
    }

    protected void removeFragment(String pageId, String fragmentId)
    {
        try
        {
            Page page = pageManager.getPage(pageId);
            Fragment f = page.getFragmentById(fragmentId);
            Fragment root = page.getRootFragment();
            root.getFragments().remove(f);
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            log.error("failed to remove portlet " + fragmentId + " from page: " + pageId);
        }
            
    }
    
    protected void addPortletToPage(String pageId, String portletId)
    {
        try
        {
            Fragment fragment = pageManager.newFragment();
            fragment.setType(Fragment.PORTLET);
            fragment.setName(portletId);
            
            Page page = pageManager.getContentPage(pageId);
            // WARNING: under construction
            // this is prototype and very dependent on a single depth fragment structure            
            Fragment root = page.getRootFragment();
            root.getFragments().add(fragment);
            pageManager.updatePage(page);            
        }
        catch (Exception e)
        {
            log.error("failed to add portlet " + portletId + " to page: " + pageId);
        }
        
    }
    
    /**
     * <p>
     * initJetspeedPowerTool
     * </p>
     * 
     * @param request
     * @param response
     * @return
     * @throws PortletException
     */
    protected JetspeedPowerTool getJetspeedPowerTool( RenderRequest request ) throws PortletException
    {
        JetspeedPowerTool tool = (JetspeedPowerTool) request.getAttribute(PortalReservedParameters.JETSPEED_POWER_TOOL_REQ_ATTRIBUTE);
        RequestContext requestContext = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);

        if (tool == null)
        {

            try
            {
                if (requestContext == null)
                {
                    throw new IllegalStateException(
                            "LayoutPortlet unable to handle request because there is no RequestContext in "
                                    + "the HttpServletRequest.");
                }

                tool = this.jptFactory.getJetspeedPowerTool(requestContext);
                request.setAttribute(PortalReservedParameters.JETSPEED_POWER_TOOL_REQ_ATTRIBUTE, tool);
            }

            catch (Exception e1)
            {
                throw new PortletException("Unable to init JetspeedPowerTool: " + e1.toString(), e1);
            }
        }
        
        return tool;
    }
    
    /**
     * 
     * @param request
     * @param maximized
     * @return
     */
    protected Fragment getFragment( RenderRequest request, boolean maximized )
    {
        String attribute = (maximized)
                ? PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE
                : PortalReservedParameters.FRAGMENT_ATTRIBUTE;
        return (Fragment) request.getAttribute(attribute);       
    }
   
    /**
     * 
     * @param request
     * @return
     */
    protected Fragment getMaximizedLayout( RenderRequest request )
    {
        return (Fragment) request.getAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE);
    }
    
    /**
     * 
     * @param request
     * @return
     */
    protected RequestContext getRequestContext( RenderRequest request )
    {
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        if (requestContext != null)
        {
            return requestContext;
        }
        else
        {
            throw new IllegalStateException(
                    "getRequestContext() failed as it appears that no RenderRequest is available within the RenderRequest");
        }
    }

    /**
     * <p>
     * doEdit
     * </p>
     * 
     * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest,
     *          javax.portlet.RenderResponse)
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    public void doEdit( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        doView(request, response);
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws TemplateLocatorException
     */
    protected LocatorDescriptor getTemplateLocatorDescriptor(RenderRequest request) throws TemplateLocatorException
    {
        RequestContext requestContext = getRequestContext(request);
        CapabilityMap capabilityMap = requestContext.getCapabilityMap();
        Locale locale = requestContext.getLocale();

        LocatorDescriptor templateLocatorDescriptor = templateLocator.createLocatorDescriptor(null);
        templateLocatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
        templateLocatorDescriptor.setCountry(locale.getCountry());
        templateLocatorDescriptor.setLanguage(locale.getLanguage());
        return templateLocatorDescriptor;     
    }
    
    
    /**
     * 
     * @param request
     * @return
     * @throws TemplateLocatorException
     */
    protected LocatorDescriptor getDecoratorLocatorDescriptor(RenderRequest request) throws TemplateLocatorException
    {
        RequestContext requestContext = getRequestContext(request);
        CapabilityMap capabilityMap = requestContext.getCapabilityMap();
        Locale locale = requestContext.getLocale();
  
        LocatorDescriptor decorationLocatorDescriptor = decorationLocator.createLocatorDescriptor(null);
        decorationLocatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
        decorationLocatorDescriptor.setCountry(locale.getCountry());
        decorationLocatorDescriptor.setLanguage(locale.getLanguage());
        
        return decorationLocatorDescriptor;
    }
    
    /**
     * 
     * @param request
     * @param fragment
     * @param page
     * @return
     * @throws TemplateLocatorException
     * @throws ConfigurationException
     */
    public String decorateAndInclude(RenderRequest request, Fragment fragment, Page page) throws TemplateLocatorException, ConfigurationException
    {   
        String fragmentType = fragment.getType();
        String decorator = fragment.getDecorator();
        LocatorDescriptor decorationLocatorDescriptor = getDecoratorLocatorDescriptor(request);
        if (decorator == null)
        {
            decorator = page.getEffectiveDefaultDecorator(fragmentType);
        }

        // get fragment properties for fragmentType or generic
        TemplateDescriptor propsTemp = getTemplate(decorator + "/" + DECORATOR_TYPE + ".properties", fragmentType,
                decorationLocator, decorationLocatorDescriptor);
        if (propsTemp == null)
        {
            fragmentType = GENERIC_TEMPLATE_TYPE;
            propsTemp = getTemplate(decorator + "/" + DECORATOR_TYPE + ".properties", fragmentType, decorationLocator,
                    decorationLocatorDescriptor);
        }

        // get decorator template
        Configuration decoConf = new PropertiesConfiguration(propsTemp.getAbsolutePath());
        String ext = decoConf.getString("template.extension");
        String decoratorPath = decorator + "/" + DECORATOR_TYPE + ext;
        TemplateDescriptor template = null;
        try
        {
            template = getDecoration(request, decoratorPath, fragmentType);
        }
        catch (TemplateLocatorException e)
        {
            String parent = decoConf.getString("extends");
            if (parent != null)
            {
                template = getDecoration(request, parent + "/" + DECORATOR_TYPE + ext, fragmentType);
            }
        }

        return  template.getAppRelativePath();
    }
    
    /**
     * 
     * @param request
     * @param path
     * @param templateType
     * @return
     * @throws TemplateLocatorException
     */
    protected TemplateDescriptor getDecoration( RenderRequest request, String path, String templateType ) throws TemplateLocatorException
    {        
        return getTemplate(path, templateType, decorationLocator, getDecoratorLocatorDescriptor(request));
    }
    
    /**
     * 
     * @param path
     * @param templateType
     * @param locator
     * @param descriptor
     * @return
     * @throws TemplateLocatorException
     */
    protected TemplateDescriptor getTemplate( String path, String templateType, TemplateLocator locator,
            LocatorDescriptor descriptor ) throws TemplateLocatorException
    {
        
        if (templateType == null)
        {
            templateType = GENERIC_TEMPLATE_TYPE;
        }
        try
        {

            descriptor.setName(path);
            descriptor.setType(templateType);

            TemplateDescriptor template = locator.locateTemplate(descriptor);
            return template;
        }
        catch (TemplateLocatorException e)
        {
            log.error("Unable to locate template: " + path, e);
            System.out.println("Unable to locate template: " + path);
            throw e;
        }
    }
}
