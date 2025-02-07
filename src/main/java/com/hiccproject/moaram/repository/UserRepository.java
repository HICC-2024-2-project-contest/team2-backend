package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndIsDeletedFalse(Long id);  // is_deleted가 false인 유저 조회
}
