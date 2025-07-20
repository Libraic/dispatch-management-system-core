package io.kovin.dispatch.management.system.model.request;

import lombok.Builder;

@Builder
public record CreateSupervisorRequest(
    String uuid,
    String fullName
) {
}
