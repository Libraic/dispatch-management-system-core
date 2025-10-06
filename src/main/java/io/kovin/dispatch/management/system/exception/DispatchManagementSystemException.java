package io.kovin.dispatch.management.system.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DispatchManagementSystemException extends RuntimeException {

    private final HttpStatus httpStatus;

    private DispatchManagementSystemException(
        String message,
        HttpStatus httpStatus
    ) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public static DispatchManagementSystemException of(String message, HttpStatus httpStatus) {
        throw new DispatchManagementSystemException(message, httpStatus);
    }
}
