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

    public void saveRow(String columns,  String values, String objectType) {
        String interface_table = getTableName(objectType);

        String sql = "INSERT INTO " +  interface_table + " (" + columns + ") VALUES " + values + ";";
        jdbcTemplate.update(sql);
    }

    public List<Map<String, Object>> fetchAll() {
        return jdbcTemplate.queryForList("SELECT * FROM interface_table");
    }

    private String getTableName(String code){
        return jdbcTemplate.queryForObject("SELECT ref_table FROM objectType where code = '" + code + "';", String.class);
    }

}

//String values = rowData.values().stream()
//        .map(value -> {
//            if (value instanceof String) {
//                return "'" + value + "'";
//            } else if (value instanceof Number) {
//                return value.toString(); // Số giữ nguyên, không thêm dấu nháy
//            } else {
//                return null; // Xử lý trường hợp null hoặc các kiểu khác
//            }
//        })
//        .collect(Collectors.joining(","));

