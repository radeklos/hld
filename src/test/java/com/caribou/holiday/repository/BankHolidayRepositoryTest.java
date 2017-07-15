package com.caribou.holiday.repository;

import com.caribou.IntegrationTests;
import com.caribou.holiday.domain.BankHoliday;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class BankHolidayRepositoryTest extends IntegrationTests {

    @Autowired
    BankHolidayRepository bankHolidayRepository;

    @Test
    public void shouldReturnFalseWhenDateIsNotBankHoliday() throws Exception {
        Date date = Date.valueOf(LocalDate.of(2017, 3, 1));
        BankHoliday bankHoliday = BankHoliday.builder()
                .country(BankHoliday.Country.CZ)
                .date(date)
                .build();
        bankHolidayRepository.save(bankHoliday);

        assertThat(bankHolidayRepository.isBankHoliday(Date.valueOf(LocalDate.of(2017, 3, 2)), BankHoliday.Country.CZ)).isFalse();
    }

    @Test
    public void shouldReturnTrueWhenDateIsNotBankHoliday() throws Exception {
        Date date = Date.valueOf(LocalDate.of(2017, 3, 1));
        BankHoliday bankHoliday = BankHoliday.builder()
                .country(BankHoliday.Country.CZ)
                .date(date)
                .build();
        bankHolidayRepository.save(bankHoliday);

        assertThat(bankHolidayRepository.isBankHoliday(date, BankHoliday.Country.CZ)).isTrue();
    }

}
