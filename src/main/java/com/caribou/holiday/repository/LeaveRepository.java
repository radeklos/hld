package com.caribou.holiday.repository;

import com.caribou.auth.domain.UserAccount;
import com.caribou.holiday.domain.Leave;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


public interface LeaveRepository extends CrudRepository<Leave, UUID> {

    List<Leave> findByUserAccount(UserAccount userAccount);

    @Query("select l " +
            "from Leave l " +
            "where l.userAccount = ?1 and l.ending >= ?2 and l.starting <= ?3")
    List<Leave> findByUserAccount(UserAccount member, Timestamp from, Timestamp to);

}
