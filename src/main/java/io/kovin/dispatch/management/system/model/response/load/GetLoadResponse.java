package io.kovin.dispatch.management.system.model.response.load;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record GetLoadResponse(
    String loadUuid,
    BigDecimal revenue,
    BigDecimal miles,
    String broker,
    String representative,
    String loadStatus,
    String representativeContactNumber,
    LocalDate startDate,
    LocalDate endDate,
    List<GetLocationResponse> locations
) {
}
