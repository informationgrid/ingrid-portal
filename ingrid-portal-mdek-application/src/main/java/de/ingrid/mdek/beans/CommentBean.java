package de.ingrid.mdek.beans;

import java.util.Date;

import de.ingrid.mdek.beans.address.MdekAddressBean;

public class CommentBean {
	public Date date;
	public String comment;
	public MdekAddressBean user;

	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public MdekAddressBean getUser() {
		return user;
	}
	public void setUser(MdekAddressBean user) {
		this.user = user;
	}
}
