package com.caribou.holiday.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class LeaveDtoTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setUp() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void reasonCannotBeLongerThan255Chars() throws Exception {
        LeaveDto leaveDto = LeaveDto.builder()
                .reason(new String(new char[256]).replace('\0', 'A'))
                .build();

        Set<ConstraintViolation<LeaveDto>> constraintViolations = localValidatorFactory.validate(leaveDto);
        ConstraintViolation<LeaveDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("size must be between 0 and 255");
    }

    @Test
    public void validLeaveDto() throws Exception {
        LeaveDto leaveDto = LeaveDto.builder()
                .starting(LocalDate.now())
                .ending(LocalDate.now())
                .build();
        Set<ConstraintViolation<LeaveDto>> constraintViolations = localValidatorFactory.validate(leaveDto);

        assertThat(constraintViolations).isEmpty();
    }
}
