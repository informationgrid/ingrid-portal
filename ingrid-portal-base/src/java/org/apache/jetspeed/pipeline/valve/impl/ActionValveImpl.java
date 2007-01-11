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

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.messaging.PortletMessagingImpl;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ActionValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
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
 * @version $Id: ActionValveImpl.java 423286 2006-07-18 23:06:33Z taylor $
 *
 */
public class ActionValveImpl extends AbstractValve implements ActionValve
{

    private static final Log log = LogFactory.getLog(ActionValveImpl.class);
    private PortletContainer container;
    private PortletWindowAccessor windowAccessor;
    private boolean patchResponseCommitted = false;

    public ActionValveImpl(PortletContainer container, PortletWindowAccessor windowAccessor)
    {
        this.container = container;
        this.windowAccessor = windowAccessor;
    }
    
    public ActionValveImpl(PortletContainer container, PortletWindowAccessor windowAccessor, boolean patchResponseCommitted)
    {
        this.container = container;
        this.windowAccessor = windowAccessor;
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
                initWindow(actionWindow, request);
                HttpServletResponse response = request.getResponseForWindow(actionWindow);
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
        ((MutablePortletEntity)window.getPortletEntity()).setFragment(fragment);
    }

}
