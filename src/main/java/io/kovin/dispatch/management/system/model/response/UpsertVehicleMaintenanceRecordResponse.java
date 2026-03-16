package io.kovin.dispatch.management.system.model.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UpsertVehicleMaintenanceRecordResponse(
    String vehicleMaintenanceRecordUuid,
    String location,
    LocalDate startDate,
    LocalDate endDate
) {
}
