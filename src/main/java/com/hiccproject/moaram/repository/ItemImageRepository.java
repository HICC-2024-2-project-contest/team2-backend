package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.Item.ItemImage;
import com.hiccproject.moaram.entity.composite.ItemImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, ItemImageId> {
    List<ItemImage> findByItemIdOrderByIdAsc(Long itemId);
}
