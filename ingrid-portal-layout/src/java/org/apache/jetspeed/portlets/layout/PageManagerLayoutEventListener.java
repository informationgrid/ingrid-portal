/*
 * **************************************************-
 * Ingrid Portal Layout
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
