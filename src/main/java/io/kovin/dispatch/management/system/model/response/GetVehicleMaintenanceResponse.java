package io.kovin.dispatch.management.system.model.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record GetVehicleMaintenanceResponse(
    String vehicleMaintenanceRecordUuid,
    String location,
    LocalDate startDate,
    LocalDate endDate
) {
}
