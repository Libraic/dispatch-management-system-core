package io.kovin.dispatch.management.system.repository;

import io.kovin.dispatch.management.system.model.persistence.VehicleMaintenanceRecordEntity;

public interface VehicleMaintenanceRepository extends BasePlannableRepository<VehicleMaintenanceRecordEntity> {

    void deleteByUuid(String uuid);
}
