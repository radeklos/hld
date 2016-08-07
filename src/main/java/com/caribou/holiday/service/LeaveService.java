package com.caribou.holiday.service;

import com.caribou.company.service.RxService;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

@Service
public class LeaveService implements RxService<Leave, Long> {

    @Autowired
    LeaveRepository leaveRepository;

    @Override
    public Observable<Leave> create(Leave leave) {
        return Observable.create(subscriber -> {
            try {
                if (leave.getFrom().after(leave.getTo())) {
                    throw new ServiceValidationException();
                }
                if (leave.getFrom().equals(leave.getTo()) && !leave.getFromWholeDay() && !leave.getToWholeDay()) {
                    throw new ServiceValidationException();
                }

                leaveRepository.save(leave);
                subscriber.onNext(leave);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Leave> update(Long aLong, Leave leave) {
        return null;
    }

    @Override
    public Observable<Leave> get(Long aLong) {
        return null;
    }
}
