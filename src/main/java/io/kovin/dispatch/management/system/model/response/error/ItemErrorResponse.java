package io.kovin.dispatch.management.system.model.response.error;

import java.util.List;

public record ItemErrorResponse(
    String itemIdentifier,
    List<FieldErrorResponse> groupItemFieldsErrors
) {

}
