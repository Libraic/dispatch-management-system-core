package io.kovin.dispatch.management.system.repository;

import java.util.Optional;
import io.kovin.dispatch.management.system.model.persistence.VehicleMaintenanceRecordEntity;

public interface VehicleMaintenanceRepository extends BaseSchedulableRepository<VehicleMaintenanceRecordEntity> {

    void deleteByUuid(String uuid);

    Optional<VehicleMaintenanceRecordEntity> findByUuid(String uuid);
}
