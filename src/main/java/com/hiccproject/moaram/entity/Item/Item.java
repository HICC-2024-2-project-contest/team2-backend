package com.hiccproject.moaram.entity.Item;

import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.university.University;
import com.hiccproject.moaram.util.ItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "items", indexes = {
        @Index(name = "idx_university_id", columnList = "university_id"),
        @Index(name = "idx_artwork_type_id", columnList = "artwork_type_id"),
        @Index(name = "idx_material_id", columnList = "material_id"),
        @Index(name = "idx_tool_id", columnList = "tool_id"),
        @Index(name = "idx_item_name", columnList = "name"),
        @Index(name = "idx_item_status", columnList = "status")
})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    @Column(nullable = false, length = 50)
    private String location;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "artwork_type_id")
    private ArtworkType artworkType;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ItemStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;
}

