package com.caribou.email;

import com.caribou.IntegrationTests;
import com.caribou.email.providers.Mailgun;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EmailSenderTest extends IntegrationTests {

    @Autowired
    Mailgun sender;

    @Ignore
    public void test() throws Exception {
        try {
            Email mimeMessage = Email.newBuilder()
                    .from(new Email.Contact("radek.los@gmail.com", "from"))
                    .to(new Email.Contact("radek.los@gmail.com", "to"))
                    .subject("subject")
                    .build();
            sender.send(mimeMessage, Locale.UK);
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("");
        }
    }
}
