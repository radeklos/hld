package com.caribou.auth.repository;

import com.caribou.auth.domain.UserAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends CrudRepository<UserAccount, UUID> {

    Optional<UserAccount> findByEmail(String email);

}
