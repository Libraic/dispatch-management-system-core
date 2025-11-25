package io.kovin.dispatch.management.system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.repository.DriverMileageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DriverMileageService {

    private final DriverMileageRepository driverMileageRepository;

    /**
     * Persist a list of DriverMileageEntity objects in the database.
     * @param mileageEntities the list of DriverMileageEntity objects.
     * @return the updated DriverMileageEntity objects (the ID field is populated).
     */
    public List<DriverMileageEntity> saveMileageEntities(List<DriverMileageEntity> mileageEntities) {
        log.info("Persisting [{}] Mileage entities.", mileageEntities.size());
        return driverMileageRepository.saveAll(mileageEntities);
    }

    /**
     * Get a dictionary, where the key is the UUID of the Driver Mileage entity and the value is the entity itself.
     * @param uuids a list of the UUIDs of the entities.
     * @return a dictionary with the UUID-DriverMileageEntity pair.
     */
    public Map<String, DriverMileageEntity> getMileageMapByUuids(List<String> uuids) {
        return driverMileageRepository.findByUuidIn(uuids)
            .stream()
            .collect(Collectors.toMap(DriverMileageEntity::getUuid, Function.identity()));
    }

    /**
     * Finds DriverMileageEntity that has the provided Dispatcher UUID, Driver UUID, start/end date.
     * The combination of Dispatcher-Driver is guaranteed to be unique, so at most one combination is possible.
     * @param dispatcherUuid the UUID of the Dispatcher.
     * @param driverUuid     the UUID of the Driver.
     * @param startDate      the start date of the Mileage.
     * @param endDate        the end date of the Mileage.
     * @return an optional containing the DriverMileageEntity.
     */
    public Optional<DriverMileageEntity> getByDispatcherAndDriverAndStartDateAndEndDate(
        String dispatcherUuid,
        String driverUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return driverMileageRepository.findByDispatcher_UuidAndDriver_UuidAndStartDateAndEndDate(
            dispatcherUuid,
            driverUuid,
            startDate,
            endDate
        );
    }

    /**
     * Removes the DriverMileageEntity objects from the database by the provided UUIDs.
     * @param uuids a list of UUIDs of the Mileage objects that should be removed.
     */
    @Transactional
    public void deleteDriversMileageByUuids(List<String> uuids) {
        driverMileageRepository.deleteAllByUuidIn(uuids);
        log.trace("[{}] records were deleted.", uuids);
    }
}
