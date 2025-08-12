package com.todo.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    // Server
    INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 에러가 발생했습니다"),

    // AUTH
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 올바르지 않습니다"),

    // User
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾지 못했습니다."),


    // GEMINI
    GEMINI_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Gemini에서 예기치 못한 오류가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}