package com.caribou.company.service;

import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;


@Service
public class CompanyService implements RxService<Company, Long> {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private CompanyRepository companyRepository;

    @Override
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

    @Override
    public Observable<Company> update(Long uid, Company company) {
        return Observable.create(subscriber -> {
            try {
                Company entity = companyRepository.findOne(uid);
                if (entity == null) {
                    throw new NotFound();
                }
                modelMapper.map(company, entity);
                companyRepository.save(entity);
                subscriber.onNext(entity);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Company> get(Long uid) {
        return Observable.create(subscriber -> {
            try {
                Company entity = companyRepository.findOne(uid);
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

    public Observable<Company> getForEmployeeEmail(Long uid, String email) {
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
