package de.ingrid.mdek.quartz.jobs.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

import javax.xml.ws.http.HTTPException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.ingrid.mdek.quartz.jobs.util.URLState.State;

public class URLValidator implements Callable<URLState> {

	private final static Logger log = Logger.getLogger(URLValidator.class);	
	private final HttpClient httpClient;
	private final URLState urlState;


	public URLValidator(HttpClient httpClient, URLState urlState) {
		this.httpClient = httpClient;
		this.urlState = urlState;
	}

	public URLState call() {
		log.debug("checking url: "+urlState.getUrl());
		String url = urlState.getUrl();

		GetMethod getMethod = null;
		long startTime = System.currentTimeMillis();
		try {
			getMethod = new GetMethod(url);
			int responseCode = httpClient.executeMethod(getMethod);
			urlState.setResponseCode(responseCode);

			if (HttpURLConnection.HTTP_OK == responseCode) {
				urlState.setState(State.VALID);
			} else {
				urlState.setState(State.HTTP_ERROR);
			}

		} catch (HTTPException ex) {
			urlState.setState(State.HTTP_ERROR);

		} catch (ConnectTimeoutException ex) {
			urlState.setState(State.CONNECT_TIMEOUT);

		} catch (SocketTimeoutException ex) {
			urlState.setState(State.SOCKET_TIMEOUT);

		} catch (IOException ex) {
			urlState.setState(State.HTTP_ERROR);

		} catch (IllegalArgumentException ex) {
			urlState.setState(State.MALFORMED_URL);

		} catch (Exception ex) {
			urlState.setState(State.HTTP_ERROR);

		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
		}

		long endTime = System.currentTimeMillis();
		log.debug("done after "+(endTime - startTime)+"ms: "+urlState.getUrl());
		log.debug("returning: "+urlState.getState()+"("+urlState.getResponseCode()+")");
		return urlState;
	}
}
