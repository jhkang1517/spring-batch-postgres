package com.blackswitch.batch_postgres_practice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {
    private final CustomerRepository customerRepository;
    private final DataSource dataSource;

    @Bean
    public FlatFileItemReader<Customer> customerReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("csv-reader")
                .linesToSkip(1)
                .resource(new ClassPathResource("dummy_data.csv"))
                .delimited()
                .names("firstName", "lastName", "balance")
                .targetType(Customer.class)
                .build();
    }

    @Bean
    public CustomerProcessor customerProcessor() {
        return new CustomerProcessor();
    }

//    @Bean
//    public RepositoryItemWriter<Customer> customerWriter() {
//        return new RepositoryItemWriterBuilder<Customer>()
//                .repository(customerRepository)
//                .methodName("insertEntity")
//                .build();
//    }

    @Bean
        public JdbcBatchItemWriter<Customer> customerWriterV2() {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("INSERT INTO CUSTOMER (first_name, last_name, balance) VALUES (:firstName, :lastName, :balance)")
                .beanMapped()
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, Step step2, JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters[test]}") String test, JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<Customer> reader, CustomerProcessor processor, JdbcBatchItemWriter<Customer> writer) {
        log.info(">>>>> test = {}", test);
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(100000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

//    @Bean
//    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<Customer> reader, CustomerProcessor processor, RepositoryItemWriter<Customer> writer) {
//        return new StepBuilder("step2", jobRepository)
//                .<Customer, Customer>chunk(3, transactionManager)
//                .reader(reader)
//                .processor(processor)
//                .writer(writer)
//                .build();
//    }
}
