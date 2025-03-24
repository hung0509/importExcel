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
@Table(name = "d_example2")
public class Example2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "field_1")
    String field1;

    @Column(name = "field_2")
    String field2;

    @Column(name = "field_3")
    String field3;

    @Column(name = "field_4")
    String field4;
}
