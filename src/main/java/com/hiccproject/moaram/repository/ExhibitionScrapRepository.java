package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.composite.ExhibitionScrapId;
import com.hiccproject.moaram.entity.relation.ExhibitionScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitionScrapRepository extends JpaRepository<ExhibitionScrap, ExhibitionScrapId> {
}
