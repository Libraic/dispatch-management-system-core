package io.kovin.dispatch.management.system.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import io.kovin.dispatch.management.system.facade.DriverFacade;
import io.kovin.dispatch.management.system.model.request.CreateDriverRequest;
import io.kovin.dispatch.management.system.model.response.DriverData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.COMPANY_FIELD;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/drivers")
@Slf4j
public class DriverController {

    private final DriverFacade driverFacade;

    @PostMapping
    public ResponseEntity<DriverData> createDriver(
        @RequestBody CreateDriverRequest createDriverRequest
    ) {
        log.info("A request to create a driver was received.");
        DriverData driverData = driverFacade.createDriver(createDriverRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(driverData);
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID driverId) {
        log.info("A request to delete the driver was received.");
        driverFacade.deleteDriver(driverId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<DriverData>> getDriversByCriteria(
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size,
        @RequestParam(name = "firstName", required = false) String firstName,
        @RequestParam(name = "lastName", required = false) String lastName,
        @RequestParam(name = "name", required = false) String fullName,
        @RequestParam(name = "companyId", required = false) String companyId
    ) {
        log.info("A request to fetch Drivers by criteria was received.");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("firstName", firstName);
        queryParams.put("lastName", lastName);
        queryParams.put("name", fullName);
        queryParams.put(COMPANY_FIELD, companyId);
        log.trace("The query parameters are the following: [{}].", queryParams);

        int finalPage = page == null ? 0 : page;
        int finalSize = size == null ? 0 : size;
        Page<DriverData> driversData = driverFacade.getDriversByCriteria(queryParams, finalPage, finalSize);
        return ResponseEntity.ok(driversData);
    }
}
