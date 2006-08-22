/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

/**
 * This portlet is the abstract base class of all content portlets.
 * Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract public class ContentPortlet extends GenericVelocityPortlet {

    //    private final static Log log = LogFactory.getLog(ContentPortlet.class);

    protected final static String KEY_BROWSER_STATE = "browserState";

    protected final static String PARAM_SORT_COLUMN = "sortColumn";

    /**
     * Get the column to sort after.
     * 
     * @param request
     * @param defaultValue The default column
     * @return
     */
    static protected String getSortColumn(RenderRequest request, String defaultValue) {
        String newSortCol = request.getParameter(PARAM_SORT_COLUMN);
        if (newSortCol == null) {
            newSortCol = defaultValue;
        }

        ContentBrowserState state = getBrowserState(request);
        state.setSortColumnName(newSortCol);

        return newSortCol;
    }

    static protected boolean isAscendingOrder(PortletRequest request) {
        ContentBrowserState state = getBrowserState(request);

        // if no sort column in request, then keep the state
        String sortCol = request.getParameter(PARAM_SORT_COLUMN);
        if (sortCol == null) {
            return state.isAscendingOrder();
        }

        // else process sort column
        return state.isAscendingOrder(sortCol);
    }

    static protected ContentBrowserState getBrowserState(PortletRequest request) {
        ContentBrowserState state = (ContentBrowserState) request.getPortletSession().getAttribute(KEY_BROWSER_STATE);
        if (state == null) {
            state = new ContentBrowserState();
            setBrowserState(request, state);
        }
        return state;
    }

    static protected void setBrowserState(PortletRequest request, ContentBrowserState state) {
        request.getPortletSession().setAttribute(KEY_BROWSER_STATE, state);
    }

    static protected void clearBrowserState(PortletRequest request) {
        request.getPortletSession().removeAttribute(KEY_BROWSER_STATE);
    }

}
