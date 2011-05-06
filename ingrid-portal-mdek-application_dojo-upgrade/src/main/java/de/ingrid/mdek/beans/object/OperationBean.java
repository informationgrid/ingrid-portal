package de.ingrid.mdek.beans.object;

import java.util.ArrayList;
import java.util.List;


public class OperationBean {

	private String name;
	private String description;
	private List<String> platform = new ArrayList<String>();
	private String methodCall;
	private List<OperationParameterBean> paramList = new ArrayList<OperationParameterBean>();
	private List<String> addressList = new ArrayList<String>();
	private List<String> dependencies = new ArrayList<String>();
	
	
	
	public List<String> getPlatform() {
		return platform;
	}
	public void setPlatform(List<String> platform) {
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
