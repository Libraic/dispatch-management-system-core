package io.kovin.dispatch.management.system.model.response;

import lombok.Builder;

@Builder
public record GetDriverResponse(String uuid, String fullName) {
}
