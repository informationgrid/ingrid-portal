package de.ingrid.mdek.exception;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;

public class InvalidPermissionException extends RuntimeException {

	private MdekDataBean rootObject;
	private MdekAddressBean rootAddress;

	private MdekDataBean invalidObject;
	private MdekAddressBean invalidAddress;

	public InvalidPermissionException(String message) {
		super(message);
	}

	public MdekDataBean getRootObject() {
		return rootObject;
	}

	public void setRootObject(MdekDataBean rootObject) {
		this.rootObject = rootObject;
	}

	public MdekAddressBean getRootAddress() {
		return rootAddress;
	}

	public void setRootAddress(MdekAddressBean rootAddress) {
		this.rootAddress = rootAddress;
	}

	public MdekDataBean getInvalidObject() {
		return invalidObject;
	}

	public void setInvalidObject(MdekDataBean invalidObject) {
		this.invalidObject = invalidObject;
	}

	public MdekAddressBean getInvalidAddress() {
		return invalidAddress;
	}

	public void setInvalidAddress(MdekAddressBean invalidAddress) {
		this.invalidAddress = invalidAddress;
	}

}
