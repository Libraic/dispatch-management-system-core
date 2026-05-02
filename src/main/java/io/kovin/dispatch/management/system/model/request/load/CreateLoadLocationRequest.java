package io.kovin.dispatch.management.system.model.request.load;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateLoadLocationRequest(
    String label,
    LocalDate date,
    LocalTime time,
    String location,
    Short order,
    String address,
    String timezone
) {

}
