package com.todo.exception;

import com.todo.exception.dto.ErrorMessage;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public CustomException(ErrorMessage errorMessage, String message) {
        super(message);
        this.errorMessage = errorMessage;
    }
}