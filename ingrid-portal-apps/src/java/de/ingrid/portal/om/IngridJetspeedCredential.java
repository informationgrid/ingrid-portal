package de.ingrid.portal.om;

import java.util.Date;

public class IngridJetspeedCredential {

	private Long securityCredentialId;
	private Long securityPrincipalId;
	private String securityColumnValue;
	private String securityClassName;
	private Date securityCreationDate;
	private Date securityModifiedDate;
	private Date securityPrevAuthDate;
	private Date securityLastAuthDate;
	
	
	public Long getSecurityCredentialId() {
		return securityCredentialId;
	}
	public void setSecurityCredentialId(Long securityCredentialId) {
		this.securityCredentialId = securityCredentialId;
	}
	public Long getSecurityPrincipalId() {
		return securityPrincipalId;
	}
	public void setSecurityPrincipalId(Long securityPrincipalId) {
		this.securityPrincipalId = securityPrincipalId;
	}
	public String getSecurityColumnValue() {
		return securityColumnValue;
	}
	public void setSecurityColumnValue(String securityColumnValue) {
		this.securityColumnValue = securityColumnValue;
	}
	public Date getSecurityPrevAuthDate() {
		return securityPrevAuthDate;
	}
	public void setSecurityPrevAuthDate(Date securityPrevAuthDate) {
		this.securityPrevAuthDate = securityPrevAuthDate;
	}
	public Date getSecurityLastAuthDate() {
		return securityLastAuthDate;
	}
	public void setSecurityLastAuthDate(Date securityLastAuthDate) {
		this.securityLastAuthDate = securityLastAuthDate;
	}
	public void setSecurityClassName(String securityClassName) {
		this.securityClassName = securityClassName;
	}
	public String getSecurityClassName() {
		return securityClassName;
	}
	public void setSecurityModifiedDate(Date securityModifiedDate) {
		this.securityModifiedDate = securityModifiedDate;
	}
	public Date getSecurityModifiedDate() {
		return securityModifiedDate;
	}
	public void setSecurityCreationDate(Date securityCreationDate) {
		this.securityCreationDate = securityCreationDate;
	}
	public Date getSecurityCreationDate() {
		return securityCreationDate;
	}
	
		
}