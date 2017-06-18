package com.caribou.holiday.repository;

import com.caribou.auth.domain.UserAccount;
import com.caribou.holiday.domain.Leave;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;


public interface LeaveRepository extends CrudRepository<Leave, UUID> {

    List<Leave> findByUserAccount(UserAccount userAccount);

    @Query(
            "select l " +
                    "from Leave l " +
                    "where l.userAccount = ?1 and l.to >= ?2 and l.from <= ?3")
    List<Leave> findByUserAccount(UserAccount member, Date from, Date to);

}
