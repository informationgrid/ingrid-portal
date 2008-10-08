package de.ingrid.mdek.beans.address;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.ingrid.mdek.beans.CommentBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;

public class MdekAddressBean {
	
	public String uuid;
	public Integer parentClass;
	public String parentUuid;
	public String information;
	public String nodeAppType;
	public String nodeDocType;
	public Boolean hasChildren;
	public Boolean writePermission;
	public Boolean writeSinglePermission;
	public Boolean writeTreePermission;
	public Boolean writeSubTreePermission;
	public Boolean isPublished;
	public Boolean isMarkedDeleted;
	
	// QA Fields
	public MdekAddressBean assignerUser;
	public Date assignTime;
	public String userOperation;

	public String addressOwner;
	public String creationTime;
	public String modificationTime;
	public MdekAddressBean lastEditor;
	public String workState;

	public Integer addressClass;
	public String organisation;
	public String name;
	public String givenName;
	public String nameForm;
	public String titleOrFunction;
	public String street;
	public String countryCode;
	public String postalCode;
	public String city;
	public String poboxPostalCode;
	public String pobox;
	public String task;
	public String addressDescription;

	public Integer typeOfRelation;
	public String nameOfRelation;
	public Integer refOfRelation;
	
	// Comments
	public ArrayList<CommentBean> commentTable;
	
	public ArrayList<HashMap<String, String>> communication;

	// Thesaurus
	public ArrayList<SNSTopic> thesaurusTermsTable;
	public ArrayList<String> thesaurusFreeTermsTable;

	// References
	public ArrayList<MdekDataBean> linksFromObjectTable;
	public ArrayList<MdekDataBean> linksFromPublishedObjectTable;
	public ArrayList<MdekAddressBean> parentInstitutions;
	public Integer totalNumReferences;
	
	
	public MdekAddressBean() {
		this.communication = new ArrayList<HashMap<String, String>>();
		this.thesaurusTermsTable = new ArrayList<SNSTopic>();
		this.thesaurusFreeTermsTable = new ArrayList<String>();
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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

	public ArrayList<HashMap<String, String>> getCommunication() {
		return communication;
	}

	public void setCommunication(ArrayList<HashMap<String, String>> communication) {
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

	public ArrayList<SNSTopic> getThesaurusTermsTable() {
		return thesaurusTermsTable;
	}

	public void setThesaurusTermsTable(ArrayList<SNSTopic> thesaurusTermsTable) {
		this.thesaurusTermsTable = thesaurusTermsTable;
	}

	public ArrayList<String> getThesaurusFreeTermsTable() {
		return thesaurusFreeTermsTable;
	}

	public void setThesaurusFreeTermsTable(ArrayList<String> thesaurusFreeTermsTable) {
		this.thesaurusFreeTermsTable = thesaurusFreeTermsTable;
	}

	public ArrayList<MdekDataBean> getLinksFromObjectTable() {
		return linksFromObjectTable;
	}

	public void setLinksFromObjectTable(ArrayList<MdekDataBean> linksFromObjectTable) {
		this.linksFromObjectTable = linksFromObjectTable;
	}

	public ArrayList<MdekAddressBean> getParentInstitutions() {
		return parentInstitutions;
	}

	public void setParentInstitutions(ArrayList<MdekAddressBean> parentInstitutions) {
		this.parentInstitutions = parentInstitutions;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public ArrayList<CommentBean> getCommentTable() {
		return commentTable;
	}

	public void setCommentTable(ArrayList<CommentBean> commentTable) {
		this.commentTable = commentTable;
	}

	public Integer getRefOfRelation() {
		return refOfRelation;
	}

	public void setRefOfRelation(Integer refOfRelation) {
		this.refOfRelation = refOfRelation;
	}

	public ArrayList<MdekDataBean> getLinksFromPublishedObjectTable() {
		return linksFromPublishedObjectTable;
	}

	public void setLinksFromPublishedObjectTable(
			ArrayList<MdekDataBean> linksFromPublishedObjectTable) {
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
