package de.ingrid.mdek.beans;

public class TreeNodeBean {
	private String id;
	private String title;
	private String nodeDocType;
	private Integer objectClass;
	private Integer publicationCondition;

	private Boolean isFolder;
	private Boolean isPublished;
	private String workState;
	private Boolean isMarkedDeleted;

	private Boolean userWritePermission;
    private Boolean userMovePermission;
    private Boolean userWriteSinglePermission;
	private Boolean userWriteTreePermission;
	private Boolean userWriteSubNodePermission;
	private Boolean userWriteSubTreePermission;

	private String nodeAppType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNodeDocType() {
		return nodeDocType;
	}

	public void setNodeDocType(String nodeDocType) {
		this.nodeDocType = nodeDocType;
	}

	public Integer getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(Integer objectClass) {
		this.objectClass = objectClass;
	}

	public Boolean getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}

	public Boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}

	public String getWorkState() {
		return workState;
	}

	public void setWorkState(String workState) {
		this.workState = workState;
	}

	public Boolean getIsMarkedDeleted() {
		return isMarkedDeleted;
	}

	public void setIsMarkedDeleted(Boolean isMarkedDeleted) {
		this.isMarkedDeleted = isMarkedDeleted;
	}

	public Boolean getUserWritePermission() {
		return userWritePermission;
	}

	public void setUserWritePermission(Boolean userWritePermission) {
		this.userWritePermission = userWritePermission;
	}

    public Boolean getUserMovePermission() {
        return userMovePermission;
    }

    public void setUserMovePermission(Boolean userMovePermission) {
        this.userMovePermission = userMovePermission;
    }
	
	public Boolean getUserWriteSinglePermission() {
		return userWriteSinglePermission;
	}

	public void setUserWriteSinglePermission(Boolean userWriteSinglePermission) {
		this.userWriteSinglePermission = userWriteSinglePermission;
	}

	public Boolean getUserWriteTreePermission() {
		return userWriteTreePermission;
	}

	public void setUserWriteTreePermission(Boolean userWriteTreePermission) {
		this.userWriteTreePermission = userWriteTreePermission;
	}

	public Boolean getUserWriteSubNodePermission() {
		return userWriteSubNodePermission;
	}

	public void setUserWriteSubNodePermission(Boolean userWriteSubNodePermission) {
		this.userWriteSubNodePermission = userWriteSubNodePermission;
	}

	public Boolean getUserWriteSubTreePermission() {
		return userWriteSubTreePermission;
	}

	public void setUserWriteSubTreePermission(Boolean userWriteSubTreePermission) {
		this.userWriteSubTreePermission = userWriteSubTreePermission;
	}

	public String getNodeAppType() {
		return nodeAppType;
	}

	public void setNodeAppType(String nodeAppType) {
		this.nodeAppType = nodeAppType;
	}

	public Integer getPublicationCondition() {
		return publicationCondition;
	}

	public void setPublicationCondition(Integer publicationCondition) {
		this.publicationCondition = publicationCondition;
	}

}
