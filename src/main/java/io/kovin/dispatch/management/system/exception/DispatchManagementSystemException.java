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
        return new DispatchManagementSystemException(message, httpStatus);
    }

    public static DispatchManagementSystemException ofInternal(String message) {
        return new DispatchManagementSystemException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static DispatchManagementSystemException ofBadRequest(String message) {
        return new DispatchManagementSystemException(message, HttpStatus.BAD_REQUEST);
    }
}
