package de.ingrid.portal.jetspeed.velocity;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.title.DynamicTitleService;
import org.apache.jetspeed.velocity.JetspeedPowerToolImpl;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;

public class IngridPowerToolImpl extends JetspeedPowerToolImpl
{

    public IngridPowerToolImpl(RequestContext requestContext, DynamicTitleService titleService) throws Exception
    {
        super(requestContext, titleService);
    }

    public Object getPreferenceFirstValue(ContentFragment f, String key) {
        List prefs = f.getPreferences();
        Iterator it = prefs.iterator();
        while (it.hasNext()) {
            FragmentPreference pref = (FragmentPreference) it.next();
            if (pref.getName().equals(key) && pref.getValueList() != null && pref.getValueList().size() > 0) {
                return pref.getValueList().get(0);
            }
        }
        
        // no preference in fragment found, try to get the preference from the portlet preferences
        try
        {
            PreferenceSet ps = windowAccess.getPortletWindow(f).getPortletEntity().getPortletDefinition().getPreferenceSet();
            Preference p = ps.get(key);
            if (p != null) {
                return (String)p.getValues().next();
            }
        }
        catch (Exception e)
        {
            handleError(e, "JetspeedPowerTool failed to retreive the current PortletEntity.  " + e.toString(),
                    getCurrentFragment());
        }
        return "";
    }

    public String getAbsoluteUrl(String relativePath)
    {
        // only rewrite a non-absolute url
        if (relativePath != null && relativePath.indexOf("://") == -1 && relativePath.indexOf("mailto:") == -1)
        {
            HttpServletRequest request = getRequestContext().getRequest();
            StringBuffer path = new StringBuffer();
            return renderResponse.encodeURL(path.append(request.getScheme()).append("://").append(
                    request.getServerName()).append(":").append(request.getServerPort()).append(
                    request.getContextPath()).append(request.getServletPath()).append(relativePath).toString());
        }
        else
        {
            return relativePath;
        }
    }
    
}
