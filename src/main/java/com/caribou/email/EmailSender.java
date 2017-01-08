package com.caribou.email;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public interface EmailSender {

    void send(Email email, Locale locale) throws UnsupportedEncodingException, MessagingException;

}
