package de.ingrid.mdek.beans;

public class ExportJobInfoBean extends JobInfoBean {

	// raw file in gz format
	private byte[] result;

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}
}
