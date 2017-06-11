package com.caribou.company.service;

import com.caribou.company.domain.Company;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.Optional;
import java.util.UUID;


@Service
public class CompanyService extends RxService.Imp<CompanyRepository, Company, UUID> {

    @Autowired
    private CompanyRepository companyRepository;

    public Observable<CompanyEmployee> getByEmployeeEmail(String uid, String email) {
        return Observable.create(subscriber -> {
            try {
                Optional<CompanyEmployee> entity = companyRepository.findEmployeeByEmailForUid(email, UUID.fromString(uid));
                if (!entity.isPresent()) {
                    throw new NotFound();
                }
                subscriber.onNext(entity.get());
                subscriber.onCompleted();
            } catch (IllegalArgumentException e) {
                throw new NotFound();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<CompanyEmployee> getEmployeeByItsUid(String uid) {
        return Observable.create(subscriber -> {
            try {
                Optional<CompanyEmployee> entity = companyRepository.findByEmployeeByUid(UUID.fromString(uid));
                if (!entity.isPresent()) {
                    throw new NotFound();
                }
                subscriber.onNext(entity.get());
                subscriber.onCompleted();
            } catch (IllegalArgumentException e) {
                throw new NotFound();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }



}
