/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.upgradeclient.IngridComponent;
import de.ingrid.portal.upgradeclient.UpgradeTools;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.metadata.AbstractIPlugOperatorInjector;
import de.ingrid.utils.metadata.AbstractIPlugOperatorInjector.IPlugOperator;
import de.ingrid.utils.metadata.AbstractIPlugOperatorInjector.Partner;
import de.ingrid.utils.metadata.AbstractIPlugOperatorInjector.Provider;

/**
 *  
 * 
 * @author andre.wallat@wemove.com
 */
public class UpgradeClientJob extends IngridMonitorAbstractJob {

    protected final static Log log = LogFactory.getLog(UpgradeClientJob.class);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // start timer
        startTimer();
        
    	if (log.isDebugEnabled()) {
    		log.info("UpgradeClientJob is started ...");
    	}

        //Session session 	= HibernateUtil.currentSession();
        //Transaction tx 		= null;
        JobDataMap dataMap 	= context.getJobDetail().getJobDataMap();
        List<IngridComponent> installedComponents = (List<IngridComponent>)dataMap.get(UpgradeTools.INSTALLED_COMPONENTS);

        int status 			= STATUS_OK;
		String statusCode 	= STATUS_CODE_NO_ERROR;
		String url          = null;
		
		try {
		    url = PortalConfig.getInstance().getString(PortalConfig.UPGRADE_SERVER_URL);
		    log.debug("Server-Url: " + url);
		    
		    // get atom feed with all information about available components
		    Map<String,IngridComponent> serverComponents = getComponentsFromUpgrader(url);
		    //Map<String,InGridComponent> serverComponents = new HashMap<String, InGridComponent>();
		    
		    //////////////////////////
	        // TEST_DATA
		    /*serverComponents.put("IPLUG_SNS", new InGridComponent("My SNS-iPlug", "IPLUG_SNS"));
		    serverComponents.get("IPLUG_SNS").setVersionAvailable("1.4.0");
		    serverComponents.get("IPLUG_SNS").setDownloadLink("http://my.download.link.de/SNS");
		    serverComponents.put("IPLUG_DSC", new InGridComponent("My DSC-iPlug", "IPLUG_DSC"));
		    serverComponents.get("IPLUG_DSC").setVersionAvailable("1.3.1");
		    serverComponents.get("IPLUG_DSC").setDownloadLink("http://my.download.link.de/DSC");
		    */
	        //////////////////////////
		    
	        /////////////////////////
	        // FIX: TEST_DATA
		    /*installedComponents = new ArrayList<InGridComponent>();
	        installedComponents.add(new InGridComponent("My SNS-iPlug","SNS-iPlug"));
	        installedComponents.get(-1).setVersion("1.4.0");//"SNS-iPlug", "1.4.0", null, "10.10.1978"));
	        installedComponents.add(new InGridComponent("My DSC-iPlug","DSC-iPlug"));
	        installedComponents.get(-1).setVersion("1.3.0");//, null, "15.10.1985"));
	        
	        installedComponents.get(0).setIPlug(true);
	        installedComponents.get(1).setIPlug(true);*/
	        
	        ////////////////////////
		    
		    // update installed components for any changes
		    updateInstalledComponents(installedComponents);
		    
		    // compare list of installed one with atom feed
		    updateUpgradeStatus(installedComponents, serverComponents);
		    
		    // update available types which are available on the server
		    dataMap.put(UpgradeTools.COMPONENT_TYPES, getComponentTypes(serverComponents));
		    
		    // update datamap
		    dataMap.put(UpgradeTools.INSTALLED_COMPONENTS, installedComponents);
		    
        } catch (Exception e) {
            status 		= STATUS_ERROR;
			statusCode 	= STATUS_CODE_ERROR_UNSPECIFIC;
        } finally {
            computeTime(dataMap, stopTimer());
            sendEmailOnUpdate(installedComponents);
            updateJobData(context, status, statusCode);
            //context.getScheduler().addJob(context.getJobDetail(), true);
            updateJob(context);
        }

    }

    private Set<String> getComponentTypes(Map<String, IngridComponent> serverComponents) {
        Set<String> types = new HashSet<String>();
        Collection<IngridComponent> components = serverComponents.values();
        
        for (IngridComponent component : components) {
            types.add(component.getType());
        }
        
        return types;
    }

    private void updateInstalledComponents(List<IngridComponent> installedComponents) {
        if (installedComponents == null)
            return;
        
        //PlugDescription[] pds = IBUSInterfaceImpl.getInstance().getAllIPlugsWithoutTimeLimitation();
        
        for (IngridComponent component : installedComponents) {
            // if component is an iPlug then try to find out its version
            if (component.isIPlug()) {
                PlugDescription pd = IBUSInterfaceImpl.getInstance().getIPlug(component.getId());
                // check if component is connected in case it is an iPlug
                if (pd != null) {
                    // clear extra infos about partner and provider
                    component.removeExtraInfo(IngridComponent.PARTNER_INFO);
                    component.removeExtraInfo(IngridComponent.PROVIDER_INFO);
                    
                    component.addExtraInfo(IngridComponent.PARTNER_INFO, pd.getPartners());
                    component.addExtraInfo(IngridComponent.PROVIDER_INFO, pd.getProviders());
                    
                    if (pd.getMetadata() != null && !pd.getMetadata().getVersion().toLowerCase().equals("unknown")) {
                        component.setVersion(pd.getMetadata().getVersion());
                        IPlugOperator plugOperator = (IPlugOperator) pd.getMetadata().getMetadata(AbstractIPlugOperatorInjector.IPLUG_OPERATOR);
                        // if more data could be found then get the missing long name
                        if (plugOperator != null) {
                            component.removeExtraInfo(IngridComponent.PARTNER_INFO);
                            component.removeExtraInfo(IngridComponent.PROVIDER_INFO);
                            List<Partner> allPartner = plugOperator.getPartners();
                            List<String> partners = new ArrayList<String>();
                            List<String> providers = new ArrayList<String>();
                            for (Partner partner: allPartner) {
                                partners.add(partner.getDisplayName());
                                for (Provider provider : partner.getProviders()) {
                                    providers.add(provider.getDisplayName());                                    
                                }
                                
                            }
                            component.addExtraInfo(IngridComponent.PARTNER_INFO, partners);
                            component.addExtraInfo(IngridComponent.PROVIDER_INFO, providers);
                        }
                    }
                    
                    component.setStatus(STATUS_IS_AVAILABLE);
                } else {
                    component.setStatus(STATUS_NOT_AVAILABLE);
                }
            } else {
                // TODO: try to get information from the location of the installed distribution
                // ...
            }            
            
        }        
    }

    private void updateUpgradeStatus(List<IngridComponent> installedComponents, Map<String, IngridComponent> serverComponents) {
        try {
            for (IngridComponent component : installedComponents) {
                if (serverComponents.containsKey(component.getType())) { // or getId()?
                    component.setVersionAvailable(serverComponents.get(component.getType()).getVersionAvailable());
                    component.setDownloadLink(serverComponents.get(component.getType()).getDownloadLink());
                    if (component.getVersion().equals(component.getVersionAvailable())) {
                        component.setStatus(STATUS_NO_UPDATE_AVAILABLE);
                    } else {
                        component.setStatus(STATUS_UPDATE_AVAILABLE);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public Map<String,IngridComponent> getComponentsFromUpgrader(String url) {
        Map<String,IngridComponent> components = new HashMap<String,IngridComponent>();
        
        Document document = buildDocument(getFeed(url));
        
        NodeList entryNodes  = document.getElementsByTagName("entry");
        
        for (int i=0; i<entryNodes.getLength(); i++) {
            String type = findSubNode("type", entryNodes.item(i)).getTextContent();
            // if type is unknown then skip
            if (type.toLowerCase().equals("unknown")) {
                continue;
            }
            IngridComponent component = new IngridComponent(
                    findSubNode("title", entryNodes.item(i)).getTextContent(),
                    findSubNode("type", entryNodes.item(i)).getTextContent());
            
            component.setVersionAvailable(findSubNode("version", entryNodes.item(i)).getTextContent());
            component.setDownloadLink(findSubNode("link", entryNodes.item(i)).getAttributes().getNamedItem("href").getTextContent());
            components.put(component.getType(), component);
        }
        
        return components;
    }
    
    public Node findSubNode(String name, Node node) {
        if (node.getNodeType() != Node.ELEMENT_NODE) {
          log.error("Error: Search node not of element type");
          return null;
        }

        if (! node.hasChildNodes()) return null;

        NodeList list = node.getChildNodes();
        for (int i=0; i < list.getLength(); i++) {
          Node subnode = list.item(i);
          if (subnode.getNodeType() == Node.ELEMENT_NODE) {
            if (subnode.getNodeName().equals(name)) return subnode;
          }
        }
        return null;
      } 
    
    private InputStream getFeed(String url) {
        try {
            HttpClientParams httpClientParams = new HttpClientParams();
            HttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager();
            httpClientParams.setSoTimeout(30 * 1000);
            httpConnectionManager.getParams().setConnectionTimeout(30 * 1000);
            httpConnectionManager.getParams().setSoTimeout(30 * 1000);

            HttpClient client = new HttpClient(httpClientParams, httpConnectionManager);
            HttpMethod method = new GetMethod(url);
        
            int status = client.executeMethod(method);
            if (status == 200) {
                log.debug("Successfully received: " + url);
                return method.getResponseBodyAsStream();
            } else {
                log.error("Response code for '" + url + "' was: " + status);
                return null;
            }
        } catch (HttpException e) {
            log.error("An HTTP-Exception occured when calling: " + url + " -> " + e.getMessage());
        } catch (IOException e) {
            log.error("An IO-Exception occured when calling: " + url + " -> " + e.getMessage());
        }
        return null;        
    }
    
    private Document buildDocument(InputStream input) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(input);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
