package io.kovin.dispatch.management.system.model.request;

import java.time.LocalDate;

public record UpsertVehicleMaintenanceRecordRequest(
    String relationId,
    String location,
    LocalDate startDate,
    LocalDate endDate
) {
}
