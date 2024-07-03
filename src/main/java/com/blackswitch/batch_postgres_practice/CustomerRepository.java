package com.blackswitch.batch_postgres_practice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomCustomerRepository {
}
