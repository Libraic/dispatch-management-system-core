package io.kovin.dispatch.management.system.service;

import java.time.LocalDate;
import java.util.List;
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
        log.info("Persisting day off with UUID=[{}].", daysOffPeriodEntity.getUuid());
        daysOffRepository.save(daysOffPeriodEntity);
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
        String driverDispatcherRelationUuid,
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
    public void deleteDaysOffPeriodByUuid(String uuid) {
        log.info("Deleting the Days Off period with UUID=[{}].", uuid);
        daysOffRepository.deleteByUuid(uuid);
    }
}
