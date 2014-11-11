/*
 * **************************************************-
 * Ingrid Portal Apps
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