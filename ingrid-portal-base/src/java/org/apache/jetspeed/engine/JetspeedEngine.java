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
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.engine;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedPortalContext;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.ojb.broker.util.ClassHelper;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.ContainerService;
import org.apache.pluto.services.factory.FactoryManagerService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;


/**
 * <p>
 * AbstractEngine
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: AbstractEngine.java 188433 2005-03-23 22:50:44Z ate $
 *
 */
public class JetspeedEngine implements Engine
{   
    private final PortalContext context;
    private final ServletConfig config;
    private final ComponentManager componentManager;
    private Map pipelineMapper ;
    private PortalStatistics statistics;
    
    protected static final Log log = LogFactory.getLog(JetspeedEngine.class);
    protected String defaultPipelineName;    

    public JetspeedEngine(Configuration configuration, String applicationRoot, ServletConfig config, ComponentManager componentManager )
    {
        this.componentManager = componentManager;
        this.context = new JetspeedPortalContext(this, configuration, applicationRoot);
        this.config = config;
        context.setApplicationRoot(applicationRoot);
        context.setConfiguration(configuration);           

        defaultPipelineName = configuration.getString(PIPELINE_DEFAULT, "jetspeed-pipeline");
        configuration.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, applicationRoot);
        
        // Make these availble as beans to Spring
        componentManager.addComponent("Engine", this);
        componentManager.addComponent("PortalContext", context);
        componentManager.addComponent("PortalConfiguration", configuration);
    }  
    
    

    /**
     * Initializes the engine with a commons configuration, starting all early
     * initable services.
     * 
     * @param configuration
     *                  a commons <code>Configuration</code> set
     * @param applicationRoot
     *                  a <code>String</code> path to the application root for
     *                  resources
     * @param
     * @throws JetspeedException
     *                   when the engine fails to initilialize
     */
    public void start() throws JetspeedException
    {        
        DateFormat format = DateFormat.getInstance();
        Date startTime = new Date();        
        try
        {  
            log.info("Starting Jetspeed Engine ("+getClass().getName()+") at "+format.format(startTime));
    
            // patch up OJB
            ClassLoader ploader2 = this.getClass().getClassLoader();
            //ClassLoader ploader2 = Thread.currentThread().getContextClassLoader();
            ClassHelper.setClassLoader(ploader2);
            
            //Start the ComponentManager
            componentManager.start();               
            pipelineMapper = (Map)componentManager.getComponent("pipeline-map");
            try
            {
                statistics = (PortalStatistics)componentManager.getComponent("PortalStatistics");
            }
            catch (Exception e)
            {
                // silenty ignore, its not configured
                // TODO: statistics as an AOP advice
            }
            
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            log.error(e.toString());
            throw new JetspeedException("Jetspeed Initialization exception!", e);
        }
        finally
        {            
            Date endTime = new Date();
            long elapsedTime = (endTime.getTime() - startTime.getTime()) / 1000;
            log.info("Finished starting Jetspeed Engine ("+getClass().getName()+") at "+format.format(endTime) 
                         +".  Elapsed time: "+elapsedTime+" seconds.");
        }
    }

    /**
     * Get the servlet configuration if this engine is running under a servlet
     * container.
     * 
     * @return config The servlet configuration
     */
    public ServletConfig getServletConfig()
    {
        return this.config;
    }



    public void shutdown() throws JetspeedException
    {        
    
        try
        {
            PortletContainer container = (PortletContainer) componentManager
                    .getComponent(PortletContainer.class);
            if (container != null)
            {
                container.shutdown();
            }
    
            componentManager.stop();
        }
        catch (PortletContainerException e)
        {
            throw new JetspeedException(e);
        }
        System.gc();
    }

    public void service( RequestContext context ) throws JetspeedException
    {        
        long start = System.currentTimeMillis();
        String targetPipeline = null;
        String requestPipeline = context
                .getRequestParameter(PortalReservedParameters.PIPELINE);
        if (null == requestPipeline)
        {
            targetPipeline = (String)context.getAttribute(PortalReservedParameters.PIPELINE);                
            if (null == targetPipeline)
            {
                String pipelineKey = context.getRequest().getServletPath();                    
                if (null != pipelineKey)
                {
                    if (pipelineKey.equals("/portal"))
                        targetPipeline = this.defaultPipelineName;
                    else
                        targetPipeline = (String)pipelineMapper.get(pipelineKey); 
                    // System.out.println("pipeline = " + targetPipeline);
                }
                else
                {
                    targetPipeline = this.defaultPipelineName;
                }
            }
        } else {
        	if (pipelineMapper.containsValue(requestPipeline)) {
        		targetPipeline = requestPipeline;
        	}
        }
        Pipeline pipeline = null;
        if (targetPipeline != null)
        {
            Pipeline specificPipeline = getPipeline(targetPipeline);
            if (specificPipeline != null)
            {
                pipeline = specificPipeline;
            }
        }
        else
            pipeline = getPipeline();
        
        context.setPipeline(pipeline);
        pipeline.invoke(context);
   
        long end = System.currentTimeMillis();
        if (statistics != null)
            statistics.logPageAccess(context, PortalStatistics.HTTP_OK, end - start);
    }

    /**
     * Returns the context associated with this engine.
     * 
     * @return an <code>EngineContext</code> associated with this engine
     */
    public PortalContext getContext()
    {
        return this.context;
    }

    /**
     * Given a application relative path, returns the real path relative to the
     * application root
     *  
     */
    public String getRealPath( String path )
    {
        String result = "";
        String base = context.getApplicationRoot();
        if (base.endsWith(java.io.File.separator))
        {
            if (path.startsWith("/"))
            {
                result = base.concat(path.substring(1));
                return result;
            }
        }
        else
        {
            if (!path.startsWith("/"))
            {
                result = base.concat("/").concat(path);
                return result;
            }
        }
        return base.concat(path);
    }
    
    public Pipeline getPipeline( String pipelineName )
    {
        return (Pipeline) componentManager.getComponent(pipelineName);
    }

    public Pipeline getPipeline()
    {
        return getPipeline(defaultPipelineName);
    }

    /**
     * @see org.apache.jetspeed.engine.Engine#getCurrentRequestContext()
     */
    public RequestContext getCurrentRequestContext()
    {
        RequestContextComponent contextComponent = (RequestContextComponent) getComponentManager()
            .getComponent(RequestContextComponent.class);
        return contextComponent.getRequestContext();
    }

    public ComponentManager getComponentManager()
    {
        return this.componentManager;
    }
    /**
     * <p>
     * getFactory
     * </p>
     *
     * @see org.apache.pluto.services.factory.FactoryManagerService#getFactory(java.lang.Class)
     * @param theClass
     * @return
     */
    public Factory getFactory( Class theClass )
    {        
        return (Factory) getComponentManager().getComponent(theClass);
    }
    /**
     * <p>
     * getContainerService
     * </p>
     *
     * @see org.apache.pluto.services.PortletContainerEnvironment#getContainerService(java.lang.Class)
     * @param service
     * @return
     */
    public ContainerService getContainerService( Class service )
    {
        if(service.equals(FactoryManagerService.class))
        {
            return this;
        }

        try
        {
            return (ContainerService) getComponentManager().getComponent(service);
        }
        catch (NoSuchBeanDefinitionException e)
        {
            log.warn("No ContainerService defined for "+service.getName());
            return null;
        }
    }

}
