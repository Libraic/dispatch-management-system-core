package io.kovin.dispatch.management.system.facade;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.START_DATE_BEFORE_END_DATE;

import ch.qos.logback.core.util.StringUtil;
import java.math.BigDecimal;
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
     * next/previous week. The mileage entities are then persisted.
     *
     * @param request the request object containing details for upserting the driver mileage.
     *                Includes fields such as companyUuid, dispatcherUuid, driverUuid,
     *                startDate, endDate, mileageDate, revenue, miles, and broker.
     * @return an {@link UpsertDriverMileageResponse} containing the UUID of the upserted driver mileage record.
     */
    @Transactional
    public UpsertDriverMileageResponse upsertMileage(UpsertDriverMileageRequest request) {
        driverMileageValidationService.validateDriversMileageUpsertion(request);

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        DispatcherEntity dispatcher = StringUtil.isNullOrEmpty(request.dispatcherUuid())
            ? null
            : dispatcherService.getByUuid(request.dispatcherUuid());
        DriverEntity driver = StringUtil.isNullOrEmpty(request.driverUuid())
            ? null
            : driverService.getByUuid(request.driverUuid());

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
            .mileage(driverMileageMapper.fromDriverMileageEntityToGetMileageResponse(driverMileageEntity))
            .build();
    }

    /**
     * Retrieves driver mileage data for all drivers in a specified company within a given timeframe.
     * The data is grouped by dispatcher and includes detailed mileage information for each driver.
     *
     * @param companyUuid the unique identifier of the company for which to retrieve driver mileage data.
     * @param startDate   the start date of the timeframe for which mileage data is to be retrieved.
     * @param endDate     the end date of the timeframe for which mileage data is to be retrieved.
     * @return a list of {@link GetDriverMileageResponse}, where each response contains a dispatcher and
     * the corresponding list of driver mileage data grouped by dispatcher and ordered by creation date.
     */
    public List<GetDriverMileageResponse> getDriversMileageForTimeframe(
        String companyUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        companyService.validateTheCompanyIsRegistered(companyUuid);
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
                List<GetMileageResponse> getMileageResponses = driverMileageMapper.fromDriverMileageEntityToGetMileageResponse(
                    driverMileageEntityOptional.orElse(null)
                );
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

        // The Drivers who are not yet assigned to a Dispatcher
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
            if (request.mileageDate() != null) {
                updateMileageDatum(driverMileageEntity.getMileageData(), request);
            }

            return driverMileageEntity;
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
            updateMileageDatum(nextWeekDriverMileage.getMileageData(), request);
            return nextWeekDriverMileage;
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

    private void updateMileageDatum(Map<String, MileageData> previousMileageData, UpsertDriverMileageRequest request) {
        MileageData previousMileageDatum = previousMileageData.get(request.mileageDate().toString());
        if (previousMileageDatum != null) {
            // If the Pickup/Delivery dates were changed, we should remove the Mileages previously created
            // for this time frame and create a new one.
            if (hasPickUpDateOrDeliveryDateChanged(request, previousMileageDatum)) {
                LocalDate iterator = request.pickUpDate();
                while (!iterator.isAfter(previousMileageDatum.getDeliveryDate())) {
                    previousMileageData.remove(iterator.toString());
                    iterator = iterator.plusDays(1);
                }
            }

            Map<String, MileageData> currentMileageData = createMileageDataMap(request);
            previousMileageData.putAll(currentMileageData);
            MileageData currentMileageDatum = currentMileageData.get(request.toString());
            updateMileageDatum(request, previousMileageDatum, currentMileageDatum);
        } else {
            Map<String, MileageData> newMileageData = createMileageDataMap(request);
            previousMileageData.putAll(newMileageData);
        }
    }

    private Map<String, MileageData> createMileageDataMap(UpsertDriverMileageRequest request) {
        Map<String, MileageData> mileageData = new HashMap<>();
        LocalDate pickUpDate = request.pickUpDate();
        LocalDate deliveryDate = request.deliveryDate();

        // Create the Mileage for the first day (the loading day), having the 'Covered' status
        mileageData.put(pickUpDate.toString(), driverMileageMapper.createCoveredMileageDatum(request));

        // Create the Mileage data for the next days prior to the last one, having the 'Transit' status
        LocalDate nextDay = pickUpDate.plusDays(1);
        while (nextDay.isBefore(deliveryDate)) {
            mileageData.put(nextDay.toString(), driverMileageMapper.createTransitMileageDatum());
            nextDay = nextDay.plusDays(1);
        }

        // Create the Mileage data for the last day, having the 'Empty' status
        mileageData.put(
            deliveryDate.toString(),
            driverMileageMapper.createEmptyMileageDatum(deliveryDate, request.deliveryLocation())
        );

        return mileageData;
    }

    private boolean hasPickUpDateOrDeliveryDateChanged(UpsertDriverMileageRequest request, MileageData mileageData) {
        LocalDate newPickUpDate = request.pickUpDate();
        LocalDate newDeliveryDate = request.deliveryDate();
        return (newPickUpDate != null && !newPickUpDate.equals(mileageData.getPickUpDate()))
            || (newDeliveryDate != null && !newDeliveryDate.equals(mileageData.getDeliveryDate()));
    }

    private void updateMileageDatum(
        UpsertDriverMileageRequest request,
        MileageData previousMileageData,
        MileageData currentMileageData
    ) {
        // By using a generic method that created the Mileage Data based on the request, some fields may be null
        // (because they are basically not updated). Therefore, we should take the previous values and populate
        // the object
        BigDecimal miles = !BigDecimalUtils.isEmpty(request.miles()) ? request.miles() : previousMileageData.getMiles();
        BigDecimal revenue = !BigDecimalUtils.isEmpty(request.revenue()) ? request.revenue() : previousMileageData.getRevenue();
        String broker = !StringUtil.isNullOrEmpty(request.broker()) ? request.broker() : previousMileageData.getBroker();
        String representative = !StringUtil.isNullOrEmpty(request.representative()) ? request.representative() : previousMileageData.getRepresentative();
        String deliveryLocation = !StringUtil.isNullOrEmpty(request.deliveryLocation()) ? request.deliveryLocation() : previousMileageData.getDeliveryLocation();
        String pickUpLocation = !StringUtil.isNullOrEmpty(request.pickUpLocation()) ? request.pickUpLocation() : previousMileageData.getPickUpLocation();

        currentMileageData.setMiles(miles);
        currentMileageData.setRevenue(revenue);
        currentMileageData.setBroker(broker);
        currentMileageData.setRepresentative(representative);
        currentMileageData.setDeliveryLocation(deliveryLocation);
        currentMileageData.setPickUpLocation(pickUpLocation);
    }
}
