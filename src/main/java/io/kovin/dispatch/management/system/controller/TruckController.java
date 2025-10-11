package io.kovin.dispatch.management.system.controller;

import static io.kovin.dispatch.management.system.utils.DispatchManagementSystemConstants.PAGE_BATCH_SIZE;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.facade.TruckFacade;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.TruckData;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<TruckData>, ErrorResponse>> getTrucksByCriteria(
        @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
        @RequestParam(name = "size", required = false, defaultValue = PAGE_BATCH_SIZE) int size,
        @RequestParam(name = "truckNumber", required = false) String truckNumber,
        @RequestParam(name = "companyId", required = false) String companyId
    ) {
        log.info("A request to fetch Trucks by criteria was received.");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("truckNumber", truckNumber);
        queryParams.put("company", companyId);
        log.trace("The query parameters are the following: [{}].", queryParams);
        List<TruckData> trucksData = truckFacade.getTrucksByCriteria(queryParams, cursor, size);
        return ResponseEntity.ok(ApiResponse.fromData(trucksData));
    }
}
