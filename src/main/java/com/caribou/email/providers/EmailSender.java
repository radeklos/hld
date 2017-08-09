package com.caribou.email.providers;

import com.caribou.email.Email;

import java.util.Locale;

public interface EmailSender {

    void send(Email email, Locale locale);

}
