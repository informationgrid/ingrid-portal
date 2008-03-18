package de.ingrid.mdek.beans;

import java.util.ArrayList;

public class OperationBean {
	String name;
	String description;
	ArrayList<String> platform = new ArrayList<String>();
	String methodCall;
	ArrayList<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
	ArrayList<String> addressList = new ArrayList<String>();
	ArrayList<String> dependencies = new ArrayList<String>();
	
	
	
	public ArrayList<String> getPlatform() {
		return platform;
	}
	public void setPlatform(ArrayList<String> platform) {
		this.platform = platform;
	}
	public String getMethodCall() {
		return methodCall;
	}
	public void setMethodCall(String methodCall) {
		this.methodCall = methodCall;
	}
	public ArrayList<OperationParameterBean> getParamList() {
		return paramList;
	}
	public void setParamList(ArrayList<OperationParameterBean> paramList) {
		this.paramList = paramList;
	}
	public ArrayList<String> getAddressList() {
		return addressList;
	}
	public void setAddressList(ArrayList<String> addressList) {
		this.addressList = addressList;
	}
	public ArrayList<String> getDependencies() {
		return dependencies;
	}
	public void setDependencies(ArrayList<String> dependencies) {
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
