package de.ingrid.mdek.exception;

import java.util.List;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;

public class EntityReferencedException extends RuntimeException {

	private MdekDataBean targetObject;
	private MdekAddressBean targetAddress;

	private List<MdekDataBean> sourceObjects;
	private List<MdekAddressBean> sourceAddresses;

	public EntityReferencedException(String message) {
		super(message);
	}

	public MdekDataBean getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(MdekDataBean targetObject) {
		this.targetObject = targetObject;
	}

	public MdekAddressBean getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(MdekAddressBean targetAddress) {
		this.targetAddress = targetAddress;
	}

	public List<MdekDataBean> getSourceObjects() {
		return sourceObjects;
	}

	public void setSourceObjects(List<MdekDataBean> sourceObjects) {
		this.sourceObjects = sourceObjects;
	}

	public List<MdekAddressBean> getSourceAddresses() {
		return sourceAddresses;
	}

	public void setSourceAddresses(List<MdekAddressBean> sourceAddresses) {
		this.sourceAddresses = sourceAddresses;
	}

}
