package de.ingrid.portal.global;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilsHttpConnection {

    public static String HEADER_CONTENT_LENGTH = "Content-Length";
    public static String HEADER_CONTENT_TYPE = "Content-Type";
    public static String HEADER_RESPONSE_CODE = "Response-Code";
    public static String HEADER_LAST_MODIFIED = "Last-Modified";

    private static final Logger log = LoggerFactory.getLogger(UtilsHttpConnection.class);

    public static void urlConnectionAuth(URLConnection conn, String login, String password) {
        String userAuth = login + ":" + password;
        String encoding = Base64.getEncoder().encodeToString(userAuth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);
    }

    public static Map<String, Object> urlConnectionHead(String paramUrl) {
       return urlConnectionHead(paramUrl, null, null);
    }

    public static Map<String, Object> urlConnectionHead(String paramUrl, String login, String password) {
        Map<String, Object> header = new HashMap<String, Object>();
        try {
            URL url = new URL(paramUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");

            if(StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
                UtilsHttpConnection.urlConnectionAuth(con, login, password);
            }
            if(con.getResponseCode() != 200 || con.getContentLength() == -1) {
                con.disconnect();
                con = (HttpURLConnection) url.openConnection();
                if(StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
                    UtilsHttpConnection.urlConnectionAuth(con, login, password);
                }
            }
            if((con.getResponseCode() != 200  || con.getContentLength() == -1) && paramUrl.startsWith("http://")) {
                con.disconnect();
                url = new URL(paramUrl.replace("http://", "https://"));
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("HEAD");

                if(StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
                    UtilsHttpConnection.urlConnectionAuth(con, login, password);
                }
                if(con.getResponseCode() != 200 || con.getContentLength() == -1) {
                    con.disconnect();
                    con = (HttpURLConnection) url.openConnection();
                    if(StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
                        UtilsHttpConnection.urlConnectionAuth(con, login, password);
                    }
                }
            }
            if(con.getResponseCode() == 200) {
                header.put(HEADER_CONTENT_LENGTH, con.getContentLength());
                header.put(HEADER_CONTENT_TYPE, con.getContentType());
                header.put(HEADER_LAST_MODIFIED, con.getLastModified());
                header.put(HEADER_RESPONSE_CODE, con.getResponseCode());
            }
            con.disconnect();
        } catch (IOException e) {
            log.error("Error load url: " + paramUrl, e);
        }
        return header;
    }
    
    
}
