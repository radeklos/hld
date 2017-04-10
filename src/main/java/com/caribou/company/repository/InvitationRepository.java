package com.caribou.company.repository;

import com.caribou.company.domain.Invitation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface InvitationRepository extends CrudRepository<Invitation, Long> {

    @Query("select i from Invitation i where i.userAccount.email = ?1")
    Optional<Invitation> findByUserEmail(String email);

}
