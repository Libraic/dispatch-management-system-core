package io.kovin.dispatch.management.system.model.request;

public record CreateSupervisorRequest(
    String uuid,
    String fullName
) {
}
