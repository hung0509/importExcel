package com.example.demo.repository;

import com.example.demo.model.ExcelData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcelDataRepository extends JpaRepository<ExcelData, Long> {
}
