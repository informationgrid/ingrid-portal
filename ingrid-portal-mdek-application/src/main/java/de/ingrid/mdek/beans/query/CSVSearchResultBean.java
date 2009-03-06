package de.ingrid.mdek.beans.query;


public class CSVSearchResultBean {
	public int numHits;
	public long totalNumHits;
	public byte[] data;

	
	public CSVSearchResultBean() {
		this.numHits = 0;
		this.totalNumHits = 0;
		this.data = null;
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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
