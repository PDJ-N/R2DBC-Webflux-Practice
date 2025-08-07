package com.r2dbc.exception;

import com.r2dbc.exception.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler  {

    /*
     * - 예기치 못하게 발생하는 예외만 처리하도록 함.
     * - 데이터베이스 커넥션 오류, 유효성 검사 실패 등 예측 불가능한 오류만 처리.
     * - 복구 불가능한 예외를 처리하도록 함.
     * */
    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> runtimeExceptionHandler(RuntimeException e) {

        log.error("[Error] BusinessException -> {}", e.getMessage());

        return Mono.just(ErrorResponseDto.of(e));
    }

    /**
     * 애플리케이션 실행 중 문제가 발생했을 때 예외를 생성하는데,
     * 그러한 예외를 처리할 수 있도록 함.
     * */
    @ExceptionHandler(CustomException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleCustomException(CustomException e) {
        log.error("[Error] CustomException -> {}", e.getMessage());

        // CustomException에 담긴 ErrorMessage를 사용해 응답을 생성
        return Mono.just(ErrorResponseDto.of(e.getErrorMessage()));
    }
}