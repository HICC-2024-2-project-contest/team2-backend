package com.hiccproject.moaram.entity.Item;

import com.hiccproject.moaram.entity.composite.ItemImageId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_images")
@Getter
@Setter
public class ItemImage {

    @EmbeddedId
    private ItemImageId id;  // 복합 키 필드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Item item;  // 아이템 엔티티와의 관계 (외래 키 매핑)

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now(); // 생성 시간
}

