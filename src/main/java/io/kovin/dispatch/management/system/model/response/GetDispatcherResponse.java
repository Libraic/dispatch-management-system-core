package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;

@Builder
public record GetDispatcherResponse(
    String uuid,
    String name,
    String phoneNumber
) {
}
