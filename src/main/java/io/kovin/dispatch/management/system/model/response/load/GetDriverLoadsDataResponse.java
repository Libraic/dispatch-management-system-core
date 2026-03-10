package io.kovin.dispatch.management.system.model.response.load;

import java.util.List;

import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import lombok.Builder;

@Builder
public record GetDriverLoadsDataResponse(
    GetDispatcherResponse dispatcher,
    List<GetDriverLoadsResponse> driverLoads
) {

}
