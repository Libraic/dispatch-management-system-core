package io.kovin.dispatch.management.system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.entity.DriverMileageEntity;
import io.kovin.dispatch.management.system.repository.DriverMileageRepository;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DriverMileageService {

    private final DriverMileageRepository driverMileageRepository;

    /**
     * Persists a list of DriverMileageEntity objects to the database.
     *
     * @param driverMileageEntities a list of DriverMileageEntity objects to be saved.
     *                              This list must not be null and should contain the entities
     *                              that need to be persisted.
     */
    public void saveAllDriverMileageEntities(List<DriverMileageEntity> driverMileageEntities) {
        if (!driverMileageEntities.isEmpty()) {
            log.info("Persisting [{}] Driver Mileage entities.", driverMileageEntities.size());
            driverMileageRepository.saveAll(driverMileageEntities);
        }
    }

    /**
     * Retrieves a DriverMileageEntity by its UUID and ensures it has not been marked as deleted.
     *
     * @param uuid the UUID of the driver mileage entity to retrieve.
     *             This parameter must not be null or empty.
     * @return the DriverMileageEntity instance matching the provided UUID and not marked as deleted.
     * @throws DispatchManagementSystemException if no entity is found with the given UUID.
     */
    public DriverMileageEntity getByUuid(String uuid) {
        var driverMileageEntityOptional = findByUuid(uuid);
        if (driverMileageEntityOptional.isEmpty()) {
            String errorMessage = String.format(ErrorMessage.DRIVER_MILEAGE_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }

        return driverMileageEntityOptional.get();
    }

    public Optional<DriverMileageEntity> findByUuid(String driverMileageUuid) {
        log.info("Retrieving the driver mileage with UUID=[{}].", driverMileageUuid);
        return driverMileageRepository.findByUuidAndDeletedAtIsNull(driverMileageUuid);
    }

    /**
     * Retrieves the mileage information for a specific driver within a particular timeframe.
     *
     * @param companyUuid the unique identifier of the company to which the driver belongs.
     *                    This parameter must not be null or empty.
     * @param dispatcherUuid the unique identifier of the dispatcher associated with the driver.
     *                       This parameter must not be null or empty.
     * @param driverUuid the unique identifier of the driver whose mileage details are being retrieved.
     *                   This parameter must not be null or empty.
     * @param startDate the start date of the timeframe for which mileage information is requested.
     *                  This parameter must not be null.
     * @param endDate the end date of the timeframe for which mileage information is requested.
     *                This parameter must not be null.
     * @return an Optional containing the DriverMileageEntity if a matching record is found within the
     *         specified parameters, or an empty Optional if no such record exists.
     */
    public Optional<DriverMileageEntity> getDriversMileageForTimeframe(
        String companyUuid,
        String dispatcherUuid,
        String driverUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return driverMileageRepository.findDriversMileageForTimeframe(
            companyUuid,
            dispatcherUuid,
            driverUuid,
            startDate,
            endDate
        );
    }

    public void deleteDriverMileage(DriverMileageEntity driverMileageEntity) {
        String uuid = driverMileageEntity.getUuid();
        log.info("Deleting the driver mileage with UUID=[{}].", uuid);
        driverMileageRepository.deleteByUuid(uuid);
    }
}
