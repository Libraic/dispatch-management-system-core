package io.kovin.dispatch.management.system.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DispatchManagementSystemException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ImpactedField impactedField;
    private final ImpactedArea impactedArea;

    private DispatchManagementSystemException(
        String message,
        HttpStatus httpStatus,
        ImpactedField impactedField,
        ImpactedArea impactedArea
    ) {
        super(message);
        this.httpStatus = httpStatus;
        this.impactedField = impactedField;
        this.impactedArea = impactedArea;
    }

    public static DispatchManagementSystemException of(
        String message,
        HttpStatus httpStatus,
        ImpactedField impactedField,
        ImpactedArea impactedArea
    ) {
        throw new DispatchManagementSystemException(message, httpStatus, impactedField, impactedArea);
    }

    public static DispatchManagementSystemException of(String message, HttpStatus httpStatus
    ) {
        throw new DispatchManagementSystemException(message, httpStatus, null, null);
    }

    public static DispatchManagementSystemException of(String message) {
        throw new DispatchManagementSystemException(message, null, null, null);
    }
}
