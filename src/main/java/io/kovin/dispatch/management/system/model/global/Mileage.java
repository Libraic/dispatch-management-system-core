package io.kovin.dispatch.management.system.model.global;

import java.math.BigDecimal;

public record Mileage(
    String date,
    BigDecimal revenue,
    BigDecimal miles,
    String broker
) {
}
