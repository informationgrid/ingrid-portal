/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.mdek.beans.object;

import java.util.ArrayList;
import java.util.List;


public class OperationBean {

	private String name;
	private String description;
	private List<Integer> platform = new ArrayList<Integer>();
	private String methodCall;
	private List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
	private List<String> addressList = new ArrayList<String>();
	private List<String> dependencies = new ArrayList<String>();
	
	
	
	public List<Integer> getPlatform() {
		return platform;
	}
	public void setPlatform(List<Integer> platform) {
		this.platform = platform;
	}
	public String getMethodCall() {
		return methodCall;
	}
	public void setMethodCall(String methodCall) {
		this.methodCall = methodCall;
	}
	public List<OperationParameterBean> getParamList() {
		return paramList;
	}
	public void setParamList(List<OperationParameterBean> paramList) {
		this.paramList = paramList;
	}
	public List<String> getAddressList() {
		return addressList;
	}
	public void setAddressList(List<String> addressList) {
		this.addressList = addressList;
	}
	public List<String> getDependencies() {
		return dependencies;
	}
	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
