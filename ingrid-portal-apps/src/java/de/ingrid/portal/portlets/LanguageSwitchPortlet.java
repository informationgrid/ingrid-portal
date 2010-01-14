package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.generic.ListTool;

import de.ingrid.portal.global.Utils;

/**
 * 
 * 
 * created 08.03.2009
 * 
 * @author andre.wallat
 * @version
 * 
 */
public class LanguageSwitchPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(LanguageSwitchPortlet.class);

    // VIEW TEMPLATES
    private final static String TEMPLATE_LANGUAGE_SWITCH = "/WEB-INF/templates/language.vm";

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        Locale loc = request.getLocale();
        String lang = Utils.checkSupportedLanguage(loc.getLanguage());
        if(log.isDebugEnabled()){
    		log.debug("LanguageSwitchPortlet language: " + lang);
        }
        setDefaultViewPage(TEMPLATE_LANGUAGE_SWITCH);
        
        // add the velocity tool to access arrays
        ListTool listTool = new ListTool();
        context.put("ListTool", listTool);
        
        context.put("languagesNames", Utils.getLanguagesFullAsArray(lang));
        context.put("languagesShort", Utils.getLanguagesShortAsArray());
        context.put("selectedLang", lang);
        super.doView(request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    	// when the language is switched the variable "lang" will be submitted as a 
    	// get-value. The real language switch is done by IngridLocalizationValveImpl
    	// which checks for the submitted variable.
    }

   
}
