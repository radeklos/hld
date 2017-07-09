package com.caribou.holiday.repository;

import com.caribou.holiday.domain.BankHoliday;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.UUID;


public interface BankHolidayRepository extends CrudRepository<BankHoliday, UUID> {

    @Query("SELECT count(b) > 0 FROM BankHoliday b WHERE b.date = ?1 AND b.country = ?2")
    boolean isBankHoliday(Date date, BankHoliday.Country country);

}
