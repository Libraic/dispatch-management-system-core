package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.model.enums.KpiAggregationPeriod.WEEK;
import static io.kovin.dispatch.management.system.utils.constants.ReportConstants.BROKER_LABEL;
import static io.kovin.dispatch.management.system.utils.constants.ReportConstants.DISPATCHER_SIGNATURE_LABEL;
import static io.kovin.dispatch.management.system.utils.constants.ReportConstants.GROSS_PER_WEEK_LABEL;
import static io.kovin.dispatch.management.system.utils.constants.ReportConstants.MILES_LABEL;
import static io.kovin.dispatch.management.system.utils.constants.ReportConstants.MILES_PER_WEEK_LABEL;
import static io.kovin.dispatch.management.system.utils.constants.ReportConstants.REVENUE_LABEL;
import static io.kovin.dispatch.management.system.utils.constants.ReportConstants.REVENUE_PER_MILE_LABEL;
import static io.kovin.dispatch.management.system.utils.constants.ReportConstants.RPM_PER_WEEK_LABEL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.facade.LoadFacade;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.global.reports.financial.KpiSubject;
import io.kovin.dispatch.management.system.model.global.reports.loadbyload.LoadByLoadData;
import io.kovin.dispatch.management.system.model.global.reports.loadbyload.LoadByLoadItem;
import io.kovin.dispatch.management.system.model.global.reports.loadbyload.LoadByLoadModel;
import io.kovin.dispatch.management.system.model.internal.Accumulator;
import io.kovin.dispatch.management.system.model.internal.load.AggregationUnit;
import io.kovin.dispatch.management.system.model.internal.load.DriverDto;
import io.kovin.dispatch.management.system.model.internal.load.DriverLoadsDto;
import io.kovin.dispatch.management.system.model.internal.load.LoadByLoadMileageDto;
import io.kovin.dispatch.management.system.model.internal.load.LoadDto;
import io.kovin.dispatch.management.system.model.internal.load.WeekAggregationUnit;
import io.kovin.dispatch.management.system.utils.AggregationUtils;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import io.kovin.dispatch.management.system.utils.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadByLoadService {

    private final LoadFacade loadFacade;

    /**
     * Retrieves a list of {@link LoadByLoadModel} instances by processing a list of driver entities
     * and aggregating load-by-load loads data for the specified loadDate range.
     * Each {@link LoadByLoadModel} corresponds to a driver and contains relevant data within the loadDate range.
     *
     * @param driverEntities a list of {@link DriverEntity} objects representing the target drivers
     *                       for which the load-by-load models are to be generated
     * @param start          the start loadDate of the range for filtering load-by-load data
     * @param end            the end loadDate of the range for filtering load-by-load data
     * @return a list of {@link LoadByLoadModel} instances containing load-by-load loads details
     *         for the given drivers within the specified loadDate range
     */
    public List<LoadByLoadModel> getLoadByLoadModels(List<DriverEntity> driverEntities, LocalDate start, LocalDate end) {
        List<LoadByLoadModel> loadByLoadModels = new ArrayList<>();
        for (DriverEntity driver : driverEntities) {
            var groups = createLoadByLoadMileageGroupedByDriver(driver, start, end);
            if (!groups.isEmpty()) {
                LoadByLoadModel loadByLoadModel = createLoadByLoadModel(groups, start, end);
                loadByLoadModels.add(loadByLoadModel);
            }
        }

        return loadByLoadModels;
    }

    /**
     * Groups load-by-load loads data by driver and loadDate within the specified loadDate range for a given driver entity.
     * This method retrieves loads data, maps it to DTOs, and organizes it into a hierarchical structure where
     * the outer map is keyed by driver information and the inner map is keyed by loadDate.
     *
     * @param driver    the {@link DriverEntity} representing the target driver for which loads data is retrieved
     * @param startDate the start loadDate of the range within which to fetch loads data
     * @param endDate   the end loadDate of the range within which to fetch loads data
     * @return a map where:
     * - the key is a {@link DriverDto} representing a driver, and
     * - the value is another map where:
     * - the key is a {@link LocalDate} representing a specific loadDate, and
     * - the value is a {@link LoadByLoadMileageDto} containing load-by-load loads details for that loadDate
     */
    private Map<DriverDto, Map<LocalDate, LoadByLoadMileageDto>> createLoadByLoadMileageGroupedByDriver(
        DriverEntity driver,
        LocalDate startDate,
        LocalDate endDate
    ) {
        List<DriverLoadsDto> driverLoadsDtos = loadFacade.getLoadDtos(driver, startDate, endDate);
        if (driverLoadsDtos.isEmpty()) {
            DriverDto driverDto = new DriverDto(driver.getUuid(), driver.getFullName());
            return Map.of(driverDto, Map.of());
        }

        Map<DriverDto, Map<LocalDate, LoadByLoadMileageDto>> groups = new HashMap<>();
        for (DriverLoadsDto driverLoadsDto : driverLoadsDtos) {
            DriverDto driverDto = driverLoadsDto.driver();
            Map<LocalDate, LoadByLoadMileageDto> mileage = groups.getOrDefault(driverLoadsDto.driver(), new HashMap<>());
            for (LoadDto loadDto : driverLoadsDto.loads()) {
                LoadByLoadMileageDto loadByLoadMileageDto = new LoadByLoadMileageDto(
                    loadDto.broker(),
                    driverLoadsDto.dispatcher().fullName(),
                    loadDto.revenue(),
                    loadDto.miles()
                );
                mileage.putIfAbsent(loadDto.date(), loadByLoadMileageDto);
            }
            groups.putIfAbsent(driverDto, mileage);
        }
        return groups;
    }

    private LoadByLoadModel createLoadByLoadModel(
        Map<DriverDto, Map<LocalDate, LoadByLoadMileageDto>> groups,
        LocalDate start,
        LocalDate end
    ) {
        List<LoadByLoadData> loadByLoadData = new ArrayList<>();
        for (Map.Entry<DriverDto, Map<LocalDate, LoadByLoadMileageDto>> entry : groups.entrySet()) {
            List<List<LoadByLoadItem<?>>> loadByLoadItems = new ArrayList<>();
            Accumulator totalRevenue = new Accumulator();
            Accumulator totalMiles = new Accumulator();
            Map<LocalDate, LoadByLoadMileageDto> mileageDtoMap = entry.getValue();
            AggregationUnit currentAggregationUnit = AggregationUtils.createAggregationUnit(WEEK, start);
            LocalDate temp = start;
            while (!temp.isAfter(end)) {
                AggregationUnit referenceWeekAggregationUnit = new WeekAggregationUnit(temp);
                if (currentAggregationUnit.differentFrom(referenceWeekAggregationUnit)) {
                    LoadByLoadData lbld = LoadByLoadData.builder()
                        .start((LocalDate) currentAggregationUnit.getValue())
                        .end(temp.minusDays(1))
                        .loadByLoadItemsPerWindow(getDefaultLoadByLoadItemsPerWindow(totalRevenue.getValue(), totalMiles.getValue()))
                        .loadByLoadItemsPerDay(loadByLoadItems)
                        .build();
                    loadByLoadData.add(lbld);
                    loadByLoadItems = new ArrayList<>();
                    totalRevenue.reset();
                    totalMiles.reset();
                    currentAggregationUnit = referenceWeekAggregationUnit.cloneUnit();
                }

                var loadByLoadMileage = mileageDtoMap.getOrDefault(
                    temp,
                    LoadByLoadMileageDto.createEmptyLoadByLoadMileageDto()
                );

                BigDecimal revenue = BigDecimalUtils.getBigDecimalFromDouble(loadByLoadMileage.revenue());
                BigDecimal miles = BigDecimalUtils.getBigDecimalFromDouble(loadByLoadMileage.miles());
                loadByLoadItems.add(getDefaultLoadByLoadItems(loadByLoadMileage));
                temp = temp.plusDays(1);
                totalRevenue.add(revenue);
                totalMiles.add(miles);
            }

            LoadByLoadData lbld = LoadByLoadData.builder()
                .start((LocalDate) currentAggregationUnit.getValue())
                .end(temp.minusDays(1))
                .loadByLoadItemsPerWindow(getDefaultLoadByLoadItemsPerWindow(totalRevenue.getValue(), totalMiles.getValue()))
                .loadByLoadItemsPerDay(loadByLoadItems)
                .build();
            loadByLoadData.add(lbld);

            DriverDto driverDto = entry.getKey();
            KpiSubject subject = new KpiSubject(driverDto.uuid(), driverDto.fullName());
            return new LoadByLoadModel(subject, loadByLoadData);
        }

        return null;
    }

    private List<LoadByLoadItem<?>> getDefaultLoadByLoadItems(LoadByLoadMileageDto loadByLoadMileage) {
        BigDecimal rpm = BigDecimalUtils.getBigDecimalFromDouble(
            NumberUtils.divide(loadByLoadMileage.miles(), loadByLoadMileage.revenue())
        );
        LoadByLoadItem<String> brokerItem = new LoadByLoadItem<>(BROKER_LABEL, loadByLoadMileage.broker());
        LoadByLoadItem<String> signatureItem = new LoadByLoadItem<>(DISPATCHER_SIGNATURE_LABEL, loadByLoadMileage.signature());
        LoadByLoadItem<BigDecimal> revenueItem = new LoadByLoadItem<>(REVENUE_LABEL, BigDecimalUtils.getBigDecimalFromDouble(loadByLoadMileage.revenue()));
        LoadByLoadItem<BigDecimal> milesItem = new LoadByLoadItem<>(MILES_LABEL, BigDecimalUtils.getBigDecimalFromDouble(loadByLoadMileage.miles()));
        LoadByLoadItem<BigDecimal> rpmItem = new LoadByLoadItem<>(REVENUE_PER_MILE_LABEL, rpm);
        return List.of(brokerItem, signatureItem, revenueItem, milesItem, rpmItem);
    }

    private List<LoadByLoadItem<?>> getDefaultLoadByLoadItemsPerWindow(BigDecimal totalRevenue, BigDecimal totalMiles) {
        LoadByLoadItem<BigDecimal> grossPerWeek = new LoadByLoadItem<>(GROSS_PER_WEEK_LABEL, totalRevenue);
        LoadByLoadItem<BigDecimal> milesPerWeek = new LoadByLoadItem<>(MILES_PER_WEEK_LABEL, totalMiles);
        LoadByLoadItem<BigDecimal> rpmPerWeek = new LoadByLoadItem<>(
            RPM_PER_WEEK_LABEL,
            BigDecimalUtils.divide(totalRevenue, totalMiles)
        );
        return List.of(grossPerWeek, milesPerWeek, rpmPerWeek);
    }
}
