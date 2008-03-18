package de.ingrid.mdek.beans;

import java.util.ArrayList;

public class ObjectSearchResultBean {
	public int numHits;
	public long totalNumHits;

	public ArrayList<MdekDataBean> resultList;

	
	public ObjectSearchResultBean() {
		this.numHits = 0;
		this.totalNumHits = 0;
		this.resultList = new ArrayList<MdekDataBean>();
	}
	
	public int getNumHits() {
		return numHits;
	}

	public void setNumHits(int numHits) {
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
}
