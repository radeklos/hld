package com.caribou.holiday.service;

import com.caribou.company.service.RxService;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveService extends RxService.Imp<LeaveRepository, Leave, Long> {

    @Autowired
    LeaveRepository leaveRepository;

}
