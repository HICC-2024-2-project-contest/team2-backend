package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.Item.ItemImage;
import com.hiccproject.moaram.entity.composite.ItemImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, ItemImageId> {
}
