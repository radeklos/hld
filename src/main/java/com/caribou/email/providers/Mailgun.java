package com.caribou.email.providers;

import com.caribou.email.ContentGenerator;
import com.caribou.email.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Locale;


@Slf4j
@Component
public class Mailgun implements EmailSender {

    private static final String ENCODING = "UTF-8";
    private final JavaMailSender sender;

    private final ContentGenerator contentGenerator;

    @Autowired
    public Mailgun(JavaMailSender sender, ContentGenerator contentGenerator) {
        this.sender = sender;
        this.contentGenerator = contentGenerator;
    }

    @Override
    public void send(Email email, Locale locale) throws MessagingException {
        final String htmlContent = contentGenerator.html(email.getTemplate(), locale);

        final MimeMessage mimeMessage = this.sender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, ENCODING);

        message.setFrom(toInternetAddress(email.getFrom()));
        message.setTo(toInternetAddress(email.getTo()));
        message.setSubject(email.getSubject());
        message.setText("plain", htmlContent);

        sender.send(message.getMimeMessage());
    }

    private static InternetAddress toInternetAddress(Email.Contact contact) {
        try {
            return new InternetAddress(contact.getEmail(), contact.getAlias());
        } catch (UnsupportedEncodingException e) {
            log.error(String.format("Can't send email because of value %s", contact), e);
            throw new IllegalArgumentException(e);
        }
    }

}
