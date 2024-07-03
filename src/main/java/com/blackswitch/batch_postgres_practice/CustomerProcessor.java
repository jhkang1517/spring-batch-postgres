package com.blackswitch.batch_postgres_practice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerProcessor.class);

    @Override
    public Customer process(final Customer customer) {
        final String firstName = customer.getFirstName().toUpperCase();
        final String lastName = customer.getLastName().toUpperCase();

        final Customer transformedPerson = Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .balance(customer.getBalance())
                .build();

        log.info("Converting (" + customer + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }
}
