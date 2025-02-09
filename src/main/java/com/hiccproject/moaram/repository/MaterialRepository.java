package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.Item.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}
