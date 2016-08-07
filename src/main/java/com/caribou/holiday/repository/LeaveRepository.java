package com.caribou.holiday.repository;

import com.caribou.holiday.domain.Leave;
import org.springframework.data.repository.CrudRepository;

public interface LeaveRepository extends CrudRepository<Leave, Long> {
}
