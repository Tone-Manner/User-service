package com.textrefiner.userservice.controller;

import com.textrefiner.userservice.dto.UserSignupRequest;
import com.textrefiner.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.textrefiner.userservice.dto.UserLoginRequest;
import java.util.HashMap;
import java.util.Map;

import com.textrefiner.userservice.dto.UserProfileResponse;
import com.textrefiner.userservice.util.JwtUtil;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 테스트용 API
    @GetMapping("/test")
    public String test() {
        return "유저 서비스 연결 성공!";
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다!");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserLoginRequest request) {
        String token = userService.login(request);

        // 토큰을 JSON 형태로 반환
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // 내 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        UserProfileResponse response = userService.getUserProfile(email);
        return ResponseEntity.ok(response);
    }

    // 대화창 사용 권한 요청 API (Chat Service가 호출할 예정)
    @PostMapping("/chat-rooms/use")
    public ResponseEntity<String> useChatRoom(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        boolean isAllowed = userService.useChatRoom(email);

        if (isAllowed) {
            return ResponseEntity.ok("대화창 생성 허용");
        } else {
            // 403 Forbidden: 무료 제공량 초과!
            return ResponseEntity.status(403).body("무료 대화창 제공량(4개)을 모두 소진했습니다. PRO로 업그레이드 해주세요.");
        }
    }
}