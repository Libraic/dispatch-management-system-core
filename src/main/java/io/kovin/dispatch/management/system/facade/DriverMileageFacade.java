package io.kovin.dispatch.management.system.facade;

import ch.qos.logback.core.util.StringUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.mapper.DriverMileageMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.DispatcherEntity;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.entity.Kpiable;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverMileageDto;
import io.kovin.dispatch.management.system.model.request.UpsertDriverMileageRequest;
import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import io.kovin.dispatch.management.system.model.response.mileage.GetDriverMileageDataResponse;
import io.kovin.dispatch.management.system.model.response.mileage.GetDriverMileageResponse;
import io.kovin.dispatch.management.system.model.response.mileage.GetMileageResponse;
import io.kovin.dispatch.management.system.model.response.mileage.UpsertDriverMileageResponse;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.DispatcherService;
import io.kovin.dispatch.management.system.service.DriverService;
import io.kovin.dispatch.management.system.service.DriverMileageService;
import io.kovin.dispatch.management.system.utils.BigDecimalUtils;
import io.kovin.dispatch.management.system.validation.DriverMileageValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.START_DATE_BEFORE_END_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverMileageFacade {

    private final DriverMileageValidationService driverMileageValidationService;

    private final CompanyService companyService;
    private final DriverService driverService;
    private final DriverMileageService driverMileageService;
    private final CriteriaService criteriaService;
    private final DispatcherService dispatcherService;

    private final DriverMileageMapper driverMileageMapper;

    /**
     * Updates or inserts driver mileage data based on the provided request.
     * This involves validation, extracting the necessary entities like the company,
     * dispatcher, and driver, and computing mileage entities for the current and
     * possibly next week. The mileage entities are then persisted.
     *
     * @param request the request object containing details for upserting the driver mileage.
     *                Includes fields such as companyUuid, dispatcherUuid, driverUuid,
     *                startDate, endDate, mileageDate, revenue, miles, and broker.
     * @return an {@link UpsertDriverMileageResponse} containing the UUID of the upserted driver mileage record.
     */
    @Transactional
    public UpsertDriverMileageResponse upsertMileage(UpsertDriverMileageRequest request) {
        if (request == null) {
            return null;
        }

        driverMileageValidationService.validateDriversMileageUpsertion(request);

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        DispatcherEntity dispatcher = getDispatcher(request.dispatcherUuid());
        DriverEntity driver = getDriver(request.driverUuid());

        List<DriverMileageEntity> driverMileageEntities = new ArrayList<>(2);
        DriverMileageEntity driverMileageEntity = getCurrentWeekDriverMileageEntity(
            request,
            company,
            dispatcher,
            driver
        );
        driverMileageEntities.add(driverMileageEntity);
        if (request.mileageDate() != null) {
            DriverMileageEntity commonWeekEntity = request.mileageDate().isAfter(request.endDate())
                ? getNextWeekDriverMileageEntity(request, company, dispatcher, driver)
                : getPreviousWeekDriverMileageEntity(request, company, dispatcher, driver);
            driverMileageEntities.add(commonWeekEntity);
        }

        driverMileageService.saveAllDriverMileageEntities(driverMileageEntities);
        return UpsertDriverMileageResponse.builder()
            .driverMileageUuid(driverMileageEntity.getUuid())
            .build();
    }

    /**
     * Retrieves driver mileage data for all drivers in a specified company within a given timeframe.
     * The data is grouped by dispatcher and includes detailed mileage information for each driver.
     *
     * @param companyUuid the unique identifier of the company for which to retrieve driver mileage data.
     * @param startDate the start date of the timeframe for which mileage data is to be retrieved.
     * @param endDate the end date of the timeframe for which mileage data is to be retrieved.
     * @return a list of {@link GetDriverMileageResponse}, where each response contains a dispatcher and
     *         the corresponding list of driver mileage data grouped by dispatcher and ordered by creation date.
     */
    public List<GetDriverMileageResponse> getDriversMileageForTimeframe(
        String companyUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        // TODO: Replace with "exists"
        companyService.getByUuid(companyUuid);
        if (startDate.isAfter(endDate)) {
            throw DispatchManagementSystemException.of(START_DATE_BEFORE_END_DATE, HttpStatus.BAD_REQUEST);
        }

        List<GetDriverMileageResponse> responses = new ArrayList<>();
        List<DispatcherEntity> dispatchers = dispatcherService.getAllDispatchersWithDriversByCompany(companyUuid);
        for (DispatcherEntity dispatcher : dispatchers) {
            String dispatcherUuid = dispatcher.getUuid();
            GetDispatcherResponse getDispatcherResponse = GetDispatcherResponse.builder()
                .uuid(dispatcherUuid)
                .name(dispatcher.getName())
                .build();
            List<GetDriverMileageDataResponse> getDriverMileageDataResponses = new ArrayList<>();
            for (DriverEntity driver : dispatcher.getDrivers()) {
                String driverUuid = driver.getUuid();
                GetDriverResponse getDriverResponse = GetDriverResponse.builder()
                    .fullName(driver.getFullName())
                    .uuid(driverUuid)
                    .build();
                Optional<DriverMileageEntity> driverMileageEntityOptional = driverMileageService.getDriversMileageForTimeframe(
                    companyUuid,
                    dispatcherUuid,
                    driverUuid,
                    startDate,
                    endDate
                );
                List<GetMileageResponse> getMileageResponses = driverMileageEntityOptional.map(driverMileageEntity -> driverMileageEntity.getMileageData()
                    .entrySet()
                    .stream()
                    .map(mileageData -> GetMileageResponse.builder()
                        .date(LocalDate.parse(mileageData.getKey()))
                        .miles(mileageData.getValue().getMiles())
                        .revenue(mileageData.getValue().getRevenue())
                        .broker(mileageData.getValue().getBroker())
                        .build()
                    ).toList()
                ).orElse(List.of());
                GetDriverMileageDataResponse getDriverMileageDataResponse = GetDriverMileageDataResponse.builder()
                    .driverMileageUuid(driverMileageEntityOptional.map(DriverMileageEntity::getUuid).orElse(null))
                    .driver(getDriverResponse)
                    .mileage(getMileageResponses)
                    .build();
                getDriverMileageDataResponses.add(getDriverMileageDataResponse);
            }
            GetDriverMileageResponse getDriverMileageResponse = GetDriverMileageResponse.builder()
                .dispatcher(getDispatcherResponse)
                .driverMileageDataList(getDriverMileageDataResponses)
                .build();
            responses.add(getDriverMileageResponse);
        }

        List<DriverEntity> driversWithoutDispatchers = driverService.getDriversWithoutDispatchersByCompanyUuid(companyUuid);

        List<GetDriverMileageDataResponse> driversWithoutDispatchersMileageDataResponses = new ArrayList<>();
        for (DriverEntity driver : driversWithoutDispatchers) {
            driversWithoutDispatchersMileageDataResponses.add(GetDriverMileageDataResponse.builder()
                .driver(GetDriverResponse.builder().uuid(driver.getUuid()).fullName(driver.getFullName()).build())
                .mileage(List.of())
                .build()
            );
        }

        if (!driversWithoutDispatchersMileageDataResponses.isEmpty()) {
            responses.add(GetDriverMileageResponse.builder()
                .dispatcher(null)
                .driverMileageDataList(driversWithoutDispatchersMileageDataResponses)
                .build());
        }

        return responses;
    }

    public List<DriverMileageDto> getDriverMileageDtos(Kpiable targetEntity, LocalDate startDate, LocalDate endDate) {
        List<DriverMileageEntity> driverMileageEntities = criteriaService.getMileageForTargetEntity(
            targetEntity,
            startDate,
            endDate
        );
        return driverMileageMapper.fromDriverMileageEntitiesToDriverMileageDtos(driverMileageEntities);
    }

    private DriverMileageEntity getCurrentWeekDriverMileageEntity(
        UpsertDriverMileageRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        if (request.driverMileageUuid() != null) {
            var driverMileageEntity = driverMileageService.getByUuidAndCompanyUuid(request.driverMileageUuid());
            DriverEntity currentDriver = driverMileageEntity.getDriver();
            DriverEntity finalDriver = driver == null
                ? driverMileageEntity.getDriver()
                : !driver.getUuid().equals(currentDriver.getUuid()) ? driver : currentDriver;
            if (request.mileageDate() != null) {
                updateMileageData(driverMileageEntity.getMileageData(), request);
            }

            return driverMileageEntity.toBuilder()
                .driver(finalDriver)
                .build();
        }

        return DriverMileageEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .company(company)
            .driver(driver)
            .dispatcher(dispatcher)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .mileageData(createMileageDataMap(request))
            .build();
    }

    private DriverMileageEntity getPreviousWeekDriverMileageEntity(
        UpsertDriverMileageRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        LocalDate startDate = request.startDate().minusWeeks(1);
        LocalDate endDate = request.endDate().minusWeeks(1);
        return getDriverMileageEntity(startDate, endDate, request, company, dispatcher, driver);
    }

    private DriverMileageEntity getNextWeekDriverMileageEntity(
        UpsertDriverMileageRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        LocalDate startDate = request.startDate().plusWeeks(1);
        LocalDate endDate = request.endDate().plusWeeks(1);
        return getDriverMileageEntity(startDate, endDate, request, company, dispatcher, driver);
    }

    private DriverMileageEntity getDriverMileageEntity(
        LocalDate startDate,
        LocalDate endDate,
        UpsertDriverMileageRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        DriverMileageEntity nextWeekDriverMileage = driverMileageService.getDriversMileageForTimeframe(
            request.companyUuid(),
            request.dispatcherUuid(),
            request.driverUuid(),
            startDate,
            endDate
        ).orElse(null);

        if (nextWeekDriverMileage != null) {
            updateMileageData(nextWeekDriverMileage.getMileageData(), request);
            DriverEntity finalDriver = driver == null ? nextWeekDriverMileage.getDriver() : driver;
            return nextWeekDriverMileage.toBuilder().driver(finalDriver).build();
        }
        return DriverMileageEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .driver(driver)
            .dispatcher(dispatcher)
            .company(company)
            .mileageData(createMileageDataMap(request))
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    private void updateMileageData(Map<String, MileageData> previousMileageData, UpsertDriverMileageRequest request) {
        MileageData data = previousMileageData.get(request.mileageDate().toString());
        if (data != null) {
            if (!BigDecimalUtils.isEmpty(request.miles())) {
                data.setMiles(request.miles());
            }

            if (!BigDecimalUtils.isEmpty(request.revenue())) {
                data.setRevenue(request.revenue());
            }

            if (!StringUtil.isNullOrEmpty(request.broker())) {
                data.setBroker(request.broker());
            }
        } else {
            MileageData newDatum = createMileageData(request);
            previousMileageData.put(request.mileageDate().toString(), newDatum);
        }
    }

    private Map<String, MileageData> createMileageDataMap(UpsertDriverMileageRequest request) {
        Map<String, MileageData> mileageData = new HashMap<>();
        MileageData data = createMileageData(request);
        mileageData.put(request.mileageDate().toString(), data);
        return mileageData;
    }

    private MileageData createMileageData(UpsertDriverMileageRequest request) {
        return MileageData.builder()
            .revenue(request.revenue())
            .miles(request.miles())
            .broker(request.broker())
            .build();
    }

    private DispatcherEntity getDispatcher(String dispatcherUuid) {
        return StringUtil.isNullOrEmpty(dispatcherUuid) ? null : dispatcherService.getByUuid(dispatcherUuid);
    }

    private DriverEntity getDriver(String driverUuid) {
        return StringUtil.isNullOrEmpty(driverUuid) ? null : driverService.getByUuid(driverUuid);
    }
}
