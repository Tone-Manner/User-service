package com.textrefiner.userservice.exception;

import com.textrefiner.userservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 유저 서비스의 모든 컨트롤러에서 발생하는 에러를 여기서 감시
public class GlobalExceptionHandler {

    // 1. 비즈니스 로직(UserService)에서 의도적으로 던진 RuntimeException 처리
    // (예: "이미 가입된 이메일입니다.", "비밀번호가 일치하지 않습니다.")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. DTO에서 @Valid 유효성 검사 실패 시 발생하는 에러 처리
    // (예: 이메일 형식이 이상할 때, 닉네임이 비어있을 때)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        // 여러 개의 유효성 에러 중 첫 번째 에러 메시지만 뽑아옴
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}