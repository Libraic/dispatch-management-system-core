package io.kovin.dispatch.management.system.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.kovin.dispatch.management.system.facade.DriverMileageFacade;
import io.kovin.dispatch.management.system.model.entity.Kpiable;
import io.kovin.dispatch.management.system.model.enums.KpiName;
import io.kovin.dispatch.management.system.model.global.reports.financial.FinancialAggregationModel;
import io.kovin.dispatch.management.system.model.global.reports.financial.FinancialModel;
import io.kovin.dispatch.management.system.model.global.reports.financial.Kpi;
import io.kovin.dispatch.management.system.model.global.reports.financial.KpiDiscriminator;
import io.kovin.dispatch.management.system.model.global.reports.financial.KpiSubject;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverMileageDto;
import io.kovin.dispatch.management.system.model.internal.mileage.MileageDto;
import io.kovin.dispatch.management.system.strategy.KpiCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialService {

    private final DriverMileageFacade driverMileageFacade;

    private final Map<KpiName, KpiCalculationStrategy> kpiCalculationStrategies;


    /**
     * Retrieves a list of financial models based on the provided KPI targets and aggregation model.
     *
     * @param kpiables          the list of entities that serve as targets for KPI calculations.
     * @param aggregationModel  the model defining the aggregation parameters for financial data.
     * @return a list of {@code FinancialModel} objects containing the calculated KPIs for each target entity.
     */
    public List<FinancialModel> getFinancialModels(List<Kpiable> kpiables, FinancialAggregationModel aggregationModel) {
        List<FinancialModel> financialModels = new ArrayList<>();
        LocalDate start = aggregationModel.startDate();
        LocalDate end = aggregationModel.endDate();
        for (Kpiable kpiable : kpiables) {
            List<DriverMileageDto> driverMileageDtos = driverMileageFacade.getDriverMileageDtos(kpiable, start, end);
            Map<LocalDate, List<MileageDto>> mergedMileageGroupedByDate = groupMileageByDateAndCompleteTimelineIfNeeded(
                start,
                end,
                driverMileageDtos
            );
            FinancialModel financialModel = createFinancialModel(
                aggregationModel,
                mergedMileageGroupedByDate,
                start,
                end,
                kpiable
            );
            financialModels.add(financialModel);
        }

        return financialModels;
    }

    private FinancialModel createFinancialModel(
        FinancialAggregationModel aggregationModel,
        Map<LocalDate, List<MileageDto>> mergedMileageGroupedByDate,
        LocalDate startDate,
        LocalDate endDate,
        Kpiable targetEntity
    ) {
        List<Kpi> kpis = new ArrayList<>();
        for (KpiName kpiName : aggregationModel.kpiNames()) {
            KpiCalculationStrategy kpiCalculationStrategy = kpiCalculationStrategies.get(kpiName);
            List<KpiDiscriminator> kpiDiscriminators = kpiCalculationStrategy.getKpiDiscriminators(
                aggregationModel.aggregationPeriod(),
                mergedMileageGroupedByDate,
                startDate,
                endDate
            );
            Kpi kpi = new Kpi(kpiName.getName(), kpiDiscriminators);
            kpis.add(kpi);
        }
        KpiSubject subject = KpiSubject.fromKpiable(targetEntity);
        return new FinancialModel(subject, kpis);
    }

    private Map<LocalDate, List<MileageDto>> groupMileageByDateAndCompleteTimelineIfNeeded(
        LocalDate start,
        LocalDate end,
        List<DriverMileageDto> driverMileageDtos
    ) {
        var mileageGroupedByDate = driverMileageDtos.stream()
            .map(DriverMileageDto::mileage)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(
                MileageDto::date,
                mileage -> {
                    List<MileageDto> list = new ArrayList<>();
                    list.add(mileage);
                    return list;
                },
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                }
            ));

        LocalDate temp = start;
        while (!temp.isAfter(end)) {
            mileageGroupedByDate.putIfAbsent(temp, List.of(MileageDto.createEmptyMileageDto(temp)));
            temp = temp.plusDays(1);
        }

        return mileageGroupedByDate;
    }
}
