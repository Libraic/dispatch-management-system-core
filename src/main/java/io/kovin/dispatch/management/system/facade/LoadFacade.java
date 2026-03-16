package io.kovin.dispatch.management.system.facade;

import static io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus.DELIVERED;
import static io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus.DISPATCHED;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.START_DATE_BEFORE_END_DATE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.mapper.LoadObjectsCreator;
import io.kovin.dispatch.management.system.model.persistence.LoadLocationEntity;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.request.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import io.kovin.dispatch.management.system.model.response.GetDriverResponse;
import io.kovin.dispatch.management.system.model.response.GetLoadStartingPointResponse;
import io.kovin.dispatch.management.system.model.response.load.GetDriverLoadsResponse;
import io.kovin.dispatch.management.system.model.response.load.GetDriverLoadsDataResponse;
import io.kovin.dispatch.management.system.model.response.load.GetLoadResponse;
import io.kovin.dispatch.management.system.model.response.load.GetLocationResponse;
import io.kovin.dispatch.management.system.model.response.load.UpsertDriverLoadsResponse;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.DriverDispatcherRelationService;
import io.kovin.dispatch.management.system.service.LoadLocationService;
import io.kovin.dispatch.management.system.service.LoadService;
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
    private final LoadService loadService;
    private final LoadLocationService loadLocationService;
    private final DriverDispatcherRelationService driverDispatcherRelationService;

    private final LoadLocationFacade loadLocationFacade;

    private final LoadObjectsCreator loadObjectsCreator;

    /**
     * Inserts or updates a load based on the provided request data. Validates the input data, identifies the
     * relationship between a driver and a dispatcher, creates load entities, updates load status, and builds
     * the response with detailed information.
     *
     * @param request the input data for inserting or updating a load, including company, dispatcher, and driver
     *                identifiers, load details such as revenue and miles, broker and representative information,
     *                and a list of load location details.
     * @return a response containing details of the inserted or updated load, including its UUID, start and end dates,
     *         revenue, miles, broker, representative, load status, and a list of its locations.
     * @throws DispatchManagementSystemException if any validation fails, such as missing mandatory fields or
     *                                           invalid driver-dispatcher relationships.
     */
    @Transactional
    public UpsertDriverLoadsResponse upsertLoad(UpsertLoadRequest request) {
        loadValidationService.validateLoadUpsertion(request);

        DriverDispatcherRelationEntity relation = driverDispatcherRelationService.findRelationByDriverAndDispatcher(
            request.companyUuid(),
            request.dispatcherUuid(),
            request.driverUuid()
        );
        LoadEntity loadEntity = createLoadEntity(request, relation);
        List<LoadLocationEntity> loadLocationEntities = loadLocationFacade.createLoadLocations(request.locations(), loadEntity);
        loadLocationEntities.sort(Comparator.comparing(LoadLocationEntity::getLocationOrder));

        LoadStatus loadStatus = loadEntity.getEndDate().isBefore(LocalDate.now()) ? DELIVERED : DISPATCHED;

        return UpsertDriverLoadsResponse.builder()
            .loadUuid(loadEntity.getUuid())
            .startDate(loadLocationEntities.getFirst().getDate())
            .endDate(loadLocationEntities.getLast().getDate())
            .revenue(loadEntity.getRevenue())
            .miles(loadEntity.getMiles())
            .broker(loadEntity.getBroker())
            .representative(loadEntity.getRepresentative())
            .loadStatus(loadStatus.getStatus())
            .locations(loadLocationEntities.stream()
                .map(loadLocationEntity -> new GetLocationResponse(
                    loadLocationEntity.getLocation(),
                    loadLocationEntity.getDate(),
                    loadLocationEntity.getLocationType().getType(),
                    loadLocationEntity.getLocationOrder()
                )).toList()
            ).build();
    }

    /**
     * Retrieves a list of driver load data for a specified company within a given timeframe.
     * The method validates whether the company is registered and ensures that the start date is
     * not after the end date. It collects and maps the dispatcher-driver relationships, fetches
     * the overlapping loads for each driver, and creates detailed responses encapsulating the
     * dispatcher and driver load information.
     *
     * @param companyUuid the unique identifier of the company whose driver loads are being queried.
     *                    Must not be null or empty, and the company must be registered in the system.
     * @param startDate the start of the timeframe for which driver loads are being retrieved.
     *                  Must not be after the endDate.
     * @param endDate the end of the timeframe for which driver loads are being retrieved.
     *                Must not be before the startDate.
     * @return a list of GetDriverLoadsDataResponse objects, each containing:
     *         - a dispatcher with associated driver load data,
     *         - load details for each driver linked to the dispatcher within the specified timeframe.
     * @throws DispatchManagementSystemException if the company UUID is not registered,
     *                                           or if the start date is after the end date.
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

        Map<DispatcherEntity, List<DriverDispatcherRelationEntity>> relations =
            driverDispatcherRelationService.findRelationsByCompanyGroupedByDispatcher(companyUuid);

        for (var entry : relations.entrySet()) {
            GetDispatcherResponse getDispatcherResponse = loadObjectsCreator.createGetDispatcherResponse(entry.getKey());
            List<GetDriverLoadsResponse> getDriverLoadResponses = new ArrayList<>();
            for (DriverDispatcherRelationEntity relation : entry.getValue()) {
                List<GetLoadResponse> getLoadResponses = new ArrayList<>();
                List<LoadEntity> loads = loadService.getOverlappingLoadsForRelation(relation.getUuid(), startDate, endDate);
                    for (LoadEntity load : loads) {
                    List<LoadLocationEntity> locations = loadLocationService.getLoadLocationsByLoadUuidAndDateBetween(
                        load.getLocations(),
                        startDate,
                        endDate
                    );
                    GetLoadResponse getLoadResponse = loadObjectsCreator.createGetLoadResponse(load, locations);
                    getLoadResponses.add(getLoadResponse);
                }

                GetDriverResponse getDriverResponse = loadObjectsCreator.createGetDriverResponse(relation.getDriver());
                GetDriverLoadsResponse getDriverLoadsResponses = GetDriverLoadsResponse.builder()
                    .relationUuid(relation.getUuid())
                    .driver(getDriverResponse)
                    .loads(getLoadResponses)
                    .build();
                getDriverLoadResponses.add(getDriverLoadsResponses);
            }

            GetDriverLoadsDataResponse getDriverLoadsDataResponse = GetDriverLoadsDataResponse.builder()
                .dispatcher(getDispatcherResponse)
                .driverLoads(getDriverLoadResponses)
                .build();
            responses.add(getDriverLoadsDataResponse);
        }

        return responses;
    }

    /**
     * Retrieves a list of load responses for a given driver-dispatcher relation and a specified timeframe.
     * The method queries for overlapping loads within the specified date range for the relation and fetches
     * their associated locations that fall within the same timeframe. The data is then mapped into response
     * objects encapsulating load details and their respective location information.
     *
     * @param relationUuid the unique identifier of the driver-dispatcher relation. Must not be null or empty.
     * @param startDate the start of the timeframe for retrieving load responses. Must not be null.
     * @param endDate the end of the timeframe for retrieving load responses. Must not be null.
     * @return a list of GetLoadResponse objects, each containing load details and their associated filtered locations
     *         within the provided date range.
     */
    public List<GetLoadResponse> getLoadResponses(String relationUuid, LocalDate startDate, LocalDate endDate) {
        List<GetLoadResponse> getLoadResponses = new ArrayList<>();
        List<LoadEntity> loads = loadService.getOverlappingLoadsForRelation(relationUuid, startDate, endDate);
        for (LoadEntity load : loads) {
            List<LoadLocationEntity> locations = loadLocationService.getLoadLocationsByLoadUuidAndDateBetween(
                load.getLocations(),
                startDate,
                endDate
            );
            GetLoadResponse getLoadResponse = loadObjectsCreator.createGetLoadResponse(load, locations);
            getLoadResponses.add(getLoadResponse);
        }

        return getLoadResponses;
    }

    public GetLoadStartingPointResponse getLoadStartingPoint(String relationUuid, LocalDate date) {
        LoadEntity load = loadService.getLoadByRelationUuidAndDateBetween(relationUuid, date);
        if (load == null || load.getLocations().isEmpty()) {
            return new GetLoadStartingPointResponse(null);
        }

        List<LoadLocationEntity> locations = load.getLocations()
            .stream()
            .sorted(Comparator.comparing(LoadLocationEntity::getLocationOrder))
            .toList();
        return new GetLoadStartingPointResponse(locations.getLast().getLocation());
    }

    /**
     * Deletes a load identified by its unique UUID. This method delegates the execution
     * to the loadService, which performs the actual deletion of the load from the database.
     * The operation is transactional, ensuring database consistency in case of errors.
     *
     * @param loadUuid the unique identifier of the load to be deleted. Must not be null or empty.
     */
    @Transactional
    public void deleteLoad(String loadUuid) {
        loadService.deleteLoadByUuid(loadUuid);
    }

    private LoadEntity createLoadEntity(UpsertLoadRequest request, DriverDispatcherRelationEntity relation) {
        List<CreateLoadLocationRequest> locations = request.locations();
        locations.sort(Comparator.comparing(CreateLoadLocationRequest::order));
        LocalDate startDate = locations.getFirst().date();
        LocalDate endDate = locations.getLast().date();
        LoadEntity loadEntity = LoadEntity.builder()
            .loadStatus(DISPATCHED)
            .uuid(UUID.randomUUID().toString())
            .miles(request.miles())
            .revenue(request.revenue())
            .broker(request.broker())
            .representative(request.representative())
            .representativeContactNumber(request.representativeContactNumber())
            .driverDispatcherRelation(relation)
            .startDate(startDate)
            .endDate(endDate)
            .build();

        loadService.persistLoad(loadEntity);

        return loadEntity;
    }
}
