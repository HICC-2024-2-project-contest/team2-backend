package com.hiccproject.moaram.entity.university;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "universities",uniqueConstraints = @UniqueConstraint(columnNames = {"name", "campus"}))
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String campus;

    @Column(length = 255)
    private String address;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
}

