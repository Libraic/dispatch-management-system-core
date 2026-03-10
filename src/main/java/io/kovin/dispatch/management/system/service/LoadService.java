package io.kovin.dispatch.management.system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.entity.LoadEntity;
import io.kovin.dispatch.management.system.model.entity.LoadData;
import io.kovin.dispatch.management.system.model.internal.Pair;
import io.kovin.dispatch.management.system.repository.LoadRepository;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class LoadService {

    private final LoadRepository loadRepository;

    /**
     * Persists a list of LoadEntity instances to the database.
     * Logs the number of entities being persisted if the list is not empty.
     *
     * @param loadEntities a list of LoadEntity objects to be persisted.
     *                     This parameter must not be null. If the list is empty, no action is taken.
     */
    public void saveLoadEntities(List<LoadEntity> loadEntities) {
        if (!loadEntities.isEmpty()) {
            log.info("Persisting [{}] Load entities.", loadEntities.size());
            loadRepository.saveAll(loadEntities);
        }
    }

    /**
     * Retrieves a {@code LoadEntity} by its unique identifier (UUID).
     * If the entity is not found, an exception is thrown.
     *
     * @param uuid the unique identifier of the load entity to be retrieved.
     *             This parameter must not be null or empty.
     * @return the {@code LoadEntity} associated with the provided UUID.
     * @throws DispatchManagementSystemException if no load entity is found for the given UUID.
     */
    public LoadEntity getByUuid(String uuid) {
        var loadEntityOptional = findByUuid(uuid);
        if (loadEntityOptional.isEmpty()) {
            String errorMessage = String.format(ErrorMessage.LOAD_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }

        return loadEntityOptional.get();
    }

    /**
     * Finds a {@code LoadEntity} by its unique identifier (UUID) if it has not been deleted.
     * This method retrieves the load with the specified UUID from the repository where
     * the {@code deletedAt} field is null.
     *
     * @param loadUuid the unique identifier of the load to be retrieved.
     *                 This parameter must not be null or empty.
     * @return an {@code Optional} containing the matching {@code LoadEntity} if found,
     *         or an empty {@code Optional} if no matching entity exists.
     */
    public Optional<LoadEntity> findByUuid(String loadUuid) {
        log.info("Retrieving the loads with UUID=[{}].", loadUuid);
        return loadRepository.findByUuidAndDeletedAtIsNull(loadUuid);
    }

    /**
     * Retrieves a load entity for a specific company, dispatcher, and driver within a given timeframe.
     * The results are filtered based on the provided UUIDs and the date range,
     * ensuring that only non-deleted loads are considered.
     *
     * @param companyUuid the UUID of the company for which the loads need to be retrieved.
     *                    This parameter must not be null or empty.
     * @param dispatcherUuid the UUID of the dispatcher associated with the loads.
     *                       This parameter must not be null or empty.
     * @param driverUuid the UUID of the driver for whom the loads are associated.
     *                   This parameter must not be null or empty.
     * @param startDate the start date of the timeframe for filtering loads.
     *                  This parameter must not be null.
     * @param endDate the end date of the timeframe for filtering loads.
     *                This parameter must not be null.
     * @return an {@code Optional} containing the matching {@code LoadEntity} if found,
     *         or an empty {@code Optional} if no matching entity exists.
     */
    public Optional<LoadEntity> getLoadsForTimeframe(
        String companyUuid,
        String dispatcherUuid,
        String driverUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return loadRepository.findLoadsForTimeframe(
            companyUuid,
            dispatcherUuid,
            driverUuid,
            startDate,
            endDate
        );
    }

    public Pair<LocalDate, LocalDate> getStartAndLastDateOfTimeframe(String loadUuid, String idAcrossTimeframe) {
        LoadEntity loadEntity = getByUuid(loadUuid);
        List<LocalDate> timeframeDates = loadEntity.getLoadData()
            .entrySet()
            .stream()
            .filter(x -> x.getValue().getIdAcrossTimeframe().equals(idAcrossTimeframe))
            .map(Map.Entry::getKey)
            .map(LocalDate::parse)
            .sorted()
            .toList();
        if (timeframeDates.isEmpty()) {
            return null;
        }
        return Pair.of(timeframeDates.getFirst(), timeframeDates.getLast());
    }

    /**
     * Deletes a load entity from the system based on its unique identifier (UUID).
     * This method retrieves the UUID from the provided {@code LoadEntity} instance
     * and delegates the deletion process to the {@code loadRepository}.
     *
     * @param loadEntity the load entity to be deleted.
     *                   This parameter must not be null and must contain a valid UUID.
     */
    public void deleteLoad(LoadEntity loadEntity) {
        String uuid = loadEntity.getUuid();
        log.info("Deleting the load with UUID=[{}].", uuid);
        loadRepository.deleteByUuid(uuid);
    }

    public String getIdAcrossTimeframe(LoadEntity loadEntity, LocalDate date) {
        LocalDate adjacentLoadDate = date.minusDays(1);
        return Optional.ofNullable(loadEntity.getLoadData().get(adjacentLoadDate.toString()))
            .map(LoadData::getIdAcrossTimeframe)
            .orElse(null);
    }
}
