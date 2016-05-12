package com.caribou.company.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static junit.framework.Assert.assertEquals;


public class DepartmentDtoTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setUp() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void daysOffShouldBePositive() {
        DepartmentDto departmentDto = DepartmentDto.newBuilder()
                .name("name")
                .daysOff(-10)
                .build();
        Set<ConstraintViolation<DepartmentDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("must be greater than or equal to 0", constraintViolation.getMessage());
        assertEquals("daysOff", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void daysOffMayNotBeNull() {
        DepartmentDto departmentDto = DepartmentDto.newBuilder()
                .name("name")
                .build();
        Set<ConstraintViolation<DepartmentDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be null", constraintViolation.getMessage());
        assertEquals("daysOff", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void nameMayNotBeEmpty() {
        DepartmentDto departmentDto = DepartmentDto.newBuilder()
                .daysOff(10)
                .build();
        Set<ConstraintViolation<DepartmentDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("name", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void nameMayNotBeNull() {
        DepartmentDto departmentDto = DepartmentDto.newBuilder()
                .daysOff(10)
                .name(null)
                .build();
        Set<ConstraintViolation<DepartmentDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("may not be empty", constraintViolation.getMessage());
        assertEquals("name", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void nameShouldntBeLongerThan255() {
        DepartmentDto departmentDto = DepartmentDto.newBuilder()
                .name(new String(new char[256]).replace('\0', 'A'))
                .daysOff(10)
                .build();
        Set<ConstraintViolation<DepartmentDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("size must be between 0 and 255", constraintViolation.getMessage());
        assertEquals("name", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void nameTrimsWhiteSpaces() {
        DepartmentDto departmentDto = DepartmentDto.newBuilder()
                .name(" name ")
                .daysOff(10)
                .build();

        assertEquals("name", departmentDto.getName());
    }

}
