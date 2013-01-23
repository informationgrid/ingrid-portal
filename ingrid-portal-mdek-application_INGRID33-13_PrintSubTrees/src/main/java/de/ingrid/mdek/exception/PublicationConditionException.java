package de.ingrid.mdek.exception;

import java.util.List;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;


public class PublicationConditionException extends RuntimeException {
    private List<MdekDataBean> referencedConflictingObjects;
    private List<MdekAddressBean> referencedConflictingAddresses;
    
	public PublicationConditionException(String message) {
		super(message);
	}

    public List<MdekDataBean> getReferencedConflictingObjects() {
        return referencedConflictingObjects;
    }

    public void setReferencedConflictingObjects(List<MdekDataBean> referencedConflictingObjects) {
        this.referencedConflictingObjects = referencedConflictingObjects;
    }

    public List<MdekAddressBean> getReferencedConflictingAddresses() {
        return referencedConflictingAddresses;
    }

    public void setReferencedConflictingAddresses(List<MdekAddressBean> referencedConflictingAddresses) {
        this.referencedConflictingAddresses = referencedConflictingAddresses;
    }


}
