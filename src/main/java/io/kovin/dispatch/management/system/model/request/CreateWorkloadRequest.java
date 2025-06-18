package io.kovin.dispatch.management.system.model.request;

public record CreateWorkloadRequest(
    String companyUuid,
    double commission
) {
}
