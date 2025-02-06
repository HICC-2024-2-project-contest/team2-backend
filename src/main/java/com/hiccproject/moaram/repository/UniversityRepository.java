package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.university.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long> {
}
