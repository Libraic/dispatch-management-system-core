package io.kovin.dispatch.management.system.service;

import java.util.List;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.entity.DriverEntity;
import io.kovin.dispatch.management.system.repository.DriverRepository;
import io.kovin.dispatch.management.system.utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverEntity saveDriver(DriverEntity driverEntity) {
        log.info("Saving driver with UUID=[{}].", driverEntity.getUuid());
        return driverRepository.save(driverEntity);
    }

    /**
     * Retrieves a driver entity by its unique driverMileageUuid (UUID) if it exists and is not marked as deleted.
     *
     * @param uuid the unique driverMileageUuid of the driver to retrieve
     * @return the driver entity corresponding to the provided UUID
     * @throws DispatchManagementSystemException if the driver is not found or marked as deleted
     */
    public DriverEntity getByUuid(String uuid) {
        log.info("Retrieving the driver with UUID=[{}].", uuid);
        Optional<DriverEntity> driverEntityOptional = driverRepository.findByUuidAndDeletedAtIsNull(uuid);
        if (driverEntityOptional.isEmpty()) {
            String errorMessage = String.format(ErrorMessage.DRIVER_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.of(errorMessage, HttpStatus.NOT_FOUND);
        }

        return driverEntityOptional.get();
    }

    public List<DriverEntity> getDriversWithoutDispatchersByCompanyUuid(String companyUuid) {
        return driverRepository.getDriversWithoutDispatchersByCompanyUuid(companyUuid);
    }
}
