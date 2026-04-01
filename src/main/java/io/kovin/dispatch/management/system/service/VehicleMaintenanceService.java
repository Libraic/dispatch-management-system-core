package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.VEHICLE_MAINTENANCE_RECORD_NOT_FOUND;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.VEHICLE_MAINTENANCE_RECORD_NOT_FOUND_BY_UUID;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.persistence.VehicleMaintenanceRecordEntity;
import io.kovin.dispatch.management.system.repository.VehicleMaintenanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleMaintenanceService {

    private final VehicleMaintenanceRepository vehicleMaintenanceRepository;

    /**
     * Persists a vehicle maintenance record entity to the database.
     * Logs the UUID of the vehicle maintenance record being saved for tracking purposes.
     *
     * @param vehicleMaintenanceRecordEntity the entity representing the vehicle maintenance record
     *                                       that needs to be persisted in the database
     */
    public void persistVehicleMaintenanceRecord(VehicleMaintenanceRecordEntity vehicleMaintenanceRecordEntity) {
        log.info("Persisting vehicle maintenance record with UUID=[{}].", vehicleMaintenanceRecordEntity.getUuid());
        vehicleMaintenanceRepository.save(vehicleMaintenanceRecordEntity);
    }

    /**
     * Retrieves a vehicle maintenance record entity by its unique UUID.
     * If the record is not found, an exception is thrown.
     *
     * @param uuid the unique identifier of the vehicle maintenance record to be retrieved
     * @return the {@code VehicleMaintenanceRecordEntity} corresponding to the specified UUID
     * @throws DispatchManagementSystemException if no vehicle maintenance record is found for the given UUID
     */
    public VehicleMaintenanceRecordEntity getVehicleMaintenanceRecordByUuid(String uuid) {
        log.info("Retrieving the Vehicle Maintenance Record by UUID=[{}].", uuid);
        var vehicleMaintenanceRecordEntityOptional = vehicleMaintenanceRepository.findByUuid(uuid);
        if (vehicleMaintenanceRecordEntityOptional.isEmpty()) {
            String errorMessage = String.format(VEHICLE_MAINTENANCE_RECORD_NOT_FOUND_BY_UUID, uuid);
            log.error(errorMessage);
            throw DispatchManagementSystemException.ofBadRequest(VEHICLE_MAINTENANCE_RECORD_NOT_FOUND);
        }

        return vehicleMaintenanceRecordEntityOptional.get();
    }

    /**
     * Retrieves a list of overlapping vehicle maintenance records for a specified
     * driver-dispatcher relation within a given date range.
     *
     * @param driverDispatcherRelationUuid the UUID of the driver-dispatcher relation
     *                                      for which overlapping records need to be fetched
     * @param startDate                    the start date of the timeframe to check for overlaps
     * @param endDate                      the end date of the timeframe to check for overlaps
     * @return a list of {@code VehicleMaintenanceRecordEntity} representing the overlapping
     *         vehicle maintenance records for the specified relation and timeframe
     */
    public List<VehicleMaintenanceRecordEntity> getOverlappingRecordsForRelation(
        String driverDispatcherRelationUuid,
        LocalDate startDate,
        LocalDate endDate
    ) {
        log.info(
            "Retrieving the overlapping vehicle maintenance records for the timeframe=[{}-{}] for Driver-Dispatcher Relation with UUID=[{}].",
            startDate,
            endDate,
            driverDispatcherRelationUuid
        );
        return vehicleMaintenanceRepository.findOverlappingRecordsForRelation(driverDispatcherRelationUuid, startDate, endDate);
    }

    /**
     * Deletes a vehicle maintenance record identified by its unique UUID.
     * Logs the UUID of the record being deleted for tracking purposes.
     *
     * @param uuid the unique identifier of the vehicle maintenance record to be deleted
     *             from the database
     */
    @Transactional
    public void deleteVehicleMaintenanceRecordByUuid(String uuid) {
        log.info("Deleting the Vehicle Maintenance record with UUID=[{}].", uuid);
        vehicleMaintenanceRepository.deleteByUuid(uuid);
    }
}
