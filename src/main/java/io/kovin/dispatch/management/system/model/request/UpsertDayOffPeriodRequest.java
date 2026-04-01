package io.kovin.dispatch.management.system.model.request;

import java.time.LocalDate;

public record UpsertDayOffPeriodRequest(
    String daysOffPeriodId,
    String relationId,
    LocalDate startDate,
    LocalDate endDate
) {
}
