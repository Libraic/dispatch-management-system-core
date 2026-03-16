package io.kovin.dispatch.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.facade.VehicleMaintenanceFacade;
import io.kovin.dispatch.management.system.model.request.UpsertVehicleMaintenanceRecordRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.GetVehicleMaintenanceResponse;
import io.kovin.dispatch.management.system.model.response.UpsertVehicleMaintenanceRecordResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.service.VehicleMaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/vehicle-maintenance")
@RequiredArgsConstructor
@Slf4j
public class VehicleMaintenanceController {

    private final VehicleMaintenanceService vehicleMaintenanceService;
    private final VehicleMaintenanceFacade vehicleMaintenanceFacade;

    @PutMapping
    public ResponseEntity<ApiResponse<UpsertVehicleMaintenanceRecordResponse, ErrorResponse>> upsertVehicleMaintenanceRecord(
        @RequestBody UpsertVehicleMaintenanceRecordRequest request
    ) {
        log.info("A request to upsert a vehicle maintenance record was received.");
        UpsertVehicleMaintenanceRecordResponse response = vehicleMaintenanceFacade.upsertVehicleMaintenanceRecord(request);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @GetMapping("/relations/{relationUuid}")
    public ResponseEntity<ApiResponse<List<GetVehicleMaintenanceResponse>, ErrorResponse>> getRelationLoadsForTimeframe(
        @PathVariable(name = "relationUuid") String relationUuid,
        @RequestParam(name = "startDate") LocalDate startDate,
        @RequestParam(name = "endDate") LocalDate endDate
    ) {
        log.info(
            "A request to retrieve the Vehicle Maintenance records for the Driver-Dispatcher Relation=[{}], between [{} - {},] was received.",
            relationUuid,
            startDate,
            endDate
        );
        var responses = vehicleMaintenanceFacade.getVehicleMaintenanceResponseList(relationUuid, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.fromData(responses));
    }

    @DeleteMapping("/{vehicleMaintenanceRecordUuid}")
    public ResponseEntity<ApiResponse<Void, ErrorResponse>> deleteLoads(@PathVariable String vehicleMaintenanceRecordUuid) {
        log.info("A request to remove the Vehicle Maintenance record with the UUID=[{}} was received.", vehicleMaintenanceRecordUuid);
        vehicleMaintenanceService.deleteVehicleMaintenanceRecordByUuid(vehicleMaintenanceRecordUuid);
        return ResponseEntity.noContent().build();
    }
}
