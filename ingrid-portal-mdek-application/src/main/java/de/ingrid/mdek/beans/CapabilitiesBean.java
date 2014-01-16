package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.List;

import de.ingrid.mdek.beans.object.AddressBean;
import de.ingrid.mdek.beans.object.ConformityBean;
import de.ingrid.mdek.beans.object.LocationBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.object.OperationBean;
import de.ingrid.mdek.beans.object.SpatialReferenceSystemBean;
import de.ingrid.mdek.beans.object.TimeReferenceBean;
import de.ingrid.mdek.beans.object.UrlBean;

public class CapabilitiesBean {

	private String title;
	private String description;
	private String serviceType;
	private List<String> versions;

	private List<OperationBean> operations;
	private List<String> keywords;
	private List<TimeReferenceBean> timeSpans;
	private List<TimeReferenceBean> timeReferences;
	private List<ConformityBean> conformities;
	private List<MdekDataBean> coupledResources;
	private List<SpatialReferenceSystemBean> spatialReferenceSystems;


    private String fees;
    private List<String> accessConstraints;
    private List<UrlBean> onlineResources;
    private List<UrlBean> resourceLocators;
    private Integer dataServiceType;
    private List<LocationBean> boundingBoxes;
    
    private AddressBean address;
    
    public CapabilitiesBean() {
        timeReferences = new ArrayList<TimeReferenceBean>();
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public List<OperationBean> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationBean> operations) {
		this.operations = operations;
	}

	public List<String> getVersions() {
		return versions;
	}

	public void setVersions(List<String> versions) {
		this.versions = versions;
	}

	public List<String> getKeywords() {
	    if (keywords == null) {
	        keywords = new ArrayList<String>();
	    }
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

    /**
     * @param evaluate
     */
    public void setFees(String value) {
        this.fees = value;        
    }

    /**
     * @return
     */
    public String getFees() {
        return this.fees;
    }

    /**
     * @param evaluate
     */
    public void setAccessConstraints(List<String> value) {
        this.accessConstraints = value;
        
    }

    /**
     * @return
     */
    public List<String> getAccessConstraints() {
        return this.accessConstraints;
    }

    /**
     * @param evaluate
     */
    public void setOnlineResources(List<UrlBean> value) {
        this.onlineResources = value;        
    }

    /**
     * @return
     */
    public List<UrlBean> getOnlineResources() {
        return this.onlineResources;
    }

    /**
     * @param string
     */
    public void setDataServiceType(Integer mappedType) {
        this.dataServiceType = mappedType;
    }

    public Integer getDataServiceType() {
        return this.dataServiceType;
    }

    /**
     * @param mapToTimeReferenceBean
     */
    public void addTimeReference(TimeReferenceBean ref) {
        if (ref != null)
            timeReferences.add(ref);
        
    }
    
    /**
     * @return the timeReferences
     */
    public List<TimeReferenceBean> getTimeReferences() {
        return timeReferences;
    }

    /**
     * @param timeReferences the timeReferences to set
     */
    public void setTimeReferences(List<TimeReferenceBean> timeReferences) {
        this.timeReferences = timeReferences;
    }

    public List<ConformityBean> getConformities() {
        return conformities;
    }

    public void setConformities(List<ConformityBean> conformities) {
        this.conformities = conformities;
    }

    public List<MdekDataBean> getCoupledResources() {
        return coupledResources;
    }

    public void setCoupledResources(List<MdekDataBean> coupledResources) {
        this.coupledResources = coupledResources;
    }

    public List<SpatialReferenceSystemBean> getSpatialReferenceSystems() {
        return spatialReferenceSystems;
    }

    public void setSpatialReferenceSystems(List<SpatialReferenceSystemBean> spatialReferenceSystems) {
        this.spatialReferenceSystems = spatialReferenceSystems;
    }

    /**
     * @param boundingBoxesFromLayers
     */
    public void setBoundingBoxes(List<LocationBean> boundingBoxes) {
        this.boundingBoxes = boundingBoxes;
        
    }
    
    public List<LocationBean> getBoundingBoxes() {
        return this.boundingBoxes;
    }

    public AddressBean getAddress() {
        return address;
    }

    public void setAddress(AddressBean address) {
        this.address = address;
    }

    public List<TimeReferenceBean> getTimeSpans() {
        return timeSpans;
    }

    public void setTimeSpans(List<TimeReferenceBean> timeSpans) {
        this.timeSpans = timeSpans;
    }

    public List<UrlBean> getResourceLocators() {
        return resourceLocators;
    }

    public void setResourceLocators(List<UrlBean> resourceLocators) {
        this.resourceLocators = resourceLocators;
    }
}