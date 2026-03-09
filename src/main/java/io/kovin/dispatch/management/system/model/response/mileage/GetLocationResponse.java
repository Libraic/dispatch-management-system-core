package io.kovin.dispatch.management.system.model.response.mileage;

import java.time.LocalDate;

public record GetLocationResponse(
    String location,
    LocalDate date,
    String label,
    Integer order
) {
}
