package io.kovin.dispatch.management.system.facade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.LoadLocationEntity;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
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
     */
    public void createLoadLocations(
        List<CreateLoadLocationRequest> createLoadLocationRequests,
        LoadEntity loadEntity
    ) {
        if (!shouldCreateLocations(loadEntity.getLocations(), createLoadLocationRequests)) {
            return;
        }

        List<LoadLocationEntity> loadLocationEntities = new ArrayList<>();
        for (CreateLoadLocationRequest createLoadLocationRequest : createLoadLocationRequests) {
            int order = createLoadLocationRequest.order();
            LoadLocationEntity loadLocationEntity = LoadLocationEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .locationType(LocationType.from(createLoadLocationRequest.label()))
                .location(createLoadLocationRequest.location())
                .date(createLoadLocationRequest.date())
                .time(createLoadLocationRequest.time())
                .load(loadEntity)
                .locationOrder(order)
                .build();
            loadLocationEntities.add(loadLocationEntity);
        }
        loadLocationService.persistLoadLocations(loadLocationEntities);

        loadLocationEntities.sort(Comparator.comparing(LoadLocationEntity::getLocationOrder));

        loadEntity.getLocations().clear();
        loadEntity.getLocations().addAll(loadLocationEntities);
    }

    private boolean shouldCreateLocations(List<LoadLocationEntity> previousLocations, List<CreateLoadLocationRequest> createLoadLocationRequests) {
        int numberOfLocations = createLoadLocationRequests.size();
        if (numberOfLocations != previousLocations.size()) {
            return true;
        }

        previousLocations.sort(Comparator.comparing(LoadLocationEntity::getLocationOrder));
        for (int i = 0; i < numberOfLocations; ++i) {
            LoadLocationEntity previous = previousLocations.get(i);
            CreateLoadLocationRequest actual = createLoadLocationRequests.get(i);
            if (!previous.getLocation().equals(actual.location()) ||
                !previous.getLocationType().getType().equals(actual.label()) ||
                !previous.getDate().equals(actual.date()) ||
                !previous.getTime().equals(actual.time())
            ) {
                return true;
            }
        }

        return false;
    }
}
