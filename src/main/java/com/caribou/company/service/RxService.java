package com.caribou.company.service;

import rx.Observable;


public interface RxService<E, ID> {

    Observable<E> create(E e);

    Observable<E> update(ID id, E e);

    Observable<E> get(ID id);

}
