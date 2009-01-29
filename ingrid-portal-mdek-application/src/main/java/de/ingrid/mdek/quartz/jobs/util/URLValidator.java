package de.ingrid.mdek.quartz.jobs.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import de.ingrid.mdek.quartz.jobs.util.URLState.State;

public class URLValidator implements Callable<URLState> {

	private final static Logger log = Logger.getLogger(URLValidator.class);	
	private final URLState urlState;

	private final static int CONNECTION_TIMEOUT = 5000;

	public URLValidator(URLState urlState) {
		this.urlState = urlState;
	}
/*
	public URLState call() {
		log.debug("check url: "+urlState.getUrl());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		urlState.setState(State.VALID);
		return urlState;
	}
*/

	public URLState call() {
		URL url = null;
		log.debug("checking url: "+urlState.getUrl());
		long startTime = System.currentTimeMillis();
		try {
			url = new URL(urlState.getUrl());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
			urlConnection.connect();
			int responseCode = urlConnection.getResponseCode();
			urlState.setResponseCode(responseCode);
			if (HttpURLConnection.HTTP_OK == responseCode) {
				urlState.setState(State.VALID);
			} else {
				urlState.setState(State.HTTP_ERROR);
			}

		} catch (MalformedURLException ex) {
			urlState.setState(State.MALFORMED_URL);

		} catch (SocketTimeoutException ex) {
			urlState.setState(State.TIMEOUT);

		} catch (IOException ex) {
			urlState.setState(State.HTTP_ERROR);
		}
		long endTime = System.currentTimeMillis();
		log.debug("done after "+(endTime - startTime)+"ms: "+urlState.getUrl());
		log.debug("returning: "+urlState.getState()+"("+urlState.getResponseCode()+")");
		return urlState;
	}
}
