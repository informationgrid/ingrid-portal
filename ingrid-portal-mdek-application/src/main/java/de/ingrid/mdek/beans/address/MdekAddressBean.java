package de.ingrid.mdek.beans.address;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ingrid.mdek.beans.CommentBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;

public class MdekAddressBean {
	
	private String uuid;
	private Integer parentClass;
	private String parentUuid;
	private String information;
	private String nodeAppType;
	private String nodeDocType;
	private Boolean hasChildren;
	private Boolean writePermission;
    private Boolean movePermission;
	private Boolean writeSinglePermission;
	private Boolean writeTreePermission;
	private Boolean writeSubTreePermission;
	private Boolean isPublished;
	private Boolean isMarkedDeleted;
	
	// QA Fields
	private MdekAddressBean assignerUser;
	private Date assignTime;
	private String userOperation;

	private String addressOwner;
	private String creationTime;
	private String modificationTime;
	private MdekAddressBean lastEditor;
	private String workState;

	private Integer addressClass;
	private String organisation;
	private String name;
	private String givenName;
	private String nameForm;
	private String titleOrFunction;
	private String street;
	private Integer countryCode;
	private String countryName;
	private String postalCode;
	private String city;
	private String poboxPostalCode;
	private String pobox;
	private String task;
	private String addressDescription;

	private Integer typeOfRelation;
	private String nameOfRelation;
	private Integer refOfRelation;
	
	// Comments
	private List<CommentBean> commentTable;
	
	private List<CommunicationBean> communication;

	// Thesaurus
	private List<SNSTopic> thesaurusTermsTable;

	// References
	private List<MdekDataBean> linksFromObjectTable;
	private List<MdekDataBean> linksFromPublishedObjectTable;
	private List<MdekAddressBean> parentInstitutions;
	private Integer totalNumReferences;
	
	
	public MdekAddressBean() {
		this.communication = new ArrayList<CommunicationBean>();
		this.thesaurusTermsTable = new ArrayList<SNSTopic>();
		this.linksFromObjectTable = new ArrayList<MdekDataBean>();
		this.linksFromPublishedObjectTable = new ArrayList<MdekDataBean>();
		this.parentInstitutions = new ArrayList<MdekAddressBean>();
	}
	
	public String getAddressOwner() {
		return addressOwner;
	}

