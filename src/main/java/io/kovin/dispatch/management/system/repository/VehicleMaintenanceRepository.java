package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.VehicleMaintenanceRecordEntity;

public interface VehicleMaintenanceRepository extends BaseSchedulableRepository<VehicleMaintenanceRecordEntity> {

    void deleteByUuid(UUID uuid);

    Optional<VehicleMaintenanceRecordEntity> findByUuid(UUID uuid);
}
