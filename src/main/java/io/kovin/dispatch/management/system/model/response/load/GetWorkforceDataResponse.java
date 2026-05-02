package io.kovin.dispatch.management.system.model.response.load;

import java.util.List;
import java.util.UUID;

import io.kovin.dispatch.management.system.model.response.GetDayOffPeriodResponse;
import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import io.kovin.dispatch.management.system.model.response.GetVehicleMaintenanceResponse;
import lombok.Builder;

@Builder
public record GetWorkforceDataResponse(
    UUID relationUuid,
    GetDriverResponse driver,
    List<GenericLoadResponse> loads,
    List<GetVehicleMaintenanceResponse> vehicleMaintenanceRecords,
    List<GetDayOffPeriodResponse> daysOffPeriods
) {
}
