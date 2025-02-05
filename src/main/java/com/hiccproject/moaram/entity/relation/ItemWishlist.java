package com.hiccproject.moaram.entity.relation;

import com.hiccproject.moaram.entity.Item.Item;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.composite.ItemWishlistId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "item_wishlist")
public class ItemWishlist {

    @EmbeddedId
    private ItemWishlistId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_wishlist_user"))
    private User user;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_wishlist_item"))
    private Item item;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();
}
