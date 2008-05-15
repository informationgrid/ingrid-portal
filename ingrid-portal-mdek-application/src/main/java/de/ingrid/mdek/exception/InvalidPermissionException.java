package de.ingrid.mdek.exception;

import java.util.ArrayList;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;

public class InvalidPermissionException extends RuntimeException {

	public MdekDataBean rootObject;
	public MdekAddressBean rootAddress;
	
	public MdekDataBean invalidObject;
	public MdekAddressBean invalidAddress;


	public InvalidPermissionException(String message) { super(message); }


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
