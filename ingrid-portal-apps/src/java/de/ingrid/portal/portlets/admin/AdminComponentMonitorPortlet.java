/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.forms.AdminComponentMonitorForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.scheduler.IngridMonitorFacade;
import de.ingrid.portal.scheduler.jobs.IngridMonitorAbstractJob;
import de.ingrid.portal.scheduler.jobs.IngridMonitorG2KJob;
import de.ingrid.portal.scheduler.jobs.IngridMonitorIPlugJob;
import de.ingrid.utils.PlugDescription;

/**
 * Portlet handling content management of Partners
 * 
 * @author martin@wemove.com
 */
public class AdminComponentMonitorPortlet extends GenericVelocityPortlet {

	private final static Log log = LogFactory.getLog(AdminComponentMonitorPortlet.class);

	private static final String VIEW_DEFAULT = "/WEB-INF/templates/administration/component_monitor.vm";

	private static final String VIEW_EDIT = "/WEB-INF/templates/administration/component_monitor_edit.vm";

	private static final String VIEW_NEW = "/WEB-INF/templates/administration/component_monitor_edit.vm";

	private static final String[] component_types = { "component.monitor.general.type.iplug", "component.monitor.general.type.g2k" };

	/**
	 * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
	 */
	public void init(PortletConfig config) throws PortletException {
		super.init(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
	 *      javax.portlet.RenderResponse)
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {

		IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
				request.getLocale()));
		Context context = getContext(request);
		context.put("MESSAGES", messages);
		context.put("UtilsString", new UtilsString());

		AdminComponentMonitorForm cf = (AdminComponentMonitorForm) Utils.getActionForm(request,
				AdminComponentMonitorForm.SESSION_KEY, AdminComponentMonitorForm.class);
		context.put("actionForm", cf);

