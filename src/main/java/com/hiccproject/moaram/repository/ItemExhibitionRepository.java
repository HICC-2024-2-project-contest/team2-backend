package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.composite.ItemExhibitionId;
import com.hiccproject.moaram.entity.relation.ItemExhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemExhibitionRepository extends JpaRepository<ItemExhibition, ItemExhibitionId> {
}
