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
            if (pref.getName().equals(key) && pref.getValueList() != null && pref.getValueList().size() > 0) {
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

    public String getAbsoluteUrl(String relativePath) {
        // only rewrite a non-absolute url
        if (relativePath != null && relativePath.indexOf("://") == -1 && relativePath.indexOf("mailto:") == -1) {
            HttpServletRequest request = getRequestContext().getRequest();
            StringBuffer path = new StringBuffer();
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
                e.printStackTrace();
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
        StringBuffer buf = new StringBuffer();
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
        
        Collection<LocalizedField> c = metadata.getFields(field);
        
        if (c != null) {
            for (LocalizedField entry : c) {
                value = entry.getValue();
                break;
            }
        }
        return value;
    }
    
    public IngridResourceBundle getIngridResourceBundle(ResourceBundle r) {
        return new IngridResourceBundle(r);
    }
}
