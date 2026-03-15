package io.kovin.dispatch.management.system.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.LoadLocationEntity;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import io.kovin.dispatch.management.system.model.persistence.enums.LoadStatus;
import io.kovin.dispatch.management.system.model.persistence.enums.LocationType;
import io.kovin.dispatch.management.system.model.request.CreateLoadLocationRequest;
import io.kovin.dispatch.management.system.service.LoadLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadLocationFacade {

    private final LoadLocationService loadLocationService;

    /**
     * Creates a list of LoadLocationEntity objects based on the provided createLoadLocationRequests and loadEntity.
     * It assigns unique UUIDs, sets the location type, date, and location details, and calculates the location order
     * and load status for each entry. Finally, it persists the entities and returns the created list.
     *
     * @param createLoadLocationRequests a list of requests containing details for creating load locations
     * @param loadEntity the LoadEntity with which the created LoadLocationEntity objects will be associated
     * @return a list of created LoadLocationEntity objects
     */
    public List<LoadLocationEntity> createLoadLocations(
        List<CreateLoadLocationRequest> createLoadLocationRequests,
        LoadEntity loadEntity
    ) {
        List<LoadLocationEntity> loadLocationEntities = new ArrayList<>();
        for (CreateLoadLocationRequest createLoadLocationRequest : createLoadLocationRequests) {
            int order = createLoadLocationRequest.order();
            LoadLocationEntity loadLocationEntity = LoadLocationEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .locationType(LocationType.from(createLoadLocationRequest.label()))
                .location(createLoadLocationRequest.location())
                .date(createLoadLocationRequest.date())
                .load(loadEntity)
                .locationOrder(order)
                .build();
            loadLocationEntities.add(loadLocationEntity);
        }
        loadLocationService.persistLoadLocations(loadLocationEntities);
        return loadLocationEntities;
    }

    private LoadStatus getLoadStatusBasedOnOrder(int order, int numberOfLocations) {
        if (order == 0) {
            return LoadStatus.DISPATCHED;
        }

        if (order == numberOfLocations - 1) {
            return LoadStatus.DELIVERED;
        }

        return LoadStatus.TRANSIT;
    }
}
