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
package de.ingrid.mdek.beans.object;


/** Data Quality (DQ) bean used for all DQ Tables (represents row content) */
public class DQBean {
	private Integer dqElementId;
	private String nameOfMeasure;
	private String resultValue;
	private String measureDescription;

	public Integer getDqElementId() {
		return dqElementId;
	}
	public void setDqElementId(Integer dqElementId) {
		this.dqElementId = dqElementId;
	}
	public String getNameOfMeasure() {
		return nameOfMeasure;
	}
	public void setNameOfMeasure(String nameOfMeasure) {
		this.nameOfMeasure = nameOfMeasure;
	}
	public String getResultValue() {
		return resultValue;
	}
	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}
	public String getMeasureDescription() {
		return measureDescription;
	}
	public void setMeasureDescription(String measureDescription) {
		this.measureDescription = measureDescription;
	}
}
