package de.ingrid.portal.global;

import java.net.URLConnection;

import java.util.Base64;

public class UtilsHttpConnection {

    public static void urlConnectionAuth(URLConnection conn, String login, String password) {
        String userAuth = login + ":" + password;
        String encoding = Base64.getEncoder().encodeToString(userAuth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);
    }

}
