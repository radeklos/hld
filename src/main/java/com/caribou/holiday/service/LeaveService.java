package com.caribou.holiday.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.service.RxService;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.repository.LeaveRepository;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
public class LeaveService extends RxService.Imp<LeaveRepository, Leave, UUID> {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Observable<Leave> findByUserAccount(UserAccount userAccount) {
        return Observable.from(leaveRepository.findByUserAccount(userAccount));
    }

    public Observable<EmployeeLeaves> getEmployeeLeaves(String companyId, final LocalDate from, final LocalDate to) {
        return Observable.from(companyRepository.findEmployeesByCompanyUid(UUID.fromString(companyId)))
                .map(e -> EmployeeLeaves.builder()
                        .userAccount(e.getMember())
                        .leaves(leaveRepository.findByUserAccount(e.getMember(), Date.valueOf(from), Date.valueOf(to)))
                        .build());
    }

    @Data
    @Builder
    static class EmployeeLeaves {
        private final UserAccount userAccount;
        private final List<Leave> leaves;
    }

}
