package com.hiccproject.moaram.repository.specification;

import com.hiccproject.moaram.entity.exhibition.Exhibition;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class ExhibitionSpecifications {

    // 시작 날짜 조건
    public static Specification<Exhibition> hasStartDate(LocalDate startDate) {
        return (root, query, criteriaBuilder) ->
                startDate != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate) : null;
    }

    // 종료 날짜 조건
    public static Specification<Exhibition> hasEndDate(LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                endDate != null ? criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate) : null;
    }

    // 전시 분야 조건
    public static Specification<Exhibition> hasField(Long fieldId) {
        return (root, query, criteriaBuilder) ->
                fieldId != null ? criteriaBuilder.equal(root.get("fieldId"), fieldId) : null;  // 필드 ID로 비교
    }

    // keyword로 name과 university.name을 검색하는 조건
    public static Specification<Exhibition> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("university").get("name"), "%" + keyword + "%")
                );
            }
            return null;
        };
    }

    // isAllowed가 true 조건
    public static Specification<Exhibition> hasIsAllowedTrue() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isAllowed"));
    }

    // deletedTime이 null인 조건
    public static Specification<Exhibition> hasDeletedTimeNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedTime"));
    }
}
