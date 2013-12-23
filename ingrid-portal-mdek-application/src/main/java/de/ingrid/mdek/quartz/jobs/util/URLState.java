package de.ingrid.mdek.quartz.jobs.util;

public class URLState {

	public enum State { NOT_CHECKED, HTTP_ERROR, CONNECT_TIMEOUT, CONNECT_REFUSED, SOCKET_TIMEOUT, MALFORMED_URL, VALID, UNKNOWN_HOST, INVALID_CAPABILITIES }
	
	private final String url;
	private String additionalParams;
	private State state;
	private int responseCode;

	public URLState(String url) {
		this.url = url;
		this.state = State.NOT_CHECKED;
	}

	public String getUrl() {
		return url;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getAdditionalParams() {
		return additionalParams;
	}

	public void setAdditionalParams(String additionalParams) {
		this.additionalParams = additionalParams;
	}
}
