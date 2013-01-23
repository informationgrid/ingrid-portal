package de.ingrid.mdek.persistence.db.model;

import de.ingrid.mdek.services.persistence.db.IEntity;

public class UserData implements IEntity {

	private Long id;
	private int version;
	private String portalLogin;
	private String plugId;
	private String addressUuid;


	public UserData() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPortalLogin() {
		return portalLogin;
	}

	public void setPortalLogin(String portalLogin) {
		this.portalLogin = portalLogin;
	}

	public String getPlugId() {
		return plugId;
	}

	public void setPlugId(String plugId) {
		this.plugId = plugId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getAddressUuid() {
		return addressUuid;
	}

	public void setAddressUuid(String addressUuid) {
		this.addressUuid = addressUuid;
	}
}