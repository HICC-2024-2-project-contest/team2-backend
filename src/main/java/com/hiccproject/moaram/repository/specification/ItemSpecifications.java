package com.hiccproject.moaram.repository.specification;

import com.hiccproject.moaram.entity.Item.Item;
import com.hiccproject.moaram.util.ItemStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ItemSpecifications {

    // 삭제되지 않은 항목 조건
    public static Specification<Item> hasDeletedTimeNull() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedTime"));
    }

    // 키워드 검색 (name 기준)
    public static Specification<Item> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(keyword)) {
                return criteriaBuilder.like(root.get("name"), "%" + keyword + "%");
            }
            return null;
        };
    }

    // 작품 종류 조건 (ID로 비교하는 대신 ArtworkType 객체로 비교)
    public static Specification<Item> hasType(Integer artworkTypeId) {
        return (root, query, criteriaBuilder) -> artworkTypeId != null ? criteriaBuilder.equal(root.get("artworkType").get("id"), artworkTypeId) : null;
    }

    // 재료 조건 (ID로 비교하는 대신 Material 객체로 비교)
    public static Specification<Item> hasMaterial(Integer materialId) {
        return (root, query, criteriaBuilder) -> materialId != null ? criteriaBuilder.equal(root.get("material").get("id"), materialId) : null;
    }

    // 도구 조건 (ID로 비교하는 대신 Tool 객체로 비교)
    public static Specification<Item> hasTool(Integer toolId) {
        return (root, query, criteriaBuilder) -> toolId != null ? criteriaBuilder.equal(root.get("tool").get("id"), toolId) : null;
    }

    // 상태 조건
    public static Specification<Item> hasStatus(ItemStatus status) {
        return (root, query, criteriaBuilder) -> status != null ? criteriaBuilder.equal(root.get("status"), status) : null;
    }

    // 최소 가격 조건
    public static Specification<Item> hasMinPrice(Integer minPrice) {
        return (root, query, criteriaBuilder) -> minPrice != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice) : null;
    }

    // 최대 가격 조건
    public static Specification<Item> hasMaxPrice(Integer maxPrice) {
        return (root, query, criteriaBuilder) -> maxPrice != null ? criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice) : null;
    }
}

