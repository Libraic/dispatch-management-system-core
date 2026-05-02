package io.kovin.dispatch.management.system.model.request;

import java.time.LocalDate;
import java.util.UUID;

public record UpsertVehicleMaintenanceRecordRequest(
    UUID vehicleMaintenanceRecordUuid,
    UUID relationId,
    String location,
    LocalDate startDate,
    LocalDate endDate
) {
}
