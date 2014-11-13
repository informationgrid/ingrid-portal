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
package de.ingrid.mdek.beans;

public class SysList {
	private Integer id;
	private Integer[] entryIds;
	private String[] deEntries;
	private String[] enEntries;
	private String[] data;
	private Boolean maintainable;
	private Integer defaultIndex;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer[] getEntryIds() {
		return entryIds;
	}

	public void setEntryIds(Integer[] entryIds) {
		this.entryIds = entryIds;
	}

	public String[] getDeEntries() {
		return deEntries;
	}

	public void setDeEntries(String[] deEntries) {
		this.deEntries = deEntries;
	}

	public String[] getEnEntries() {
		return enEntries;
	}

	public void setEnEntries(String[] enEntries) {
		this.enEntries = enEntries;
	}
	
	public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

	public Boolean getMaintainable() {
		return maintainable;
	}

	public void setMaintainable(Boolean maintainable) {
		this.maintainable = maintainable;
	}

	public Integer getDefaultIndex() {
		return defaultIndex;
	}

	public void setDefaultIndex(Integer defaultIndex) {
		this.defaultIndex = defaultIndex;
	}
}