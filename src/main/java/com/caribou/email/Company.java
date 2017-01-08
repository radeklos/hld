package com.caribou.email;

public class Company {

    public final String name;

    public final String facebook;

    public final String twitter;

    public final String url;

    public Company(String name, String url, String facebook, String twitter) {
        this.name = name;
        this.facebook = facebook;
        this.twitter = twitter;
        this.url = url;
    }
}
