package com.textrefiner.userservice.repository;

import com.textrefiner.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 중복 가입 방지용 메서드
    boolean existsByEmail(String email);
}