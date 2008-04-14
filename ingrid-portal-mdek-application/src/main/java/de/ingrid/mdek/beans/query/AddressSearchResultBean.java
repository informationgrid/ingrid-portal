package de.ingrid.mdek.beans.query;

import java.util.ArrayList;

import de.ingrid.mdek.beans.address.MdekAddressBean;

public class AddressSearchResultBean {
	public int numHits;
	public long totalNumHits;

	public ArrayList<MdekAddressBean> resultList;

	
	public AddressSearchResultBean() {
		this.numHits = 0;
		this.totalNumHits = 0;
		this.resultList = new ArrayList<MdekAddressBean>();
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

	public ArrayList<MdekAddressBean> getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList<MdekAddressBean> resultList) {
		this.resultList = resultList;
	}
}
