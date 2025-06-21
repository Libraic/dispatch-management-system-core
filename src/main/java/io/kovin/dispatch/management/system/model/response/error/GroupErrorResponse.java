package io.kovin.dispatch.management.system.model.response.error;

import java.util.List;

public record GroupErrorResponse(
    String impactedGroup,
    List<ItemErrorResponse> errors
) {
}
