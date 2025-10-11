package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.facade.TrailerFacade;
import io.kovin.dispatch.management.system.model.request.CreateTrailerRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.TrailerData;
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
import static io.kovin.dispatch.management.system.utils.DispatchManagementSystemConstants.PAGE_BATCH_SIZE;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/trailers")
@RequiredArgsConstructor
@Slf4j
public class TrailerController {

    private final TrailerFacade trailerFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<TrailerData, GroupsErrorResponse>> createTrailer(
        @RequestBody CreateTrailerRequest createTrailerRequest
    ) {
        log.info("A request to create a Trailer was received.");
        TrailerData trailerData = trailerFacade.createTrailer(createTrailerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.fromData(trailerData));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TrailerData>, ErrorResponse>> getTrailersByCriteria(
        @RequestParam(name = "cursor", required = false) LocalDateTime cursor,
        @RequestParam(name = "size", required = false, defaultValue = PAGE_BATCH_SIZE) int size,
        @RequestParam(name = "trailerNumber", required = false) String trailerNumber,
        @RequestParam(name = "companyId", required = false) String companyId
    ) {
        log.info("A request to fetch Trailers by criteria was received.");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("trailerNumber", trailerNumber);
        queryParams.put("company", companyId);
        log.trace("The query parameters are the following: [{}].", queryParams);
        List<TrailerData> trailersData = trailerFacade.getTrailersByCriteria(queryParams, cursor, size);
        return ResponseEntity.ok(ApiResponse.fromData(trailersData));
    }
}
