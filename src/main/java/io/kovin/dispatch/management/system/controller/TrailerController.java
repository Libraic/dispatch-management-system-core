package io.kovin.dispatch.management.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size,
        @RequestParam(name = "trailerNumber", required = false) String trailerNumber,
        @RequestParam(name = "companyId", required = false) String companyId
    ) {
        log.info("A request to fetch Trailers by criteria was received.");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("trailerNumber", trailerNumber);
        queryParams.put("company", companyId);
        log.trace("The query parameters are the following: [{}].", queryParams);
        int finalPage = page == null ? 0 : page;
        int finalSize = size == null ? 0 : size;
        List<TrailerData> trailersData = trailerFacade.getTrailersByCriteria(queryParams, finalPage, finalSize);
        return ResponseEntity.ok(ApiResponse.fromData(trailersData));
    }
}
