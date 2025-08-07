package com.r2dbc.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponseDto {
    private final String code;
    private final String message;
    private final LocalDateTime serverDateTime;


    public static ResponseEntity<ErrorResponseDto> of(ErrorMessage message) {
        return ResponseEntity
                .status(message.getStatus())
                .body(new ErrorResponseDto(
                        message.getStatus().toString(),
                        message.getMessage(),
                        LocalDateTime.now())
                );
    }

    public static ResponseEntity<ErrorResponseDto> of(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        exception.getMessage(),
                        LocalDateTime.now()
                ));
    }
}