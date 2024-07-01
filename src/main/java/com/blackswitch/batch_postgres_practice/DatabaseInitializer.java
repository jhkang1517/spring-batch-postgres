package com.blackswitch.batch_postgres_practice;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final CustomerRepository customerRepository;

    @PostConstruct
    public void init() {
        initializeCustomers();
    }

    private void initializeCustomers() {
        List<Customer> customers = Arrays.asList(
                Customer.builder().firstName("Alex").lastName("Johnson").balance(new BigDecimal("1500.55")).build(),
                Customer.builder().firstName("Jordan").lastName("Martinez").balance(new BigDecimal("1250.66")).build(),
                Customer.builder().firstName("Morgan").lastName("Williams").balance(new BigDecimal("3000.79")).build(),
                Customer.builder().firstName("Riley").lastName("Kim").balance(new BigDecimal("4000.32")).build(),
                Customer.builder().firstName("Cameron").lastName("Muller").balance(new BigDecimal("5350.654")).build(),
                Customer.builder().firstName("Casey").lastName("Smith").balance(new BigDecimal("200.89")).build(),
                Customer.builder().firstName("Tris").lastName("Garcia").balance(new BigDecimal("600.22")).build()
        );

        customerRepository.saveAll(customers);
    }
}
