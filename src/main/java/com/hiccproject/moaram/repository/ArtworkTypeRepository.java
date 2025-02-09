package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.Item.ArtworkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkTypeRepository extends JpaRepository<ArtworkType, Long> {
}
