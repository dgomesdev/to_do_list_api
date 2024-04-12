package com.dgomesdev.to_do_list_api.service;

public interface BaseCrudService<T, ID> {
    T save(T entity);
    T findById(ID id);
    T update(T entity);
    void delete(ID id);
}