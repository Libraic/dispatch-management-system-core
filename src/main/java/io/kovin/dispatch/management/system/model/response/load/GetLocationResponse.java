package io.kovin.dispatch.management.system.model.response.load;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record GetLocationResponse(
    String location,
    LocalDate date,
    LocalTime time,
    String label,
    String address,
    Short order,
    String timezone
) {
}
