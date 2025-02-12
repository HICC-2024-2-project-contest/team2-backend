package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.composite.ItemWishlistId;
import com.hiccproject.moaram.entity.relation.ItemWishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemWishlistRepository extends JpaRepository<ItemWishlist, ItemWishlistId> {
    // 유저 아이디로 모든 위시리스트 아이템 목록을 가져오는 메서드 추가
    @Query("SELECT iw FROM ItemWishlist iw WHERE iw.id.userId = :userId")
    List<ItemWishlist> findByUserId(@Param("userId") Long userId);

    @Query("SELECT i, iw FROM Item i " +
            "LEFT JOIN ItemWishlist iw ON iw.item.id = i.id AND iw.user.id = :userId " +
            "WHERE i.id = :itemId AND i.deletedTime IS NULL")
    Optional<Object[]> findItemWithWishlistStatus(@Param("itemId") Long itemId, @Param("userId") Long userId);
}
