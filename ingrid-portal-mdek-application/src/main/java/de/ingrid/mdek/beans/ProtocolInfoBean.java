/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.beans;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.job.protocol.ProtocolHandler;
import de.ingrid.mdek.job.protocol.ProtocolHandler.Type;

public class ProtocolInfoBean {

	private String             inputType;
	private boolean            finished;
	private List<Map<Type, List<String>>> protocol;
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

    public List<Map<Type, List<String>>> getProtocol() {
        return protocol;
    }

    public void setProtocol(List<Map<Type, List<String>>> allProtocols) {
        this.protocol = allProtocols;
    }
	
	
}
