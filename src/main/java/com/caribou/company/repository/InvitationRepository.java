package com.caribou.company.repository;

import com.caribou.company.domain.Invitation;
import org.springframework.data.repository.CrudRepository;


public interface InvitationRepository extends CrudRepository<Invitation, Long> {
}
