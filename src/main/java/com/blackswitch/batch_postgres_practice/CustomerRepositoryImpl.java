package com.blackswitch.batch_postgres_practice;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class CustomerRepositoryImpl implements CustomCustomerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void insertEntity(Customer customer) {
        entityManager.persist(customer);
    }
}
