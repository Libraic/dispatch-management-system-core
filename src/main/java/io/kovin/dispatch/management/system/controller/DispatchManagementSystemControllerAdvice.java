package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DispatchManagementSystemControllerAdvice {

    @ExceptionHandler(DispatchManagementSystemException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(DispatchManagementSystemException ex) {
        var errorResponse = Error.builder()
            .message(ex.getLocalizedMessage())
            .status(ex.getHttpStatus())
            .build();
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(ApiResponse.fromError(errorResponse));
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<ApiResponse<?>> handleNativeExceptions(RuntimeException ex) {
        var errorResponse = Error.builder()
            .message(ex.getLocalizedMessage())
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fromError(errorResponse));
    }
}
