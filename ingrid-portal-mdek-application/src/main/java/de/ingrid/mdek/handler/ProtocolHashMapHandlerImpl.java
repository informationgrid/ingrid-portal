package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class ProtocolHashMapHandlerImpl implements ProtocolHandler {
	
	private final static Logger log = Logger.getLogger(ProtocolHashMapHandlerImpl.class);	
	
	/** Value: List < HashMap > */
    public final static String PROTOCOL_MESSAGES= "protocol-messages";
    /** Value: String */
    public final static String PROTOCOL_FILENAME = "protocol-file";
    /** Value: String */
    public final static String PROTOCOL_MESSAGE = "protocol-message";
    /** Value: String */
    public final static String PROTOCOL_TYPE = "protocol-type";

    private static ProtocolHashMapHandlerImpl myInstance;
    
    private HashMap<String, ArrayList<String>> importHashMapProtocol;
    private ArrayList<String> messageList;
    private String currentFilename;
	
	/** Singleton of ProtocolHashMapHandlerImpl*/
	public static synchronized ProtocolHashMapHandlerImpl getInstance() {
		if (myInstance == null) {
	        myInstance = new ProtocolHashMapHandlerImpl();
	      }
		return myInstance;
	}
	
	public void startProtocol(){
		if(importHashMapProtocol == null){
			importHashMapProtocol = new HashMap<String, ArrayList<String>>();
		}else{
			importHashMapProtocol.clear();
		}
		
		if(messageList == null){
			messageList = new ArrayList<String>(); 
		}else{
			messageList.clear();
		}
	}
	
	public void addMessage(String protocolMessage){
		if(importHashMapProtocol == null){
			startProtocol();
		}
		messageList.add(protocolMessage);
		importHashMapProtocol.put(PROTOCOL_MESSAGES, messageList);	
	}
	
	public String getCurrentFilename(){
		return currentFilename;
	}
	
	public void setCurrentFilename(String currentFilename){
		this.currentFilename = currentFilename;
	}
	
	public ArrayList <String> getProtocolMessages(){
		return importHashMapProtocol.get(PROTOCOL_MESSAGES);
	}
	
	public void stopProtocol(){
		// Not in use for HashMap Protocol
	}
	
	public void clearProtocol(){
		if(importHashMapProtocol!= null){
			importHashMapProtocol.clear();	
		}
		
		if(messageList != null){
			messageList.clear();
		}
	}
	
	public HashMap<String, ArrayList<String>> getHashMapImportProtocol() {
		return importHashMapProtocol;
	}

	public void setHashMapImportProtocol(HashMap<String, ArrayList<String>> memoryImportProtocol) {
		this.importHashMapProtocol = memoryImportProtocol;
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
