package de.ingrid.mdek.exception;

import java.util.ArrayList;

import de.ingrid.mdek.beans.address.MdekAddressBean;

public class GroupDeleteException extends RuntimeException {

	public ArrayList<MdekAddressBean> addresses;

	public GroupDeleteException(String message) { super(message); }

	public ArrayList<MdekAddressBean> getAddresses() {
		return addresses;
	}

	public void setAddresses(ArrayList<MdekAddressBean> addresses) {
		this.addresses = addresses;
	}


}
