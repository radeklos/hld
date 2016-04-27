package com.caribou.auth.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static junit.framework.Assert.assertEquals;


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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("email", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("email", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("not a well-formed email address", constraintViolation.getMessage());
        assertEquals("email", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("firstName", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("firstName", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("lastName", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("lastName", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be null", constraintViolation.getMessage());
        assertEquals("password", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("size must be between 6 and 255", constraintViolation.getMessage());
        assertEquals("password", constraintViolation.getPropertyPath().toString());
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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("size must be between 6 and 255", constraintViolation.getMessage());
        assertEquals("password", constraintViolation.getPropertyPath().toString());
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
        assertEquals(constraintViolations.toString(), 0, constraintViolations.size());
    }

}
