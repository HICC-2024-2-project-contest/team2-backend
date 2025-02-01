package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Integer> {
}
