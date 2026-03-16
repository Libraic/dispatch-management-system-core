package io.kovin.dispatch.management.system.model.request;

import java.time.LocalDate;

public record UpsertDayOffPeriodRequest(
    String relationId,
    LocalDate startDate,
    LocalDate endDate
) {
}
