package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;

@Builder
public record UserData(
    String uuid
) {
}
