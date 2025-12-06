package io.kovin.dispatch.management.system.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.kovin.dispatch.management.system.mapper.DriverMileageMapper;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.entity.Kpiable;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.global.kpi.KpiSubject;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverMileageDto;
import io.kovin.dispatch.management.system.model.global.kpi.Kpi;
import io.kovin.dispatch.management.system.model.global.kpi.KpiAggregationModel;
import io.kovin.dispatch.management.system.model.global.kpi.KpiDiscriminator;
import io.kovin.dispatch.management.system.model.global.kpi.KpiModel;
import io.kovin.dispatch.management.system.model.enums.KpiTargetEntity;
import io.kovin.dispatch.management.system.model.enums.KpiName;
import io.kovin.dispatch.management.system.model.internal.mileage.MileageDto;
import io.kovin.dispatch.management.system.strategy.KpiCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KpiService {

    private final CriteriaService criteriaService;

    private final DriverMileageMapper driverMileageMapper;

    private final Map<KpiName, KpiCalculationStrategy> kpiCalculationStrategies;

    /**
     * Generates KPI models for the given aggregation request.
     * <p>
     * This method performs the following steps for each target entity specified in the
     * {@link KpiAggregationModel}:
     * <ul>
     *   <li>Retrieves the target entities for the specified company.</li>
     *   <li>Fetches driver mileage records for each target entity within the specified date range.</li>
     *   <li>Maps mileage entities to DTOs and merges them, completing the timeline if necessary.</li>
     *   <li>Calculates all requested KPIs for the aggregation period using the registered
     *       {@link KpiCalculationStrategy} instances.</li>
     *   <li>Builds a {@link KpiModel} for each target entity containing its subject and the list of calculated KPIs.</li>
     * </ul>
     *
     * @param aggregationModel the aggregation request containing:
     *                         <ul>
     *                           <li>the target entity type and company UUID</li>
     *                           <li>the KPI names to calculate</li>
     *                           <li>the aggregation period</li>
     *                           <li>the start and end dates for the data</li>
     *                         </ul>
     * @return a list of {@link KpiModel} instances, each representing a target entity with
     *         its corresponding calculated KPIs
     *
     * @throws IllegalArgumentException if the target entity type in {@code aggregationModel} is invalid
     * @throws RuntimeException for any errors occurring during KPI calculation or data mapping
     */
    public List<KpiModel> getKpis(KpiAggregationModel aggregationModel) {
        LocalDate startDate = aggregationModel.startDate();
        LocalDate endDate = aggregationModel.endDate();
        List<KpiModel> kpiModels = new ArrayList<>();
        Class<Kpiable> clazz = getTargetEntityClass(aggregationModel.target());
        List<Kpiable> targetEntities = criteriaService.getTargetEntitiesForKpis(clazz, aggregationModel.companyUuid());
        for (Kpiable targetEntity: targetEntities) {
            List<DriverMileageEntity> driverMileageEntities = criteriaService.getMileageForTargetEntity(
                targetEntity,
                startDate,
                endDate
            );
            List<DriverMileageDto> driverMileageDtos = driverMileageMapper.fromDriverMileageEntitiesToDriverMileageDtos(driverMileageEntities);
            Map<LocalDate, List<MileageDto>> mergedMileageGroupedByDate = groupMileageByDateAndCompleteTimelineIfNeeded(
                startDate,
                endDate,
                driverMileageDtos
            );
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
            KpiModel kpiModel = new KpiModel(subject, kpis);
            kpiModels.add(kpiModel);
        }

        return kpiModels;
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
            mileageGroupedByDate.putIfAbsent(temp, List.of(new MileageDto(temp, 0.0, 0.0)));
            temp = temp.plusDays(1);
        }

        return mileageGroupedByDate;
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getTargetEntityClass(KpiTargetEntity kpiTargetEntity) {
        if (kpiTargetEntity == KpiTargetEntity.DRIVER) {
            return (Class<T>) DriverEntity.class;
        }

        return (Class<T>) UserEntity.class;
    }
}
