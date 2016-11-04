package com.caribou.auth.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class UserAccountTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setUp() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void testEmailIsMandatory() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    public void testEmailCannotBeEmpty() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    public void testEmailHasToBeValid() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe.email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("not a well-formed email address");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    public void testFirstNameIsMandatory() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .lastName("Doe")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("firstName");
    }

    @Test
    public void testFirstNameCannotBeEmpty() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .firstName("")
                .lastName("Doe")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("firstName");
    }

    @Test
    public void testLastNameIsMandatory() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("lastName");
    }

    @Test
    public void testLastNameCannotBeEmpty() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("lastName");
    }

    @Test
    public void testPasswordIsMandatory() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be null");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    public void testPasswordCannotBeEmpty() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("size must be between 6 and 255");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    public void testPasswordCannotBeShorterThan6Characters() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcab")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("size must be between 6 and 255");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    public void testValidUser() {
        UserAccountDto userAccount = UserAccountDto.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        assertThat(constraintViolations).isEmpty();
    }

}
