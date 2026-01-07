package io.kovin.dispatch.management.system.model.response.mileage;

import java.util.List;

import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import lombok.Builder;

@Builder
public record GetDriverMileageDataResponse(
    String driverMileageUuid,
    GetDriverResponse driver,
    List<GetMileageResponse> mileage
) {
}
