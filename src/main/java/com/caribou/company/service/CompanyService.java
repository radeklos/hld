package com.caribou.company.service;

import com.caribou.company.domain.Company;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.Optional;


@Service
public class CompanyService extends RxService.Imp<CompanyRepository, Company, Long> {

    @Autowired
    private CompanyRepository companyRepository;

    public Observable<CompanyEmployee> getByEmployeeEmail(Long uid, String email) {
        return Observable.create(subscriber -> {
            try {
                Optional<CompanyEmployee> entity = companyRepository.findEmployeeByEmailForUid(email, uid);
                if (!entity.isPresent()) {
                    throw new NotFound();
                }
                subscriber.onNext(entity.get());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<CompanyEmployee> getEmployeeByItsUid(Long uid) {
        return Observable.create(subscriber -> {
            try {
                Optional<CompanyEmployee> entity = companyRepository.findByEmployeeByUid(uid);
                if (!entity.isPresent()) {
                    throw new NotFound();
                }
                subscriber.onNext(entity.get());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }



}
