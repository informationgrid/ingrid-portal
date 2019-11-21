/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class HelpPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger(HelpPortlet.class);

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "help.portlet.title");
        response.setTitle(messages.getString(titleKey));

        // find help file according to the language
        String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
        String fileName = "ingrid-portal-help_" + lang + ".xml";
        String filePath = null;
        try {
            filePath = Utils.getResourceAsStream(fileName);
        } catch (Exception e1) {
            log.warn(fileName + " not found!");
        }
        if (filePath == null) {
            try {
                filePath = Utils.getResourceAsStream("ingrid-portal-help.xml");
            } catch (Exception e) {
                log.error("ingrid-portal-help.xml not found!");
            }
        }
        
        // read help resource
        SAXReader xmlReader = new SAXReader();
        Document doc = null;
        try {
            doc = xmlReader.read(filePath);
        } catch (Exception e) {
            log.error("Error reading help source file: " + filePath, e);
        }

        // get the help chapter
        String helpKey = request.getParameter("hkey");
        if (helpKey == null) {
            helpKey = prefs.getValue("default", "index");
        }
        context.put( "helpKey", helpKey );
        // read help chapter
        Object chapterObj = null;
        ArrayList<DefaultElement> menuObj = null;
        String myPath = "//section[@help-key='" + helpKey + "']/ancestor::chapter";
        try {
            if(doc != null) {
                chapterObj = doc.selectSingleNode(myPath);
                menuObj = (ArrayList<DefaultElement>) doc.selectNodes("//chapter");
            }
        } catch (Exception t) {
            log.error("Error reading '" + myPath + "' from help source file: " + filePath, t);
        }

        // transform the xml content to valid html using xslt
        if (chapterObj == null) {
            context.put("help_content", "<p>help key (" + helpKey + ") not found!</p>");
        } else {
            TransformerFactory factory;
            try {
                factory = TransformerFactory.newInstance();
            } catch (Exception t) {
                System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
                factory = TransformerFactory.newInstance();
            }
            
            try {
                StreamSource stylesheet = new StreamSource(Utils.getResourceAsStream("ingrid-portal-help.xsl"));
                Transformer transformer = factory.newTransformer(stylesheet);
                DocumentResult result = new DocumentResult();
                String subtree = ((DefaultElement)chapterObj).asXML();
                DocumentSource source = new DocumentSource( DocumentHelper.parseText(subtree) );
                
                transformer.transform(source, result);
                String helpContent = result.getDocument().asXML();
                context.put("help_content", helpContent);
                
                if(menuObj != null) {
                    stylesheet = new StreamSource(Utils.getResourceAsStream("ingrid-portal-help-menu.xsl"));
                    transformer = factory.newTransformer(stylesheet);
                    result = new DocumentResult();
                    String menuContent = "";
                    for (int i=0; i < menuObj.size(); i++) {
                        DefaultElement obj = menuObj.get(i);
                        subtree = obj.asXML();
                        source = new DocumentSource( DocumentHelper.parseText(subtree) );
                        transformer.transform(source, result);
                        menuContent += result.getDocument().asXML();
                    }
                    context.put("menu_content", menuContent);
                }
                
            } catch (Exception e) {
                log.error("Error processing help entry!", e);
            }
        }
        
        super.doView(request, response);
    }
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }
    
    

}
