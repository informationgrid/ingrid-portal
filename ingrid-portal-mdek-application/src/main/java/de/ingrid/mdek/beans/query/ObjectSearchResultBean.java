package de.ingrid.mdek.beans.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.ingrid.mdek.beans.object.MdekDataBean;

public class ObjectSearchResultBean {
	public long numHits;
	public long totalNumHits;

	public ArrayList<MdekDataBean> resultList;
	public Map<String, String> additionalData;
	
	public ObjectSearchResultBean() {
		this.numHits = 0;
		this.totalNumHits = 0;
		this.resultList = new ArrayList<MdekDataBean>();
		this.additionalData = new HashMap<String, String>();
	}
	
	public long getNumHits() {
		return numHits;
	}

	public void setNumHits(long numHits) {
		this.numHits = numHits;
	}

	public long getTotalNumHits() {
		return totalNumHits;
	}

	public void setTotalNumHits(long totalNumHits) {
		this.totalNumHits = totalNumHits;
	}

	public ArrayList<MdekDataBean> getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList<MdekDataBean> resultList) {
		this.resultList = resultList;
	}

	public Map<String, String> getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(Map<String, String> additionalData) {
		this.additionalData = additionalData;
	}
}
