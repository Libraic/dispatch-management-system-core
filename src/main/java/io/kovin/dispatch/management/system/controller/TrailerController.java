package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.facade.TrailerFacade;
import io.kovin.dispatch.management.system.model.request.CreateTrailerRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.TrailerData;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
