package de.ingrid.portal.jetspeed.velocity;

import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.title.DynamicTitleService;
import org.apache.jetspeed.velocity.JetspeedPowerToolImpl;

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
        return "";
    }

}
