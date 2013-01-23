package de.ingrid.mdek.dwr.services.sns;

import java.util.Date;

public class SNSEventTopic {

	private String topicId;
	private String name;
	private String description;
	private Date at;
	private Date from;
	private Date to;

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

	public String toString() {
		return "[" + topicId + ", " + name + ", " + description + ", "
				+ at + ", " + from + ", " + to + "]";
	}
}
