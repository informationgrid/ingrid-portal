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
