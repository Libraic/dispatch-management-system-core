package io.kovin.dispatch.management.system.facade;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.START_DATE_BEFORE_END_DATE;

import ch.qos.logback.core.util.StringUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.mapper.DriverMileageObjectsCreator;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.DispatcherEntity;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.model.entity.Kpiable;
import io.kovin.dispatch.management.system.model.entity.LocationData;
import io.kovin.dispatch.management.system.model.entity.MileageData;
import io.kovin.dispatch.management.system.model.entity.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.entity.enums.LocationType;
import io.kovin.dispatch.management.system.model.internal.Pair;
import io.kovin.dispatch.management.system.model.internal.mileage.DriverMileageDto;
import io.kovin.dispatch.management.system.model.request.CreateMileageLocationRequest;
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
import io.kovin.dispatch.management.system.utils.CollectionUtils;
import io.kovin.dispatch.management.system.utils.LocalDateUtils;
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

    private final DriverMileageObjectsCreator driverMileageObjectsCreator;

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
            .mileage(driverMileageObjectsCreator.fromDriverMileageEntityToGetMileageResponse(driverMileageEntity))
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
                Optional<DriverMileageEntity> driverMileageEntityOptional = driverMileageService.getDriversMileageForTimeframe(
                    companyUuid,
                    dispatcherUuid,
                    driver.getUuid(),
                    startDate,
                    endDate
                );
                var getDriverMileageDataResponse = driverMileageObjectsCreator.createGetDriverMileageDataResponse(
                    driverMileageEntityOptional.orElse(null),
                    driver
                );
                getDriverMileageDataResponses.add(getDriverMileageDataResponse);
            }
            GetDriverMileageResponse getDriverMileageResponse = GetDriverMileageResponse.builder()
                .dispatcher(getDispatcherResponse)
                .driverMileageDataList(getDriverMileageDataResponses)
                .build();
            responses.add(getDriverMileageResponse);
        }

        List<DriverEntity> driversWithoutDispatchers = driverService.getDriversWithoutDispatchersByCompanyUuid(companyUuid);
        List<GetDriverMileageDataResponse> driversWithoutDispatchersMileageDataResponses = driverMileageObjectsCreator.createGetDriverMileageDataResponses(
            driversWithoutDispatchers
        );

        // The Drivers who are not yet assigned to a Dispatcher
        if (!driversWithoutDispatchersMileageDataResponses.isEmpty()) {
            responses.add(GetDriverMileageResponse.builder()
                .dispatcher(null)
                .driverMileageDataList(driversWithoutDispatchersMileageDataResponses)
                .build());
        }

        return responses;
    }

    /**
     * Retrieves mileage data for a specific driver based on the provided UUID.
     * If no driver mileage entity is found with the given UUID, an empty list is returned.
     * Otherwise, the mileage data is mapped into a list of {@link GetMileageResponse} objects.
     *
     * @param driverMileageUuid the unique identifier of the driver mileage entity to retrieve.
     * @return a list of {@link GetMileageResponse} objects containing the mileage data.
     * Returns an empty list if no data is found for the given UUID.
     */
    public List<GetMileageResponse> getMileageData(String driverMileageUuid) {
        Optional<DriverMileageEntity> driverMileageEntityOptional = driverMileageService.findByUuid(driverMileageUuid);
        if (driverMileageEntityOptional.isEmpty()) {
            return List.of();
        }

        return driverMileageObjectsCreator.fromDriverMileageEntityToGetMileageResponse(driverMileageEntityOptional.get());
    }

    /**
     * Deletes mileage data for a specific driver within the given time frame.
     * This method identifies and updates driver mileage entities corresponding to the
     * current, previous, and next weeks affected by the removal of mileage data.
     * The updated entities are then persisted.
     *
     * @param driverMileageUuid the unique identifier of the driver mileage entity to be updated.
     */
    @Transactional
    public void deleteDriverMileage(String driverMileageUuid, String idAcrossTimeframe) {
        Optional<DriverMileageEntity> driverMileageEntityOptional = driverMileageService.findByUuid(driverMileageUuid);
        if (driverMileageEntityOptional.isEmpty()) {
            return;
        }

        DriverMileageEntity currentWeekDriverMileage = driverMileageEntityOptional.get();
        String companyUuid = currentWeekDriverMileage.getCompany().getUuid();
        String dispatcherUuid = currentWeekDriverMileage.getDispatcher().getUuid();
        String driverUuid = currentWeekDriverMileage.getDriver().getUuid();
        LocalDate weekStart = currentWeekDriverMileage.getStartDate();
        LocalDate weekEnd = currentWeekDriverMileage.getEndDate();

        List<DriverMileageEntity> driverMileageEntities = new ArrayList<>(3);
        Pair<LocalDate, LocalDate> startAndEndDatesOfTimeframe = driverMileageService.getStartAndLastDateOfTimeframe(
            driverMileageUuid,
            idAcrossTimeframe
        );
        if (startAndEndDatesOfTimeframe == null) {
            return;
        }

        LocalDate timeframeStart = startAndEndDatesOfTimeframe.left();
        LocalDate timeframeEnd = startAndEndDatesOfTimeframe.right();
        DriverMileageEntity currentRemoval = removeMileageDataBetweenDates(currentWeekDriverMileage, timeframeStart, timeframeEnd);
        CollectionUtils.addIfNotEmpty(driverMileageEntities, currentRemoval);

        driverMileageService.getDriversMileageForTimeframe(
            companyUuid,
            dispatcherUuid,
            driverUuid,
            LocalDateUtils.addWeek(weekStart),
            LocalDateUtils.addWeek(weekEnd)
        ).ifPresent(nextWeekDriverMileage -> {
            DriverMileageEntity afterMileageDataRemoval = removeMileageDataBetweenDates(
                nextWeekDriverMileage,
                timeframeStart,
                timeframeEnd
            );
            CollectionUtils.addIfNotEmpty(driverMileageEntities, afterMileageDataRemoval);
        });

        driverMileageService.getDriversMileageForTimeframe(
            companyUuid,
            dispatcherUuid,
            driverUuid,
            LocalDateUtils.subtractWeek(weekStart),
            LocalDateUtils.subtractWeek(weekEnd)
        ).ifPresent(previousWeekMileage -> {
            DriverMileageEntity afterMileageDataRemoval = removeMileageDataBetweenDates(
                previousWeekMileage,
                timeframeStart,
                timeframeEnd
            );
            CollectionUtils.addIfNotEmpty(driverMileageEntities, afterMileageDataRemoval);
        });

        driverMileageService.saveAllDriverMileageEntities(driverMileageEntities);
    }

    public List<DriverMileageDto> getDriverMileageDtos(Kpiable targetEntity, LocalDate startDate, LocalDate endDate) {
        List<DriverMileageEntity> driverMileageEntities = criteriaService.getMileageForTargetEntity(
            targetEntity,
            startDate,
            endDate
        );
        return driverMileageObjectsCreator.fromDriverMileageEntitiesToDriverMileageDtos(driverMileageEntities);
    }

    private DriverMileageEntity removeMileageDataBetweenDates(
        DriverMileageEntity driverMileageEntity,
        LocalDate startDate,
        LocalDate endDate
    ) {
        Map<String, MileageData> newMileageData = new HashMap<>();
        for (Map.Entry<String, MileageData> entry : driverMileageEntity.getMileageData().entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey());
            MileageData mileageData = entry.getValue();
            boolean isOutsideRange = date.isBefore(startDate) || date.isAfter(endDate);
            if (isOutsideRange) {
                newMileageData.put(date.toString(), mileageData);
            } else if (mileageData.getPreviousLoadStatus() == LoadStatus.EMPTY) {
                String previousIdAcrossTimeframe = driverMileageService.getIdAcrossTimeframe(driverMileageEntity, date.minusDays(1));
                if (previousIdAcrossTimeframe == null) {
                    continue;
                }

                mileageData.setPreviousLoadStatus(null);
                mileageData.setCurrentLoadStatus(LoadStatus.EMPTY);
                mileageData.setIdAcrossTimeframe(previousIdAcrossTimeframe);
                mileageData.setRevenue(BigDecimal.ZERO);
                mileageData.setMiles(BigDecimal.ZERO);
                mileageData.setBroker(null);
                mileageData.setRepresentative(null);
                mileageData.setRepresentativeContactNumber(null);
                newMileageData.put(date.toString(), mileageData);
            }
        }

        if (newMileageData.isEmpty()) {
            driverMileageService.deleteDriverMileage(driverMileageEntity);
            return null;
        }

        driverMileageEntity.setMileageData(newMileageData);
        return driverMileageEntity;
    }

    private DriverMileageEntity getCurrentWeekDriverMileageEntity(
        UpsertDriverMileageRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        if (request.driverMileageUuid() != null) {
            var driverMileageEntity = driverMileageService.getByUuid(request.driverMileageUuid());
            if (request.locations() != null && !request.locations().isEmpty()) {
                Map<String, MileageData> mergedMileageData = mergeMileageData(request, driverMileageEntity);
                driverMileageEntity.setMileageData(mergedMileageData);
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
        LocalDate startDate = LocalDateUtils.subtractWeek(request.startDate());
        LocalDate endDate = LocalDateUtils.subtractWeek(request.endDate());
        return getDriverMileageEntity(startDate, endDate, request, company, dispatcher, driver);
    }

    private DriverMileageEntity getNextWeekDriverMileageEntity(
        UpsertDriverMileageRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        LocalDate startDate = LocalDateUtils.addWeek(request.startDate());
        LocalDate endDate = LocalDateUtils.addWeek(request.endDate());
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
        DriverMileageEntity driverMileageEntity = driverMileageService.getDriversMileageForTimeframe(
            request.companyUuid(),
            request.dispatcherUuid(),
            request.driverUuid(),
            startDate,
            endDate
        ).orElse(null);

        if (driverMileageEntity != null && request.locations() != null && !request.locations().isEmpty()) {
            Map<String, MileageData> mergedMileageData = mergeMileageData(request, driverMileageEntity);
            driverMileageEntity.setMileageData(mergedMileageData);
            return driverMileageEntity;
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

    private Map<String, MileageData> createMileageDataMap(UpsertDriverMileageRequest request) {
        Map<String, MileageData> mileageData = new HashMap<>();
        List<CreateMileageLocationRequest> locations = request.locations()
            .stream()
            .sorted(Comparator.comparing(CreateMileageLocationRequest::order))
            .toList();
        String idAcrossTimeframe = UUID.randomUUID().toString();

        addCoveredMileageData(mileageData, request, locations, idAcrossTimeframe);
        addEmptyMileageData(mileageData, locations, idAcrossTimeframe);
        addTransitMileageData(
            mileageData,
            locations,
            request.locations().getFirst().date(),
            request.locations().getLast().date(),
            idAcrossTimeframe
        );

        return mileageData;
    }

    /**
     * Merges mileage data from the provided request with the existing mileage data
     * in the given Driver Mileage entity. If there is a data conflict for a specific date
     * and the previous mileage data has a load status of EMPTY, the new data will
     * inherit this load status as its previous load status. Any new dates in the request
     * data will be added, and existing dates not present in the request will be retained.
     *
     * @param request the request object containing the new mileage data to merge. Includes
     *                details such as mileageDate, revenue, miles, broker, and locations.
     * @param driverMileageEntity the entity containing the existing mileage data to merge with.
     *                            The data is contained in the mileageData map field.
     * @return a map containing the merged mileage data. The keys are dates in string format,
     *         and the values are MileageData objects representing the mileage details for each date.
     */
    private Map<String, MileageData> mergeMileageData(
        UpsertDriverMileageRequest request,
        DriverMileageEntity driverMileageEntity
    ) {
        Map<String, MileageData> previousMileageData = driverMileageEntity.getMileageData();
        Map<String, MileageData> newMileageData = createMileageDataMap(request);
        Map<String, MileageData> mergedMileageData = new HashMap<>();
        for (Map.Entry<String, MileageData> entry : newMileageData.entrySet()) {
            String date = entry.getKey();
            MileageData newMileageDatum = entry.getValue();
            MileageData previousMileageDatum = previousMileageData.get(date);
            if (previousMileageDatum != null && previousMileageDatum.getCurrentLoadStatus() == LoadStatus.EMPTY) {
                newMileageDatum.setPreviousLoadStatus(LoadStatus.EMPTY);
            }

            mergedMileageData.put(date, newMileageDatum);
        }

        previousMileageData.forEach(mergedMileageData::putIfAbsent);
        return mergedMileageData;
    }

    private void addCoveredMileageData(
        Map<String, MileageData> mileageDataMap,
        UpsertDriverMileageRequest request,
        List<CreateMileageLocationRequest> locations,
        String idAcrossTimeframe
    ) {
        // Mileage Data for the Loading Day
        LocalDate startDate = locations.getFirst().date();
        MileageData coveredMileageDatum = driverMileageObjectsCreator.createCoveredMileageDatum(request, idAcrossTimeframe);
        List<LocationData> loadingDayLocations = new ArrayList<>();
        int order = 0;
        for (CreateMileageLocationRequest createMileageLocationRequest : locations) {
            if (createMileageLocationRequest.date().equals(startDate)) {
                LocationData secondLocationData = driverMileageObjectsCreator.fromCreateMileageLocationRequestToLocationData(
                    createMileageLocationRequest,
                    order++
                );
                loadingDayLocations.add(secondLocationData);
            }
        }

        coveredMileageDatum.setLocations(loadingDayLocations);
        mileageDataMap.put(startDate.toString(), coveredMileageDatum);
    }

    private void addEmptyMileageData(
        Map<String, MileageData> mileageDataMap,
        List<CreateMileageLocationRequest> locations,
        String idAcrossTimeframe
    ) {
        // Mileage Data for the last Delivery Day
        LocalDate endDate = locations.getLast().date();
        MileageData emptyMileageDatum = driverMileageObjectsCreator.createEmptyMileageDatum(idAcrossTimeframe);
        List<LocationData> lastDayLocations = new ArrayList<>();
        int order = 0;
        for (CreateMileageLocationRequest createMileageLocationRequest : locations) {
            if (createMileageLocationRequest.date().equals(endDate)) {
                LocationData secondLocationData = driverMileageObjectsCreator.fromCreateMileageLocationRequestToLocationData(
                    createMileageLocationRequest,
                    order++
                );
                lastDayLocations.add(secondLocationData);
            }
        }

        // The last location from the load will be the starting point for the next load (in the majority of cases), so
        // we default that for a better UX. In case it is not, the user will change it. Even if we did not populate with
        // anything, the user would still need to introduce the location. Therefore, by defaulting to a value, we have
        // a change to minimize the interaction.
        lastDayLocations.getLast().setLocationType(LocationType.STARTING_POINT);

        // We add an implicit Pickup point (which will be defaulted to have the location of the Starting Point).
        // We add a Delivery location as well.
        // These are added because any load will require a Pickup/Delivery point, so we want to expect this and
        // minimize the interaction with the system.
        lastDayLocations.add(LocationData.builder()
            .locationType(LocationType.PICK_UP)
            .date(lastDayLocations.getLast().getDate())
            .location(lastDayLocations.getLast().getLocation())
            .order(order)
            .build()
        );
        lastDayLocations.add(LocationData.builder()
            .locationType(LocationType.DELIVERY)
            .date(LocalDateUtils.addDay(lastDayLocations.getLast().getDate()))
            .order(order + 1)
            .build()
        );

        emptyMileageDatum.setLocations(lastDayLocations);
        mileageDataMap.put(endDate.toString(), emptyMileageDatum);
    }

    private void addTransitMileageData(
        Map<String, MileageData> mileageDataMap,
        List<CreateMileageLocationRequest> locations,
        LocalDate startDate,
        LocalDate endDate,
        String idAcrossTimeframe
    ) {
        // Create the Mileage data for the next days prior to the last one, having the 'Transit' status
        Map<LocalDate, List<CreateMileageLocationRequest>> transitMileageData = locations.stream()
            .collect(Collectors.groupingBy(CreateMileageLocationRequest::date, Collectors.toList()));
        LocalDate current = LocalDateUtils.addDay(startDate);
        int order = 0;
        while (current.isBefore(endDate)) {
            MileageData transitMileageDatum = driverMileageObjectsCreator.createTransitMileageDatum(idAcrossTimeframe);
            List<LocationData> transitLocations = new ArrayList<>();
            for (CreateMileageLocationRequest location : transitMileageData.getOrDefault(current, new ArrayList<>())) {
                transitLocations.add(driverMileageObjectsCreator.fromCreateMileageLocationRequestToLocationData(location, order++));
            }
            transitMileageDatum.setLocations(transitLocations);
            mileageDataMap.put(current.toString(), transitMileageDatum);
            current = LocalDateUtils.addDay(current);
        }
    }
}
