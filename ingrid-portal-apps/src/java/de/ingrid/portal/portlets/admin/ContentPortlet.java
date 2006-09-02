/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;

/**
 * This portlet is the abstract base class of all content portlets.
 * Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract public class ContentPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(ContentPortlet.class);

    // Keys/Values for velocity render context
    protected final static String CONTEXT_MODE = "mode";

    protected final static String CONTEXTV_MODE_NEW = "new";

    protected final static String CONTEXTV_MODE_EDIT = "edit";

    protected final static String CONTEXT_ENTITIES = "dbEntities";

    protected final static String CONTEXT_BROWSER_STATE = "browser";

    protected final static String CONTEXT_UTILS_STRING = "UtilsString";

    // Attributes in Session
    protected final static String KEY_BROWSER_STATE = "browserState";

    protected final static String KEY_ACTION_FORM = "actionForm";

    // Common Parameters in Request
    protected final static String PARAM_SORT_COLUMN = "sortColumn";

    protected final static String PARAM_ID = "id";

    /** indicates whether page is called from other page (then not set) or from own page */
    protected final static String PARAM_NOT_INITIAL = "notInitial";

    // ACTIONS
    protected static String PARAMV_ACTION_DO_REFRESH = "doRefresh";

    protected static String PARAMV_ACTION_DO_FIRST_PAGE = "doFirst";

    protected static String PARAMV_ACTION_DO_PREV_PAGE = "doPrev";

    protected static String PARAMV_ACTION_DO_NEXT_PAGE = "doNext";

    protected static String PARAMV_ACTION_DO_LAST_PAGE = "doLast";

    protected static String PARAMV_ACTION_DO_EDIT = "doEdit";

    protected static String PARAMV_ACTION_DO_NEW = "doNew";

    protected static String PARAMV_ACTION_DB_DO_SAVE = "doSave";

    protected static String PARAMV_ACTION_DB_DO_UPDATE = "doUpdate";

    protected static String PARAMV_ACTION_DB_DO_DELETE = "doDelete";

    protected static String PARAMV_ACTION_DB_DO_CANCEL = "doCancel";

    // Data to set in Subclasses
    // -------------------------

    /** The PSML page to redirect from action to */
    protected String psmlPage = null;

    /** The view template for default viewing */
    protected String viewDefault = null;

    /** The view template for editing */
    protected String viewEdit = null;

    /** The view template for adding new entity */
    protected String viewNew = null;

    /** The Hibernate Mapping class of the database entity */
    protected Class dbEntityClass = null;

    /**
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        try {
            // add localization recources to the context
            IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                    request.getLocale()));
            Context context = getContext(request);
            context.put("MESSAGES", messages);

            // reset state ? may be necessary on initial call (e.g. called from other page)
            checkInitialEnter(request);

            // default view
            boolean doViewDefault = true;
            setDefaultViewPage(viewDefault);

            // handle action
            String action = getAction(request);

            // EDIT
            if (action.equals(PARAMV_ACTION_DO_EDIT)) {
                doViewDefault = !doViewEdit(request);
            }

            // NEW
            else if (action.equals(PARAMV_ACTION_DO_NEW)) {
                doViewDefault = !doViewNew(request);
            }

            // After DB Update of entry
            else if (action.equals(PARAMV_ACTION_DB_DO_UPDATE)) {
                doViewDefault = doViewAfterUpdate(request);
            }

            // New DB entry
            else if (action.equals(PARAMV_ACTION_DB_DO_SAVE)) {
                doViewDefault = doViewAfterSave(request);
            }

            // REFRESH, DELETE
            if (doViewDefault) {
                doViewDefault(request);
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing doView:", ex);
            }
        }

        super.doView(request, response);
    }

    /**
     * Default method for editing entities.
     * NOTICE: "viewEdit" and "dbEntityClass" have to be set in this class.
     * 
     * @param request
     * @return true: all is fine, false: something wrong
     */
    protected boolean doViewEdit(RenderRequest request) {
        try {
            // get data from database
            Long[] ids = convertIds(getIds(request));
            if (ids != null) {
                Session session = HibernateUtil.currentSession();
                Criteria crit = session.createCriteria(dbEntityClass).add(Restrictions.in("id", ids));
                List rssSources = UtilsDB.getValuesFromDB(crit, session, null, true);

                // put to render context and switch view
                Context context = getContext(request);
                context.put(CONTEXT_MODE, CONTEXTV_MODE_EDIT);
                context.put(CONTEXT_ENTITIES, rssSources);
                setDefaultViewPage(viewEdit);
                return true;
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching entities to edit:", ex);
            }
        }

        return false;
    }

    /**
     * Default method for adding new entity.
     * NOTICE: "viewNew" and "dbEntityClass" have to be set in this class.
     * 
     * @param request
     * @return true: all is fine, false: something wrong
     */
    protected boolean doViewNew(RenderRequest request) {
        try {
            Object[] newEntity = { dbEntityClass.newInstance() };

            Context context = getContext(request);
            context.put(CONTEXT_MODE, CONTEXTV_MODE_NEW);
            context.put(CONTEXT_ENTITIES, newEntity);
            setDefaultViewPage(viewNew);
            return true;
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems adding new entity:", ex);
            }
        }
        return false;
    }

    /**
     * Default method for handling viewing after update (handles view dependent from action form errors).
     * @param request
     * @return true: all is fine, false: something wrong
     */
    protected boolean doViewAfterUpdate(RenderRequest request) {
        ActionForm af = getActionForm(request);
        if (af != null && af.hasErrors()) {
            Context context = getContext(request);
            context.put("actionForm", af);
            context.put(CONTEXT_MODE, CONTEXTV_MODE_EDIT);
            Object[] entities = getDBEntities(request);
            context.put(CONTEXT_ENTITIES, entities);
            setDefaultViewPage(viewEdit);
            return false;
        }

        return true;
    }

    /**
     * Default method for handling viewing after new entity was saved (handles view dependent from action form errors).
     * @param request
     * @return true: all is fine, false: something wrong
     */
    protected boolean doViewAfterSave(RenderRequest request) {
        ActionForm af = getActionForm(request);
        if (af != null && af.hasErrors()) {
            Context context = getContext(request);
            context.put("actionForm", af);
            context.put(CONTEXT_MODE, CONTEXTV_MODE_NEW);
            Object[] entities = getDBEntities(request);
            context.put(CONTEXT_ENTITIES, entities);
            setDefaultViewPage(viewNew);
            return false;
        }

        return true;
    }

    /**
     * Default method for doing default view (browsing).
     * NOTICE: "viewDefault" and "dbEntityClass" have to be set in this class.
     * 
     * @param request
     * @return true: all is fine, false: something wrong
     */
    protected boolean doViewDefault(RenderRequest request) {
        try {
            // always refresh !
            refreshBrowserState(request);
            ContentBrowserState state = getBrowserState(request);

            // get data from database
            String sortColumn = getSortColumn(request, "id");
            boolean ascendingOrder = isAscendingOrder(request);
            Session session = HibernateUtil.currentSession();
            Criteria crit = session.createCriteria(dbEntityClass);
            if (ascendingOrder) {
                crit.addOrder(Order.asc(sortColumn));
            } else {
                crit.addOrder(Order.desc(sortColumn));
            }
            crit.setFirstResult(state.getFirstRow());
            crit.setMaxResults(state.getMaxRows());

            List rssSources = UtilsDB.getValuesFromDB(crit, session, null, true);

            // put to render context
            Context context = getContext(request);
            context.put(CONTEXT_ENTITIES, rssSources);
            context.put(CONTEXT_UTILS_STRING, new UtilsString());
            context.put(CONTEXT_BROWSER_STATE, state);
            setDefaultViewPage(viewDefault);
            return true;
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing default view:", ex);
            }
        }

        return false;
    }

    /**
     * Called from sub Portlets. Handles Actions and passes all Request Params then to render method.
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        try {
            StringBuffer urlViewParams = new StringBuffer("?");

            // append all params from passed request
            urlViewParams.append(Utils.getURLParams(request));

            // indicates call from same page
            String urlParam = Utils.toURLParam(PARAM_NOT_INITIAL, Settings.MSGV_TRUE);
            Utils.appendURLParameter(urlViewParams, urlParam);

            // handle ACTIONS and set action Request param for rendering
            if (request.getParameter(PARAMV_ACTION_DO_REFRESH) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DO_REFRESH);
                Utils.appendURLParameter(urlViewParams, urlParam);

                // handled in render method

            } else if (request.getParameter(PARAMV_ACTION_DO_NEW) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DO_NEW);
                Utils.appendURLParameter(urlViewParams, urlParam);

                // handled in render method

            } else if (request.getParameter(PARAMV_ACTION_DO_EDIT) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DO_EDIT);
                Utils.appendURLParameter(urlViewParams, urlParam);

                // handled in render method

            } else if (request.getParameter(PARAMV_ACTION_DO_FIRST_PAGE) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DO_FIRST_PAGE);
                Utils.appendURLParameter(urlViewParams, urlParam);

                ContentBrowserState state = getBrowserState(request);
                state.doFirstPage();

            } else if (request.getParameter(PARAMV_ACTION_DO_PREV_PAGE) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DO_PREV_PAGE);
                Utils.appendURLParameter(urlViewParams, urlParam);

                ContentBrowserState state = getBrowserState(request);
                state.doPreviousPage();

            } else if (request.getParameter(PARAMV_ACTION_DO_NEXT_PAGE) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DO_NEXT_PAGE);
                Utils.appendURLParameter(urlViewParams, urlParam);

                ContentBrowserState state = getBrowserState(request);
                state.doNextPage();

            } else if (request.getParameter(PARAMV_ACTION_DO_LAST_PAGE) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DO_LAST_PAGE);
                Utils.appendURLParameter(urlViewParams, urlParam);

                ContentBrowserState state = getBrowserState(request);
                state.doLastPage();

            } else if (request.getParameter(PARAMV_ACTION_DB_DO_SAVE) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DB_DO_SAVE);
                Utils.appendURLParameter(urlViewParams, urlParam);

                // call sub method
                doActionSave(request);

            } else if (request.getParameter(PARAMV_ACTION_DB_DO_UPDATE) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DB_DO_UPDATE);
                Utils.appendURLParameter(urlViewParams, urlParam);

                // call sub method
                doActionUpdate(request);

            } else if (request.getParameter(PARAMV_ACTION_DB_DO_DELETE) != null) {
                urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DB_DO_DELETE);
                Utils.appendURLParameter(urlViewParams, urlParam);

                // call sub method
                doActionDelete(request);

            } else if (request.getParameter(PARAM_SORT_COLUMN) != null) {
                // jump to first page when column sorting was clicked
                ContentBrowserState state = getBrowserState(request);
                state.doFirstPage();
            }

            // redirect to render method WITH URL PARAMS
            if (psmlPage != null) {
                response.sendRedirect(psmlPage + urlViewParams);
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing action:", ex);
            }
        }
    }

    /**
     * Abstract method for getting the specific Database Entities from request.
     * Must be implemented in subclass 
     * @param request
     * @return
     */
    abstract protected Object[] getDBEntities(PortletRequest request);

    /**
     * Default method for saving entities in DB.
     * Entities are extracted from request via getDBEntities()
     * @param request
     */
    protected void doActionSave(ActionRequest request) {
        Object[] entities = getDBEntities(request);
        UtilsDB.saveDBObjects(entities);
    }

    /**
     * Default method for updating entities in DB.
     * Entities are extracted from request via getDBEntities()
     * @param request
     */
    protected void doActionUpdate(ActionRequest request) {
        Object[] entities = getDBEntities(request);
        UtilsDB.updateDBObjects(entities);
    }

    /**
     * Default method for deleting entities in DB.
     * Entities are extracted from request via getDBEntities()
     * @param request
     */
    protected void doActionDelete(ActionRequest request) {
        Object[] entities = getDBEntities(request);
        UtilsDB.deleteDBObjects(entities);
    }

    /**
     * Do initialization when entering from other page (resets browser state, action form ...).
     * @param request
     */
    static protected void checkInitialEnter(RenderRequest request) {
        if (request.getParameter(PARAM_NOT_INITIAL) != null) {
            return;
        }

        // do initial stuff
        clearBrowserState(request);
        clearActionForm(request);
    }

    /**
     * Get current Action from request. returns "" if no action found.
     * @param request
     * @return
     */
    static protected String getAction(PortletRequest request) {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        return action;
    }

    /**
     * Get the column to sort after.
     * @param request
     * @param defaultValue The default column
     * @return
     */
    static protected String getSortColumn(PortletRequest request, String defaultValue) {
        ContentBrowserState state = getBrowserState(request);

        String sortCol = request.getParameter(PARAM_SORT_COLUMN);
        if (sortCol == null) {
            sortCol = state.getSortColumnName();
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

    /**
     * Get all ids in request.
     * @param request
     * @return
     */
    static protected String[] getIds(PortletRequest request) {
        String[] strIds = request.getParameterValues(PARAM_ID);
        return strIds;
    }

    /**
     * Convert Ids from String to Long.
     * @param strIds
     * @return
     */
    static protected Long[] convertIds(String[] strIds) {
        Long[] longIds = null;
        if (strIds != null) {
            longIds = (Long[]) Array.newInstance(Long.class, strIds.length);
            for (int i = 0; i < strIds.length; i++) {
                try {
                    longIds[i] = new Long(strIds[i]);
                } catch (Exception ex) {
                }
            }
        }

        return longIds;
    }

    /**
     * Get Action Form if present.
     * @param request
     * @return
     */
    static protected ActionForm getActionForm(PortletRequest request) {
        return (ActionForm) request.getPortletSession().getAttribute(KEY_ACTION_FORM);
    }

    /**
     * Clear state of ActionForm.
     * @param request
     */
    static protected void clearActionForm(PortletRequest request) {
        request.getPortletSession().removeAttribute(KEY_ACTION_FORM);
    }

    /**
     * Get current state of DB Browser.
     * NOTICE: Only ONE state for all DB Browsers (APPLICATION_SCOPE).
     * @param request
     * @return
     */
    static protected ContentBrowserState getBrowserState(PortletRequest request) {
        ContentBrowserState state = (ContentBrowserState) request.getPortletSession().getAttribute(KEY_BROWSER_STATE,
                PortletSession.APPLICATION_SCOPE);
        if (state == null) {
            state = new ContentBrowserState();
            setBrowserState(request, state);
        }
        return state;
    }

    /**
     * Set state of Browser.
     * NOTICE: Only ONE state for all DB Browsers (APPLICATION_SCOPE).
     * @param request
     * @param state
     */
    static protected void setBrowserState(PortletRequest request, ContentBrowserState state) {
        request.getPortletSession().setAttribute(KEY_BROWSER_STATE, state, PortletSession.APPLICATION_SCOPE);
    }

    /**
     * Clear state of Browser.
     * NOTICE: Only ONE state for all DB Browsers (APPLICATION_SCOPE).
     * @param request
     */
    static protected void clearBrowserState(PortletRequest request) {
        request.getPortletSession().removeAttribute(KEY_BROWSER_STATE, PortletSession.APPLICATION_SCOPE);
    }

    /**
     * Refresh Browser State (totalNumRows etc.)
     * @param request
     * @return
     */
    protected void refreshBrowserState(PortletRequest request) {
        ContentBrowserState state = getBrowserState(request);

        try {
            Criteria crit = null;
            Session session = HibernateUtil.currentSession();
            crit = session.createCriteria(dbEntityClass);
            List entities = UtilsDB.getValuesFromDB(crit, session, null, true);

            state.setTotalNumRows(entities.size());
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems refreshing browser state:", ex);
            }
        }
    }
}
