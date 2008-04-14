package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.Date;

public class ObjectExtSearchParamsBean {
	private String queryTerm;
	private Integer relation;
	private ArrayList<Integer> objClasses;

	private ArrayList<String> thesaurusTerms;
	private Integer thesaurusRelation;

	private ArrayList<String> geoThesaurusTerms;
	private Integer geoThesaurusRelation;

	private Integer customLocation;

	private Date timeBegin;
	private Date timeEnd;
	private Date timeAt;
	private Boolean timeIntersects;
	private Boolean timeContains;

	public String getQueryTerm() {
		return queryTerm;
	}

	public void setQueryTerm(String queryTerm) {
		this.queryTerm = queryTerm;
	}

	public Integer getRelation() {
		return relation;
	}

	public void setRelation(Integer relation) {
		this.relation = relation;
	}

	public ArrayList<Integer> getObjClasses() {
		return objClasses;
	}

	public void setObjClasses(ArrayList<Integer> objClasses) {
		this.objClasses = objClasses;
	}

	public ArrayList<String> getThesaurusTerms() {
		return thesaurusTerms;
	}

	public void setThesaurusTerms(ArrayList<String> thesaurusTerms) {
		this.thesaurusTerms = thesaurusTerms;
	}

	public Integer getThesaurusRelation() {
		return thesaurusRelation;
	}

	public void setThesaurusRelation(Integer thesaurusRelation) {
		this.thesaurusRelation = thesaurusRelation;
	}

	public ArrayList<String> getGeoThesaurusTerms() {
		return geoThesaurusTerms;
	}

	public void setGeoThesaurusTerms(ArrayList<String> geoThesaurusTerms) {
		this.geoThesaurusTerms = geoThesaurusTerms;
	}

	public Integer getGeoThesaurusRelation() {
		return geoThesaurusRelation;
	}

	public void setGeoThesaurusRelation(Integer geoThesaurusRelation) {
		this.geoThesaurusRelation = geoThesaurusRelation;
	}

	public Integer getCustomLocation() {
		return customLocation;
	}

	public void setCustomLocation(Integer customLocation) {
		this.customLocation = customLocation;
	}

	public Date getTimeBegin() {
		return timeBegin;
	}

	public void setTimeBegin(Date timeBegin) {
		this.timeBegin = timeBegin;
	}

	public Date getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}

	public Date getTimeAt() {
		return timeAt;
	}

	public void setTimeAt(Date timeAt) {
		this.timeAt = timeAt;
	}

	public Boolean getTimeIntersects() {
		return timeIntersects;
	}

	public void setTimeIntersects(Boolean timeIntersects) {
		this.timeIntersects = timeIntersects;
	}

	public Boolean getTimeContains() {
		return timeContains;
	}

	public void setTimeContains(Boolean timeContains) {
		this.timeContains = timeContains;
	}
}
