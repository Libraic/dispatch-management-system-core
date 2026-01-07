package io.kovin.dispatch.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.facade.DriverMileageFacade;
import io.kovin.dispatch.management.system.model.request.UpsertDriverMileageRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.mileage.GetDriverMileageResponse;
import io.kovin.dispatch.management.system.model.response.mileage.UpsertDriverMileageResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.service.DriverMileageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/drivers-mileage")
@Slf4j
public class DriverMileageController {

    private final DriverMileageService driverMileageService;

    private final DriverMileageFacade driverMileageFacade;

    @PutMapping
    public ResponseEntity<ApiResponse<UpsertDriverMileageResponse, ErrorResponse>> upsertMileage(
        @RequestBody UpsertDriverMileageRequest upsertDriverMileageRequest
    ) {
        log.info("A request to upsert the drivers mileage was received.");
        UpsertDriverMileageResponse response = driverMileageFacade.upsertMileage(upsertDriverMileageRequest);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetDriverMileageResponse>, ErrorResponse>> getCompanyDriversMileage(
        @RequestParam(name = "companyId") String companyId,
        @RequestParam(name = "startDate") LocalDate startDate,
        @RequestParam(name = "endDate") LocalDate endDate
    ) {
        log.info(
            "A request to retrieve the Drivers Mileage for the Company=[{}], between [{} - {},] was received.",
            companyId,
            startDate,
            endDate
        );
        List<GetDriverMileageResponse> response = driverMileageFacade.getDriversMileageForTimeframe(companyId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void, ErrorResponse>> deleteDriversMileageByIds(
        @RequestBody List<String> ids
    ) {
        log.info("A request to delete the drivers mileage was received.");
        driverMileageService.deleteDriversMileageByUuids(ids);
        return ResponseEntity.noContent().build();
    }
}
