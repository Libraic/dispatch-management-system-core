package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.DAYS_OFF_PERIOD_NOT_FOUND;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.DAYS_OFF_PERIOD_NOT_FOUND_BY_UUID;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.DaysOffPeriodEntity;
import io.kovin.dispatch.management.system.repository.DaysOffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DaysOffService {

    private final DaysOffRepository daysOffRepository;

    /**
     * Persists a given Days Off period into the repository. The method logs the operation
     * and saves the provided {@code DaysOffPeriodEntity} to the database.
     *
     * @param daysOffPeriodEntity the {@code DaysOffPeriodEntity} object representing
     *                            the Days Off period to be persisted. It contains details
     *                            such as the unique identifier (UUID), start date, end date,
     *                            and associated Driver-Dispatcher relation.
     */
    public void persistDayOffPeriod(DaysOffPeriodEntity daysOffPeriodEntity) {
        log.debug("Persisting day off with UUID=[{}].", daysOffPeriodEntity.getUuid());
        daysOffRepository.save(daysOffPeriodEntity);
    }

    /**
     * Retrieves a {@code DaysOffPeriodEntity} by its unique identifier (UUID).
     * This method interacts with the repository to fetch the record associated
     * with the provided UUID. If no record is found, an exception is thrown.
     *
     * @param uuid the unique identifier of the Days Off period to be retrieved.
     * @return the {@code DaysOffPeriodEntity} associated with the given UUID.
     * @throws DispatchManagementSystemException if no Days Off period is found for the provided UUID.
     */
    public DaysOffPeriodEntity getDaysOffPeriodByUuid(UUID uuid) {
        log.debug("Retrieving the Days Off Period by UUID=[{}].", uuid);
        var daysOffPeriodEntityOptional = daysOffRepository.findByUuid(uuid);
        if (daysOffPeriodEntityOptional.isEmpty()) {
            String errorMessage = String.format(DAYS_OFF_PERIOD_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.ofBadRequest(DAYS_OFF_PERIOD_NOT_FOUND);
        }

        return daysOffPeriodEntityOptional.get();
    }

    /**
     * Retrieves a list of days off periods that overlap with the specified time frame
     * for a given Driver-Dispatcher relation.
     *
     * @param driverDispatcherRelationUuid the unique identifier of the Driver-Dispatcher relation.
     * @param startDate the starting date of the time frame.
     * @param endDate the ending date of the time frame.
     * @return a list of {@code DaysOffPeriodEntity} objects representing the overlapping days off periods.
     */
    public List<DaysOffPeriodEntity> getOverlappingDaysOffPeriodsForRelation(
        UUID driverDispatcherRelationUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        log.info(
            "Retrieving the overlapping days off period for the timeframe=[{}-{}] for Driver-Dispatcher Relation with UUID=[{}].",
            startDate,
            endDate,
            driverDispatcherRelationUuid
        );
        return daysOffRepository.findOverlappingRecordsForRelation(driverDispatcherRelationUuid, startDate, endDate);
    }

    /**
     * Deletes a Days Off period from the system based on the provided UUID.
     * The method logs the deletion operation and interacts with the data repository
     * to remove the corresponding Days Off record.
     *
     * @param uuid the unique identifier of the Days Off period to be deleted.
     */
    @Transactional
    public void deleteDaysOffPeriodByUuid(UUID uuid) {
        log.info("Deleting the Days Off period with UUID=[{}].", uuid);
        daysOffRepository.deleteByUuid(uuid);
    }
}
