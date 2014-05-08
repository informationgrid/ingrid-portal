package de.ingrid.mdek.quartz.jobs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import javax.xml.ws.http.HTTPException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.ingrid.mdek.quartz.jobs.util.URLState.State;

public class URLValidator implements Callable<URLState> {

	private final static Logger log = Logger.getLogger(URLValidator.class);	
	protected final HttpClient httpClient;
	protected final URLState urlState;
	protected String responseString = null;


	public URLValidator(HttpClient httpClient, URLState urlState) {
		this.httpClient = httpClient;
		this.urlState = urlState;
	}

	public URLState call() {
		log.debug("checking url: "+urlState.getUrl());
		String url = urlState.getUrl() + urlState.getAdditionalParams();

		GetMethod getMethod = null;
		long startTime = System.currentTimeMillis();
		try {
			getMethod = new GetMethod(url);
			int responseCode = httpClient.executeMethod(getMethod);
			urlState.setResponseCode(responseCode);

			if (HttpURLConnection.HTTP_OK == responseCode) {
				urlState.setState(State.VALID);
				// get content for further analysis if page is ok
				// -> getResponseBodyAsString-method can easily lead to OOM!!!
				// responseString = getMethod.getResponseBodyAsString();
				Reader reader = new InputStreamReader(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet()); 
			    // consume the response entity
				responseString = getStringFromInputStream( reader, 10 );
			} else {
				urlState.setState(State.HTTP_ERROR);
			}

		} catch (HTTPException ex) {
			urlState.setState(State.HTTP_ERROR);
			
		} catch (ConnectException ex) {
		    urlState.setState(State.CONNECT_REFUSED);		    

		} catch (ConnectTimeoutException ex) {
			urlState.setState(State.CONNECT_TIMEOUT);

		} catch (SocketTimeoutException ex) {
			urlState.setState(State.SOCKET_TIMEOUT);
			
		} catch (UnknownHostException ex) {
		    urlState.setState(State.UNKNOWN_HOST);		    

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
	
	// convert InputStream to String
    private static String getStringFromInputStream(Reader reader, int numLinesToRead) {
 
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
 
        String line;
        int currentLine = 1;
        try {
 
            br = new BufferedReader(reader);
            while ((line = br.readLine()) != null) {
                // stop reading after a specified amount of lines
                if (currentLine >= numLinesToRead) break;
                
                sb.append(line);
                currentLine++;
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
 
        return sb.toString();
 
    }
}
