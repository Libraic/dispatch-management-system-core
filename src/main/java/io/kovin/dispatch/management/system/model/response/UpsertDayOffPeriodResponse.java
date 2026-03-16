package io.kovin.dispatch.management.system.model.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UpsertDayOffPeriodResponse(String daysOffPeriodId, LocalDate startDate, LocalDate endDate) {
}
