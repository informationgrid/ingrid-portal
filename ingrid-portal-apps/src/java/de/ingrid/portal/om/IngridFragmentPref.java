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


public class IngridFragmentPref {

	private Long id;
	private Long fragmentId;
	private String fragmentName;
	private Long fragmentReadOnly;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFragmentId() {
		return fragmentId;
	}
	public void setFragmentId(Long fragmentId) {
		this.fragmentId = fragmentId;
	}
	public String getFragmentName() {
		return fragmentName;
	}
	public void setFragmentName(String fragmentName) {
		this.fragmentName = fragmentName;
	}
	public Long getFragmentReadOnly() {
		return fragmentReadOnly;
	}
	public void setFragmentReadOnly(Long fragmentReadOnly) {
		this.fragmentReadOnly = fragmentReadOnly;
	}
	
		
}