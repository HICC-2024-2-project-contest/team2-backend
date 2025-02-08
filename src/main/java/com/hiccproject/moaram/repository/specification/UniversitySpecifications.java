package com.hiccproject.moaram.repository.specification;

import com.hiccproject.moaram.entity.university.University;
import org.springframework.data.jpa.domain.Specification;

public class UniversitySpecifications {

    public static Specification<University> hasName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null; // keyword가 없으면 동적 쿼리를 추가하지 않음
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }
}
