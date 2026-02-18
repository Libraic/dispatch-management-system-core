package io.kovin.dispatch.management.system.controller;

import java.util.List;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.GetCityAndStateResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cities")
@Slf4j
public class CityController {

    private final CityService cityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetCityAndStateResponse>, ErrorResponse>> getCities(
        @RequestParam(name = "prefix", required = false) String prefix
    ) {
        log.info("A request to fetch cities by zip code was received.");
        List<GetCityAndStateResponse> getCityAndStateResponseList = cityService.searchByPrefix(prefix);
        return ResponseEntity.ok(ApiResponse.fromData(getCityAndStateResponseList));
    }
}
