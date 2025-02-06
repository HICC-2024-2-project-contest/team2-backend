package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.Exhibition;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long>, JpaSpecificationExecutor<Exhibition> {

    Optional<Exhibition> findByIdAndIsAllowedAndDeletedTimeIsNull(Long exhibitionId, Boolean isAllowed);

    List<Exhibition> findAll(Specification<Exhibition> spec);
}
