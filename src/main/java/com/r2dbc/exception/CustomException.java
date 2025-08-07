package com.r2dbc.exception;

import com.r2dbc.exception.dto.ErrorMessage;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public CustomException(ErrorMessage errorMessage, String message) {
        super(message);
        this.errorMessage = errorMessage;
    }
}