	public void setAddressOwner(String addressOwner) {
		this.addressOwner = addressOwner;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getModificationTime() {
		return modificationTime;
	}

	public void setModificationTime(String modificationTime) {
		this.modificationTime = modificationTime;
	}

	public MdekAddressBean getLastEditor() {
		return lastEditor;
	}

	public void setLastEditor(MdekAddressBean lastEditor) {
		this.lastEditor = lastEditor;
	}

	public String getWorkState() {
		return workState;
	}

	public void setWorkState(String workState) {
		this.workState = workState;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getNodeAppType() {
		return nodeAppType;
	}

	public void setNodeAppType(String nodeAppType) {
		this.nodeAppType = nodeAppType;
	}

	public String getNodeDocType() {
		return nodeDocType;
	}

	public void setNodeDocType(String nodeDocType) {
		this.nodeDocType = nodeDocType;
	}

	public Integer getAddressClass() {
		return addressClass;
	}

	public void setAddressClass(Integer addressClass) {
		this.addressClass = addressClass;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getNameForm() {
		return nameForm;
	}

	public void setNameForm(String nameForm) {
		this.nameForm = nameForm;
	}

	public String getTitleOrFunction() {
		return titleOrFunction;
	}

	public void setTitleOrFunction(String titleOrFunction) {
		this.titleOrFunction = titleOrFunction;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Integer getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPoboxPostalCode() {
		return poboxPostalCode;
	}

	public void setPoboxPostalCode(String poboxPostalCode) {
		this.poboxPostalCode = poboxPostalCode;
	}

	public String getPobox() {
		return pobox;
	}

	public void setPobox(String pobox) {
		this.pobox = pobox;
	}

	public String getAddressDescription() {
		return addressDescription;
	}

	public void setAddressDescription(String addressDescription) {
		this.addressDescription = addressDescription;
	}

	public Integer getTypeOfRelation() {
		return typeOfRelation;
	}

	public void setTypeOfRelation(Integer typeOfRelation) {
		this.typeOfRelation = typeOfRelation;
	}

	public String getNameOfRelation() {
		return nameOfRelation;
	}

	public void setNameOfRelation(String nameOfRelation) {
		this.nameOfRelation = nameOfRelation;
	}

	public List<CommunicationBean> getCommunication() {
		return communication;
	}

	public void setCommunication(List<CommunicationBean> communication) {
		this.communication = communication;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public Integer getParentClass() {
		return parentClass;
	}

	public void setParentClass(Integer parentClass) {
		this.parentClass = parentClass;
	}

	public List<SNSTopic> getThesaurusTermsTable() {
		return thesaurusTermsTable;
	}

	public void setThesaurusTermsTable(List<SNSTopic> thesaurusTermsTable) {
		this.thesaurusTermsTable = thesaurusTermsTable;
	}

	public List<MdekDataBean> getLinksFromObjectTable() {
		return linksFromObjectTable;
	}

	public void setLinksFromObjectTable(List<MdekDataBean> linksFromObjectTable) {
		this.linksFromObjectTable = linksFromObjectTable;
	}

	public List<MdekAddressBean> getParentInstitutions() {
		return parentInstitutions;
	}

	public void setParentInstitutions(List<MdekAddressBean> parentInstitutions) {
		this.parentInstitutions = parentInstitutions;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public List<CommentBean> getCommentTable() {
		return commentTable;
	}

	public void setCommentTable(List<CommentBean> commentTable) {
		this.commentTable = commentTable;
	}

	public Integer getRefOfRelation() {
		return refOfRelation;
	}

	public void setRefOfRelation(Integer refOfRelation) {
		this.refOfRelation = refOfRelation;
	}

	public List<MdekDataBean> getLinksFromPublishedObjectTable() {
		return linksFromPublishedObjectTable;
	}

	public void setLinksFromPublishedObjectTable(
			List<MdekDataBean> linksFromPublishedObjectTable) {
		this.linksFromPublishedObjectTable = linksFromPublishedObjectTable;
	}

	public Boolean getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public Boolean getWritePermission() {
		return writePermission;
	}

	public void setWritePermission(Boolean writePermission) {
		this.writePermission = writePermission;
	}

	public Boolean getMovePermission() {
        return movePermission;
    }

    public void setMovePermission(Boolean movePermission) {
        this.movePermission = movePermission;
    }

    public Boolean getWriteSinglePermission() {
		return writeSinglePermission;
	}

	public void setWriteSinglePermission(Boolean writeSinglePermission) {
		this.writeSinglePermission = writeSinglePermission;
	}

	public Boolean getWriteTreePermission() {
		return writeTreePermission;
	}

	public void setWriteTreePermission(Boolean writeTreePermission) {
		this.writeTreePermission = writeTreePermission;
	}

	public Boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}

	public Integer getTotalNumReferences() {
		return totalNumReferences;
	}

	public void setTotalNumReferences(Integer totalNumReferences) {
		this.totalNumReferences = totalNumReferences;
	}

	public Boolean getWriteSubTreePermission() {
		return writeSubTreePermission;
	}

	public void setWriteSubTreePermission(Boolean writeSubTreePermission) {
		this.writeSubTreePermission = writeSubTreePermission;
	}

	public MdekAddressBean getAssignerUser() {
		return assignerUser;
	}

	public void setAssignerUser(MdekAddressBean assignerUser) {
		this.assignerUser = assignerUser;
	}

	public Date getAssignTime() {
		return assignTime;
	}

	public void setAssignTime(Date assignTime) {
		this.assignTime = assignTime;
	}

	public String getUserOperation() {
		return userOperation;
	}

	public void setUserOperation(String userOperation) {
		this.userOperation = userOperation;
	}

	public Boolean getIsMarkedDeleted() {
		return isMarkedDeleted;
	}

	public void setIsMarkedDeleted(Boolean isMarkedDeleted) {
		this.isMarkedDeleted = isMarkedDeleted;
	}
}
