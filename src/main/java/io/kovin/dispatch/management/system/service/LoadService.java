package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOAD_NOT_FOUND;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.LOAD_NOT_FOUND_BY_UUID;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.LoadEntity;
import io.kovin.dispatch.management.system.repository.LoadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadService {

    private final LoadRepository loadRepository;

    /**
     * Persists the given load entity into the database.
     * Logs the UUID of the load before saving it to provide traceability.
     *
     * @param loadEntity the load entity to be persisted. It contains details such as
     *                   UUID, start and end dates, revenue, loadedMiles, broker information,
     *                   load status, and associated driver-dispatcher relation.
     */
    public LoadEntity persistLoad(LoadEntity loadEntity) {
        log.info("Persisting load with UUID=[{}].", loadEntity.getUuid());
        return loadRepository.save(loadEntity);
    }

    /**
     * Retrieves all load entities associated with a specific driver-dispatcher relation that
     * overlap within the given date range.
     *
     * @param driverDispatcherRelationUuid the UUID of the driver-dispatcher relation for which overlapping loads are to be retrieved.
     * @param startDate the start date of the timeframe to check for overlapping loads.
     * @param endDate the end date of the timeframe to check for overlapping loads.
     * @return a list of {@code LoadEntity} objects that overlap with the specified date range for the given driver-dispatcher relation.
     */
    public List<LoadEntity> getOverlappingLoadsForRelation(
        String driverDispatcherRelationUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        log.info(
            "Retrieving the overlapping loads for the timeframe=[{}-{}] for Driver-Dispatcher Relation with UUID=[{}].",
            startDate,
            endDate,
            driverDispatcherRelationUuid
        );
        return loadRepository.findOverlappingRecordsForRelation(driverDispatcherRelationUuid, startDate, endDate);
    }

    /**
     * Deletes the load entity associated with the specified UUID from the database.
     * This method logs the UUID of the load being deleted for traceability purposes.
     * The deletion is executed within a transactional context to ensure data consistency.
     *
     * @param uuid the unique identifier of the load to be deleted. It must not be null or empty.
     */
    @Transactional
    public void deleteLoadByUuid(String uuid) {
        log.info("Deleting the Load with UUID=[{}].", uuid);
        loadRepository.deleteByUuid(uuid);
    }

    /**
     * Retrieves the load entity associated with the specified UUID.
     * If no load entity is found, an exception is thrown.
     *
     * @param uuid the unique identifier of the load to be retrieved. It must not be null or empty.
     * @return the {@code LoadEntity} associated with the given UUID.
     * @throws DispatchManagementSystemException if no load entity is found with the specified UUID.
     */
    public LoadEntity getLoadByUuid(String uuid) {
        log.info("Retrieving the Load by UUID=[{}].", uuid);
        var loadOptional = loadRepository.findByUuid(uuid);
        if (loadOptional.isEmpty()) {
            String errorMessage = String.format(LOAD_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.ofBadRequest(LOAD_NOT_FOUND);
        }

        return loadOptional.get();
    }

    public LoadEntity getLoadByRelationUuidAndDateBetween(String relationUuid, LocalDate date) {
        return loadRepository.findByRelationUuidAndDateBetween(relationUuid, date).orElse(null);
    }
}
