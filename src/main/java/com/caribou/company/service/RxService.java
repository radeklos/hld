package com.caribou.company.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import rx.Observable;

import java.io.Serializable;
import java.util.UUID;


public interface RxService<E, ID extends Serializable> {

    Observable<E> create(E e);

    Observable<E> update(ID id, E e);

    Observable<E> update(String uid, E company);

    Observable<E> getRx(ID id);

    Observable<E> getRx(String id);

    E get(ID uid);

    E get(String uid);

    class Imp<R extends CrudRepository<E, ID>, E, ID extends Serializable> implements RxService<E, ID> {

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
        public Observable<E> update(String uid, E company) {
            try {
                return update((ID) UUID.fromString(uid), company);
            } catch (IllegalArgumentException e) {
                return Observable.error(new NotFound());
            }
        }

        @Override
        public Observable<E> getRx(ID uid) {
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

        @Override
        public Observable<E> getRx(String uid) {
            try {
                return getRx((ID) UUID.fromString(uid));
            } catch (IllegalArgumentException e) {
                return Observable.error(new NotFound());
            }
        }

        @Override
        public E get(ID uid) {
            E entity = repository.findOne(uid);
            if (entity != null) {
                return entity;
            }
            throw new NotFound();
        }

        @Override
        public E get(String uid) {
            return get((ID) UUID.fromString(uid));
        }

    }

}
