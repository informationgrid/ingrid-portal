/*
 * **************************************************-
 * Ingrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.jetspeed.velocity;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.velocity.JetspeedPowerToolImpl;

import de.ingrid.portal.global.IngridResourceBundle;

public class IngridPowerToolImpl extends JetspeedPowerToolImpl {
    private static UserManager userManager = null;

    public IngridPowerToolImpl(RequestContext requestContext, PortletConfig portletConfig, RenderRequest renderRequest, RenderResponse renderResponse, PortletRenderer renderer) throws Exception {
        super(requestContext, portletConfig, renderRequest, renderResponse, renderer);
        if (userManager == null) {
            userManager = Jetspeed.getComponentManager().lookupComponent("org.apache.jetspeed.security.UserManager");
            if (null == userManager) {
                throw new PortletException("Failed to find the User Manager on powertool initialization");
            }
        }
    }

    public Object getPreferenceFirstValue(ContentFragment f, String key) {
        List<FragmentPreference> prefs = f.getPreferences();
        Iterator<FragmentPreference> it = prefs.iterator();
        while (it.hasNext()) {
            FragmentPreference pref = it.next();
            if (pref.getName().equals(key) && pref.getValueList() != null && !pref.getValueList().isEmpty()) {
                return pref.getValueList().get(0);
            }
        }

        // no preference in fragment found, try to get the preference from the
        // portlet preferences
        try {
            Preferences ps = getPortletWindow(f).getPortletDefinition().getPortletPreferences();
            Preference p = ps.getPortletPreference(key);
            if (p != null) {
                return p.getValues().get(0);
            }
        } catch (Exception e) {
            handleError(e, "JetspeedPowerTool failed to retreive the current PortletEntity.  " + e.toString(),
                    getCurrentFragment());
        }
        return "";
    }

    @Override
    public String getAbsoluteUrl(String relativePath) {
        // only rewrite a non-absolute url
        if (relativePath != null && relativePath.indexOf("://") == -1 && relativePath.indexOf("mailto:") == -1) {
            HttpServletRequest request = getRequestContext().getRequest();
            StringBuilder path = new StringBuilder();
            return renderResponse.encodeURL(path.append(request.getScheme()).append("://").append(
                    request.getServerName()).append(":").append(request.getServerPort()).append(
                    request.getContextPath()).append(request.getServletPath()).append(relativePath).toString());
        } else {
            return relativePath;
        }
    }

    public Map<String, String> getUserProperties() {
        Principal principal = requestContext.getRequest().getUserPrincipal();
        if (principal != null) {
            String userName = principal.getName();
            User user = null;
            try {
                user = userManager.getUser(userName);
            } catch (SecurityException e) {
                log.error("Error on getUserProperties.", e);
            }
            if (user != null) {
                return user.getInfoMap();
            }
        }
        return null;
    }

    /**
     * Turns all characters into HTML entity equivalents
     * 
     */
    public String htmlescapeAll(String s1) {
        if (s1 == null)
            return null;
        StringBuilder buf = new StringBuilder();
        int i;
        for (i = 0; i < s1.length(); ++i) {
            char ch = s1.charAt(i);
            buf.append("&#" + ((int) ch) + ";");
        }
        return buf.toString();
    }
    
    public String getPageMetadata(String field) {
        String value = "";
        GenericMetadata metadata = getPage().getMetadata();
        if (metadata == null)
            return "";
        Collection<LocalizedField> c = metadata.getFields();
        
        if (c != null) {
            for (LocalizedField entry : c) {
                if(entry.getName() != null && entry.getName().equals(field)) {
                    value = entry.getValue();
                    if(value != null) {
                        break;
                    }
                }
            }
        }
        return value;
    }
    
    public IngridResourceBundle getIngridResourceBundle(ResourceBundle r) {
        return new IngridResourceBundle(r);
    }
}
