package com.caribou.holiday.repository;

import com.caribou.auth.domain.UserAccount;
import com.caribou.holiday.domain.Leave;
import org.springframework.data.repository.CrudRepository;

public interface LeaveRepository extends CrudRepository<Leave, Long> {

    Iterable<Leave> findByUserAccount(UserAccount userAccount);

}
