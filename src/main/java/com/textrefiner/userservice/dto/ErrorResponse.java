package com.textrefiner.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;      // HTTP 상태 코드 (예: 400)
    private String message;  // 에러 메시지 (예: "이미 가입된 이메일입니다.")
}