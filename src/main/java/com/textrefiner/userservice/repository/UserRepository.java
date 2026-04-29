package com.textrefiner.userservice.repository;

import com.textrefiner.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 중복 가입 방지용 메서드
    boolean existsByEmail(String email);
    // 로그인할 때 이 이메일 가진 유저 정보 확인 용도
    Optional<User> findByEmail(String email);
}