package io.kovin.dispatch.management.system.model.response.error;

public record FieldErrorResponse(
    String field,
    String errorMessage
) {
}
