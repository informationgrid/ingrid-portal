/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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
import org.apache.velocity.tools.generic.SortTool;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.forms.AdminComponentMonitorForm;
import de.ingrid.portal.forms.AdminComponentUpdateForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.scheduler.IngridMonitorFacade;
import de.ingrid.portal.scheduler.jobs.IngridAbstractStateJob;
import de.ingrid.portal.scheduler.jobs.IngridJobHandler;
import de.ingrid.portal.scheduler.jobs.IngridMonitorAbstractJob;
import de.ingrid.portal.scheduler.jobs.IngridMonitorIPlugJob;
import de.ingrid.portal.upgradeclient.IngridComponent;
import de.ingrid.portal.upgradeclient.UpgradeClient;
import de.ingrid.portal.upgradeclient.UpgradeTools;


/**
 * Portlet handling content management of Partners
 * 
 * @author martin@wemove.com
 */
public class AdminComponentMonitorPortlet extends GenericVelocityPortlet {

	private final static Log log = LogFactory.getLog(AdminComponentMonitorPortlet.class);

	private static final String VIEW_DEFAULT = "/WEB-INF/templates/administration/component_monitor.vm";

	private static final String VIEW_EDIT = "/WEB-INF/templates/administration/component_monitor_edit.vm";

	private static final String VIEW_NEW = "/WEB-INF/templates/administration/component_monitor_new.vm";

    private static final String VIEW_UPDATES = "/WEB-INF/templates/administration/updates.vm";
    
    private static final String VIEW_UPDATES_EDIT = "/WEB-INF/templates/administration/updates_edit.vm";
    
	private IngridJobHandler jobHandler;
	
	private UpgradeClient upgradeClient;

