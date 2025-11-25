package io.kovin.dispatch.management.system.strategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.model.enums.KpiAggregationPeriod;
import io.kovin.dispatch.management.system.model.global.kpi.KpiDiscriminator;
import io.kovin.dispatch.management.system.model.internal.mileage.MileageDto;

public interface KpiCalculationStrategy {

    List<KpiDiscriminator> getKpiDiscriminators(
        KpiAggregationPeriod kpiAggregationPeriod,
        Map<LocalDate, List<MileageDto>> mileage,
        LocalDate startDate,
        LocalDate endDate
    );
}
