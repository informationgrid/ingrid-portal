package de.ingrid.mdek.exception;

import java.util.List;

import de.ingrid.mdek.beans.address.MdekAddressBean;


public class AddressNeverPublishedException extends RuntimeException {
    private List<MdekAddressBean> notPublishedAddresses;
	public AddressNeverPublishedException(String message) {
		super(message);
	}
    public void setNotPublishedAddresses(List<MdekAddressBean> notPublishedAddresses) {
        this.notPublishedAddresses = notPublishedAddresses;
    }
    public List<MdekAddressBean> getNotPublishedAddresses() {
        return notPublishedAddresses;
    }

}
