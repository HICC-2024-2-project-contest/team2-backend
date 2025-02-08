package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.university.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UniversityRepository extends JpaRepository<University, Long>, JpaSpecificationExecutor<University> {
    List<University> findByNameContainingIgnoreCase(String name);
}
