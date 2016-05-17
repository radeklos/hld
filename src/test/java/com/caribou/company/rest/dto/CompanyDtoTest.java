package com.caribou.company.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;


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

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("must be greater than or equal to 0", constraintViolation.getMessage());
        assertEquals("defaultDaysOff", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void nameShouldntBeLongerThan255() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(new String(new char[256]).replace('\0', 'A'))
                .defaultDaysOf(10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("size must be between 0 and 255", constraintViolation.getMessage());
        assertEquals("name", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void defaultDaysOfShouldntBeEmpty() {
        CompanyDto company = CompanyDto.newBuilder()
                .name("name")
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be null", constraintViolation.getMessage());
        assertEquals("defaultDaysOff", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void nameShouldntBeNull() {
        CompanyDto company = CompanyDto.newBuilder()
                .defaultDaysOf(10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("name", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void nameShouldntBeEmpty() {
        CompanyDto company = CompanyDto.newBuilder()
                .name("")
                .defaultDaysOf(10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("name", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void isValid() {
        CompanyDto company = CompanyDto.newBuilder()
                .name("name")
                .defaultDaysOf(10)
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);

        assertEquals(constraintViolations.toString(), 0, constraintViolations.size());
    }

    @Test
    public void nameTrimsWhiteSpaces() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(" name ")
                .defaultDaysOf(10)
                .build();

        assertEquals("name", company.getName());
    }

}
