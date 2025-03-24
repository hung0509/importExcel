package com.example.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InterfaceTableRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveRow(Map<String, Object> rowData) {
        String columns = String.join(",", rowData.keySet());
        String values = rowData.values().stream()
                .map(value -> {
                    if (value instanceof String) {
                        return "'" + value + "'";
                    } else if (value instanceof Number) {
                        return value.toString(); // Số giữ nguyên, không thêm dấu nháy
                    } else {
                        return null; // Xử lý trường hợp null hoặc các kiểu khác
                    }
                })
                .collect(Collectors.joining(","));

        String sql = "INSERT INTO interface_table (" + columns + ") VALUES (" + values + ")";
        jdbcTemplate.update(sql);
    }

    public List<Map<String, Object>> fetchAll() {
        return jdbcTemplate.queryForList("SELECT * FROM interface_table");
    }

}
