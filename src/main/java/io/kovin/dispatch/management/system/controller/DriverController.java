package io.kovin.dispatch.management.system.controller;

import static io.kovin.dispatch.management.system.utils.DispatchManagementSystemConstants.PAGE_BATCH_SIZE;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.facade.DriverFacade;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.DriverData;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
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
@RequestMapping("api/drivers")
@Slf4j
public class DriverController {

    private final DriverFacade driverFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<DriverData, List<GroupsErrors>>> createDriver(
        @RequestBody CreateDriverRequest createDriverRequest
    ) {
        log.info("A request to create a driver was received.");
        DriverData driverData = driverFacade.createDriver(createDriverRequest);
        ApiResponse<DriverData, List<GroupsErrors>> apiResponse = ApiResponse.fromData(driverData);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverData>, ErrorResponse>> getDriversByCriteria(
        @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
        @RequestParam(name = "size", required = false, defaultValue = PAGE_BATCH_SIZE) int size,
        @RequestParam(name = "firstName", required = false) String firstName,
        @RequestParam(name = "lastName", required = false) String lastName,
        @RequestParam(name = "fullName", required = false) String fullName,
        @RequestParam(name = "companyId", required = false) String companyId
    ) {
        log.info("A request to fetch Drivers by criteria was received.");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("firstName", firstName);
        queryParams.put("lastName", lastName);
        queryParams.put("fullName", fullName);
        queryParams.put("company", companyId);
        log.trace("The query parameters are the following: [{}].", queryParams);
        List<DriverData> driversData = driverFacade.getDriversByCriteria(queryParams, cursor, size);
        return ResponseEntity.ok(ApiResponse.fromData(driversData));
    }
}
