package io.kovin.dispatch.management.system.controller.advice;

import java.util.Map;
import java.util.stream.Collectors;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrorResponse;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class DispatchManagementSystemControllerAdvice {

    @ExceptionHandler(DispatchManagementSystemGroupException.class)
    public ResponseEntity<ApiResponse<?, ?>> handleDispatchManagementSystemGroupException(DispatchManagementSystemGroupException ex) {
        Map<String, Object> errors = ex.getGroupsErrors().getErrors()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));
        GroupsErrorResponse groupsErrorResponse = new GroupsErrorResponse(errors);
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(ApiResponse.fromError(groupsErrorResponse));
    }

    @ExceptionHandler(DispatchManagementSystemException.class)
    public ResponseEntity<ApiResponse<?, ?>> handleDispatchManagementSystemException(DispatchManagementSystemException ex) {
        var errorResponse = ErrorResponse.builder()
            .message(ex.getLocalizedMessage())
            .status(ex.getHttpStatus())
            .build();
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(ApiResponse.fromError(errorResponse));
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<ApiResponse<?, ?>> handleNativeExceptions(RuntimeException ex) {
        log.error("An internal error has occurred: [{}].", ex.getLocalizedMessage());
        var errorResponse = ErrorResponse.builder()
            .message(ErrorMessage.INTERNAL_SERVER_ERROR)
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fromError(errorResponse));
    }
}
