package com.blackswitch.batch_postgres_practice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {
    private final CustomerRepository customerRepository;

    @Bean
    public FlatFileItemReader<Customer> customerReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("csv-reader")
                .resource(new ClassPathResource("customer.csv"))
                .delimited()
                .names("firstName", "lastName")
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public CustomerProcessor customerProcessor() {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> customerWriter() {
        return new RepositoryItemWriterBuilder<Customer>()
                .repository(customerRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters[date]}") String date, JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<Customer> reader, CustomerProcessor processor, RepositoryItemWriter<Customer> writer) {
        log.info(">>>>> date = {}", date);
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(2, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
