package io.kovin.dispatch.management.system.controller;

import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemGroupException;
import io.kovin.dispatch.management.system.exception.ImpactedArea;
import io.kovin.dispatch.management.system.exception.ImpactedField;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupErrorResponse;
import io.kovin.dispatch.management.system.utils.ErrorUtils;
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
        List<GroupErrorResponse> groupErrorResponses = ErrorUtils.getGroupsErrors(ex.getItemsGroups());
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(ApiResponse.fromError(groupErrorResponses));
    }

    @ExceptionHandler(DispatchManagementSystemException.class)
    public ResponseEntity<ApiResponse<?, ?>> handleDispatchManagementSystemException(DispatchManagementSystemException ex) {
        log.error(ex.getLocalizedMessage());
        var errorResponse = ErrorResponse.builder()
            .message(ex.getLocalizedMessage())
            .field(Optional.ofNullable(ex.getImpactedField()).map(ImpactedField::getMappedField).orElse(null))
            .area(Optional.ofNullable(ex.getImpactedArea()).map(ImpactedArea::getArea).orElse(null))
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
            .message(ex.getLocalizedMessage())
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fromError(errorResponse));
    }
}
