package io.kovin.dispatch.management.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.facade.DriverMileageFacade;
import io.kovin.dispatch.management.system.model.request.UpsertDriverMileageRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.DriverMileageData;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
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
    public ResponseEntity<ApiResponse<List<DriverMileageData>, List<GroupsErrors>>> upsertMileage(
        @RequestBody UpsertDriverMileageRequest upsertDriverMileageRequest
    ) {
        log.info("A request to upsert mileage was received.");
        List<DriverMileageData> driverMileageDataList = driverMileageFacade.upsertMileage(upsertDriverMileageRequest);
        return ResponseEntity.ok(ApiResponse.fromData(driverMileageDataList));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverMileageData>, ErrorResponse>> getDriversMileageByCriteria(
        @RequestParam(name = "companyId", required = false) String companyId
    ) {
        log.info("A request to retrieve the drivers mileage by criteria was received.");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("company", companyId);
        log.trace("The query parameters are the following: [{}].", queryParams);
        List<DriverMileageData> usersData = driverMileageFacade.getDriversMileageByCriteria(queryParams);
        return ResponseEntity.ok(ApiResponse.fromData(usersData));
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
