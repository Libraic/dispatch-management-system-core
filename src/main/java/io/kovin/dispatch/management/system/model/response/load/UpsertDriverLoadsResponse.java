package io.kovin.dispatch.management.system.model.response.load;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record UpsertDriverLoadsResponse(
    String loadUuid,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal revenue,
    BigDecimal miles,
    String broker,
    String representative,
    String loadStatus,
    String representativeContactNumber,
    List<GetLocationResponse> locations
) {
}
