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
@Table(name = "d_example3")
public class Example3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "example_1")
    String example1;

    @Column(name = "example_2")
    String example2;

    @Column(name = "example_3")
    String example3;

    @Column(name = "example_4")
    String example4;
}
