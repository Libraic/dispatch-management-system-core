package io.kovin.dispatch.management.system.exception;

public record FieldError(
    ImpactedField field,
    String errorMessage
) {

}
