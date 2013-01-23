package de.ingrid.mdek.handler;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class HashMapProtocolHandler implements ProtocolHandler {
	
	private final static Logger log = Logger.getLogger(HashMapProtocolHandler.class);	
	
	private ArrayList<String> messageList;
    private String currentFilename;
	
	public void startProtocol(){
		if(messageList == null){
			messageList = new ArrayList<String>(); 
		}else{
			messageList.clear();
		}
	}
	
	public void addMessage(String protocolMessage){
		if(messageList == null){
			startProtocol();
		}
		messageList.add(protocolMessage);
	}
	
	public String getCurrentFilename(){
		return currentFilename;
	}
	
	public void setCurrentFilename(String currentFilename){
		this.currentFilename = currentFilename;
	}
	
	public void stopProtocol(){
		// Not in use for HashMap Protocol
	}
	
	public void clearProtocol(){
		if(messageList != null){
			messageList.clear();
		}
	}
	
	public String getProtocol(){
		String messageEntities = "";
		if(messageList != null){
			for(int i=0; i < messageList.size(); i++){
				String message = messageList.get(i);
				messageEntities = messageEntities.concat(message);
			}
			return messageEntities;
		}else{
			return "";
		}
	}
	
	public boolean isDebugEnabled(){
		return log.isDebugEnabled();
	}	
	
	public boolean isInfoEnabled(){
		return log.isInfoEnabled();
	}
	
	public boolean isErrorEnabled(){
		return log.isEnabledFor(Level.ERROR);
	}
	
	public boolean isWarningEnabled(){
		return log.isEnabledFor(Level.WARN);
	}
}
