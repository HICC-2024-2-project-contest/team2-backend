package com.hiccproject.moaram.entity.relation;

import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.entity.Item.Item;
import com.hiccproject.moaram.entity.composite.ItemExhibitionId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "item_exhibition")
public class ItemExhibition {

    @EmbeddedId
    private ItemExhibitionId id;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_exhibition_item"))
    private Item item;

    @ManyToOne
    @MapsId("exhibitionId")
    @JoinColumn(name = "exhibition_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_exhibition_exhibition"))
    private Exhibition exhibition;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();
}
