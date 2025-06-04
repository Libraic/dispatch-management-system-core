package io.kovin.dispatch.management.system.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class DispatchManagementSystemException extends RuntimeException {

    @Getter
    private final HttpStatus httpStatus;

    private DispatchManagementSystemException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public static DispatchManagementSystemException of(String message, HttpStatus httpStatus) {
        throw new DispatchManagementSystemException(message, httpStatus);
    }

    public static DispatchManagementSystemException of(String message) {
        throw new DispatchManagementSystemException(message, null);
    }
}
