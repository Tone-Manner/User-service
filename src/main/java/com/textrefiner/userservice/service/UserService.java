package com.textrefiner.userservice.service;

import com.textrefiner.userservice.dto.UserSignupRequest;
import com.textrefiner.userservice.entity.User;
import com.textrefiner.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.textrefiner.userservice.dto.UserLoginRequest;
import com.textrefiner.userservice.util.JwtUtil;

import com.textrefiner.userservice.dto.UserProfileResponse;
import com.textrefiner.userservice.entity.UserStatus;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
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

    // 로그인
    @Transactional(readOnly = true)
    public String login(UserLoginRequest request) {
        // 1. 이메일로 유저 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 여부 확인 (평문 비밀번호와 DB의 암호화된 비밀번호를 비교)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 인증 성공 시 JWT 토큰 발급 (status 값 필수 전달!)
        return jwtUtil.generateToken(user.getId(), user.getEmail(), user.getStatus().name());
    }

    // 내 정보 조회 (마이페이지)
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        return new UserProfileResponse(user);
    }

    // 대화창 생성 권한 확인 및 카운트 증가 (핵심 수익 모델)
    @Transactional
    public boolean useChatRoom(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 유료 회원(PRO)이면 패스 (카운트 증가 안 함)
        if (user.getStatus() == UserStatus.PRO) {
            return true;
        }

        // 무료 회원(FREE)이면 4개 미만일 때만 허용 -> 카운트 +1
        if (user.getChatRoomCount() < 4) {
            user.setChatRoomCount(user.getChatRoomCount() + 1);
            // JPA의 변경 감지(Dirty Checking)로 인해 userRepository.save()를 하지 않아도 DB에 자동 반영
            return true;
        }

        // 무료 회원인데 이미 4개를 다 썼다면 거절 (결제 유도 트리거)
        return false;
    }
}