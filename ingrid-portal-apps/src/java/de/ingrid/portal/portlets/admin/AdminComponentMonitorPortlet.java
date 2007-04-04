/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

	private static final String[] component_types = { "component.monitor.general.type.iplug",
			"component.monitor.general.type.csw", "component.monitor.general.type.csw",
			"component.monitor.general.type.g2k" };

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
			String sortBy = request.getParameter("sortby");
			boolean ascending = request.getParameter("desc") == null || !request.getParameter("desc").equals("true");
			context.put("model", IngridMonitorFacade.instance().getJobs(sortBy, ascending));
		} else if (action.equals("viewEdit")) {
			String id = request.getParameter("id");
			if (id != null) {
				initActionForm(cf, id, context);
				request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Error getting job (id = null)");
				}
			}
		} else if (action.equals("new")) {
			context.put("mode", "create");
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_NEW);
		} else if (action.equals("addContact")) {
			String id = request.getParameter("id");
			initActionForm(cf, id, context);
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		} else if (action.equals("deleteContact")) {
			String id = request.getParameter("id");
			initActionForm(cf, id, context);
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		} else if (action.equals("update")) {
			String id = request.getParameter("id");
			initActionForm(cf, id, context);
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		}
		super.doView(request, response);
	}

	private void initActionForm(ActionForm cf, String id, Context context) {
		try {
			IngridMonitorFacade monitor = IngridMonitorFacade.instance();
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id,
					IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			JobDataMap dataMap = jobDetail.getJobDataMap();
			cf.setInput(AdminComponentMonitorForm.FIELD_ACTIVE, String.valueOf(dataMap.getInt(IngridMonitorIPlugJob.PARAM_ACTIVE)));
			cf.setInput(AdminComponentMonitorForm.FIELD_ID, jobDetail.getName());
			cf.setInput(AdminComponentMonitorForm.FIELD_INTERVAL, String.valueOf(dataMap.getInt(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL)));
			cf.setInput(AdminComponentMonitorForm.FIELD_QUERY, dataMap.getString(IngridMonitorIPlugJob.PARAM_QUERY));
			cf.setInput(AdminComponentMonitorForm.FIELD_TITLE, dataMap.getString(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE));
			cf.setInput(AdminComponentMonitorForm.FIELD_TYPE, dataMap.getString(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE));
			ArrayList contacts = (ArrayList)dataMap.get(IngridMonitorIPlugJob.PARAM_CONTACTS);
			if (contacts != null) {
				String[] emails = new String[contacts.size()];
				String[] thresholds = new String[contacts.size()];
				for (int i=0; i<contacts.size(); i++) {
					HashMap contact = (HashMap)contacts.get(i);
					emails[i] = (String)contact.get(IngridMonitorIPlugJob.PARAM_CONTACT_EMAIL);
					thresholds[i] = ((Integer)contact.get(IngridMonitorIPlugJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT)).toString();
				}
				cf.setInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS, emails);
				cf.setInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS, thresholds);
			}
			context.put("actionForm", cf);
			context.put("mode", "edit");
			context.put("componentTypes", component_types);
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
			if (id != null) {
				cf.populate(request);
				if (cf.validate()) {
					try {
						JobDetail jobDetail = monitor.getScheduler().getJobDetail(id,
								IngridMonitorFacade.SCHEDULER_GROUP_NAME);
						JobDataMap dataMap = jobDetail.getJobDataMap();
						
						
						dataMap.put(IngridMonitorIPlugJob.PARAM_ACTIVE, cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_ACTIVE, Integer.valueOf(0)));
						dataMap.put(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL, cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_INTERVAL, Integer.valueOf(30)));
						dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE, cf.getInput(AdminComponentMonitorForm.FIELD_TITLE));
						String componentType = cf.getInput(AdminComponentMonitorForm.FIELD_TYPE);
						dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE, componentType);
						if (componentType.equals(IngridMonitorIPlugJob.COMPONENT_TYPE)) {
							jobDetail.setJobClass(IngridMonitorIPlugJob.class);
						}
						dataMap.put(IngridMonitorIPlugJob.PARAM_QUERY, cf.getInput(AdminComponentMonitorForm.FIELD_QUERY));
						dataMap.put(IngridMonitorIPlugJob.PARAM_STATUS, IngridMonitorIPlugJob.STATUS_OK);
						dataMap.put(IngridMonitorIPlugJob.PARAM_STATUS_CODE,	IngridMonitorIPlugJob.STATUS_CODE_NO_ERROR);
						
						ArrayList contacts = new ArrayList();
						String[] emails = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS);
						String[] thresholds = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS);
						for (int i=0; i<emails.length; i++) {
							HashMap contact = new HashMap();
							contact.put(IngridMonitorIPlugJob.PARAM_CONTACT_EMAIL, emails[i]);
							contact.put(IngridMonitorIPlugJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT, Integer.valueOf(thresholds[i]));
							contacts.add(contact);
						}
						dataMap.put(IngridMonitorIPlugJob.PARAM_CONTACTS, contacts);
						monitor.getScheduler().addJob(jobDetail, true);
						
						jobDetail.setRequestsRecovery(false);
						Trigger trigger = TriggerUtils.makeSecondlyTrigger(cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_INTERVAL, Integer.valueOf(30)).intValue());
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
		} else if (request.getParameter("doCreate") != null) {
			response.setRenderParameter("action", "new");
		} else if (request.getParameter("doAddContact") != null) {
			String id = request.getParameter("id");
			response.setRenderParameter("id", id);
			if (id != null) {
				try {
					JobDetail job = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
					cf.populate(request);
					if (cf.validate()) {
						if (cf.getInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAIL_NEW) == "") {
							cf.setError(AdminComponentMonitorForm.FIELD_CONTACT_EMAIL_NEW, "component.monitor.form.error.invalid.email");
						}
						if (cf.getInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLD_NEW) == "") {
							cf.setError(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLD_NEW, "component.monitor.form.error.invalid.threshold");
						}
						if (!cf.hasErrors()) {
							JobDataMap jobData = job.getJobDataMap();
							ArrayList contacts = (ArrayList) jobData.get(IngridMonitorAbstractJob.PARAM_CONTACTS);
							if (contacts == null) {
								contacts = new ArrayList();
							}
							HashMap contact = new HashMap();
							contact.put(IngridMonitorAbstractJob.PARAM_CONTACT_EMAIL, cf
									.getInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAIL_NEW));
							contact.put(IngridMonitorAbstractJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT, Integer.valueOf(cf
									.getInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLD_NEW)));
							contacts.add(contact);
							jobData.put(IngridMonitorAbstractJob.PARAM_CONTACTS, contacts);
							monitor.getScheduler().addJob(job, true);
							cf.clearInput(AdminComponentMonitorForm.FIELD_CONTACT_EMAIL_NEW);
							cf.clearInput(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLD_NEW);
						}
					}
				} catch (SchedulerException e) {
					log.error("Error adding new contact to job.", e);
				}
			}
			response.setRenderParameter("action", "addContact");
		} else if (request.getParameter("doImport") != null) {
			// remove all iplug components from job storage
			monitor.deleteAllJobs(IngridMonitorIPlugJob.COMPONENT_TYPE);

			PlugDescription[] iPlugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();
			for (int i = 0; i < iPlugs.length; i++) {
				PlugDescription iPlug = iPlugs[i];

				JobDetail jobDetail = new JobDetail(iPlug.getPlugId(), IngridMonitorFacade.SCHEDULER_GROUP_NAME,
						IngridMonitorIPlugJob.class);
				jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_ACTIVE, IngridMonitorIPlugJob.ACTIVE_OFF);
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
			try {
				JobDetail job = monitor.getScheduler().getJobDetail(id,
						IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				JobDataMap jobData = job.getJobDataMap();
				ArrayList contacts = (ArrayList) jobData.get(IngridMonitorAbstractJob.PARAM_CONTACTS);
				if (contacts != null) {
					for (int i = 1; i <= contacts.size(); i++) {
						if (request.getParameter("doDeleteContact_" + i) != null) {
							contacts.remove(i - 1);
							monitor.getScheduler().addJob(job, true);
							response.setRenderParameter("id", id);
							response.setRenderParameter("action", "deleteContact");
							break;
						}
					}
				}
			} catch (SchedulerException e) {
				log.error("Error adding new contact to job.", e);
			}
		}
	}

}
