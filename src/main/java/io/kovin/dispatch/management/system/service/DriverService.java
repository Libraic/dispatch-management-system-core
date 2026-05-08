package io.kovin.dispatch.management.system.service;

import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.DriverEntity;
import io.kovin.dispatch.management.system.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public DriverEntity getByUuid(UUID uuid) {
        log.info("Retrieving driver by UUID=[{}].", uuid);
        return driverRepository.findByUuidAndDeletedAtIsNull(uuid).orElse(null);
    }
}
