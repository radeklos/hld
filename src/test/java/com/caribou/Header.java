package com.caribou;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;

import java.nio.charset.Charset;


public class Header {

    public static HttpHeaders basic(String email, String password) {
        HttpHeaders acceptHeaders = new HttpHeaders();
        String authorization = String.format("%s:%s", email, password);
        String basic = new String(Base64.encodeBase64(authorization.getBytes(Charset.forName("utf-8"))));
        acceptHeaders.set("Authorization", "Basic " + basic);
        return acceptHeaders;
    }

}
