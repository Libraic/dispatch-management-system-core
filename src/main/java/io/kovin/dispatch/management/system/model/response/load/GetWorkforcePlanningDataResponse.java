package io.kovin.dispatch.management.system.model.response.load;

import java.util.List;

import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import lombok.Builder;

@Builder
public record GetWorkforcePlanningDataResponse(
    GetDispatcherResponse dispatcher,
    List<GetDriverPlanningDataResponse> driverPlanningData
) {

}
