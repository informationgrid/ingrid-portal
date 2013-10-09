package de.ingrid.mdek.beans.object;

public class UrlBean {
	private String name;
	private String url;
	private Integer relationType;
	private String relationTypeName;
	private String datatype;
	private String description;
	private Integer urlType;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getRelationType() {
		return relationType;
	}

	public void setRelationType(Integer relationType) {
		this.relationType = relationType;
	}

	public String getRelationTypeName() {
		return relationTypeName;
	}

	public void setRelationTypeName(String relationTypeName) {
		this.relationTypeName = relationTypeName;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUrlType() {
		return urlType;
	}

	public void setUrlType(Integer urlType) {
		this.urlType = urlType;
	}
}
