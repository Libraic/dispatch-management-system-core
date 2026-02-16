package io.kovin.dispatch.management.system.model.response.mileage;

import lombok.Builder;

import java.util.List;

@Builder
public record UpsertDriverMileageResponse(
    String driverMileageUuid,
    List<GetMileageResponse> mileage
) {
}
