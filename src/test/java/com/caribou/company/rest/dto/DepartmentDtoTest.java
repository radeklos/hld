package com.caribou.company.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


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
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .name("name")
                .daysOff(BigDecimal.TEN.negate())
                .boss(UUID.randomUUID().toString())
                .build();
        Set<ConstraintViolation<DepartmentWriteDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentWriteDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("must be greater than or equal to 0");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("daysOff");
    }

    @Test
    public void daysOffMayNotBeNull() {
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .name("name")
                .boss(UUID.randomUUID().toString())
                .build();
        Set<ConstraintViolation<DepartmentWriteDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentWriteDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be null");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("daysOff");
    }

    @Test
    public void nameMayNotBeEmpty() {
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .daysOff(BigDecimal.TEN)
                .boss(UUID.randomUUID().toString())
                .build();
        Set<ConstraintViolation<DepartmentWriteDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentWriteDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    public void nameMayNotBeNull() {
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .daysOff(BigDecimal.TEN)
                .name(null)
                .boss(UUID.randomUUID().toString())
                .build();
        Set<ConstraintViolation<DepartmentWriteDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentWriteDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    public void bossShouldNotBeEmpty() {
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .daysOff(BigDecimal.TEN)
                .name("Department name")
                .boss("")
                .build();
        Set<ConstraintViolation<DepartmentWriteDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentWriteDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("boss");
    }

    @Test
    public void bossShouldNotBeNull() {
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .daysOff(BigDecimal.TEN)
                .name("department name")
                .boss(null)
                .build();
        Set<ConstraintViolation<DepartmentWriteDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentWriteDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("boss");
    }

    @Test
    public void nameShouldntBeLongerThan255() {
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .name(new String(new char[256]).replace('\0', 'A'))
                .daysOff(BigDecimal.TEN)
                .boss(UUID.randomUUID().toString())
                .build();
        Set<ConstraintViolation<DepartmentWriteDto>> constraintViolations = localValidatorFactory.validate(departmentDto);
        ConstraintViolation<DepartmentWriteDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("size must be between 0 and 255");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("name");
    }

}
