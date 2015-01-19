/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.Principal;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.forms.MapForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsBaseConverter;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.om.IngridTinyUrlSource;
import de.ingrid.portal.portlets.admin.ContentBrowserState;

public class SaveMapsPortlet extends GenericVelocityPortlet {

	private final static Logger log = LoggerFactory.getLogger(SaveMapsPortlet.class);

	private final static String CONTEXT_UTILS_STRING = "UtilsString";

	private final static String PARAM_NOT_INITIAL = "notInitial";

	private final static String TEMPLATE_FORM_INPUT = "/WEB-INF/templates/save_maps.vm";

	private final static String CONTEXT_ENTITIES = "dbEntities";

	private final static String CONTEXT_BROWSER_STATE = "browser";

	private final static String PARAM_ID = "id";

	private final static String PARAMV_ACTION_DB_DO_SAVE = "doSave";

	private final static String PARAMV_ACTION_DB_DO_DELETE = "doDelete";

	private final static String PARAMV_ACTION_DB_DO_UPDATE = "doUpdate";

	private final static String KEY_BROWSER_STATE = "browserState";

	private final static String PARAM_SORT_COLUMN = "sortColumn";

	private String psmlPage = "/portal/main-maps.psml";

	private Class dbEntityClass = IngridTinyUrlSource.class;

	private Principal principal;

	/**
	 * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
	 */
	public void init(PortletConfig config) throws PortletException {
		super.init(config);

	}

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		try {
			// add localization recources to the context
			IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(request.getLocale()), request.getLocale());
			Context context = getContext(request);
			context.put("MESSAGES", messages);
			context.put(CONTEXT_UTILS_STRING, new UtilsString());
			
			setDefaultViewPage(TEMPLATE_FORM_INPUT);
			response.setTitle(messages.getString("maps.save.url.title"));

