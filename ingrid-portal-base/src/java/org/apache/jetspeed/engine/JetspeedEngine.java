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
package org.apache.jetspeed.engine;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.JetspeedPortalContext;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.administration.PortalConfigurationImpl;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.ojb.broker.util.ClassHelper;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;


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
    
    protected static final Logger log = LoggerFactory.getLogger(JetspeedEngine.class);
    protected String defaultPipelineName;    

    public JetspeedEngine(Configuration configuration, String applicationRoot, ServletConfig config, ComponentManager componentManager )
    {
        this(new PortalConfigurationImpl(configuration), applicationRoot, config, componentManager);
    }
    
    public JetspeedEngine(PortalConfiguration configuration, String applicationRoot, ServletConfig config, ComponentManager componentManager )
    {
        this.componentManager = componentManager;
        this.context = new JetspeedPortalContext(this, configuration, applicationRoot);
        this.config = config;
        context.setApplicationRoot(applicationRoot);
        context.setConfiguration(configuration);           

        defaultPipelineName = configuration.getString(PIPELINE_DEFAULT, "jetspeed-pipeline");
        configuration.setString(JetspeedEngineConstants.APPLICATION_ROOT_KEY, applicationRoot);
        
        // Make these availble as beans to Spring
        componentManager.addComponent("Engine", this);
        componentManager.addComponent("PortalContext", context);
        componentManager.addComponent("PortalConfiguration", configuration);
    }  
    
    

    /**
     * Initializes the engine with a commons configuration, starting all early
     * initable services.
     * 
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
            pipelineMapper = componentManager.lookupComponent("pipeline-map");
            componentManager.<PortletFactory>lookupComponent("portletFactory").setPortalContext(context);
            try
            {
                statistics = componentManager.lookupComponent("PortalStatistics");
            }
            catch (Exception e)
            {
                // silenty ignore, its not configured
                // TODO: statistics as an AOP advice
            }
            // TODO: complete this work for JSP (https://issues.apache.org/jira/browse/JS2-711)
            // I think config.getServletName is incorrect, need to fix this and change this name to jetspeed-layouts:: when looking up in registry
            // but not when dispatching, still trying to figure that out
            //PortletApplicationManagement pam = (PortletApplicationManagement)componentManager.getComponent("PAM");
            //pam.startInternalApplication(config.getServletName());                
            
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
            PortletContainer container = null;
            
            if (componentManager.containsComponent(PortletContainer.class))
            {
                container = componentManager.lookupComponent(PortletContainer.class);
            }
            
            if (container != null)
            {
                container.destroy();
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
        String requestPipeline = (String)context.getAttribute(PortalReservedParameters.PIPELINE);                
        if (null == requestPipeline)
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
        } else {
        	// wemove: we explicitly check whether pipeline from request can be mapped (we removed pipelines) !
        	// to avoid exceptions ?
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
        return componentManager.lookupComponent(pipelineName);
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
        RequestContextComponent contextComponent = getComponentManager().lookupComponent(RequestContextComponent.class);
        return contextComponent.getRequestContext();
    }

    public ComponentManager getComponentManager()
    {
        return this.componentManager;
    }
}
