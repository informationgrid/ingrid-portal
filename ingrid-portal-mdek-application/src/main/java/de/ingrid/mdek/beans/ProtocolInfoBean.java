package de.ingrid.mdek.beans;

import java.util.ArrayList;

import de.ingrid.mdek.handler.ProtocolHandler;

public class ProtocolInfoBean {

	private String inputType;
	private boolean status;
	private String protocol;
	private ArrayList<byte[]> importData;
	private ProtocolHandler protocolHandler;
	
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public ArrayList<byte[]> getImportData() {
		return importData;
	}
	public void setImportData(ArrayList<byte[]> importData) {
		this.importData = importData;
	}
	public ProtocolHandler getProtocolHandler() {
		return protocolHandler;
	}
	public void setProtocolHandler(ProtocolHandler protocolHandler) {
		this.protocolHandler = protocolHandler;
	}
	
	
}