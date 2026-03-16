package io.kovin.dispatch.management.system.facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.persistence.VehicleMaintenanceRecordEntity;
import io.kovin.dispatch.management.system.model.request.UpsertVehicleMaintenanceRecordRequest;
import io.kovin.dispatch.management.system.model.response.GetVehicleMaintenanceResponse;
import io.kovin.dispatch.management.system.model.response.UpsertVehicleMaintenanceRecordResponse;
import io.kovin.dispatch.management.system.service.DriverDispatcherRelationService;
import io.kovin.dispatch.management.system.service.VehicleMaintenanceService;
import io.kovin.dispatch.management.system.validation.VehicleMaintenanceValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleMaintenanceFacade {

    private final DriverDispatcherRelationService driverDispatcherRelationService;
    private final VehicleMaintenanceService vehicleMaintenanceService;
    private final VehicleMaintenanceValidationService vehicleMaintenanceValidationService;

    public UpsertVehicleMaintenanceRecordResponse upsertVehicleMaintenanceRecord(
        UpsertVehicleMaintenanceRecordRequest request
    ) {
        vehicleMaintenanceValidationService.validateVehicleMaintenanceUpsertion(request);
        var driverDispatcherRelationEntity = driverDispatcherRelationService.getRelationByUuid(request.relationId());
        VehicleMaintenanceRecordEntity vehicleMaintenanceRecordEntity = VehicleMaintenanceRecordEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .location(request.location())
            .driverDispatcherRelation(driverDispatcherRelationEntity)
            .build();
        vehicleMaintenanceService.persistingVehicleMaintenanceRecord(vehicleMaintenanceRecordEntity);
        return UpsertVehicleMaintenanceRecordResponse.builder()
            .vehicleMaintenanceRecordUuid(vehicleMaintenanceRecordEntity.getUuid())
            .startDate(vehicleMaintenanceRecordEntity.getStartDate())
            .endDate(vehicleMaintenanceRecordEntity.getEndDate())
            .location(vehicleMaintenanceRecordEntity.getLocation())
            .build();
    }

    /**
     * Retrieves a list of vehicle maintenance response objects for a specified
     * driver-dispatcher relation within a given date range.
     *
     * @param relationId the UUID of the driver-dispatcher relation for which
     *                   vehicle maintenance records need to be fetched
     * @param startDate  the start date of the timeframe for which the records
     *                   should be retrieved
     * @param endDate    the end date of the timeframe for which the records
     *                   should be retrieved
     * @return a list of {@code GetVehicleMaintenanceResponse} objects containing
     *         details about the vehicle maintenance records for the specified
     *         relation and time range
     */
    public List<GetVehicleMaintenanceResponse> getVehicleMaintenanceResponseList(
        String relationId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        var records = vehicleMaintenanceService.getOverlappingRecordsForRelation(relationId, startDate, endDate);
        List<GetVehicleMaintenanceResponse> responses = new ArrayList<>();
        for (var record : records) {
            GetVehicleMaintenanceResponse response = GetVehicleMaintenanceResponse.builder()
                .vehicleMaintenanceRecordUuid(record.getUuid())
                .startDate(record.getStartDate())
                .endDate(record.getEndDate())
                .location(record.getLocation())
                .build();
            responses.add(response);
        }

        return responses;
    }
}
