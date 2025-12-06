package io.kovin.dispatch.management.system.model.internal.mileage;

import java.time.LocalDate;

public record MileageDto(
    LocalDate date,
    double revenue,
    double miles
) {
}
