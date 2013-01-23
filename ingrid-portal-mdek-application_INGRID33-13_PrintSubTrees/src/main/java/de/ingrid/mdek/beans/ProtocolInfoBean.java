package de.ingrid.mdek.beans;

import java.util.List;

import de.ingrid.mdek.handler.ProtocolHandler;

public class ProtocolInfoBean {

	private String             inputType;
	private boolean            finished;
	//private String             protocol;
	private List<byte[]>       importData;
	private ProtocolHandler    protocolHandler;
	private int                dataProcessed;
	
	public ProtocolInfoBean(ProtocolHandler protocolHandler) {
	    this.protocolHandler = protocolHandler;
	    this.finished = false;
	}
	
	public int getDataProcessed() {
        return dataProcessed;
    }
    public void setDataProcessed(int value) {
        this.dataProcessed = value;
    }
    public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public boolean getFinished() {
		return finished;
	}
	public void setFinished(boolean status) {
		this.finished = status;
	}
	public String getProtocol() {
	    if (protocolHandler != null)
	        return protocolHandler.getProtocol();
	    return null;
	}
	/*public void setProtocol(String protocol) {
		this.protocol = protocol;
	}*/
	
	public List<byte[]> getImportData() {
		return importData;
	}
	
	public void setImportData(List<byte[]> importData) {
		this.importData = importData;
	}
	
	public ProtocolHandler getProtocolHandler() {
		return protocolHandler;
	}
	
	public void setProtocolHandler(ProtocolHandler protocolHandler) {
		this.protocolHandler = protocolHandler;
	}
	
	
}