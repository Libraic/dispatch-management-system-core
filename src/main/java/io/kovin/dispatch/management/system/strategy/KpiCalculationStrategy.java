package io.kovin.dispatch.management.system.strategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.model.enums.KpiAggregationPeriod;
import io.kovin.dispatch.management.system.model.global.reports.financial.KpiDiscriminator;
import io.kovin.dispatch.management.system.model.internal.load.LoadDto;

public interface KpiCalculationStrategy {

    List<KpiDiscriminator> getKpiDiscriminators(
        KpiAggregationPeriod kpiAggregationPeriod,
        Map<LocalDate, List<LoadDto>> mileage,
        LocalDate startDate,
        LocalDate endDate
    );
}
