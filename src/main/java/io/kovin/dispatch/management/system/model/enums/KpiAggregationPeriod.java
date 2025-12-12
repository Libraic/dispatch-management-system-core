package io.kovin.dispatch.management.system.model.enums;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_KPI_AGGREGATION_PERIOD;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;

public enum KpiAggregationPeriod {
    DAY,
    WEEK,
    MONTH,
    QUARTER,
    YEAR;

    public static KpiAggregationPeriod from(String input) {
        if (input == null) {
            return MONTH;
        }

        for (var aggregationPeriod : KpiAggregationPeriod.values()) {
            if (aggregationPeriod.name().equalsIgnoreCase(input.toLowerCase())) {
                return aggregationPeriod;
            }
        }

        throw DispatchManagementSystemException.of(String.format(INVALID_KPI_AGGREGATION_PERIOD, input), BAD_REQUEST);
    }
}
