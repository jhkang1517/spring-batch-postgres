package com.blackswitch.batch_postgres_practice;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryV2 {

    private final JdbcTemplate jdbcTemplate;

    private int batchSize = 10000;

    public void saveAll(List<Customer> items) {
        int batchCount = 0;
        List<Customer> subItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            subItems.add(items.get(i));
            if ((i + 1) % batchSize == 0) {
                batchCount = batchInsert(batchCount, subItems);
            }
        }
        if (!subItems.isEmpty()) {
            batchCount = batchInsert(batchCount, subItems);
        }
        System.out.println("batchCount: " + batchCount);
    }

    private int batchInsert(int batchCount, List<Customer> subItems) {
        jdbcTemplate.batchUpdate("INSERT INTO CUSTOMER (`first_name`, `last_name`, `balance`) VALUES (?, ?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, subItems.get(i).getFirstName());
                ps.setString(2, subItems.get(i).getLastName());
            }
            @Override
            public int getBatchSize() {
                return subItems.size();
            }
        });
        subItems.clear();
        batchCount++;
        return batchCount;
    }
}
