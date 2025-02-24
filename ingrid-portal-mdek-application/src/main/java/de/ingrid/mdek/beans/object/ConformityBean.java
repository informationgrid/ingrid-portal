/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.beans.object;


import java.util.Date;

public class ConformityBean {
    private Boolean isInspire;
	private Integer level;
	private String specification;
	private String explanation;
	private Date publicationDate;

    public Boolean getIsInspire() {
        return isInspire;
    }

    public void setIsInspire(Boolean inspire) {
        isInspire = inspire;
    }

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getExplanation() {
    	return explanation;
    }

	public void setExplanation(String explanation) {
    	this.explanation = explanation;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}
