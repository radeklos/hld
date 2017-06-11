package com.caribou.holiday.repository;

import com.caribou.auth.domain.UserAccount;
import com.caribou.holiday.domain.Leave;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;


public interface LeaveRepository extends CrudRepository<Leave, UUID> {

    Iterable<Leave> findByUserAccount(UserAccount userAccount);

}
