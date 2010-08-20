package de.ingrid.mdek.handler;

import java.util.ArrayList;



public interface ProtocolHandler {
	
	public void startProtocol();
	public void stopProtocol();
	public void clearProtocol();
	public String getProtocol();
	public void addMessage(String protocolMessage);
	public ArrayList<String> getProtocolMessages();
	public boolean isDebugEnabled();
	public boolean isErrorEnabled();
	public boolean isInfoEnabled();
	public boolean isWarningEnabled();
	public String getCurrentFilename();
	public void setCurrentFilename(String currentFilename);
}
