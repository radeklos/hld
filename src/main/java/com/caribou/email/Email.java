package com.caribou.email;

public class Email {

    private Contact from;

    private Contact to;

    private String subject;

    private EmailTemplate template;

    private Email(Builder builder) {
        from = builder.from;
        to = builder.to;
        subject = builder.subject;
        template = builder.template;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Contact getFrom() {
        return from;
    }

    public Contact getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public EmailTemplate getTemplate() {
        return template;
    }

    public static class Contact {

        private final String alias;

        private final String address;

        public Contact(String email, String alias) {
            this.alias = alias;
            this.address = email;
        }

        public String getAlias() {
            return alias;
        }

        public String getAddress() {
            return address;
        }
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
