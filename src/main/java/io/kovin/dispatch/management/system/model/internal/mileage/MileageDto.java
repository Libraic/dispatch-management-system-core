package io.kovin.dispatch.management.system.model.internal.mileage;

import java.time.LocalDate;

public record MileageDto(
    LocalDate date,
    String broker,
    double revenue,
    double miles
) {

    public static MileageDto createEmptyMileageDto(LocalDate date) {
        return new MileageDto(date, null, 0.0, 0.0);
    }
}
