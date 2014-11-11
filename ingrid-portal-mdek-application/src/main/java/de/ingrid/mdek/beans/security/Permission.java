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
package de.ingrid.mdek.beans.security;

import de.ingrid.mdek.MdekUtilsSecurity.IdcPermission;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;

public class Permission {
	private String uuid;
	private IdcPermission permission;

	// Optional references to the obj, adr
	private MdekDataBean object;
	private MdekAddressBean address;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public IdcPermission getPermission() {
		return permission;
	}

	public void setPermission(IdcPermission permission) {
		this.permission = permission;
	}

	public MdekDataBean getObject() {
		return object;
	}

	public void setObject(MdekDataBean object) {
		this.object = object;
	}

	public MdekAddressBean getAddress() {
		return address;
	}

	public void setAddress(MdekAddressBean address) {
		this.address = address;
	}
}
