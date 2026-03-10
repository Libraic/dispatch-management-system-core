package io.kovin.dispatch.management.system.model.response.load;

import java.time.LocalDate;

public record GetLocationResponse(
    String location,
    LocalDate date,
    String label,
    Integer order
) {
}
