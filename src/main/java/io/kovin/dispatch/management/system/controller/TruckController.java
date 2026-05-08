package io.kovin.dispatch.management.system.controller;

import java.util.HashMap;
import java.util.Map;
import io.kovin.dispatch.management.system.facade.TruckFacade;
import io.kovin.dispatch.management.system.model.request.CreateTruckRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.GetTruckResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<ApiResponse<GetTruckResponse, GroupsErrorResponse>> createTruck(
        @RequestBody CreateTruckRequest createTruckRequest
    ) {
        log.info("A request to register a Truck was received.");
        GetTruckResponse getTruckResponse = truckFacade.createTruck(createTruckRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.fromData(getTruckResponse));
    }

    @GetMapping
    public ResponseEntity<Page<GetTruckResponse>> getTrucksByCriteria(
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size,
        @RequestParam(name = "truckNumber", required = false) String truckNumber,
        @RequestParam(name = "companyId", required = false) String companyId
    ) {
        log.info("A request to fetch Trucks by criteria was received.");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("truckNumber", truckNumber);
        queryParams.put("company", companyId);
        log.trace("The query parameters are the following: [{}].", queryParams);
        int finalPage = page == null ? 0 : page;
        int finalSize = size == null ? 0 : size;
        Page<GetTruckResponse> trucksData = truckFacade.getTrucksByCriteria(queryParams, finalPage, finalSize);
        return ResponseEntity.ok(trucksData);
    }
}
