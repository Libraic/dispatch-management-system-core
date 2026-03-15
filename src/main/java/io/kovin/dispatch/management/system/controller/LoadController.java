package io.kovin.dispatch.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.facade.LoadFacade;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.load.GetDriverLoadsDataResponse;
import io.kovin.dispatch.management.system.model.response.load.GetLoadResponse;
import io.kovin.dispatch.management.system.model.response.load.UpsertDriverLoadsResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
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
@RequiredArgsConstructor
@RequestMapping("api/loads")
@Slf4j
public class LoadController {

    private final LoadFacade loadFacade;

    @PutMapping
    public ResponseEntity<ApiResponse<UpsertDriverLoadsResponse, ErrorResponse>> upsertLoad(
        @RequestBody UpsertLoadRequest upsertLoadRequest
    ) {
        log.info("A request to upsert the load was received.");
        UpsertDriverLoadsResponse response = loadFacade.upsertLoad(upsertLoadRequest);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @GetMapping("/companies/{companyId}")
    public ResponseEntity<ApiResponse<List<GetDriverLoadsDataResponse>, ErrorResponse>> getCompanyLoads(
        @PathVariable(name = "companyId") String companyId,
        @RequestParam(name = "startDate") LocalDate startDate,
        @RequestParam(name = "endDate") LocalDate endDate
    ) {
        log.info(
            "A request to retrieve the Loads for the Company=[{}], between [{} - {},] was received.",
            companyId,
            startDate,
            endDate
        );
        List<GetDriverLoadsDataResponse> response = loadFacade.getDriverLoadsForTimeframe(companyId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @GetMapping("/relations/{relationUuid}")
    public ResponseEntity<ApiResponse<List<GetLoadResponse>, ErrorResponse>> getRelationLoadsForTimeframe(
        @PathVariable(name = "relationUuid") String relationUuid,
        @RequestParam(name = "startDate") LocalDate startDate,
        @RequestParam(name = "endDate") LocalDate endDate
    ) {
        log.info(
            "A request to retrieve the Loads for the Driver-Dispatcher Relation=[{}], between [{} - {},] was received.",
            relationUuid,
            startDate,
            endDate
        );
        List<GetLoadResponse> response = loadFacade.getLoadResponses(relationUuid, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @DeleteMapping("/{loadUuid}")
    public ResponseEntity<ApiResponse<Void, ErrorResponse>> deleteLoads(@PathVariable String loadUuid) {
        log.info("A request to remove the Loads with the UUID=[{}} was received.", loadUuid);
        loadFacade.deleteLoad(loadUuid);
        return ResponseEntity.noContent().build();
    }
}
