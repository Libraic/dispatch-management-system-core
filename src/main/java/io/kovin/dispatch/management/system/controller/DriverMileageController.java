package io.kovin.dispatch.management.system.controller;

import java.util.List;
import io.kovin.dispatch.management.system.facade.DriverMileageFacade;
import io.kovin.dispatch.management.system.model.request.UpsertDriverMileageRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.DriverMileageData;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/drivers-mileage")
@Slf4j
public class DriverMileageController {

    private final DriverMileageFacade driverMileageFacade;

    @PutMapping
    public ResponseEntity<ApiResponse<List<DriverMileageData>, List<GroupsErrors>>> upsertMileage(
        @RequestBody UpsertDriverMileageRequest upsertDriverMileageRequest
    ) {
        log.info("A request to upsert mileage was received.");
        List<DriverMileageData> driverMileageDataList = driverMileageFacade.upsertMileage(upsertDriverMileageRequest);
        return ResponseEntity.ok(ApiResponse.fromData(driverMileageDataList));
    }
}
