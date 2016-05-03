package com.caribou.company.service;

import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;


@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Observable<Company> create(Company company) {
        return Observable.create(subscriber -> {
            try {
                companyRepository.save(company);
                subscriber.onNext(company);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
