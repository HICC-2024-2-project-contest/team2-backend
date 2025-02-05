package com.hiccproject.moaram.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users", indexes = {@Index(name = "idx_email", columnList = "email")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