			principal = request.getUserPrincipal();
			doViewDefault(request, principal, response);

		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Problems processing doView:", ex);
			}
		}

		super.doView(request, response);
	}

	public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {

		try {
			StringBuffer urlViewParams = new StringBuffer("?");
			MapForm mf = (MapForm) Utils.getActionForm(request, MapForm.SESSION_KEY, MapForm.class);

			mf.populate(request);
			// indicates call from same page
			String urlParam = Utils.toURLParam(PARAM_NOT_INITIAL, Settings.MSGV_TRUE);
			Utils.appendURLParameter(urlViewParams, urlParam);

			// handle ACTIONS and set action Request param for rendering
			if (request.getParameter(PARAMV_ACTION_DB_DO_SAVE) != null) {

				urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DB_DO_SAVE);
				Utils.appendURLParameter(urlViewParams, urlParam);

				String wmc = WMSInterfaceImpl.getInstance().getWMCDocument(request.getPortletSession().getId());

				if (request.getParameter("mapurl").length() < 1) {
					mf.clearErrors();
					mf.setError("mapurl", "maps.error.noName");

				} else {
					principal = request.getUserPrincipal();
					if (request.getParameter("mapurl") != null && wmc != null && principal != null) {
						saveNewUrl(request.getParameter("mapurl"), wmc, principal.getName());
					}
				}

			} else if (request.getParameter(PARAMV_ACTION_DB_DO_DELETE) != null) {
				urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DB_DO_DELETE);
				Utils.appendURLParameter(urlViewParams, urlParam);

				// call sub method
				doActionDelete(request);

			} else if (request.getParameter(PARAM_SORT_COLUMN) != null) {
				urlParam = Utils.toURLParam("sortColumn", request.getParameter("sortColumn"));
				Utils.appendURLParameter(urlViewParams, urlParam);
				// jump to first page when column sorting was clicked
				ContentBrowserState state = getBrowserState(request);
				state.doFirstPage();
			} else if (request.getParameter(PARAMV_ACTION_DB_DO_UPDATE) != null) {
				urlParam = Utils.toURLParam(Settings.PARAM_ACTION, PARAMV_ACTION_DB_DO_UPDATE);
				Utils.appendURLParameter(urlViewParams, urlParam);
				urlParam = Utils.toURLParam("id", request.getParameter("id"));
				Utils.appendURLParameter(urlViewParams, urlParam);
			}

			// redirect to render method WITH URL PARAMS
			if (psmlPage != null) {
				actionResponse.sendRedirect(actionResponse.encodeURL(psmlPage + urlViewParams));
			}
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Problems processing action:", ex);
			}
		}

	}

	private void doActionDelete(ActionRequest request) {
		Object[] entities = getDBEntities(request);
		UtilsDB.deleteDBObjects(entities);
	}

	private boolean doViewDefault(RenderRequest request, Principal principal, RenderResponse response) {
		try {

			if (principal != null) {

				// always refresh !
				refreshBrowserState(request);
				ContentBrowserState state = getBrowserState(request);
				// get data from database
				String sortColumn = getSortColumn(request, "id");
				boolean ascendingOrder = isAscendingOrder(request);
				Session session = HibernateUtil.currentSession();
				Criteria crit = session.createCriteria(dbEntityClass).add(Restrictions.eq("tinyUserRef", principal.getName()));
				if (ascendingOrder) {
					crit.addOrder(Order.asc(sortColumn));
				} else {
					crit.addOrder(Order.desc(sortColumn));
				}
				crit.setFirstResult(state.getFirstRow());
				
				List tinySources = UtilsDB.getValuesFromDB(crit, session, null, true);

				// put to render context
				Context context = getContext(request);
				context.put(CONTEXT_ENTITIES, tinySources);
				context.put(CONTEXT_BROWSER_STATE, state);

			}

			return true;
		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Problems processing default view:", ex);
			}
		}

		return false;
	}

	private void saveNewUrl(String name, String wmc, String user) {

		Transaction tx = null;
		IngridTinyUrlSource data = null;
		try {
			// save it
			Session session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			// if (name != null || wmc != null) {
			data = new IngridTinyUrlSource();
			data.setTinyUserRef(user);
			data.setTinyKey("dummy");
			data.setTinyConfig(wmc);
			data.setTinyName(name);
			session.save(data);
			data.setTinyKey(UtilsBaseConverter.toBase62(data.getId().intValue()));
			session.update(data);
			// }

			tx.commit();
		} catch (Exception ex) {
			if (tx != null) {
				tx.rollback();
			}
			if (log.isErrorEnabled()) {
				log.error("Problems saving DB entity, mapped entities=" + data, ex);
			}
		} finally {
			HibernateUtil.closeSession();
		}

		try {

		} catch (Exception ex) {
			if (log.isErrorEnabled()) {
				log.error("Problems saving data:", ex);
			}

		} finally {

		}

	}

	private String getSortColumn(PortletRequest request, String defaultValue) {
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

	private ContentBrowserState getBrowserState(PortletRequest request) {
		ContentBrowserState state = (ContentBrowserState) request.getPortletSession().getAttribute(KEY_BROWSER_STATE,
				PortletSession.APPLICATION_SCOPE);
		if (state == null) {
			state = new ContentBrowserState();
			setBrowserState(request, state);
		}
		return state;
	}

	private void setBrowserState(PortletRequest request, ContentBrowserState state) {
		request.getPortletSession().setAttribute(KEY_BROWSER_STATE, state, PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * Refresh Browser State (totalNumRows etc.)
	 * 
	 * @param request
	 * @return
	 */
	private void refreshBrowserState(PortletRequest request) {
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

	/**
	 * Get Sort Order
	 * 
	 * @param request
	 * @return true = ascending else descending
	 */
	private boolean isAscendingOrder(PortletRequest request) {
		ContentBrowserState state = getBrowserState(request);

		// if no sort column in request, then keep the state
		String sortCol = request.getParameter(PARAM_SORT_COLUMN);
		if (sortCol == null) {
			return state.isAscendingOrder();
		}

		// else process sort column
		return state.isAscendingOrder(sortCol);
	}

	private Object[] getDBEntities(PortletRequest request) {
		IngridTinyUrlSource[] dbEntities = null;
		Long[] ids = convertIds(getIds(request));
		// set up entity
		if (ids != null) {
			dbEntities = (IngridTinyUrlSource[]) Array.newInstance(IngridTinyUrlSource.class, ids.length);
			for (int i = 0; i < ids.length; i++) {
				dbEntities[i] = new IngridTinyUrlSource();
				dbEntities[i].setId(ids[i]);
				dbEntities[i].setTinyUserRef(request.getParameter("user_ref" + i));
				dbEntities[i].setTinyKey(request.getParameter("tiny_key" + i));
				dbEntities[i].setTinyName(request.getParameter("tiny_name" + i));
				dbEntities[i].setTinyConfig(request.getParameter("tiny_config" + i));
			}
		}

		return dbEntities;
	}

	/**
	 * Get all ids in request.
	 * 
	 * @param request
	 * @return
	 */
	private String[] getIds(PortletRequest request) {
		String[] strIds = request.getParameterValues(PARAM_ID);
		return strIds;
	}

	/**
	 * Convert Ids from String to Long.
	 * 
	 * @param strIds
	 * @return
	 */
	private Long[] convertIds(String[] strIds) {
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
}
