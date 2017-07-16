package com.caribou;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.rest.dto.UserAccountDto;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.rest.dto.CompanyDto;
import com.caribou.company.rest.dto.DepartmentDto;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.github.javafaker.Faker;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class Factory {

    public static Faker faker = new Faker();

    public static UserAccount userAccount() {
        return UserAccount.newBuilder()
                .email(faker.internet().emailAddress())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .password(faker.internet().password())
                .build();
    }

    public static UserAccountDto userAccountDto() {
        return UserAccountDto.builder()
                .email(faker.internet().emailAddress())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .password(faker.internet().password())
                .build();
    }

    public static Company company() {
        return Company.newBuilder()
                .name(faker.company().name())
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postCode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
                .build();
    }

    public static CompanyDto companyDto() {
        return CompanyDto.newBuilder()
                .name(faker.company().name())
                .regNo(String.valueOf(faker.number().numberBetween(1_000_000, 9_000_000)))
                .address1(faker.address().streetAddress())
                .city(faker.address().city())
                .postcode(faker.address().zipCode())
                .defaultDaysOff(faker.number().numberBetween(1, 100))
                .build();
    }

    public static Department department(Company company) {
        return Department.newBuilder()
                .company(company)
                .name(faker.commerce().department())
                .daysOff(10)
                .build();
    }

    public static DepartmentDto departmentDto() {
        return DepartmentDto.builder()
                .name(faker.commerce().department())
                .daysOff(10)
                .build();
    }

    public static Leave leave(UserAccount userAccount, LeaveType leaveType) {
        Date _from = faker.date().future(20, TimeUnit.DAYS);
        LocalDate from = LocalDate.from(toLocalDate(_from));
        LocalDate to = LocalDate.from(toLocalDate(faker.date().future(20, TimeUnit.DAYS, _from)));
        return leave(userAccount, leaveType, from, to);
    }

    private static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }

    public static Leave leave(UserAccount userAccount, LeaveType leaveType, LocalDate from, LocalDate to) {
        return leave(userAccount, leaveType, from.atStartOfDay(), to.atStartOfDay());
    }

    public static Leave leave(UserAccount userAccount, LeaveType leaveType, LocalDateTime from, LocalDateTime to) {
        Duration between = Duration.between(from, to);
        return Leave.builder()
                .userAccount(userAccount)
                .leaveType(leaveType)
                .starting(Timestamp.valueOf(from))
                .ending(Timestamp.valueOf(to))
                .numberOfDays((double) between.toDays())
                .status(Leave.Status.CONFIRMED)
                .build();
    }

}
