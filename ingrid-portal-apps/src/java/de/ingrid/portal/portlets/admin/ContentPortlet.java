/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * This portlet is the abstract base class of all content portlets.
 * Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract public class ContentPortlet extends GenericVelocityPortlet {

    //    private final static Log log = LogFactory.getLog(ContentPortlet.class);

    // Parameters/Keys for Attributes in Request /Session
    protected final static String KEY_BROWSER_STATE = "browserState";

    protected final static String PARAM_SORT_COLUMN = "sortColumn";

    protected final static String PARAM_PAGE = "currentPage";

    protected final static String PARAM_NOT_INITIAL = "notInitial";

    // ACTIONS

    protected static String PARAMV_ACTION_DO_REFRESH = "doRefresh";

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        StringBuffer urlViewParams = new StringBuffer("?");

        // append all params from passed request
        urlViewParams.append(Utils.getURLParams(request));

        // indicates call from same page for rendering
        String urlParam = Utils.toURLParam(PARAM_NOT_INITIAL, Settings.MSGV_TRUE);
        Utils.appendURLParameter(urlViewParams, urlParam);

        // set action param for rendering
        if (request.getParameter(PARAMV_ACTION_DO_REFRESH) != null) {
            urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DO_REFRESH);
            Utils.appendURLParameter(urlViewParams, urlParam);
        }

        String currentPage = (String) request.getAttribute(PARAM_PAGE);
        if (currentPage != null) {
            response.sendRedirect(currentPage + urlViewParams);
        }
    }

    static protected String getAction(PortletRequest request) {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        return action;
    }

    static protected void handleState(RenderRequest request) {
        if (request.getParameter(PARAM_NOT_INITIAL) == null) {
            clearBrowserState(request);
        }
    }

    /**
     * Get the column to sort after.
     * 
     * @param request
     * @param defaultValue The default column
     * @return
     */
    static protected String getSortColumn(RenderRequest request, String defaultValue) {
        ContentBrowserState state = getBrowserState(request);

        String sortCol = null;
        if (getAction(request).equals(PARAMV_ACTION_DO_REFRESH)) {
            sortCol = state.getSortColumnName();
        } else {
            sortCol = request.getParameter(PARAM_SORT_COLUMN);
        }

        if (sortCol == null) {
            sortCol = defaultValue;
        }

        state.setSortColumnName(sortCol);

        return sortCol;
    }

    /**
     * Get Sort Order
     * @param request
     * @return true = ascending else descending
     */
    static protected boolean isAscendingOrder(RenderRequest request) {
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
