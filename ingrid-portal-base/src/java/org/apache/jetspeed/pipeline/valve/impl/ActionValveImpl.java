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
package org.apache.jetspeed.pipeline.valve.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentFragmentImpl;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ActionValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <p>
 * ActionValveImpl
 * </p>
 * 
 * Default implementation of the ActionValve interface.  Expects to be
 * called after the ContainerValve has set up the appropriate action window
 * within the request context.  This should come before ANY rendering takes
 * place.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: ActionValveImpl.java 506339 2007-02-12 07:03:07Z taylor $
 *
 */
public class ActionValveImpl extends AbstractValve implements ActionValve
{

    private static final Log log = LogFactory.getLog(ActionValveImpl.class);
    private PortletContainer container;
    private PortletWindowAccessor windowAccessor;
    private boolean patchResponseCommitted = false;
    private JetspeedCache portletContentCache;

    public ActionValveImpl(PortletContainer container, PortletWindowAccessor windowAccessor, JetspeedCache portletContentCache)
    {
        this.container = container;
        this.windowAccessor = windowAccessor;
        this.portletContentCache = portletContentCache;
    }
    
    public ActionValveImpl(PortletContainer container, PortletWindowAccessor windowAccessor, JetspeedCache portletContentCache, boolean patchResponseCommitted)
    {
        this.container = container;
        this.windowAccessor = windowAccessor;
        this.portletContentCache = portletContentCache;        
        this.patchResponseCommitted = patchResponseCommitted;
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {     
        boolean responseCommitted = false;
        try
        {            
            PortletWindow actionWindow = request.getActionWindow();
            if (actionWindow != null)
            {
                // If portlet entity is null, try to refresh the actionWindow.
                // Under some clustered environments, a cached portlet window could have null entity.
                if (null == actionWindow.getPortletEntity())
                {
                    try 
                    {
                        Fragment fragment = request.getPage().getFragmentById(actionWindow.getId().toString());
                        if (fragment == null) {
                        	log.error("Unable to get fragment for action windew id='" + actionWindow.getId().toString() + "' while making the following request: " + request.getRequest().getRequestURL() + "?" + request.getRequest().getQueryString());
                        } else {
                        	// wemove: Only if we have fragment to avoid Exception
                        	ContentFragment contentFragment = new ContentFragmentImpl(fragment, new HashMap());
                            actionWindow = this.windowAccessor.getPortletWindow(contentFragment);                        	
                        }
                    } 
                    catch (Exception e)
                    {
                        log.error("Failed to refresh action window.", e);
                    }
                }

                HttpServletResponse response = request.getResponseForWindow(actionWindow);

            	// wemove: DO THIS ONLY IF PORTLET PRESENT to avoid exception ! 
                if (actionWindow.getPortletEntity() != null) {
                    initWindow(actionWindow, request);

                    HttpServletRequest requestForWindow = request.getRequestForWindow(actionWindow);
                    requestForWindow.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, request);
                    
                    //PortletMessagingImpl msg = new PortletMessagingImpl(windowAccessor);
                    
                    requestForWindow.setAttribute("JETSPEED_ACTION", request);
                    container.processPortletAction(
                        actionWindow,
                        requestForWindow,
                        response);
                    // The container redirects the client after PortletAction processing
                    // so there is no need to continue the pipeline
                    
                    //msg.processActionMessage("todo", request);
                } else {
                	// wemove: send Error, so request not executed anymore, e.g. by search engines !
                	response.sendError(response.SC_NOT_FOUND, "Unable to get fragment for action window");
                }
                
                // clear the cache for all portlets on the current page
                clearPortletCacheForPage(request, actionWindow);
                
                if (patchResponseCommitted)
                {
                    responseCommitted = true;
                }
                else
                {
                    responseCommitted = response.isCommitted();
                }
                request.setAttribute(PortalReservedParameters.PIPELINE, null); // clear the pipeline
            }
        }
        catch (PortletContainerException e)
        {
            log.fatal("Unable to retrieve portlet container!", e);
            throw new PipelineException("Unable to retrieve portlet container!", e);
        }
        catch (PortletException e)
        {
            log.warn("Unexpected PortletException in ActionValveImpl", e);
            //  throw new PipelineException("Unexpected PortletException in ActionValveImpl", e);

        }
        catch (IOException e)
        {
            log.error("Unexpected IOException in ActionValveImpl", e);
            // throw new PipelineException("Unexpected IOException in ActionValveImpl", e);
        }
        catch (IllegalStateException e)
        {
            log.error("Illegal State Exception. Response was written to in Action Phase", e);
            responseCommitted = true;
        }
        catch (Throwable t)
        {
            log.error("Unknown exception processing Action", t);
        }
        finally
        {
            // Check if an action was processed and if its response has been committed
            // (Pluto will redirect the client after PorletAction processing)
            if ( responseCommitted )
            {
                log.info("Action processed and response committed (pipeline processing stopped)");
            }
            else
            {
                // Pass control to the next Valve in the Pipeline
                context.invokeNext(request);
            }
        }

    }

