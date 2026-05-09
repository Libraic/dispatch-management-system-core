package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.model.response.GetCityAndStateResponse;
import io.kovin.dispatch.management.system.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<GetCityAndStateResponse>> getCities(
        @RequestParam(name = "prefix", required = false) String prefix
    ) {
        log.info("A request to fetch cities by zip code was received.");
        Page<GetCityAndStateResponse> getCityAndStateResponseList = cityService.searchByPrefix(prefix);
        return ResponseEntity.ok(getCityAndStateResponseList);
    }
}
