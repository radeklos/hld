package com.caribou.email;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.email.templates.Invite;
import com.caribou.email.templates.LeaveApproved;
import com.caribou.holiday.domain.Leave;
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
        assertThat(contentGenerator.generate(emailTemplate, Locale.UK).getHtml())
                .contains(emailTemplate.getToken())
                .contains(emailTemplate.getUser().getFirstName());
    }

    @Test
    public void leaveApproved() throws Exception {
        Leave leave = Factory.leave(Factory.userAccount(), Factory.leaveType(Factory.company()));
        String content = contentGenerator.generate(LeaveApproved.builder().leave(leave).build(), Locale.UK).getHtml();
        assertThat(content).contains("Time off booked");
    }

}
