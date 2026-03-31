package io.kovin.dispatch.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import io.kovin.dispatch.management.system.facade.LoadFacade;
import io.kovin.dispatch.management.system.model.request.UpsertLoadRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.GetLoadStartingPointResponse;
import io.kovin.dispatch.management.system.model.response.load.GenericLoadResponse;
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
    public ResponseEntity<ApiResponse<GenericLoadResponse, ErrorResponse>> upsertLoad(
        @RequestBody UpsertLoadRequest upsertLoadRequest
    ) {
        log.info("A request to upsert the load was received.");
        GenericLoadResponse response = loadFacade.upsertLoad(upsertLoadRequest);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @GetMapping("/relations/{relationUuid}")
    public ResponseEntity<ApiResponse<List<GenericLoadResponse>, ErrorResponse>> getRelationLoadsForTimeframe(
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
        List<GenericLoadResponse> response = loadFacade.getLoadResponses(relationUuid, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @GetMapping("/relations/{relationUuid}/starting-point")
    public ResponseEntity<ApiResponse<GetLoadStartingPointResponse, ErrorResponse>> getLoadStartingPoint(
        @PathVariable(name = "relationUuid") String relationUuid,
        @RequestParam(name = "date") LocalDate date
    ) {
        log.info("A request to retrieve the starting point for date=[{}] was received.", date);
        GetLoadStartingPointResponse response = loadFacade.getLoadStartingPoint(relationUuid, date);
        return ResponseEntity.ok(ApiResponse.fromData(response));
    }

    @DeleteMapping("/{loadUuid}")
    public ResponseEntity<ApiResponse<Void, ErrorResponse>> deleteLoads(@PathVariable String loadUuid) {
        log.info("A request to remove the Load with the UUID=[{}} was received.", loadUuid);
        loadFacade.deleteLoad(loadUuid);
        return ResponseEntity.noContent().build();
    }
}
