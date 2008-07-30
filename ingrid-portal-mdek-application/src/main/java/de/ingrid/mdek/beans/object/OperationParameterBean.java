package de.ingrid.mdek.beans.object;

public class OperationParameterBean {
	String name;
	String direction;
	String description;
	Integer optional;
	Integer multiple;

	public OperationParameterBean() {}
	
	public OperationParameterBean(String name, String description, String direction, boolean optional, boolean multiple) {
		this.name = name;
		this.description = description;
		this.direction = direction;
		this.optional = optional? 1 : 0;
		this.multiple = multiple? 1 : 0;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getOptional() {
		return optional;
	}
	public void setOptional(Integer optional) {
		this.optional = optional;
	}
	public Integer getMultiple() {
		return multiple;
	}
	public void setMultiple(Integer multiple) {
		this.multiple = multiple;
	}
}
