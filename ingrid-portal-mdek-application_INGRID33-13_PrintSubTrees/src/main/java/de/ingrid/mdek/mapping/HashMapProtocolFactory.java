package de.ingrid.mdek.mapping;

import de.ingrid.mdek.handler.HashMapProtocolHandler;

public class HashMapProtocolFactory implements ProtocolFactory{
	
	public HashMapProtocolHandler getProtocolHandler(){
		return new HashMapProtocolHandler();
	}
}
