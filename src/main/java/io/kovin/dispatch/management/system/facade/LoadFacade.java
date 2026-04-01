package io.kovin.dispatch.management.system.facade;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOAD_WAS_ALREADY_DELIVERED;

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
import io.kovin.dispatch.management.system.model.request.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.model.response.GetLoadStartingPointResponse;
import io.kovin.dispatch.management.system.model.response.load.GenericLoadResponse;
import io.kovin.dispatch.management.system.service.DriverDispatcherRelationService;
import io.kovin.dispatch.management.system.service.LoadLocationService;
import io.kovin.dispatch.management.system.service.LoadService;
import io.kovin.dispatch.management.system.validation.LoadValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadFacade {

    private final LoadValidationService loadValidationService;

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
     * revenue, miles, broker, representative, load status, and a list of its locations.
     * @throws DispatchManagementSystemException if any validation fails, such as missing mandatory fields or
     *                                           invalid driver-dispatcher relationships.
     */
    @Transactional
    public GenericLoadResponse upsertLoad(UpsertLoadRequest request) {
        loadValidationService.validateLoadUpsertion(request);

        DriverDispatcherRelationEntity relation = driverDispatcherRelationService.getRelationByUuid(request.relationUuid());

        LoadEntity loadEntity = createLoadEntity(request, relation);
        List<LoadLocationEntity> loadLocationEntities = loadLocationFacade.createLoadLocations(request.locations(), loadEntity);
        loadLocationEntities.sort(Comparator.comparing(LoadLocationEntity::getLocationOrder));

        loadEntity.getLocations().clear();
        loadEntity.getLocations().addAll(loadLocationEntities);
        loadService.persistLoad(loadEntity);

        return loadObjectsCreator.createGetLoadResponse(loadEntity, loadLocationEntities);
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
        return new GetLoadStartingPointResponse(startingPoint.getLocation(), startingPoint.getTime());
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
        LoadEntity loadEntity = getInitialLoadEntity(request.loadUuid());
        List<CreateLoadLocationRequest> locations = request.locations();
        locations.sort(Comparator.comparing(CreateLoadLocationRequest::order));
        LocalDate startDate = locations.getFirst().date();
        LocalDate endDate = locations.getLast().date();
        LoadEntity finalLoadEntity = loadEntity.toBuilder()
            .loadStatus(loadEntity.getLoadStatus() != null ? loadEntity.getLoadStatus() : getLoadStatus(locations.getLast()))
            .miles(request.miles())
            .revenue(request.revenue())
            .broker(request.broker())
            .representative(request.representative())
            .representativeContactNumber(request.representativeContactNumber())
            .driverDispatcherRelation(relation)
            .startDate(startDate)
            .endDate(endDate)
            .locations(new ArrayList<>())
            .build();

        return loadService.persistLoad(finalLoadEntity);
    }

    private LoadEntity getInitialLoadEntity(String loadUuid) {
        if (loadUuid != null) {
            LoadEntity existingLoadEntity = loadService.getLoadByUuid(loadUuid);
            LocalDateTime deliveryDateTime = loadLocationService.getDeliveryTime(existingLoadEntity.getLocations());
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(deliveryDateTime)) {
                throw DispatchManagementSystemException.ofBadRequest(LOAD_WAS_ALREADY_DELIVERED);
            }

            return existingLoadEntity;
        }


        return LoadEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .build();
    }

    private LoadStatus getLoadStatus(CreateLoadLocationRequest lastCreateLoadLocationRequest) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deliveryDateTime = LocalDateTime.of(lastCreateLoadLocationRequest.date(), lastCreateLoadLocationRequest.time());
        return now.isAfter(deliveryDateTime) ? LoadStatus.DELIVERED : LoadStatus.DISPATCHED;
    }
}
