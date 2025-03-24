package com.example.demo.repository;

import com.example.demo.factory.Example1;
import com.example.demo.factory.Example2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Example2Repository extends JpaRepository<Example2, Integer> {
}
