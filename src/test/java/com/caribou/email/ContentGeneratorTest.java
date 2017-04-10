package com.caribou.email;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.email.templates.Invite;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ContentGeneratorTest extends IntegrationTests {

    @Autowired
    private ContentGenerator contentGenerator;

    private Faker faker;

    @Before
    public void setUp() throws Exception {
        faker = new Faker();
    }

    @Test
    public void testInviteTemplate() {
        Invite emailTemplate = Invite.builder()
                .companyName(faker.company().name())
                .departmentName(faker.commerce().department())
                .user(Factory.userAccount())
                .token(faker.crypto().sha512()).build();
        assertThat(contentGenerator.html(emailTemplate, Locale.UK))
                .contains(emailTemplate.getToken())
                .contains(emailTemplate.getCompanyName())
                .contains(emailTemplate.getUser().getFirstName());
    }

}
