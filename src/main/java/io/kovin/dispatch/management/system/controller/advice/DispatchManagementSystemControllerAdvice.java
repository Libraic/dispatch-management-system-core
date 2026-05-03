package io.kovin.dispatch.management.system.controller.advice;

import ch.qos.logback.core.util.StringUtil;

import java.sql.SQLException;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemValidationException;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class DispatchManagementSystemControllerAdvice {

    @ExceptionHandler(DispatchManagementSystemValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(DispatchManagementSystemValidationException ex) {
        logException(ex);
        var errorResponse = ErrorResponse.builder()
            .message(ex.getLocalizedMessage())
            .type(ErrorType.VALIDATION)
            .status(ex.getHttpStatus())
            .errors(ex.getErrors())
            .build();
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(errorResponse);
    }

    @ExceptionHandler(DispatchManagementSystemException.class)
    public ResponseEntity<ApiResponse<?, ?>> handleDispatchManagementSystemException(DispatchManagementSystemException ex) {
        logException(ex);
        var errorResponse = ErrorResponse.builder()
            .message(ex.getLocalizedMessage())
            .status(ex.getHttpStatus())
            .build();
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(ApiResponse.fromError(errorResponse));
    }

    @ExceptionHandler({ IllegalArgumentException.class, NullPointerException.class })
    public ResponseEntity<ApiResponse<?, ?>> handleNativeExceptions(RuntimeException ex) {
        logException(ex);
        String exceptionMessage = ex.getLocalizedMessage();
        String message = StringUtil.isNullOrEmpty(exceptionMessage) ? INTERNAL_SERVER_ERROR : exceptionMessage;
        var errorResponse = ErrorResponse.builder()
            .message(message)
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fromError(errorResponse));
    }

    @ExceptionHandler({ SQLException.class })
    public ResponseEntity<ApiResponse<?, ?>> handleSqlExceptions(Exception sqlException) {
        logException(sqlException);
        var errorResponse = ErrorResponse.builder()
            .message(INTERNAL_SERVER_ERROR)
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fromError(errorResponse));
    }

    private void logException(Exception ex) {
        log.error("An internal error has occurred: ", ex);
    }
}
