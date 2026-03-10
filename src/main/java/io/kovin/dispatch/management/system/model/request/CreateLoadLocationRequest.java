package io.kovin.dispatch.management.system.model.request;

import java.time.LocalDate;

public record CreateLoadLocationRequest(
    String label,
    LocalDate date,
    String location,
    Integer order
) {

}
