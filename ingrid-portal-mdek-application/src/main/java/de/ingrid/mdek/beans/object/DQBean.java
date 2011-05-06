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
