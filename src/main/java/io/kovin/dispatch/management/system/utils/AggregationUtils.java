package io.kovin.dispatch.management.system.utils;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_KPI_AGGREGATION_PERIOD;

import java.time.LocalDate;
import io.kovin.dispatch.management.system.model.internal.mileage.DayAggregationUnit;
import io.kovin.dispatch.management.system.model.enums.KpiAggregationPeriod;
import io.kovin.dispatch.management.system.model.internal.mileage.AggregationUnit;
import io.kovin.dispatch.management.system.model.internal.mileage.MonthAggregationUnit;
import io.kovin.dispatch.management.system.model.internal.mileage.QuarterAggregationUnit;
import io.kovin.dispatch.management.system.model.internal.mileage.YearAggregationUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A utility class that provides support for aggregating values across a specified timeframe.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AggregationUtils {

    /**
     * Extracts the corresponding time unit from the LocalDate object and creates an AggregationUnit object from it.
     *
     * @param kpiAggregationPeriod the period the aggregation is done for.
     * @param date                 the LocalDate object used to extract the time unit.
     * @return                     the AggregationUnit object.
     */
    public static AggregationUnit createAggregationUnit(KpiAggregationPeriod kpiAggregationPeriod, LocalDate date) {
        if (kpiAggregationPeriod == KpiAggregationPeriod.DAY) {
            return new DayAggregationUnit(date);
        } else if (kpiAggregationPeriod == KpiAggregationPeriod.MONTH) {
            return new MonthAggregationUnit(date);
        } else if (kpiAggregationPeriod == KpiAggregationPeriod.QUARTER) {
            return new QuarterAggregationUnit(date);
        } else if (kpiAggregationPeriod == KpiAggregationPeriod.YEAR) {
            return new YearAggregationUnit(date.getYear());
        }

        throw new IllegalArgumentException(String.format(INVALID_KPI_AGGREGATION_PERIOD, kpiAggregationPeriod.name()));
    }
}
