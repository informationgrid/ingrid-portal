/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class HelpPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(HelpPortlet.class);

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        
        // find help file according to the language
        String fileName = "ingrid-portal-help_" + request.getLocale().getLanguage() + ".xml";
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
            log.error("Error reading help source file!", e);
        }

        // get the help chapter
        String helpKey = request.getParameter("hkey");
        if (helpKey == null) {
            helpKey = "index";
        }
        Object chapterObj = doc.selectSingleNode( "//section[@help-key='" + helpKey + "']/ancestor::chapter" );

        // transform the xml content to valid html using xslt
        if (chapterObj == null) {
            context.put("help_content", "<p>help key (" + helpKey + ") not found!</p>");
        } else {
            TransformerFactory factory = TransformerFactory.newInstance();
            try {
                StreamSource stylesheet = new StreamSource(Utils.getResourceAsStream("ingrid-portal-help.xsl"));
                Transformer transformer = factory.newTransformer(stylesheet);
                DocumentResult result = new DocumentResult();
                String subtree = ((DefaultElement)chapterObj).asXML();
                DocumentSource source = new DocumentSource( DocumentHelper.parseText(subtree) );
                
                transformer.transform(source, result);
                String helpContent = result.getDocument().asXML();
                context.put("help_content", helpContent);
            } catch (Exception e) {
                log.error("Error processing help entry!", e);
            }
        }
        
        super.doView(request, response);
    }
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }
    
    

}
