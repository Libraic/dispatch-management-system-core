package io.kovin.dispatch.management.system.model.response.load;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetLocationResponse(
    String location,
    LocalDate date,
    LocalTime time,
    String label,
    Integer order
) {
}
