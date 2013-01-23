package de.ingrid.mdek.exception;

import java.util.List;

import de.ingrid.mdek.beans.address.MdekAddressBean;

public class GroupDeleteException extends RuntimeException {

	private List<MdekAddressBean> addresses;

	public GroupDeleteException(String message) {
		super(message);
	}

	public List<MdekAddressBean> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<MdekAddressBean> addresses) {
		this.addresses = addresses;
	}

}
