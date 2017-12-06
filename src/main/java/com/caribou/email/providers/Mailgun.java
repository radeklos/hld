package com.caribou.email.providers;

import com.caribou.email.ContentGenerator;
import com.caribou.email.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
    private final Email.Contact defaultFrom;

    @Autowired
    public Mailgun(JavaMailSender sender, ContentGenerator contentGenerator,
                   @Value("${services.mailgun.defaulAlias}") String defaultAlias,
                   @Value("${services.mailgun.defaultFrom}") String defaultFrom) {
        this.sender = sender;
        this.contentGenerator = contentGenerator;
        this.defaultFrom = new Email.Contact(defaultFrom, defaultAlias);
    }

    @Async
    @Override
    public void send(Email email, Locale locale) {
        final ContentGenerator.Content emailContent = contentGenerator.generate(email.getTemplate(), locale);
        final MimeMessage mimeMessage = this.sender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, ENCODING);
            message.setFrom(toInternetAddress(email.getFrom() == null ? defaultFrom : email.getFrom()));
            message.setTo(toInternetAddress(email.getTo()));
            message.setSubject(emailContent.getSubject());
            message.setText(emailContent.getPlain(), emailContent.getHtml());
            sender.send(message.getMimeMessage());
        } catch (MessagingException e) {
            log.error("Can not sent email {} to {}: {}", email.getTemplate(), email.getTo(), e);
        }
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
