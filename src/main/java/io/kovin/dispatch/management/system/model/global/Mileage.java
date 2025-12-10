package io.kovin.dispatch.management.system.model.global;

import java.math.BigDecimal;

public record Mileage(
    String date,
    String destinationNote,
    BigDecimal revenue,
    BigDecimal miles,
    String note,
    String broker
) {
}
