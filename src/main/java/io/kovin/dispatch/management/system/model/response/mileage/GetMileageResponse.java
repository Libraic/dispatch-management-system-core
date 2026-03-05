package io.kovin.dispatch.management.system.model.response.mileage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record GetMileageResponse(
    LocalDate date,
    BigDecimal revenue,
    BigDecimal miles,
    String broker,
    String representative,
    String loadStatus,
    String representativeContactNumber,
    String idAcrossTimeframe,
    List<GetLocationResponse> locations
) {
}
