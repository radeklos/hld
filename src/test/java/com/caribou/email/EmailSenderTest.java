package com.caribou.email;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.email.providers.Mailgun;
import com.caribou.email.templates.Invite;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailSenderTest extends IntegrationTests {

    private Faker faker = new Faker();

    @Autowired
    private ContentGenerator contentGenerator;

    @Autowired
    private JavaMailSender javaMailSender;

    private Mailgun sender;

    @Before
    public void setUp() throws Exception {
        javaMailSender = mock(JavaMailSender.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        sender = new Mailgun(javaMailSender, contentGenerator, null, null);
    }

    @Test
    public void emailIsSend() throws Exception {
        Email mimeMessage = Email.builder()
                .from(new Email.Contact(faker.internet().emailAddress(), faker.name().fullName()))
                .to(new Email.Contact(faker.internet().emailAddress(), faker.name().fullName()))
                .template(Invite.builder()
                        .companyName(faker.company().name())
                        .departmentName(faker.commerce().department())
                        .user(Factory.userAccount())
                        .token(faker.crypto().sha512()).build())
                .build();
        sender.send(mimeMessage, Locale.UK);

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }
}
