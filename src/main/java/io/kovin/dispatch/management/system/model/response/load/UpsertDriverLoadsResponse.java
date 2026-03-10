package io.kovin.dispatch.management.system.model.response.load;

import lombok.Builder;

import java.util.List;

@Builder
public record UpsertDriverLoadsResponse(
    String loadUuid,
    List<GetLoadResponse> loads
) {
}
