package com.caribou.auth.repository;


import com.caribou.auth.domain.UserAccount;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<UserAccount, Long> {

}
