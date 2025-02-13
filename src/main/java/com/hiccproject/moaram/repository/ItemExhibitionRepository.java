package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.relation.ItemExhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemExhibitionRepository extends JpaRepository<ItemExhibition, Long> {

    // 아이템과 전시회 모두의 deletedTime과 전시회 isAllowed 상태를 체크
    Optional<ItemExhibition> findByItemIdAndItemDeletedTimeIsNullAndExhibitionIsAllowedAndExhibitionDeletedTimeIsNull(
            Long itemId, boolean isAllowed);
}