		String action = request.getParameter(Settings.PARAM_ACTION);
		if (action == null) {
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_DEFAULT);
			String sortColumn = request.getParameter("sortColumn");
			boolean ascending = request.getParameter("desc") == null || !request.getParameter("desc").equals("true");
			if (sortColumn != null) {
				context.put("sortColumn", sortColumn);
				context.put("sortAsc", new Boolean(ascending));
			}
			context.put("model", IngridMonitorFacade.instance().getJobs(sortColumn, ascending));
		} else if (action.equals("viewEdit")) {
			String id = request.getParameter("id");
			if (id != null) {
				context.put("mode", "edit");
				initActionForm(cf, id, context);
				context.put("mode", "edit");
				context.put("componentTypes", component_types);
				request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Error getting job (id = null)");
				}
			}
		} else if (action.equals("viewNew")) {
			cf.clear();
			context.put("componentTypes", component_types);
			context.put("mode", "new");
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_NEW);
		} else if (action.equals("addContact")) {
			context.put("componentTypes", component_types);
			context.put("mode", request.getParameter("mode"));
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		} else if (action.equals("deleteContact")) {
			context.put("mode", request.getParameter("mode"));
			context.put("componentTypes", component_types);
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		} else if (action.equals("update")) {
			String id = request.getParameter("id");
			initActionForm(cf, id, context);
			context.put("mode", request.getParameter("mode"));
			context.put("componentTypes", component_types);
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		} else if (action.equals("new")) {
			String id = request.getParameter("id");
			if (!cf.hasErrors()) {
				initActionForm(cf, id, context);
			}
			context.put("mode", "new");
			context.put("componentTypes", component_types);
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_NEW);
		}
		super.doView(request, response);
	}

	private void initActionForm(ActionForm cf, String id, Context context) {
		try {
			IngridMonitorFacade monitor = IngridMonitorFacade.instance();
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			JobDataMap dataMap = jobDetail.getJobDataMap();
			cf.setInput(AdminComponentMonitorForm.FIELD_ACTIVE, String.valueOf(dataMap
					.getInt(IngridMonitorIPlugJob.PARAM_ACTIVE)));
			cf.setInput(AdminComponentMonitorForm.FIELD_ID, jobDetail.getName());
			cf.setInput(AdminComponentMonitorForm.FIELD_INTERVAL, String.valueOf(dataMap
					.getInt(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL)));
			cf.setInput(AdminComponentMonitorForm.FIELD_TIMEOUT, String.valueOf(dataMap
					.getInt(IngridMonitorIPlugJob.PARAM_TIMEOUT)));
			cf.setInput(AdminComponentMonitorForm.FIELD_QUERY, dataMap.getString(IngridMonitorAbstractJob.PARAM_QUERY));
			cf.setInput(AdminComponentMonitorForm.FIELD_SERVICE_URL, dataMap.getString(IngridMonitorAbstractJob.PARAM_SERVICE_URL));
			cf.setInput(AdminComponentMonitorForm.FIELD_TITLE, dataMap
					.getString(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE));
			cf.setInput(AdminComponentMonitorForm.FIELD_TYPE, dataMap
					.getString(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE));
			ArrayList contacts = (ArrayList) dataMap.get(IngridMonitorIPlugJob.PARAM_CONTACTS);
			if (contacts != null) {
				String[] emails = new String[contacts.size()];
				String[] thresholds = new String[contacts.size()];
				for (int i = 0; i < contacts.size(); i++) {
					HashMap contact = (HashMap) contacts.get(i);
					emails[i] = (String) contact.get(IngridMonitorIPlugJob.PARAM_CONTACT_EMAIL);
					thresholds[i] = ((Integer) contact
							.get(IngridMonitorIPlugJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT)).toString();
				}
				cf.setInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS, emails);
				cf.setInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS, thresholds);
			}
			context.put("actionForm", cf);
		} catch (SchedulerException e) {
			log.error("Error getting job (" + id + ")", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
	 *      javax.portlet.ActionResponse)
	 */
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		IngridMonitorFacade monitor = IngridMonitorFacade.instance();

		AdminComponentMonitorForm cf = (AdminComponentMonitorForm) Utils.getActionForm(request,
				AdminComponentMonitorForm.SESSION_KEY, AdminComponentMonitorForm.class);
		if (request.getParameter("doDelete") != null) {
			String[] plugIds = request.getParameterValues("id");
			for (int i = 0; i < plugIds.length; i++) {
				String plugId = plugIds[i];
				monitor.deleteJob(plugId);
			}
		} else if (request.getParameter("doEdit") != null) {
			String id = request.getParameter("id");
			if (id != null) {
				cf.clear();
				response.setRenderParameter("action", "viewEdit");
				response.setRenderParameter("id", request.getParameter("id"));
			}
		} else if (request.getParameter("doUpdate") != null) {
			String id = request.getParameter("id");
			response.setRenderParameter("mode", request.getParameter("mode"));
			if (id != null) {
				cf.populate(request);
				if (cf.validate()) {
					try {
						JobDetail jobDetail = monitor.getScheduler().getJobDetail(id,
								IngridMonitorFacade.SCHEDULER_GROUP_NAME);
						JobDataMap dataMap = jobDetail.getJobDataMap();

						dataMap.put(IngridMonitorIPlugJob.PARAM_ACTIVE, cf.getInputAsInteger(
								AdminComponentMonitorForm.FIELD_ACTIVE, new Integer(0)));
						dataMap.put(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL, cf.getInputAsInteger(
								AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)));
						dataMap.put(IngridMonitorIPlugJob.PARAM_TIMEOUT, cf.getInputAsInteger(
								AdminComponentMonitorForm.FIELD_TIMEOUT, new Integer(500)));
						dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE, cf
								.getInput(AdminComponentMonitorForm.FIELD_TITLE));
						String componentType = cf.getInput(AdminComponentMonitorForm.FIELD_TYPE);
						dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE, componentType);
						if (componentType.equals(IngridMonitorIPlugJob.COMPONENT_TYPE)) {
							jobDetail.setJobClass(IngridMonitorIPlugJob.class);
						}
						dataMap.put(IngridMonitorIPlugJob.PARAM_QUERY, cf
								.getInput(AdminComponentMonitorForm.FIELD_QUERY));
						dataMap.put(IngridMonitorIPlugJob.PARAM_SERVICE_URL, cf
								.getInput(AdminComponentMonitorForm.FIELD_SERVICE_URL));
						dataMap.put(IngridMonitorIPlugJob.PARAM_STATUS, IngridMonitorIPlugJob.STATUS_OK);
						dataMap
								.put(IngridMonitorIPlugJob.PARAM_STATUS_CODE,
										IngridMonitorIPlugJob.STATUS_CODE_NO_ERROR);

						ArrayList contacts = new ArrayList();
						String[] emails = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS);
						String[] thresholds = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS);
						for (int i = 0; i < emails.length; i++) {
							HashMap contact = new HashMap();
							contact.put(IngridMonitorIPlugJob.PARAM_CONTACT_EMAIL, emails[i]);
							contact.put(IngridMonitorIPlugJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT, Integer
									.valueOf(thresholds[i]));
							contacts.add(contact);
						}
						dataMap.put(IngridMonitorIPlugJob.PARAM_CONTACTS, contacts);
						monitor.getScheduler().addJob(jobDetail, true);

						jobDetail.setRequestsRecovery(false);
						Trigger trigger = TriggerUtils.makeSecondlyTrigger(cf.getInputAsInteger(
								AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)).intValue());
						trigger.setStartTime(new Date());
						trigger.setName(id);
						trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
						trigger.setJobName(id);
						trigger.setJobGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
						monitor.getScheduler().rescheduleJob(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME, trigger);
					} catch (SchedulerException e) {
						log.error("Error updating job (" + id + ").", e);
					}
				}
				response.setRenderParameter("action", "update");
				response.setRenderParameter("id", request.getParameter("id"));
			}
		} else if (request.getParameter("doNew") != null) {
			response.setRenderParameter("action", "viewNew");
		} else if (request.getParameter("doSort") != null) {
			String sortColumn = request.getParameter("sortColumn");
			if (sortColumn != null) {
				response.setRenderParameter("sortColumn", sortColumn);
			}
			String desc = request.getParameter("desc");
			if (desc != null) {
				response.setRenderParameter("desc", desc);
			}
		} else if (request.getParameter("doSave") != null) {
			String id = request.getParameter("id");
			response.setRenderParameter("mode", request.getParameter("mode"));
			cf.populate(request);
			if (cf.validate()) {
				try {
					// check for existing job id
					JobDetail detail = monitor.getScheduler().getJobDetail(AdminComponentMonitorForm.FIELD_ID,
							IngridMonitorFacade.SCHEDULER_GROUP_NAME);
					if (detail != null) {
						cf.setError(AdminComponentMonitorForm.FIELD_ID, "component.monitor.form.error.duplicate.id");
						return;
					}

					Class jobClass = null;
					if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorIPlugJob.COMPONENT_TYPE)) {
						jobClass = IngridMonitorIPlugJob.class;
					} else if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorG2KJob.COMPONENT_TYPE)) {
						jobClass = IngridMonitorG2KJob.class;
					}
					if (jobClass != null) {
						JobDetail jobDetail = new JobDetail(cf.getInput(AdminComponentMonitorForm.FIELD_ID),
								IngridMonitorFacade.SCHEDULER_GROUP_NAME, jobClass);

						JobDataMap dataMap = jobDetail.getJobDataMap();

						dataMap.put(IngridMonitorIPlugJob.PARAM_ACTIVE, cf.getInputAsInteger(
								AdminComponentMonitorForm.FIELD_ACTIVE, new Integer(0)));
						dataMap.put(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL, cf.getInputAsInteger(
								AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)));
						dataMap.put(IngridMonitorIPlugJob.PARAM_TIMEOUT, cf.getInputAsInteger(
								AdminComponentMonitorForm.FIELD_TIMEOUT, new Integer(500)));
						dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE, cf
								.getInput(AdminComponentMonitorForm.FIELD_TITLE));
						dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE, cf
								.getInput(AdminComponentMonitorForm.FIELD_TYPE));
						dataMap.put(IngridMonitorIPlugJob.PARAM_QUERY, cf
								.getInput(AdminComponentMonitorForm.FIELD_QUERY));
						dataMap.put(IngridMonitorIPlugJob.PARAM_SERVICE_URL, cf
								.getInput(AdminComponentMonitorForm.FIELD_SERVICE_URL));
						dataMap.put(IngridMonitorIPlugJob.PARAM_STATUS, IngridMonitorIPlugJob.STATUS_OK);
						dataMap
								.put(IngridMonitorIPlugJob.PARAM_STATUS_CODE,
										IngridMonitorIPlugJob.STATUS_CODE_NO_ERROR);
						dataMap.put(IngridMonitorIPlugJob.PARAM_EVENT_OCCURENCES, 1);

						ArrayList contacts = new ArrayList();
						String[] emails = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS);
						String[] thresholds = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS);
						for (int i = 0; i < emails.length; i++) {
							HashMap contact = new HashMap();
							contact.put(IngridMonitorIPlugJob.PARAM_CONTACT_EMAIL, emails[i]);
							contact.put(IngridMonitorIPlugJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT, Integer
									.valueOf(thresholds[i]));
							contacts.add(contact);
						}
						dataMap.put(IngridMonitorIPlugJob.PARAM_CONTACTS, contacts);
						jobDetail.setRequestsRecovery(false);

						Trigger trigger = TriggerUtils.makeSecondlyTrigger(cf.getInputAsInteger(
								AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(1800)).intValue());
						trigger.setStartTime(new Date());
						trigger.setName(id);
						trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
						trigger.setJobName(id);
						trigger.setJobGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
						monitor.getScheduler().scheduleJob(jobDetail, trigger);
					}
				} catch (SchedulerException e) {
					log.error("Error creating job (" + id + ").", e);
				}
				response.setRenderParameter("action", "update");
				response.setRenderParameter("id", request.getParameter("id"));
				return;
			}
			response.setRenderParameter("action", "new");
		} else if (request.getParameter("doAddContact") != null) {
			String id = request.getParameter("id");
			if (id != null) {
				response.setRenderParameter("id", id);
			}
			response.setRenderParameter("mode", request.getParameter("mode"));
			cf.populate(request);
			cf.validate();
			if (cf.getInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAIL_NEW) == "") {
				cf.setError(AdminComponentMonitorForm.FIELD_CONTACT_EMAIL_NEW,
						"component.monitor.form.error.invalid.email");
			}
			if (cf.getInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLD_NEW) == "") {
				cf.setError(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLD_NEW,
						"component.monitor.form.error.invalid.threshold");
			}
			if (!cf.hasErrors()) {
				String[] emails = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS);
				String[] thresholds = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS);
				List emailList;
				if (emails != null) {
					emailList = new ArrayList(Arrays.asList(emails));
				} else {
					emailList = new ArrayList();
				}
				List thresholdList;
				if (thresholds != null) {
					thresholdList = new ArrayList(Arrays.asList(thresholds));
				} else {
					thresholdList = new ArrayList();
				}
				emailList.add(cf.getInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAIL_NEW));
				thresholdList.add(cf.getInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLD_NEW));
				cf.setInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS, (String[]) emailList
						.toArray(new String[] {}));
				cf.setInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS, (String[]) thresholdList
						.toArray(new String[] {}));
				cf.clearInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAIL_NEW);
				cf.clearInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLD_NEW);
			}
			response.setRenderParameter("action", "addContact");
		} else if (request.getParameter("doImport") != null) {
			// remove all iplug components from job storage
			monitor.deleteAllJobs(IngridMonitorIPlugJob.COMPONENT_TYPE);

			boolean activate = (request.getParameter("activate") != null);

			PlugDescription[] iPlugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();
			for (int i = 0; i < iPlugs.length; i++) {
				PlugDescription iPlug = iPlugs[i];

				JobDetail jobDetail = new JobDetail(iPlug.getPlugId(), IngridMonitorFacade.SCHEDULER_GROUP_NAME,
						IngridMonitorIPlugJob.class);
				if (activate) {
					jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_ACTIVE, IngridMonitorIPlugJob.ACTIVE_ON);
				} else {
					jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_ACTIVE, IngridMonitorIPlugJob.ACTIVE_OFF);
				}
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL, 1800);
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_TIMEOUT, 30000);
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE, iPlug.getDataSourceName());
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE,
						IngridMonitorIPlugJob.COMPONENT_TYPE);
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_QUERY,
						"wasser iplugs:\"" + iPlug.getProxyServiceURL() + "\" ranking:any");
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_STATUS, IngridMonitorIPlugJob.STATUS_OK);
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_STATUS_CODE,
						IngridMonitorIPlugJob.STATUS_CODE_NO_ERROR);
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_EVENT_OCCURENCES, 1);

				jobDetail.setRequestsRecovery(false);

				Trigger trigger = TriggerUtils.makeSecondlyTrigger(1800);
				trigger.setStartTime(new Date());
				trigger.setName(iPlug.getPlugId());
				trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				monitor.scheduleJob(jobDetail, trigger);
			}

		} else {
			String id = request.getParameter("id");

			// delete contact
			String[] emails = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS);
			String[] thresholds = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS);
			List emailList;
			if (emails != null) {
				emailList = new ArrayList(Arrays.asList(emails));
			} else {
				emailList = new ArrayList();
			}
			List thresholdList;
			if (thresholds != null) {
				thresholdList = new ArrayList(Arrays.asList(thresholds));
			} else {
				thresholdList = new ArrayList();
			}
			for (int i = 1; i <= emailList.size(); i++) {
				if (request.getParameter("doDeleteContact_" + i) != null) {
					emailList.remove(i - 1);
					thresholdList.remove(i - 1);
					cf.setInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS, (String[]) emailList
							.toArray(new String[] {}));
					cf.setInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS, (String[]) thresholdList
							.toArray(new String[] {}));
					if (id != null) {
						response.setRenderParameter("id", id);
					}
					response.setRenderParameter("action", "deleteContact");
					response.setRenderParameter("mode", request.getParameter("mode"));
					cf.validate();
					return;
				}
			}
		}
	}

}
