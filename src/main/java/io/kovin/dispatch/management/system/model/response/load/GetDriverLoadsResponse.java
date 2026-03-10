package io.kovin.dispatch.management.system.model.response.load;

import java.util.List;

import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import lombok.Builder;

@Builder
public record GetDriverLoadsResponse(
    String loadUuid,
    GetDriverResponse driver,
    List<GetLoadResponse> loads
) {
}