	/**
	 * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
	 */
	public void init(PortletConfig config) throws PortletException {
		super.init(config);
		jobHandler = new IngridJobHandler();
		upgradeClient = new UpgradeClient(jobHandler);
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
		
        // set localized title for this page
        response.setTitle(messages.getString("component.monitor.title"));

		AdminComponentMonitorForm cf = (AdminComponentMonitorForm) Utils.getActionForm(request,
				AdminComponentMonitorForm.SESSION_KEY, AdminComponentMonitorForm.class);
		context.put("actionForm", cf);
		AdminComponentUpdateForm cfUpdate = (AdminComponentUpdateForm) Utils.getActionForm(request,
                AdminComponentUpdateForm.SESSION_KEY, AdminComponentUpdateForm.class);
		context.put("actionFormUpdate", cfUpdate);

		String action = request.getParameter(Settings.PARAM_ACTION);
		if (action == null) {
	        // check if upgrade-job already exists, otherwise create it
		    // do it here, so that this function isn't called so often
		    checkForUpgradeClient();
	        
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_DEFAULT);
			String sortColumn = request.getParameter("sortColumn");
			boolean ascending = request.getParameter("desc") == null || !request.getParameter("desc").equals("true");
			//if (sortColumn != null) {
				context.put("sortColumn", sortColumn);
				context.put("sortAsc", new Boolean(ascending));
			//}
			context.put("jobHandler", jobHandler);
			context.put("filterMap", new HashMap());
			//context.put("model", jobHandler.getJobs(sortColumn, ascending));
		// ------------------viewEdit-------------------------
		} else if (action.equals("viewEdit")) {
			String id = request.getParameter("id");
			if (id != null) {
				context.put("mode", "edit");
				initActionForm(cf, id, context);
				if (!jobHandler.isMonitorJob(id))
					context.put("disableSaving", "true");
				context.put("componentTypes", jobHandler.getComponentTypes());
				request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Error getting job (id = null)");
				}
			}
		// ------------------viewNew-------------------------
		} else if (action.equals("viewNew")) {
			cf.clear();
			context.put("componentTypes", jobHandler.getComponentTypes());
			context.put("mode", "new");
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_NEW);
		// ------------------addContact-------------------------
		} else if (action.equals("addContact")) {
			context.put("componentTypes", jobHandler.getComponentTypes());
			context.put("mode", request.getParameter("mode"));
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		// ------------------deleteContact-------------------------
		} else if (action.equals("deleteContact")) {
			context.put("mode", request.getParameter("mode"));
			context.put("componentTypes", jobHandler.getComponentTypes());
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		// ------------------update-------------------------
		} else if (action.equals("update")) {
			cf.clear();
			String id = request.getParameter("id");
			initActionForm(cf, id, context);
			context.put("mode", request.getParameter("mode"));
			context.put("disableSaving", request.getParameter("disableSaving"));
			context.put("componentTypes", jobHandler.getComponentTypes());
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		// ------------------reshow-------------------------
		} else if (action.equals("reshow")) {
			context.put("mode", request.getParameter("mode"));
			context.put("disableSaving", request.getParameter("disableSaving"));
			context.put("componentTypes", jobHandler.getComponentTypes());
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
		// ------------------editAfterNew-------------------
		} else if (action.equals("editAfterNew")) {
			String id = request.getParameter("id");
			String type = request.getParameter("type");
			context.put("mode", "new");
			cf.setInput(AdminComponentMonitorForm.FIELD_ID, request.getParameter("id"));
			cf.setInput(AdminComponentMonitorForm.FIELD_TYPE, request.getParameter("type"));

			// setting some standard values
			cf.setInput(AdminComponentMonitorForm.FIELD_TIMEOUT, "30000");
			cf.setInput(AdminComponentMonitorForm.FIELD_INTERVAL, "1800");
			
			if (jobHandler.jobExists(id)) {
				cf.setError(AdminComponentMonitorForm.FIELD_ID, "component.monitor.form.error.duplicate.id");
			} 
			if (jobHandler.jobTypeOnlyOnce(request.getParameter("type"))) {
				cf.setError(AdminComponentMonitorForm.FIELD_TYPE, "component.monitor.form.error.single.job");
			}
			if (cf.hasErrors()) {
				context.put("type", type);
				context.put("componentTypes", jobHandler.getComponentTypes());
				request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_NEW);
			} else {
				request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_EDIT);
			}
		// ------------------new----------------------------
		} else if (action.equals("new")) {
			String id = request.getParameter("id");
			if (!cf.hasErrors()) {
				initActionForm(cf, id, context);
			}
			context.put("mode", "new");
			context.put("componentTypes", jobHandler.getComponentTypes());
			request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_NEW);
		} else if (action.equals("resetTime")) {
			
		// ------------------showUpdates--------------------
		} else if (action.equals("showUpdates")) {
		    cfUpdate.clear();
		    response.setTitle(messages.getString("component.monitor.update.title"));
		    request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_UPDATES);
		    context.put("components", upgradeClient.getComponents());
		    context.put("sorter", new SortTool());
        // ------------------updateAddComponent--------------------
        } else if (action.equals("updateAddComponent")) {
            response.setTitle(messages.getString("component.monitor.update.title"));
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_UPDATES_EDIT);
            cfUpdate.clear();
            context.put("actionForm", cfUpdate);
            IngridComponent component = new IngridComponent("new", "");
            component.setIPlug(false);
            context.put("component", component);
            context.put("componentTypes", upgradeClient.getComponentTypes());
        // ------------------updateEditComponent--------------------
        } else if (action.equals("updateEditComponent")) {
            response.setTitle(messages.getString("component.monitor.update.title"));
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_UPDATES_EDIT);
            if (!cfUpdate.hasErrors()) {
                cfUpdate.initialize(upgradeClient.getComponent((String)request.getParameter("id")));
            }
            context.put("actionForm", cfUpdate);
            
            context.put("component", upgradeClient.getComponent((String)request.getParameter("id")));
            context.put("componentTypes", upgradeClient.getComponentTypes());
        }
		
		super.doView(request, response);
	}

	
	/**
	 * 
	 */
	private void checkForUpgradeClient() {
        if (!jobHandler.jobExists("UpgradeClientJob", "UPDATE")) {
            jobHandler.addUpgradeClientJob();
        }	    
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
	 *      javax.portlet.ActionResponse)
	 */
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		String id = "";
		AdminComponentMonitorForm cf = (AdminComponentMonitorForm) Utils.getActionForm(request,
				AdminComponentMonitorForm.SESSION_KEY, AdminComponentMonitorForm.class);
		AdminComponentUpdateForm cfUpdate = (AdminComponentUpdateForm) Utils.getActionForm(request,
		        AdminComponentUpdateForm.SESSION_KEY, AdminComponentUpdateForm.class);
		// ------------------doDelete-------------------------
		if (request.getParameter("doDelete") != null) {
			jobHandler.removeJob(request.getParameterValues("id"));
		// ------------------doEdit-------------------------
		} else if (request.getParameter("doEdit") != null) {
			id = request.getParameter("id");
			if (id != null) {
				cf.clear();
				response.setRenderParameter("action", "viewEdit");
				response.setRenderParameter("id", id);
			}
		// ------------------doUpdate-------------------------
		} else if (request.getParameter("doUpdate") != null) {
			id = request.getParameter("id");
			String mode = request.getParameter("mode"); 
			response.setRenderParameter("mode", mode);
			
			if (id != null) {
				cf.populate(request);
				if (cf.validate()) {
					if (mode.equals("new")) {
						if (jobHandler.newJob(cf) == true) {
							response.setRenderParameter("action", "");
							response.setRenderParameter("id", id);
							response.setRenderParameter("mode", "edit");
						} else {
							response.setRenderParameter("action", "reshow");
						}
					} else {
						if (jobHandler.updateJob(id, cf) == false) {
							response.setRenderParameter("action", "reshow");
						} else {
							response.setRenderParameter("action", "");
						}
					}
				} else {
					response.setRenderParameter("action", "reshow");
				}
			}
		// ------------------doNew-------------------------
		} else if (request.getParameter("doNew") != null) {
			response.setRenderParameter("action", "viewNew");
		// ------------------doSort-------------------------
		} else if (request.getParameter("doSort") != null) {
			String sortColumn = request.getParameter("sortColumn");
			if (sortColumn != null) {
				response.setRenderParameter("sortColumn", sortColumn);
			}
			String desc = request.getParameter("desc");
			if (desc != null) {
				response.setRenderParameter("desc", desc);
			}
		// ------------------doSave-(newJob)------------------	
		} else if (request.getParameter("doSave") != null) {
			id = request.getParameter("id");
			cf.populate(request);
			response.setRenderParameter("action", "editAfterNew");
			response.setRenderParameter(AdminComponentMonitorForm.FIELD_ID, id);
			response.setRenderParameter(AdminComponentMonitorForm.FIELD_TYPE, request.getParameter("type"));
		// ------------------doAddContact-------------------------
		} else if (request.getParameter("doAddContact") != null) {
			id = request.getParameter("id");
			String mode = request.getParameter("mode");
			if (id != null) {
				response.setRenderParameter("id", id);
			}
			response.setRenderParameter("mode", mode);
			cf.populate(request);
			cf.validate();
			addContact(request, response, cf);
			if (!mode.equals("new")) {
				addStatusInfo(cf, jobHandler.getJobDataMap(id), id);
			}
			response.setRenderParameter("action", "addContact");
		// ------------------doImport-------------------------
		} else if (request.getParameter("doImport") != null) {
			// set all imported jobs to active!?
			jobHandler.importJobs(request);
		// ------------------doRefresh-------------------------
		} else if (request.getParameter("doRefresh") != null) {
			// do nothing, but set action to null
			response.setRenderParameter("action", "");
		// ------------------doCancel-------------------------
		} else if (request.getParameter("doCancel") != null) {
			// do nothing, but set action to null
			response.setRenderParameter("action", "");
		// ------------------doDeleteContact-------------------------
		} else if (parameterContains(request, "doDeleteContact_")){ // the name of the form element contains the number of email entry to be removed
			removeContact(request, response, cf);
			if (id != null) {
				response.setRenderParameter("id", id);
			}
			response.setRenderParameter("action", "deleteContact");
			response.setRenderParameter("mode", request.getParameter("mode"));
		// ------------------doResetTime-------------------------
		} else if (request.getParameter("doResetTime") != null) {
			jobHandler.resetTime(request.getParameter("id"));
			response.setRenderParameter("id", request.getParameter("id"));
			response.setRenderParameter("action", "update");
			response.setRenderParameter("mode", "edit");
			response.setRenderParameter("disableSaving", request.getParameter("disableSaving"));
		// ------------------doShowUpdates-----------------------
		} else if (request.getParameter("doShowUpdates") != null) {
		    response.setRenderParameter("action", "showUpdates");
		// ------------------doUpdateRemove----------------------
		} else if (request.getParameter("doUpdateRemove") != null) {
            response.setRenderParameter("action", "showUpdates");
            //response.setRenderParameter("id", "updateRemoveComponent");
		    upgradeClient.removeComponent(request.getParameterValues("id"));
        // ------------------doUpdateAdd-------------------------
		} else if (request.getParameter("doUpdateAdd") != null) {
            response.setRenderParameter("action", "updateAddComponent");
        // ------------------doUpdateRefresh---------------------
		} else if (request.getParameter("doUpdateImport") != null) {
            response.setRenderParameter("action", "showUpdates");
            // import iPlugs
            upgradeClient.importIPlugs();
            
            // start UpgradeClient-Job
        // ------------------doUpdateEdit---------------------
        } else if (request.getParameter("doUpdateEdit") != null) {
            response.setRenderParameter("id", request.getParameter("id"));
            response.setRenderParameter("action", "updateEditComponent");
        // ------------------doUpdateAddEmail---------------------
        } else if (request.getParameter("doUpdateAddEmail") != null) {
            response.setRenderParameter("id", request.getParameter("componentId"));
            response.setRenderParameter("action", "updateEditComponent");
            cfUpdate.populate(request);
            upgradeClient.updateComponent(cfUpdate);
            if (cfUpdate.validate())
                upgradeClient.addEmail(request.getParameter("componentId"), request.getParameter(AdminComponentUpdateForm.FIELD_CONTACT_EMAILS_NEW));
        // ------------------doUpdateDeleteEmail---------------------
        } else if (request.getParameter("doUpdateDeleteEmail") != null) {
            response.setRenderParameter("id", request.getParameter("componentId"));
            response.setRenderParameter("action", "updateEditComponent");
            upgradeClient.removeEmail(request.getParameter("componentId"), request.getParameterValues("contact_email_marked"));
        // ------------------doSaveUpdates---------------------
        } else if (request.getParameter("doSaveUpdates") != null) {
            response.setRenderParameter("id", request.getParameter("componentId"));
            response.setRenderParameter("action", "showUpdates");
            cfUpdate.populate(request);
            upgradeClient.updateComponent(cfUpdate);
            if (!cfUpdate.validate()) {
                response.setRenderParameter("action", "updateEditComponent");
                response.setRenderParameter("id", cfUpdate.getInput(AdminComponentUpdateForm.FIELD_ID));
            }
        } else if (request.getParameter("doRestartUpgradeJob") != null) {
            response.setRenderParameter("action", "showUpdates");
            IngridMonitorFacade monitor = IngridMonitorFacade.instance();
            try {
                monitor.triggerJob(UpgradeTools.JOB_NAME, UpgradeTools.JOB_GROUP);
            } catch (SchedulerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
		} else {
			// unhandled action
			log.error("Action could not be processed, because it's unknown!");
		}
	}


	/**
	 * This function checks if a parameter starts with a given string and returns
	 * true if there is one. This is useful when information is appended on a form
	 * element's name for example.
	 * 
	 * @param request, the request object
	 * @param str, the string a parameter should start with
	 * @return 	true, if a requestParameter starts with str
	 * 			false, if there's no parameter that starts with str	
	 */
	private boolean parameterContains( ActionRequest request, String str ) {
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			if (paramNames.nextElement().toString().startsWith(str)) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * The form will be read
	 * @param request
	 * @param response
	 * @param cf
	 */
	private void addContact(ActionRequest request, ActionResponse response,
			AdminComponentMonitorForm cf) {
		
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
		
	}

	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param cf
	 */
	private void removeContact(ActionRequest request, ActionResponse response,
			AdminComponentMonitorForm cf) {
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
				cf.validate();
				return;
			}
		}
	}
	
	
	/**
	 * 
	 * @param cf
	 * @param id
	 * @param context
	 */
	private void initActionForm(ActionForm cf, String id, Context context) {
		
		JobDataMap dataMap = jobHandler.getJobDataMap(id);
		
		if (dataMap.containsKey(IngridMonitorIPlugJob.PARAM_ACTIVE))		
			cf.setInput(AdminComponentMonitorForm.FIELD_ACTIVE,
					String.valueOf(dataMap.getInt(IngridMonitorIPlugJob.PARAM_ACTIVE)));
		
		cf.setInput(AdminComponentMonitorForm.FIELD_ID,
				jobHandler.getJobName(id));
		
		if (dataMap.containsKey(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL))
			cf.setInput(AdminComponentMonitorForm.FIELD_INTERVAL,
				String.valueOf(dataMap.getInt(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL)));
		
		if (dataMap.containsKey(IngridMonitorIPlugJob.PARAM_TIMEOUT))
			cf.setInput(AdminComponentMonitorForm.FIELD_TIMEOUT,
				String.valueOf(dataMap.getInt(IngridMonitorIPlugJob.PARAM_TIMEOUT)));
		
		if (dataMap.containsKey(IngridMonitorIPlugJob.PARAM_SERVICE_URL))
			cf.setInput(AdminComponentMonitorForm.FIELD_SERVICE_URL,
				dataMap.getString(IngridMonitorAbstractJob.PARAM_SERVICE_URL));
		
		if (dataMap.containsKey(IngridMonitorIPlugJob.PARAM_QUERY))
			cf.setInput(AdminComponentMonitorForm.FIELD_QUERY,
				dataMap.getString(IngridMonitorAbstractJob.PARAM_QUERY));
		
		if (dataMap.containsKey(IngridMonitorIPlugJob.PARAM_EXCLUDED_PROVIDER))
			cf.setInput(AdminComponentMonitorForm.FIELD_EXCLUDED_PROVIDER,
				dataMap.getString(IngridMonitorAbstractJob.PARAM_EXCLUDED_PROVIDER));
		
		if (dataMap.containsKey(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE))
			cf.setInput(AdminComponentMonitorForm.FIELD_TITLE,
				dataMap.getString(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE));
		
		if (dataMap.containsKey(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE))
			cf.setInput(AdminComponentMonitorForm.FIELD_TYPE,
				dataMap.getString(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE));
		

		// add information about time execution
		addStatusInfo(cf, dataMap, id);
		
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
	}
	
	private void addStatusInfo(ActionForm cf, JobDataMap dataMap, String id) {
		String lastExec   = "";
		String nextExec   = "";
		String lastOkExec = "";
		
		// for jobs that never ran there's no information -> return! 
		if (dataMap.containsKey(IngridMonitorAbstractJob.PARAM_TIMER_NUM) == false) {
			// write at least the error message
			cf.setInput(AdminComponentMonitorForm.FIELD_ERROR_MSG, dataMap.getString(IngridMonitorAbstractJob.PARAM_STATUS_CODE));
			return;
		}
		
		Trigger trigger = jobHandler.getTrigger(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		
		if (trigger == null)
			trigger = jobHandler.getTrigger(id,"DEFAULT");
		if (trigger == null)
            trigger = jobHandler.getTrigger(id,UpgradeTools.JOB_GROUP);
		
		SimpleDateFormat portalFormat = new SimpleDateFormat("yyyy-mm-dd H:mm:ss");
        
        portalFormat.applyPattern("yyyy-MM-dd H:mm:ss");
        
        if (trigger.getPreviousFireTime()!=null) {
	        lastExec = portalFormat.format(trigger.getPreviousFireTime());
	        lastOkExec = dataMap.getString(IngridAbstractStateJob.PARAM_LAST_ERRORFREE_RUN);
        }
        
        nextExec = portalFormat.format(trigger.getNextFireTime()); //  portalFormat.format((Date) dataMap.get(IngridMonitorAbstractJob.PARAM_NEXT_CHECK));
		
		cf.setInput(AdminComponentMonitorForm.FIELD_LAST_EXECUTION, lastExec);
		cf.setInput(AdminComponentMonitorForm.FIELD_NEXT_EXECUTION, nextExec);
		cf.setInput(AdminComponentMonitorForm.FIELD_LAST_OK_EXECUTION, lastOkExec);
		cf.setInput(AdminComponentMonitorForm.FIELD_NUM_EXECUTIONS, String.valueOf(dataMap.getInt(IngridMonitorAbstractJob.PARAM_TIMER_NUM)));
		cf.setInput(AdminComponentMonitorForm.FIELD_ERROR_MSG, dataMap.getString(IngridMonitorAbstractJob.PARAM_STATUS_CODE));
		
		String execTime = "n/a ";
		if (dataMap.get(IngridMonitorAbstractJob.PARAM_TIMER_AVERAGE) != null) {
			execTime = String.valueOf(dataMap.getLong(IngridMonitorAbstractJob.PARAM_TIMER_AVERAGE));
		}
		cf.setInput(AdminComponentMonitorForm.FIELD_AVERAGE_EXECTIME, execTime);
	}
}
