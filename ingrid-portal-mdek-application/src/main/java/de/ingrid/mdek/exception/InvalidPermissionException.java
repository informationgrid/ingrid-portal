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
