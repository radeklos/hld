package com.caribou.company.rest.dto;

import com.github.javafaker.Faker;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class CompanyDtoTest {

    private LocalValidatorFactoryBean localValidatorFactory;

    private Faker faker = new Faker();

    @Before
    public void setUp() throws Exception {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void defaultDaysOfShouldBePositive() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(faker.company().name())
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(-100, 0))
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("defaultDaysOff");
        assertThat(constraintViolation.getMessage()).isEqualTo("must be greater than or equal to 0");
    }

    @Test
    public void nameShouldntBeLongerThan255() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(new String(new char[256]).replace('\0', 'A'))
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
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
                .name(faker.company().name())
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
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
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
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
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    public void regNumberShouldNotBeEmpty() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(faker.company().name())
                .regNo("")
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("regNo");
    }

    @Test
    public void address1ShouldNotBeEmpty() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(faker.company().name())
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1("")
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("address1");
    }

    @Test
    public void cityShouldNotBeEmpty() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(faker.company().name())
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city("")
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("city");
    }

    @Test
    public void postCodeShouldNotBeEmpty() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(faker.company().name())
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode("")
                .defaultDaysOff(faker.number().numberBetween(1, 100))
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        ConstraintViolation<CompanyDto> constraintViolation = constraintViolations.iterator().next();

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolation.getMessage()).isEqualTo("may not be empty");
        assertThat(constraintViolation.getPropertyPath().toString()).isEqualTo("postcode");
    }

    @Test
    public void isValid() {
        CompanyDto company = CompanyDto.newBuilder()
                .name(faker.company().name())
                .defaultDaysOff(10)
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
                .build();
        Set<ConstraintViolation<CompanyDto>> constraintViolations = localValidatorFactory.validate(company);
        assertThat(constraintViolations).isEmpty();
    }

}
