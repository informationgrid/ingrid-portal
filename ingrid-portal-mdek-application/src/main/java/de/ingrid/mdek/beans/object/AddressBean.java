/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.beans.object;

/**
 * @author André
 *
 */
public class AddressBean {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String uuid;
    private Integer type;
    private String organisation;
    
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getPostcode() {
        return postcode;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid( String uuid ) {
        this.uuid = uuid;
    }
    public Integer getType() {
        return type;
    }
    public void setType( Integer type ) {
        this.type = type;
    }
    public String getOrganisation() {
        return organisation;
    }
    public void setOrganisation( String organisation ) {
        this.organisation = organisation;
    }
    
}
