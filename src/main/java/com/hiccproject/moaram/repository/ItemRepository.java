package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.Item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAll(Specification<Item> spec, Pageable pageable);
}
