package org.apache.jetspeed.portlets.layout;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotUpdatedException;

public class PageManagerLayoutEventListener implements LayoutEventListener
{
    private final PageManager pageManager;
    private final Page page;
    private final String layoutType;
    
    public PageManagerLayoutEventListener(PageManager pageManager, Page page, String layoutType)
    {
        this.pageManager = pageManager;
        this.page = page;
        this.layoutType = layoutType;
    }

    public void handleEvent(LayoutEvent event) throws LayoutEventException
    {
        try
        {
            if(event.getEventType() == LayoutEvent.ADDED)
            {
                page.getRootFragment().getFragments().add(event.getFragment());
                pageManager.updatePage(page);
            }
            else
            {
                Fragment fragment = event.getFragment();
                LayoutCoordinate coordinate = event.getNewCoordinate();
                fragment.getProperties().put(Fragment.COLUMN_PROPERTY_NAME, String.valueOf(coordinate.getX()));
                fragment.getProperties().put(Fragment.ROW_PROPERTY_NAME, String.valueOf(coordinate.getY()));
                pageManager.updatePage(page);
            }
        }
        catch (Exception e)
        {
            throw new LayoutEventException("Unable to update page.", e);
        }
    }

}
