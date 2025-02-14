package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.exhibition.Exhibition;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long>, JpaSpecificationExecutor<Exhibition> {

    Optional<Exhibition> findByIdAndIsAllowedAndDeletedTimeIsNull(Long exhibitionId, Boolean isAllowed);

    List<Exhibition> findAll(Specification<Exhibition> spec);

    @Query("SELECT e FROM Exhibition e WHERE " +
            "(e.startDate <= :endDate AND e.endDate >= :startDate) " +  // 기간이 겹치는 전시 포함
            "AND e.isAllowed = true " +
            "AND e.deletedTime IS NULL")
    List<Exhibition> findByDateRangeAndIsAllowedAndDeletedTimeIsNull(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
