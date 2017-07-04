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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class LeaveService extends RxService.Imp<LeaveRepository, Leave, UUID> {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Observable<Leave> findByUserAccount(UserAccount userAccount) {
        return Observable.from(leaveRepository.findByUserAccount(userAccount));
    }

    public List<EmployeeLeaves> getEmployeeLeaves(String companyId, final LocalDate from, final LocalDate to) {
        return companyRepository.findEmployeesByCompanyUid(UUID.fromString(companyId)).stream()
                .map(e -> EmployeeLeaves.builder()
                        .userAccount(e.getMember())
                        .leaves(leaveRepository.findByUserAccount(
                                e.getMember(),
                                Timestamp.valueOf(from.atStartOfDay()),
                                Timestamp.valueOf(to.atStartOfDay()))
                        )
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Data
    @Builder
    public static class EmployeeLeaves {
        private final UserAccount userAccount;
        private final List<Leave> leaves;
    }

}
