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
import io.kovin.dispatch.management.system.mapper.LoadObjectsCreator;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import io.kovin.dispatch.management.system.model.persistence.jsonb.LocationData;
import io.kovin.dispatch.management.system.model.persistence.jsonb.LoadData;
import io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.persistence.enums.LocationType;
import io.kovin.dispatch.management.system.model.internal.Pair;
import io.kovin.dispatch.management.system.model.request.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import io.kovin.dispatch.management.system.model.response.load.GetDriverLoadsResponse;
import io.kovin.dispatch.management.system.model.response.load.GetDriverLoadsDataResponse;
import io.kovin.dispatch.management.system.model.response.load.GetLoadResponse;
import io.kovin.dispatch.management.system.model.response.load.UpsertDriverLoadsResponse;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.DispatcherService;
import io.kovin.dispatch.management.system.service.DriverService;
import io.kovin.dispatch.management.system.service.LoadService;
import io.kovin.dispatch.management.system.utils.CollectionUtils;
import io.kovin.dispatch.management.system.utils.LocalDateUtils;
import io.kovin.dispatch.management.system.validation.LoadValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadFacade {

    private final LoadValidationService loadValidationService;

    private final CompanyService companyService;
    private final DriverService driverService;
    private final LoadService loadService;
    private final CriteriaService criteriaService;
    private final DispatcherService dispatcherService;

    private final LoadObjectsCreator loadObjectsCreator;

    /**
     * Inserts or updates a load entity based on the provided request. This method validates
     * the input data, retrieves associated company, dispatcher, and driver entities, and
     * updates load data for the current week as well as the previous or next week if applicable.
     * It then saves the resulting load entities in the database.
     *
     * @param request the {@link UpsertLoadRequest} containing the load details to be upserted,
     *                including information such as company UUID, dispatcher UUID, driver UUID,
     *                start date, end date, load date, revenue, miles, broker, representative,
     *                and load locations.
     * @return an {@link UpsertDriverLoadsResponse} containing the UUID of the updated or
     *         newly created load and a list of {@link GetLoadResponse} objects representing
     *         the detailed load information.
     */
    @Transactional
    public UpsertDriverLoadsResponse upsertLoad(UpsertLoadRequest request) {
        loadValidationService.validateLoadUpsertion(request);

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        DispatcherEntity dispatcher = StringUtil.isNullOrEmpty(request.dispatcherUuid())
            ? null
            : dispatcherService.getByUuid(request.dispatcherUuid());
        DriverEntity driver = StringUtil.isNullOrEmpty(request.driverUuid())
            ? null
            : driverService.getByUuid(request.driverUuid());

        List<LoadEntity> loadEntities = new ArrayList<>(2);
        LoadEntity loadEntity = getCurrentWeekLoadEntity(
            request,
            company,
            dispatcher,
            driver
        );
        loadEntities.add(loadEntity);
        if (request.loadDate() != null) {
            LoadEntity commonWeekEntity = request.loadDate().isAfter(request.endDate())
                ? getNextWeekLoadEntity(request, company, dispatcher, driver)
                : getPreviousWeekLoadEntity(request, company, dispatcher, driver);
            loadEntities.add(commonWeekEntity);
        }

        loadService.saveLoadEntities(loadEntities);

        return UpsertDriverLoadsResponse.builder()
            .loadUuid(loadEntity.getUuid())
            .loads(loadObjectsCreator.fromLoadEntityToGetLoadResponse(loadEntity))
            .build();
    }

    /**
     * Retrieves a list of driver loads and their corresponding dispatcher information
     * within a specified time frame for a given company. If any drivers are not assigned
     * to a dispatcher, they are also included in the response.
     *
     * @param companyUuid the unique identifier of the company for which loads data is to be retrieved.
     * @param startDate the start date of the time frame for which loads data is requested.
     * @param endDate the end date of the time frame for which loads data is requested.
     * @return a list of {@link GetDriverLoadsDataResponse} objects containing dispatcher
     *         and driver loads information within the specified time frame.
     * @throws DispatchManagementSystemException if the start date is after the end date
     *         or if the company is not registered.
     */
    public List<GetDriverLoadsDataResponse> getDriverLoadsForTimeframe(
        String companyUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        companyService.validateTheCompanyIsRegistered(companyUuid);
        if (startDate.isAfter(endDate)) {
            throw DispatchManagementSystemException.of(START_DATE_BEFORE_END_DATE, HttpStatus.BAD_REQUEST);
        }

        List<GetDriverLoadsDataResponse> responses = new ArrayList<>();
        List<DispatcherEntity> dispatchers = dispatcherService.getAllDispatchersWithDriversByCompany(companyUuid);
        for (DispatcherEntity dispatcher : dispatchers) {
            String dispatcherUuid = dispatcher.getUuid();
            GetDispatcherResponse getDispatcherResponse = GetDispatcherResponse.builder()
                .uuid(dispatcherUuid)
                .name(dispatcher.getName())
                .phoneNumber(dispatcher.getPhoneNumber())
                .build();
            List<GetDriverLoadsResponse> getDriverLoadResponses = new ArrayList<>();
            for (DriverEntity driver : dispatcher.getDrivers()) {
                Optional<LoadEntity> loadEntityOptional = loadService.getLoadsForTimeframe(
                    companyUuid,
                    dispatcherUuid,
                    driver.getUuid(),
                    startDate,
                    endDate
                );
                var getDriverLoadsResponses = loadObjectsCreator.createGetDriverLoadsResponse(
                    loadEntityOptional.orElse(null),
                    driver
                );
                getDriverLoadResponses.add(getDriverLoadsResponses);
            }
            GetDriverLoadsDataResponse getDriverLoadsDataResponse = GetDriverLoadsDataResponse.builder()
                .dispatcher(getDispatcherResponse)
                .driverLoads(getDriverLoadResponses)
                .build();
            responses.add(getDriverLoadsDataResponse);
        }

        List<DriverEntity> driversWithoutDispatchers = driverService.getDriversWithoutDispatchersByCompanyUuid(companyUuid);
        List<GetDriverLoadsResponse> driversWithoutDispatchersLoadsResponses = loadObjectsCreator.createGetDriverLoadsResponses(
            driversWithoutDispatchers
        );

        // The Drivers who are not yet assigned to a Dispatcher
        if (!driversWithoutDispatchersLoadsResponses.isEmpty()) {
            responses.add(GetDriverLoadsDataResponse.builder()
                .dispatcher(null)
                .driverLoads(driversWithoutDispatchersLoadsResponses)
                .build());
        }

        return responses;
    }

    /**
     * Retrieves a list of detailed load responses associated with the given unique load identifier.
     * If the load entity corresponding to the provided UUID is not found, an empty list is returned.
     *
     * @param loadUuid the unique identifier of the load whose details are to be retrieved.
     * @return a list of {@code GetLoadResponse} objects representing the detailed load responses.
     *         If the load UUID does not match any existing records, the returned list is empty.
     */
    public List<GetLoadResponse> getLoadResponses(String loadUuid) {
        Optional<LoadEntity> loadEntityOptional = loadService.findByUuid(loadUuid);
        if (loadEntityOptional.isEmpty()) {
            return List.of();
        }

        return loadObjectsCreator.fromLoadEntityToGetLoadResponse(loadEntityOptional.get());
    }


    /**
     * Deletes a load entity and its associated data within a specified timeframe.
     * The method identifies the load based on its unique UUID and removes load data
     * for the current week as well as, if applicable, for the previous or next week.
     *
     * @param loadUuid the unique identifier of the load to be deleted.
     * @param idAcrossTimeframe an identifier representing the timeframe across which
     *                          the load data should be removed.
     */
    @Transactional
    public void deleteLoad(String loadUuid, String idAcrossTimeframe) {
        Optional<LoadEntity> loadEntityOptional = loadService.findByUuid(loadUuid);
        if (loadEntityOptional.isEmpty()) {
            return;
        }

        LoadEntity currentWeekLoad = loadEntityOptional.get();
        String companyUuid = currentWeekLoad.getCompany().getUuid();
        String dispatcherUuid = currentWeekLoad.getDispatcher().getUuid();
        String driverUuid = currentWeekLoad.getDriver().getUuid();
        LocalDate weekStart = currentWeekLoad.getStartDate();
        LocalDate weekEnd = currentWeekLoad.getEndDate();

        List<LoadEntity> loadEntities = new ArrayList<>(3);
        Pair<LocalDate, LocalDate> startAndEndDatesOfTimeframe = loadService.getStartAndLastDateOfTimeframe(
            loadUuid,
            idAcrossTimeframe
        );
        if (startAndEndDatesOfTimeframe == null) {
            return;
        }

        LocalDate timeframeStart = startAndEndDatesOfTimeframe.left();
        LocalDate timeframeEnd = startAndEndDatesOfTimeframe.right();
        LoadEntity currentRemoval = removeLoadDataBetweenDates(currentWeekLoad, timeframeStart, timeframeEnd);
        CollectionUtils.addIfNotEmpty(loadEntities, currentRemoval);

        loadService.getLoadsForTimeframe(
            companyUuid,
            dispatcherUuid,
            driverUuid,
            LocalDateUtils.addWeek(weekStart),
            LocalDateUtils.addWeek(weekEnd)
        ).ifPresent(nextWeekLoad -> {
            LoadEntity afterLoadDataRemoval = removeLoadDataBetweenDates(
                nextWeekLoad,
                timeframeStart,
                timeframeEnd
            );
            CollectionUtils.addIfNotEmpty(loadEntities, afterLoadDataRemoval);
        });

        loadService.getLoadsForTimeframe(
            companyUuid,
            dispatcherUuid,
            driverUuid,
            LocalDateUtils.subtractWeek(weekStart),
            LocalDateUtils.subtractWeek(weekEnd)
        ).ifPresent(previousWeekLoad -> {
            LoadEntity afterLoadDataRemoval = removeLoadDataBetweenDates(
                previousWeekLoad,
                timeframeStart,
                timeframeEnd
            );
            CollectionUtils.addIfNotEmpty(loadEntities, afterLoadDataRemoval);
        });

        loadService.saveLoadEntities(loadEntities);
    }

    private LoadEntity removeLoadDataBetweenDates(
        LoadEntity loadEntity,
        LocalDate startDate,
        LocalDate endDate
    ) {
        Map<String, LoadData> newLoadData = new HashMap<>();
        for (Map.Entry<String, LoadData> entry : loadEntity.getLoadData().entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey());
            LoadData loadData = entry.getValue();
            boolean isOutsideRange = date.isBefore(startDate) || date.isAfter(endDate);
            if (isOutsideRange) {
                newLoadData.put(date.toString(), loadData);
            } else if (loadData.getPreviousLoadStatus() == LoadStatus.EMPTY) {
                String previousIdAcrossTimeframe = loadService.getIdAcrossTimeframe(loadEntity, date.minusDays(1));
                if (previousIdAcrossTimeframe == null) {
                    continue;
                }

                loadData.setPreviousLoadStatus(null);
                loadData.setCurrentLoadStatus(LoadStatus.EMPTY);
                loadData.setIdAcrossTimeframe(previousIdAcrossTimeframe);
                loadData.setRevenue(BigDecimal.ZERO);
                loadData.setMiles(BigDecimal.ZERO);
                loadData.setBroker(null);
                loadData.setRepresentative(null);
                loadData.setRepresentativeContactNumber(null);
                newLoadData.put(date.toString(), loadData);
            }
        }

        if (newLoadData.isEmpty()) {
            loadService.deleteLoad(loadEntity);
            return null;
        }

        loadEntity.setLoadData(newLoadData);
        return loadEntity;
    }

    private LoadEntity getCurrentWeekLoadEntity(
        UpsertLoadRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        if (request.loadUuid() != null) {
            var loadEntity = loadService.getByUuid(request.loadUuid());
            if (request.locations() != null && !request.locations().isEmpty()) {
                Map<String, LoadData> mergedLoadData = mergeLoadData(request, loadEntity);
                loadEntity.setLoadData(mergedLoadData);
            }
            return loadEntity;
        }

        return LoadEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .company(company)
            .driver(driver)
            .dispatcher(dispatcher)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .loadData(createLoadDataMap(request))
            .build();
    }

    private LoadEntity getPreviousWeekLoadEntity(
        UpsertLoadRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        LocalDate startDate = LocalDateUtils.subtractWeek(request.startDate());
        LocalDate endDate = LocalDateUtils.subtractWeek(request.endDate());
        return getLoadEntity(startDate, endDate, request, company, dispatcher, driver);
    }

    private LoadEntity getNextWeekLoadEntity(
        UpsertLoadRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        LocalDate startDate = LocalDateUtils.addWeek(request.startDate());
        LocalDate endDate = LocalDateUtils.addWeek(request.endDate());
        return getLoadEntity(startDate, endDate, request, company, dispatcher, driver);
    }

    private LoadEntity getLoadEntity(
        LocalDate startDate,
        LocalDate endDate,
        UpsertLoadRequest request,
        CompanyEntity company,
        DispatcherEntity dispatcher,
        DriverEntity driver
    ) {
        LoadEntity loadEntity = loadService.getLoadsForTimeframe(
            request.companyUuid(),
            request.dispatcherUuid(),
            request.driverUuid(),
            startDate,
            endDate
        ).orElse(null);

        if (loadEntity != null && request.locations() != null && !request.locations().isEmpty()) {
            Map<String, LoadData> mergedLoadData = mergeLoadData(request, loadEntity);
            loadEntity.setLoadData(mergedLoadData);
            return loadEntity;
        }

        return LoadEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .driver(driver)
            .dispatcher(dispatcher)
            .company(company)
            .loadData(createLoadDataMap(request))
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    private Map<String, LoadData> createLoadDataMap(UpsertLoadRequest request) {
        Map<String, LoadData> loadData = new HashMap<>();
        List<CreateLoadLocationRequest> locations = request.locations()
            .stream()
            .sorted(Comparator.comparing(CreateLoadLocationRequest::order))
            .toList();
        String idAcrossTimeframe = UUID.randomUUID().toString();

        addCoveredLoadData(loadData, request, locations, idAcrossTimeframe);
        addEmptyLoadData(loadData, locations, idAcrossTimeframe);
        addTransitLoadData(
            loadData,
            locations,
            request.locations().getFirst().date(),
            request.locations().getLast().date(),
            idAcrossTimeframe
        );

        return loadData;
    }

    /**
     * Merges new load data with existing load data in a {@link LoadEntity}. The method processes
     * each new load data entry and updates its previous load status based on the corresponding
     * existing load data, if available. Any new entries not already in the previous data are added,
     * and existing entries not overridden by the new data are preserved.
     *
     * @param request the {@link UpsertLoadRequest} containing the new load data and associated details.
     * @param loadEntity the existing {@link LoadEntity} containing the current load data to be merged.
     * @return a {@code Map<String, LoadData>} that represents the merged load data. Keys are date strings,
     *         and values are {@link LoadData} objects containing the load details for the specific dates.
     */
    private Map<String, LoadData> mergeLoadData(UpsertLoadRequest request, LoadEntity loadEntity) {
        Map<String, LoadData> previousLoadData = loadEntity.getLoadData();
        Map<String, LoadData> newLoadData = createLoadDataMap(request);
        Map<String, LoadData> mergedLoadData = new HashMap<>();
        for (Map.Entry<String, LoadData> entry : newLoadData.entrySet()) {
            String date = entry.getKey();
            LoadData newLoadDatum = entry.getValue();
            LoadData previousLoadDatum = previousLoadData.get(date);
            if (previousLoadDatum != null && previousLoadDatum.getCurrentLoadStatus() == LoadStatus.EMPTY) {
                newLoadDatum.setPreviousLoadStatus(LoadStatus.EMPTY);
            }

            mergedLoadData.put(date, newLoadDatum);
        }

        previousLoadData.forEach(mergedLoadData::putIfAbsent);
        return mergedLoadData;
    }

    private void addCoveredLoadData(
        Map<String, LoadData> loadDataMap,
        UpsertLoadRequest request,
        List<CreateLoadLocationRequest> locations,
        String idAcrossTimeframe
    ) {
        // Load Data for the Loading Day
        LocalDate startDate = locations.getFirst().date();
        LoadData coveredLoadDatum = loadObjectsCreator.createCoveredLoadDatum(request, idAcrossTimeframe);
        List<LocationData> loadingDayLocations = new ArrayList<>();
        int order = 0;
        for (CreateLoadLocationRequest createLoadLocationRequest : locations) {
            if (createLoadLocationRequest.date().equals(startDate)) {
                LocationData secondLocationData = loadObjectsCreator.fromCreateLoadLocationRequestToLocationData(
                    createLoadLocationRequest,
                    order++
                );
                loadingDayLocations.add(secondLocationData);
            }
        }

        coveredLoadDatum.setLocations(loadingDayLocations);
        loadDataMap.put(startDate.toString(), coveredLoadDatum);
    }

    private void addEmptyLoadData(
        Map<String, LoadData> loadDataMap,
        List<CreateLoadLocationRequest> locations,
        String idAcrossTimeframe
    ) {
        // Load Data for the last Delivery Day
        LocalDate endDate = locations.getLast().date();
        LoadData emptyLoadDatum = loadObjectsCreator.createEmptyLoadDatum(idAcrossTimeframe);
        List<LocationData> lastDayLocations = new ArrayList<>();
        int order = 0;
        for (CreateLoadLocationRequest createLoadLocationRequest : locations) {
            if (createLoadLocationRequest.date().equals(endDate)) {
                LocationData secondLocationData = loadObjectsCreator.fromCreateLoadLocationRequestToLocationData(
                    createLoadLocationRequest,
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

        emptyLoadDatum.setLocations(lastDayLocations);
        loadDataMap.put(endDate.toString(), emptyLoadDatum);
    }

    private void addTransitLoadData(
        Map<String, LoadData> loadDataMap,
        List<CreateLoadLocationRequest> locations,
        LocalDate startDate,
        LocalDate endDate,
        String idAcrossTimeframe
    ) {
        // Create the Load data for the next days prior to the last one, having the 'Transit' status
        Map<LocalDate, List<CreateLoadLocationRequest>> transitLoadData = locations.stream()
            .collect(Collectors.groupingBy(CreateLoadLocationRequest::date, Collectors.toList()));
        LocalDate current = LocalDateUtils.addDay(startDate);
        int order = 0;
        while (current.isBefore(endDate)) {
            LoadData transitLoadDatum = loadObjectsCreator.createTransitLoadDatum(idAcrossTimeframe);
            List<LocationData> transitLocations = new ArrayList<>();
            for (CreateLoadLocationRequest location : transitLoadData.getOrDefault(current, new ArrayList<>())) {
                transitLocations.add(loadObjectsCreator.fromCreateLoadLocationRequestToLocationData(location, order++));
            }
            transitLoadDatum.setLocations(transitLocations);
            loadDataMap.put(current.toString(), transitLoadDatum);
            current = LocalDateUtils.addDay(current);
        }
    }
}
