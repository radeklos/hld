package com.caribou.company.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import rx.Observable;

import java.io.Serializable;


public interface RxService<E, ID extends Serializable> {

    Observable<E> create(E e);

    Observable<E> update(ID id, E e);

    Observable<E> get(ID id);

    abstract class Imp<R extends CrudRepository<E, ID>, E, ID extends Serializable> implements RxService<E, ID> {

        @Autowired
        R repository;

        private ModelMapper modelMapper = new ModelMapper();

        @Override
        public Observable<E> create(E entity) {
            return Observable.create(subscriber -> {
                try {
                    repository.save(entity);
                    subscriber.onNext(entity);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            });
        }

        @Override
        public Observable<E> update(ID uid, E company) {
            return Observable.create(subscriber -> {
                try {
                    E entity = repository.findOne(uid);
                    if (entity == null) {
                        throw new NotFound();
                    }
                    modelMapper.map(company, entity);
                    repository.save(entity);
                    subscriber.onNext(entity);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            });
        }

        @Override
        public Observable<E> get(ID uid) {
            return Observable.create(subscriber -> {
                try {
                    E entity = repository.findOne(uid);
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

}
