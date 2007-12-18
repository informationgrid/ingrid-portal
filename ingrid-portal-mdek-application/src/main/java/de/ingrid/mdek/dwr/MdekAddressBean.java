package de.ingrid.mdek.dwr;

import java.util.ArrayList;
import java.util.HashMap;

public class MdekAddressBean {
	public String Id;
	public String information;

	public String icon;
	public String name;
	public String organisation;
	public String givenName;
	public String nameForm;
	public String titleOrFunction;
	public String street;
	public String countryCode;
	public String city;
	public String poboxPostalCode;
	public String pobox;
	public String function;
	public String addressDescription;
	public ArrayList<HashMap<String, String>> communication;

	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getNameForm() {
		return nameForm;
	}
	public void setNameForm(String nameForm) {
		this.nameForm = nameForm;
	}
	public String getTitleOrFunction() {
		return titleOrFunction;
	}
	public void setTitleOrFunction(String titleOrFunction) {
		this.titleOrFunction = titleOrFunction;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPoboxPostalCode() {
		return poboxPostalCode;
	}
	public void setPoboxPostalCode(String poboxPostalCode) {
		this.poboxPostalCode = poboxPostalCode;
	}
	public String getPobox() {
		return pobox;
	}
	public void setPobox(String pobox) {
		this.pobox = pobox;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getAddressDescription() {
		return addressDescription;
	}
	public void setAddressDescription(String addressDescription) {
		this.addressDescription = addressDescription;
	}
	public ArrayList<HashMap<String, String>> getCommunication() {
		return communication;
	}
	public void setCommunication(ArrayList<HashMap<String, String>> communication) {
		this.communication = communication;
	}
}
