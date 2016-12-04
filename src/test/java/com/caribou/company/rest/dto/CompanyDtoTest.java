package com.caribou.company.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class CompanyDtoTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setUp() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void defaultDaysOfShouldBePositive() {
        CompanyDto company = CompanyDto.newBuilder()
                .name("name")
                .defaultDaysOf(-10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("must be greater than or equal to 0");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("defaultDaysOff");
    }

    @Test
    public void nameShouldntBeLongerThan255() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(new String(new char[256]).replace('\0', 'A'))
                .defaultDaysOf(10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("size must be between 0 and 255");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    public void defaultDaysOfShouldntBeEmpty() {
        CompanyDto company = CompanyDto.newBuilder()
                .name("name")
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be null");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("defaultDaysOff");
    }

    @Test
    public void nameShouldntBeNull() {
        CompanyDto company = CompanyDto.newBuilder()
                .defaultDaysOf(10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    public void nameShouldNotBeEmpty() {
        CompanyDto company = CompanyDto.newBuilder()
                .name("")
                .defaultDaysOf(10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    public void isValid() {
        CompanyDto company = CompanyDto.newBuilder()
                .name("name")
                .defaultDaysOf(10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        assertThat(constraintViolations).isEmpty();
    }

}