    protected void clearPortletCacheForPage(RequestContext request, PortletWindow actionWindow)
    throws JetspeedException
    {
        ContentPage page = request.getPage();
        if (null == page)
        {
            throw new JetspeedException("Failed to find PSML Pin ContentPageAggregator.build");
        }
        ContentFragment root = page.getRootContentFragment();
        if (root == null)
        {
            throw new JetspeedException("No root ContentFragment found in ContentPage");
        }
        if (!isNonStandardAction(actionWindow))
        {
            notifyFragments(root, request, page);
        }
        else
        {
            ContentFragment fragment = page.getContentFragmentById(actionWindow.getId().toString());
            clearTargetCache(fragment, request);
        }
    }
    
    /**
     * Actions can be marked as non-standard if they don't participate in
     * JSR-168 standard action behavior. By default, actions are supposed
     * to clear the cache of all other portlets on the page.
     * By setting this parameter, we can ignore the standard behavior
     * and not clear the cache on actions. This is useful for portlets
     * which never participate with other portlets.
     * 
     */    
    protected boolean isNonStandardAction(PortletWindow actionWindow)
    {
        PortletEntity entity = actionWindow.getPortletEntity();
        if (entity != null)
        {
            PortletDefinitionComposite portletDefinition = (PortletDefinitionComposite)entity.getPortletDefinition();
            if (portletDefinition != null)
            {
                Collection actionList = null;
        
                if (portletDefinition != null)
                {
                    actionList = portletDefinition.getMetadata().getFields(PortalReservedParameters.PORTLET_EXTENDED_DESCRIPTOR_NON_STANDARD_ACTION);
                }
                if (actionList != null) 
                {
                    if (!actionList.isEmpty())
                        return true;
                }
            }
        }
        return false;
    }
   
    protected void notifyFragments(ContentFragment f, RequestContext context, ContentPage page)
    {
        if (f.getContentFragments() != null && f.getContentFragments().size() > 0)
        {
            Iterator children = f.getContentFragments().iterator();
            while (children.hasNext())
            {
                ContentFragment child = (ContentFragment) children.next();
                if (!"hidden".equals(f.getState()))
                {
                    notifyFragments(child, context, page);
                }
            } 
        }    
        String cacheKey = portletContentCache.createCacheKey(context.getUserPrincipal().getName(), f.getId());
        if (portletContentCache.isKeyInCache(cacheKey))
        {
            portletContentCache.remove(cacheKey);
        }
    }

    protected void clearTargetCache(ContentFragment f, RequestContext context)
    {
        String cacheKey = portletContentCache.createCacheKey(context.getUserPrincipal().getName(), f.getId());
        if (portletContentCache.isKeyInCache(cacheKey))
        {
            portletContentCache.remove(cacheKey);
        }
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        // TODO Auto-generated method stub
        return "ActionValveImpl";
    }
    
    /**
     * Makes sure that this PortletWindow's PortletEntity is set to have the
     * current requests fragment.
     * @param window
     * @param request
     */
    protected void initWindow(PortletWindow window, RequestContext request)
    {
        Page page = request.getPage();
    	Fragment fragment = page.getFragmentById(window.getId().toString());
    	MutablePortletEntity mpe = (MutablePortletEntity)window.getPortletEntity();
    	if (mpe == null) {
    		log.error("Unable to get PortletEntity for fragment id='" + window.getId().toString() + "' while making the following request: " + request.getRequest().getRequestURL().toString());
    	}
		mpe.setFragment(fragment);
    }

}
