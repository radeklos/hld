package com.caribou.auth.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class UserAccountDtoTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setUp() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void testEmailIsMandatory() {
        UserAccountDto userAccount = UserAccountDto.builder()
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
    public void passwordIsOptionalForDefaultGroup() {
        UserAccountDto userAccount = UserAccountDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        assertThat(constraintViolations).isEmpty();
    }

    @Test
    public void testEmailCannotBeEmpty() {
        UserAccountDto userAccount = UserAccountDto.builder()
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
        UserAccountDto userAccount = UserAccountDto.builder()
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
        UserAccountDto userAccount = UserAccountDto.builder()
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
        UserAccountDto userAccount = UserAccountDto.builder()
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
        UserAccountDto userAccount = UserAccountDto.builder()
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
        UserAccountDto userAccount = UserAccountDto.builder()
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
        UserAccountDto userAccount = UserAccountDto.builder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount, UserAccountDto.CreateGroup.class);
        ConstraintViolation<UserAccountDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be null");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    public void testPasswordCannotBeEmpty() {
        UserAccountDto userAccount = UserAccountDto.builder()
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
        UserAccountDto userAccount = UserAccountDto.builder()
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
        UserAccountDto userAccount = UserAccountDto.builder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        Set<ConstraintViolation<UserAccountDto>> constraintViolations = localValidatorFactory.validate(userAccount);
        assertThat(constraintViolations).isEmpty();
    }

}
