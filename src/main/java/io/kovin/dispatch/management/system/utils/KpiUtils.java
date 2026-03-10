package io.kovin.dispatch.management.system.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import io.kovin.dispatch.management.system.model.global.reports.financial.KpiDiscriminator;
import io.kovin.dispatch.management.system.model.internal.Pair;
import io.kovin.dispatch.management.system.model.internal.load.AggregationUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KpiUtils {

    public static KpiDiscriminator createKpiDiscriminator(AggregationUnit aggregationUnit, double value, boolean hasCurrency) {
        Pair<LocalDate, LocalDate> startAndEndDate = aggregationUnit.getFirstAndLastDayOfAggregationUnit();
        String label = aggregationUnit.getLabel();
        BigDecimal kpiValue = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_EVEN);
        return new KpiDiscriminator(startAndEndDate.left(), startAndEndDate.right(), label, kpiValue, hasCurrency);
    }
}
