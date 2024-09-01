package com.taxilf.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class CustomTooManyRequestException extends RuntimeException {
    public CustomTooManyRequestException(String message) {
        super(message);
    }
}
