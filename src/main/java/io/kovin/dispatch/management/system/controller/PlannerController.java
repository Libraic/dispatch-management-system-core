package io.kovin.dispatch.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.kovin.dispatch.management.system.facade.PlannerFacade;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.load.GetDispatchingDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/planner")
@RequiredArgsConstructor
@Slf4j
public class PlannerController {

    private final PlannerFacade plannerFacade;

    @GetMapping
    public ResponseEntity<List<GetDispatchingDataResponse>> getWorkforcePlanningData(
        @RequestParam(name = "companyId") UUID companyId,
        @RequestParam(name = "startDate") LocalDate startDate,
        @RequestParam(name = "endDate") LocalDate endDate
    ) {
        log.info(
            "A request to retrieve the Loads for the Company=[{}], between [{} - {},] was received.",
            companyId,
            startDate,
            endDate
        );
        List<GetDispatchingDataResponse> response = plannerFacade.getDriverLoadsForTimeframe(companyId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
