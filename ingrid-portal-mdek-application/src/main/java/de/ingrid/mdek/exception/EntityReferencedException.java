package de.ingrid.mdek.exception;

import java.util.ArrayList;

import de.ingrid.mdek.dwr.MdekAddressBean;
import de.ingrid.mdek.dwr.MdekDataBean;

public class EntityReferencedException extends RuntimeException {

	public MdekDataBean targetObject;
	public MdekAddressBean targetAddress;
	
	public ArrayList<MdekDataBean> sourceObjects;
	public ArrayList<MdekAddressBean> sourceAddresses;


	public EntityReferencedException(String message) { super(message); }


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


	public ArrayList<MdekDataBean> getSourceObjects() {
		return sourceObjects;
	}


	public void setSourceObjects(ArrayList<MdekDataBean> sourceObjects) {
		this.sourceObjects = sourceObjects;
	}


	public ArrayList<MdekAddressBean> getSourceAddresses() {
		return sourceAddresses;
	}


	public void setSourceAddresses(ArrayList<MdekAddressBean> sourceAddresses) {
		this.sourceAddresses = sourceAddresses;
	}
	

}
