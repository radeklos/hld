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

    public Observable<Company> update(Long uid, Company company) {
        return Observable.create(subscriber -> {
            try {
                Company entity = companyRepository.findOne(uid);
                if (entity == null) {
                    throw new NotFound();
                }
                company.setUid(entity.getUid());
                companyRepository.save(company);
                subscriber.onNext(company);
                subscriber.onCompleted();
            } catch (Exception | NotFound e) {
                subscriber.onError(e);
            }
        });
    }

}
