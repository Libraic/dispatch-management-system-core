package io.kovin.dispatch.management.system.model.request;

import java.time.LocalDate;
import java.util.UUID;

public record UpsertDayOffPeriodRequest(
    UUID daysOffPeriodId,
    UUID relationId,
    LocalDate startDate,
    LocalDate endDate
) {
}
