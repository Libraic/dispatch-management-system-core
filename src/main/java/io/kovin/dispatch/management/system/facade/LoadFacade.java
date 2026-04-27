package io.kovin.dispatch.management.system.facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.mapper.LoadObjectsCreator;
import io.kovin.dispatch.management.system.model.persistence.LoadLocationEntity;
import io.kovin.dispatch.management.system.model.persistence.DriverDispatcherRelationEntity;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.persistence.enums.LocationType;
import io.kovin.dispatch.management.system.model.request.load.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.model.request.load.UpsertLoadRequest;
import io.kovin.dispatch.management.system.model.response.GetLoadStartingPointResponse;
import io.kovin.dispatch.management.system.model.response.load.GenericLoadResponse;
import io.kovin.dispatch.management.system.service.DriverDispatcherRelationService;
import io.kovin.dispatch.management.system.service.IngestionService;
import io.kovin.dispatch.management.system.service.LoadLocationService;
import io.kovin.dispatch.management.system.service.LoadService;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import io.kovin.dispatch.management.system.validation.LoadValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadFacade {

    private final LoadValidationService loadValidationService;

    private final LoadService loadService;
    private final LoadLocationService loadLocationService;
    private final DriverDispatcherRelationService driverDispatcherRelationService;
    private final IngestionService ingestionService;

    private final LoadLocationFacade loadLocationFacade;

    private final LoadObjectsCreator loadObjectsCreator;

    /**
     * Inserts or updates a load based on the provided request data. Validates the input data, identifies the
     * relationship between a driver and a dispatcher, creates load entities, updates load status, and builds
     * the response with detailed information.
     *
     * @param request the input data for inserting or updating a load, including company, dispatcher, and driver
     *                identifiers, load details such as revenue and loadedMiles, broker and representative information,
     *                and a list of load location details.
     * @return a response containing details of the inserted or updated load, including its UUID, start and end dates,
     * revenue, loadedMiles, broker, representative, load status, and a list of its locations.
     * @throws DispatchManagementSystemException if any validation fails, such as missing mandatory fields or
     *                                           invalid driver-dispatcher relationships.
     */
    @Transactional
    public GenericLoadResponse upsertLoad(UpsertLoadRequest request) {
        loadValidationService.validateLoadUpsertion(request);

        DriverDispatcherRelationEntity relation = driverDispatcherRelationService.getRelationByUuid(request.relationUuid());

        List<CreateLoadLocationRequest> locations = request.locations();
        locations.sort(Comparator.comparing(CreateLoadLocationRequest::order));

        LoadEntity loadEntity = createLoadEntity(request, locations, relation);
        loadLocationFacade.createLoadLocations(locations, loadEntity);

        loadService.persistLoad(loadEntity);

        return loadObjectsCreator.createGetLoadResponse(loadEntity, loadEntity.getLocations());
    }

    public GenericLoadResponse ingestDocument(MultipartFile file) {
        GenericLoadResponse response = ingestionService.ingestDocument(file);
        if (response != null) {
            return response;
        }

        return GenericLoadResponse.builder().build();
    }

    /**
     * Retrieves a list of load responses for a given driver-dispatcher relation and a specified timeframe.
     * The method queries for overlapping loads within the specified date range for the relation and fetches
     * their associated locations that fall within the same timeframe. The data is then mapped into response
     * objects encapsulating load details and their respective location information.
     *
     * @param relationUuid the unique identifier of the driver-dispatcher relation. Must not be null or empty.
     * @param startDate    the start of the timeframe for retrieving load responses. Must not be null.
     * @param endDate      the end of the timeframe for retrieving load responses. Must not be null.
     * @return a list of GetLoadResponse objects, each containing load details and their associated filtered locations
     * within the provided date range.
     */
    public List<GenericLoadResponse> getLoadResponses(String relationUuid, LocalDate startDate, LocalDate endDate) {
        List<GenericLoadResponse> genericLoadResponses = new ArrayList<>();
        List<LoadEntity> loads = loadService.getOverlappingLoadsForRelation(relationUuid, startDate, endDate);
        for (LoadEntity load : loads) {
            List<LoadLocationEntity> locations = loadLocationService.getLoadLocationsByLoadUuidAndDateBetween(
                load.getLocations(),
                startDate,
                endDate
            );
            GenericLoadResponse genericLoadResponse = loadObjectsCreator.createGetLoadResponse(load, locations);
            genericLoadResponses.add(genericLoadResponse);
        }

        return genericLoadResponses;
    }

    public GetLoadStartingPointResponse getLoadStartingPoint(String relationUuid, LocalDate date) {
        LoadEntity load = loadService.getLoadByRelationUuidAndDateBetween(relationUuid, date);
        if (load == null || load.getLocations().isEmpty()) {
            return new GetLoadStartingPointResponse(null, null);
        }

        List<LoadLocationEntity> locations = load.getLocations()
            .stream()
            .sorted(Comparator.comparing(LoadLocationEntity::getLocationOrder))
            .toList();
        LoadLocationEntity startingPoint = locations.getLast();
        return new GetLoadStartingPointResponse(startingPoint.getLocation(), startingPoint.getAddress());
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

    private LoadEntity createLoadEntity(
        UpsertLoadRequest request,
        List<CreateLoadLocationRequest> locations,
        DriverDispatcherRelationEntity relation
    ) {
        LoadEntity loadEntity = getOrCreateLoadEntity(request.loadUuid());

        LocalDate startDate = getStartDate(locations);
        LocalDate endDate = getEndDate(locations);
        LoadEntity updatedLoadEntity = loadEntity.toBuilder()
            .loadNumber(request.loadNumber())
            .loadStatus(getLoadStatus(request.loadStatus(), loadEntity.getLoadStatus(), locations.getLast()))
            .loadedMiles(request.loadedMiles())
            .emptyMiles(request.emptyMiles())
            .revenue(request.revenue())
            .broker(request.broker())
            .representative(request.representative())
            .representativeContactNumber(request.representativeContactNumber())
            .driverDispatcherRelation(relation)
            .startDate(startDate)
            .endDate(endDate)
            .locations(new ArrayList<>())
            .build();

        return loadService.persistLoad(updatedLoadEntity);
    }

    private LoadEntity getOrCreateLoadEntity(String loadUuid) {
        if (loadUuid != null) {
            return loadService.getLoadByUuid(loadUuid);
        }

        return LoadEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .build();
    }

    private LoadStatus getLoadStatus(
        String requestedLoadStatus,
        LoadStatus actualLoadStatus,
        CreateLoadLocationRequest lastCreateLoadLocationRequest
    ) {
        if (requestedLoadStatus != null) {
            return LoadStatus.from(requestedLoadStatus);
        }

        if (actualLoadStatus != null) {
            return actualLoadStatus;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deliveryDateTime = LocalDateTime.of(lastCreateLoadLocationRequest.date(), lastCreateLoadLocationRequest.time());
        return now.isAfter(deliveryDateTime) ? LoadStatus.DELIVERED : LoadStatus.BOOKED;
    }

    private LocalDate getStartDate(List<CreateLoadLocationRequest> locations) {
        for (CreateLoadLocationRequest location : locations) {
            if (LocationType.from(location.label()) == LocationType.PICK_UP) {
                return location.date();
            }
        }

        throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.PICK_UP_LOCATION_MISSING);
    }

    private LocalDate getEndDate(List<CreateLoadLocationRequest> locations) {
        for (int i = locations.size() - 1; i >= 0; i--) {
            if (LocationType.from(locations.get(i).label()) == LocationType.DELIVERY) {
                return locations.get(i).date();
            }
        }

        throw DispatchManagementSystemException.ofBadRequest(ErrorMessage.DELIVERY_LOCATION_MISSING);
    }
}
