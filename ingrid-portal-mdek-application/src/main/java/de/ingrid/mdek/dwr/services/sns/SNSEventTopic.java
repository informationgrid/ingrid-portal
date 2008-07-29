package de.ingrid.mdek.dwr.services.sns;

import java.util.Date;

public class SNSEventTopic {

	public String topicId;
	public String name;
	public String description;
	public Date at;
	public Date from;
	public Date to;


	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
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
	public Date getAt() {
		return at;
	}
	public void setAt(Date at) {
		this.at = at;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}

}
