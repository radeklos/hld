package com.caribou.holiday.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.company.service.RxService;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.UUID;


@Service
public class LeaveService extends RxService.Imp<LeaveRepository, Leave, UUID> {

    @Autowired
    private LeaveRepository leaveRepository;

    public Observable<Leave> findByUserAccount(UserAccount userAccount) {
        return Observable.from(leaveRepository.findByUserAccount(userAccount));
    }

}
