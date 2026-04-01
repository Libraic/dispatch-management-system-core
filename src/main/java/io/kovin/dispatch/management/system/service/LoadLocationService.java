package io.kovin.dispatch.management.system.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import io.kovin.dispatch.management.system.model.persistence.LoadLocationEntity;
import io.kovin.dispatch.management.system.repository.LoadLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoadLocationService {

    private final LoadLocationRepository loadLocationRepository;

    /**
     * Persists a list of load location entities into the database.
     * This method logs the number of entities being persisted and saves them
     * using the {@code loadLocationRepository}.
     *
     * @param loadLocationEntities the list of {@code LoadLocationEntity} objects to be persisted.
     *                             If the list is empty, no action is performed.
     */
    public void persistLoadLocations(List<LoadLocationEntity> loadLocationEntities) {
        log.info("Persisting [{}] load locations.", loadLocationEntities.size());
        loadLocationRepository.saveAll(loadLocationEntities);
    }

    /**
     * Filters a list of {@code LoadLocationEntity} objects to include only those whose date falls
     * between the specified start and end dates, inclusive.
     *
     * @param loadLocations the list of {@code LoadLocationEntity} objects to be filtered.
     *                      Each entity must have a non-null {@code date} field.
     * @param startDate the start date (inclusive) used as the lower bound for filtering.
     **/
    public List<LoadLocationEntity> getLoadLocationsByLoadUuidAndDateBetween(
        List<LoadLocationEntity> loadLocations,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return loadLocations.stream()
            .filter(x -> !x.getDate().isBefore(startDate) && !x.getDate().isAfter(endDate))
            .toList();
    }

    public LocalDateTime getDeliveryTime(List<LoadLocationEntity> loadLocations) {
        loadLocations.sort(Comparator.comparing(LoadLocationEntity::getLocationOrder));
        LoadLocationEntity lastLocation = loadLocations.getLast();
        return LocalDateTime.of(lastLocation.getDate(), lastLocation.getTime());
    }
}
