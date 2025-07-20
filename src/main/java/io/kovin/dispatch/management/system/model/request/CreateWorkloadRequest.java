package io.kovin.dispatch.management.system.model.request;

import lombok.Builder;

@Builder
public record CreateWorkloadRequest(
    String companyUuid,
    String companyName,
    String itemIdentifier,
    double commission
) {
}
