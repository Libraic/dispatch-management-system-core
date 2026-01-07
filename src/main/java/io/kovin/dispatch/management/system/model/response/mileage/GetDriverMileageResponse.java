package io.kovin.dispatch.management.system.model.response.mileage;

import java.util.List;

import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import lombok.Builder;

@Builder
public record GetDriverMileageResponse(
    GetDispatcherResponse dispatcher,
    List<GetDriverMileageDataResponse> driverMileageDataList
) {

}
