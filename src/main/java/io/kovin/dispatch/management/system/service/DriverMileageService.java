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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DriverMileageService {

    private final DriverMileageRepository driverMileageRepository;

    /**
     * Persists a DriverMileageEntity object into the database.
     *
     * @param driverMileageEntity the DriverMileageEntity object to be saved.
     */
    public void saveDriverMileage(DriverMileageEntity driverMileageEntity) {
        log.info("Persisting the Driver Mileage.");
        driverMileageRepository.save(driverMileageEntity);
    }

    public void saveAllDriverMileageEntities(List<DriverMileageEntity> driverMileageEntities) {
        log.info("Persisting [{}] Driver Mileage entities.", driverMileageEntities.size());
        driverMileageRepository.saveAll(driverMileageEntities);
    }

    public DriverMileageEntity getDriverMileageEntity(
        LocalDate startDate,
        LocalDate endDate,
        String companyUuid,
        String dispatcherUuid
    ) {
        return driverMileageRepository.findByStartDateAndEndDateAndCompanyUuidAndDispatcherUuidAndDeletedAtIsNull(
            startDate,
            endDate,
            companyUuid,
            dispatcherUuid
        ).orElse(null);
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

    public DriverMileageEntity getByUuidAndCompanyUuid(String uuid) {
        log.info("Retrieving the driver mileage with UUID=[{}].", uuid);
        var driverMileageEntityOptional = driverMileageRepository.findByUuidAndDeletedAtIsNull(uuid);
        if (driverMileageEntityOptional.isEmpty()) {
            String errorMessage = String.format(ErrorMessage.DRIVER_MILEAGE_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }

        return driverMileageEntityOptional.get();
    }

    /**
     * Retrieves a list of DriverMileageEntity objects for a specified company within a given timeframe.
     * Only mileage records that are not marked as deleted are included, and the results are ordered by creation date in ascending order.
     *
     * @param companyUuid the unique identifier of the company for which driver mileage is being retrieved.
     * @param startDate the start date of the timeframe for which mileage records are requested.
     * @param endDate the end date of the timeframe for which mileage records are requested.
     * @return a list of DriverMileageEntity objects matching the specified company and timeframe.
     */
    public List<DriverMileageEntity> getDriversMileageByCompanyAndTimeframe(
        String companyUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return driverMileageRepository.findByCompanyUuidAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndDeletedAtIsNullOrderByCreatedAtAsc(
            companyUuid,
            startDate,
            endDate
        );
    }

    public Optional<DriverMileageEntity> getDriversMileageForTimeframe(
        String dispatcherUuid,
        String driverUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return driverMileageRepository.findDriversMileageForTimeframe(dispatcherUuid, driverUuid, startDate, endDate);
    }
}
