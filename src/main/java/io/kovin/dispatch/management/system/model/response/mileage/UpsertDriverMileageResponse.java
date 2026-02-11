package io.kovin.dispatch.management.system.model.response.mileage;

import lombok.Builder;

@Builder
public record UpsertDriverMileageResponse(
    String driverMileageUuid
) {
}
