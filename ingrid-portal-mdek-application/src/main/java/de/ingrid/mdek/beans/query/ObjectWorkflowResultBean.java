package de.ingrid.mdek.beans.query;

import java.util.Date;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;

public class ObjectWorkflowResultBean {

	public MdekDataBean object;
	public MdekAddressBean assignedUser;
	public String state;
	public String type;
	public Date date;

	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public MdekDataBean getObject() {
		return object;
	}
	public void setObject(MdekDataBean object) {
		this.object = object;
	}
	public MdekAddressBean getAssignedUser() {
		return assignedUser;
	}
	public void setAssignedUser(MdekAddressBean assignedUser) {
		this.assignedUser = assignedUser;
	}

}
