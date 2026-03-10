package io.kovin.dispatch.management.system.strategy;

import static io.kovin.dispatch.management.system.utils.AggregationUtils.createAggregationUnit;
import static io.kovin.dispatch.management.system.utils.KpiUtils.createKpiDiscriminator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.model.enums.KpiAggregationPeriod;
import io.kovin.dispatch.management.system.model.global.reports.financial.KpiDiscriminator;
import io.kovin.dispatch.management.system.model.internal.load.AggregationUnit;
import io.kovin.dispatch.management.system.model.internal.load.LoadDto;

public class MilesKpiCalculationStrategy implements KpiCalculationStrategy {

    @Override
    public List<KpiDiscriminator> getKpiDiscriminators(
        KpiAggregationPeriod kpiAggregationPeriod,
        Map<LocalDate, List<LoadDto>> mileage,
        LocalDate startDate,
        LocalDate endDate
    ) {
        List<KpiDiscriminator> kpiDiscriminators = new ArrayList<>();
        LocalDate current = startDate;
        AggregationUnit referenceAggregationUnit = createAggregationUnit(kpiAggregationPeriod, current);
        double temp = 0.0;
        while (!current.isAfter(endDate)) {
            List<LoadDto> loadDtos = mileage.get(current);
            for (int i = 0; i < loadDtos.size(); ++i) {
                LoadDto loadDto = loadDtos.get(i);
                AggregationUnit currentAggregationUnit = createAggregationUnit(kpiAggregationPeriod, loadDto.date());
                if (currentAggregationUnit.differentFrom(referenceAggregationUnit)) {
                    KpiDiscriminator kpiDiscriminator = createKpiDiscriminator(referenceAggregationUnit, temp, false);
                    kpiDiscriminators.add(kpiDiscriminator);
                    referenceAggregationUnit = currentAggregationUnit.cloneUnit();
                    temp = 0.0;
                }

                temp += loadDto.miles();
                if (i == mileage.size() - 1) {
                    kpiDiscriminators.add(createKpiDiscriminator(referenceAggregationUnit, temp, false));
                }
            }
            current = current.plusDays(1);
            if (current.isAfter(endDate)) {
                kpiDiscriminators.add(createKpiDiscriminator(referenceAggregationUnit, temp, false));
            }
        }

        return kpiDiscriminators;
    }
}
