package com.caribou.company.service;

import com.caribou.company.domain.Department;
import com.caribou.company.repository.DepartmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;


@Service
public class DeparmentService implements RxService<Department, Long> {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private DepartmentRepository repository;

    @Override
    public Observable<Department> create(Department department) {
        return Observable.create(subscriber -> {
            try {
                repository.save(department);
                subscriber.onNext(department);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Department> update(Long uid, Department department) {
        return Observable.create(subscriber -> {
            try {
                Department entity = repository.findOne(uid);
                if (entity == null) {
                    throw new NotFound();
                }
                modelMapper.map(department, entity);
                repository.save(entity);
                subscriber.onNext(entity);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Department> get(Long uid) {
        return Observable.create(subscriber -> {
            try {
                Department entity = repository.findOne(uid);
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
