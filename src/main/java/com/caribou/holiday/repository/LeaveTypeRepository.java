package com.caribou.holiday.repository;

import com.caribou.holiday.domain.LeaveType;
import org.springframework.data.repository.CrudRepository;


public interface LeaveTypeRepository extends CrudRepository<LeaveType, Long> {
}
