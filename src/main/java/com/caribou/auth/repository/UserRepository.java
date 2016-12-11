package com.caribou.auth.repository;

import com.caribou.auth.domain.UserAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);

}
