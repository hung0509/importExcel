package com.example.demo.factory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "d_example1")
public class Example1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "field_example_1")
    String fieldExample1;

    @Column(name = "field_example_2")
    String fieldExample2;

    @Column(name = "field_example_3")
    String fieldExample3;

    @Column(name = "field_example_4")
    String fieldExample4;
}
