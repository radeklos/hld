package com.caribou.company.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;


public interface Service<E, ID extends Serializable> {

    E create(E e);

    E update(ID id, E e) throws NotFound;

    E get(ID id) throws NotFound;

    abstract class Imp<R extends CrudRepository<E, ID>, E, ID extends Serializable> implements Service<E, ID> {

        @Autowired
        R repository;

        private ModelMapper modelMapper = new ModelMapper();

        @Override
        public E create(E entity) {
            return repository.save(entity);
        }

        @Override
        public E update(ID uid, E company) throws NotFound {
            E entity = repository.findOne(uid);
            if (entity == null) {
                throw new NotFound();
            }
            modelMapper.map(company, entity);
            return repository.save(entity);
        }

        @Override
        public E get(ID uid) throws NotFound {
            E entity = repository.findOne(uid);
            if (entity == null) {
                throw new NotFound();
            }
            return entity;
        }
    }

}
