package com.caribou.email;

import com.caribou.auth.domain.UserAccount;
import com.caribou.email.templates.EmailTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


public class Email {

    @Getter
    private Contact from;

    @Getter
    private Contact to;

    @Getter
    private String subject;

    private EmailTemplate template;

    private Email(Builder builder) {
        from = builder.from;
        to = builder.to;
        subject = builder.subject;
        template = builder.template;
    }

    public static Builder builder() {
        return new Builder();
    }

    public EmailTemplate getTemplate() {
        return template;
    }

    @ToString
    @AllArgsConstructor
    public static class Contact {

        @Getter
        private final String email;

        @Getter
        private final String alias;

    }

    public static final class Builder {
        private Contact from;
        private Contact to;
        private String subject;
        private EmailTemplate template;

        private Builder() {
        }

        public Builder from(Contact val) {
            from = val;
            return this;
        }

        public Builder to(UserAccount user) {
            to = new Email.Contact(user.getEmail(), String.format("%s %s", user.getFirstName(), user.getLastName()));
            return this;
        }

        public Builder to(Contact val) {
            to = val;
            return this;
        }

        public Builder subject(String val) {
            subject = val;
            return this;
        }

        public Builder template(EmailTemplate val) {
            template = val;
            return this;
        }

        public Email build() {
            return new Email(this);
        }
    }

}
