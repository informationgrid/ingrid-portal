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
