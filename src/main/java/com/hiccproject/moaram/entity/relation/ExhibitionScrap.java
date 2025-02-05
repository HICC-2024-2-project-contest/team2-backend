package com.hiccproject.moaram.entity.relation;

import com.hiccproject.moaram.entity.Exhibition;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.composite.ExhibitionScrapId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "exhibition_scrap")
public class ExhibitionScrap {

    @EmbeddedId
    private ExhibitionScrapId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_exhibition_scrap_user"))
    private User user;

    @ManyToOne
    @MapsId("exhibitionId")
    @JoinColumn(name = "exhibition_id", nullable = false, foreignKey = @ForeignKey(name = "fk_exhibition_scrap_exhibition"))
    private Exhibition exhibition;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();
}
