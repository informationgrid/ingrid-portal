package de.ingrid.mdek.beans.query;

import java.util.Date;

import de.ingrid.mdek.beans.address.MdekAddressBean;

public class AddressWorkflowResultBean {

	public MdekAddressBean address;
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
	public MdekAddressBean getAddress() {
		return address;
	}
	public void setAddress(MdekAddressBean address) {
		this.address = address;
	}
	public MdekAddressBean getAssignedUser() {
		return assignedUser;
	}
	public void setAssignedUser(MdekAddressBean assignedUser) {
		this.assignedUser = assignedUser;
	}

}
