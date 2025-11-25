package io.kovin.dispatch.management.system.model.global.kpi;

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
