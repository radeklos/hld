package com.caribou.holiday.rest.dto;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
                .starting(LocalDate.now())
                .ending(LocalDate.now())
                .startingAt(LeaveDto.AMPM.AM)
                .endingAt(LeaveDto.AMPM.PM)
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
                .startingAt(LeaveDto.AMPM.AM)
                .endingAt(LeaveDto.AMPM.PM)
                .build();
        Set<ConstraintViolation<LeaveDto>> constraintViolations = localValidatorFactory.validate(leaveDto);

        assertThat(constraintViolations).isEmpty();
    }

    @Test
    public void startingAtIsMandatory() throws Exception {
        LeaveDto leaveDto = LeaveDto.builder()
                .starting(LocalDate.now())
                .ending(LocalDate.now())
                .endingAt(LeaveDto.AMPM.PM)
                .build();
        List<ConstraintViolation<LeaveDto>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(leaveDto));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("startingAt");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("may not be null");
    }

    @Test
    public void endingAtIsMandatory() throws Exception {
        LeaveDto leaveDto = LeaveDto.builder()
                .starting(LocalDate.now())
                .ending(LocalDate.now())
                .startingAt(LeaveDto.AMPM.PM)
                .build();
        List<ConstraintViolation<LeaveDto>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(leaveDto));

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.get(0).getPropertyPath().toString()).isEqualTo("endingAt");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("may not be null");
    }

}
