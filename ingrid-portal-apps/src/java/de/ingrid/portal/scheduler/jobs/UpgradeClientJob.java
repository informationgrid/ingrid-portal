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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.upgradeclient.IngridComponent;
import de.ingrid.portal.upgradeclient.UpgradeTools;
import de.ingrid.utils.PlugDescription;

/**
 *  
 * 
 * @author andre.wallat@wemove.com
 */
public class UpgradeClientJob extends IngridMonitorAbstractJob {

    protected final static Logger log = LoggerFactory.getLogger(UpgradeClientJob.class);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // start timer
        startTimer();
        
   		log.info("UpgradeClientJob is started ...");

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
		    
		    if (serverComponents.isEmpty()) {
		        status = STATUS_ERROR;
		        statusCode = STATUS_CODE_ERROR_NOT_AVAILABLE_OR_EMPTY;
		    }
		    
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
			log.error("An exception occured: " + e.getMessage());
			e.printStackTrace();
        } finally {
            computeTime(dataMap, stopTimer());
            sendEmailOnUpdate(installedComponents);
            updateJobData(context, status, statusCode);
            //context.getScheduler().addJob(context.getJobDetail(), true);
            updateJob(context);
        }
        log.info("UpgradeClientJob finished");

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
        // get all partner and provider from db to get the long name later
        Session session = HibernateUtil.currentSession();
        List<IngridProvider> allIngridProviderInDB = session.createCriteria(IngridProvider.class).list();
        List<IngridPartner> allIngridPartnerInDB = session.createCriteria(IngridPartner.class).list();
        
        for (IngridComponent component : installedComponents) {
            // if component is an iPlug then try to find out its version
            if (component.isIPlug()) {
                PlugDescription pd = IBUSInterfaceImpl.getInstance().getIPlug(component.getId());
                // check if component is connected in case it is an iPlug
                if (pd != null) {
                    // clear extra infos about partner and provider
                    component.removeExtraInfo(IngridComponent.PARTNER_INFO);
                    component.removeExtraInfo(IngridComponent.PROVIDER_INFO);
                    
                    component.addExtraInfo(IngridComponent.PARTNER_INFO, getTranslatedPartner(allIngridPartnerInDB, pd.getPartners()));
                    component.addExtraInfo(IngridComponent.PROVIDER_INFO, getTranslatedProvider(allIngridProviderInDB, pd.getProviders()));
                    component.setVersion(pd.getMetadata().getVersion());
                    // add a suffix for address-iPlugs (DSCs)
                    if (pd.getPlugId().endsWith("_addr"))
                        component.setName(pd.getDataSourceName() + "(address)");
                    else {
                        String name = pd.getDataSourceName();
                        if (name == null || name.trim().isEmpty()) {
                            name = "(no name)";
                        }
                        component.setName(name);
                    }
                    // pd.getMetadata().getReleaseDate().toString()
                    
                    component.setConnected(STATUS_IS_AVAILABLE);
                } else {
                    component.setConnected(STATUS_NOT_AVAILABLE);
                }
            } else {
                // TODO: try to get information from the location of the installed distribution
                // ...
            }            
            
        }        
    }
    
    private List<String> getTranslatedPartner(List<IngridPartner> allIngridPartnerInDB, String[] partnerFromPD) {
        List<String> partners = new ArrayList<String>();
        boolean added = false;
        for (String partner : partnerFromPD) {
            added = false;
            for (IngridPartner dbPartner : allIngridPartnerInDB) {
                if (partner.equals(dbPartner.getIdent())) {
                    partners.add(dbPartner.getName());
                    added = true;
                    break;
                }
            }
            if (!added)
                partners.add(partner);
        }
        return partners;
    }
    
    private List<String> getTranslatedProvider(List<IngridProvider> allIngridProviderInDB, String[] providerFromPD) {
        List<String> providers = new ArrayList<String>();
        boolean added = false;
        for (String provider : providerFromPD) {
            added = false;
            for (IngridProvider dbProvider : allIngridProviderInDB) {
                if (provider.equals(dbProvider.getIdent())) {
                    providers.add(dbProvider.getName());
                    added = true;
                    break;
                }
            }
            // if provider cannot be found in DB use at least the identifier
            if (!added)
                providers.add(provider);
        }
        return providers;
        
    }

    private void updateUpgradeStatus(List<IngridComponent> installedComponents, Map<String, IngridComponent> serverComponents) {
        try {
            for (IngridComponent component : installedComponents) {
                if (serverComponents.containsKey(component.getType())) {
                    component.setVersionAvailable(serverComponents.get(component.getType()).getVersionAvailable());
                    component.setVersionAvailableBuild(serverComponents.get(component.getType()).getVersionAvailableBuild());
                    component.setDownloadLink(serverComponents.get(component.getType()).getDownloadLink());
                    component.setChangelogLink(serverComponents.get(component.getType()).getChangelogLink());
                    // compare using build number only if it is provided in the version info of the managed component
                    // (on the client side)
                    String serverVersion = component.getVersionAvailable();
                    if (component.getVersionAvailableBuild() != "") {
                        serverVersion += " Build:" + component.getVersionAvailableBuild(); 
                    }
                    if (component.getVersion().compareToIgnoreCase(serverVersion) >= 0) {
                        component.setStatus(STATUS_NO_UPDATE_AVAILABLE);
                    } else {
                        component.setStatus(STATUS_UPDATE_AVAILABLE);
                    }
                // if component is not available on the server (anymore?)
                } else {
                    component.reset();
                    component.setStatus(STATUS_NO_UPDATE_AVAILABLE);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public Map<String,IngridComponent> getComponentsFromUpgrader(String url) {
        Map<String,IngridComponent> components = new HashMap<String,IngridComponent>();
        
        InputStream feed = getFeed(url);
        // return empty map if upgrade server couldn't be reached
        if (feed == null)
            return components;
        
        Document document = buildDocument(feed);
        
        NodeList entryNodes  = document.getElementsByTagName("entry");
        
        for (int i=0; i<entryNodes.getLength(); i++) {
            String type = findSubNode("type", entryNodes.item(i)).getTextContent();
            // if type is unknown then skip
            if (type.toLowerCase().equals("unknown")) {
                continue;
            }
            IngridComponent component = new IngridComponent(
                    findSubNode("title", entryNodes.item(i)).getTextContent(),
                    type);
            
            component.setVersionAvailable(findSubNode("version", entryNodes.item(i)).getTextContent());
            component.setVersionAvailableBuild(findSubNode("build", entryNodes.item(i)).getTextContent());
            component.setDownloadLink(findSubNode("link", entryNodes.item(i)).getAttributes().getNamedItem("href").getTextContent());
            Node changeLogLink = findSubNode("changelogLink", entryNodes.item(i));
            if (changeLogLink != null)
                component.setChangelogLink(findSubNode("changelogLink", entryNodes.item(i)).getTextContent());
            
            // only add newer version of a component
            if ((components.get(component.getType()) == null) || 
                    ((components.get(type) != null)
                    && (components.get(type).getVersionAvailable().compareToIgnoreCase(component.getVersionAvailable()) < 0))) {
                components.put(component.getType(), component);
            }
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
            
            setCredentialsIfAny(client);
        
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
    
    /**
     * Add username and password to the connection if it was set up in 
     * ingrid-portal-apps.properties.
     * @param client
     */
    private void setCredentialsIfAny(HttpClient client) {
        String username = PortalConfig.getInstance().getString(PortalConfig.UPGRADE_SERVER_USERNAME);
        String password = PortalConfig.getInstance().getString(PortalConfig.UPGRADE_SERVER_PASSWORD);
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
            //client.getState().setCredentials(new AuthScope("localhost", 80, AuthScope.ANY_REALM), defaultcreds);
            client.getState().setCredentials(AuthScope.ANY, defaultcreds);
        }
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
