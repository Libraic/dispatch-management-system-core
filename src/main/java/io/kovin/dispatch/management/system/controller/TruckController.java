package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.facade.TruckFacade;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.TruckData;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrorResponse;
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
@RequestMapping("api/trucks")
@Slf4j
public class TruckController {

    private final TruckFacade truckFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<TruckData, GroupsErrorResponse>> createTruck(
        @RequestBody CreateTruckRequest createTruckRequest
    ) {
        log.info("A request to register a Truck was received.");
        TruckData truckData = truckFacade.createTruck(createTruckRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.fromData(truckData));
    }
}
