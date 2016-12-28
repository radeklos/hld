package com.caribou.company.service;

import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;


@Service
public class CompanyService extends RxService.Imp<CompanyRepository, Company, Long> {

    @Autowired
    private CompanyRepository companyRepository;

    public Observable<Company> getByEmployeeEmail(Long uid, String email) {
        return Observable.create(subscriber -> {
            try {
                Company entity = companyRepository.findEmployeeByEmailForUid(email, uid);
                if (entity == null) {
                    throw new NotFound();
                }
                subscriber.onNext(entity);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

}
