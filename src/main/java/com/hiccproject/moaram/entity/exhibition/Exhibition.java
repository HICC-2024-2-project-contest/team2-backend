package com.hiccproject.moaram.entity.exhibition;

import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.university.University;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "exhibitions", indexes = {
        @Index(name = "idx_university_id", columnList = "university_id"),
        @Index(name = "idx_major", columnList = "major"),
        @Index(name = "idx_field_id", columnList = "field_id"),  // 기존 field → field_id로 변경
        @Index(name = "idx_start_date", columnList = "start_date"),
        @Index(name = "idx_end_date", columnList = "end_date"),
        @Index(name = "idx_is_allowed", columnList = "is_allowed")
})
public class Exhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Column(length = 50)
    private String location;

    @Column(nullable = false, length = 50)
    private String major;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)  // field_id 참조 추가
    private Field field;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    @Column(name = "is_allowed")
    private Boolean isAllowed = false;
}
