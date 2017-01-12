package com.caribou.email;

import com.caribou.IntegrationTests;
import com.caribou.email.providers.Mailgun;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EmailSenderTest extends IntegrationTests {

    private Faker faker = new Faker();

    @Autowired
    private SpringTemplateEngine templateEngine;

    private JavaMailSender javaMailSender;

    private Mailgun sender;

    @Before
    public void setUp() throws Exception {
        javaMailSender = mock(JavaMailSender.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        sender = new Mailgun(javaMailSender, templateEngine);
    }

    @Test
    public void emailIsSend() throws Exception {
        Email mimeMessage = Email.newBuilder()
                .from(new Email.Contact(faker.internet().emailAddress(), faker.name().fullName()))
                .to(new Email.Contact(faker.internet().emailAddress(), faker.name().fullName()))
                .subject("subject")
                .build();
        sender.send(mimeMessage, Locale.UK);

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }
}
