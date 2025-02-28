package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.Item.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
}
