package com.caribou.holiday.rest.dto;

import com.caribou.holiday.domain.When;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Date;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class LeaveDtoTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    @Before
    public void setUp() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void fromCannotBeEmpty() throws Exception {
        LeaveDto leaveDto = LeaveDto.newBuilder().build();

        Set<ConstraintViolation<LeaveDto>> constraintViolations = localValidatorFactory.validate(leaveDto);
        ConstraintViolation<LeaveDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("must be greater than or equal to 0", constraintViolation.getMessage());
        assertEquals("defaultDaysOff", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void reasonCannotBeLongerThan255Chars() throws Exception {
        LeaveDto leaveDto = LeaveDto.newBuilder()
                .reason(new String(new char[256]).replace('\0', 'A'))
                .build();

        Set<ConstraintViolation<LeaveDto>> constraintViolations = localValidatorFactory.validate(leaveDto);
        ConstraintViolation<LeaveDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("size must be between 0 and 255", constraintViolation.getMessage());
    }

    @Test
    public void leaveTypeCannotBeEmpty() throws Exception {
        LeaveDto leaveDto = LeaveDto.newBuilder()
                .from(new Date())
                .leaveAt(When.Morning)
                .to(new Date())
                .returnAt(When.Evening)
                .build();

        Set<ConstraintViolation<LeaveDto>> constraintViolations = localValidatorFactory.validate(leaveDto);
        ConstraintViolation<LeaveDto> constraintViolation = constraintViolations.iterator().next();

        assertEquals(constraintViolations.toString(), 1, constraintViolations.size());
        assertEquals("size must be between 0 and 255", constraintViolation.getMessage());

    }
}
