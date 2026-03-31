package io.kovin.dispatch.management.system.model.response.load;

import java.util.List;

import io.kovin.dispatch.management.system.model.response.GetDayOffPeriodResponse;
import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import io.kovin.dispatch.management.system.model.response.GetVehicleMaintenanceResponse;
import lombok.Builder;

@Builder
public record GetWorkforceDataResponse(
    String relationUuid,
    GetDriverResponse driver,
    List<GenericLoadResponse> loads,
    List<GetVehicleMaintenanceResponse> vehicleMaintenanceRecords,
    List<GetDayOffPeriodResponse> daysOffPeriods
) {
}
