package com.hiccproject.moaram.entity.university;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "regions")
public class Region {

    @Id
    private Long id; // AUTO_INCREMENT가 없으므로 수동 입력 필요

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
}
