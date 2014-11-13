/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
