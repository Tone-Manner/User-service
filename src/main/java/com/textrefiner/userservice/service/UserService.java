package com.textrefiner.userservice.service;

import com.textrefiner.userservice.dto.UserSignupRequest;
import com.textrefiner.userservice.entity.User;
import com.textrefiner.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(UserSignupRequest request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        // 2. 엔티티 생성 및 데이터 세팅
        User user = new User();
        user.setEmail(request.getEmail());

        // 비밀번호는 절대 원본으로 넣지 않고, 암호화해서 넣는다
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setNickname(request.getNickname());
        // status(FREE)와 chatRoomCount(0)는 Entity에 설정한 기본값이 자동으로 들어감

        // 3. DB 저장
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }
}