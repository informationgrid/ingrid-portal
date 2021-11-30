package de.ingrid.portal.global;

import java.net.URLConnection;

import sun.misc.BASE64Encoder;

public class UtilsHttpConnection {

    public static void urlConnectionAuth(URLConnection conn, String login, String password) {
        String userAuth = login + ":" + password;
        String encoding = new BASE64Encoder().encode(userAuth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encoding);
    }

}
