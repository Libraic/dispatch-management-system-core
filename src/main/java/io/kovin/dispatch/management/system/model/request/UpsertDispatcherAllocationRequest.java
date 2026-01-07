package io.kovin.dispatch.management.system.model.request;

import java.time.LocalDate;

public record UpsertDispatcherAllocationRequest(
    String dispatcherAllocationUuid,
    String dispatcherUuid,
    LocalDate startDate,
    LocalDate endDate
) {
}
