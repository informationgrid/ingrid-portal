package de.ingrid.portal.upgradeclient;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import de.ingrid.portal.forms.AdminComponentUpdateForm;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.scheduler.IngridMonitorFacade;
import de.ingrid.portal.scheduler.jobs.IngridAbstractStateJob;
import de.ingrid.portal.scheduler.jobs.IngridJobHandler;
import de.ingrid.utils.PlugDescription;

public class UpgradeClient {
    private final static Log log = LogFactory.getLog(UpgradeClient.class);

    private static final String UNKNOWN_COMPONENT_ID = "NO_COMPONENT_ID";
    
    private IngridJobHandler jobHandler;

    public UpgradeClient(IngridJobHandler jobHandler) {
        this.jobHandler = jobHandler;
    }
    
    public List<IngridComponent> getComponents() {
        JobDetail jobDetail = jobHandler.getJobDetail(UpgradeTools.JOB_NAME, UpgradeTools.JOB_GROUP);
        return getComponents(jobDetail);
    }

    @SuppressWarnings("unchecked")
    public List<IngridComponent> getComponents(JobDetail jobDetail) {
        List<IngridComponent> components = (List<IngridComponent>)jobDetail.getJobDataMap().get(UpgradeTools.INSTALLED_COMPONENTS);
        if (components == null) {
            log.error("UpgradeClientJob DataMap contains non-initialized INSTALLED_COMPONENTS-Key!");
            return null;
        } else
            return components;
    }
    
    public IngridComponent getComponent(String id) {
        List<IngridComponent> components = getComponents();
        if (components == null) return null;
        for (IngridComponent component : components) {
            if (component.getId().equals(id)) {
                return component;
            }
        }
        return null;
    }
    
    public IngridComponent getComponent(String id, JobDetail jobDetail) {
        List<IngridComponent> components = getComponents(jobDetail);
        if (components == null) return null;
        for (IngridComponent component : components) {
            if (component.getId().equals(id)) {
                return component;
            }
        }
        return null;
    }
    
    public boolean updateComponent(AdminComponentUpdateForm form) {
        JobDetail jobDetail = jobHandler.getJobDetail(UpgradeTools.JOB_NAME, UpgradeTools.JOB_GROUP);
        String oldId = form.getInput(AdminComponentUpdateForm.OLD_FIELD_ID);
        String newId = form.getInput(AdminComponentUpdateForm.FIELD_ID);
        
        if (newId.isEmpty()) { 
            newId = generateNewId();
        }

        // if ID was changed check if new ID already exists
        // if so, then generate a new one with a trailing number
        if (!oldId.equals(newId)) {
            newId = generateNewId(newId);
        }
        
        IngridComponent component = getComponent(oldId, jobDetail);
        if (component == null) {
            component = new IngridComponent(newId, form.getInput(AdminComponentUpdateForm.FIELD_TYPE));
            getComponents(jobDetail).add(component);
        }
            
        if (!form.getInput(AdminComponentUpdateForm.FIELD_TITLE).isEmpty())
            component.setName(form.getInput(AdminComponentUpdateForm.FIELD_TITLE));
        component.setId(newId);
        component.setType(form.getInput(AdminComponentUpdateForm.FIELD_TYPE));
        component.setInfo(form.getInput(AdminComponentUpdateForm.FIELD_INFO));
        component.setVersion(form.getInput(AdminComponentUpdateForm.FIELD_VERSION));
        
        updateJobData(jobDetail);
        
        // update the form which is used when returning from here
        // correct ID, that might have changed here, has to be written in response
        form.setInput(AdminComponentUpdateForm.FIELD_ID, newId);
        
        return true;
    }
    
    private String generateNewId() {
        return generateNewId(UNKNOWN_COMPONENT_ID, 0);
    }
    
    private String generateNewId(String startId) {
        return generateNewId(startId, 0);
    }
    
    private String generateNewId(String startId, int num) {
        String newId = startId + "_" + Integer.toString(num);
        
        // ommit trailing if num == 0 (beginning)
        if (num == 0) {
            newId = startId;
        }
        
        List<IngridComponent> components = getComponents();
        for (IngridComponent component : components) {
            if (component.getId().equals(newId)) {
                return generateNewId(startId, ++num);
            }
        }
        return newId;
    }

    /**
     * Add a component to the list if the id doesn't exist already.
     * 
     * @param component
     * @return true if component was added, otherwise false (which means the id already existed)
     */
    public boolean addComponent(IngridComponent component) {
        // add component if its id doesn't exist already
        if (getComponent(component.getId()) == null) {
            JobDetail jobDetail = jobHandler.getJobDetail(UpgradeTools.JOB_NAME, UpgradeTools.JOB_GROUP);
            getComponents(jobDetail).add(component);
            updateJobData(jobDetail);
            return true;
        }
        return false;
    }
    
    public void updateJobData(JobDetail jobDetail) {
        try {
            IngridMonitorFacade.instance().getScheduler().addJob(jobDetail, true);
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void addComponents(List<IngridComponent> components) {
        for (IngridComponent component : components) {
            addComponent(component);
        }
    }
    
    public void removeComponent(String[] ids) {
        JobDetail jobDetail = jobHandler.getJobDetail(UpgradeTools.JOB_NAME, UpgradeTools.JOB_GROUP);
        List<IngridComponent> components = getComponents(jobDetail);
        
        for (String id : ids) {
            components.remove(getComponent(id, jobDetail));
        }
                
        updateJobData(jobDetail);        
    }

    public void importIPlugs() {
        PlugDescription[] iPlugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();
        for (PlugDescription pd : iPlugs) {
            String version = null;
            String type    = null;
            String date    = null;
            
            if (pd.getMetadata() != null) {
                version = pd.getMetadata().getVersion();
                date    = pd.getMetadata().getReleaseDate().toString();
                type    = pd.getMetadata().getPlugType().toString(); 
            }
            IngridComponent component = new IngridComponent(pd.getPlugId(), type);
            
            if (type == null || type.equals("OTHER")) {
                component.setWasUnknown(true);
                component.setType("OTHER");
            }
            
            // add a suffix for address-iPlugs (DSCs)
            if (pd.getPlugId().endsWith("_addr"))
                component.setName(pd.getDataSourceName() + "(address)");
            else
                component.setName(pd.getDataSourceName());
            //component.setDate(date);
            component.setVersion(version);
            component.setIPlug(true);
            component.setStatus(IngridAbstractStateJob.STATUS_IS_AVAILABLE);
            
            addComponent(component);
        }
        
    }

    public void addEmail(String id, String email) {
        JobDetail jobDetail = jobHandler.getJobDetail(UpgradeTools.JOB_NAME, UpgradeTools.JOB_GROUP);
        
        IngridComponent component = getComponent(id, jobDetail);
        component.addEmail(email);
        
        // reset hasBeenSent-status, since email has been added and the user
        // needs to be informed
        component.setHasBeenSent(false);
                
        updateJobData(jobDetail);
    }

    public void removeEmail(String id, String[] emails) {
        JobDetail jobDetail = jobHandler.getJobDetail(UpgradeTools.JOB_NAME, UpgradeTools.JOB_GROUP);
        
        getComponent(id, jobDetail).removeEmail(emails);
                
        updateJobData(jobDetail);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getComponentTypes() {
        JobDetail jobDetail = jobHandler.getJobDetail(UpgradeTools.JOB_NAME, UpgradeTools.JOB_GROUP);
        return (Set<String>)jobDetail.getJobDataMap().get(UpgradeTools.COMPONENT_TYPES);
    }
}
