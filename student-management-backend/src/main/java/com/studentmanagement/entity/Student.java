package com.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String course;
    private String department;
    private int age;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}