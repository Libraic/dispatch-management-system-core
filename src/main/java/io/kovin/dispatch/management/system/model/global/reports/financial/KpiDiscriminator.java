package io.kovin.dispatch.management.system.model.global.reports.financial;

import java.math.BigDecimal;
import java.time.LocalDate;

public record KpiDiscriminator(
    LocalDate startDate,
    LocalDate endDate,
    String label,
    BigDecimal value,
    boolean hasCurrency
) {
}
