package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.facade.DriverFacade;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.DriverData;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/drivers")
@Slf4j
public class DriverController {

    private final DriverFacade driverFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<DriverData, ErrorResponse>> createDriver(
        @RequestBody CreateDriverRequest createDriverRequest
    ) {
        log.info("A request to create a driver was received.");
        DriverData driverData = driverFacade.createDriver(createDriverRequest);
        ApiResponse<DriverData, ErrorResponse> apiResponse = ApiResponse.fromData(driverData);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
